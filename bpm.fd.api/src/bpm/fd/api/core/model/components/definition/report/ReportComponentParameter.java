package bpm.fd.api.core.model.components.definition.report;

import bpm.fd.api.core.model.components.definition.ComponentParameter;

public class ReportComponentParameter extends ComponentParameter{

	private String name;
	
	public ReportComponentParameter(String name, int indice) {
		super(indice);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
