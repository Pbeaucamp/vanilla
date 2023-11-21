package bpm.database.ui.viewer.relations.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;



public class Node {
	public static final String PROPERTY_LAYOUT = "NodeLayout";
	
	private List<Node> children;
	private List<JoinConnection> sourceConnections = new ArrayList<JoinConnection>();
	private List<JoinConnection> targetConnections = new ArrayList<JoinConnection>();

	
	private Rectangle layout;
	private PropertyChangeSupport listeners;
	private Node parent;
	private String name;
	
	
	public Node() {
		layout = new Rectangle(10, 10, 100, 100);
		children = new ArrayList<Node>();
		parent = null;
		listeners = new PropertyChangeSupport(this);
		name = "Node";
	}

	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	public void setLayout(int x, int y, int width, int height) {
		setLayout(new Rectangle(x, y, width, height));
	}

	public void setLayout(Rectangle newLayout) {
		Rectangle oldLayout = layout;
		layout = newLayout;
		getListeners()
				.firePropertyChange(PROPERTY_LAYOUT, oldLayout, newLayout);
	}

	public Rectangle getLayout() {
		return layout;
	}

	public boolean addChild(Node child) {
		boolean b = children.add(child);
		if (b) {
			child.setParent(this);
			String bigest = "";
			for(Node n : children){
				if (bigest.length() < n.getName().length()){
					bigest = new String(n.getName());
				}
			}
			
			layout.width = (bigest ).length() * 10;
			layout.height = (children.size() + 4) * 15; 
		}
		
		
		
		return b;
	}

	

	public List<Node> getChildrenArray() {
		return children;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	public List<Node> getChildren() {
		return children;
	}

	public int getChildrenNumber() {
		return children.size() + 1;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public PropertyChangeSupport getListeners() {
		return listeners;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public boolean contains(Node child) {
		return children.contains(child);
	}

	

	public void addConnection(JoinConnection conn) {
		if (conn == null || conn.getSource() == conn.getTarget()) {

			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			sourceConnections.add(conn);
			
		} else if (conn.getTarget() == this) {

			targetConnections.add(conn);
			

		}
	}

	

	public List<JoinConnection> getSourceConnections() {
		return new ArrayList<JoinConnection>(sourceConnections);
	}

	public List<JoinConnection> getAllConnections(){
		List<JoinConnection> list = new ArrayList<JoinConnection>();
		
		return list;
	}
	
	public List<JoinConnection> getTargetConnections() {
		return new ArrayList<JoinConnection>(targetConnections);
	}

	
	
}
