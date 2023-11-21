package bpm.fwr.api.beans.dataset;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class FWRFilter implements IResource {
	
	private String name;
	private String columnName;
	private String tableName;
	private List<String> values = new ArrayList<String>();
	private String operator = "=";

	// Metadata informations
	private int metadataId;
	private String packageParent;
	private String modelParent;

	public FWRFilter() {
	}

	public FWRFilter(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public void addValue(String v) {
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(v);
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
	}

	public int getMetadataId() {
		return metadataId;
	}

	public void setPackageParent(String packageParent) {
		this.packageParent = packageParent;
	}

	public String getPackageParent() {
		return packageParent;
	}

	public void setModelParent(String modelParent) {
		this.modelParent = modelParent;
	}

	public String getModelParent() {
		return modelParent;
	}

	@Override
	public boolean equals(Object obj) {
		return name.equals(((FWRFilter) obj).getName());
	}
	
	@Override
	public String toString() {
		return name;
	}
}
