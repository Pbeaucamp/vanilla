package bpm.es.web.client.services;

import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("adminServlet")
public interface AdminService extends RemoteService {
	
	public static class Connect{
		private static AdminServiceAsync instance;
		
		public static AdminServiceAsync getInstance(){
			if(instance == null){
				instance = (AdminServiceAsync) GWT.create(AdminService.class);
			}
			return instance;
		}
	}
	
	public void initSession() throws ServiceException;
	
	public void manageUser(User user, boolean edit) throws ServiceException;
	
	public void deleteUser(User user) throws ServiceException;
	
	public Settings getSettings() throws ServiceException;
	
	public void updateSettings(Settings settings) throws ServiceException;
	
	public boolean canAccessAdministration() throws ServiceException;

	public List<SecurityLog> getSecurityLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate) throws ServiceException;
}
