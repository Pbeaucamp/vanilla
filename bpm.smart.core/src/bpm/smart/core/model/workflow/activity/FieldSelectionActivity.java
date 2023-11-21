package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class FieldSelectionActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private String inputDataset = "";
	private VariableString outputDataset;

	private List<DataColumn> selectedColumns = new ArrayList<DataColumn>();

	public FieldSelectionActivity() {
	}

	public FieldSelectionActivity(String name) {
		super(TypeActivity.FIELD_SELECTION, name);
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
		return true;
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

	public List<DataColumn> getSelectedColumns() {
		return selectedColumns;
	}

	public void setSelectedColumns(List<DataColumn> selectedColumns) {
		this.selectedColumns = selectedColumns;
	}

}
