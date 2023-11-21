package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.HashMap;

public class SearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private HashMap<Integer, String> fieldValues = new HashMap<Integer, String>();

	public HashMap<Integer, String> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(HashMap<Integer, String> fieldValues) {
		this.fieldValues = fieldValues;
	}

	public void addFieldValue(int fieldId, String value) {
		fieldValues.put(fieldId, value);
	}
}
