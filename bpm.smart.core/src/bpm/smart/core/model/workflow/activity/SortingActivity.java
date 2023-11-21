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

public class SortingActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private String inputDataset = "";

	private List<DataColumn> columns = new ArrayList<DataColumn>();

	private SortType sortType = SortType.ASC;

	private VariableString outputDataset;
	
	public SortingActivity() {
	}

	public SortingActivity(String name) {
		super(TypeActivity.SORTING_ACTIVITY, name);
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

	public List<DataColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<DataColumn> columns) {
		this.columns = columns;
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	public VariableString getOutputDataset() {
		return outputDataset;
	}

	public void setOutputDataset(VariableString outputDataset) {
		this.outputDataset = outputDataset;
	}

	public enum SortType {
		ASC, DESC;
	}

}
