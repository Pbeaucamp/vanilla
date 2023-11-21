package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

public class FmdtDrillerFilter implements Serializable {

	private static final long serialVersionUID = -7484924989908374696L;
	
	private String name;
	private String origin;
	private String value;
	
	public FmdtDrillerFilter() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return origin + " -> " + value;
	}
}
