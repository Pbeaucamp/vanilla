package bpm.android.vanilla.wrapper.reporting;

import bpm.android.vanilla.core.IAndroidConstant;
import bpm.fwr.api.beans.template.Style;

/**
 * This class is placed here for now but at the end it will be in the android core
 * @author svi
 *
 */
public class DefaultTemplate {
	
	public DefaultTemplate(){}
	
	public Style getDataStyle() {
		Style defaultDataStyle = new Style(false);
		defaultDataStyle.setBackgroundColor(IAndroidConstant.WHITE);
		defaultDataStyle.setTextColor(IAndroidConstant.BLACK);
		defaultDataStyle.setFontType(IAndroidConstant.VERDANA);
		defaultDataStyle.setFontSize(IAndroidConstant.T8);
		
		return defaultDataStyle;	
	}

	public Style getHeaderStyle() {
		Style defaultHeaderStyle = new Style(false);
		defaultHeaderStyle.setBackgroundColor(IAndroidConstant.GRAY);
		defaultHeaderStyle.setTextColor(IAndroidConstant.WHITE);
		defaultHeaderStyle.setFontType(IAndroidConstant.VERDANA);
		defaultHeaderStyle.setFontSize(IAndroidConstant.T10);
		
		return defaultHeaderStyle;
	}

	public Style getSubTitleStyle() {
		Style defaultSubTitleStyle = new Style(false);
		defaultSubTitleStyle.setBackgroundColor(IAndroidConstant.WHITE);
		defaultSubTitleStyle.setTextColor(IAndroidConstant.GRAY);
		defaultSubTitleStyle.setFontType(IAndroidConstant.COMIC);
		defaultSubTitleStyle.setFontSize(IAndroidConstant.T8);
		
		return defaultSubTitleStyle;
	}

	public Style getTitleStyle() {
		Style defaultTitleStyle = new Style(false);
		defaultTitleStyle.setBackgroundColor(IAndroidConstant.BLACK);
		defaultTitleStyle.setTextColor(IAndroidConstant.WHITE);
		defaultTitleStyle.setFontType(IAndroidConstant.COMIC);
		defaultTitleStyle.setFontSize(IAndroidConstant.T14);
		return defaultTitleStyle;
	}

	public String getOddRowsBackgroundColor() {
		return IAndroidConstant.GRAY;
	}
}
