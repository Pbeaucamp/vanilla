package bpm.fd.design.ui.project.views.actions;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;
import bpm.fd.design.ui.wizard.pages.DataSourceSelectionPage;

public class CreateDataSourceAction extends Action {

	
	public CreateDataSourceAction(String label, String id){
		super(label);
		setId(id);
	}
	@Override
	public boolean isEnabled() {
		
		return super.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		try {
			OdaDataSourceWizard wiz = new OdaDataSourceWizard();
			WizardDialog d = new WizardDialog(shell, wiz){

				/* (non-Javadoc)
				 * @see org.eclipse.jface.wizard.WizardDialog#updateButtons()
				 */
				@Override
				public void updateButtons() {
					super.updateButtons();
					if (getWizard().getPreviousPage(getCurrentPage()) != null && getWizard().getPreviousPage(getCurrentPage()) instanceof DataSourceSelectionPage){
						getButton(IDialogConstants.BACK_ID).setEnabled(false);
					}
					else{
						getButton(IDialogConstants.BACK_ID).setEnabled(true);
					}
				}
				
			};
			if (d.open() == WizardDialog.OK){
				
			}
		} catch (OdaException e) {
			e.printStackTrace();
		}
	}
	
	
}
