package bpm.vanilla.portal.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("AdminService")
public interface AdminService extends RemoteService {
	public static class Connect{
		private static AdminServiceAsync instance;
		public static AdminServiceAsync getInstance(){
			if(instance == null){
				instance = (AdminServiceAsync) GWT.create(AdminService.class);
			}
			return instance;
		}
	}
	
	public void close();
	
	public PortailRepositoryItem getDirectoryItemById(PortailRepositoryItem item) throws ServiceException;
	
	public void updateItemProperties(PortailRepositoryItem item) throws ServiceException;
	
	public void changeUserMail(String newMail, boolean privateMail) throws ServiceException;
	
	public HashMap<String, HashMap<String, HashMap<String, String>>> getVanillaDocumentationPaths() throws ServiceException;

	public List<Alert> getAllAlerts() throws ServiceException;
	
	public void updateUser(User user, boolean changePassword, String oldPassword, String newPassword) throws ServiceException;
	
	public ArchiveType saveArchiveType(ArchiveType archiveType) throws ServiceException;
	
	public List<ArchiveType> getArchiveTypes() throws ServiceException;
	
	public ArchiveTypeItem getArchiveTypeForItem(int itemId, boolean isDirectory) throws ServiceException;

	ArchiveTypeItem addArchiveTypeToItem(int itemId, int archiveTypeId, boolean isDirectory, boolean applyToChildren) throws ServiceException;

	public List<Alert> getAlertsByTypeAndDirectoryItemId(TypeEvent typeEvent, int directoryItemId, int repositoryId) throws ServiceException;

	public void addSubscriber(Alert alert) throws ServiceException;

	public void deleteArchiveType(ArchiveType selectedObject) throws ServiceException;
}
