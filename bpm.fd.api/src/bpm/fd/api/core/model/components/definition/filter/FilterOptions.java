package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class FilterOptions implements IComponentOptions{
	private static final String[] standardKeys = new String[] {"submitOnChange", "selectFirstValue", "defaultValue", "hidden"};
	
	public final static String DEFAULT_VALUE = "--- Select a Value ---";
	private boolean submitOnChange = true;
	private boolean selectFirstValue = false;
	private boolean initParameterWithFirstValue = true;
	private boolean hidden = false;
	private boolean required = false;
	private String defaultValue = DEFAULT_VALUE;
	
	

	/**
	 * @return the submitOnChange
	 */
	public boolean isSubmitOnChange() {
		return submitOnChange;
	}
	/**
	 * @param submitOnChange the submitOnChange to set
	 */
	public void setSubmitOnChange(boolean submitOnChange) {
		this.submitOnChange = submitOnChange;
	}
	/**
	 * @return the selectFirstValue
	 */
	public boolean isSelectFirstValue() {
		return selectFirstValue;
	}
	/**
	 * @param selectFirstValue the selectFirstValue to set
	 */
	public void setSelectFirstValue(boolean selectFirstValue) {
		this.selectFirstValue = selectFirstValue;
		if (this.selectFirstValue){
			defaultValue = null;
		}
		else{
			defaultValue = DEFAULT_VALUE;
		}
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Element getElement(){
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("submitOnChange", isSubmitOnChange() + "");
		e.addAttribute("initParameterWithFirstValue", isInitParameterWithFirstValue() + "");
		e.addAttribute("selectFirstValue", isSelectFirstValue() + "") ;
		e.addAttribute("hidden", isHidden() + "") ;
		e.addAttribute("Required", isRequired() + "");
		if (!isSelectFirstValue()){
			e.addAttribute("defaultValue", getDefaultValue() + "") ;
		}
		
		
		return e;
	}
	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	/**
	 * 
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}
	/**
	 * 
	 * @param required to set	
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}
	/**
	 * @return the initParameterWithFirstValue
	 */
	public boolean isInitParameterWithFirstValue() {
		return initParameterWithFirstValue;
	}
	/**
	 * @param initParameterWithFirstValue the initParameterWithFirstValue to set
	 */
	public void setInitParameterWithFirstValue(boolean initParameterWithFirstValue) {
		this.initParameterWithFirstValue = initParameterWithFirstValue;
	}
	
	/**
	 * return this
	 */
	public IComponentOptions getAdapter(Object type) {
		
		
		return this;
	}
	
	public String getDefaultLabelValue(String key) {
		
		return null;
	}
	public String[] getInternationalizationKeys() {
		return new String[]{};
	}
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}
	public String getValue(String key) {
		
		return null;
	}
	@Override
	public IComponentOptions copy() {
		FilterOptions copy = new FilterOptions();
		
		copy.setDefaultValue(defaultValue);
		copy.setHidden(hidden);
		copy.setInitParameterWithFirstValue(initParameterWithFirstValue);
		copy.setRequired(required);
		copy.setSelectFirstValue(selectFirstValue);
		copy.setSubmitOnChange(submitOnChange);
		
		return copy;
	}
}
