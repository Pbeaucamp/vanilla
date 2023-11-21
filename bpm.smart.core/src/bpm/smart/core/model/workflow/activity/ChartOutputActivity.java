package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class ChartOutputActivity extends Activity {

	private static final long serialVersionUID = 1L;
	
	private String datasetName = "";
	private String xColumnName = "";
	private String yColumnName = "";
	
	public ChartOutputActivity() {}
	
	public ChartOutputActivity(String name) {
		super(TypeActivity.CHART, name);
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
	
	

}
