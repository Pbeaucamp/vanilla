package bpm.sqldesigner.query.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import bpm.sqldesigner.query.commands.ConnectionDeleteCommand;
import bpm.sqldesigner.query.commands.TableDeleteCommand;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.model.connection.JoinConnection;

public class AppDeletePolicy extends ComponentEditPolicy {

	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Node model = (Node) getHost().getModel();
		if (model instanceof JoinConnection) {
			ConnectionDeleteCommand command = new ConnectionDeleteCommand(
					(JoinConnection) model);
			return command;
		} else if (model instanceof Table) {
			TableDeleteCommand command = new TableDeleteCommand();
			command.setModel(model);
			command.setParentModel(getHost().getParent().getModel());
			return command;
		}
		
		return null;
	}
}