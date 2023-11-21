package bpm.vanilla.repository.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.dialogs.DialogShowDependencies;

public class UpdateHandler extends AbstractHandler{
	public static final String COMMAND_ID = "bpm.repository.ui.commands.export";
	
	public UpdateHandler(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		
		IRepositoryApi sock = Activator.getDefault().getDesignerActivator().getRepositoryConnection();
		Integer directoryItemId = Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId();
		String modelXml = Activator.getDefault().getDesignerActivator().getCurrentModelXml();
		
		
		RepositoryItem it = null;
		
		try{
			it = sock.getRepositoryService().getDirectoryItem(directoryItemId);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(window.getShell(), "Update Repository Model", "Could not find this item on the repository.");
		}
		
		try{
			DialogShowDependencies dial = new DialogShowDependencies(window.getShell(), it, sock, DialogShowDependencies.UPDATE_TITLE);
			if(dial.hasDependencies()) {
				if(dial.open() != Dialog.OK) {
					return null;
				}
			}
			
			sock.getRepositoryService().updateModel(it, modelXml);
			MessageDialog.openInformation(window.getShell(), "Update Repository Model", "The item " + it.getItemName() + " has has been updated on repository  " + Activator.getDefault().getDesignerActivator().getRepositoryContext().getRepository().getName());
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(window.getShell(), "Update Repository Model", "Failed to update the item " + it.getItemName() + " on repository : " + ex.getMessage());
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
