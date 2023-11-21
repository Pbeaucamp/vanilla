package bpm.database.ui.viewer.relations.model;

import org.eclipse.swt.graphics.Color;


public class Column extends Node{

	private Object col;
	private boolean exists = true;
	private Color color;

	public void setDatastreamElement(Object col) {
		this.col = col;
	}

	public Object getDatastreamElement() {
		return col;
	}
	
	public void setExists(boolean exists) {
		this.exists  = exists;
	}

	public boolean exists() {
		return exists;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	
}
