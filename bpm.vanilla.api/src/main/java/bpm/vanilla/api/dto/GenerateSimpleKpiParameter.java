package bpm.vanilla.api.dto;

import bpm.vanilla.platform.core.beans.resources.SimpleKPIGenerationInformations;

public class GenerateSimpleKpiParameter {

	private int groupId;
	private int repositoryId;
	private String modifyMetadata;
	private String modifyIntegration;
	private SimpleKPIGenerationInformations integrationInfos;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public boolean isModifyMetadata() {
		return modifyMetadata != null && (modifyMetadata.equals("true") || modifyMetadata.equals("1"));
	}
	
	public String getModifyMetadata() {
		return modifyMetadata;
	}
	
	public void setModifyMetadata(String modifyMetadata) {
		this.modifyMetadata = modifyMetadata;
	}
	
	public boolean isModifyIntegration() {
		return modifyIntegration != null && (modifyIntegration.equals("true") || modifyIntegration.equals("1"));
	}
	
	public void setModifyIntegration(String modifyIntegration) {
		this.modifyIntegration = modifyIntegration;
	}

	public SimpleKPIGenerationInformations getIntegrationInfos() {
		return integrationInfos;
	}

	public void setIntegrationInfos(SimpleKPIGenerationInformations integrationInfos) {
		this.integrationInfos = integrationInfos;
	}

}
