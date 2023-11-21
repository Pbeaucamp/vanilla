package bpm.gwt.commons.shared.utils;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ExportResult implements IsSerializable {

	private String reportName;
	
	private int nbMailSuccess;
	
	private List<String> errors;
	private List<String> warnings;
	
	public ExportResult() { }
	
	public ExportResult(String reportName) {
		this.reportName = reportName;
	}
	
	public ExportResult(int nbMailSuccess, List<String> errors, List<String> warnings) {
		this.nbMailSuccess = nbMailSuccess;
		this.errors = errors;
		this.warnings = warnings;
	}
	
	public String getReportName() {
		return reportName;
	}
	
	public int getNbMailSuccess() {
		return nbMailSuccess;
	}
	
	public int getNbMailError() {
		return errors != null ? errors.size() : 0;
	}
	
	public int getNbMailWarning() {
		return warnings != null ? warnings.size() : 0;
	}
	
	public List<String> getErrors() {
		return errors;
	}
	
	public List<String> getWarnings() {
		return warnings;
	}
	
	public boolean hasErrors() {
		return getNbMailError() > 0;
	}
	
	public boolean hasWarnings() {
		return getNbMailWarning() > 0;
	}
}
