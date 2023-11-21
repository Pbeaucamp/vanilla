package bpm.metadata.web.server.security;

import bpm.gwt.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.IRepositoryApi;

public class MetadataSession extends CommonSession {

	public MetadataSession() {

	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FMDT_WEB;
	}
}
