package bpm.map.viewer.web.client.services;


import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.map.viewer.web.client.UserSession;

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
	
	public UserSession initMapViewerSession() throws Exception;

	public void initSession() throws ServiceException;
	
}
