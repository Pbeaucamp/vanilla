package bpm.vanilla.api.dto;

public class ValidationDataParameter {

	private String d4cUrl;
	private String d4cOrg;
	private String datasetId;
	private String resourceId;
	private int contractId;
	private String[] schemas;

	public String getD4cUrl() {
		return d4cUrl;
	}

	public void setD4cUrl(String d4cUrl) {
		this.d4cUrl = d4cUrl;
	}

	public String getD4cOrg() {
		return d4cOrg;
	}

	public void setD4cOrg(String d4cOrg) {
		this.d4cOrg = d4cOrg;
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

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public String[] getSchemas() {
		return schemas;
	}
	
	public void setSchemas(String[] schemas) {
		this.schemas = schemas;
	}

}
