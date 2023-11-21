package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class MenuOptions implements IComponentOptions{
	public static final int KEY_SIZE= 0;
	public static final int KEY_VERTICAL = 1;
	public static final int KEY_WIDTH = 2;
	
	
	private static final String[] standardKeys = new String[] {"size", "isVertical", "width"};
	private int size = 1;
	private boolean isVertical = false;
	private int width = 200;
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public String getDefaultLabelValue(String key) {
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("menuOptions");
		e.addAttribute("size", getSize() + "");
		e.addAttribute("isHorizontal", getIsVertical() + "");
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
			return getSize() + "";
		}
		if (standardKeys[1].equals(key)){
			return getIsVertical() + "";
		}
		if (standardKeys[2].equals(key)){
			return getWidth() + "";
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

	public boolean getIsVertical() {
		return isVertical;
	}

	public void setIsVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public IComponentOptions copy() {
		MenuOptions copy = new MenuOptions();
		
		copy.setIsVertical(isVertical);
		copy.setSize(size);
		copy.setWidth(width);
		
		return copy;
	}

}
