package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentRepositoryItem;
import bpm.vanilla.platform.core.IRepositoryApi;

public class KpiComponent extends DashboardComponent implements IComponentRepositoryItem {
	
	private static final long serialVersionUID = 1L;
	
	private int itemId;
	
	@Override
	public int getItemId() {
		return itemId;
	}

	@Override
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public int getItemType() {
		return IRepositoryApi.KPI_THEME;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.KPI;
	}

	@Override
	protected void clearData() {
		this.itemId = 0;
	}
}
