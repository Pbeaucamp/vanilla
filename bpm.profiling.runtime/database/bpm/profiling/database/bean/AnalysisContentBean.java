package bpm.profiling.database.bean;

public class AnalysisContentBean {
	private int id;
	private int analysisId;
	private String tableName;
	private String columnName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(int analysisId) {
		this.analysisId = analysisId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	
	
	
}
