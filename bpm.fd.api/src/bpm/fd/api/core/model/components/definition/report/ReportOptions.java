package bpm.fd.api.core.model.components.definition.report;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class ReportOptions implements IComponentOptions{
	public static final int KEY_WIDTH= 0;
	public static final int KEY_HEIGHT = 1;
	
	
	
	private static final String[] standardKeys = new String[] {"width", "height"};
	private int width = 1000;
	private int height = 400;
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public String getDefaultLabelValue(String key) {
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("reportOptions");
		e.addAttribute("height", getHeight() + "");
		e.addAttribute("width", getWidth() + "");
	
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
			return getWidth() + "";
		}
		if (standardKeys[1].equals(key)){
			return getHeight() + "";
		}
		
		return null;
	}



	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public IComponentOptions copy() {
		ReportOptions copy = new ReportOptions();
		
		copy.setHeight(height);
		copy.setWidth(width);
		
		return copy;
	}
}
