package bpm.smart.web.client.services;



import bpm.gwt.commons.shared.InfoUser;
import bpm.smart.web.client.UserSession;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConnectionServicesAsync {

	public void initSmartWebSession(InfoUser infoUser, AsyncCallback<UserSession> callback);

	public void initSession(AsyncCallback<Void> asyncCallback);

}
