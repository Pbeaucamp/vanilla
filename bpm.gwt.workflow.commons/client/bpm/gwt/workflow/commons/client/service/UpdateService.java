package bpm.gwt.workflow.commons.client.service;

import bpm.gwt.commons.client.services.exception.ServiceException;
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
	
	public UpdateInformations checkUpdates(String updateManagerUrl) throws ServiceException;
}
