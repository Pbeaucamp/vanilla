package bpm.fwr.api.beans.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bpm.fwr.api.beans.Agregate;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.api.beans.template.DefaultTemplate;
import bpm.fwr.api.beans.template.Style;

public class GridComponent extends ReportComponent {
	private List<Column> columns;
	private List<Column> groups;
	private List<Agregate> agregates;

	private boolean classic = true;
	private boolean showHeader = true;
	private boolean automaticGroupList;
	
	//Component style
	private Style dataCellsStyle;
	private String oddRowsBackgroundColor;
	private Style headerCellsStyle;
	
	private List<String> filters = new ArrayList<String>();
	private List<FWRFilter> fwrFilters = new ArrayList<FWRFilter>();
	private List<IResource> prompts = new ArrayList<IResource>();
	private List<FwrRelationStrategy> relations = new ArrayList<FwrRelationStrategy>();

	public GridComponent() {
		super();
		columns = new ArrayList<Column>();
		groups = new ArrayList<Column>();
		agregates = new ArrayList<Agregate>();
		
		//make a default style
		DefaultTemplate template = new DefaultTemplate();
		dataCellsStyle = template.getDataStyle();
//		oddRowsBackgroundColor = template.getOddRowsBackgroundColor();
		headerCellsStyle = template.getHeaderStyle();
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Column> getGroups() {
		return groups;
	}

	public void setGroups(List<Column> groups) {
		this.groups = groups;
	}

	public void setAgregates(List<Agregate> agregates) {
		this.agregates = agregates;
	}

	public void addColumn(Column c) {
		this.columns.add(c);
	}

	public void addGroup(Column g) {
		this.groups.add(g);
	}

	public void addAgregate(Agregate a) {
		this.agregates.add(a);
	}

	public void generateAgregates(HashMap ag) {
		Collection v = ag.keySet();
		for (Iterator it = v.iterator(); it.hasNext();) {
			String oncolumn = (String) it.next();
			String formula = (String) ag.get(oncolumn);
			Agregate agregate = new Agregate(oncolumn, formula);
			this.agregates.add(agregate);
		}
	}

	public List<Agregate> getAgregates() {
		return agregates;
	}

	public boolean isClassic() {
		return classic;
	}

	public void setClassic(boolean classic) {
		this.classic = classic;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public void setShowHeader(String showHeader) {
		if (showHeader.equalsIgnoreCase("true")) {
			this.showHeader = true;
		}
		else {
			this.showHeader = false;
		}
	}

	public boolean showHeader() {
		return showHeader;
	}

	public Style getDataCellsStyle() {
		return dataCellsStyle;
	}

	public void setDataCellsStyle(Style dataCellsStyle) {
		this.dataCellsStyle = dataCellsStyle;
	}

	public String getOddRowsBackgroundColor() {
		return oddRowsBackgroundColor;
	}

	public void setOddRowsBackgroundColor(String oddRowsBackgroundColor) {
		this.oddRowsBackgroundColor = oddRowsBackgroundColor;
	}

	public Style getHeaderCellsStyle() {
		return headerCellsStyle;
	}

	public void setHeaderCellsStyle(Style headerCellsStyle) {
		this.headerCellsStyle = headerCellsStyle;
	}

	public void setFwrFilters(List<FWRFilter> fwrFilters) {
		this.fwrFilters = fwrFilters;
	}

	public List<FWRFilter> getFwrFilters() {
		return fwrFilters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setPrompts(List<IResource> prompts) {
		this.prompts = prompts;
	}

	public List<IResource> getPrompts() {
		return prompts;
	}

	public void setRelations(List<FwrRelationStrategy> relations) {
		this.relations = relations;
	}

	public List<FwrRelationStrategy> getRelations() {
		return relations;
	}

	public boolean isAutomaticGroupingList() {
		return automaticGroupList;
	}

	public void setAutomaticGroupingList(boolean automaticGroupingList) {
		this.automaticGroupList = automaticGroupingList;
	}
}
