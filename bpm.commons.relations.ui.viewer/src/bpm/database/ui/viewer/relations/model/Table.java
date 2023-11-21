package bpm.database.ui.viewer.relations.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

public class Table extends Node{

	private Object table;
	private Color color;

	public void setBTable(Object table) {
		this.table = table;
	}
	
	public Object getBTable() {
		return table;
	}
	
	public List<JoinConnection> getChildrenSourceConnections() {
		List<JoinConnection> list = new ArrayList<JoinConnection>();
		for(Node child : getChildren()){
			if (!child.getSourceConnections().isEmpty())
				list.addAll(child.getSourceConnections());
		}
		return list;
	}

	public List<JoinConnection> getAllChildrenConnections(){
		List<JoinConnection> list = new ArrayList<JoinConnection>();
		list.addAll(getChildrenSourceConnections());
		list.addAll(getChildrenTargetConnections());
		
		return list;
	}
	
	public List<JoinConnection> getChildrenTargetConnections() {
		List<JoinConnection> list = new ArrayList<JoinConnection>();
		for(Node child : getChildren()){
			if (!child.getTargetConnections().isEmpty())
				list.addAll(child.getTargetConnections());
		}
		return list;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
 

}
