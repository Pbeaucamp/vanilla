package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class SimpleLinearRegActivity extends Activity {
		
	private static final long serialVersionUID = 1L;
	
	private boolean withGraph;
	private String datasetName = "";
	private String xColumnName = "";
	private String yColumnName = "";

	public SimpleLinearRegActivity(){}
	
	public SimpleLinearRegActivity(String name) {
		super(TypeActivity.SIMPLE_LINEAR_REG, name);
		this.withGraph = false;
	}
	
	public boolean isWithGraph() {
		return withGraph;
	}

	public void setWithGraph(boolean withGraph) {
		this.withGraph = withGraph;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getxColumnName() {
		return xColumnName;
	}

	public void setxColumnName(String xColumnName) {
		this.xColumnName = xColumnName;
	}

	public String getyColumnName() {
		return yColumnName;
	}

	public void setyColumnName(String yColumnName) {
		this.yColumnName = yColumnName;
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
		return !datasetName.isEmpty() && !xColumnName.isEmpty() && !yColumnName.isEmpty();
	}
}
