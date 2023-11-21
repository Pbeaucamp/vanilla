package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class RecodeActivity extends Activity {
	
	private static final long serialVersionUID = 1L;
	
	private String inputDataset = "";
	private VariableString outputDataset;
	private int columnIndex = -1;
	private HashMap<String, String> valueMapping = new HashMap<String, String>();
	
	public RecodeActivity() {}
	
	public RecodeActivity(String name) {
		super(TypeActivity.RECODE, name);
	}
	
	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return outputDataset.getVariables();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return outputDataset.getParameters();
	}

	@Override
	public boolean isValid() {
		return inputDataset != null && outputDataset != null;
	}

	public String getInputDataset() {
		return inputDataset;
	}

	public void setInputDataset(String inputDataset) {
		this.inputDataset = inputDataset;
	}

	public VariableString getOutputDataset() {
		return outputDataset;
	}

	public void setOutputDataset(VariableString outputDataset) {
		this.outputDataset = outputDataset;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public HashMap<String, String> getValueMapping() {
		return valueMapping;
	}

	public void setValueMapping(HashMap<String, String> valueMapping) {
		this.valueMapping = valueMapping;
	}

	@Override
	public List<String> getOutputs() {
		List<String> outputs = new ArrayList<String>();
		outputs.add(outputDataset.getStringForTextbox());
		return outputs;
	}
}
