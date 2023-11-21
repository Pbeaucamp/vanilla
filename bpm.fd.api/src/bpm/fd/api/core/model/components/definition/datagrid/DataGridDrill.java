package bpm.fd.api.core.model.components.definition.datagrid;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.FdModel;

public class DataGridDrill {
//	public enum Renderer{
//		hyperlink;
//	}
//	public enum DrillDestination{
//		page,
//		popup;
//	}
//	
//	public enum Action{
//		openPage;
//	}
//	
//	
//	
//	private Renderer renderer = Renderer.hyperlink;
//	private DrillDestination destination = DrillDestination.popup;
//	private Action action = Action.openPage;
	
	public static enum DrillTarget{
		Parameter, PopupPage
	}
	private DrillTarget type = DrillTarget.Parameter;
	
	private int popupWidth = 400;
	private int popupHeight = 300;
	private String modelPageName;
	private FdModel modelPage;
	private Integer drillableFieldIndex;
	
	
//	private HashMap<ComponentParameter, Integer> outputParamProviders = new HashMap<ComponentParameter, Integer>();
//	private HashMap<ComponentParameter, ComponentParameter> outputParamMapToComponentParam = new HashMap<ComponentParameter, ComponentParameter>();
	

	
	
	
//	public void setParameterForColumn(Integer element,	ComponentParameter parameter) {
//		if (element == null || element.intValue() == -1){
//			outputParamProviders.put(parameter, null);
//		}
//		else{
//			outputParamProviders.put(parameter, element);
//		}
//		
//	}

	


	/**
	 * @return the type
	 */
	public DrillTarget getType() {
		return type;
	}


	/**
	 * @return the drillableFieldIndex
	 */
	public Integer getDrillableFieldIndex() {
		return drillableFieldIndex;
	}


	/**
	 * @param drillableFieldIndex the drillableFieldIndex to set
	 */
	public void setDrillableFieldIndex(Integer drillableFieldIndex) {
		this.drillableFieldIndex = drillableFieldIndex;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(DrillTarget type) {
		this.type = type;
	}


//	public Integer getColumnPositionForParameter(ComponentParameter param) {
//		return outputParamProviders.get(param);
//	}
//
//
//	public List<ComponentParameter> getOutputParameters() {
//		return new ArrayList<ComponentParameter>(outputParamProviders.keySet());
//	}


	/**
	 * @return the modelPage
	 */
	public FdModel getModelPage() {
		return modelPage;
	}


	/**
	 * @param modelPage the modelPage to set
	 */
	public void setModelPage(FdModel modelPage) {
		if (modelPage != null){
			this.modelPageName = modelPage.getName();
			this.modelPage = modelPage;
		}
		
//		outputParamMapToComponentParam.clear();
	}
	
	public void setModelPage(String modelPageName){
		this.modelPageName = modelPageName;
	}
	
//	public void map(ComponentParameter dataGridOutputParam, ComponentParameter targetParam){
//		outputParamMapToComponentParam.put(dataGridOutputParam, targetParam);
//	}
//	
//	public ComponentParameter getMappingForTargetParameter(ComponentParameter targetParameter){
//
//		return outputParamMapToComponentParam.get(targetParameter);
//	}
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("drill");
		
		
		if (modelPageName != null){
			e.addElement("targetPage").setText(modelPageName);
		}
//		for(ComponentParameter p : outputParamProviders.keySet()){
//			Element _e = e.addElement("outputParameterProvider").addAttribute("parameterName", p.getName());
//			if (outputParamProviders.get(p) != null && outputParamProviders.get(p) >= 0){
//				_e.setText(outputParamProviders.get(p) + "");
//			}
//		}
		e.addAttribute("type", getType().name());
		if (getDrillableFieldIndex() != null){
			e.addAttribute("drillFieldIndex", getDrillableFieldIndex() + "");
		}
		e.addAttribute("popupWidth", "" + getPopupWidth());
		e.addAttribute("popupHeight", "" + getPopupHeight());
//		for(ComponentParameter p : outputParamMapToComponentParam.keySet()){
//			Element _e = e.addElement("parameterMapping").addAttribute("parameterName", p.getName());
//			if (outputParamMapToComponentParam.get(p) != null ){
//				_e.setText(outputParamMapToComponentParam.get(p.getName()) + "");
//			}
//		}
		return e;
	}


	public String getModelPageName() {
		return modelPageName;
	}


//	public void removeParameter(ComponentParameter parameter) {
//		outputParamProviders.remove(parameter);
//		
//	}


	/**
	 * @return the popupWidth
	 */
	public int getPopupWidth() {
		return popupWidth;
	}


	/**
	 * @param popupWidth the popupWidth to set
	 */
	public void setPopupWidth(int popupWidth) {
		if (popupWidth > 10){
			this.popupWidth = popupWidth;
		}
		
	}


	/**
	 * @return the popupHeight
	 */
	public int getPopupHeight() {
		return popupHeight;
	}


	/**
	 * @param popupHeight the popupHeight to set
	 */
	public void setPopupHeight(int popupHeight) {
		if (popupHeight > 10){
			this.popupHeight = popupHeight;
		}
		
	}
	
}
