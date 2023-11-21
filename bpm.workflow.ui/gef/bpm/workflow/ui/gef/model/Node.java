package bpm.workflow.ui.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.WorkflowObject;
import bpm.workflow.runtime.model.activities.Comment;
import bpm.workflow.ui.Messages;

public class Node implements IAdaptable {

	/*
	 * Constants for events
	 */
	public static final String PROPERTY_LAYOUT = "layout"; //$NON-NLS-1$
	public static final String PROPERTY_RENAME = "rename"; //$NON-NLS-1$
	public static final String PROPERTY_CHANGE_TYPE = "type"; //$NON-NLS-1$
	public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String SOURCE_CONNECTIONS_PROP = "nodeSourceLink"; //$NON-NLS-1$
	public static final String TARGET_CONNECTIONS_PROP = "nodeTargetLink"; //$NON-NLS-1$

	private PropertyChangeSupport listeners;

	private String description;

	/*
	 * not used for now
	 */
	private int type;

	private Rectangle layout;

	private List<Link> sourceLinks = new ArrayList<Link>();
	private List<Link> targetLinks = new ArrayList<Link>();

	/*
	 * Contains a WorkflowObject
	 */
	private WorkflowObject modelObject;

	public Node() {
		layout = new Rectangle(10, 10, 50, 50);
		this.listeners = new PropertyChangeSupport(this);

	}

	/**
	 * Define the model object associated to this Node This method must be called only when creating a new Node
	 * 
	 * @param modelObject
	 * @throws NodeException
	 *             if you try to set a null gatewayObject
	 * @throws NodeException
	 *             if you try to change the modelObject
	 */
	public void setWorkflowObject(WorkflowObject modelObject) throws NodeException {
		if(this.modelObject != null) {
			throw new NodeException(Messages.Node_6);
		}

		if(modelObject == null) {
			throw new NodeException(Messages.Node_7);
		}

		this.modelObject = modelObject;
	}

	/**
	 * 
	 * @return the model object represented by this Node
	 */
	public WorkflowObject getWorkflowObject() {
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
	public final void setLayout(Rectangle layout, int translateX, int translateY) {
		Rectangle oldValue = this.layout;
		layout.x = layout.x + translateX;
		layout.y = layout.y + translateY;
		this.layout = layout;
		getListeners().firePropertyChange(PROPERTY_LAYOUT, oldValue, layout);

		if(modelObject != null) {
			modelObject.setPositionX(layout.x);
			modelObject.setPositionY(layout.y);

		}
	}

	public final void setLayout(Rectangle layout) {
		Rectangle oldValue = this.layout;
		this.layout = layout;
		getListeners().firePropertyChange(PROPERTY_LAYOUT, oldValue, layout);

		if(modelObject != null) {
			(modelObject).setPositionX(layout.x);
			(modelObject).setPositionY(layout.y);
		}
	}

	/**
	 * @return the x
	 */
	public int getX() {
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
	public String getName() {
		return modelObject.getName();
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.modelObject.setName(name);

		getListeners().firePropertyChange(PROPERTY_RENAME, null, name);
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return modelObject.getDescription();
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String desc) {
		String oldValue = this.description;
		this.description = desc;

		if(modelObject != null) {
			modelObject.setDescription(desc);
			this.description = desc;
		}

		getListeners().firePropertyChange(PROPERTY_DESCRIPTION, oldValue, desc);
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

		if(modelObject instanceof Comment) {
			((Comment) modelObject).setType(type);
			getListeners().firePropertyChange(Node.PROPERTY_CHANGE_TYPE, null, null);
		}
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void addLink(Link conn) throws Exception {
		if(conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}

		if(conn.getSource().getWorkflowObject() instanceof IActivity && conn.getTarget().getWorkflowObject() instanceof IActivity) {
			NodeLinkerHelper.add(conn);
			try {
				if(conn.getSource() == this && !sourceLinks.contains(conn)) {
					sourceLinks.add(conn);
					getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
				}
				else if(conn.getTarget() == this && !targetLinks.contains(conn)) {
					targetLinks.add(conn);
					getListeners().firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);

				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void removeLink(Link conn) {
		if(conn == null) {
			throw new IllegalArgumentException();
		}
		// NodeLinkerHelper.remove(conn);
		if(conn.getSource() == this) {
			sourceLinks.remove(conn);
			getListeners().firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
		}
		else if(conn.getTarget() == this) {
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
