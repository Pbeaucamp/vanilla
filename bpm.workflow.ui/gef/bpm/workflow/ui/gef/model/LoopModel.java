package bpm.workflow.ui.gef.model;

import java.util.ArrayList;
import java.util.List;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.WorkflowException;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.LoopGlobale;

public class LoopModel extends Node {

	private int width;
	private int height;
	private WorkflowModel workflowModel;
	private List<Node> contents = new ArrayList<Node>();

	public static final String PROPERTY_ADD_CHILD = "newChild"; //$NON-NLS-1$
	public static final String PROPERTY_REMOVE_CHILD = "removeChild"; //$NON-NLS-1$

	public LoopModel() {
		super();

	}

	public LoopModel(WorkflowModel workflowInput) {

		super();
		workflowModel = workflowInput;

	}

	public String getName() {
		return super.getName();
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		for(Node n : contents) {
			n.getWorkflowObject().setParent(name);
		}
	}

	public void setWidth(int width) {
		this.width = width;
		((LoopGlobale) getWorkflowObject()).setPositionWidth(width);
		getListeners().firePropertyChange(PROPERTY_LAYOUT, null, width);

	}

	public void setHeight(int height) {
		this.height = height;
		((LoopGlobale) getWorkflowObject()).setPositionHeight(height);
		getListeners().firePropertyChange(PROPERTY_LAYOUT, null, height);

	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public final void setX(int x) {
		super.setX(x);
		((LoopGlobale) getWorkflowObject()).setPositionX(x);
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public final void setY(int y) {
		super.setY(y);
		((LoopGlobale) getWorkflowObject()).setPositionY(y);
	}

	public List<Node> getContents() {
		return new ArrayList<Node>(contents);
	}

	/**
	 * @param newNode
	 */
	public void addChild(Node t) {
		boolean isAlready = false;
		for(IActivity activitipresente : workflowModel.getActivities().values()) {
			if(activitipresente.getName().equalsIgnoreCase(t.getName())) {
				isAlready = true;
			}
		}
		if(!isAlready) {
			contents.add(t);

			try {
				workflowModel.addActivity((IActivity) t.getWorkflowObject());

			} catch(WorkflowException e) {

				e.printStackTrace();
			}
			getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, t);
		}
	}

	public void addChild(Node t, String toto) {
		contents.add(t);

		getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, t);

	}

	public boolean removeChild(Node child) {

		boolean b = this.contents.remove(child);
		if(b) {
			try {
				if(child.getWorkflowObject() instanceof IActivity) {

					workflowModel.removeActivity(child.getWorkflowObject().getName());

					getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, null);
				}
			} catch(WorkflowException e) {
				e.printStackTrace();
			}

		}

		return b;

	}

}
