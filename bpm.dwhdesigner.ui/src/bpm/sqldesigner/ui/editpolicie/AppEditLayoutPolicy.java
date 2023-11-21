package bpm.sqldesigner.ui.editpolicie;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import bpm.sqldesigner.ui.command.AbstractLayoutCommand;
import bpm.sqldesigner.ui.command.TableChangeLayoutCommand;
import bpm.sqldesigner.ui.command.creation.TableCreateCommand;
import bpm.sqldesigner.ui.editpart.SchemaPart;
import bpm.sqldesigner.ui.editpart.TablePart;

public class AppEditLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		AbstractLayoutCommand command = null;

		if (child instanceof TablePart) {
			command = new TableChangeLayoutCommand();
		}

		command.setModel(child.getModel());

		Rectangle constraintRect = (Rectangle) constraint;

		Rectangle rect = ((AbstractGraphicalEditPart) getHost()).getFigure().getBounds();

		constraintRect.x = (constraintRect.x < 0) ? 0 : constraintRect.x;
		constraintRect.y = (constraintRect.y < 0) ? 0 : constraintRect.y;

		constraintRect.x = (constraintRect.x > rect.width) ? rect.width - 50
				: constraintRect.x;
		constraintRect.y = (constraintRect.y > rect.height) ? rect.height - 50
				: constraintRect.y;

		command.setConstraint(constraintRect);

		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getType() == REQ_CREATE
				&& getHost() instanceof SchemaPart) {
			TableCreateCommand cmd = new TableCreateCommand();

			cmd.setSchema(getHost().getModel());
			cmd.setTable(request.getNewObject());

			Rectangle constraint = (Rectangle) getConstraintFor(request);
			constraint.x = (constraint.x < 0) ? 0 : constraint.x;
			constraint.y = (constraint.y < 0) ? 0 : constraint.y;

			cmd.setLayout(new int[]{constraint.x,constraint.y,200,100});
			return cmd;
		}
		return null;
	}

}