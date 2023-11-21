package bpm.document.management.core.model;

import java.io.Serializable;

public class LOV implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id = 0;
	private String valueName = "";
	private String table = "";
	private String itemCode = "";
	private String itemLabel = "";
	private SourceConnection source;
	
	private Integer parentListId;
	private String itemParent;
	
	private String parentValue;

	public String getParentValue() {
		return parentValue;
	}

	public void setParentValue(String parentValue) {
		this.parentValue = parentValue;
	}

	public LOV() {
	}

	public LOV(String name, String table, String itemCode, String itemLabel, SourceConnection source) {
		super();
		this.valueName = name;
		this.table = table;
		this.itemCode = itemCode;
		this.itemLabel = itemLabel;
		this.source = source;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemLabel() {
		return itemLabel;
	}

	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	public SourceConnection getSource() {
		return source;
	}

	public void setSource(SourceConnection source) {
		this.source = source;
	}

	public Integer getParentListId() {
		return parentListId;
	}

	public void setParentListId(Integer parentListId) {
		this.parentListId = parentListId;
	}

	public String getItemParent() {
		return itemParent;
	}

	public void setItemParent(String itemParent) {
		this.itemParent = itemParent;
	}

	
}
