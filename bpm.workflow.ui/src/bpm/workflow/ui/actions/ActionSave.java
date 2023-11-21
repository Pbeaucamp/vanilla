package bpm.workflow.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Save the model
 * 
 * @author MARTIN
 * 
 */
public class ActionSave extends Action {
	private boolean cancelled = false;

	public ActionSave() {
		super(Messages.ActionSave_0);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void run() {

		IEditorPart p = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		p.doSave(null);
	}

}
