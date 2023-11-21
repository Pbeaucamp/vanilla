package bpm.gateway.ui.mdm.actions;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.transformations.mdm.MdmHelper;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.i18n.Messages;
import bpm.mdm.model.Synchronizer;

public class WizardMdm extends Wizard{
	private SynchronizerSelectionPage syncPage;

	@Override
	public void addPages() {
		super.addPages();
		syncPage = new SynchronizerSelectionPage("sync"); //$NON-NLS-1$
		syncPage.setTitle(Messages.WizardMdm_1);
		syncPage.setDescription(Messages.WizardMdm_2);
		addPage(syncPage);
	}
	@Override
	public boolean performFinish() {
		Synchronizer s = syncPage.getSynchronizer();
		
		String fileName = null;
		try {
			fileName = syncPage.getFileName();
			File f = new File(fileName);
			if (f.exists() ){
				if (!MessageDialog.openQuestion(getShell(), Messages.WizardMdm_3, Messages.WizardMdm_4 + f.getName() + Messages.WizardMdm_5)){
					return false;
				}
				
			}
			//XXX
			DocumentGateway doc = null;//new MdmHelper(SynchronizerSelectionPage.helper).createGatewayModel(s.getName());
			
			GatewayEditorInput i = new GatewayEditorInput(fileName,  doc);
			
			IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.openEditor(i, GatewayEditorPart.ID, false);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.WizardMdm_6, Messages.WizardMdm_7 + e.getMessage());
			return false;
		}
		
		
	}
	
}
