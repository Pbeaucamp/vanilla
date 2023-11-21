package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class MultipleValuesOptions implements IComponentOptions{
	public static int KEY_MULTIPLE= 0;
	
	private static final String[] standardKeys = new String[] {"multipleValues"};
	private boolean multipleValues = false;
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public String getDefaultLabelValue(String key) {
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("multipleValuesOptions");
		e.addAttribute("multipleValues", getMultipleValues() + "");
	
		return e;
	}

	public String[] getInternationalizationKeys() {
		return new String[]{};
	}

	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	public String getValue(String key) {
		if (standardKeys[0].equals(key)){
			return getMultipleValues() + "";
		}
		return null;
	}

	/**
	 * @return the size
	 */
	public boolean getMultipleValues() {
		return multipleValues;
	}

	/**
	 * @param size the size to set
	 */
	public void setMultipleValues(boolean multipleValues) {
		this.multipleValues = multipleValues;
	}

	@Override
	public IComponentOptions copy() {
		MultipleValuesOptions copy = new MultipleValuesOptions();
		
		copy.setMultipleValues(multipleValues);
		
		return copy;
	}

}
