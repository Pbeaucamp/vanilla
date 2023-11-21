package bpm.fm.designer.web.client.services;

import java.util.List;

import bpm.fm.designer.web.client.ClientSession;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ConnectionServices")
public interface ConnectionServices extends RemoteService {

	public static class Connection{
		private static ConnectionServicesAsync instance;
		public static ConnectionServicesAsync getInstance(){
			if(instance == null){
				instance = (ConnectionServicesAsync) GWT.create(ConnectionServices.class);
			}
			return instance;
		}
	}
	
	public ClientSession initFmSession() throws Throwable;
	
	public List<Group> getGroupForUser() throws Throwable;
	
	public List<Group> getAllGroups() throws Throwable;
	
	public List<User> getUsers() throws Throwable;

	public void initSession() throws ServiceException;
}
