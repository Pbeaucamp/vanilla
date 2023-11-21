package bpm.fa.api.item;

import java.io.Serializable;


public class Parameter implements Serializable {

	private String name;
	private String value;
	private String level;
	private String uname;
	
	public Parameter() {
		
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

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUname() {
		return uname;
	}
	
	
	
}
