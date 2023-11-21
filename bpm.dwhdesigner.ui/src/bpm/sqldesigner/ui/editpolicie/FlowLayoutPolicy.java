package bpm.sqldesigner.ui.editpolicie;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.ui.command.CreateConnectionCommand;
import bpm.sqldesigner.ui.command.creation.ColumnCreateCommand;
import bpm.sqldesigner.ui.editpart.ColumnPart;
import bpm.sqldesigner.ui.editpart.TablePart;
import bpm.sqldesigner.ui.figure.ColumnFigure;

public class FlowLayoutPolicy extends FlowLayoutEditPolicy {

	private ColumnFigure figure = null;

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {

		if (child instanceof ColumnPart) {
			if (after instanceof ColumnPart) {
				ColumnFigure fig = (ColumnFigure) ((ColumnPart) after)
						.getFigure();
				fig.toRed();

				if (figure != fig && figure != null)
					figure.toBlack();

				figure = fig;

				return new CreateConnectionCommand((Column) child.getModel(),
						(Column) after.getModel());
			}
		}
		return null;
	}

	@Override
	protected void eraseLayoutTargetFeedback(Request request) {
		if (figure != null)
			figure.toBlack();

		super.eraseLayoutTargetFeedback(request);
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getType() == REQ_CREATE && getHost() instanceof TablePart) {
			ColumnCreateCommand cmd = new ColumnCreateCommand();

			cmd.setTable(getHost().getModel());
			cmd.setColumn(request.getNewObject());

			return cmd;
		}
		return null;
	}

	@Override
	protected boolean isHorizontal() {
		IFigure figure = ((GraphicalEditPart) getHost()).getContentPane();
		LayoutManager layout = figure.getLayoutManager();
		if (layout instanceof FlowLayout)
			return ((FlowLayout) figure.getLayoutManager()).isHorizontal();
		if (layout instanceof ToolbarLayout)
			return ((ToolbarLayout) figure.getLayoutManager()).isHorizontal();
		return false;
	}

	@Override
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

}
