package bpm.vanilla.repository.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.repository.ui.connection.DialogConnect;

public class DisconnectHandler extends AbstractHandler{
	public static final String COMMAND_ID = "bpm.repository.ui.commands.disconnection";
	
	public DisconnectHandler(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		Activator.getDefault().getDesignerActivator().setRepositoryContext(null);
		
		MessageDialog.openInformation(window.getShell(), Messages.DisconnectAction_0, Messages.DisconnectAction_1);
		IRepositoryContext ctx = Activator.getDefault().getDesignerActivator().getRepositoryContext();
		
		ISourceProviderService service = (ISourceProviderService) window.getService(ISourceProviderService.class);
		SessionSourceProvider sessionProvider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);

		sessionProvider.setContext(ctx);
		
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
