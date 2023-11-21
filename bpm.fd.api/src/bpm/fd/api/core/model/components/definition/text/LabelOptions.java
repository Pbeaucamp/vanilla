package bpm.fd.api.core.model.components.definition.text;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class LabelOptions implements IComponentOptions{

	public static String[] i18nKeys = new String[]{"text"};

	public static final int KEY_TEXT = 0;
	private String text;
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public void setText(String text){
		this.text = text;
	}
	public String getText(){
		return text;
	}
	
	public String getDefaultLabelValue(String key) {
		if (i18nKeys[0].equals(key)){
			return text;
		}
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addElement("content").setText(getText());
				
		
		return e;
	}

	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	public String[] getNonInternationalizationKeys() {
		return new String[]{};
	}

	public String getValue(String key) {
		if (i18nKeys[0].equals(key)){
			return text;
		}
		return null;
	}

	@Override
	public IComponentOptions copy() {
		LabelOptions copy = new LabelOptions();
		
		copy.setText(text);
		
		return copy;
	}

}
