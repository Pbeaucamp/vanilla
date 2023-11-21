package bpm.vanilla.api.dto;

public class RunVanillaHubParameter {

	private int workflowId;
	private VanillaHubParameter[] parameters;

	public int getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}

	public VanillaHubParameter[] getParameters() {
		return parameters;
	}

	public void setParameters(VanillaHubParameter[] parameters) {
		this.parameters = parameters;
	}

}
