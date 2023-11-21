package bpm.android.vanilla.core.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Parameter {
	private String paramName;
	
	private LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
	private String selectedValue;
	
	private Parameter paramChild;
	private Parameter paramParent;
	
	public Parameter(){	}
	
	public Parameter(String paramName, LinkedHashMap<String, String> values){
		this.paramName = paramName;
		this.values = values;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamName() {
		return paramName;
	}
	
	public void setValues(LinkedHashMap<String, String> values) {
		this.values = values;
	}
	
	public LinkedHashMap<String, String> getValues() {
		return values;
	}

	public Parameter getParamChild() {
		return paramChild;
	}

	public void setParamChild(Parameter paramChild) {
		this.paramChild = paramChild;
	}

	public boolean hasParent() {
		return paramParent != null;
	}

	public Parameter getParamParent() {
		return paramParent;
	}

	public void setParamParent(Parameter paramParent) {
		this.paramParent = paramParent;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}
	
	public List<String> getValuesAsList() {
		if(values != null) {
			List<String> listValues = new ArrayList<String>();
			for(String value : values.values()) {
				listValues.add(value);
			}
			return listValues;
		}

		return null;
	}
}
