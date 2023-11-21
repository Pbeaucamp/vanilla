package bpm.metadata.client.birtreport.command;

import metadataclient.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;

import bpm.metadata.ui.birtreport.wizards.BirtReportWizard;
import bpm.repository.ui.SessionSourceProvider;

public class GenerateFwr extends AbstractHandler {
	public static final String COMMAND_ID = "bpm.metadata.client.birtreport.command.GenerateFwr";

	public GenerateFwr(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		BirtReportWizard wiz = new BirtReportWizard();
		WizardDialog dial = new WizardDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		dial.setMinimumPageSize(600, 600);
		dial.create();
	
		if (dial.open() == Dialog.OK) {
			
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		boolean old = isEnabled();
		try {
			//check if connected to vanilla
			Object o = ((IEvaluationContext)evaluationContext).getVariable(SessionSourceProvider.CONNECTION_STATE);
			
			
			boolean enabled = SessionSourceProvider.enabled.equals(o);

			//check if model imported
			o = ((IEvaluationContext)evaluationContext).getVariable(SessionSourceProvider.IMPORTED_MODEL_STATE);
			enabled = enabled && SessionSourceProvider.enabled.equals(o);

			if (old != enabled){
				super.setBaseEnabled(enabled);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}


}
