package bpm.workflow.ui.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;

import bpm.workflow.runtime.model.activities.CancelActivity;
import bpm.workflow.runtime.model.activities.ConpensationActivity;
import bpm.workflow.runtime.model.activities.ErrorActivity;
import bpm.workflow.runtime.model.activities.LinkActivity;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.model.activities.SignalActivity;
import bpm.workflow.runtime.model.activities.TimerActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.dialogs.DialogCancelIcon;
import bpm.workflow.ui.dialogs.DialogConpensationIcon;
import bpm.workflow.ui.dialogs.DialogErrorIcon;
import bpm.workflow.ui.dialogs.DialogLinkIcon;
import bpm.workflow.ui.dialogs.DialogMailIcon;
import bpm.workflow.ui.dialogs.DialogSignalIcon;
import bpm.workflow.ui.dialogs.DialogTimerIcon;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

public class ChangeCommand extends Command {

	private NodePart model;
	private Node node;

	@Override
	public void execute() {
		try {

			Object workobj = node.getWorkflowObject();

			if(workobj instanceof MailActivity) {

				DialogMailIcon dial = new DialogMailIcon(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if(dial.open() == Dialog.OK) {
					String type = dial.getType();
					model.changeGraph(type);
					((MailActivity) workobj).setTypeact(dial.getTypeact());

				}
			}
			if(workobj instanceof TimerActivity) {

				DialogTimerIcon dial = new DialogTimerIcon(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if(dial.open() == Dialog.OK) {
					String type = dial.getType();
					model.changeGraph(type);

				}
			}
			if(workobj instanceof ErrorActivity) {

				DialogErrorIcon dial = new DialogErrorIcon(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if(dial.open() == Dialog.OK) {
					String type = dial.getType();
					model.changeGraph(type);
					((ErrorActivity) workobj).setTypeact(dial.getTypeact());
				}
			}
			if(workobj instanceof CancelActivity) {

				DialogCancelIcon dial = new DialogCancelIcon(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if(dial.open() == Dialog.OK) {
					String type = dial.getType();
					model.changeGraph(type);
					((CancelActivity) workobj).setTypeact(dial.getTypeact());
				}
			}
			if(workobj instanceof ConpensationActivity) {

				DialogConpensationIcon dial = new DialogConpensationIcon(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if(dial.open() == Dialog.OK) {
					String type = dial.getType();
					model.changeGraph(type);
					((ConpensationActivity) workobj).setTypeact(dial.getTypeact());
				}
			}
			if(workobj instanceof SignalActivity) {

				DialogSignalIcon dial = new DialogSignalIcon(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if(dial.open() == Dialog.OK) {
					String type = dial.getType();
					model.changeGraph(type);
					((SignalActivity) workobj).setTypeact(dial.getTypeact());

				}
			}
			if(workobj instanceof LinkActivity) {

				DialogLinkIcon dial = new DialogLinkIcon(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				if(dial.open() == Dialog.OK) {
					String type = dial.getType();
					model.changeGraph(type);
					((LinkActivity) workobj).setTypeact(dial.getTypeact());
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param model
	 *            the model to set
	 */
	public final void setModel(NodePart model) {
		this.model = model;
	}

	public final void setNode(Node nodet) {
		this.node = nodet;
	}

}
