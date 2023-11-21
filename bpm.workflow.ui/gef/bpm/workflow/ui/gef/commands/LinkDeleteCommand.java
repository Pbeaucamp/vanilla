package bpm.workflow.ui.gef.commands;

import org.eclipse.gef.commands.Command;

import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Link;
import bpm.workflow.ui.gef.model.NodeLinkerHelper;

public class LinkDeleteCommand extends Command {

	private final Link connection;

	public LinkDeleteCommand(Link conn) {
		if(conn == null) {
			throw new IllegalArgumentException();
		}
		setLabel(Messages.LinkDeleteCommand_0);
		this.connection = conn;
	}

	public void execute() {
		connection.disconnect();
		NodeLinkerHelper.remove(connection);
	}
}
