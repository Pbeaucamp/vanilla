package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;
import java.util.List;

/**
 * OpenDataSoft Properties
 */
public class ODSProperties implements IOpenDataProperties, Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum Format {
		CSV,
		JSON,
		GEOJSON,
		XLS
	}

	private boolean crawlOneDataset;
	private String datasetId;
	private String query;
	
	private List<Format> formats;
	private Integer limit;
	
	public ODSProperties() {
	}
	
	public ODSProperties(Integer limit, boolean crawlOneDataset, String datasetId, String query) {
		this.limit = limit;
		this.crawlOneDataset = crawlOneDataset;
		this.datasetId = datasetId;
		this.query = query;
	}
	
	public boolean isCrawlOneDataset() {
		return crawlOneDataset;
	}
	
	public String getDatasetId() {
		return datasetId;
	}
	
	public String getQuery() {
		return query;
	}
	
//	public VariableString getDatasetId() {
//		return datasetId;
//	}
//	
//	public String getDatasetIdDisplay() {
//		return datasetId.getStringForTextbox();
//	}
//	
//	public String getDatasetId(List<Parameter> parameters, List<Variable> variables) {
//		return datasetId.getString(parameters, variables);
//	}
//	
//	public void setDatasetId(VariableString datasetId) {
//		this.datasetId = datasetId;
//	}
//	
//	public VariableString getQuery() {
//		return query;
//	}
//	
//	public String getQueryDisplay() {
//		return query.getStringForTextbox();
//	}
//	
//	public String getQuery(List<Parameter> parameters, List<Variable> variables) {
//		return query.getString(parameters, variables);
//	}
//	
//	public void setQuery(VariableString query) {
//		this.query = query;
//	}
	
	public List<Format> getFormats() {
		return formats;
	}
	
	public Integer getLimit() {
		return limit;
	}
	
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
