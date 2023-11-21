package bpm.workflow.ui.actions;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;

import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.commands.LinkCommand;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;
import bpm.workflow.ui.views.ViewPalette;

public class ActionLink extends Action {

	private List objects;
	
	public ActionLink(List objects) {
		super(Messages.ActionLink_0);
		this.objects = objects;
	}

	@Override
	public void run() {
		Activator activator = Activator.getDefault();
		if (objects != null) {
			EditPart source = null;
			EditPart target = null;
			for (int i = 0; i < objects.size(); i++) {
				EditPart object = (EditPart) objects.get(i);
				if (object instanceof NodePart) {
					if (i == 0) {
						source = object;
					}
					else if (i == 1) {
						target = object;
					}
					else {
						//We have more than 2 Node, we don't set links
						source = null;
						target = null;
						break;
					}
				}
			}
			
			if (source != null && target == null) {
				LinkCommand cmd = new LinkCommand((Node) source.getModel());
				activator.setActiveLinkCommand(cmd);
				return;
			}
			else if (source != null && target != null) {
				LinkCommand cmd = new LinkCommand((Node) source.getModel());
				cmd.setTarget((Node) target.getModel());
				if (cmd.canExecute()) {
					cmd.execute();
					return;
				}
			}
		}
		
		ViewPalette v = (ViewPalette) activator.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewPalette.ID);
		v.activateToolEntry(ViewPalette.TOOL_ENTRY_LINK);
		activator.setOnlyOneLink(true);
	}
}
