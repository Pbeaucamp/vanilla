package bpm.database.ui.viewer.relations.gef.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import bpm.database.ui.viewer.relations.gef.commands.AbstractLayoutCommand;
import bpm.database.ui.viewer.relations.gef.commands.TableChangeLayoutCommand;
import bpm.database.ui.viewer.relations.gef.editparts.SchemaPart;
import bpm.database.ui.viewer.relations.gef.editparts.TablePart;

public class AppEditLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {

		AbstractLayoutCommand command = null;

		if(child instanceof TablePart) {
			command = new TableChangeLayoutCommand();
		}

		command.setModel(child.getModel());

		Rectangle constraintRect = (Rectangle) constraint;

		Rectangle rect = ((SchemaPart) getHost()).getFigure().getBounds();

		constraintRect.x = (constraintRect.x < 0) ? 0 : constraintRect.x;
		constraintRect.y = (constraintRect.y < 0) ? 0 : constraintRect.y;

		constraintRect.x = (constraintRect.x > rect.width) ? rect.width - 50 : constraintRect.x;
		constraintRect.y = (constraintRect.y > rect.height) ? rect.height - 50 : constraintRect.y;

		command.setConstraint(constraintRect);

		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}
}