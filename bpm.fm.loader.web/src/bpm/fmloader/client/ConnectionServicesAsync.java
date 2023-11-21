package bpm.fmloader.client;

import bpm.fmloader.client.infos.InfosUser;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConnectionServicesAsync {

	public void initFmSession(InfosUser infos, AsyncCallback<InfosUser> asyncCallback);

	public void initSession(AsyncCallback<Void> callback);
}
