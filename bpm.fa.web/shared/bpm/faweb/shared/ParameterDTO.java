package bpm.faweb.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ParameterDTO implements IsSerializable {
	
	private String name;
	private String value;
	private String level;
	
	public ParameterDTO() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	
	
	
}
