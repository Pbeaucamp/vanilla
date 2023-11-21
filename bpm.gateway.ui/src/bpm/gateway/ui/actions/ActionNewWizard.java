package bpm.gateway.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.dialogs.utils.DialogWizardList;
import bpm.gateway.ui.i18n.Messages;



public class ActionNewWizard extends Action{
	public static final String WIZARD_CATEGORIE_FD = "bpm.gateway.ui.wizard.category"; //$NON-NLS-1$
	
	protected int type;
	
	public ActionNewWizard() {
		super(Messages.ActionNewWizard_1);
		this.type = DialogWizardList.NEW_WIZARD;
		
		setId("bpm.gateway.action.runnewwizard"); //$NON-NLS-1$
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
				MessageDialog.openError(dial.getShell(), Messages.ActionNewWizard_3, Messages.ActionNewWizard_4 + e.getCause().getMessage());
			}
		}
		
	}
	
	
	
}
