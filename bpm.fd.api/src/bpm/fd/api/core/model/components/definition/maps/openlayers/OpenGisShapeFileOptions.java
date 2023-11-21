package bpm.fd.api.core.model.components.definition.maps.openlayers;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class OpenGisShapeFileOptions implements IOpenLayersOptions {

	public static String[] standardKeys = new String[]{"wmsUrl","layerName","type","longitude","latitude","zoom"};
	public static String[] i18nKeys = new String[]{};
	
	private String xml;
	
	@Override
	public String getZoneXml() {
		return xml;
	}
	
	public void setZoneXml(String xml) {
		this.xml = xml;
	}

	@Override
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	@Override
	public String getDefaultLabelValue(String key) {
		
		return null;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("class", OpenGisShapeFileOptions.class.getName());
		
		for(int i = 0; i < standardKeys.length; i++){
			e.addElement(standardKeys[i]).setText(getValue(standardKeys[i]));
		}
		return e;
	}

	@Override
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	@Override
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	@Override
	public String getValue(String key) {
		
		return null;
	}

	@Override
	public IComponentOptions copy() {
		OpenGisShapeFileOptions copy = new OpenGisShapeFileOptions();
		
		copy.setZoneXml(xml);
		
		return copy;
	}

}
