package bpm.gwt.commons.shared.repository;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PortailItemFmdtDriller extends PortailRepositoryItem {

	private static final long serialVersionUID = 1L;
	
	private List<PortailItemFmdtChart> charts = new ArrayList<PortailItemFmdtChart>();

	public PortailItemFmdtDriller() { }
	
	public PortailItemFmdtDriller(RepositoryItem item, String typeName) {
		super(item, typeName);
	}

	public PortailItemFmdt getItemFmdt() {
		return getParentItem() != null && getParentItem() instanceof PortailItemFmdt ? (PortailItemFmdt) getParentItem() : null;
	}

	@Override
	public int getType() {
		return IRepositoryApi.FMDT_DRILLER_TYPE;
	}
	
	public List<PortailItemFmdtChart> getCharts() {
		return charts;
	}
	
	public void addChart(PortailItemFmdtChart chart) {
		if (charts == null) {
			this.charts = new ArrayList<PortailItemFmdtChart>();
		}
		
		chart.setParent(this);
		
		this.charts.add(chart);
	}
}
