package bpm.sqldesigner.query.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

import bpm.sqldesigner.query.model.connection.ConnectionsManager;
import bpm.sqldesigner.query.model.connection.JoinConnection;

public class Node {

	private String name;
	private Rectangle layout;
	private List<Node> children;
	private Node parent;
	private PropertyChangeSupport listeners;
	public static final String PROPERTY_LAYOUT = "NodeLayout";
	public static final String PROPERTY_ADD = "NodeAddChild";
	public static final String PROPERTY_REMOVE = "NodeRemoveChild";
	public static final String SOURCE_CONNECTIONS_PROP = "nodeSourceLink";
	public static final String TARGET_CONNECTIONS_PROP = "nodeTargetLink";
	public static final String CONNECTIONS_PROP = "Nodelinks";
	public static final String REMOVE = "NodeRemove";
	private List<JoinConnection> sourceConnections = new ArrayList<JoinConnection>();
	private List<JoinConnection> targetConnections = new ArrayList<JoinConnection>();

	public Node() {
		layout = new Rectangle(10, 10, 100, 100);
		children = new ArrayList<Node>();
		parent = null;
		listeners = new PropertyChangeSupport(this);
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
		for(Node n : children){
			if (n.getName().equals(child.getName())){
				return false;
			}
		}
		boolean b = children.add(child);
		if (b) {
			child.setParent(this);
			getListeners().firePropertyChange(PROPERTY_ADD, null, child);
		}
		return b;
	}

	public boolean removeChild(Node child) {
		boolean b = children.remove(child);
		if (b)
			getListeners().firePropertyChange(PROPERTY_REMOVE, child, null);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addConnection(JoinConnection conn) {
		if (conn == null || conn.getSource() == conn.getTarget()) {

			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			sourceConnections.add(conn);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null,
					conn);
		} else if (conn.getTarget() == this) {

			targetConnections.add(conn);
			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null,
					conn);

		}
	}

	public void removeConnection(JoinConnection conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			sourceConnections.remove(conn);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null,
					conn);
		} else if (conn.getTarget() == this) {
			targetConnections.remove(conn);
			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null,
					conn);
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

	public void removeAllConnection() {
		Iterator<JoinConnection> it = sourceConnections.iterator();
		while (it.hasNext()) {
			JoinConnection j = it.next();
			j.getTarget().removeConnection(j);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null, j);
			ConnectionsManager.getInstance().removeConnection(j);
		}
		sourceConnections.clear();

		Iterator<JoinConnection> it2 = targetConnections.iterator();
		while (it2.hasNext()) {
			JoinConnection j = it2.next();
			j.getSource().removeConnection(j);
			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null, j);
			ConnectionsManager.getInstance().removeConnection(j);
		}
		targetConnections.clear();
	}

	public void fireRemoveOrAdd() {
		getListeners().firePropertyChange(REMOVE, null, null);
	}

}