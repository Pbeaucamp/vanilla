package bpm.map.viewer.web.server;

import java.util.List;

import bpm.fm.api.IFreeMetricsManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.IRepositoryApi;

public class MapViewerSession extends CommonSession {
	
	private List<Metric> metrics;
	
	private IFreeMetricsManager manager;
	
	public MapViewerSession() {}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public IFreeMetricsManager getManager() {
		return manager;
	}

	public void setManager(IFreeMetricsManager manager) {
		this.manager = manager;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FMUSERWEB;
	}
	
	
}
