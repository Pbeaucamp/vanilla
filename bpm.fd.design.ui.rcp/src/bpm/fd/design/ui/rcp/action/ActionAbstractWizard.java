package bpm.fd.design.ui.rcp.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.rcp.Messages;
import bpm.fd.design.ui.rcp.dialogs.DialogWizardList;

public class ActionAbstractWizard extends Action{
	public static final String WIZARD_CATEGORIE_FD = "bpm.fd.design.ui.freedashCategory"; //$NON-NLS-1$
	
	protected int type;
	
	protected ActionAbstractWizard(String name, int type){
		super(name);
		this.type = type;
	}
	
	public void run(){
		DialogWizardList dial = new DialogWizardList(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), WIZARD_CATEGORIE_FD, type);
		if (dial.open() == DialogWizardList.OK){
			
			
			try {
				IWizard wiz = dial.getWizardDescriptor().createWizard();
				
				WizardDialog d = new WizardDialog(dial.getShell(), wiz);
				d.open();
			} catch (Exception e) {
				
				e.printStackTrace();
				MessageDialog.openError(dial.getShell(), Messages.ActionAbstractWizard_1, Messages.ActionAbstractWizard_2 + e.getCause().getMessage());
			}
		}
		
	}
}
