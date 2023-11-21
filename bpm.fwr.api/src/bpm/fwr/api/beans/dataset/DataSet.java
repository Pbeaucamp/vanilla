package bpm.fwr.api.beans.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Class representing a dataset
 * @author Marc
 *
 */
public class DataSet implements Serializable {

	private String name;
	
	private DataSource datasource;
	private JoinDataSet parent;
	
	private List<Column> columns;
	private List<FWRFilter> fwrFilters;
	private List<FwrPrompt> prompts;
	private List<String> filters;
	private List<FwrRelationStrategy> relationStrategies;
	
	private boolean isPreview;
	private String language;
	
	public DataSet() {
		fwrFilters = new ArrayList<FWRFilter>();
		prompts = new ArrayList<FwrPrompt>();
		filters = new ArrayList<String>();
		columns = new ArrayList<Column>();
		relationStrategies = new ArrayList<FwrRelationStrategy>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public void setParent(JoinDataSet parent) {
		this.parent = parent;
	}

	public JoinDataSet getParent() {
		return parent;
	}

	public List<FWRFilter> getFwrFilters() {
		return fwrFilters;
	}

	public void setFwrFilters(List<FWRFilter> filters) {
		this.fwrFilters = filters;
	}

	public List<FwrPrompt> getPrompts() {
		return prompts;
	}

	public void setPrompts(List<FwrPrompt> prompts) {
		this.prompts = prompts;
	}
	
	public void addFwrFilter(FWRFilter filter) {
		fwrFilters.add(filter);
	}
	
	public void addPrompt(FwrPrompt prompt) {
		prompts.add(prompt);
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public List<String> getFilters() {
		return filters;
	}
	
	public void addFilter(String filter) {
		filters.add(filter);
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Column> getColumns() {
		return columns;
	}
	
	public void addColumn(Column col) {
		columns.add(col);
	}

	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}
	
	public void setPreview(String isPreview) {
		if(isPreview.equalsIgnoreCase("true")) {
			this.isPreview = true;
		}
		else {
			this.isPreview = false;
		}
	}

	public boolean isPreview() {
		return isPreview;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public List<FwrRelationStrategy> getRelationStrategies() {
		return relationStrategies;
	}
	
	public void addRelationStrategy(FwrRelationStrategy relation) {
		if(relationStrategies == null) {
			this.relationStrategies = new ArrayList<FwrRelationStrategy>();
		}
		this.relationStrategies.add(relation);
	}
}
