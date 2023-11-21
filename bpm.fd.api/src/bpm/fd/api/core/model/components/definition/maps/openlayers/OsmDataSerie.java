package bpm.fd.api.core.model.components.definition.maps.openlayers;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.internal.ILabelable;

public abstract class OsmDataSerie implements ILabelable {

	public static final String POLYGON = "Polygon";
	public static final String MARKER = "Marker";
	public static final String LINE = "Line";

	protected String name;
	protected String type = POLYGON;
	
	protected boolean hasParent;
	protected String parentSerie = "";
	protected Integer parentIdFieldIndex;
	
	protected String minColor = "FF0000";
	protected String maxColor = "00FF00";

	private boolean drillable;
	
	public String getName() {
		return name.replace(" ", "_");
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("serie");
		e.addElement("name").setText(name);
		e.addElement("type").setText(type);
		
		e.addElement("hasParent").setText(String.valueOf(isHasParent()));
		if(hasParent) {
			e.addElement("parentName").setText(getParentSerie());
			e.addElement("parentIdFieldIndex").setText(String.valueOf(getParentIdFieldIndex()));
		}
		
		Element colorRanges = e.addElement("colorRanges");
		try {
			
			colorRanges.addElement("minColor").setText(minColor);
			colorRanges.addElement("maxColor").setText(maxColor);
			
		} catch (Exception e1) {
		}
		
		e.addElement("drillable").setText(String.valueOf(isDrillable()));
		
		return e;
	}

	public boolean isHasParent() {
		return hasParent;
	}

	public void setHasParent(boolean hasParent) {
		this.hasParent = hasParent;
	}

	public Integer getParentIdFieldIndex() {
		return parentIdFieldIndex;
	}

	public void setParentIdFieldIndex(Integer parentIdFieldIndex) {
		this.parentIdFieldIndex = parentIdFieldIndex;
	}

	public String getParentSerie() {
		return parentSerie;
	}

	public void setParentSerie(String parentSerie) {
		this.parentSerie = parentSerie;
	}

	public String getLabel() {
		return name;
	}

	public String getMinColor() {
		return minColor;
	}

	public void setMinColor(String minColor) {
		this.minColor = minColor;
	}

	public String getMaxColor() {
		return maxColor;
	}

	public void setMaxColor(String maxColor) {
		this.maxColor = maxColor;
	}

	public boolean isDrillable() {
		return drillable;
	}

	public void setDrillable(boolean drillable) {
		this.drillable = drillable;
	}
}
