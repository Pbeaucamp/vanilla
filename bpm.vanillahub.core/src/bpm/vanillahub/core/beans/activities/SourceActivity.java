package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.workflow.commons.beans.TypeActivity;

public class SourceActivity extends ActivityWithResource<Source> {

	private VariableString cibleItem = new VariableString();

	public SourceActivity() { }
	
	public SourceActivity(String name) {
		super(TypeActivity.SOURCE, name);
	}
	
	public VariableString getCibleItemVS() {
		return cibleItem;
	}
	
	public String getCibleItemDisplay() {
		return cibleItem.getStringForTextbox();
	}
	
	public void setCibleItem(VariableString targetItem) {
		this.cibleItem = targetItem;
	}

	public String getCibleItem(List<Parameter> parameters, List<Variable> variables) {
		return cibleItem != null ? cibleItem.getString(parameters, variables) : null;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return cibleItem != null ? cibleItem.getVariables() : new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return cibleItem != null ? cibleItem.getParameters() : new ArrayList<Parameter>();
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0;
	}
}
