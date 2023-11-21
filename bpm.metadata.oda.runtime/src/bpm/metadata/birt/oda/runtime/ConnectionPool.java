package bpm.metadata.birt.oda.runtime;

import java.net.ConnectException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.metadata.MetaDataReader;
import bpm.metadata.birt.oda.runtime.impl.Connection;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ConnectionPool {
	
	private static HashMap<Properties, Collection<IBusinessModel>> connections = new HashMap<Properties, Collection<IBusinessModel>>();
	
	private static final String key_directoryItem = "connectionPool.directoryItem";
	private static final String key_loadingDate = "connectionPool.loadingDate";
	private static final String key_repositorySocket = "connectionPool.repositorySocket";
	
	public static synchronized Collection<IBusinessModel> getConnection(Properties p) throws OdaException{
		
		//XXX check if it's already loaded, if it is, look for updates
		Logger.getLogger(ConnectionPool.class).debug("Check the connection pool");
		
		
		for(Properties _p: connections.keySet()){
			
			boolean equal = false;
			
			if (p.equals(_p)){
				equal = true;
			}
			else if (
					(p.getProperty("REPOSITORY_ID") != null && p.getProperty("REPOSITORY_ID").equals(_p.getProperty("REPOSITORY_ID")) ||
					(p.getProperty("URL") != null && p.getProperty("URL").equals(_p.getProperty("URL")))) &&
					p.getProperty("DIRECTORY_ITEM_ID").equals(_p.getProperty("DIRECTORY_ITEM_ID")) && 
//					p.getProperty("USER").equals(_p.getProperty("USER")) &&
//					p.getProperty("PASSWORD").equals(_p.getProperty("PASSWORD")) &&
					p.getProperty("GROUP_NAME").equals(_p.getProperty("GROUP_NAME"))) {
				equal = true;
			}
			
			if (equal){
				Logger.getLogger(ConnectionPool.class).debug("Metadata already loaded, check for updates");
				
				IRepositoryApi sock = (IRepositoryApi)_p.get(key_repositorySocket);
				RepositoryItem it = (RepositoryItem)_p.get(key_directoryItem);
				Date date = (Date)_p.get(key_loadingDate);
				
				try{
					if (sock.getRepositoryService().checkItemUpdate(it, date)){
						connections.remove(_p);
						Logger.getLogger(ConnectionPool.class).debug("Fmdt model " + it.getId() +  " has been updated, it is removed from the ConnectionPool");
					}
					else{
						Logger.getLogger(ConnectionPool.class).debug("Fmdt model " + it.getId() +  " have no updates");
						return connections.get(_p);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		}
//				
//				/*
//				 * check if the FMDT model has been updated since it has been loaded
//				 */
//				IRepositoryApi sock = (IRepositoryApi)_p.get(key_repositorySocket);
//				RepositoryItem it = (RepositoryItem)_p.get(key_directoryItem);
//				Date date = (Date)_p.get(key_loadingDate);
//				//check if item is still On
//				
//				try{
//					if (!sock.getRepositoryService().getDirectoryItem(it.getId()).isOn()){
//						throw new OdaException("The Fmdt Model (" + sock.getContext().getRepository().getUrl() + "," + it.getId() + ") has been shutdown from EntrepriseServices. Contact a Vanilla SuperUser");
//					}
//				}catch(OdaException ex){
//					throw ex;
//				}catch(Exception ex){
//					Logger.getLogger(ConnectionPool.class).warn("Failed to check if Fmdt model is still on - " + ex.getMessage(), ex);
//				}
//				
//				try{
//					if (sock.getRepositoryService().checkItemUpdate(it, date)){
//						connections.remove(_p);
//						Logger.getLogger(ConnectionPool.class).info("Fmdt model " + it.getId() +  " has been updated, it is removed from the ConnectionPool");
//					}
//					else{
//						return connections.get(_p);
//					}
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//				
//				
//			}
//			
//		}
		
		Logger.getLogger(ConnectionPool.class).debug("Metadata not in pool, loading it");
		
		//the parameters are set, now we build the FMDTModel
		String username = p.getProperty("USER");
		String url = p.getProperty("URL");
		String password = p.getProperty("PASSWORD");
		String groupName =  p.getProperty("GROUP_NAME");
		String encryption = p.getProperty("IS_ENCRYPTED");
		
		String vanillaUrl = p.getProperty("VANILLA_URL");
		
		if (vanillaUrl == null){
			vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
		}
		
		Integer repositoryId = null;
		
		try{
			repositoryId = Integer.parseInt(p.getProperty("REPOSITORY_ID"));
			if(repositoryId == 0) {
				repositoryId = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanilla.repository.repositoryId"));
			}
		}catch(Exception ex){
			Logger.getLogger(ConnectionPool.class).warn("No RepositoryId found in oda connection Properties, we will use the repository adress from properties" );
			repositoryId = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanilla.repository.repositoryId"));
		}
		
		int directoryItemId = Integer.parseInt(p.getProperty("DIRECTORY_ITEM_ID"));
		
		Integer groupId = null;
		IVanillaAPI rm = null;
		String xmlModel = null;
		RepositoryItem item = null;
		IRepositoryApi sock = null;
		try{
			//TODO : separate the case vanillaUrl cannot be null at this stage
			//in the else clause do not ask vanillaUrl from
			//birepostory socket 
			if (vanillaUrl != null && repositoryId != null){
				/*
				 * get repository connection from propertie's repId
				 */
				Group grp = null;
				IVanillaContext vanillaCtx = new BaseVanillaContext(vanillaUrl, username, password);
				rm = new RemoteVanillaPlatform(vanillaCtx);
				Logger.getLogger(ConnectionPool.class).info("Looking for Repository " + repositoryId + " from " + vanillaUrl);
				Repository repdef = null;
				try{
					repdef = rm.getVanillaRepositoryManager().getRepositoryById(repositoryId);
				}catch(ConnectException ex){
					Logger.getLogger(ConnectionPool.class).warn("Failed to communicate with Vanilla Server at " + vanillaUrl);
					Logger.getLogger(ConnectionPool.class).info("Use VanillaUrl from configurationManager file");
					vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
					rm = new RemoteVanillaPlatform(vanillaUrl, username, password);
					repdef = rm.getVanillaRepositoryManager().getRepositoryById(repositoryId);
				}
				if (repdef == null){
					throw new Exception("The repository with id=" + repositoryId + " is not present within the vanilla plateform");
				}
				
				//check grants
				User user = rm.getVanillaSecurityManager().authentify("", username, password, false);
				boolean repGrantedForUser = false;
				for(UserRep ur : rm.getVanillaRepositoryManager().getUserRepByRepositoryId(repositoryId)){
					if (ur.getRepositoryId() == repositoryId.intValue()){
						repGrantedForUser = true;
						break;
					}
				}
				
				if (!repGrantedForUser){
					if (user.isSuperUser()){
						Logger.getLogger(ConnectionPool.class).warn("User " + username + " has no grant on repository " + repdef.getName() + " but is superUser");
					}
					else{
						throw new Exception("The user " + username + " has no grant access on repository " + repdef.getName());
					}
				}

				
				for(Group g : rm.getVanillaSecurityManager().getGroups(user)){
					if (g.getName().equals(groupName)){
						grp = g;
						break;
					}
				}
				if (grp == null){
					try{
						grp = rm.getVanillaSecurityManager().getGroupByName(groupName);
						if (grp == null){
							Logger.getLogger(ConnectionPool.class).error("It seems that the group " + groupName + " does not exists");
						}

					}catch(Exception ex){
						
					}
					
					if (grp == null){
						throw new OdaException("Unable to get GroupId for group " + groupName);
					}
					
				}
				//create connection
				vanillaCtx = new BaseVanillaContext(vanillaUrl, vanillaCtx.getLogin(), vanillaCtx.getPassword());
				sock = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, grp, repdef)); 


				//loading modelXml
				Logger.getLogger(ConnectionPool.class).debug("Loading FMDT xml from repository...");
				item = sock.getRepositoryService().getDirectoryItem(directoryItemId);
				if (item == null){
					throw new Exception("Could not find Fmdt Item id=" + directoryItemId);
				}
				if (!item.isOn()){
					throw new OdaException("The Fmdt Model (" + sock.getContext().getRepository().getUrl() + "," + item.getId() + ") has been shutdown from EntrepriseServices. Contact a Vanilla SuperUser");
				}
				try {
					xmlModel = sock.getRepositoryService().loadModel(item);
					
				} catch (Exception e) {
					e.printStackTrace();
					throw new OdaException("Unable to load model from repository\n" + e.getMessage());
				}
	
			}
			else{
				BaseVanillaContext vanillaCtx = new BaseVanillaContext(vanillaUrl, username, password);
				IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
				Group group  = vanillaApi.getVanillaSecurityManager().getGroupByName(groupName);
				Repository repDef = vanillaApi.getVanillaRepositoryManager().getRepositoryFromUrl(url);
				
				sock = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, group, repDef)); 
						try{
					item = sock.getRepositoryService().getDirectoryItem(directoryItemId);
				}catch(Exception ex){
					ex.printStackTrace();
					throw new OdaException("Unable to find FMDT item with id=" + directoryItemId + " : " + ex.getMessage());
				}
				try {
					xmlModel = sock.getRepositoryService().loadModel(item);
					
				} catch (Exception e) {
					e.printStackTrace();
					throw new OdaException("Unable to load model from repository\n" + e.getMessage());
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw new OdaException("Unable to get GroupId for group " + groupName + " : " + ex.getMessage());
		}
		
		
		
		
	
		try {
			
			Collection<IBusinessModel> col = MetaDataReader.read(groupName, IOUtils.toInputStream(xmlModel, "UTF-8"), null, false);
			
			p.put(key_repositorySocket, sock);
			p.put(key_directoryItem, item);
			p.put(key_loadingDate, Calendar.getInstance().getTime());
			
			connections.put(p, col);
			
			return col;
//			Logger.getLogger(ConnectionPool.class).info("Fmdt model parsed");

//			return connections.get(p);
		} catch (Exception e) {
			String s = "Fail to parse FMDT model - " + e.getMessage();
			Logger.getLogger(ConnectionPool.class).error(e.getMessage(), e);
			throw new OdaException(s);
		}
		
		
		
	}
	
	
	public static IRepositoryApi getRepositoryConnection(Properties p) throws Exception{
		for(Properties _p: connections.keySet()){
					
			boolean equal = false;
			
			if (p.equals(_p)){
				
				
				equal = true;
			}
			else if (	(	p.getProperty("REPOSITORY_ID") == null && 
							p.getProperty("URL") != null && 
							p.getProperty("URL").equals(_p.getProperty("URL")) 
					|| 	(p.getProperty("REPOSITORY_ID") != null && 
							p.getProperty("REPOSITORY_ID").equals(_p.getProperty("REPOSITORY_ID")))) 
					&&	p.getProperty("USER").equals(_p.getProperty("USER")) &&
					p.getProperty("PASSWORD").equals(_p.getProperty("PASSWORD"))){
				
				equal = true;
			}
			if (equal){
				return (IRepositoryApi)_p.get(key_repositorySocket);
			}
		}
		String username = p.getProperty("USER");
		String url = p.getProperty("VANILLA_URL");
		if (url == null){
			url = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
			p.setProperty("VANILLA_URL", url);
		} 
		String password = p.getProperty("PASSWORD");
		String repId = p.getProperty("REPOSITORY_ID");
		IRepositoryApi sock = null;
		Repository repDef = null;
		IVanillaContext vanillCtx = new BaseVanillaContext(url, username, password);
		if (repId == null){
			
			Group group = null;
			url =  p.getProperty("URL");
			
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillCtx);
			try{
				
				group = vanillaApi.getVanillaSecurityManager().getGroupByName(p.getProperty("GROUP_NAME"));
			}catch(Exception ex){
				ex.printStackTrace();
				group = null;
			}
			if (group == null){
				group = new Group(); 
				group.setId(-1);
			}
			repDef = vanillaApi.getVanillaRepositoryManager().getRepositoryFromUrl(url);
			
			sock = new RemoteRepositoryApi(new BaseRepositoryContext(vanillCtx, group, repDef));
		}
		else{
			try{
			
				IVanillaAPI api = new RemoteVanillaPlatform(url, username, password);
				Repository repdef = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(p.getProperty("REPOSITORY_ID")));
				Group group = api.getVanillaSecurityManager().getGroupByName(p.getProperty(Connection.GROUP_NAME));
				
				sock = new RemoteRepositoryApi(new BaseRepositoryContext(vanillCtx, group, repDef));

			}catch(Exception ex){
				ex.printStackTrace();
			}
						
		}
		
		
		
		
		
		return sock;
	}
	
	public static void clear(){
		connections.clear();
	}


	
}
