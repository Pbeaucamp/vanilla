package bpm.workflow.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.workflow.ui.views.ResourceViewPart;

/**
 * Delete a parameter : action
 *
 */
public class DeleteParameterActionDelegate implements IViewActionDelegate {
	private ResourceViewPart view ;

	public void init(IViewPart view) {
		this.view = (ResourceViewPart)view;
	}

	public void run(IAction action) {
		view.deleteParameter();
		view.refresh();
	}

	public void selectionChanged(IAction action, ISelection selection) { }

}
