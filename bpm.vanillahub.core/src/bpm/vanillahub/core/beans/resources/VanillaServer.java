package bpm.vanillahub.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class VanillaServer extends ApplicationServer {

	private static final long serialVersionUID = 1L;
	
	private VariableString groupId;
	private String groupName;

	private VariableString repositoryId;
	private String repositoryName;

	public VanillaServer() {
		super("", TypeServer.VANILLA);
	}

	public VanillaServer(String name) {
		super(name, TypeServer.VANILLA);
	}

	public VariableString getGroupId() {
		return groupId;
	}

	public void setGroupId(VariableString groupId) {
		this.groupId = groupId;
	}

	public VariableString getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(VariableString repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = super.getVariables();
		variables.addAll(groupId != null ? groupId.getVariables() : new ArrayList<Variable>());
		variables.addAll(repositoryId != null ? repositoryId.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = super.getParameters();
		parameters.addAll(groupId != null ? groupId.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(repositoryId != null ? repositoryId.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}

	public void updateInfo(String name, VariableString url, VariableString login, VariableString password, VariableString groupId, VariableString repoId, TypeServer typeServer) {
		setName(name);
		super.updateInfo(name, url, login, password, typeServer);
		this.groupId = groupId;
		this.repositoryId = repoId;
	}

}
