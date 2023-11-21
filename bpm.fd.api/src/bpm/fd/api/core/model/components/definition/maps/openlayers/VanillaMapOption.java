package bpm.fd.api.core.model.components.definition.maps.openlayers;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class VanillaMapOption implements IComponentOptions {
	public static final String ORIENTATION_HORIZONTAL = "Horizontal";
	public static final String ORIENTATION_VERTICAL = "Vertical";
	
	public static final String LAYOUT_TOP = "Top";
	public static final String LAYOUT_BOTTOM = "Bottom";
	public static final String LAYOUT_LEFT = "Left";
	public static final String LAYOUT_RIGHT = "Right";
	
	public static List<String> ORIENTATIONS;
	public static List<String> LAYOUTS;
	
	static {
		ORIENTATIONS = new ArrayList<String>();
		LAYOUTS = new ArrayList<String>();
		
		ORIENTATIONS.add(ORIENTATION_HORIZONTAL);
		ORIENTATIONS.add(ORIENTATION_VERTICAL);
		
		LAYOUTS.add(LAYOUT_TOP);
		LAYOUTS.add(LAYOUT_BOTTOM);
		LAYOUTS.add(LAYOUT_LEFT);
		LAYOUTS.add(LAYOUT_RIGHT);
	}
	
	public static final int SHOW_LEGEND = 0;
	public static final int LEGEND_ORIENTATION = 1;
	public static final int LEGEND_LAYOUT = 2;
	public static final int NUMBER_FORMAT = 3;
	public static final int SHOW_BASE_LAYER = 4;
	
	public static final String[] standardKeys = {"Showlegend", "legendOrientation", "legendLayout", "numberFormat", "showBaseLayer"};
	public static final String[] i18nKeys = {};
	
	private boolean showLegend;
	private boolean showBaseLayer = true;
	
	private String legendOrientation = ORIENTATION_HORIZONTAL;
	private String legendLayout = LAYOUT_BOTTOM;
	private String numberFormat = "";
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("class", OsmMapOptions.class.getName());
		
		for(int i = 0; i < standardKeys.length; i++){
			try {
				e.addElement(standardKeys[i]).setText(getValue(standardKeys[i]));
			} catch (Exception e1) {
				System.out.println("No value for " + standardKeys[i]);
			}
		}
		return e;
	}

	@Override
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	@Override
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	@Override
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	@Override
	public String getValue(String key) {
		int index = -1;
		
		for(int i = 0; i < standardKeys.length; i ++){
			if (standardKeys[i].equals(key)){
				index = i;
				break;
			}
		}
		switch (index) {
		case SHOW_LEGEND:
			return showLegend + "";
		case LEGEND_ORIENTATION:
			return legendOrientation;
		case LEGEND_LAYOUT:
			return legendLayout;
		case NUMBER_FORMAT:
			return numberFormat;
		case SHOW_BASE_LAYER:
			return showBaseLayer+"";
		default:
			return null;
		}
	}

	@Override
	public String getDefaultLabelValue(String key) {
		return key;
	}

	@Override
	public IComponentOptions copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public String getLegendOrientation() {
		return legendOrientation;
	}

	public void setLegendOrientation(String legendOrientation) {
		this.legendOrientation = legendOrientation;
	}

	public String getLegendLayout() {
		return legendLayout;
	}

	public void setLegendLayout(String legendLayout) {
		this.legendLayout = legendLayout;
	}

	public String getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}

	public boolean isShowBaseLayer() {
		return showBaseLayer;
	}

	public void setShowBaseLayer(boolean showBaseLayer) {
		this.showBaseLayer = showBaseLayer;
	}
}
