package bpm.metadata.ui.birtreport.templates;

import bpm.fwr.api.beans.Constants.Colors;
import bpm.fwr.api.beans.Constants.FontSizes;
import bpm.fwr.api.beans.Constants.FontTypes;
import bpm.fwr.api.beans.template.Style;

public class DefaultTemplate{

	public DefaultTemplate(){}
	
	public Style getDataStyle() {
		Style defaultDataStyle = new Style(false);
		defaultDataStyle.setBackgroundColor(Colors.WHITE);
		defaultDataStyle.setTextColor(Colors.BLACK);
		defaultDataStyle.setFontType(FontTypes.VERDANA);
		defaultDataStyle.setFontSize(FontSizes.T8);
		
		return defaultDataStyle;	
	}

	public Style getHeaderStyle() {
		Style defaultHeaderStyle = new Style(false);
		defaultHeaderStyle.setBackgroundColor(Colors.GRAY);
		defaultHeaderStyle.setTextColor(Colors.WHITE);
		defaultHeaderStyle.setFontType(FontTypes.VERDANA);
		defaultHeaderStyle.setFontSize(FontSizes.T10);
		
		return defaultHeaderStyle;
	}

	public Style getSubTitleStyle() {
		Style defaultSubTitleStyle = new Style(false);
		defaultSubTitleStyle.setBackgroundColor(Colors.WHITE);
		defaultSubTitleStyle.setTextColor(Colors.GRAY);
		defaultSubTitleStyle.setFontType(FontTypes.COMIC);
		defaultSubTitleStyle.setFontSize(FontSizes.T8);
		
		return defaultSubTitleStyle;
		
		
	}

	public Style getTitleStyle() {
		Style defaultTitleStyle = new Style(false);
		defaultTitleStyle.setBackgroundColor(Colors.BLACK);
		defaultTitleStyle.setTextColor(Colors.WHITE);
		defaultTitleStyle.setFontType(FontTypes.COMIC);
		defaultTitleStyle.setFontSize(FontSizes.T14);
		return defaultTitleStyle;
	}

	public String getOddRowsBackgroundColor() {
		return Colors.GRAY;
	}

}
