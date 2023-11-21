package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class BasicAuthProperties implements ISecurityDataService {

	private static final long serialVersionUID = 1L;
	
	private VariableString login = new VariableString();
	private VariableString password = new VariableString();

	public BasicAuthProperties() {
	}
	
	public VariableString getLoginVS() {
		return login;
	}
	
	public String getLoginDisplay() {
		return login.getStringForTextbox();
	}
	
	public void setLogin(VariableString login) {
		this.login = login;
	}
	
	public String getLogin(List<Parameter> parameters, List<Variable> variables) {
		return login.getString(parameters, variables);
	}
	
	public VariableString getPasswordVS() {
		return password;
	}
	
	public String getPasswordDisplay() {
		return password.getStringForTextbox();
	}
	
	public void setPassword(VariableString password) {
		this.password = password;
	}
	
	public String getPassword(List<Parameter> parameters, List<Variable> variables) {
		return password.getString(parameters, variables);
	}
	
	@Override
	public boolean isValid() {
		return login != null && !login.getStringForTextbox().isEmpty()
				&& password != null && !password.getStringForTextbox().isEmpty();
	}
	
	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(login.getVariables());
		variables.addAll(password.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(login.getParameters());
		parameters.addAll(password.getParameters());
		return parameters;
	}

}
