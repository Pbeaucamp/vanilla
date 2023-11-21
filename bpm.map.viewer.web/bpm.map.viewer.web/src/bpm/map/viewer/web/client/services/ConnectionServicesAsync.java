package bpm.map.viewer.web.client.services;



import bpm.map.viewer.web.client.UserSession;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConnectionServicesAsync {

	public void initMapViewerSession(AsyncCallback<UserSession> callback);

	public void initSession(AsyncCallback<Void> asyncCallback);

}
