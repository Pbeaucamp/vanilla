package bpm.workflow.commons.beans.activity;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class StartActivity extends Activity {

	public StartActivity() { }
	
	public StartActivity(String name) {
		super(TypeActivity.START, name);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public List<Variable> getVariables(List resources) {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters(List resources) {
		return new ArrayList<Parameter>();
	}

}
