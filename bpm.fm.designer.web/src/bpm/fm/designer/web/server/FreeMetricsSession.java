package bpm.fm.designer.web.server;

import java.util.List;

import bpm.fm.api.IFreeMetricsManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.IRepositoryApi;

public class FreeMetricsSession extends CommonSession {
	
	private List<Metric> metrics;
	private List<Application> applications;
	
	private IFreeMetricsManager manager;
	
	public FreeMetricsSession() {}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FMDESIGNER;
	}

	public IFreeMetricsManager getManager() {
		return manager;
	}

	public void setManager(IFreeMetricsManager manager) {
		this.manager = manager;
	}
	
	
}
