package bpm.vanilla.repository.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.repository.ui.connection.DialogConnect;

public class ConnectHandler extends AbstractHandler {
	public static final String COMMAND_ID = "bpm.repository.ui.commands.connection";
	
	public ConnectHandler(){
		super.setBaseEnabled(true);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		DialogConnect d = new DialogConnect(window.getShell());
		d.open();
		
		
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
			
			
			boolean enabled = SessionSourceProvider.disabled.equals(o);

			
			if (old != enabled){
				super.setBaseEnabled(enabled);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
}
