package bpm.fd.api.core.model.components.definition.datagrid;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class DataGridOptions implements IComponentOptions{
	private static final int KEY_HEADERS_VISIBLE = 0;
	private static final int KEY_ROWS_CAN_BE_ADDED = 1;
	
	private static final String[] standardKeys = new String[]{"headersVisible", "rowsCanBeAdded", "includeTotal"};
	private static final String[] i18nKeys = new String[]{"drillHeader", "drillText"};
	
	public static final int KEY_DRILL_HEADER = 0;
	public static final int KEY_DRILL_TEXT = 1;
	
	private boolean headersVisible = true;
	private boolean rowsCanBeAdded=false;
	private boolean includeTotal = false;
	
	private String drillHeader = "Drill";
	private String drillText = "Details";
	
	public IComponentOptions getAdapter(Object type) {
		return this;
	}
	
	/**
	 * @return the includeTotal
	 */
	public boolean isIncludeTotal() {
		return includeTotal;
	}

	/**
	 * @param includeTotal the includeTotal to set
	 */
	public void setIncludeTotal(boolean includeTotal) {
		this.includeTotal = includeTotal;
	}

	/**
	 * @return the standardkeys
	 */
	public static String[] getStandardkeys() {
		return standardKeys;
	}

	public void setHeadersVisible(boolean value){
		this.headersVisible = value;
	}
	public void setRowsCanBeAdded(boolean value){
		this.rowsCanBeAdded = value;
	}

	/**
	 * @return the drillHeader
	 */
	public String getDrillHeader() {
		return drillHeader;
	}

	/**
	 * @param drillHeader the drillHeader to set
	 */
	public void setDrillHeader(String drillHeader) {
		this.drillHeader = drillHeader;
	}

	/**
	 * @return the drillText
	 */
	public String getDrillText() {
		return drillText;
	}

	/**
	 * @param drillText the drillText to set
	 */
	public void setDrillText(String drillText) {
		this.drillText = drillText;
	}

	public String getDefaultLabelValue(String key) {
		if (key.equals(i18nKeys[KEY_DRILL_HEADER])){
			return getDrillHeader();
		}
		else if (key.equals(i18nKeys[KEY_DRILL_TEXT])){
			return getDrillText();
		}
		else if (key.equals(standardKeys[KEY_HEADERS_VISIBLE])){
			return isHeadersVisible() + "";
		}
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		
		e.addAttribute("showHeaders", headersVisible + "");
		e.addAttribute("rowsCanBeAdded", rowsCanBeAdded + "");
		e.addAttribute("includeTotal", includeTotal + "");
		e.addAttribute("drillHeader", getDrillHeader());
		e.addAttribute("drillText", getDrillText());
		return e;
	}

	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	public String getValue(String key) {
		if (key.equals(standardKeys[0])){
			return headersVisible + "";
		}
	
		if (key.equals(standardKeys[1])){
			return rowsCanBeAdded + "";
		}
		
		if (key.equals(standardKeys[2])){
			return includeTotal + "";
		}
		return null;
	}

	/**
	 * @return the headersVisible
	 */
	public boolean isHeadersVisible() {
		return headersVisible;
	}

	/**
	 * @return the rowsCanBeAdded
	 */
	public boolean isRowsCanBeAdded() {
		return rowsCanBeAdded;
	}

	@Override
	public IComponentOptions copy() {
		DataGridOptions copy = new DataGridOptions();
		
		copy.setDrillHeader(drillHeader);
		copy.setDrillText(drillText);
		copy.setHeadersVisible(headersVisible);
		copy.setIncludeTotal(includeTotal);
		copy.setRowsCanBeAdded(rowsCanBeAdded);
		
		return copy;
	}

}
