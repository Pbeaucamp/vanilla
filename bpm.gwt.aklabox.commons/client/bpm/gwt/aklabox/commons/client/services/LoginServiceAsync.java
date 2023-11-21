package bpm.gwt.aklabox.commons.client.services;

import bpm.gwt.aklabox.commons.shared.InfoConnection;
import bpm.gwt.aklabox.commons.shared.InfoUser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	public void getConnectionInformations(AsyncCallback<InfoConnection> callback);
	
	public void login(String username, String password, AsyncCallback<InfoUser> callback);

	public void checkCookieSession(String sessionId, AsyncCallback<InfoUser> asyncCallback);

	public void loginWithSessionId(String sessionId, AsyncCallback<InfoUser> asyncCallback);
}
