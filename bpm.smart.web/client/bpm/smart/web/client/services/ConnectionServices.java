package bpm.smart.web.client.services;


import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoUser;
import bpm.smart.web.client.UserSession;

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
	
	public UserSession initSmartWebSession(InfoUser infoUser) throws Exception;

	public void initSession() throws ServiceException;
	
}
