package bpm.fm.designer.web.client.services;

import java.util.List;

import bpm.fm.designer.web.client.ClientSession;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConnectionServicesAsync {

	public void initFmSession(AsyncCallback<ClientSession> callback);

	public void getGroupForUser(AsyncCallback<List<Group>> callback);

	public void getAllGroups(AsyncCallback<List<Group>> callback);

	public void getUsers(AsyncCallback<List<User>> callback);

	public void initSession(AsyncCallback<Void> callback);

}
