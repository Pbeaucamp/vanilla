package bpm.fd.api.core.model.components.definition.filter;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class SliderOptions implements IComponentOptions{
	private static final String[] standardKeys = new String[] {"delay", "width", "height", "barColor", "sliderColor", "autoRun"};
	
	public static int DELAY = 0;
	public static int WIDTH = 1;
	public static int HEIGHT = 2;
	public static int BAR_COLOR = 3;
	public static int SLIDER_COLOR = 4;
	public static int HAS_AUTO_RUN = 5;
	
	private int delay = 5000;
	private int width = 500;
	private int height = 10;
	private boolean autoRun = false;
	
	private String barColor = "silver";
	private String sliderColor = "#666666";
	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}
	/**
	 * @param delay the delay to set
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return the barColor
	 */
	public String getBarColor() {
		return barColor;
	}
	/**
	 * @param barColor the barColor to set
	 */
	public void setBarColor(String barColor) {
		this.barColor = barColor;
	}
	/**
	 * @return the sliderColor
	 */
	public String getSliderColor() {
		return sliderColor;
	}
	/**
	 * @param sliderColor the sliderColor to set
	 */
	public void setSliderColor(String sliderColor) {
		this.sliderColor = sliderColor;
	}
	public IComponentOptions getAdapter(Object type) {
		try{
			if (type == FilterRenderer.getRenderer(FilterRenderer.SLIDER)){
				return this;
			}
		}catch(Exception ex){
			
		}
		
		return null;
	}
	public String getDefaultLabelValue(String key) {
		return "";
	}
	public Element getElement() {
		Element e = DocumentHelper.createElement("sliderOptions");
		e.addAttribute("barColor",getBarColor() );
		e.addAttribute("delay",getDelay() + "" );
		e.addAttribute("height",getHeight() + "");
		e.addAttribute("sliderColor",getSliderColor() + "");
		e.addAttribute("width",getWidth() + "");
		e.addAttribute("autoRun",isAutoRun() + "");

		return e;
	}
	public String[] getInternationalizationKeys() {
		return new String[]{};
	}
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}
	public String getValue(String key) {
		if (standardKeys[BAR_COLOR].equals(key)){
			return getBarColor();
		}
		else if (standardKeys[DELAY].equals(key)){
			return getDelay() + "";
		}
		else if (standardKeys[HEIGHT].equals(key)){
			return getHeight() + "";
		}
		else if (standardKeys[SLIDER_COLOR].equals(key)){
			return getSliderColor();
		}
		else if (standardKeys[WIDTH].equals(key)){
			return getWidth() + "";
		}
		else if (standardKeys[HAS_AUTO_RUN].equals(key)){
			return isAutoRun() + "";
		}
		return null;
	}
	
	/**
	 * @return the autoRun
	 */
	public boolean isAutoRun() {
		return autoRun;
	}
	/**
	 * @param autoRun the autoRun to set
	 */
	public void setAutoRun(boolean autoRun) {
		this.autoRun = autoRun;
	}
	@Override
	public IComponentOptions copy() {
		SliderOptions copy = new SliderOptions();
		
		copy.setAutoRun(autoRun);
		copy.setBarColor(barColor);
		copy.setDelay(delay);
		copy.setHeight(height);
		copy.setSliderColor(sliderColor);
		copy.setWidth(width);
		
		return copy;
	}
	
	
	
}
