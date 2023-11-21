package bpm.gateway.ui.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.file.FileShape;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.ui.gef.model.properties.DataStreamProperties;
import bpm.gateway.ui.gef.model.properties.FileCSVProperties;
import bpm.gateway.ui.gef.model.properties.FileShapeProperties;
import bpm.gateway.ui.gef.model.properties.FileWekaProperties;
import bpm.gateway.ui.gef.model.properties.FileXLSProperties;
import bpm.gateway.ui.gef.model.properties.FileXMLProperties;
import bpm.gateway.ui.i18n.Messages;

public class Node implements IAdaptable {
	/*
	 * Constants for events
	 */
	public static final String PROPERTY_LAYOUT = "layout"; //$NON-NLS-1$
	// public static final String PROPERTY_ADD_CHILD = "newChild";
	// public static final String PROPERTY_REMOVE_CHILD = "removeChild";
	public static final String PROPERTY_RENAME = "rename"; //$NON-NLS-1$
	public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String SOURCE_CONNECTIONS_PROP = "nodeSourceLink"; //$NON-NLS-1$
	public static final String TARGET_CONNECTIONS_PROP = "nodeTargetLink"; //$NON-NLS-1$

	private PropertyChangeSupport listeners;

	private String name;
	private String description;

	/*
	 * not used for now
	 */
	private int type;

	private Rectangle layout;

	// private List<Node> childs = new ArrayList<Node>();
	// private Node parent;

	private List<Link> sourceLinks = new ArrayList<Link>();
	private List<Link> targetLinks = new ArrayList<Link>();

	/*
	 * Contains a GatewayObject
	 */
	private Transformation modelObject;

	public Node() {
		layout = new Rectangle(10, 10, 50, 50);
		this.listeners = new PropertyChangeSupport(this);

	}

	/**
	 * Define the model object associated to this Node This method must be
	 * called only when creating a new Node
	 * 
	 * @param modelObject
	 * @throws NodeException
	 *             if you try to set a null gatewayObject
	 * @throws NodeException
	 *             if you try to change the modelObject
	 */
	public void setTransformation(Transformation modelObject) throws NodeException {
		if (this.modelObject != null) {
			throw new NodeException(Messages.Node_5);
		}

		if (modelObject == null) {
			throw new NodeException(Messages.Node_6);
		}

		this.modelObject = modelObject;
	}

	/**
	 * 
	 * @return the model object represented by this Node
	 */
	public Transformation getGatewayModel() {
		return modelObject;
	}

	/**
	 * add a listener
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * remove the listener
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	/**
	 * 
	 * @return the listeners
	 */
	public PropertyChangeSupport getListeners() {
		return listeners;
	}

	/**
	 * @return the layout
	 */
	public final Rectangle getLayout() {
		return layout;
	}

	/**
	 * @param layout
	 *            the layout to set and fire an event
	 */
	public final void setLayout(Rectangle layout) {
		Rectangle oldValue = this.layout;
		this.layout = layout;
		getListeners().firePropertyChange(PROPERTY_LAYOUT, oldValue, layout);

		if (modelObject != null) {
			((Transformation) modelObject).setPositionX(layout.x);
			((Transformation) modelObject).setPositionY(layout.y);
		}
	}

	/**
	 * @return the x
	 */
	public final int getX() {
		return layout.x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.layout.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return layout.y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.layout.y = y;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		boolean refresh = false;
		if (modelObject != null) {
			if (!name.equals(modelObject.getName())) {
				modelObject.setName(name);
				refresh = true;
			}

			this.name = name;
		}

		if (refresh) {
			getListeners().firePropertyChange(PROPERTY_RENAME, oldValue, name);
		}

	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public final void setDescription(String description) {
		String oldValue = this.description;
		this.description = description;

		if (modelObject != null) {
			modelObject.setDescription(description);
		}

		getListeners().firePropertyChange(PROPERTY_DESCRIPTION, oldValue, name);
	}

	/**
	 * @return the type
	 */
	public final int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public final void setType(int type) {
		this.type = type;
	}

	// public boolean addChild(Node child) {
	// child.setParent(this);
	// boolean result = this.childs.add(child);
	// getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, child);
	// return result ;
	// }

	// public boolean removeChild(Node child) {
	// return this.childs.remove(child);
	// }

	// public List<Node> getChildren() {
	// return this.childs;
	// }
	//	
	// public void setParent(Node parent) {
	// this.parent = parent;
	// }
	//	
	// public Node getParent() {
	// return this.parent;
	// }

	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return this;
		}

		if (adapter == IPropertySource.class) {

			if (modelObject instanceof FileOutputWeka) {
				return new FileWekaProperties(this);
			}
			if (modelObject instanceof FileOutputCSV || modelObject instanceof FileInputCSV) {
				return new FileCSVProperties(this);
			}
			if (modelObject instanceof FileXML) {
				return new FileXMLProperties(this);
			}
			if (modelObject instanceof FileXLS) {
				return new FileXLSProperties(this);
			}
			if (modelObject instanceof FileShape) {
				return new FileShapeProperties(this);
			}

			if (modelObject instanceof DataStream) {
				return new DataStreamProperties(this);
			}

			if (modelObject instanceof SimpleMappingTransformation) {
				return new MapperTransformationProperties(this);
			}

			return new GatewayObjectProperties(this);
		}

		return null;

	}

	// /////////////////////////////////////////////////////////
	// for connections
	// /////////////////////////////////////////////////////////

	public void addLink(Link conn) throws Exception {
		if (conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}

		NodeLinkerHelper.add(conn);
		if (conn.getSource() == this && !sourceLinks.contains(conn)) {
			sourceLinks.add(conn);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
		}
		else if (conn.getTarget() == this && !targetLinks.contains(conn)) {
			targetLinks.add(conn);
			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);

		}

	}

	public void removeLink(Link conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		// NodeLinkerHelper.remove(conn);
		if (conn.getSource() == this) {
			sourceLinks.remove(conn);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
		}
		else if (conn.getTarget() == this) {
			targetLinks.remove(conn);
			getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
		}

		// NodeLinkerHelper.remove(conn);
	}

	public List<Link> getSourceLink() {
		return sourceLinks;
	}

	public List<Link> getTargetLink() {
		return targetLinks;
	}

}
