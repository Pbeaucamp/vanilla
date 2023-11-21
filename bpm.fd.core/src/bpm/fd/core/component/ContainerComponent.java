package bpm.fd.core.component;

import java.io.Serializable;
import java.util.List;

import bpm.fd.core.DashboardComponent;

public class ContainerComponent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<DashboardComponent> components;
	
	public ContainerComponent() { }
	
	public ContainerComponent(List<DashboardComponent> components) {
		this.components = components;
	}
	
	public void setComponents(List<DashboardComponent> components) {
		this.components = components;
	}
	
	public List<DashboardComponent> getComponents() {
		return components;
	}
}
