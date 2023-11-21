package bpm.fwr.oda.runtime;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ConnectionPool {
	
	private static HashMap<Properties, Collection<IBusinessModel>> connections = new HashMap<Properties, Collection<IBusinessModel>>();
	
	private static final String key_directoryItem = "connectionPool.directoryItem";
	private static final String key_loadingDate = "connectionPool.loadingDate";
	private static final String key_repositorySocket = "connectionPool.repositorySocket";
	
	public static synchronized Collection<IBusinessModel> getConnection(Properties p) throws OdaException{
		for(Properties _p: connections.keySet()){
			
			boolean equal = false;
			
			if (p.equals(_p)){
				
				
				equal = true;
			}
			else if (p.getProperty("URL").equals(_p.getProperty("URL")) &&
					p.getProperty("DIRECTORY_ITEM_ID").equals(_p.getProperty("DIRECTORY_ITEM_ID")) && 
					p.getProperty("USER").equals(_p.getProperty("USER")) &&
					p.getProperty("PASSWORD").equals(_p.getProperty("PASSWORD")) &&
					p.getProperty("GROUP_NAME").equals(_p.getProperty("GROUP_NAME"))){
				
				equal = true;
			}
			
			if (equal){
				
				/*
				 * check if the FMDT model has been updated since it has been loaded
				 */
				IRepositoryApi sock = (IRepositoryApi)_p.get(key_repositorySocket);
				RepositoryItem it = (RepositoryItem)_p.get(key_directoryItem);
				Date date = (Date)_p.get(key_loadingDate);
				
				try{
					if (sock.getRepositoryService().checkItemUpdate(it, date)){
						connections.remove(_p);
					}
					else{
						return connections.get(_p);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
			}
			
		}
		
		//the parameters are set, now we build the FMDTModel
		String username = p.getProperty("USER");
		String url = p.getProperty("URL");
		String password = p.getProperty("PASSWORD");
		String groupName =  p.getProperty("GROUP_NAME");
		String encryption = p.getProperty("IS_ENCRYPTED");
		int directoryItemId = Integer.parseInt(p.getProperty("DIRECTORY_ITEM_ID"));
		
		Integer groupId = null;
		IVanillaContext ctx = new BaseVanillaContext(url, username, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx);
		Repository repository = null;
		try {
			repository = vanillaApi.getVanillaRepositoryManager().getRepositoryFromUrl(url);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		Group group = null;
		
		try{
			group  = vanillaApi.getVanillaSecurityManager().getGroupByName(groupName);	
			groupId = group.getId();
		}catch(Exception ex){
			ex.printStackTrace();
			throw new OdaException("Unable to get GroupId for group " + groupName + " : " + ex.getMessage());
		}
		
		IRepositoryContext repctx = new BaseRepositoryContext(ctx, group, repository);
		IRepositoryApi sock = new RemoteRepositoryApi(repctx);
		
		IRepository rep = null;
		try {
			rep = new bpm.vanilla.platform.core.repository.Repository(sock,IRepositoryApi.FMDT_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException("Unable to connect to Bi Repository\n" + e.getMessage());
		}
		
		
		
		RepositoryItem item = null;
		try {
			item = rep.getItem(directoryItemId);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		String xmlModel = null;
		try {
			xmlModel = sock.getRepositoryService().loadModel(item);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException("Unable to load model from repository\n" + e.getMessage());
		}
		
	
		try {
			p.put(key_repositorySocket, sock);
			p.put(key_directoryItem, item);
			p.put(key_loadingDate, Calendar.getInstance().getTime());
			connections.put(p, MetaDataReader.read(groupName, IOUtils.toInputStream(xmlModel, "UTF-8"), null, true));
			return connections.get(p);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e);
		}
		
		
		
	}
	
}
