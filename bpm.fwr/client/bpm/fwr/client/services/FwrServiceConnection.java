package bpm.fwr.client.services;

import java.util.List;

import bpm.fwr.shared.models.TreeParentDTO;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("fwrConnection")
public interface FwrServiceConnection extends RemoteService {
	
	public static class Connect{
		private static FwrServiceConnectionAsync instance;
		public static FwrServiceConnectionAsync getInstance(){
			if(instance == null){
				instance = (FwrServiceConnectionAsync) GWT.create(FwrServiceConnection.class);
			}
			return instance;
		}
	}
	
	public void initSession() throws ServiceException;
	
	public TreeParentDTO browseRepositoryService() throws ServiceException;
	
	public List<Group> getGroupsService() throws ServiceException;
	
	public List<FwrMetadata> getMetadatas(String group) throws ServiceException;
}


