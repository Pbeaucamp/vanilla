package bpm.vanilla.repository.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.repository.ui.icons.IconsRegistry;
import bpm.vanilla.repository.ui.wizards.page.ExportObjectWizard;

public class ImportHandler extends AbstractHandler{
	public static final String COMMAND_ID = "bpm.repository.ui.commands.export";
	
	public ImportHandler(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		ExportObjectWizard wizard = new ExportObjectWizard(Activator.getDefault().getDesignerActivator());
		
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard){

			@Override
			public void updateButtons() {
				
				super.updateButtons();
				getButton(IDialogConstants.CANCEL_ID).setVisible(false);
//				getButton(IDialogConstants.FINISH_ID).setText(IDialogConstants.CLOSE_LABEL);
				getButton(IDialogConstants.FINISH_ID).setText(Messages.Import_0);
			}
			
		};
		
		dialog.create();
		dialog.getShell().setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.REPOSITORY));
		dialog.getShell().setText(Messages.Import_3);
		dialog.getShell().setSize(800, 600);
		dialog.open();
		
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		boolean old = isEnabled();
		
		try {
			//check if connected to vanilla
			Object o = ((IEvaluationContext)evaluationContext).getVariable(SessionSourceProvider.CONNECTION_STATE);
			
			
			boolean enabled = SessionSourceProvider.enabled.equals(o);

			
			if (old != enabled){
				super.setBaseEnabled(enabled);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
}
