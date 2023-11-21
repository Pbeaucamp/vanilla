package bpm.gateway.ui.gef.commands;

import org.eclipse.gef.commands.Command;

import bpm.gateway.ui.gef.model.Link;
import bpm.gateway.ui.gef.model.NodeLinkerHelper;
import bpm.gateway.ui.i18n.Messages;

public class LinkDeleteCommand extends Command {
	
	private final Link connection;
	
	public LinkDeleteCommand(Link conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		setLabel(Messages.LinkDeleteCommand_0);
		this.connection = conn;
	}

	public void execute() {
		connection.disconnect();
		NodeLinkerHelper.remove(connection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		execute();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		try {
			connection.reconnect();
			NodeLinkerHelper.add(connection);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	
}
