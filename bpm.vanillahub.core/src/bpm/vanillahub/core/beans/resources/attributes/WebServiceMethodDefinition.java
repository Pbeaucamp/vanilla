package bpm.vanillahub.core.beans.resources.attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;

public class WebServiceMethodDefinition implements Serializable {

	private static final long serialVersionUID = 1L;

	private WebServiceDefinition webServiceDefinition;

	private String name;
	private String messageNameInput;
	private String messageNameOutput;
	private List<WebServiceParameter> parameters;

	public WebServiceMethodDefinition() {
	}

	public WebServiceMethodDefinition(WebServiceDefinition webServiceDefinition, String name, String messageNameInput, String messageNameOutput) {
		this.webServiceDefinition = webServiceDefinition;
		this.name = name;
		this.messageNameInput = messageNameInput;
		this.messageNameOutput = messageNameOutput;
	}

	public String getName() {
		return name;
	}

	public String getMessageNameInput() {
		return messageNameInput;
	}

	public String getMessageNameOutput() {
		return messageNameOutput;
	}

	public String getPortName() {
		return webServiceDefinition != null ? webServiceDefinition.getPortName() : "";
	}

	public String getBindingName() {
		return webServiceDefinition != null ? webServiceDefinition.getBindingName() : "";
	}

	public String getLocation() {
		return webServiceDefinition != null ? webServiceDefinition.getLocation() : "";
	}

	public List<WebServiceParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<WebServiceParameter> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getWorkflowParameters() {
		List<Parameter> workflowParams = new ArrayList<Parameter>();
		if (parameters != null) {
			for(WebServiceParameter param : parameters) {
				workflowParams.addAll(param.getWorkflowParameters());
			}
		}
		return workflowParams;
	}

	public List<Variable> getWorkflowVariables() {
		List<Variable> workflowVariables = new ArrayList<Variable>();
		if (parameters != null) {
			for(WebServiceParameter param : parameters) {
				workflowVariables.addAll(param.getWorkflowVariables());
			}
		}
		return workflowVariables;
	}
}
