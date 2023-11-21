package bpm.gateway.ui.dwh.importer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.gateway.ui.Activator;

public class LaunchWizardAction implements IWorkbenchWindowActionDelegate {

	
	private IWorkbenchWindow window;
	@Override
	public void dispose() {
		

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	@Override
	public void run(IAction action) {
		
		
		try{
			IWizardDescriptor wizDesc = ImportWizardRegistry.getInstance().findWizard("bpm.gateway.ui.dwh.importer.wizards.DwhToFmdtWizard");
			
			IWorkbenchWizard wiz = wizDesc.createWizard();
			((IImportWizard)wiz).init(Activator.getDefault().getWorkbench(), StructuredSelection.EMPTY);
			WizardDialog dial = new WizardDialog(window.getShell(), wiz);
			dial.setMinimumPageSize(800, 600);
			
			dial.open();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(window.getShell(), "Unable to create Wizard", "An error occured while creating wizard.\n" + ex.getMessage());
		}


	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
