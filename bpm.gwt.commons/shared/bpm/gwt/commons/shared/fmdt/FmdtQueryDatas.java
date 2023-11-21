package bpm.gwt.commons.shared.fmdt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtTableStruct;

public class FmdtQueryDatas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<FmdtTableStruct> tables = new ArrayList<FmdtTableStruct>();
	private List<FmdtFilter> filters = new ArrayList<FmdtFilter>();
	//private List<FmdtCondition> conditions=null;
	
	public FmdtQueryDatas(){}
	
	public FmdtQueryDatas(List<FmdtTableStruct> tables, List<FmdtFilter> filters) {
		super();
		this.tables = tables;
		this.filters = filters;
		//this.conditions = conditions;
	}
	public List<FmdtTableStruct> getTables() {
		return tables;
	}
	public void setTables(List<FmdtTableStruct> tables) {
		this.tables = tables;
	}
	public List<FmdtFilter> getFilters() {
		return filters;
	}
	public void setFilters(List<FmdtFilter> filters) {
		this.filters = filters;
	}
	/*
	public List<FmdtCondition> getConditions() {
		return conditions;
	}
	public void setConditions(List<FmdtCondition> conditions) {
		this.conditions = conditions;
	}
	*/
	
}
