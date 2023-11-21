package bpm.faweb.shared;

import java.util.HashMap;

public class OptionsExport {	
	public enum ExportType{
		PDF,
		HTML,
		XLS,
		XLS_DRILL_THROUGH
	}
	
	private String reportName;
	private ExportType exportType;
	private HashMap<String, Object> options = new HashMap<String, Object>();
	private boolean exportChart = false;
	
	public OptionsExport(){ }

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}

	public ExportType getExportType() {
		return exportType;
	}

	public void setOptions(HashMap<String, Object> options) {
		this.options = options;
	}

	public HashMap<String, Object> getOptions() {
		return options;
	}

	public void setExportChart(boolean exportChart) {
		this.exportChart = exportChart;
	}

	public boolean isExportChart() {
		return exportChart;
	}
	
}
