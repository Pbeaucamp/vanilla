package bpm.update.manager.client.services;

import java.util.List;

import bpm.update.manager.api.beans.GlobalProgress;
import bpm.update.manager.api.beans.UpdateInformations;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UpdateServiceAsync {
	
	public void initSession(AsyncCallback<Void> callback);
	
	public void checkUpdates(AsyncCallback<UpdateInformations> callback);
	
	public void updateApplication(AsyncCallback<Void> callback);
	
	public void getGlobalProgress(AsyncCallback<GlobalProgress> callback);

	public void cancelUpdate(String appName, AsyncCallback<Boolean> callback);
	
	public void getApplications(AsyncCallback<List<String>> callback);
	
	public void restartServer(AsyncCallback<Boolean> callback);
}
