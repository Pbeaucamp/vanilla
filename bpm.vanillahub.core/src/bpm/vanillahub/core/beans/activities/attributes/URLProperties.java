package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class URLProperties implements DataServiceAttribute {

	private static final long serialVersionUID = 1L;
	
	private VariableString url = new VariableString();
	private VariableString outputName = new VariableString();
	private boolean unzip;

	public URLProperties() {
	}
	
	public boolean isUnzip() {
		return unzip;
	}
	
	public void setUnzip(boolean unzip) {
		this.unzip = unzip;
	}

	public VariableString getUrlVS() {
		return url;
	}

	public String getUrDisplay() {
		return url.getStringForTextbox();
	}

	public void setUr(VariableString outputName) {
		this.url = outputName;
	}

	public String getUrl(List<Parameter> parameters, List<Variable> variables) {
		return url.getString(parameters, variables);
	}

	public VariableString getOutputNameVS() {
		return outputName;
	}

	public String getOutputNameDisplay() {
		return outputName.getStringForTextbox();
	}

	public void setOutputName(VariableString outputName) {
		this.outputName = outputName;
	}

	@Override
	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return outputName.getString(parameters, variables);
	}

	@Override
	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables) {
		return url.getString(parameters, variables);
	}

	@Override
	public boolean isValid() {
		return outputName != null && !outputName.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(url.getVariables());
		variables.addAll(outputName.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(url.getParameters());
		parameters.addAll(outputName.getParameters());
		return parameters;
	}

}
