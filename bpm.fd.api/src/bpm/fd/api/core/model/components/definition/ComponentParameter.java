package bpm.fd.api.core.model.components.definition;

import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.api.internal.ILabelable;

public class ComponentParameter implements ILabelable{
	private ParameterDescriptor desc;
	private int indice = -1;
	private String defaultValue;
	
	
	public ComponentParameter(ParameterDescriptor desc, int indice){
		this.desc = desc;
		this.indice = indice;
	}
	
	protected ComponentParameter(int indice){
		this.indice = indice;
	}
	
	public ParameterDescriptor getParameterDescriptor(){
		return desc;
	}
	public String getLabel(){
		return getName();
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return desc.getLabel();
	}

	public String getId(){
		return getName().replace(" ", "_");
	}

	public int getIndice(){
		return indice;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}
