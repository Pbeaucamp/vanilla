package bpm.sqldesigner.query.editpolicies;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.widgets.Composite;

import bpm.sqldesigner.query.SQLDesignerComposite;
import bpm.sqldesigner.query.commands.AbstractLayoutCommand;
import bpm.sqldesigner.query.commands.TableChangeLayoutCommand;
import bpm.sqldesigner.query.commands.creation.TableCreateCommand;
import bpm.sqldesigner.query.figure.TableFigure;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.part.SchemaPart;
import bpm.sqldesigner.query.part.TablePart;

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
		
		Rectangle rect = ((SchemaPart)getHost()).getFigure().getBounds();

		constraintRect.x = (constraintRect.x < 0) ? 0 : constraintRect.x;
		constraintRect.y = (constraintRect.y < 0) ? 0 : constraintRect.y;
		
		constraintRect.x = (constraintRect.x > rect.width) ? rect.width-50 : constraintRect.x;
		constraintRect.y = (constraintRect.y > rect.height) ? rect.height-50 : constraintRect.y;
		
		
		
		command.setConstraint(constraintRect);

		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getType() == REQ_CREATE && getHost() instanceof SchemaPart) {
			TableCreateCommand cmd = new TableCreateCommand();

			cmd.setSchema(getHost().getModel());
			Composite cmp = getHost().getViewer().getControl().getParent();
			
			while(!((cmp= cmp.getParent()) instanceof SQLDesignerComposite)){
				if ( cmp == null){
					break;
				}
			}
			
			Table table = (Table) request.getNewObject();
			cmd.setTable(table, ((SQLDesignerComposite)cmp).getPalette());

			Rectangle constraint = (Rectangle) getConstraintFor(request);

			constraint.x = (constraint.x < 0) ? 0 : constraint.x;
			constraint.y = (constraint.y < 0) ? 0 : constraint.y;
			
			Iterator<?> it = table.getChildren().iterator();
			int lMax = 0;
			int l = 0;
			while (it.hasNext()) {
				Column c = (Column) it.next();
				l = (c.getName() + "---" + c.getType()).length();

				if (c.isKey())
					l = l +4;
				
				if (l > lMax)
					lMax = l;
			}
			constraint.width = TableFigure.TABLE_FIGURE_DEFWIDTH * lMax+65;
			//constraint.width = 300;
			constraint.height = TableFigure.TABLE_FIGURE_DEF_BESTHEIGHT
					* table.getChildrenNumber();
			cmd.setLayout(constraint);
			return cmd;
		}
		return null;
	}
}