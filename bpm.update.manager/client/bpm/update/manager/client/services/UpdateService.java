package bpm.update.manager.client.services;

import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.update.manager.api.beans.GlobalProgress;
import bpm.update.manager.api.beans.UpdateInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("updateService")
public interface UpdateService extends RemoteService {

	public static class Connect {
		private static UpdateServiceAsync instance;

		public static UpdateServiceAsync getInstance() {
			if (instance == null) {
				instance = (UpdateServiceAsync) GWT.create(UpdateService.class);
			}
			return instance;
		}
	}
	
	public void initSession() throws ServiceException;

	public UpdateInformations checkUpdates() throws ServiceException;
	
	public void updateApplication() throws ServiceException;
	
	public GlobalProgress getGlobalProgress() throws ServiceException;
	
	public boolean cancelUpdate(String appName) throws ServiceException;
	
	public List<String> getApplications() throws ServiceException;
	
	public Boolean restartServer() throws ServiceException;
}
