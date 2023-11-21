package bpm.fwr.client.utils;


public enum VariableTypes {
	USER_GROUP("%usergroup%"),
	DATE("%currdate%");
	
	private String value;
	
	private VariableTypes(String value) {
		this.setValue(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
