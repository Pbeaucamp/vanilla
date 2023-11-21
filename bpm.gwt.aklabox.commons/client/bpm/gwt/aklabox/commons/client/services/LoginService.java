package bpm.gwt.aklabox.commons.client.services;

import bpm.gwt.aklabox.commons.shared.InfoConnection;
import bpm.gwt.aklabox.commons.shared.InfoUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("loginService")
public interface LoginService extends RemoteService {

	public static class Connect {
		private static LoginServiceAsync instance;

		public static LoginServiceAsync getService() {
			if (instance == null) {
				instance = (LoginServiceAsync) GWT.create(LoginService.class);
			}
			return instance;
		}
	}

	public InfoConnection getConnectionInformations() throws Exception;

	public InfoUser login(String username, String password) throws Exception;

	public InfoUser checkCookieSession(String sessionId) throws Exception;
	
	public InfoUser loginWithSessionId(String sessionId) throws Exception;
}