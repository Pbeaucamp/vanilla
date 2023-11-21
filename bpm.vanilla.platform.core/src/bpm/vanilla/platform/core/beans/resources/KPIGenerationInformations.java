package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
 * This class is used to define a KPI creation (with KpiUser, KpiDesigner)
 * 
 * It is opposed to the Simple KPI which is just a join and calcul about datasets
 */
public class KPIGenerationInformations implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String observatory;
	private String theme;

//	private String d4cUrl;
	private String datasetId;
	private String resourceId;
//	private String resourceUrl;

	private List<String> axes;
	private List<String> metrics;
	
	private Date selectedDate;
	
	private int folderTargetId;

	public String getObservatory() {
		return observatory;
	}
	
	public void setObservatory(String observatory) {
		this.observatory = observatory;
	}

	public String getTheme() {
		return theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
//	public String getD4cUrl() {
//		return d4cUrl;
//	}
//	
//	public void setD4cUrl(String d4cUrl) {
//		this.d4cUrl = d4cUrl;
//	}

	public String getDatasetId() {
		return datasetId;
	}
	
	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

//	public String getResourceUrl() {
//		return resourceUrl;
//	}
//	
//	public void setResourceUrl(String resourceUrl) {
//		this.resourceUrl = resourceUrl;
//	}

	public List<String> getAxes() {
		return axes;
	}
	
	public void setAxes(List<String> axes) {
		this.axes = axes;
	}

	public List<String> getMetrics() {
		return metrics;
	}
	
	public void setMetrics(List<String> metrics) {
		this.metrics = metrics;
	}

	public String getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public Date getSelectedDate() {
		return selectedDate;
	}
	
	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}
	
	public int getFolderTargetId() {
		return folderTargetId;
	}
	
	public void setFolderTargetId(int folderTargetId) {
		this.folderTargetId = folderTargetId;
	}
}