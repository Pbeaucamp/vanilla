package bpm.vanilla.portal.client.services;

import java.util.List;

import bpm.gwt.commons.shared.InfoUser;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.portal.client.services.SecurityService.TypeGroupRights;
import bpm.vanilla.portal.client.services.SecurityService.TypeItemGed;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SecurityServiceAsync {
	
	public void initSession(AsyncCallback<Void> callback);
	
	public void initHubSession(InfoUser infoUser, AsyncCallback<Void> callback);
	
	public void changeCurrentGroup(InfoUser infoUser, Group group, AsyncCallback<InfoUser> callback);
	
	public void getAvailableGroupsForDisplay(int itemId, boolean isDirectory, AsyncCallback<List<Group>> callback);
	
	public void udpateTextGroup(List<Integer> grIds, String groupText, AsyncCallback<Void> callback);

	public void getAllGroups(AsyncCallback<List<Group>> asyncCallback);

	public void getCommentableGroups(int itemId, boolean isDirectory, AsyncCallback<List<Group>> callback);

	public void getGroupProjections(int itemId, AsyncCallback<List<Group>> callback);

	public void updateGroupRights(int id, List<Group> toAdd, List<Group> toRemove, boolean isDirectory, TypeGroupRights type, AsyncCallback<Void> callback);

	public void updateItemGed(int itemId, boolean value, TypeItemGed typeItemGed, AsyncCallback<Void> asyncCallback);
}

