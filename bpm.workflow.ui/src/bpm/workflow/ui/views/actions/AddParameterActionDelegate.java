package bpm.workflow.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.dialogs.DialogParameter;
import bpm.workflow.ui.views.ResourceViewPart;

public class AddParameterActionDelegate implements IViewActionDelegate {
	private ResourceViewPart view;
	@Override
	public void init(IViewPart view) {
		this.view = (ResourceViewPart)view;

	}

	@Override
	public void run(IAction action) {

		DialogParameter dial = new DialogParameter(view.getSite().getShell(), null);
		if (dial.open() == Dialog.OK) {
			ResourceViewPart view = (ResourceViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);
			
			if (view != null){
				view.refresh();
			}
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) { }

}
