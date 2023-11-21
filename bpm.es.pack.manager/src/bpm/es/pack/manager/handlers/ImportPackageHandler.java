package bpm.es.pack.manager.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;

import adminbirep.Activator;
import bpm.es.pack.manager.perspective.DeploymentsPerspective;

public class ImportPackageHandler extends AbstractHandler {
	
	public static final String COMMAND_ID = "bpm.es.pack.manager.commands.ImportPackageHandler"; //$NON-NLS-1$

	public ImportPackageHandler() {
		super.setBaseEnabled(true);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IPerspectiveDescriptor pd  = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(DeploymentsPerspective.ID);
			if (pd != null){
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);	
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		super.setBaseEnabled(true);
	}
}
