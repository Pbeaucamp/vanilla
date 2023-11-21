package bpm.workflow.ui.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.workflow.runtime.model.PoolModel;
import bpm.workflow.runtime.model.Transition;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.LoopGlobale;
import bpm.workflow.runtime.model.activities.MacroProcess;
import bpm.workflow.ui.editors.WorkflowEditorInput;

public class ContainerPanelPool implements IAdaptable {
	public static final String PROPERTY_ADD_CHILD = "newChild"; //$NON-NLS-1$
	public static final String PROPERTY_REMOVE_CHILD = "removeChild"; //$NON-NLS-1$

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<Pool> contents = new ArrayList<Pool>();

	protected Rectangle layout;

	private WorkflowModel workflowModel;

	public ContainerPanelPool(WorkflowEditorInput input) {
		this.workflowModel = input.getWorkflowModel();
		rebuildModel();

	}

	public void rebuildModel() {
		Collection<PoolModel> pools = workflowModel.getPools();

		for(PoolModel p : pools) {
			try {
				Pool n = new Pool(workflowModel);
				n.setX(p.getPositionX());
				n.setY(p.getPositionY());
				n.setPoolModel(p);
				n.setName(p.getName());

				contents.add(n);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		List<Node> allNodes = new ArrayList<Node>();
		for(Pool p : contents) {
			List<Node> premiereliste = p.getChildren();
			for(Node node : premiereliste) {
				if(node.getWorkflowObject().getClass() != LoopGlobale.class && node.getWorkflowObject().getClass() != MacroProcess.class) {
					allNodes.add(node);
				}
			}

		}

		for(Transition tr : workflowModel.getTransitions()) {
			try {
				Node s = null;
				for(Node n : allNodes) {
					if(n.getName().equalsIgnoreCase(tr.getSource().getName())) {
						s = n;
					}
				}

				Node t = null;
				for(Node n : allNodes) {
					if(n.getName().equalsIgnoreCase(tr.getTarget().getName())) {
						t = n;
					}
				}

				if(s != null && t != null) {
					new Link(s, t);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	public PropertyChangeSupport getListeners() {
		return listeners;
	}

	public boolean removeChild(Pool child) {
		if(contains(child)) {
			try {
				workflowModel.removePool(child.getPoolModel());

				getListeners().firePropertyChange(PROPERTY_REMOVE_CHILD, child, null);
				return true;
			} catch(Exception e) {
				e.printStackTrace();
			}

		}

		return false;
	}

	public boolean addChild(Pool child) {
		if(contains(child)) {
			return false;
		}
		boolean b = this.contents.add(child);
		if(b) {

			try {
				workflowModel.addPool(child.getPoolModel());
			} catch(Exception e) {
				e.printStackTrace();
				this.contents.remove(child);
				return false;
			}
			child.setName(child.getPoolModel().getName());

			getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, child);
		}
		return b;

	}

	private boolean contains(Pool child) {
		return contents.contains(child);
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

	public Object getAdapter(Class adapter) {
		return null;
	}

}
