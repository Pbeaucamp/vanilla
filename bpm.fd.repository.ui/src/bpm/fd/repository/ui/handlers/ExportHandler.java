package bpm.fd.repository.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import bpm.fd.repository.ui.Activator;
import bpm.fd.repository.ui.wizard.FdExportWizard;
import bpm.repository.ui.SessionSourceProvider;

public class ExportHandler extends AbstractHandler{
	public static final String COMMAND_ID = "bpm.repository.ui.commands.export";
	
	public ExportHandler(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		FdExportWizard wizard = new FdExportWizard();
		
		
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard){

			@Override
			public void updateButtons() {
				
				super.updateButtons();
				getButton(IDialogConstants.CANCEL_ID).setVisible(false);
				//getButton(IDialogConstants.FINISH_ID).setText(Messages.Export_2);
			}
			
		};
		
		dialog.create();
		//dialog.getShell().setImage(new Image(Display.getCurrent(), System.getProperty("user.dir") + "/resources/icons/repository.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		//dialog.getShell().setText(Messages.Export_5);
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
			o = ((IEvaluationContext)evaluationContext).getVariable(SessionSourceProvider.MODEL_OPENED_STATE);
			enabled = enabled && SessionSourceProvider.enabled.equals(o);

			
			if (old != enabled){
				super.setBaseEnabled(enabled);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
}
