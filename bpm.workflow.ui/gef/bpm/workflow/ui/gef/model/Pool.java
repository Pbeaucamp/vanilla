package bpm.workflow.ui.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.dialogs.MessageDialog;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IManual;
import bpm.workflow.runtime.model.PoolModel;
import bpm.workflow.runtime.model.WorkflowException;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkflowObject;
import bpm.workflow.runtime.model.activities.LoopGlobale;
import bpm.workflow.runtime.model.activities.MacroProcess;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

public class Pool implements IAdaptable {

	/*
	 * Constants for events
	 */
	public static final String PROPERTY_LAYOUT = "layout"; //$NON-NLS-1$
	public static final String PROPERTY_RENAME = "rename"; //$NON-NLS-1$
	public static final String PROPERTY_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String PROPERTY_ADD_CHILD = "newChild"; //$NON-NLS-1$
	public static final String PROPERTY_REMOVE_CHILD = "removeChild"; //$NON-NLS-1$

	private PropertyChangeSupport listeners;

	private String name;
	private String description;

	private Rectangle layout;

	private List<Node> childs = new ArrayList<Node>();

	/*
	 * Contains a PoolModel
	 */
	private PoolModel poolModel;
	private WorkflowModel workflowModel;

	public Pool(WorkflowModel workflowModel) {
		layout = new Rectangle(10, 10, 400, 50);
		this.listeners = new PropertyChangeSupport(this);
		this.workflowModel = workflowModel;
	}

	public void setPoolModel(PoolModel poolModel) throws Exception {
		if(this.poolModel != null) {
			throw new Exception(Messages.Pool_5);
		}
		if(poolModel == null) {
			throw new NodeException(Messages.Pool_6);
		}

		this.poolModel = poolModel;

		for(IActivity a : poolModel.getChildrens()) {
			if(a instanceof LoopGlobale) {
				LoopModel loop = new LoopModel(workflowModel);
				loop.setWorkflowObject((LoopGlobale) a);
				loop.setName(a.getName());
				loop.setX(a.getPositionX());
				loop.setY(a.getPositionY());
				loop.setWidth(((LoopGlobale) a).getPositionWidth());
				loop.setHeight(((LoopGlobale) a).getPositionHeight());
				this.childs.add(loop);

			}
			else if(a instanceof MacroProcess) {
				MacroProcessModel loop = new MacroProcessModel(workflowModel);
				loop.setWorkflowObject((MacroProcess) a);
				loop.setName(a.getName());
				loop.setX(a.getPositionX());
				loop.setY(a.getPositionY());
				loop.setWidth(((MacroProcess) a).getPositionWidth());
				loop.setHeight(((MacroProcess) a).getPositionHeight());
				this.childs.add(loop);

			}
			else {
				Node n = new Node();
				n.setX(a.getPositionX());
				n.setY(a.getPositionY());
				n.setWorkflowObject((WorkflowObject) a);
				n.setName(a.getName());

				this.childs.add(n);
			}
		}

	}

	public PoolModel getPoolModel() {
		return this.poolModel;
	}

	public List getChildren() {
		List l = new ArrayList();
		l.addAll(childs);
		return l;
	}

	public boolean addChild(Node child) {
		if(contains(child)) {
			return false;
		}
		boolean b = this.childs.add(child);
		if(b) {

			try {
				if(child.getWorkflowObject() instanceof IActivity) {
					poolModel.addChild((IActivity) child.getWorkflowObject());
				}
				else {
					this.childs.remove(child);
					return false;
				}

			} catch(Exception e) {
				e.printStackTrace();
				this.childs.remove(child);
				return false;
			}
			child.setName(child.getWorkflowObject().getName());

			getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, child);
		}
		return b;
	}

	public boolean addNewChild(Node child) {
		if(contains(child)) {
			return false;
		}

		try {
			if(child.getWorkflowObject() instanceof IManual) {
				((IManual) child.getWorkflowObject()).setGroupForValidation(this.name);
			}
			else if(!(child.getWorkflowObject() instanceof IManual) && !this.name.equalsIgnoreCase(PoolModel.PROCESS)) {
				throw new WorkflowException(Messages.Pool_7);
			}
			workflowModel.addActivity((IActivity) child.getWorkflowObject());
			this.childs.add(child);
			getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, child);
			return true;
		} catch(WorkflowException e) {
			MessageDialog.openError(Activator.getDefault().getWorkbench().getDisplay().getShells()[0], Messages.Pool_8, e.getMessage());
			return false;
		}

	}

	public boolean removeChild(Node child) {
		if(!contains(child)) {
			return false;
		}
		else {
			try {
				poolModel.removeChild((IActivity) child.getWorkflowObject());
				this.childs.remove(child);
				getListeners().firePropertyChange(PROPERTY_REMOVE_CHILD, child, null);
				return true;
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private boolean contains(Node child) {
		return childs.contains(child);
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

		if(poolModel != null) {
			poolModel.setPositionX(layout.x);
			poolModel.setPositionY(layout.y);
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
	public final void setX(int x) {
		this.layout.x = x;
	}

	/**
	 * @return the y
	 */
	public final int getY() {
		return layout.y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public final void setY(int y) {
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

		if(poolModel != null) {
			poolModel.setName(name);
			this.name = name;
		}

		getListeners().firePropertyChange(PROPERTY_RENAME, oldValue, name);
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
	public void setDescription(String desc) {
		this.description = desc;
		if(poolModel != null) {
			poolModel.setDescription(desc);
		}

		getListeners().firePropertyChange(PROPERTY_DESCRIPTION, null, null);
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}
