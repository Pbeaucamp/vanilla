package bpm.fasd.client.dwh.importer.actions;


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

import bpm.fasd.client.dwh.importer.Activator;
import bpm.fasd.client.dwh.importer.Messages;

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
			IWizardDescriptor wizDesc = ImportWizardRegistry.getInstance().findWizard("bpm.fasd.client.dwh.importer.wizards.DwhToFmdtWizard"); //$NON-NLS-1$
			
			IWorkbenchWizard wiz = wizDesc.createWizard();
			((IImportWizard)wiz).init(Activator.getDefault().getWorkbench(), StructuredSelection.EMPTY);
			WizardDialog dial = new WizardDialog(window.getShell(), wiz);
			dial.setMinimumPageSize(800, 600);
			
			dial.open();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(window.getShell(), Messages.LaunchWizardAction_1, Messages.LaunchWizardAction_2 + ex.getMessage());
		}


	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
