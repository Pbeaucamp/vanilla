package bpm.workflow.ui.views.actions;



import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.resources.server.wizard.ServerWizard;
import bpm.workflow.ui.views.ResourceViewPart;


/**
 * Create a server : action
 * @author CHARBONNIER, MARTIN
 *
 */
public class CreateServerActionDelegate implements IViewActionDelegate {

	private ResourceViewPart view ;
	

	public void init(IViewPart view) {
		this.view = (ResourceViewPart)view;

	}

	public void run(IAction action) {
		
		ServerWizard wizard = new ServerWizard();
		
		wizard.init(Activator.getDefault().getWorkbench(),
					(IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
		
		WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
		dialog.create();
		dialog.getShell().setSize(800, 600);
		dialog.getShell().setText("Server Wizard"); //$NON-NLS-1$
		
		if (dialog.open() == WizardDialog.OK){

	
		}


	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
