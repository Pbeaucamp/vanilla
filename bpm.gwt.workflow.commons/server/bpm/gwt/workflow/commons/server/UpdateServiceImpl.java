package bpm.gwt.workflow.commons.server;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.workflow.commons.client.service.UpdateService;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.api.remote.RemoteUpdateManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UpdateServiceImpl extends RemoteServiceServlet implements UpdateService {

	private static final long serialVersionUID = 1L;
	
	private RemoteUpdateManager updateManager;

	@Override
	public UpdateInformations checkUpdates(String updateManagerUrl) throws ServiceException {
		try {
			if (updateManagerUrl == null || updateManagerUrl.isEmpty()) {
				return null;
			}
			
			if (updateManager == null) {
				updateManager = new RemoteUpdateManager(updateManagerUrl);
			}
			UpdateInformations updateInfos = updateManager.hasUpdate();
			return updateInfos;
		} catch (Exception e) {
			e.printStackTrace();
			return new UpdateInformations();
		}
	}
}
