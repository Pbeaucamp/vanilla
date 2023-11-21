package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;

public class CheckResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String value;
	private boolean isError;
	
	public CheckResult() { }
	
	public CheckResult(String value, boolean isError) {
		this.value = value;
		this.isError = isError;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public String getValue() {
		return value;
	}
}
