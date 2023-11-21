package bpm.update.manager.server.security;

import bpm.gwt.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.IRepositoryApi;

public class UpdateSession extends CommonSession {

	public UpdateSession() {

	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.UPDATE_MANAGER;
	}
}
