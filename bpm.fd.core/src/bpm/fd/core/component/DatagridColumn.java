package bpm.fd.core.component;

import java.io.Serializable;

import bpm.fd.api.core.model.components.definition.datagrid.Aggregation;
import bpm.vanilla.platform.core.beans.data.DataColumn;

public class DatagridColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	private DataColumn column;
	private String customName;

	private boolean visible;
	private boolean group;
	
	private Aggregation aggregation;

	public DatagridColumn() { }

	public DatagridColumn(DataColumn column, String customName, boolean visible) {
		this.column = column;
		this.customName = customName != null ? customName : (column.getColumnLabel() != null ? column.getColumnLabel() : column.getColumnName());
		this.visible = visible;
	}

	public DatagridColumn(DataColumn column, String customName, boolean visible, boolean group, Aggregation aggregation) {
		this.column = column;
		this.customName = customName != null ? customName : (column.getColumnLabel() != null ? column.getColumnLabel() : column.getColumnName());
		this.visible = visible;
		this.group = group;
		this.aggregation = aggregation;
	}

	public String getName() {
		return column.getColumnName() != null ? column.getColumnName() : "Unknown";
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isGroup() {
		return group;
	}
	
	public void setGroup(boolean group) {
		this.group = group;
	}
	
	public Aggregation getAggregation() {
		return aggregation;
	}
	
	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
	}
}