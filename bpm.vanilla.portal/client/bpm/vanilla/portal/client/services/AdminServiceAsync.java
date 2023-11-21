package bpm.vanilla.portal.client.services;

import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AdminServiceAsync {
	
	public void close(AsyncCallback<Void> callback);
	
	public void getDirectoryItemById(PortailRepositoryItem item, AsyncCallback<PortailRepositoryItem> callback);
	
	public void updateItemProperties(PortailRepositoryItem item, AsyncCallback<Void> callback);
	
	public void changeUserMail(String newMail, boolean privateMail, AsyncCallback<Void> callback);
	
	public void getVanillaDocumentationPaths(AsyncCallback<HashMap<String, HashMap<String, HashMap<String, String>>>> callback);

	public void getAllAlerts(AsyncCallback<List<Alert>> asyncCallback);
	
	public void updateUser(User user, boolean changePassword, String oldPassword, String newPassword, AsyncCallback<Void> callback);

	public void saveArchiveType(ArchiveType archiveType, AsyncCallback<ArchiveType> callback);

	public void getArchiveTypes(AsyncCallback<List<ArchiveType>> callback);

	public void getArchiveTypeForItem(int itemId, boolean isDirectory, AsyncCallback<ArchiveTypeItem> callback);

	public void addArchiveTypeToItem(int itemId, int archiveTypeId, boolean isDirectory, boolean applyToChildren, AsyncCallback<ArchiveTypeItem> asyncCallback);

	public void getAlertsByTypeAndDirectoryItemId(TypeEvent typeEvent, int directoryItemId, int repositoryId, AsyncCallback<List<Alert>> callback);

	public void addSubscriber(Alert alert, AsyncCallback<Void> asyncCallback);

	public void deleteArchiveType(ArchiveType selectedObject, AsyncCallback<Void> gwtCallbackWrapper);
}
