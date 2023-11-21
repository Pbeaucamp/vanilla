package bpm.fd.api.core.model.components.definition.gauge;

import java.awt.Color;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class GaugeOptions implements IComponentOptions{
	public static final int KEY_SHOW_VALUES = 0;
	public static final int KEY_BACKGROUND_COLOR = 1;
//	private static final int KEY_SHOW_BORDER = 2;
//	private static final int KEY_BORDER_COLOR = 3;
//	private static final int KEY_BORDER_THICKNESS = 4;
	public static final int KEY_BG_ALPHA = 2;
//	private static final int KEY_BG_SWF_ALPHA = 3;
	
	public static final int KEY_COLOR_BAD = 3;
	public static final int KEY_COLOR_MEDIUM = 4;
	public static final int KEY_COLOR_GOOD = 5;
	public static final int KEY_INNER_RADIUS = 6;
	public static final int KEY_OUTER_RADIUS = 7;
	public static final int KEY_START_ANGLE = 8;
	public static final int KEY_STOP_ANGLE = 9;
	
	public static final String[] i18nKeys = new String[]{};
	public static final String[] standardKeys = new String[] {
		"showValues", "bgColor",
		/*"showBorder", "borderColor", "borderThickness", */"bgAlpha", /*"bgSWFAlpha",*/
		"colorBadValue", "colorMediumValue", "colorGoodValue", "innerRadius", "outerRadius",
		"startAngle", "stopAngle"};
	
	private int bgAlpha = 0;
//	private int bgSWFAlpha = 0;
	

	private boolean showValues = true;
	private Color  bgColor  = null;
	
//	private boolean showBorder = true;
//	private Color borderColor;
//	private int borderThickness = 1;
	private Color colorBadValue, colorMediumValue, colorGoodValue;
	
	private int innerRadius = 70;
	private int outerRadius = 100;
	private int startAngle = 180;
	private int stopAngle = 0;
	private boolean bulb = false;
	
	/**
	 * @return the innerRadius
	 */
	public int getInnerRadius() {
		return innerRadius;
	}
	/**
	 * @return the startAngle
	 */
	public int getStartAngle() {
		return startAngle;
	}
	/**
	 * @param startAngle the startAngle to set
	 */
	public void setStartAngle(int startAngle) {
		this.startAngle = startAngle;
	}
	/**
	 * @return the stopAngle
	 */
	public int getStopAngle() {
		return stopAngle;
	}
	/**
	 * @param stopAngle the stopAngle to set
	 */
	public void setStopAngle(int stopAngle) {
		this.stopAngle = stopAngle;
	}
	/**
	 * @param innerRadius the innerRadius to set
	 */
	public void setInnerRadius(int innerRadius) {
		this.innerRadius = innerRadius;
	}
	/**
	 * @return the outerRadius
	 */
	public int getOuterRadius() {
		return outerRadius;
	}
	/**
	 * @param outerRadius the outerRadius to set
	 */
	public void setOuterRadius(int outerRadius) {
		this.outerRadius = outerRadius;
	}
	public int getBgAlpha() {
		return bgAlpha;
	}
	public void setBgAlpha(int bgAlpha) {
		this.bgAlpha = bgAlpha;
	}
//	public int getBgSWFAlpha() {
//		return bgSWFAlpha;
//	}
//	public void setBgSWFAlpha(int bgSWFAlpha) {
//		this.bgSWFAlpha = bgSWFAlpha;
//	}
//	/**
//	 * @return the showBorder
//	 */
//	public boolean isShowBorder() {
//		return showBorder;
//	}
//	/**
//	 * @param showBorder the showBorder to set
//	 */
//	public void setShowBorder(boolean showBorder) {
//		this.showBorder = showBorder;
//	}
//	/**
//	 * @return the borderColor
//	 */
//	public Color getBorderColor() {
//		return borderColor;
//	}
//	/**
//	 * @param borderColor the borderColor to set
//	 */
//	public void setBorderColor(Color borderColor) {
//		this.borderColor = borderColor;
//	}
	/**
	 * @return the colorBadValue
	 */
	public Color getColorBadValue() {
		return colorBadValue;
	}
	/**
	 * @param colorBadValue the colorBadValue to set
	 */
	public void setColorBadValue(Color colorBadValue) {
		this.colorBadValue = colorBadValue;
	}
	/**
	 * @return the colorMediumValue
	 */
	public Color getColorMediumValue() {
		return colorMediumValue;
	}
	/**
	 * @param colorMediumValue the colorMediumValue to set
	 */
	public void setColorMediumValue(Color colorMediumValue) {
		this.colorMediumValue = colorMediumValue;
	}
	/**
	 * @return the colorGoodValue
	 */
	public Color getColorGoodValue() {
		return colorGoodValue;
	}
	/**
	 * @param colorGoodValue the colorGoodValue to set
	 */
	public void setColorGoodValue(Color colorGoodValue) {
		this.colorGoodValue = colorGoodValue;
	}
//	/**
//	 * @return the borderThickness
//	 */
//	public int getBorderThickness() {
//		return borderThickness;
//	}
//	/**
//	 * @param borderThickness the borderThickness to set
//	 */
//	public void setBorderThickness(int borderThickness) {
//		this.borderThickness = borderThickness;
//	}
//	
	/**
	 * @return the backgroundColor
	 */
	public Color getBgColor () {
		return  bgColor ;
	}
	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBgColor (Color backgroundColor) {
		this. bgColor  = backgroundColor;
	}
	
	/**
	 * @return the showValues
	 */
	public boolean isShowValues() {
		return showValues;
	}
	/**
	 * @param showValues the showValues to set
	 */
	public void setShowValues(boolean showValues) {
		this.showValues = showValues;
	}
	public Element getElement() {
		Element e = DocumentHelper.createElement("gaugeOptions");

		e.addElement("backgroundAlpha").setText(getBgAlpha()+ "");
		
		e.addElement("innerRadius").setText(getInnerRadius() + "");
		e.addElement("outerRadius").setText(getOuterRadius() + "");
		e.addElement("startAngle").setText(getStartAngle() + "");
		e.addElement("stopAngle").setText(getStopAngle() + "");
		e.addElement("bulb").setText(bulb+"");
		
		if (getBgColor() != null){
			Element bckCol = e.addElement("backgroundColor");
			bckCol.addAttribute("red", getBgColor().getRed() + "");
			bckCol.addAttribute("green", getBgColor().getGreen() + "");
			bckCol.addAttribute("blue", getBgColor().getBlue() + "");
		}
		
		if (getColorBadValue() != null){
			Element bckCol = e.addElement("colorBadValue");
			bckCol.addAttribute("red", getColorBadValue().getRed() + "");
			bckCol.addAttribute("green", getColorBadValue().getGreen() + "");
			bckCol.addAttribute("blue", getColorBadValue().getBlue() + "");
		}
		if (getColorMediumValue() != null){
			Element bckCol = e.addElement("colorMediumValue");
			bckCol.addAttribute("red", getColorMediumValue().getRed() + "");
			bckCol.addAttribute("green", getColorMediumValue().getGreen() + "");
			bckCol.addAttribute("blue", getColorMediumValue().getBlue() + "");
		}
		if (getColorMediumValue() != null){
			Element bckCol = e.addElement("colorGoodValue");
			bckCol.addAttribute("red", getColorGoodValue().getRed() + "");
			bckCol.addAttribute("green", getColorGoodValue().getGreen() + "");
			bckCol.addAttribute("blue", getColorGoodValue().getBlue() + "");
		}
		
		return e;
	}
	
	public IComponentOptions getAdapter(Object type) {
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
		if (standardKeys[KEY_SHOW_VALUES].equals(key)){
			return isShowValues()? "1" : "0";
		}
		else if (standardKeys[KEY_BACKGROUND_COLOR].equals(key)){
			if (getBgColor() == null){
				return "FFFFFF";
			}
			return Integer.toHexString(getBgColor().getRGB());
			
		}
//		else if (standardKeys[KEY_BORDER_COLOR].equals(key)){
//			if (getBorderColor() == null){
//				return "FFFFFF";
//			}
//			return Integer.toHexString(getBorderColor().getRGB());
//
//		}
		else if (standardKeys[KEY_COLOR_BAD].equals(key)){
			if (getColorBadValue() == null){
				return "FF654F";
			}
			String res =  Integer.toHexString(getColorBadValue().getRGB());
			if(res.length() > 6) {
				return res.substring(res.length() - 6, res.length());
			}

		}
		else if (standardKeys[KEY_COLOR_MEDIUM].equals(key)){
			if (getColorMediumValue() == null){
				return "F6BD0F";
			}
			String res =  Integer.toHexString(getColorMediumValue().getRGB());
			if(res.length() > 6) {
				return res.substring(res.length() - 6, res.length());
			}

		}
		else if (standardKeys[KEY_COLOR_GOOD].equals(key)){
			if (getColorGoodValue() == null){
				return "8BBA00";
			}
			String res =  Integer.toHexString(getColorGoodValue().getRGB());
			if(res.length() > 6) {
				return res.substring(res.length() - 6, res.length());
			}

		}
//		else if (standardKeys[KEY_BORDER_THICKNESS].equals(key)){
//			return getBorderThickness() + "";
//		}
//		else if (standardKeys[KEY_SHOW_BORDER].equals(key)){
//			return isShowBorder()? "1" : "0";
//		}
//		else if (standardKeys[KEY_BG_ALPHA].equals(key)){
//			return getBgAlpha() +  "";
//		}
//		else if (standardKeys[KEY_BG_SWF_ALPHA].equals(key)){
//			return getBgSWFAlpha() +  "";
//		}
		else if (standardKeys[KEY_INNER_RADIUS].equals(key)){
			return getInnerRadius() +  "";
		}
		else if (standardKeys[KEY_OUTER_RADIUS].equals(key)){
			return getOuterRadius() +  "";
		}
		else if (standardKeys[KEY_START_ANGLE].equals(key)){
			return getStartAngle() +  "";
		}
		else if (standardKeys[KEY_STOP_ANGLE].equals(key)){
			return getStopAngle() +  "";
		}
			
		return null;
	}
	@Override
	public IComponentOptions copy() {
		GaugeOptions copy = new GaugeOptions();
		
		copy.setBgAlpha(bgAlpha);
		copy.setBgColor(bgColor);
		copy.setColorBadValue(colorBadValue);
		copy.setColorGoodValue(colorGoodValue);
		copy.setColorMediumValue(colorMediumValue);
		copy.setInnerRadius(innerRadius);
		copy.setOuterRadius(outerRadius);
		copy.setShowValues(showValues);
		copy.setStartAngle(startAngle);
		copy.setStopAngle(stopAngle);
		copy.setBulb(bulb);
		
		return copy;
	}
	public boolean isBulb() {
		return bulb;
	}
	
	public void setBulb(boolean bulb) {
		this.bulb = bulb;
	}
} 
