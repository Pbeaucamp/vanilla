package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Added to centralize all of the portal security needs
 * 
 * @author manu
 *
 */
@RemoteServiceRelativePath("SecurityService")
public interface SecurityService extends RemoteService {
	
	public static class Connect{
		
		private static SecurityServiceAsync instance;
		
		public static SecurityServiceAsync getInstance(){
			if (instance == null) {
				instance = (SecurityServiceAsync) GWT.create(SecurityService.class);
			}
			
			return instance;
		}
	}
	
	public enum TypeGroupRights {
		DISPLAY,
		EXECUTE,
		COMMENT,
		PROJECTION,
		HISTORIC
	}
	
	public enum TypeItemGed {
		GED_AVAILABLE,
		REALTIME,
		CREATE_ENTRY
	}
	
	public void initSession() throws ServiceException;
	
	public void initHubSession(InfoUser infoUser) throws ServiceException;
	
	public InfoUser changeCurrentGroup(InfoUser infoUser, Group group) throws ServiceException;
	
	public List<Group> getAvailableGroupsForDisplay(int itemId, boolean isDirectory) throws ServiceException;
	
	public void udpateTextGroup(List<Integer> grIds, String groupText) throws ServiceException;

	public List<Group> getAllGroups() throws ServiceException;

	public List<Group> getCommentableGroups(int itemId, boolean isDirectory) throws ServiceException;

	public List<Group> getGroupProjections(int itemId) throws ServiceException;

	public void updateGroupRights(int id, List<Group> toAdd, List<Group> toRemove, boolean isDirectory, TypeGroupRights type) throws ServiceException;

	public void updateItemGed(int itemId, boolean value, TypeItemGed typeItemGed) throws ServiceException;
}
