package bpm.fd.api.core.model.components.definition.datagrid;

import java.io.Serializable;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class DataGridColumnOption implements Serializable {
	
	public enum DataGridCellType {
		Visible,
		Hidden,
		Editable
	}

	private static final long serialVersionUID = 1L;

	private String name;
	private String customName;
	
	private DataGridCellType cellType;
	private boolean group;
	
	private Aggregation aggregation;
	
	//Transient (just used for runtime)
	private int index;

	public DataGridColumnOption() { }

	public DataGridColumnOption(String name, DataGridCellType cellType) {
		this.name = name;
		this.cellType = cellType;
	}

	public DataGridColumnOption(String name, String customName, DataGridCellType cellType) {
		this.name = name;
		this.customName = customName;
		this.cellType = cellType;
	}

	public DataGridColumnOption(String name, String customName, DataGridCellType cellType, boolean group, Aggregation aggregation) {
		this.name = name;
		this.customName = customName;
		this.cellType = cellType;
		this.group = group;
		this.aggregation = aggregation;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public DataGridCellType getCellType() {
		return cellType;
	}
	
	public void setCellType(DataGridCellType cellType) {
		this.cellType = cellType;
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
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("column");
		e.addElement("name").setText(name != null ? name : "");
		e.addElement("customName").setText(customName != null ? customName : "");
		e.addElement("cellType").setText(cellType != null ? cellType.toString() : "");
		e.addElement("group").setText(String.valueOf(group));
		e.addElement("aggregation").setText(aggregation != null ? aggregation.toString() : "");
		return e;
	}
}