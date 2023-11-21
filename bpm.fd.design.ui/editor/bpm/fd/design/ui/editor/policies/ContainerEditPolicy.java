package bpm.fd.design.ui.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import bpm.fd.design.ui.editor.commands.RemoveItemCommand;

public class ContainerEditPolicy extends ComponentEditPolicy{
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		RemoveItemCommand command = new RemoveItemCommand();
		command.setHost(getHost().getParent());
		command.setItem(deleteRequest.getEditParts());
		
		
		return command;
	}
	
//	public Command getCommand(org.eclipse.gef.Request request) {
//		
//	};
}
