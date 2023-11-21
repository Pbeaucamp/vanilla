package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class DynamicLabelComponent extends DashboardComponent implements IComponentData {

	private static final long serialVersionUID = 1L;

	private Dataset dataset;
	
	private Integer columnValueIndex;

	@Override
	public ComponentType getType() {
		return ComponentType.DYNAMIC_LABEL;
	}

	@Override
	protected void clearData() {
		this.dataset = null;

		this.columnValueIndex = null;
	}

	public Integer getColumnValueIndex() {
		return columnValueIndex;
	}

	public void setColumnValueIndex(Integer columnValueIndex) {
		this.columnValueIndex = columnValueIndex;
	}

	@Override
	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
}
