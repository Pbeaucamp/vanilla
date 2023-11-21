package bpm.fwr.shared.models.metadata;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FwrBusinessPackage extends FwrBusinessObject implements IsSerializable {

	private List<FwrBusinessTable> businessTables;
	private List<FwrPrompt> prompts = new ArrayList<FwrPrompt>();
	private List<FWRFilter> filters = new ArrayList<FWRFilter>();
	private List<FwrSavedQuery> savedQueries;
	
	public FwrBusinessPackage(){
		super();
	}
	
	public FwrBusinessPackage(String n, String description){
		super(n, description);
	}
	
	public FwrBusinessPackage(String n, String description, List<FwrBusinessTable> l){
		super(n, description);
		this.businessTables = l;
	}
	
	public void setBusinessTables(List<FwrBusinessTable> businessTables) {
		this.businessTables = businessTables;
	}

	public List<FwrBusinessTable> getBusinessTables() {
		return businessTables;
	}

	public void setPrompts(List<FwrPrompt> prompts) {
		this.prompts = prompts;
	}

	public List<FwrPrompt> getPrompts() {
		return prompts;
	}
	
	public void addFwrPrompt(FwrPrompt gwtPrompt){
		this.prompts.add(gwtPrompt);
	}

	public void setFilters(List<FWRFilter> filters) {
		this.filters = filters;
	}

	public List<FWRFilter> getFilters() {
		return filters;
	}
	
	public void addFilter(FWRFilter filter){
		this.filters.add(filter);
	}

	public void setSavedQueries(List<FwrSavedQuery> savedQueries) {
		this.savedQueries = savedQueries;
	}

	public List<FwrSavedQuery> getSavedQueries() {
		return savedQueries;
	}

	public void addSavedQueries(FwrSavedQuery savedQuery) {
		if(savedQueries == null) {
			this.savedQueries = new ArrayList<FwrSavedQuery>();
		}
		this.savedQueries.add(savedQuery);
	}
}
