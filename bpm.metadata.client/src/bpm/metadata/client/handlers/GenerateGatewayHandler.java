package bpm.metadata.client.handlers;

import metadata.client.wizards.big.BigWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import bpm.repository.ui.SessionSourceProvider;



public class GenerateGatewayHandler extends AbstractHandler {
	public static final String COMMAND_ID = "bpm.metadata.client.commands.generateGateway";

	
	public GenerateGatewayHandler(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		BigWizard wiz = new BigWizard();
		WizardDialog d = new WizardDialog(window.getShell(), wiz);
		d.open();
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
