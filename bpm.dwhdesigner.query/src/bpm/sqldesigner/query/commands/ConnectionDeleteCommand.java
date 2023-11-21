package bpm.sqldesigner.query.commands;

import org.eclipse.gef.commands.Command;

import bpm.sqldesigner.query.model.connection.ConnectionsManager;
import bpm.sqldesigner.query.model.connection.JoinConnection;

public class ConnectionDeleteCommand extends Command {

	private final JoinConnection connection;

	public ConnectionDeleteCommand(JoinConnection conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}
		connection = conn;
	}

	public void execute() {		
		ConnectionsManager.getInstance().removeConnection(connection);
		connection.disconnect();
	}
	
	public void undo() {		
		ConnectionsManager.getInstance().addConnection(connection);
		connection.reconnect();
	}
}
