package bpm.vanilla.server.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;

import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.StepInfos;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.dialogs.DialogFailureCause;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.VisualConstants;
import bpm.vanilla.server.ui.views.actions.gateway.DialogGatewayStepsInfos;
import bpm.vanilla.server.ui.views.actions.gateway.DialogWorkflowStepsInfos;
import bpm.vanilla.server.ui.views.activity.ServerContent;
import bpm.vanilla.server.ui.views.activity.ServerPreviousContent;

public class ActionFullDescription extends Action {
	
	private IViewPart view;
	private Dialog dialog;
	
	public ActionFullDescription(IViewPart view) {
		this.view = view;
		setId(VisualConstants.ACTION_SHOW_LOGS);
		setText(Messages.ActionFullDescription_6);
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.INFORMATION));
	}

	public ActionFullDescription(Dialog dialog) {
		this.dialog = dialog;
		setId(VisualConstants.ACTION_SHOW_LOGS);
		setText(Messages.ActionFullDescription_7);
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.INFORMATION));
	}

	public void run() {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if(view != null && view instanceof ServerContent) {
			TaskInfo task = ((ServerContent) view).getSelectedTask();
			
			if (task == null) {
				MessageDialog.openInformation(sh, Messages.ActionFullDescription_0, Messages.ActionFullDescription_1);
			}
			else {
				DialogFailureCause dialog = new DialogFailureCause(sh, task.getFailureCause());
				dialog.open();
			}
		}
		else if(view != null && view instanceof ServerPreviousContent) {
			ISelection s = view.getSite().getSelectionProvider().getSelection();

			if (s.isEmpty()) {
				MessageDialog.openInformation(sh, Messages.ActionFullDescription_0, Messages.ActionFullDescription_1);
			}
			else {
				Object o = ((IStructuredSelection) s).getFirstElement();
	
				if (o instanceof IRuntimeState) {
					DialogFailureCause dialog = new DialogFailureCause(sh, ((IRuntimeState)o).getFailureCause());
					dialog.open();
				}
			}
		}
		else if(dialog != null && dialog instanceof DialogGatewayStepsInfos) {
			StepInfos stepInfo = ((DialogGatewayStepsInfos)dialog).getSelectedStep();

			if (stepInfo == null) {
				MessageDialog.openInformation(sh, Messages.ActionFullDescription_0, Messages.ActionFullDescription_1);
			}
			else {
				DialogFailureCause dialog = new DialogFailureCause(sh, stepInfo.getLogs());
				dialog.open();
			}
		}
		else if(dialog != null && dialog instanceof DialogWorkflowStepsInfos) {
			bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState.StepInfos stepInfo = ((DialogWorkflowStepsInfos)dialog).getSelectedStep();

			if (stepInfo == null) {
				MessageDialog.openInformation(sh, Messages.ActionFullDescription_0, Messages.ActionFullDescription_1);
			}
			else {
				DialogFailureCause dialog = new DialogFailureCause(sh, stepInfo.getFailureCause());
				dialog.open();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) { }

}
