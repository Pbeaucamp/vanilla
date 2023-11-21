package bpm.vanillahub.core.beans.resources;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class AklaboxServer extends ApplicationServer {

	private static final long serialVersionUID = 1L;

	public AklaboxServer() {
		super("", TypeServer.AKLABOX);
	}  

	public AklaboxServer(String name) {
		super(name, TypeServer.AKLABOX);
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = super.getVariables();
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = super.getParameters();
		return parameters;
	}

	public void updateInfo(String name, VariableString url, VariableString login, VariableString password, VariableString groupId, VariableString repoId, TypeServer typeServer) {
		setName(name);
		super.updateInfo(name, url, login, password, typeServer);
	}

}
