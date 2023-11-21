package bpm.vanilla.api.dto;

import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;

public class GenerateKpiParameter {

	private int groupId;
	private int repositoryId;
	private KPIGenerationInformations integrationInfos;

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

	public KPIGenerationInformations getIntegrationInfos() {
		return integrationInfos;
	}

	public void setIntegrationInfos(KPIGenerationInformations integrationInfos) {
		this.integrationInfos = integrationInfos;
	}

}
