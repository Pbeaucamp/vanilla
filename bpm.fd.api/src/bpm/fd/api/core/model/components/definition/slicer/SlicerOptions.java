package bpm.fd.api.core.model.components.definition.slicer;

import java.awt.Color;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

/**
 * Contains some behavior and cosmetics setting for the Slicer
 * 
 * About the levelLinked Option : 
 * 
 * If this value is true, this means that a row will be allowed by the slicer only
 * each level Row values match to a selected Level value. It act as a standard dimension
 * Using this, will a have the additional following result :
 *  - unchecking a value from a level will hide all available values from the next level that has this one as parent
 *  (for a dimension (country,city), unchecking France will remove the visible cities from France on the level2 panel.
 *  And no row with France as country will be used.
 *  
 *   If this value is False, the rows will be validated in the same way, but the Level2 panel wont be removed from 
 *   values that do not have a match from the previous levels
 * 
 * @author ludo
 *
 */
public class SlicerOptions implements IComponentOptions{
	public static final  Color INACTIVE_COLOR = new Color(
			Integer.valueOf("00", 16),
			Integer.valueOf("78", 16),
			Integer.valueOf("AE", 16));
	public static final Color ACTIVE_COLOR = new Color(
			Integer.valueOf("6E", 16),
			Integer.valueOf("AC", 16),
			Integer.valueOf("2C", 16));
	
	private Color defaultColor = INACTIVE_COLOR;
	private Color activeColor = ACTIVE_COLOR;
	/**
	 * asks for a refresh UI when a check
	 */
	private boolean submitOnCheck = true;
	
	/**
	 * true if the levels are linked
	 */
	private boolean levelLinked = true;
	/**
	 * @return the defaultColor
	 */
	public Color getDefaultColor() {
		if (defaultColor == null){
			return INACTIVE_COLOR;
		}
		return defaultColor;
	}
	/**
	 * @param defaultColor the defaultColor to set
	 */
	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}
	/**
	 * @return the activeColor
	 */
	public Color getActiveColor() {
		if (activeColor == null){
			return ACTIVE_COLOR;
		}
		return activeColor;
	}
	/**
	 * @param activeColor the activeColor to set
	 */
	public void setActiveColor(Color activeColor) {
		this.activeColor = activeColor;
	}
	/**
	 * @return the submitOnCheck
	 */
	public boolean isSubmitOnCheck() {
		return submitOnCheck;
	}
	/**
	 * @param submitOnCheck the submitOnCheck to set
	 */
	public void setSubmitOnCheck(boolean submitOnCheck) {
		this.submitOnCheck = submitOnCheck;
	}
	/**
	 * @return the levelLinked
	 */
	public boolean isLevelLinked() {
		return levelLinked;
	}
	/**
	 * @param levelLinked the levelLinked to set
	 */
	public void setLevelLinked(boolean levelLinked) {
		this.levelLinked = levelLinked;
	}
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("slicerOptions");
		
		e.addElement("defaultColor").addAttribute("red", getDefaultColor().getRed() + "")
									.addAttribute("green", getDefaultColor().getGreen() + "")
									.addAttribute("blue", getDefaultColor().getBlue()+ "");
		
		e.addElement("activeColor").addAttribute("red", getActiveColor().getRed() + "")
									.addAttribute("green", getActiveColor().getGreen() + "")
									.addAttribute("blue", getActiveColor().getBlue()+ "");
		e.addAttribute("levelLinked", isLevelLinked() + "");
		e.addAttribute("submitOnCheck", isSubmitOnCheck() + "");
		return e;
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
	public String[] getInternationalizationKeys() {
		return new String[]{};
	}
	@Override
	public String[] getNonInternationalizationKeys() {
		return new String[]{};
	}
	@Override
	public String getValue(String key) {
		return null;
	}
	@Override
	public IComponentOptions copy() {
		SlicerOptions copy = new SlicerOptions();
		
		copy.setActiveColor(activeColor);
		copy.setDefaultColor(defaultColor);
		copy.setLevelLinked(levelLinked);
		copy.setSubmitOnCheck(submitOnCheck);
		
		return copy;
	}
	
}
