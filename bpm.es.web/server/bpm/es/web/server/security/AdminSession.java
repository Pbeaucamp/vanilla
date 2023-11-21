package bpm.es.web.server.security;

import bpm.gwt.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.IRepositoryApi;

public class AdminSession extends CommonSession {

	public AdminSession() {

	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.ADMIN_WEB;
	}
}
