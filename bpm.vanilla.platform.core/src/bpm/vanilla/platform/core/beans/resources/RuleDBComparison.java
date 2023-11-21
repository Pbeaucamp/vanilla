package bpm.vanilla.platform.core.beans.resources;

import java.util.List;


public class RuleDBComparison implements IRule {
	
	private static final long serialVersionUID = 1L;
	
	private OperatorListe operator;
	
	private int datasourceId;
	private String datasourceName;
	
	private int datasetId;
	private String datasetName;
	
	private String columnName;
	
	//List to contains values for the run (not saved in DB)
	private List<String> values;

	public RuleDBComparison() { }
	
	public OperatorListe getOperator() {
		return operator;
	}
	
	public void setOperator(OperatorListe operator) {
		this.operator = operator;
	}
	
	public int getDatasourceId() {
		return datasourceId;
	}
	
	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}
	
	public String getDatasourceName() {
		return datasourceName;
	}
	
	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}
	
	public int getDatasetId() {
		return datasetId;
	}
	
	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}
	
	public String getDatasetName() {
		return datasetName;
	}
	
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}
}
