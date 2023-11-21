package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserRunConfigurationParameter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private List<String> values;
	
	public UserRunConfigurationParameter() {
		
	}
	
	public UserRunConfigurationParameter(String name, List<String> values) {
		this.name = name;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public void addValue(String value) {
		if(this.values == null) {
			this.values = new ArrayList<String>();
		}
		this.values.add(value);
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
