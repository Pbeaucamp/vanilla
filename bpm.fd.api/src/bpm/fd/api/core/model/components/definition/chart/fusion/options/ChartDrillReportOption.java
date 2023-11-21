package bpm.fd.api.core.model.components.definition.chart.fusion.options;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class ChartDrillReportOption implements IComponentOptions{
	public final static String[] standardKeys = new String[]{"chartDataDetails", "cssName"};
	private String chartDataDetails = "Chart Datas";
	private String cssName = "";
	
	
	
	/**
	 * @return the cssName
	 */
	public String getCssName() {
		return cssName;
	}

	/**
	 * @param cssName the cssName to set
	 */
	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	public IComponentOptions getAdapter(Object type) {

		return this;
	}

	public void setChartDataDetails(String value){
		chartDataDetails = value;
	}
	
	public String getChartDataDetails(){
		return chartDataDetails;
	}
	public String getDefaultLabelValue(String key) {
		return chartDataDetails;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("drillReportOptions");
		
		for(String s : standardKeys){
			e.addAttribute(s, getValue(s));
		}
		return e;
	}

	public String[] getInternationalizationKeys() {
		return new String[]{};
	}

	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	public String getValue(String key) {
		if (key.equals("ChartDataDetails")){
			return chartDataDetails;
		}
		if (key.equals("cssName")){
			return cssName;
		}
		return null;
	}

	@Override
	public IComponentOptions copy() {
		ChartDrillReportOption copy = new ChartDrillReportOption();
		
		copy.setChartDataDetails(chartDataDetails);
		copy.setCssName(cssName);
		
		return copy;
	}

}
