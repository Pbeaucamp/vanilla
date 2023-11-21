package bpm.vanillahub.web.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoUser;
import bpm.vanillahub.web.shared.Dummy;

@RemoteServiceRelativePath("adminService")
public interface AdminService extends RemoteService {

	public static class Connect {
		private static AdminServiceAsync instance;

		public static AdminServiceAsync getInstance() {
			if (instance == null) {
				instance = (AdminServiceAsync) GWT.create(AdminService.class);
			}
			return instance;
		}
	}
	
	/**
	 * For serialization purpose. Needed by GWT. Don't touch this.
	 */
	Dummy dummy(Dummy d);
	
	public void initSession() throws ServiceException;
	
	public void initVanillaHubSession(InfoUser infoUser) throws ServiceException;
}
