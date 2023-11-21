package bpm.es.web.server;

import java.util.Date;
import java.util.List;

import bpm.es.web.client.services.AdminService;
import bpm.es.web.server.security.AdminSession;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.Settings;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {

	private AdminSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), AdminSession.class);
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(AdminSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	@Override
	public void manageUser(User user, boolean edit) throws ServiceException {
		AdminSession session = getSession();
		try {
			if (edit) {
				session.getVanillaApi().getVanillaSecurityManager().updateUser(user);
			}
			else {
				session.getVanillaApi().getVanillaSecurityManager().addUser(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save this user : " + e.getMessage());
		}
	}

	@Override
	public void deleteUser(User user) throws ServiceException {
		AdminSession session = getSession();
		try {
			if (user.isSuperUser()) {
				throw new ServiceException("You cannot delete a super user.");
			}
			session.getVanillaApi().getVanillaSecurityManager().delUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete this user : " + e.getMessage());
		}
	}

	@Override
	public Settings getSettings() throws ServiceException {
		try {
			return getSession().getVanillaApi().getVanillaSecurityManager().getSettings();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get settings : " + e.getMessage());
		}
	}

	@Override
	public void updateSettings(Settings settings) throws ServiceException {
		try {
			getSession().getVanillaApi().getVanillaSecurityManager().updateSettings(settings);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update settings : " + e.getMessage());
		}
	}

	@Override
	public boolean canAccessAdministration() throws ServiceException {
		AdminSession session = getSession();
		if (!session.getUser().isSuperUser()) {
			return false;
		}

		String clientIp = getThreadLocalRequest().getRemoteAddr();

		try {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String ipPattern = config.getProperty(VanillaConfiguration.P_ADMINISTRATION_IP_ALLOWED);
			if (ipPattern != null && !ipPattern.isEmpty()) {
				String[] ips = ipPattern.split(",");
				for (String ip : ips) {
					if (ip.contains("*")) {
						ip = ip.substring(0, ip.indexOf("*"));
					}

					if (clientIp.startsWith(ip)) {
						return true;
					}
				}

				throw new Exception("Client IP does not match pattern '" + ipPattern + "' ");
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}

	@Override
	public List<SecurityLog> getSecurityLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate) throws ServiceException {
		AdminSession session = getSession();
		try {
			return session.getVanillaApi().getVanillaLoggingManager().getSecurityLogs(userId, type, startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get security logs : " + e.getMessage());
		}
	}
}
