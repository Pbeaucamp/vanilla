package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class ClientCredentialsProperties implements ISecurityDataService {

	private static final long serialVersionUID = 1L;
	
	private VariableString accessTokenURL = new VariableString();
	private VariableString clientId = new VariableString();
	private VariableString clientSecret = new VariableString();
	private VariableString scope = new VariableString();

	public ClientCredentialsProperties() {
	}

	public VariableString getAccessTokenURLVS() {
		return accessTokenURL;
	}

	public String getAccessTokenURLDisplay() {
		return accessTokenURL.getStringForTextbox();
	}

	public void setAccessTokenURL(VariableString accessTokenURL) {
		this.accessTokenURL = accessTokenURL;
	}

	public String getAccessTokenURL(List<Parameter> parameters, List<Variable> variables) {
		return accessTokenURL.getString(parameters, variables);
	}
	

	public VariableString getClientIdVS() {
		return clientId;
	}

	public String getClientIdDisplay() {
		return clientId.getStringForTextbox();
	}

	public void setClientId(VariableString clientId) {
		this.clientId = clientId;
	}

	public String getClientId(List<Parameter> parameters, List<Variable> variables) {
		return clientId.getString(parameters, variables);
	}
	

	public VariableString getClientSecretVS() {
		return clientSecret;
	}

	public String getClientSecretDisplay() {
		return clientSecret.getStringForTextbox();
	}

	public void setClientSecret(VariableString clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientSecret(List<Parameter> parameters, List<Variable> variables) {
		return clientSecret.getString(parameters, variables);
	}
	

	public VariableString getScopeVS() {
		return scope;
	}

	public String getScopeDisplay() {
		return scope.getStringForTextbox();
	}

	public void setScope(VariableString scope) {
		this.scope = scope;
	}

	public String getScope(List<Parameter> parameters, List<Variable> variables) {
		return scope.getString(parameters, variables);
	}

	@Override
	public boolean isValid() {
		return accessTokenURL != null && !accessTokenURL.getStringForTextbox().isEmpty()
				&& clientId != null && !clientId.getStringForTextbox().isEmpty()
				&& clientSecret != null && !clientSecret.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(accessTokenURL.getVariables());
		variables.addAll(clientId.getVariables());
		variables.addAll(clientSecret.getVariables());
		variables.addAll(scope.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(accessTokenURL.getParameters());
		parameters.addAll(clientId.getParameters());
		parameters.addAll(clientSecret.getParameters());
		parameters.addAll(scope.getParameters());
		return parameters;
	}

}
