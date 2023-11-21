package bpm.smart.core.model.workflow.activity;

import java.util.ArrayList;
import java.util.List;

import bpm.smart.core.model.workflow.activity.custom.ColumnFilter;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class FilterActivity extends Activity {

	private static final long serialVersionUID = 1L;

	private String inputDataset = "";

	private VariableString outputDataset;
	
	private List<ColumnFilter> filters = new ArrayList<ColumnFilter>();
	
	public FilterActivity() {
	}

	public FilterActivity(String name) {
		super(TypeActivity.FILTER_ACTIVITY, name);
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

	public List<ColumnFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<ColumnFilter> filters) {
		this.filters = filters;
	}

	public List<ColumnFilter> getFiltersByColumn(DataColumn col) {
		List<ColumnFilter> filterByCol = new ArrayList<ColumnFilter>();
		for(ColumnFilter f : filters) {
			if(f.getColumn().equals(col)) {
				filterByCol.add(f);
			}
		}
		return filterByCol;
	}

	public void setFiltersForColumn(List<ColumnFilter> columnFilters, DataColumn col) {
		List<ColumnFilter> filterByCol = getFiltersByColumn(col);
		filters.removeAll(filterByCol);
		filters.addAll(columnFilters);
	}

}
