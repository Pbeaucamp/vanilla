package bpm.mdm.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;

import bpm.mdm.ui.Activator;
import bpm.mdm.ui.perspectives.SupplierManagementPerspective;

public class SupplierCommandHandler extends AbstractHandler {
	private IPerspectiveDescriptor original;
	public static final String COMMAND_ID = "bpm.mdm.ui.commands.supplierCommand";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		
//		if (original == null ){
//			original = window.getActivePage().getPerspective();
			for (IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
				if (pd.getId().equals(SupplierManagementPerspective.ID)){
					window.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
					break;
				}
			}
//		}
//		else {
//			window.getActivePage().setPerspective(original);
//			original = null;
//		}
		
		//force to refresh all UI that are linked to this handler
		ICommandService commandService = (ICommandService)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ICommandService.class);
		commandService.refreshElements(event.getCommand().getId(), null);
		return null;
	}

}
