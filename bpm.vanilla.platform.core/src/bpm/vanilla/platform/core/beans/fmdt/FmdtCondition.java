package bpm.vanilla.platform.core.beans.fmdt;

import java.util.HashMap;

public class FmdtCondition extends FmdtData{
//	private String name;
	private String dataStreamName, dataStreamElementName;
	
	private String origin;
	private String table;
	
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();	
	//private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	/*
	
	public FmdtCondition(String name, String dataStreamName,
			String dataStreamElementName, String origin, String table,
			HashMap<String, Boolean> granted, HashMap<Locale, String> outputName) {
		super();
		this.name = name;
		this.dataStreamName = dataStreamName;
		this.dataStreamElementName = dataStreamElementName;
		this.origin = origin;
		this.table = table;
		this.granted = granted;
//		this.outputName = outputName;
	}
	*/
	/*
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	*/
	public String getDataStreamName() {
		return dataStreamName;
	}
	public void setDataStreamName(String dataStreamName) {
		this.dataStreamName = dataStreamName;
	}
	public String getDataStreamElementName() {
		return dataStreamElementName;
	}
	public void setDataStreamElementName(String dataStreamElementName) {
		this.dataStreamElementName = dataStreamElementName;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public HashMap<String, Boolean> getGranted() {
		return granted;
	}
	public void setGranted(HashMap<String, Boolean> granted) {
		this.granted = granted;
	}
	/*
	public HashMap<Locale, String> getOutputName() {
		return outputName;
	}
	public void setOutputName(HashMap<Locale, String> outputName) {
		this.outputName = outputName;
	}
*/
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmdtCondition other = (FmdtCondition) obj;
		if (dataStreamElementName == null) {
			if (other.dataStreamElementName != null)
				return false;
		}
		else if (!dataStreamElementName.equals(other.dataStreamElementName))
			return false;
		if (dataStreamName == null) {
			if (other.dataStreamName != null)
				return false;
		}
		else if (!dataStreamName.equals(other.dataStreamName))
			return false;
		if (granted == null) {
			if (other.granted != null)
				return false;
		}
		else if (!granted.equals(other.granted))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		}
		else if (!origin.equals(other.origin))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		}
		else if (!table.equals(other.table))
			return false;
		return true;
	}		
}
