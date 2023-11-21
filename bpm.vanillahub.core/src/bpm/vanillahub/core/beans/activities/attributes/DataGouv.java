package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;

public class DataGouv implements IOpenDataProperties, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DATA_GOUV_API_URL = "https://www.data.gouv.fr/api/1/datasets/";
	
	private String datasetId;
	private String resourceId;

	public DataGouv() {
	}
	
	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public boolean isValid() {
		return datasetId != null && !datasetId.isEmpty() &&  resourceId != null && !resourceId.isEmpty();
	}

	public String buildDatasetUrl() {
		StringBuilder builder = new StringBuilder(DataGouv.DATA_GOUV_API_URL);
		builder.append(datasetId);
		return builder.toString();
	}

}
