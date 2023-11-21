package bpm.freematrix.reborn.web.client.services;


import bpm.freematrix.reborn.web.client.ClientSession;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConnectionServicesAsync {

	public void initFmSession(AsyncCallback<ClientSession> callback);

	public void initSession(AsyncCallback<Void> asyncCallback);

}
