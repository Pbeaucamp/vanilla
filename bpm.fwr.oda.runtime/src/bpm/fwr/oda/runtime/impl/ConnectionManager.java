package bpm.fwr.oda.runtime.impl;

import java.util.HashMap;
import java.util.Properties;

import bpm.fwr.api.beans.FWRReport;
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
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.XStream;

public class ConnectionManager {
	private static HashMap<Properties, FWRReport> loadFWRReport = new HashMap<Properties, FWRReport>();
	
	public static FWRReport getFWRReport(Properties connProps) throws Exception{
		
		for(Properties key : loadFWRReport.keySet()){
			if (key.equals(connProps)){
				FWRReport r = loadFWRReport.get(key);
				if (r != null){
					
					/*
					 * we check if the model have been updated
					 */
					
					
					return r;
				}
			}
		}

		String repositoryUrl = connProps.getProperty(Connection.REPOSITORY_URL);
		String username = connProps.getProperty(Connection.USER);
		String password = connProps.getProperty(Connection.PASSWORD);
		int groupId = Integer.parseInt(connProps.getProperty(Connection.GROUP_ID));
		int directoryItemId = Integer.parseInt(connProps.getProperty(Connection.FWREPORT_ID));
		
		IRepositoryApi connection = getConnection(connProps);
		try {
			RepositoryItem dirItem = connection.getRepositoryService().getDirectoryItem(directoryItemId);
			String xmlModel = connection.getRepositoryService().loadModel(dirItem);

			XStream xstream = new XStream();
			FWRReport model = (FWRReport)xstream.fromXML(xmlModel);

			loadFWRReport.put(connProps, model);
         
			return model;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public static IRepositoryApi getConnection(Properties connProps) throws Exception {
		String repositoryUrl = connProps.getProperty(Connection.REPOSITORY_URL);
		int groupId = Integer.parseInt(connProps.getProperty(Connection.GROUP_ID));
		
		IVanillaContext vanillaContext = new BaseVanillaContext(connProps.getProperty(Connection.URL), connProps.getProperty(Connection.USER), connProps.getProperty(Connection.PASSWORD));
		
		IVanillaAPI api = new RemoteVanillaPlatform(vanillaContext);
		
		Group group = api.getVanillaSecurityManager().getGroupById(groupId);
		Repository repository = api.getVanillaRepositoryManager().getRepositoryFromUrl(repositoryUrl);
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, repository);
		
		return new RemoteRepositoryApi(ctx);
	}
	
	
}
