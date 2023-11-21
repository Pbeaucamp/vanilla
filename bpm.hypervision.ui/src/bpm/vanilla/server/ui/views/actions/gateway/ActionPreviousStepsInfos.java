package bpm.vanilla.server.ui.views.actions.gateway;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.VisualConstants;

public class ActionPreviousStepsInfos extends Action {

	private TableViewer taskViewer;
	
	public ActionPreviousStepsInfos(TableViewer tasksViewer) {
		this.taskViewer = tasksViewer;
		setId(VisualConstants.ACTION_GATEWAY_PREVIOUS_STEPS_INFOS_ID);
		setText(Messages.ActionPreviousStepsInfos_0);
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.GATEWAYDETAILS));
	}

	public void run() {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		ISelection s = taskViewer.getSelection();

		if (s.isEmpty()) {
			MessageDialog.openInformation(sh, Messages.ActionStepsInfos_1, Messages.ActionStepsInfos_2);
		}

		Object o = ((IStructuredSelection) s).getFirstElement();

		if (o instanceof IRuntimeState) {
			try {
				IRuntimeState runtimeState = (IRuntimeState) o;
				if(runtimeState instanceof WorkflowInstanceState) {
					DialogWorkflowStepsInfos d = new DialogWorkflowStepsInfos(sh, (WorkflowInstanceState)runtimeState);
					d.open();
				}
				else if(runtimeState instanceof GatewayRuntimeState) {
					DialogGatewayStepsInfos d = new DialogGatewayStepsInfos(sh, (GatewayRuntimeState)runtimeState);
					d.open();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(sh, Messages.ActionStepsInfos_3, ex.getMessage());
			}
		}
		else if (o instanceof ItemInstance) {
			try {
				IRuntimeState runtimeState = ((ItemInstance) o).getState();
				if(runtimeState instanceof WorkflowInstanceState) {
					DialogWorkflowStepsInfos d = new DialogWorkflowStepsInfos(sh, (WorkflowInstanceState)runtimeState);
					d.open();
				}
				else if(runtimeState instanceof GatewayRuntimeState) {
					DialogGatewayStepsInfos d = new DialogGatewayStepsInfos(sh, (GatewayRuntimeState)runtimeState);
					d.open();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(sh, Messages.ActionStepsInfos_3, ex.getMessage());
			}
		}
	}

}
