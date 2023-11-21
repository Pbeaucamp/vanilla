package bpm.fwr.api.beans.template;

import java.util.HashMap;


public class CustomTemplate implements ITemplate {

	public CustomTemplate() {
		
	}
	
	private HashMap<String,String> dataStyle = new HashMap<String, String>();
	private HashMap<String,String> headerStyle = new HashMap<String, String>();
	private HashMap<String,String> subtitleStyle = new HashMap<String, String>();
	private HashMap<String,String> titleStyle = new HashMap<String, String>();
	private String oddRowsBackgroundColor;
	

	public String getOddRowsBackgroundColor() {
		return oddRowsBackgroundColor;
	}

	public void setOddRowsBackgroundColor(String oddRowsBackgroundColor) {
		this.oddRowsBackgroundColor = oddRowsBackgroundColor;
	}

	public Style getDataStyle() {
		Style defaultDataStyle = new Style(true);
		defaultDataStyle.setBackgroundColor(dataStyle.get(BACKGROUND_COLOR));
		defaultDataStyle.setTextColor(dataStyle.get(TEXT_COLOR));
		defaultDataStyle.setFontType(dataStyle.get(FONT_TYPE));
		defaultDataStyle.setFontSize(dataStyle.get(FONT_SIZE));
		
		return defaultDataStyle;	
	}

	public Style getHeaderStyle() {
		Style defaultHeaderStyle = new Style(true);
		defaultHeaderStyle.setBackgroundColor(headerStyle.get(BACKGROUND_COLOR));
		defaultHeaderStyle.setTextColor(headerStyle.get(TEXT_COLOR));
		defaultHeaderStyle.setFontType(headerStyle.get(FONT_TYPE));
		defaultHeaderStyle.setFontSize(headerStyle.get(FONT_SIZE));
		
		return defaultHeaderStyle;
	}

	public Style getSubTitleStyle() {
		Style defaultSubTitleStyle = new Style(true);
		defaultSubTitleStyle.setBackgroundColor(subtitleStyle.get(BACKGROUND_COLOR));
		defaultSubTitleStyle.setTextColor(subtitleStyle.get(TEXT_COLOR));
		defaultSubTitleStyle.setFontType(subtitleStyle.get(FONT_TYPE));
		defaultSubTitleStyle.setFontSize(subtitleStyle.get(FONT_SIZE));
		
		return defaultSubTitleStyle;
		
		
	}

	public Style getTitleStyle() {
		Style defaultTitleStyle = new Style(true);
		defaultTitleStyle.setBackgroundColor(titleStyle.get(BACKGROUND_COLOR));
		defaultTitleStyle.setTextColor(titleStyle.get(TEXT_COLOR));
		defaultTitleStyle.setFontType(titleStyle.get(FONT_TYPE));
		defaultTitleStyle.setFontSize(titleStyle.get(FONT_SIZE));
		
		return defaultTitleStyle;
	}

	public void setStyle(String style, String backgroundColor, String textColor, String fontType, String fontSize) {
		if(style.equalsIgnoreCase(ITemplate.DATA_STYLE)) {
			dataStyle.put(ITemplate.BACKGROUND_COLOR, backgroundColor);
			dataStyle.put(ITemplate.TEXT_COLOR, textColor);
			dataStyle.put(ITemplate.FONT_SIZE, fontSize);
			dataStyle.put(ITemplate.FONT_TYPE, fontType);
		}
		else if(style.equalsIgnoreCase(ITemplate.HEADER_STYLE)) {
			headerStyle.put(ITemplate.BACKGROUND_COLOR, backgroundColor);
			headerStyle.put(ITemplate.TEXT_COLOR, textColor);
			headerStyle.put(ITemplate.FONT_SIZE, fontSize);
			headerStyle.put(ITemplate.FONT_TYPE, fontType);
		}
		else if(style.equalsIgnoreCase(ITemplate.TITLE_STYLE)) {
			titleStyle.put(ITemplate.BACKGROUND_COLOR, backgroundColor);
			titleStyle.put(ITemplate.TEXT_COLOR, textColor);
			titleStyle.put(ITemplate.FONT_SIZE, fontSize);
			titleStyle.put(ITemplate.FONT_TYPE, fontType);
		}
		else if(style.equalsIgnoreCase(ITemplate.SUBTITLE_STYLE)) {
			subtitleStyle.put(ITemplate.BACKGROUND_COLOR, backgroundColor);
			subtitleStyle.put(ITemplate.TEXT_COLOR, textColor);
			subtitleStyle.put(ITemplate.FONT_SIZE, fontSize);
			subtitleStyle.put(ITemplate.FONT_TYPE, fontType);
		}
	}
}
