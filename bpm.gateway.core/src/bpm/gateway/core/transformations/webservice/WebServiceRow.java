package bpm.gateway.core.transformations.webservice;

import java.util.HashMap;

public class WebServiceRow {

	private HashMap<String, String> columnValues;

	public void addColumnValue(String name, String value) {
		if(columnValues == null){
			columnValues = new HashMap<String, String>();
		}
		columnValues.put(name, value);
	}

	public HashMap<String, String> getColumnValues() {
		return columnValues;
	}

}