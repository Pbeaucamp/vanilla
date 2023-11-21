package bpm.vanilla.platform.core.beans.alerts;

import java.util.Map;

import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;



public class ActionGateway implements IActionInformation {

	private static final long serialVersionUID = -8853664181032575164L;
	
	private int directoryItemId;
	private int repositoryId;
	private Map<VanillaParameter, String> parameters;

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public void setRepositoryId(int repositoryId) {
		this.repositoryId = repositoryId;
	}

	public int getRepositoryId() {
		return repositoryId;
	}
	
	public Map<VanillaParameter, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<VanillaParameter, String> parameters) {
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object o) {
		return directoryItemId == ((ActionGateway)o).getDirectoryItemId() && repositoryId == ((ActionGateway)o).getRepositoryId();
	}
}