package bpm.sqldesigner.query.editpolicies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import bpm.sqldesigner.query.commands.MoveColumnCommand;
import bpm.sqldesigner.query.part.ColumnPart;

public class FlowAppEditLayoutPolicy extends FlowLayoutEditPolicy {

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {
		return new MoveColumnCommand((ColumnPart) getHost(), (ColumnPart) child);

	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}
}