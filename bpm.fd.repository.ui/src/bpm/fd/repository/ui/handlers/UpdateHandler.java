package bpm.fd.repository.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IWorkbenchWindow;

import bpm.fd.repository.ui.wizard.actions.ActionUpdateProject;
import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.repository.ui.Activator;

public class UpdateHandler extends AbstractHandler{

	public static final String COMMAND_ID = "bpm.repository.ui.commands.update";

	
	public UpdateHandler(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		
		IRepositoryApi sock = Activator.getDefault().getDesignerActivator().getRepositoryConnection();
		Integer directoryItemId = Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId();
		String modelXml = Activator.getDefault().getDesignerActivator().getCurrentModelXml();
		
		IVanillaAPI api = new RemoteVanillaPlatform(Activator.getDefault().getDesignerActivator().getRepositoryContext().getVanillaContext());
		
		try{
			new ActionUpdateProject(sock, 
					bpm.fd.design.ui.Activator.getDefault().getProject(), 
					new Repository(sock), 
					api).perform();
		}catch(Exception ex){
			ex.printStackTrace();
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
