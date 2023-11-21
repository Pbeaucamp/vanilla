package bpm.vanillahub.web.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import bpm.gwt.commons.shared.InfoUser;
import bpm.vanillahub.web.shared.Dummy;


public interface AdminServiceAsync {
	
	/**
	 * For serialization purpose. Needed by GWT. Don't touch this.
	 */
	void dummy(Dummy d, AsyncCallback<Dummy> callback);
	
	public void initSession(AsyncCallback<Void> callback);
	
	public void initVanillaHubSession(InfoUser infoUser, AsyncCallback<Void> callback);
}
