package bpm.fd.api.core.model.components.definition.chart.fusion.options;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;

public class GenericNonPieOptions implements IComponentOptions{

	private static final int KEY_ROTATE_LABELS = 0;
	private static final int KEY_SLANT_LABELS = 1;
	private static final int KEY_ROTATE_VALUE = 2;
	private static final int KEY_VALUES_INSIDE = 3;
	private static final int KEY_ROTATE_Y_AXIS_NAME = 4;
	public static final int KEY_PRINCIPAL_AXIS_NAME = 5;
	public static final int KEY_SECONDARY_AXIS_NAME = 6;
	public static final int KEY_X_AXIS_NAME = 7;
	
	private static final String[] i18nKeys = new String[]{};
	private static final String[] standardKeys = new String[] {
		"rotateLabels", "slantLabels", "rotateValues",
		"placeValuesInside", "rotateYAxisName",  "PYAxisName", "SYAxisName", "xAxisName"
		};
	
	private boolean rotateLabels = false;
	private boolean slantLabels = false;
	private boolean rotateValues = false;
	private boolean placeValuesInside = false;
	private boolean rotateYAxisName = false;
	private String PYAxisName = "", SYAxisName = "", xAxisName = "";
	
	
	/**
	 * @return the rotateLabels
	 */
	public boolean isRotateLabels() {
		return rotateLabels;
	}

	/**
	 * @param rotateLabels the rotateLabels to set
	 */
	public void setRotateLabels(boolean rotateLabels) {
		this.rotateLabels = rotateLabels;
	}

	/**
	 * @return the slantLabels
	 */
	public boolean isSlantLabels() {
		return slantLabels;
	}

	/**
	 * @param slantLabels the slantLabels to set
	 */
	public void setSlantLabels(boolean slantLabels) {
		this.slantLabels = slantLabels;
	}

	/**
	 * @return the rotateValues
	 */
	public boolean isRotateValues() {
		return rotateValues;
	}

	/**
	 * @param rotateValues the rotateValues to set
	 */
	public void setRotateValues(boolean rotateValues) {
		this.rotateValues = rotateValues;
	}

	/**
	 * @return the placeValuesInside
	 */
	public boolean isPlaceValuesInside() {
		return placeValuesInside;
	}

	/**
	 * @param placeValuesInside the placeValuesInside to set
	 */
	public void setPlaceValuesInside(boolean placeValuesInside) {
		this.placeValuesInside = placeValuesInside;
	}

	/**
	 * @return the rotateYAxisName
	 */
	public boolean isRotateYAxisName() {
		return rotateYAxisName;
	}

	/**
	 * @param rotateYAxisName the rotateYAxisName to set
	 */
	public void setRotateYAxisName(boolean rotateYAxisName) {
		this.rotateYAxisName = rotateYAxisName;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("genericNonPieOptions");
		
		for(String s : standardKeys){
			e.addAttribute(s, getValue(s));
		}
		return e;
	}
	
	/**
	 * @return the principalAxisName
	 */
	public String getPYAxisName() {
		if(PYAxisName == null) {
			PYAxisName = "";
		}
		return PYAxisName;
	}

	/**
	 * @param principalAxisName the principalAxisName to set
	 */
	public void setPYAxisName(String principalAxisName) {
		this.PYAxisName = principalAxisName;
	}

	/**
	 * @return the secondaryAxisName
	 */
	public String getSYAxisName() {
		if(SYAxisName == null) {
			SYAxisName = "";
		}
		return SYAxisName;
	}

	/**
	 * @param secondaryAxisName the secondaryAxisName to set
	 */
	public void setSYAxisName(String secondaryAxisName) {
		this.SYAxisName = secondaryAxisName;
	}
	
	public IComponentOptions getAdapter(Object type) {
		if (type instanceof ChartNature){
			ChartNature nature = (ChartNature)type;
			try{
				if (nature == ChartNature.getNature(ChartNature.PIE) || nature == ChartNature.getNature(ChartNature.PIE_3D)){
					return null;
				}
			}catch(Exception e){
				//TODO
			}
			
			
			
		}
		return this;
	}
	public String getDefaultLabelValue(String key) {
		
		return null;
	}
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}
	public String getValue(String key) {
		if (standardKeys[KEY_ROTATE_LABELS].equals(key)){
			return isRotateLabels()? "1" : "0";
		}
		else if (standardKeys[KEY_ROTATE_VALUE].equals(key)){
			return isRotateValues()? "1" : "0";
		}
		else if (standardKeys[KEY_ROTATE_Y_AXIS_NAME].equals(key)){
			return isRotateYAxisName()? "1" : "0";
			
		}
		else if (standardKeys[KEY_SLANT_LABELS].equals(key)){
			return isSlantLabels()? "1" : "0";
			
		}
		else if (standardKeys[KEY_VALUES_INSIDE].equals(key)){
			return isPlaceValuesInside()? "1" : "0";
			
		}
		
		else if (standardKeys[KEY_PRINCIPAL_AXIS_NAME].equals(key)){
			return getPYAxisName();
			
		}
		else if (standardKeys[KEY_SECONDARY_AXIS_NAME].equals(key)){
			return getSYAxisName();
			
		}
		else if (standardKeys[KEY_X_AXIS_NAME].equals(key)){
			return xAxisName;
			
		}
		
		return null;
	}

	@Override
	public IComponentOptions copy() {
		GenericNonPieOptions copy = new GenericNonPieOptions();
		
		copy.setPlaceValuesInside(placeValuesInside);
		copy.setRotateLabels(rotateLabels);
		copy.setRotateValues(rotateValues);
		copy.setRotateYAxisName(rotateYAxisName);
		copy.setSlantLabels(slantLabels);
		copy.setPYAxisName(PYAxisName);
		copy.setSYAxisName(SYAxisName);
		copy.setxAxisName(xAxisName);
		
		return copy;
	}

	public String getxAxisName() {
		if(xAxisName == null) {
			xAxisName = "";
		}
		return xAxisName;
	}

	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}
	
	
	
}
