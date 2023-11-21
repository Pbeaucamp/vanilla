package bpm.es.web.client.services;

import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {

	public void initSession(AsyncCallback<Void> callback);
	
	public void manageUser(User user, boolean edit, AsyncCallback<Void> callback);
	
	public void deleteUser(User user, AsyncCallback<Void> callback);
	
	public void getSettings(AsyncCallback<Settings> callback);
	
	public void updateSettings(Settings settings, AsyncCallback<Void> callback);
	
	public void canAccessAdministration(AsyncCallback<Boolean> callback);

	public void getSecurityLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate, AsyncCallback<List<SecurityLog>> callback);
}
