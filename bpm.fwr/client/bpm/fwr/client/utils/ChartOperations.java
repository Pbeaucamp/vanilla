package bpm.fwr.client.utils;

public enum ChartOperations {
	SUM("Sum"),
	COUNT("Count");
	
	private String operation;
	
	private ChartOperations(String operation) {
		this.setOperation(operation);
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}
}
