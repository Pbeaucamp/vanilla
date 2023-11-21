package bpm.gateway.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.resource.forms.wizard.FormWizard;
import bpm.gateway.ui.views.ModelViewPart;

public class FormMappingActionDelegate implements IViewActionDelegate {

	private ModelViewPart view;
	
	public void init(IViewPart view) {
		this.view = (ModelViewPart)view;
		

	}

	public void run(IAction action) {
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return ;
		}
		
		FormWizard wizard = new FormWizard();
		wizard.init(Activator.getDefault().getWorkbench(),
				(IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
	
		WizardDialog dialog = new WizardDialog(view.getSite().getShell(), wizard);
		dialog.create();
		dialog.getShell().setSize(800, 600);
		dialog.getShell().setText("Forms Mapping Wizard"); //$NON-NLS-1$
		
		if (dialog.open() == WizardDialog.OK){
			view.refresh();
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
