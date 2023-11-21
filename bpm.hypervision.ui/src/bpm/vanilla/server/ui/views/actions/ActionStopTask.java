package bpm.vanilla.server.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.views.activity.ServerContent;

public class ActionStopTask implements IViewActionDelegate {
	private IViewPart view;

	public ActionStopTask() {
	}

	public void init(IViewPart view) {
		this.view = view;

	}

	public void run(IAction action) {
		Shell sh = view.getSite().getShell();
		TaskInfo task = ((ServerContent)view).getSelectedTask();

		if (task == null) {
			MessageDialog.openInformation(sh, Messages.ActionStopTask_0, Messages.ActionStopTask_1);
		}
		
		if (Activator.getDefault().getServerType() != null 
				&& (Activator.getDefault().getServerType() == ServerType.REPORTING || Activator.getDefault().getServerType() == ServerType.GATEWAY)) {
			try {
				Activator.getDefault().getRemoteServerManager().stopTask(new SimpleRunTaskId(Integer.parseInt(task.getId())));
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(sh, Messages.ActionStopTask_2, Messages.ActionStopTask_3 + ex.getCause().getMessage());
			}
		}
		else if (Activator.getDefault().getServerType() != null && Activator.getDefault().getServerType() == ServerType.WORKFLOW) {
			try {
				Activator.getDefault().getRemoteServerManager().stopTask(new SimpleRunIdentifier(task.getId()));
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(sh, Messages.ActionStopTask_2, Messages.ActionStopTask_3 + ex.getCause().getMessage());
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
