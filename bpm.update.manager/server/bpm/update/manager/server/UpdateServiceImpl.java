package bpm.update.manager.server;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.update.manager.api.beans.GlobalProgress;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.client.services.UpdateService;
import bpm.update.manager.server.security.UpdateSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UpdateServiceImpl extends RemoteServiceServlet implements UpdateService {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
//	private UpdateSession getSession() throws ServiceException {
//		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), UpdateSession.class);
//	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(UpdateSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	private UpdateManager getManager() throws ServiceException {
		return ApplicationManager.getInstance().getUpdateManager();
	}

	@Override
	public UpdateInformations checkUpdates() throws ServiceException {
		UpdateManager manager = getManager();
		return manager.hasUpdate();
	}

	@Override
	public void updateApplication() throws ServiceException {
		UpdateManager manager = getManager();
		UpdateInformations appsInfos = manager.hasUpdate();
		manager.updateApplication(false, appsInfos);
	}

	@Override
	public GlobalProgress getGlobalProgress() throws ServiceException {
		UpdateManager manager = getManager();
		return manager.getProgress();
	}

	@Override
	public boolean cancelUpdate(String appName) throws ServiceException {
		UpdateManager manager = getManager();
		return manager.cancel();
	}

	@Override
	public List<String> getApplications() throws ServiceException {
		ApplicationManager appManager = ApplicationManager.getInstance();
		return appManager.getApplications();
	}

	@Override
	public Boolean restartServer() throws ServiceException {
		UpdateManager manager = getManager();
		return manager.restartServer();
	}
}
