package bpm.vanilla.server.ui.views.actions.gateway;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;

import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.VisualConstants;
import bpm.vanilla.server.ui.views.activity.ServerContent;

public class ActionStepsInfos extends Action {

	public ActionStepsInfos() {
		setId(VisualConstants.ACTION_GATEWAY_STEPS_INFOS_ID);
		setText(Messages.ActionStepsInfos_0);
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.GATEWAYDETAILS));
	}

	public void run() {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		IViewPart view = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VisualConstants.SERVER_STATE_VIEW_ID);

		TaskInfo task = ((ServerContent) view).getSelectedTask();

		if (task == null) {
			MessageDialog.openInformation(sh, Messages.ActionStepsInfos_1, Messages.ActionStepsInfos_2);
		}

		if (Activator.getDefault().getServerType() != null && Activator.getDefault().getServerType() == ServerType.GATEWAY) {
			try {
				GatewayRuntimeState runtimeState = ((GatewayComponent) Activator.getDefault().getRemoteServerManager()).getRunState(new SimpleRunTaskId(Integer.parseInt(task.getId())));
				DialogGatewayStepsInfos d = new DialogGatewayStepsInfos(sh, Integer.parseInt(task.getId()), runtimeState);
				d.open();
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(sh, Messages.ActionStepsInfos_3, ex.getMessage());
			}
		}
		else if (Activator.getDefault().getServerType() != null && Activator.getDefault().getServerType() == ServerType.WORKFLOW) {
			try {
				WorkflowInstanceState runtimeState = ((WorkflowService) Activator.getDefault().getRemoteServerManager()).getInfos(new SimpleRunIdentifier(task.getId()), task.getItemId(), task.getRepositoryId());
				DialogWorkflowStepsInfos d = new DialogWorkflowStepsInfos(sh, task, runtimeState);
				d.open();
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(sh, Messages.ActionStepsInfos_3, ex.getMessage());
			}
		}
	}

}
