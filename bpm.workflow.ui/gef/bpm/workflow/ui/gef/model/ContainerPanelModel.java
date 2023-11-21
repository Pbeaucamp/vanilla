package bpm.workflow.ui.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.Transition;
import bpm.workflow.runtime.model.WorkflowException;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkflowObject;
import bpm.workflow.runtime.model.activities.LoopGlobale;
import bpm.workflow.runtime.model.activities.MacroProcess;
import bpm.workflow.ui.editors.WorkflowEditorInput;

public class ContainerPanelModel implements IAdaptable {
	public static final String PROPERTY_ADD_CHILD = "newChild"; //$NON-NLS-1$
	public static final String PROPERTY_REMOVE_CHILD = "removeChild"; //$NON-NLS-1$

	private List<IActivity> allreadyin = new ArrayList<IActivity>();

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<Node> contents = new ArrayList<Node>();
	private List<Node> total = new ArrayList<Node>();

	protected Rectangle layout;

	private WorkflowModel workflowModel;

	public ContainerPanelModel(WorkflowEditorInput input) {
		workflowModel = input.getWorkflowModel();
		try {
			rebuildModel();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void rebuildModel() {
		Collection<IActivity> activities = workflowModel.getActivities().values();

		for(IActivity a : activities) {
			try {
				List<IActivity> r = new ArrayList<IActivity>();
				r.addAll(activities);
				r.remove(a);
				simulAdd(a, r, null);
			} catch(NodeException e) {
				e.printStackTrace();
			}

		}

		for(Transition tr : workflowModel.getTransitions()) {
			try {
				Node s = null;
				for(Node n : total) {
					if(n.getName().equalsIgnoreCase(tr.getSource().getName()))
						s = n;
				}

				Node t = null;
				for(Node n : total) {
					if(n.getName().equalsIgnoreCase(tr.getTarget().getName()))
						t = n;
				}

				if(s != null && t != null) {
					if(tr.getColor() != null) {
						new Link(s, t, tr.getColor());
					}
					else {
						new Link(s, t);
					}

				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void simulAdd(IActivity a, Collection<IActivity> activities, LoopModel parent) throws NodeException {
		if((a.getParent().equalsIgnoreCase("") || a instanceof LoopGlobale) && !allreadyin.contains(a)) { //$NON-NLS-1$
			if(a instanceof LoopGlobale) {
				allreadyin.add(a);
				LoopModel loop = new LoopModel(workflowModel);
				loop.setWorkflowObject((LoopGlobale) a);
				loop.setName(a.getName());
				loop.setX(a.getPositionX());
				loop.setY(a.getPositionY());
				loop.setWidth(((LoopGlobale) a).getPositionWidth());
				loop.setHeight(((LoopGlobale) a).getPositionHeight());
				if(parent != null) {
					parent.addChild(loop, ""); //$NON-NLS-1$
				}
				else
					contents.add(loop);

				total.add(loop);
				List<IActivity> r = new ArrayList<IActivity>();
				r.addAll(activities);

				for(IActivity ac : activities) {
					if(ac.getParent().equalsIgnoreCase(a.getName())) {
						r.remove(ac);
						if(ac instanceof LoopGlobale) {
							simulAdd(ac, r, loop);
						}
						else {
							Node node = new Node();
							node.setX(ac.getPositionX());
							node.setY(ac.getPositionY());
							node.setWorkflowObject((WorkflowObject) ac);
							node.setName(ac.getName());
							loop.addChild(node, "t"); //$NON-NLS-1$
							total.add(node);
							allreadyin.add(ac);
						}

					}
				}

			}
			else if(a instanceof MacroProcess) {
				MacroProcessModel loop = new MacroProcessModel(workflowModel);
				loop.setWorkflowObject((MacroProcess) a);
				loop.setName(a.getName());
				loop.setX(a.getPositionX());
				loop.setY(a.getPositionY());
				loop.setWidth(((MacroProcess) a).getPositionWidth());
				loop.setHeight(((MacroProcess) a).getPositionHeight());

				contents.add(loop);
				total.add(loop);

				for(IActivity ac : activities) {
					if(ac.getParent().equalsIgnoreCase(a.getName())) {

						Node node = new Node();
						node.setX(ac.getPositionX());
						node.setY(ac.getPositionY());
						node.setWorkflowObject((WorkflowObject) ac);
						node.setName(ac.getName());
						loop.addChild(node, "t"); //$NON-NLS-1$
						total.add(node);

					}
				}

			}
			else {
				Node n = new Node();
				n.setX(a.getPositionX());
				n.setY(a.getPositionY());
				n.setWorkflowObject((WorkflowObject) a);
				n.setName(a.getName());
				contents.add(n);
				total.add(n);
				allreadyin.add(a);

			}
		}

	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public PropertyChangeSupport getListeners() {
		return listeners;
	}

	public boolean removeChild(Node child) {

		boolean b = this.contents.remove(child);
		if(b) {
			try {
				if(child.getWorkflowObject() instanceof IActivity)
					workflowModel.removeActivity(child.getWorkflowObject().getName());

				getListeners().firePropertyChange(PROPERTY_REMOVE_CHILD, child, null);
			} catch(WorkflowException e) {
				e.printStackTrace();
			}

		}

		return b;

	}

	public boolean addChild(Object child_) {
		if(child_ instanceof Node) {
			Node child = (Node) child_;

			for(IActivity activitipresente : workflowModel.getActivities().values()) {
				if(activitipresente.getName().equalsIgnoreCase(child.getName())) {
					return false;
				}
			}
			boolean b = this.contents.add(child);
			if(b) {

				try {
					if(child.getWorkflowObject() instanceof IActivity) {
						workflowModel.addActivity((IActivity) child.getWorkflowObject());
					}

				} catch(Exception e) {
					e.printStackTrace();
					this.contents.remove(child);
					return false;
				}
				child.setName(child.getWorkflowObject().getName());

				getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, child);
			}
			return b;
		}
		else if(child_ instanceof LoopModel) {
			LoopModel child = (LoopModel) child_;

			for(IActivity activitipresente : workflowModel.getActivities().values()) {
				if(activitipresente.getName().equalsIgnoreCase(child.getName())) {
					return false;
				}
			}
			boolean b = this.contents.add(child);
			if(b) {

				try {
					if(child.getWorkflowObject() instanceof IActivity) {
						workflowModel.addActivity((IActivity) child.getWorkflowObject());
					}

				} catch(Exception e) {
					e.printStackTrace();
					this.contents.remove(child);
					return false;
				}
				child.setName(child.getWorkflowObject().getName());

				getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, child);

			}
			return b;
		}
		return false;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public List getChildren() {
		List l = new ArrayList();
		l.addAll(contents);

		return l;
	}

}
