package bpm.fd.api.core.model.components.definition.chart.fusion.options;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class NumberFormatOptions implements IComponentOptions{

	public static final int KEY_FORMAT_NUMBER = 0;
	public static final int KEY_FORMAT_NUMBER_SCALE = 1;
	public static final int KEY_NUMBER_PREFIX = 2;
	public static final int KEY_NUMBER_SUFFIX = 3;
	public static final int KEY_DECIMAL_SEPARATOR = 4;
	public static final int KEY_THOUSAND_SEPARATOR = 5;
	public static final int KEY_DECIMALS = 6;
	public static final int KEY_FORCE_DECIMALS = 7;
	
	private static final String[] i18nKeys = new String[]{};
	public static final String[] standardKeys = new String[] {
		"formatNumber", "formatNumberScale",
		"numberPrefix",
		"numberSuffix", "decimalSeparator", "thousandSeparator",
		"decimals", "forceDecimal"
		};
	
	private boolean formatNumber = false;
	private boolean formatNumberScale = false;
	private String numberPrefix = "";
	private String numberSuffix = "";
	private String decimalSeparator = ".";
	private String thousandSeparator = ",";
	
	private int decimals = 2;
	private boolean forceDecimal = false;

	
	
	
	
	

	/**
	 * @return the formatNumber
	 */
	public boolean isFormatNumber() {
		return formatNumber;
	}

	/**
	 * @param formatNumber the formatNumber to set
	 */
	public void setFormatNumber(boolean formatNumber) {
		this.formatNumber = formatNumber;
	}

	/**
	 * @return the formatNumberScale
	 */
	public boolean isFormatNumberScale() {
		return formatNumberScale;
	}

	/**
	 * @param formatNumberScale the formatNumberScale to set
	 */
	public void setFormatNumberScale(boolean formatNumberScale) {
		this.formatNumberScale = formatNumberScale;
	}

	/**
	 * @return the numberPrefix
	 */
	public String getNumberPrefix() {
		return numberPrefix;
	}

	/**
	 * @param numberPrefix the numberPrefix to set
	 */
	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
	}

	/**
	 * @return the numberSuffix
	 */
	public String getNumberSuffix() {
		return numberSuffix;
	}

	/**
	 * @param numberSuffix the numberSuffix to set
	 */
	public void setNumberSuffix(String numberSuffix) {
		this.numberSuffix = numberSuffix;
	}

	/**
	 * @return the decimalSeparator
	 */
	public String getDecimalSeparator() {
		return decimalSeparator;
	}

	/**
	 * @param decimalSeparator the decimalSeparator to set
	 */
	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	/**
	 * @return the thousandSeparator
	 */
	public String getThousandSeparator() {
		return thousandSeparator;
	}

	/**
	 * @param thousandSeparator the thousandSeparator to set
	 */
	public void setThousandSeparator(String thousandSeparator) {
		this.thousandSeparator = thousandSeparator;
	}

	/**
	 * @return the decimals
	 */
	public int getDecimals() {
		return decimals;
	}

	/**
	 * @param decimals the decimals to set
	 */
	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}

	/**
	 * @return the forceDecimal
	 */
	public boolean isForceDecimal() {
		return forceDecimal;
	}

	/**
	 * @param forceDecimal the forceDecimal to set
	 */
	public void setForceDecimal(boolean forceDecimal) {
		this.forceDecimal = forceDecimal;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("numberFormatOptions");
		
		for(String s : standardKeys){
			e.addAttribute(s, getValue(s));
		}
		return e;
	}
	
	public IComponentOptions getAdapter(Object type) {
		
		return this;
	}
	public String getDefaultLabelValue(String key) {
		
		return null;
	}
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}
	public String getValue(String key) {
		if (standardKeys[KEY_DECIMAL_SEPARATOR].equals(key)){
			return getDecimalSeparator();
		}
		else if (standardKeys[KEY_DECIMALS].equals(key)){
			return getDecimals() + "";
		}
		else if (standardKeys[KEY_FORCE_DECIMALS].equals(key)){
			return isForceDecimal() ? "1" : "0";
			
		}
		else if (standardKeys[KEY_FORMAT_NUMBER].equals(key)){
			return isFormatNumber() ? "1" : "0";
			
		}
		else if (standardKeys[KEY_FORMAT_NUMBER_SCALE].equals(key)){
			return isFormatNumberScale()? "1" : "0";
			
		}
		else if (standardKeys[KEY_NUMBER_PREFIX].equals(key)){
			return getNumberPrefix();
			
		}
		else if (standardKeys[KEY_NUMBER_SUFFIX].equals(key)){
			return getNumberSuffix();
			
		}
		else if (standardKeys[KEY_THOUSAND_SEPARATOR].equals(key)){
			return getThousandSeparator();
			
		}
		return null;
	}

	@Override
	public IComponentOptions copy() {
		NumberFormatOptions copy = new NumberFormatOptions();
		
		copy.setDecimals(decimals);
		copy.setDecimalSeparator(decimalSeparator);
		copy.setForceDecimal(forceDecimal);
		copy.setFormatNumber(formatNumber);
		copy.setFormatNumberScale(formatNumberScale);
		copy.setNumberPrefix(numberPrefix);
		copy.setNumberSuffix(numberSuffix);
		copy.setThousandSeparator(thousandSeparator);
		
		return copy;
	}
	
	
	
}

