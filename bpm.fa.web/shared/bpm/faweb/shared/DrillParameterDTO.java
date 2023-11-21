package bpm.faweb.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DrillParameterDTO implements IsSerializable {

	private String name;
	private String dimension;
	private String level;
	private String value;
	private List<String> possibleValues;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDimension() {
		return dimension;
	}
	
	public void setDimension(String dimension) {
		this.dimension = dimension;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public void setPossibleValues(List<String> possibleValues) {
		this.possibleValues = possibleValues;
	}

	public List<String> getPossibleValues() {
		return possibleValues;
	}
}
