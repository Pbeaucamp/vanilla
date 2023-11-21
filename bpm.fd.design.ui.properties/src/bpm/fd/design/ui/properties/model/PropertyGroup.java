package bpm.fd.design.ui.properties.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;

import bpm.fd.design.ui.properties.model.chart.PropertyColor;

public class PropertyGroup extends Property{
	private List<Property> properties = new ArrayList<Property>();
	
	public PropertyGroup(String name){
		super(name, null);
	}
	
	public PropertyGroup(String name, CellEditor editor){
		super(name, editor);
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	/**
	 * @return the properties
	 */
	public List<Property> getProperties() {
		return properties;
	}
	
	public void add(Property prop){
		properties.add(prop);
		prop.setParent(this);
	}

	public void clear() {
		for(Property p : properties){
			p.setParent(null);
			if(p instanceof PropertyColor) {
				((PropertyColor)p).getCellEditor().dispose();
			}
		}
		
		properties.clear();
	}

	public void remove(Property p) {
		if(p instanceof PropertyColor) {
			((PropertyColor)p).getCellEditor().dispose();
		}
		properties.remove(p);
	}
}
