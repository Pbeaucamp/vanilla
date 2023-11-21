package bpm.fd.core;

import java.io.Serializable;

public class ComponentParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	private DashboardComponent provider;
	private int index;
	private String defaultValue;
	private String name;

	public DashboardComponent getProvider() {
		return provider;
	}

	public void setProvider(DashboardComponent provider) {
		this.provider = provider;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
