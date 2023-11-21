package bpm.gwt.workflow.commons.client.service;

import bpm.update.manager.api.beans.UpdateInformations;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface UpdateServiceAsync {
	
	public void checkUpdates(String updateManagerUrl, AsyncCallback<UpdateInformations> callback);
}
