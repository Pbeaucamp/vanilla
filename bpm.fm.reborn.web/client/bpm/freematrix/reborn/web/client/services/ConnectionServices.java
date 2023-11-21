package bpm.freematrix.reborn.web.client.services;


import bpm.freematrix.reborn.web.client.ClientSession;
import bpm.gwt.commons.client.services.exception.ServiceException;

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
	
	public ClientSession initFmSession() throws Exception;

	public void initSession() throws ServiceException;
	
}
