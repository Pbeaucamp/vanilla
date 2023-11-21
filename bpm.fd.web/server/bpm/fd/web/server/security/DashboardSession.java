package bpm.fd.web.server.security;

import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.repository.FaApiHelper;
import bpm.fd.core.xstream.IDashboardManager;
import bpm.fd.core.xstream.RemoteDashboardManager;
import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.platform.core.IRepositoryApi;

public class DashboardSession extends CommonSession {

	private IDashboardManager dashboardManager;
	private FaApiHelper faApiHelper;
	private IFreeMetricsManager fmManager;

	public DashboardSession() {

	}

	public IDashboardManager getDashboardManager() {
		if (dashboardManager == null) {
			dashboardManager = new RemoteDashboardManager(getRepositoryConnection());
		}
		return dashboardManager;
	}		
	
	public FaApiHelper getFaApiHelper() {
		if (faApiHelper == null) {
			faApiHelper = new FaApiHelper(getVanillaRuntimeUrl(), UnitedOlapLoaderFactory.getLoader());
			RemoteServiceProvider remoteServiceProvider = new RemoteServiceProvider();
			remoteServiceProvider.configure(getVanillaContext());
			UnitedOlapServiceProvider.getInstance().init(remoteServiceProvider.getRuntimeProvider(), remoteServiceProvider.getModelProvider());
		}
		return faApiHelper;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FDWEB;
	}

	public IFreeMetricsManager getFmManager() {
		if(fmManager == null) {
			fmManager = new RemoteFreeMetricsManager(getVanillaContext());
		}
		return fmManager;
	}
}
