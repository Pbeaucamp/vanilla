package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BulkLoad implements Serializable{

	private static final long serialVersionUID = 1L;

	private int bulkId=0;
	private String mainColumn="";
	private List<String> values = new ArrayList<String>();
	private String parentColumnName="Email";
	
	public int getBulkId() {
		return bulkId;
	}
	public void setBulkId(int bulkId) {
		this.bulkId = bulkId;
	}
	public String getMainColumn() {
		return mainColumn;
	}
	public void setMainColumn(String mainColumn) {
		this.mainColumn = mainColumn;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	public void addChild(String item) {
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(item);
	}
	public String getParentColumnName() {
		return parentColumnName;
	}
	public void setParentColumnName(String parentColumnName) {
		this.parentColumnName = parentColumnName;
	}
}
