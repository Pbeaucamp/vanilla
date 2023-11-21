package bpm.gateway.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.resource.variables.wizard.VariableWizard;

public class CreateVariableActionDelegate implements IViewActionDelegate {
	private IViewPart view ;
	
	public void init(IViewPart view) {
		this.view = view;

	}

	public void run(IAction action) {
		VariableWizard wizard = new VariableWizard();
		
		wizard.init(Activator.getDefault().getWorkbench(),
					(IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
		
		WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
		dialog.create();
		dialog.getShell().setSize(800, 600);
		dialog.getShell().setText("Variable Wizard"); //$NON-NLS-1$
		
		if (dialog.open() == WizardDialog.OK){
						
//			IEditorPart editor = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(Activator.getDefault().getCurrentInput());
			
			
			
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
