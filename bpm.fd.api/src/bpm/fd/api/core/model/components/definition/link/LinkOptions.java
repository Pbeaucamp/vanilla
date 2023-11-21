package bpm.fd.api.core.model.components.definition.link;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class LinkOptions implements IComponentOptions{
	public static String[] i18nKeys = new String[]{"label"};
	public static String[] standarKeys = new String[]{"url"};

	public static final int KEY_LABEL = 0;
	
	private String url = "";
	private String label = "";
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public void setUrl(String url){
		this.url = url;
	}
	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	public String getUrl(){
		return url;
	}
	
	public String getDefaultLabelValue(String key) {
		if (i18nKeys[0].equals(key)){
			return label;
		}
		if (standarKeys[0].equals(key)){
			return url;
		}
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addElement("url").setText(getUrl());
		e.addElement("label").setText(getLabel());	
		
		return e;
	}

	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	public String[] getNonInternationalizationKeys() {
		return standarKeys;
	}

	public String getValue(String key) {
		if (i18nKeys[0].equals(key)){
			return label;
		}
		else if (standarKeys[0].equals(key)){
			return url;
		}
		return null;
	}

	@Override
	public IComponentOptions copy() {
		LinkOptions copy = new LinkOptions();
		
		copy.setLabel(label);
		copy.setUrl(url);
		
		return copy;
	}
}
