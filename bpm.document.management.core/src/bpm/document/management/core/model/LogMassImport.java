package bpm.document.management.core.model;

import java.io.Serializable;

public class LogMassImport implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int logId=0;
	private int importId;
	private String logContent;
	
	
	public int getLogId() {
		return logId;
	}
	public void setLogId(int logId) {
		this.logId = logId;
	}
	public int getImportId() {
		return importId;
	}
	public void setImportId(int importId) {
		this.importId = importId;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
}
