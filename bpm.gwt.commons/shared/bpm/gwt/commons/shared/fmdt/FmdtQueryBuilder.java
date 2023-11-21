package bpm.gwt.commons.shared.fmdt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.fmdt.FmdtAggregate;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFormula;

public class FmdtQueryBuilder implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private List<FmdtColumn> columns= new ArrayList<FmdtColumn>();
	private List<FmdtAggregate> aggregates = new ArrayList<FmdtAggregate>();
	private List<FmdtFormula> formulas= new ArrayList<FmdtFormula>();
	private List<FmdtFilter> filters  = new ArrayList<FmdtFilter>();
//	private List<FmdtFilter> sqlFilters = new ArrayList<FmdtFilter>();
//	private List<FmdtFilter> complexFilters = new ArrayList<FmdtFilter>();
	private List<FmdtFilter> promptFilters = new ArrayList<FmdtFilter>();
//	private List<FmdtCondition> conditions = new ArrayList<FmdtCondition>();
	private List<FmdtData> listColumns = new ArrayList<FmdtData>();
	private List<FmdtFilter> listFilters= new ArrayList<FmdtFilter>();
	
	private String queryXml;
	
	private boolean distinct =false;
	private boolean limit = false;
	private int nbLimit =0;
	
	
	public FmdtQueryBuilder(){}
	
	public FmdtQueryBuilder(String name){
		this.name=name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<FmdtColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<FmdtColumn> columns) {
		this.columns = columns;
	}
	public List<FmdtAggregate> getAggregates() {
		return aggregates;
	}
	public void setAggregates(List<FmdtAggregate> aggregates) {
		this.aggregates = aggregates;
	}
	
	public List<FmdtFormula> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<FmdtFormula> formulas) {
		this.formulas = formulas;
	}

	public List<FmdtFilter> getFilters() {
		return filters;
	}
	public void setFilters(List<FmdtFilter> filters) {
		this.filters = filters;
	}
	/*
	public List<FmdtFilter> getSqlFilters() {
		return sqlFilters;
	}
	public void setSqlFilters(List<FmdtFilter> sqlFilters) {
		this.sqlFilters = sqlFilters;
	}
	public List<FmdtFilter> getComplexFilters() {
		return complexFilters;
	}
	public void setComplexFilters(List<FmdtFilter> complexFilters) {
		this.complexFilters = complexFilters;
	}
	*/
	public List<FmdtFilter> getPromptFilters() {
		return promptFilters;
	}
	public void setPromptFilters(List<FmdtFilter> promptFilters) {
		this.promptFilters = promptFilters;
	}
	/*
	public List<FmdtCondition> getConditions() {
		return conditions;
	}
	public void setConditions(List<FmdtCondition> conditions) {
		this.conditions = conditions;
	}
	*/
	public String getQueryXml() {
		return queryXml;
	}

	public void setQueryXml(String queryXml) {
		this.queryXml = queryXml;
	}

	public List<FmdtData> getListColumns() {
		return listColumns;
	}

	public void setListColumns(List<FmdtData> listColumns) {
		this.listColumns = listColumns;
	}

	public List<FmdtFilter> getListFilters() {
		return listFilters;
	}

	public void setListFilters(List<FmdtFilter> listFilters) {
		this.listFilters = listFilters;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public boolean isLimit() {
		return limit;
	}

	public void setLimit(boolean limit) {
		this.limit = limit;
	}

	public int getNbLimit() {
		return nbLimit;
	}

	public void setNbLimit(int nbLimit) {
		this.nbLimit = nbLimit;
	}
	
	
	
}
