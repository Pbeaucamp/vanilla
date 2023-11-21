package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

public class VanillaLocale implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String localeValue;
	private int localeOrder;

	public VanillaLocale() {
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLocaleValue(String localeValue) {
		this.localeValue = localeValue;
	}

	public String getLocaleValue() {
		return localeValue;
	}

	public void setLocaleOrder(int localeOrder) {
		this.localeOrder = localeOrder;
	}

	public int getLocaleOrder() {
		return localeOrder;
	}

}
