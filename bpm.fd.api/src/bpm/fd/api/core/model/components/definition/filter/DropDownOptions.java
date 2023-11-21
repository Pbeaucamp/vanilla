package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class DropDownOptions implements IComponentOptions{
	public static int KEY_SIZE= 0;
	
	private static final String[] standardKeys = new String[] {"size"};
	private int size = 1;
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public String getDefaultLabelValue(String key) {
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("dropDownOptions");
		e.addAttribute("size", getSize() + "");
	
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
			return getSize() + "";
		}
		return null;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public IComponentOptions copy() {
		DropDownOptions copy = new DropDownOptions();
		
		copy.setSize(size);
		
		return copy;
	}

}
