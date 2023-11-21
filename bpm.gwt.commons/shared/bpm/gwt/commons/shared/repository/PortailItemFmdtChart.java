package bpm.gwt.commons.shared.repository;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemFmdtChart extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	
	private SavedChart chart;

	public PortailItemFmdtChart() { }
	
	public PortailItemFmdtChart(RepositoryItem item, String typeName, SavedChart chart) {
		super(item, typeName);
		this.chart = chart;
	}

	public PortailItemFmdtDriller getItemFmdt() {
		return getParentItem() != null && getParentItem() instanceof PortailItemFmdtDriller ? (PortailItemFmdtDriller) getParentItem() : null;
	}

	@Override
	public int getType() {
		return IRepositoryApi.FMDT_CHART_TYPE;
	}
	
	public SavedChart getChart() {
		return chart;
	}
}
