package bpm.gateway.core.transformations.webservice;

import bpm.vanilla.platform.core.beans.service.IService;

public class WebServiceParameter implements IService{

	private String name;
	private int type;
	private String value;

	public WebServiceParameter() {
	}

	public WebServiceParameter(String name, int type) {
		this.name = name;
		this.setType(type);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value != null ? value : "";
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = Integer.parseInt(type);
	}

	public int getType() {
		return type;
	}

}