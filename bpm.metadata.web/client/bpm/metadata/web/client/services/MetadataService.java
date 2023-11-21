package bpm.metadata.web.client.services;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("metadataService")
public interface MetadataService extends RemoteService {
	
	public static class Connect{
		private static MetadataServiceAsync instance;
		
		public static MetadataServiceAsync getInstance(){
			if(instance == null){
				instance = (MetadataServiceAsync) GWT.create(MetadataService.class);
			}
			return instance;
		}
	}
	
	public void initSession() throws ServiceException;
	
	public Integer save(RepositoryDirectory target, Metadata metadata, boolean update) throws ServiceException;
	
	
}
