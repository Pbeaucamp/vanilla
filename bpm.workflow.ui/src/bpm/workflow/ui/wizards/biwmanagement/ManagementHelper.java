package bpm.workflow.ui.wizards.biwmanagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteWorkflowComponent;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

public class ManagementHelper {
	public static final String ACTION_START = "start"; //$NON-NLS-1$

	public static boolean run(Shell sh, String groupName, int itemId, String action) throws Exception {

		final RemoteWorkflowComponent remote = new RemoteWorkflowComponent(Activator.getDefault().getRepositoryContext().getVanillaContext());
		
		final RuntimeConfiguration config = new RuntimeConfiguration(Activator.getDefault().getRepositoryContext().getGroup().getId(), 
				new ObjectIdentifier(Activator.getDefault().getRepositoryContext().getRepository().getId(), itemId), new ArrayList<VanillaGroupParameter>());
		
		if(action.endsWith(ACTION_START)) {
			IRunnableWithProgress r = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					monitor.setTaskName(Messages.ManagementHelper_0);
					monitor.beginTask(Messages.ManagementHelper_1, IProgressMonitor.UNKNOWN);
					try {
						remote.startWorkflow(config);

						monitor.worked(1);
					} catch (Exception e) {
						e.printStackTrace();
						throw new InterruptedException(e.getMessage());
					}
				}
			};
			
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			try {
				service.run(true, false, r);
				MessageDialog.openInformation(sh, Messages.ManagementHelper_5 + action, Messages.ManagementHelper_6);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(sh, Messages.ManagementHelper_2, e.getMessage());
			}
		}

		return true;
	}

}
