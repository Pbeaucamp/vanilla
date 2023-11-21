package bpm.workflow.ui.gef.model;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.Transition;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.InterfaceGoogleActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.ui.Activator;

public class NodeLinkerHelper {

	public static void add(Link link) throws Exception {

		try {
			IActivity source = (IActivity) link.getSource().getWorkflowObject();
			IActivity target = (IActivity) link.getTarget().getWorkflowObject();

			if(source instanceof InterfaceGoogleActivity) {
				if(target instanceof StopActivity) {
					Transition t = new Transition(source, target);
					((WorkflowModel) Activator.getDefault().getCurrentModel()).addTransition(t);
				}
				else {

				}
			}
			else {
				Transition t = new Transition(source, target);
				((WorkflowModel) Activator.getDefault().getCurrentModel()).addTransition(t);
			}

		} catch(Exception e) {

		}

	}

	public static void remove(Link link) {
		try {
			IActivity source = (IActivity) link.getSource().getWorkflowObject();
			IActivity target = (IActivity) link.getTarget().getWorkflowObject();

			for(Transition t : ((WorkflowModel) Activator.getDefault().getCurrentModel()).getTransitions()) {
				if(t.getSource().equals(source) && t.getTarget().equals(target)) {
					((WorkflowModel) Activator.getDefault().getCurrentModel()).removeTransition(t);
					break;
				}
			}
		} catch(Exception e) {

		}

	}
}
