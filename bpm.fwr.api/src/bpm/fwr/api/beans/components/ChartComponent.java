package bpm.fwr.api.beans.components;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;

public class ChartComponent extends ReportComponent implements IChart {
	
	private Column groupColumn;
	private List<Column> detailColumns;
	private ChartTypes chartType;
	private String chartOperation;
	private String chartTitle;
	
	private String groupName = "";
	private List<String> detailsName = new ArrayList<String>();
	
	private String language;
	
	private int height = 300;
	private int width = 600;
	
	public ChartComponent() {
		super();
		detailColumns = new ArrayList<Column>();
	}

	public ChartComponent(String chartTitle, Column groupColumn, List<Column> detailColumns, ChartTypes chartType, String chartOperation) {
		super();
		this.groupColumn = groupColumn;
		this.detailColumns = detailColumns;
		this.chartType = chartType;
		this.chartOperation = chartOperation;
		this.chartTitle = chartTitle;
	}

	@Override
	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public void setGroupName(String groupColumn) {
		this.groupName = groupColumn;
	}
	

	public ChartTypes getChartType() {
		return chartType;
	}

	public void setChartType(ChartTypes chartType) {
		this.chartType = chartType;
	}

	public String getChartOperation() {
		return chartOperation;
	}

	public void setChartOperation(String chartOperation) {
		this.chartOperation = chartOperation;
	}
	
	public void addDetailColumn(Column col) {
		if(detailColumns == null) {
			detailColumns = new ArrayList<Column>();
		}
		detailColumns.add(col);
	}
	public void addDetailName(String col) {
		detailsName.add(col);
	}

	public Column getColumnGroup() {
		return groupColumn;
	}

	public void setColumnGroup(Column groupColumn) {
		this.groupColumn = groupColumn;
	}

	public List<Column> getColumnDetails() {
		return detailColumns;
	}

	public void setColumnDetails(List<Column> detailColumns) {
		this.detailColumns = detailColumns;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<String> getDetailsName() {
		return detailsName;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}
	
	public void setHeight(String height) {
		this.height = Integer.parseInt(height);
	}
	public void setWidth(String width) {
		this.width = Integer.parseInt(width);
	}

	@Override
	public void setChartType(ChartType chartType) {
		setChartType(chartType.getType());
	}
}
