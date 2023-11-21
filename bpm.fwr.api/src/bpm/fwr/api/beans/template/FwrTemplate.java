package bpm.fwr.api.beans.template;

import bpm.fwr.api.beans.Constants.Colors;
import bpm.fwr.api.beans.Constants.FontSizes;
import bpm.fwr.api.beans.Constants.FontTypes;

public class FwrTemplate implements ITemplate {

	public FwrTemplate() {
		
	}
	
	public Style getDataStyle() {
		Style dataStyle = new Style(false);
		dataStyle.setBackgroundColor(Colors.WHITE);
		dataStyle.setTextColor(Colors.BLACK);
		dataStyle.setFontType(FontTypes.VERDANA);
		dataStyle.setFontSize(FontSizes.T8);
		
		return dataStyle;
	}

	public Style getHeaderStyle() {
		Style headerStyle = new Style(false);
		headerStyle.setBackgroundColor(Colors.MARRON);
		headerStyle.setTextColor(Colors.WHITE);
		headerStyle.setFontType(FontTypes.VERDANA);
		headerStyle.setFontSize(FontSizes.T10);
		
		return headerStyle;
	}

	public String getOddRowsBackgroundColor() {
		return Colors.MARRON_CLAIR;
	}

	public Style getSubTitleStyle() {
		Style subTitleStyle = new Style(false);
		subTitleStyle.setBackgroundColor(Colors.MARRON_FONCE);
		subTitleStyle.setTextColor(Colors.BLACK);
		subTitleStyle.setFontType(FontTypes.ARIAL);
		subTitleStyle.setFontSize(FontSizes.T10);
		
		return subTitleStyle;
	}

	public Style getTitleStyle() {
		Style titleStyle = new Style(false);
		titleStyle.setBackgroundColor(Colors.MARRON_FONCE);
		titleStyle.setTextColor(Colors.JAUNE_FWR);
		titleStyle.setFontType(FontTypes.ARIAL);
		titleStyle.setFontSize(FontSizes.T14);
		return titleStyle;
	}

}
