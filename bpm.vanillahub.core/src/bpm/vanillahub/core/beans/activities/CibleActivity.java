package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.resources.Cible;

public class CibleActivity extends ActivityWithResource<Cible> {

	private static final long serialVersionUID = 1L;
	
	private VariableString targetItem = new VariableString();
	
	public CibleActivity() {
		super();
	}

	public CibleActivity(String name) {
		super(TypeActivity.CIBLE, name);
	}
	
	public VariableString getTargetItemVS() {
		return targetItem;
	}
	
	public String getTargetItemDisplay() {
		return targetItem.getStringForTextbox();
	}
	
	public void setTargetItem(VariableString targetItem) {
		this.targetItem = targetItem;
	}

	public String getTargetItem(List<Parameter> parameters, List<Variable> variables) {
		return targetItem != null ? targetItem.getString(parameters, variables) : null;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return targetItem != null ? targetItem.getVariables() : new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return targetItem != null ? targetItem.getParameters() : new ArrayList<Parameter>();
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0;
	}
}
