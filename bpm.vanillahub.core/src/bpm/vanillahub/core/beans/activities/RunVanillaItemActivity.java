package bpm.vanillahub.core.beans.activities;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.VanillaItemParameter;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.workflow.commons.beans.TypeActivity;

public class RunVanillaItemActivity extends ActivityWithResource<VanillaServer> {

	private Integer itemId;
	private String itemName;
	private List<VanillaItemParameter> parameters;

	public RunVanillaItemActivity() {

	}

	public RunVanillaItemActivity(String name) {
		super(TypeActivity.VANILLA_ITEM, name);
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public List<VanillaItemParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<VanillaItemParameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public boolean isValid() {
		return itemId != null && itemId > 0;
	}
	
	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = super.getVariables(resources);
		if (parameters != null) {
			for (VanillaItemParameter parameter : parameters) {
				variables.addAll(parameter.getVariables());
			}
		}
		return variables;
	}
	
	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = super.getParameters(resources);
		if (this.parameters != null) {
			for (VanillaItemParameter parameter : this.parameters) {
				parameters.addAll(parameter.getParameters());
			}
		}
		return parameters;
	}

}
