package bpm.sqldesigner.api.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import bpm.sqldesigner.api.database.DataBaseConnection;

public class Node {

	public static final String SOURCE_CONNECTIONS_PROP = "nodeSourceLink";
	public static final String TARGET_CONNECTIONS_PROP = "nodeTargetLink";
	public static final String PROPERTY_LAYOUT = "NodeLayout";
	public static final String PROPERTY_ADD = "NodeAddChild";
	public static final String PROPERTY_REMOVE = "NodeRemoveChild";
	public static final String FOCUS = "NodeFocus";

	protected String name;
	boolean isNotFullLoaded = true; // children and not full loaded
	boolean isNotLoaded = true; // First level loaded
	public PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private RectangleLayout layout = new RectangleLayout();
	private boolean isCommit = true;

	private class RectangleLayout {
		public int x = 0;
		public int y = 0;
		public int width = 0;
		public int height = 0;

		public int[] getValues() {
			return new int[] { x, y, width, height };
		}

		public boolean isNull() {
			return x == 0 && y == 0 && width == 0 && height == 0;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getChildren() {
		return null;
	}

	public Node getParent() {
		return null;
	}

	public DataBaseConnection getDatabaseConnection() {
		if (getParent() != null)
			return getParent().getDatabaseConnection();
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.toString().equals(toString());
	}

	public String getClusterName() {
		return "";
	}

	public boolean isNotLoaded() {
		return isNotLoaded;
	}

	public void setNotFullLoaded(boolean isNotFullLoaded) {
		if (!isNotFullLoaded)
			isNotLoaded = false;
		this.isNotFullLoaded = isNotFullLoaded;
	}

	public void setNotLoaded(boolean isNotLoaded) {
		this.isNotLoaded = isNotLoaded;
	}

	public boolean isNotFullLoaded() {
		Object[] children = getChildren();

		if (children != null) {

			int length = children.length;
			Object[] objs = getChildren();

			if (isNotLoaded)
				isNotFullLoaded = true;

			for (int i = 0; i < length && !isNotFullLoaded; i++) {
				Node node = (Node) objs[i];
				isNotFullLoaded = node.isNotFullLoaded();
			}
			return isNotFullLoaded;
		}
		isNotFullLoaded = false;
		return isNotFullLoaded;
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

	public void setLayout(int x, int y, int width, int height) {
		int[] oldLayout = layout.getValues();
		layout.x = x;
		layout.y = y;
		layout.width = width;
		layout.height = height;
		getListeners().firePropertyChange(PROPERTY_LAYOUT, oldLayout,
				layout.getValues());
	}

	public int[] getLayout() {
		return layout.getValues();
	}

	public DatabaseCluster getCluster() {
		return getParent().getCluster();
	}

	public boolean isCommit() {
		return isCommit;
	}

	public void setCommit(boolean isCommit) {
		this.isCommit = isCommit;
	}

	public void updateName(Node node, String oldName) {

	}

	public boolean layoutIsNull() {
		return layout.isNull();
	}
	
	public String dumpName(){
		String s = getName();
		Node parent = getParent();
		
		while(parent != null){
			s = parent.getName() + "/" + s;
			parent = parent.getParent();
		}
		
		return s;
	}
}
