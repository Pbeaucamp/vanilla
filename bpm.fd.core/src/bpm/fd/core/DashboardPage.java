package bpm.fd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DashboardPage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String label;

	private List<DashboardComponent> components;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<DashboardComponent> getComponents() {
		return components;
	}

	public void setComponents(List<DashboardComponent> components) {
		this.components = components;
	}

	public void addComponent(DashboardComponent component) {
		if(components == null) {
			components = new ArrayList<DashboardComponent>();
		}
		components.add(component);
	}
	
	public void removeComponent(DashboardComponent component) {
		components.remove(component);
	}
}
