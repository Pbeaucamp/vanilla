package bpm.vanillahub.core.beans.resources.attributes;

import java.io.Serializable;

public class WebServiceDefinition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String portName;
	private String bindingName;
	private String location;
	
	public WebServiceDefinition() { }

	public WebServiceDefinition(String portName, String bindingName, String location) {
		this.portName = portName;
		this.bindingName = bindingName;
		this.location = location;
	}

	public String getPortName() {
		return portName;
	}
	
	public String getBindingName() {
		return bindingName;
	}
	
	public String getLocation() {
		return location;
	}
}
