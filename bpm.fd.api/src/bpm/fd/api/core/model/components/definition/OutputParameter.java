package bpm.fd.api.core.model.components.definition;

import bpm.fd.api.core.model.datas.ParameterDescriptor;

public class OutputParameter extends ComponentParameter{

	private String name;
	public OutputParameter(int indice, String name) {
		super(indice);
		this.name = name;
		
	}

	/* (non-Javadoc)
	 * @see bpm.fd.api.core.model.components.definition.ComponentParameter#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see bpm.fd.api.core.model.components.definition.ComponentParameter#getParameterDescriptor()
	 */
	@Override
	public ParameterDescriptor getParameterDescriptor() {
		return null;
	}
	
	

}
