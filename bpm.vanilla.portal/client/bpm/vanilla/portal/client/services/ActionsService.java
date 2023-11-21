package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.vanilla.portal.shared.repository.LinkedDocumentDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ActionsService")
public interface ActionsService extends RemoteService {
	public static class Connect{
		private static ActionsServiceAsync instance;
		public static ActionsServiceAsync getInstance(){
			if(instance == null){
				instance = (ActionsServiceAsync) GWT.create(ActionsService.class);
			}
			return instance;
		}
	}
	
	public String checkRunnable(int itemId) throws ServiceException;
	
	public List<LinkedDocumentDTO> getLinkedDocuments(int itemId) throws ServiceException;
	
	public DisplayItem getLinkedDocumentUrl(int itemId, int linkDocId) throws ServiceException;

}
