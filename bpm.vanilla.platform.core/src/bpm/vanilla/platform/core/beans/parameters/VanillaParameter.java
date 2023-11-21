package bpm.vanilla.platform.core.beans.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class VanillaParameter implements Serializable {

	private static final long serialVersionUID = 4988271055681720267L;
	
	public static final int TEXT_BOX = 0;
	public static final int LIST_BOX = 1;
	public static final int RADIO_BUTTON = 2;
	public static final int CHECK_BOX = 3;
	public static final int AUTO_SUGGEST = 4;

	public static final int TYPE_ANY = 0;
	public static final int TYPE_STRING = 1;
	public static final int TYPE_FLOAT = 2;
	public static final int TYPE_DECIMAL = 3;
	public static final int TYPE_DATE_TIME = 4;
	public static final int TYPE_BOOLEAN = 5;
	public static final int TYPE_INTEGER = 6;
	public static final int TYPE_DATE = 7;
	public static final int TYPE_TIME = 8;

	public static final String PARAM_TYPE_SIMPLE = "simple";
	public static final String PARAM_TYPE_MULTI = "multi-value";
	public static final String PARAM_TYPE_ADHOC = "ad-hoc";

	private String name;
	private String promptText;
	private String displayName;
	private String defaultValue;
	private int controlType;
	private int dataType;
	private List<String> selectedValues = new ArrayList<String>();

	private String paramType;

	private Integer vanillaRepositoryParemeterId;

	private LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();

	private boolean isRequired = true;
	private boolean isHidden = false;
	private boolean allowNewValues = false;

	public VanillaParameter() {
	}

	public VanillaParameter(String parameterName, String singleValue) {
		setName(parameterName);
		addSelectedValue(singleValue);
	}

	public VanillaParameter(String parameterName, List<String> selectedValues) {
		setName(parameterName);
		setSelectedValues(selectedValues);
	}

	/**
	 * 
	 * @return the id of the matching VanillaParameter stored in the repository.
	 * 
	 *         We dont bother with the repositoryId because it is implicit to
	 *         the current execution context (its the same as the origin of the
	 *         model you are interested in)
	 */
	public Integer getVanillaParameterId() {
		return vanillaRepositoryParemeterId;
	}

	public void setVanillaParameterId(Integer parameterId) {
		this.vanillaRepositoryParemeterId = parameterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamType() {
		return paramType;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setControlType(int controlType) {
		this.controlType = controlType;
	}

	public int getControlType() {
		return controlType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public int getDataType() {
		return dataType;
	}

	/**
	 * 
	 * @param values
	 * @deprecated do no use this method, use instead addValue() for each couple
	 *             label-value
	 */
	public void setValues(LinkedHashMap<String, String> values) {
		this.values = values;
	}

	public LinkedHashMap<String, String> getValues() {
		return values;
	}

	public void addValue(String label, String value) {
		if (label == null) {
			this.values.put(value, value);
		}
		else {
			this.values.put(label, value);
		}

	}

	/**
	 * this seletec Values List is replaced then addSelectedValue is ccalled for
	 * each values
	 * 
	 * @param selectedValues
	 * @deprecated do not use this, use addSelectedValue for each label you want
	 *             to select
	 */
	public void setSelectedValues(List<String> selectedValues) {
		this.selectedValues = selectedValues;
		for (String s : selectedValues) {
			addSelectedValue(s);
		}
	}

	public List<String> getSelectedValues() {

		return selectedValues;
	}

	/**
	 * add the Value in Selected Value if it is not yet within
	 * 
	 * If their is no matching within the HashMap values, it add the value as
	 * Key/Value
	 * 
	 * @param s
	 */
	public void addSelectedValue(String s) {
		if (selectedValues == null) {
			this.selectedValues = new ArrayList<String>();
		}
		if (!this.selectedValues.contains(s)) {
			this.selectedValues.add(s);
		}

		if (this.values.get(s) == null) {
//			this.values.put(s, s);
		}

	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setAllowNewValues(boolean allowNewValues) {
		this.allowNewValues = allowNewValues;
	}
	
	public boolean allowNewValues() {
		return allowNewValues;
	}
}
