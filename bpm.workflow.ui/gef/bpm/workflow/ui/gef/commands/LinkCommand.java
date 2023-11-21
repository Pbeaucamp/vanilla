package bpm.workflow.ui.gef.commands;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.workflow.runtime.model.activities.InterfaceGoogleActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Link;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.views.ViewPalette;

public class LinkCommand extends Command {
	private Link link;
	private Node source, target;

	public LinkCommand(Node source) {
		this.source = source;

	}

	@Override
	public boolean canExecute() {
		for(Iterator iter = source.getSourceLink().iterator(); iter.hasNext();) {
			Link conn = (Link) iter.next();
			if(conn.getTarget().equals(target)) {
				return false;
			}
		}

		return source != target;
	}

	@Override
	public void execute() {
		try {
			if(source.getWorkflowObject() instanceof ReportActivity) {
				ReportActivity a = (ReportActivity) source.getWorkflowObject();

				if(a.getBiObject() == null) {
					throw new Exception(Messages.LinkCommand_0);
				}
			}
			if(source.getWorkflowObject() instanceof InterfaceGoogleActivity) {
				if(!(target.getWorkflowObject() instanceof StopActivity)) {
					throw new Exception(Messages.LinkCommand_1);

				}
			}
			link = new Link(source, target);


			Activator activator = Activator.getDefault();
			if (activator.isOnlyOneLink()) {
				ViewPalette v = (ViewPalette) activator.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewPalette.ID);
				v.activateToolEntry(ViewPalette.TOOL_ENTRY_SELECT);
				
				activator.setOnlyOneLink(false);
			}
		} catch(Exception e) {
			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(shell, Messages.LinkCommand_2, e.getMessage());
		}

	}

	public void setTarget(Node target) {
		if(target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}

	public Link getLink() {
		return link;
	}
}
