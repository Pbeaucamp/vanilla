package bpm.workflow.ui.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.WorkflowObject;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.NodeException;

public class CopyCommand extends Command {

	private Node model;

	@Override
	public void execute() {
		if(model.getWorkflowObject() instanceof IActivity) {
			IActivity a = ((IActivity) model.getWorkflowObject()).copy();
			Node d = new Node();
			try {
				d.setWorkflowObject((WorkflowObject) a);
				d.setName(a.getName());
				Clipboard.getDefault().setContents(d);
			} catch(NodeException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param model
	 *            the model to set
	 */
	public final void setModel(Node model) {
		this.model = model;
	}

}
