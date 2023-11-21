package bpm.fd.api.core.model.components.definition.styledtext;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class StyledTextOptions implements IComponentOptions{
	
	private static final int KEY_INITIAL_VALUE = 0;
	private static final int KEY_COL_NUM = 1;
	private static final int KEY_ROW_NUM = 2;
	private static final int KEY_WRAP = 3;
	
	
	public static String[] standarKeys = new String[]{"initialValue", "columnsNumber", "rowsNumber", "wrap"};
	public static String[] i18nKeys = {};
	private String initialValue = "";
	private int columnsNumber = 80;
	private int rowsNumber = 20;
	
	private boolean wrap = true;

	
	
	/**
	 * @return the initialValue
	 */
	public String getInitialValue() {
		return initialValue;
	}

	/**
	 * @param initialValue the initialValue to set
	 */
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	/**
	 * @return the columnsNumber
	 */
	public int getColumnsNumber() {
		return columnsNumber;
	}

	/**
	 * @param columnsNumber the columnsNumber to set
	 */
	public void setColumnsNumber(int columnsNumber) {
		this.columnsNumber = columnsNumber;
	}

	/**
	 * @return the rowsNumber
	 */
	public int getRowsNumber() {
		return rowsNumber;
	}

	/**
	 * @param rowsNumber the rowsNumber to set
	 */
	public void setRowsNumber(int rowsNumber) {
		this.rowsNumber = rowsNumber;
	}

	/**
	 * @return the wrap
	 */
	public boolean isWrap() {
		return wrap;
	}

	/**
	 * @param wrap the wrap to set
	 */
	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}

	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	public String getDefaultLabelValue(String key) {
		
		return null;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("class", StyledTextOptions.class.getName());
		
		for(int i = 0; i < standarKeys.length; i++){
			e.addElement(standarKeys[i]).setText(getValue(standarKeys[i]));
		}
		return e;
	}

	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	public String[] getNonInternationalizationKeys() {
		return standarKeys;
	}

	public String getValue(String key) {
		int index = -1;
		
		for(int i = 0; i < standarKeys.length; i ++){
			if (standarKeys[i].equals(key)){
				index = i;
				break;
			}
		}
		switch (index) {
		case KEY_COL_NUM:
			return getColumnsNumber() + "";
		case KEY_ROW_NUM:
			return getRowsNumber() + "";
		case KEY_INITIAL_VALUE:
			return getInitialValue();
		case KEY_WRAP:
			return isWrap() + "";
		default:
			break;
		}
		return null;
	}

	@Override
	public IComponentOptions copy() {
		StyledTextOptions copy = new StyledTextOptions();
		
		copy.setColumnsNumber(columnsNumber);
		copy.setInitialValue(initialValue);
		copy.setRowsNumber(rowsNumber);
		copy.setWrap(wrap);
		
		return copy;
	}
	
	
}
