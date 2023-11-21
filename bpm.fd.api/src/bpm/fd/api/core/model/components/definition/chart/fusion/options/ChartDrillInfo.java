package bpm.fd.api.core.model.components.definition.chart.fusion.options;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;

public class ChartDrillInfo {

	
	
	private boolean drillable;
	private String url;
	private boolean categorieAsParameterValue = true;

	private FdModel targetModelPage;
	private TypeTarget typeTarget =  TypeTarget.Parameter;
	private int zoomWidth = 500;
	private int zoomHeight = 500;
	
	private boolean keepColor;
	
	/**
	 * this field is used to allow using a Chart of TypeTarget.Dimension
	 * to provide other component value
	 * only the defined level will set the value of the chart
	 */
	private int drillLevelProvider = 0;
	
	private ComponentChartDefinition chart;
	
	
	
	/**
	 * @return the drillLevelProvider
	 */
	public int getDrillLevelProvider() {
		return drillLevelProvider;
	}

	/**
	 * @param drillLevelProvider the drillLevelProvider to set
	 */
	public void setDrillLevelProvider(int drillLevelProvider) {
		this.drillLevelProvider = drillLevelProvider;
	}

	/**
	 * @return the targetModelPage
	 */
	public FdModel getTargetModelPage() {
		return targetModelPage;
	}

	/**
	 * @param targetModelPage the targetModelPage to set
	 */
	public void setTargetModelPage(FdModel targetModelPage) {
		this.targetModelPage = targetModelPage;
	}

	/**
	 * @return the typeTarget
	 */
	public TypeTarget getTypeTarget() {
		return typeTarget;
	}

	/**
	 * @param typeTarget the typeTarget to set
	 */
	public void setTypeTarget(TypeTarget typeTarget) {
		this.typeTarget = typeTarget;
	}

	public ChartDrillInfo(ComponentChartDefinition chart){
		this.chart = chart;
	}

	public ComponentChartDefinition getChart(){
		return chart;
	}
	
	
	
	
	
	/**
	 * @return the zoomWidth
	 */
	public int getZoomWidth() {
		return zoomWidth;
	}

	/**
	 * @param zoomWidth the zoomWidth to set
	 */
	public void setZoomWidth(int zoomWidth) {
		this.zoomWidth = zoomWidth;
	}

	/**
	 * @return the zoomHeight
	 */
	public int getZoomHeight() {
		return zoomHeight;
	}

	/**
	 * @param zoomHeight the zoomHeight to set
	 */
	public void setZoomHeight(int zoomHeight) {
		this.zoomHeight = zoomHeight;
	}
	/**
	 * @return the drillable
	 */
	public boolean isDrillable() {
		return drillable;
	}
	/**
	 * @param drillable the drillable to set
	 */
	public void setDrillable(boolean drillable) {
		this.drillable = drillable;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the categorieAsParameterValue
	 */
	public boolean isCategorieAsParameterValue() {
		return categorieAsParameterValue;
	}
	/**
	 * @param categorieAsParameterValue the categorieAsParameterValue to set
	 */
	public void setCategorieAsParameterValue(boolean categorieAsParameterValue) {
		this.categorieAsParameterValue = categorieAsParameterValue;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("drillInfo");
		e.addAttribute("active", isDrillable() + "");
		e.addAttribute("categoryAsValue", isCategorieAsParameterValue() + "");
		e.addAttribute("drillLevelProvider", getDrillLevelProvider() + "");
		if (getUrl() != null){
			e.addElement("url").setText(getUrl());
		}
		
		e.addAttribute("drillType", getTypeTarget().name() + "");
		e.addAttribute("keepColor", isKeepColor() + "");
		if (getTargetModelPage() != null){
			e.addAttribute("targetModel", getTargetModelPage().getName());
		}

		e.addElement("zoomWidth").setText(getZoomWidth() + "");
		e.addElement("zoomHeight").setText(getZoomHeight() + "");
		
		return e;
	}

	public boolean isKeepColor() {
		return keepColor;
	}

	public void setKeepColor(boolean keepColor) {
		this.keepColor = keepColor;
	}
}
