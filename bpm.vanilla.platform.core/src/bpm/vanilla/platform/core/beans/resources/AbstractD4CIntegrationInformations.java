package bpm.vanilla.platform.core.beans.resources;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.meta.MetaValue;

public abstract class AbstractD4CIntegrationInformations implements Serializable {
	
	public static String KEY_INFO_SERVICE_TYPE = "service-type";
	public static String KEY_INFO_LAYER = "layer";
	
	public static String KEY_INFO_AUTH_TYPE = "auth-type";
	public static String KEY_INFO_AUTH_LOGIN = "auth-login";
	public static String KEY_INFO_AUTH_PASSWORD = "auth-password";
	public static String KEY_INFO_AUTH_API_KEY_NAME = "auth-api-key-name";
	public static String KEY_INFO_AUTH_API_KEY = "auth-api-key";

	private static final long serialVersionUID = 1L;

	private ContractType type;

	private Integer contractId;
	private Integer documentId;
	
	private int hubId;
	private String hubName;
	
	private String d4cUrl;
	private String d4cLogin;
	private String d4cPassword;
	
	private String sourceOrganisation;

	private String targetOrganisation;
	
	// CKAN ID
	private String targetDatasetName;
	
	// CKAN NAME
	private String targetDatasetCustomName;
	
	// CKAN Title
	private String targetDatasetTitle;
	
	private String targetDatasetDescription;
	private List<MetaValue> metadata;

	private List<String> validationSchemas;
	private HashMap<String, String> additionnalInfos;
	
	private ISchedule schedule;

	public ContractType getType() {
		return type;
	}

	public void setType(ContractType type) {
		this.type = type;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}
	
	public Integer getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public int getHubId() {
		return hubId;
	}

	public void setHubId(int hubId) {
		this.hubId = hubId;
	}

	public String getHubName() {
		return hubName;
	}

	public void setHubName(String hubName) {
		this.hubName = hubName;
	}

	public String getD4cUrl() {
		return d4cUrl;
	}

	public void setD4cUrl(String d4cUrl) {
		this.d4cUrl = d4cUrl;
	}

	public String getD4cLogin() {
		return d4cLogin;
	}

	public void setD4cLogin(String d4cLogin) {
		this.d4cLogin = d4cLogin;
	}

	public String getD4cPassword() {
		return d4cPassword;
	}

	public void setD4cPassword(String d4cPassword) {
		this.d4cPassword = d4cPassword;
	}

	public String getSourceOrganisation() {
		return sourceOrganisation;
	}

	public void setSourceOrganisation(String sourceOrganisation) {
		this.sourceOrganisation = sourceOrganisation;
	}

	public String getTargetOrganisation() {
		return targetOrganisation;
	}

	public void setTargetOrganisation(String targetOrganisation) {
		this.targetOrganisation = targetOrganisation;
	}

	public String getTargetDatasetName() {
		return targetDatasetName;
	}

	public void setTargetDatasetName(String targetDatasetName) {
		this.targetDatasetName = targetDatasetName;
	}
	
	public String getTargetDatasetCustomName() {
		return targetDatasetCustomName != null && !targetDatasetCustomName.isEmpty() ? targetDatasetCustomName : targetDatasetName;
	}
	
	public void setTargetDatasetCustomName(String targetDatasetCustomName) {
		this.targetDatasetCustomName = targetDatasetCustomName;
	}
	
	public String getTargetDatasetTitle() {
		return targetDatasetTitle;
	}
	
	public void setTargetDatasetTitle(String targetDatasetTitle) {
		this.targetDatasetTitle = targetDatasetTitle;
	}

	public String getTargetDatasetDescription() {
		return targetDatasetDescription;
	}

	public void setTargetDatasetDescription(String targetDatasetDescription) {
		this.targetDatasetDescription = targetDatasetDescription;
	}

	public List<MetaValue> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<MetaValue> metadata) {
		this.metadata = metadata;
	}

	public List<String> getValidationSchemas() {
		return validationSchemas;
	}

	public void setValidationSchemas(List<String> validationSchemas) {
		this.validationSchemas = validationSchemas;
	}
	
	public HashMap<String, String> getAdditionnalInfos() {
		return additionnalInfos;
	}
	
	public void setAdditionnalInfos(HashMap<String, String> additionnalInfos) {
		this.additionnalInfos = additionnalInfos;
	}

	public ISchedule getSchedule() {
		return schedule;
	}
	
	public void setSchedule(ISchedule schedule) {
		this.schedule = schedule;
	}
}
