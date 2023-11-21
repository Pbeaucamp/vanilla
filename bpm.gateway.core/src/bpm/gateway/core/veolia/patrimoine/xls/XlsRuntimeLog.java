package bpm.gateway.core.veolia.patrimoine.xls;


public class XlsRuntimeLog {
	
	private boolean isError;
	private String message;
	
	public XlsRuntimeLog(boolean isError, String message) {
		this.isError = isError;
		this.message = message;
	}
	
	public boolean isError() {
		return isError;
	}
	
	public String getMessage() {
		return message;
	}
}