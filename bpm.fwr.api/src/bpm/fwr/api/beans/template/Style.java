package bpm.fwr.api.beans.template;

import java.io.Serializable;
import java.util.HashMap;

import bpm.fwr.api.beans.Constants.Colors;

public class Style implements Serializable {

	public static final String FONT_TYPE = "fontType";
	public static final String FONT_SIZE = "fontSize";
	public static final String TEXT_COLOR = "textColor";
	public static final String BACKGROUND_COLOR = "backgroundColor";

	private boolean isCustom = false;

	private HashMap<String, String> attributes;

	public Style() {
		attributes = new HashMap<String, String>();
	}

	public Style(boolean isCustom) {
		super();
		this.isCustom = isCustom;
		attributes = new HashMap<String, String>();
	}

	private void setAttribute(String attribute, String value) {
		attributes.put(attribute, value);
	}

	private String getAttribute(String attributeKey) {
		return attributes.get(attributeKey);
	}

	public String getBackgroundColor() {
		return getAttribute(BACKGROUND_COLOR);
	}

	public void setBackgroundColor(String backgroundColor) {
		setAttribute(BACKGROUND_COLOR, backgroundColor);
	}

	public String getFontSize() {
		return getAttribute(FONT_SIZE);
	}

	public void setFontSize(String fontSize) {
		setAttribute(FONT_SIZE, fontSize);
	}

	public String getFontType() {
		return getAttribute(FONT_TYPE);
	}

	public void setFontType(String fontType) {
		setAttribute(FONT_TYPE, fontType);
	}

	public String getTextColor() {
		return getAttribute(TEXT_COLOR);
	}

	public void setTextColor(String textColor) {
		setAttribute(TEXT_COLOR, textColor);
	}

	public void setCustomTextColor(String name, String r, String g, String b) {
		String c = r+","+g+","+b;
		Colors.colors.put(name, c);
		setAttribute(TEXT_COLOR, name);
		isCustom = true;
	}

	public void setCustomBackgroundColor(String name, String r, String g, String b) {
		String c = r+","+g+","+b;
		Colors.colors.put(name, c);
		setAttribute(BACKGROUND_COLOR, name);
		isCustom = true;
	}

	public boolean isCustom() {
		return isCustom;
	}

}
