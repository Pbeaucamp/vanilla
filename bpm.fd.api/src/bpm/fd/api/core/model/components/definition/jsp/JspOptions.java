package bpm.fd.api.core.model.components.definition.jsp;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class JspOptions implements IComponentOptions{
	public static final int KEY_WIDTH= 0;
	public static final int KEY_HEIGHT = 1;
	public static final int KEY_BORDER_WIDTH = 2;
	
	
	private static final String[] standardKeys = new String[] {"width", "height", "border_width"};
	private int width = 200;
	private int height = 200;
	private int border_width = 1;
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public String getDefaultLabelValue(String key) {
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("jspOptions");
		e.addAttribute("height", getHeight() + "");
		e.addAttribute("width", getWidth() + "");
		e.addAttribute("border_width", getBorder_width() + "");
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

	/**
	 * @return the border_width
	 */
	public int getBorder_width() {
		return border_width;
	}

	/**
	 * @param borderWidth the border_width to set
	 */
	public void setBorder_width(int borderWidth) {
		border_width = borderWidth;
	}

	@Override
	public IComponentOptions copy() {
		JspOptions copy = new JspOptions();
		
		copy.setBorder_width(border_width);
		copy.setHeight(height);
		copy.setWidth(width);
		
		return copy;
	}
	
	
}
