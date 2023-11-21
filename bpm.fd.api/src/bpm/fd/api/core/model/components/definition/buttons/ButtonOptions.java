package bpm.fd.api.core.model.components.definition.buttons;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class ButtonOptions implements IComponentOptions{
	private static final String[] i18nKeys = new String[]{"label"};
	private static final String[] standardKeys = new String[] {};
	
	public static final int KEY_LABEL = 0;
	
	
	private String label;
	
	
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}
	
	public String getDefaultLabelValue(String key) {
		if (key.equals(i18nKeys[KEY_LABEL])){
			return getLabel();
		}
		return null;
	}
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("buttonOptions");
		
		e.addElement("label").setText(getLabel());
		
		return e;
	}
	
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}
	
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}
	
	public String getValue(String key) {
		if (i18nKeys[KEY_LABEL].equals(key)){
			return getLabel();
		}
		
		return null;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(String key, String value) {
		if (i18nKeys[KEY_LABEL].equals(key)){
			setLabel(value);
		}
		
	}

	@Override
	public IComponentOptions copy() {
		ButtonOptions copy = new ButtonOptions();
		copy.setLabel(label);
		
		return copy;
	}
	
	
	
}
