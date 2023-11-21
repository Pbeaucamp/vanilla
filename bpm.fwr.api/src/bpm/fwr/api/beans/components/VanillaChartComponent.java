package bpm.fwr.api.beans.components;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;

/**
 * 
 * This class represent an VanillaChart Components
 * 
 * @author svi
 *
 */
public class VanillaChartComponent extends ReportComponent implements IChart {
	private String id;
	private String name;
	private String customProperties;
	private String chartDataXml;
	private List<Column> columnDetails;
	private Column columnGroup;
	
	private ChartType chartType;
	private OptionsFusionChart options;
	
	public VanillaChartComponent(){ }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setChartDataXml(String chartDataXml) {
		this.chartDataXml = chartDataXml;
	}

	public String getChartDataXml() {
		return chartDataXml;
	}

	public void setCustomProperties(String customProperties) {
		this.customProperties = customProperties;
	}

	public String getCustomProperties() {
		return customProperties;
	}

	public void setColumnDetails(List<Column> columnDetails) {
		this.columnDetails = columnDetails;
	}

	public List<Column> getColumnDetails() {
		return columnDetails;
	}
	
	public void addColumnDetail(Column column){
		if(columnDetails == null){
			this.columnDetails = new ArrayList<Column>();
		}
		this.columnDetails.add(column);
	}

	public void setColumnGroup(Column columnGroup) {
		this.columnGroup = columnGroup;
	}

	public Column getColumnGroup() {
		return columnGroup;
	}

	public void setOptions(OptionsFusionChart options) {
		this.options = options;
	}

	public OptionsFusionChart getOptions() {
		return options;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public ChartType getChartType() {
		return chartType;
	}

	@Override
	public String getChartTitle() {
		return options != null ? (options.getChartTitle() != null ? options.getChartTitle() : "") : "";
	}
}
