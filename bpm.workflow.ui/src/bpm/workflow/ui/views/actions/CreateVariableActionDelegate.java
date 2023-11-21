package bpm.workflow.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.dialogs.DialogVariable;
import bpm.workflow.ui.views.ResourceViewPart;

/**
 * Create a variable : action
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class CreateVariableActionDelegate implements IViewActionDelegate {
	private ResourceViewPart view;

	public void init(IViewPart view) {
		this.view = (ResourceViewPart) view;

	}

	public void run(IAction action) {
		DialogVariable dial = new DialogVariable(view.getSite().getShell(), new Variable());
		if(dial.open() == Dialog.OK) {
			try {
				Variable v = new Variable(dial.getProperties());
				ListVariable.getInstance().addVariable(v);

			} catch(Exception e) {
				Shell shell = view.getSite().getShell();
				MessageDialog.openError(shell, Messages.CreateVariableActionDelegate_0, e.getMessage());
			}

			ResourceViewPart view = (ResourceViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);

			if(view != null) {
				view.refresh();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
