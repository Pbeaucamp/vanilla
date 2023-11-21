package bpm.fd.api.core.model.components.definition;

import org.dom4j.Element;

public interface IComponentOptions {
	public Element getElement();
	public IComponentOptions getAdapter(Object type);
	
	/**
	 * must not return null because its used by UI when saving 
	 * model to create the project properties files within a loop
	 * @return
	 */
	public String[] getInternationalizationKeys();
	public String[] getNonInternationalizationKeys();
	public String getValue(String key);
	
	public String getDefaultLabelValue(String key);
	
	public IComponentOptions copy();
	
}
