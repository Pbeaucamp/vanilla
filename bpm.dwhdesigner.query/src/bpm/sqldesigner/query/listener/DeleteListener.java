package bpm.sqldesigner.query.listener;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.sqldesigner.query.SQLDesignerComposite;

public class DeleteListener implements Listener {

	private SQLDesignerComposite designerComposite;

	public DeleteListener(SQLDesignerComposite designerComposite) {
		this.designerComposite = designerComposite;
	}

	
	public void handleEvent(Event event) {

		Command command = createDeleteCommand(designerComposite.getEditor().getSelectedEditParts());
		if (command == null || !command.canExecute())
			return;
		designerComposite.getDomain().getCommandStack().execute(command);
	}

	protected Command createDeleteCommand(List objects) {
		if (objects.isEmpty())
			return null;
		if (!(objects.get(0) instanceof EditPart))
			return null;

		GroupRequest deleteReq = new GroupRequest(RequestConstants.REQ_DELETE);
		deleteReq.setEditParts(objects);

		CompoundCommand compoundCmd = new CompoundCommand(
				GEFMessages.DeleteAction_ActionDeleteCommandName);
		for (int i = 0; i < objects.size(); i++) {
			EditPart object = (EditPart) objects.get(i);
			Command cmd = object.getCommand(deleteReq);
			if (cmd != null)
				compoundCmd.add(cmd);
		}
		return compoundCmd;
	}
}
