package bpm.vanilla.workplace.core.disco;


public class DisconnectedDatasetParameter {

	private String columnName;
	private String separator;
	private String operator;
	
	public DisconnectedDatasetParameter(String columnName, String separator, String operator) {
		this.columnName = columnName;
		this.separator = separator;
		this.operator = operator;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
