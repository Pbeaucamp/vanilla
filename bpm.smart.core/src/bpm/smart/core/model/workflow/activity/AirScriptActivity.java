package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class AirScriptActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private int rProjectId;
	private int rScriptId;
	
	private transient RScriptModel scriptModel;

	public AirScriptActivity() {}
	
	public AirScriptActivity(String name) {
		super(TypeActivity.RSCRIPT, name);
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return new ArrayList<Parameter>();
	}

	@Override
	public boolean isValid() {
		return rProjectId > -1 && rScriptId > -1;
	}

	public int getrProjectId() {
		return rProjectId;
	}

	public void setrProjectId(int rProjectId) {
		this.rProjectId = rProjectId;
	}

	public int getrScriptId() {
		return rScriptId;
	}

	public void setrScriptId(int rScriptId) {
		this.rScriptId = rScriptId;
	}

	public RScriptModel getScriptModel() {
		return scriptModel;
	}

	public void setScriptModel(RScriptModel scriptModel) {
		this.scriptModel = scriptModel;
	}

	

}
