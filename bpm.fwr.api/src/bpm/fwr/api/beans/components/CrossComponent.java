package bpm.fwr.api.beans.components;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.IResource;

public class CrossComponent extends ReportComponent {

	private List<Column> crossCells = new ArrayList<Column>();
	private List<Column> crossCols = new ArrayList<Column>();
	private List<Column> crossRows = new ArrayList<Column>();

	private String XStyle = "COLOR_SCHEMA_BLUE";
	
	private List<String> filters = new ArrayList<String>();
	private List<FWRFilter> fwrFilters = new ArrayList<FWRFilter>();
	private List<IResource> prompts = new ArrayList<IResource>();
	private List<FwrRelationStrategy> relations = new ArrayList<FwrRelationStrategy>();

	public CrossComponent() {
		super();
	}

	public List<Column> getCrossCols() {
		return crossCols;
	}

	public void setCrossCols(List<Column> crossCols) {
		this.crossCols = crossCols;
	}

	public List<Column> getCrossRows() {
		return crossRows;
	}

	public void setCrossRows(List<Column> crossRows) {
		this.crossRows = crossRows;
	}

	public void addRow(Column c) {
		if (this.crossRows == null) {
			this.crossRows = new ArrayList<Column>();
		}
		this.crossRows.add(c);
	}

	public void addCol(Column c) {
		if (this.crossCols == null) {
			this.crossCols = new ArrayList<Column>();
		}
		this.crossCols.add(c);
	}

	public String getXStyle() {
		return XStyle;
	}

	public void setXStyle(String style) {
		XStyle = style;
	}

	public List<Column> getCrossCells() {
		return crossCells;
	}

	public void setCrossCells(List<Column> crossCells) {
		this.crossCells = crossCells;
	}

	public void addCell(Column c) {
		if (this.crossCells == null) {
			this.crossCells = new ArrayList<Column>();
		}
		this.crossCells.add(c);
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
}
