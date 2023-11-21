package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class ApiKeyProperties implements ISecurityDataService {

	private static final long serialVersionUID = 1L;

	private VariableString apiKeyName = new VariableString();
	private VariableString apiKey = new VariableString();

	public ApiKeyProperties() {
	}
	
	public VariableString getApiKeyNameVS() {
		return apiKeyName;
	}
	
	public String getApiKeyNameDisplay() {
		return apiKeyName.getStringForTextbox();
	}
	
	public void setApiKeyName(VariableString apiKeyName) {
		this.apiKeyName = apiKeyName;
	}
	
	public String getApiKeyName(List<Parameter> parameters, List<Variable> variables) {
		return apiKeyName.getString(parameters, variables);
	}

	public VariableString getApiKeyVS() {
		return apiKey;
	}
	
	public String getApiKeyDisplay() {
		return apiKey.getStringForTextbox();
	}
	
	public void setApiKey(VariableString apiKey) {
		this.apiKey = apiKey;
	}
	
	public String getApiKey(List<Parameter> parameters, List<Variable> variables) {
		return apiKey.getString(parameters, variables);
	}
	
	@Override
	public boolean isValid() {
		return apiKeyName != null && !apiKeyName.getStringForTextbox().isEmpty()
				&& apiKey != null && !apiKey.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(apiKeyName.getVariables());
		variables.addAll(apiKey.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(apiKeyName.getParameters());
		parameters.addAll(apiKey.getParameters());
		return parameters;
	}

}
