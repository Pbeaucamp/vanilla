package bpm.fwr.api.beans.components;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

/**
 * 
 * This class represent an VanillaMap Components
 * 
 * @author svi
 *
 */
public class VanillaMapComponent extends ReportComponent{

	private String id;
	private String name;
	private String swfUrl;
	private String mapDataXml;
	private String customProperties;
	private Column columnId;
	private Column columnValues;
	private int height;
	private int width;
	private String unit;
	
	private OptionsFusionMap options;
	private List<ColorRange> colors;
	
	public VanillaMapComponent(){ }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSwfUrl(String swfUrl) {
		this.swfUrl = swfUrl;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setHeight(String height){
		this.height = Integer.parseInt(height);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setWidth(String width){
		this.width = Integer.parseInt(width);
	}

	public String getSwfUrl() {
		return swfUrl;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}	
	
	public void setColumnId(Column columnId) {
		this.columnId = columnId;
	}

	public Column getColumnId() {
		return columnId;
	}

	public void setColumnValues(Column columnValues) {
		this.columnValues = columnValues;
	}

	public Column getColumnValues() {
		return columnValues;
	}

	public void setMapDataXml(String mapDataXml) {
		this.mapDataXml = mapDataXml;
	}

	public String getMapDataXml() {
		return mapDataXml;
	}

	public void setCustomProperties(String customProperties) {
		this.customProperties = customProperties;
	}

	public String getCustomProperties() {
		return customProperties;
	}

	public void setOptions(OptionsFusionMap options) {
		this.options = options;
	}

	public OptionsFusionMap getOptions() {
		return options;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String getUnit() {
		return unit;
	}

	public void setColors(List<ColorRange> colors) {
		this.colors = colors;
	}

	public List<ColorRange> getColors() {
		return colors;
	}
}
