package bpm.fwr.shared.models.metadata;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;

import com.google.gwt.user.client.rpc.IsSerializable;


public class FwrSavedQuery implements IsSerializable {
	
	private String name;
	private String description;
	
	private List<FWRFilter> filters;
	private List<FwrPrompt> prompts;
	private List<Column> columns;
	
	public FwrSavedQuery() {
		
	}
	
	public FwrSavedQuery(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public List<FWRFilter> getFilters() {
		return filters;
	}

	public void addFilter(FWRFilter filter) {
		if(filters == null) {
			this.filters = new ArrayList<FWRFilter>();
		}
		this.filters.add(filter);
	}

	public List<FwrPrompt> getPrompts() {
		return prompts;
	}

	public void addPrompt(FwrPrompt prompt) {
		if(prompts == null) {
			this.prompts = new ArrayList<FwrPrompt>();
		}
		this.prompts.add(prompt);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void addColumn(Column column) {
		if(columns == null) {
			this.columns = new ArrayList<Column>();
		}
		this.columns.add(column);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
