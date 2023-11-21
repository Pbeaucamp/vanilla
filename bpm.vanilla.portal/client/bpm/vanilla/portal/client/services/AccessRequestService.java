package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.AccessRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("AccessRequestService")
public interface AccessRequestService extends RemoteService {
	
	public static class Connect {
		private static AccessRequestServiceAsync instance;
		public static AccessRequestServiceAsync getInstance(){
			if(instance == null){
				instance = (AccessRequestServiceAsync) GWT.create(AccessRequestService.class);
			}
			return instance;
		}
	}
	
	public void addAccessRequestOnRepositoryItem(PortailRepositoryItem dto, String message) throws ServiceException;
	
	public void addAccessRequestGedDocument(DocumentVersionDTO dto, String message) throws ServiceException;

	/**
	 * the requests i need to answer
	 * @param showAllDemands
	 * @param callback
	 */
	public List<AccessRequest> getMyAccessRequests(boolean showAllDemands) throws ServiceException;
	
	/**
	 * the requests i have made
	 * @param showAllDemands
	 * @param callback
	 */
	public List<AccessRequest> getMyAccessDemands(boolean showAllDemands) throws ServiceException;
	
	public void approveRequest(AccessRequest request) throws ServiceException;
	
	public void refuseRequest(AccessRequest request) throws ServiceException;
}
