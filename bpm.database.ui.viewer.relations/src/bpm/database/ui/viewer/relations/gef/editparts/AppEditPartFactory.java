package bpm.database.ui.viewer.relations.gef.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.database.ui.viewer.relations.model.Column;
import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Table;

public class AppEditPartFactory implements EditPartFactory {
		

	public EditPart createEditPart(EditPart context, Object model) {
		AbstractGraphicalEditPart part = null;

		if (model instanceof Column) {
			part = new ColumnPart();
//			part.addEditPartListener(outputQueryListener);

		} else if (model instanceof Table) {
			part = new TablePart();
//			part.addEditPartListener(outputQueryListener);
			
		} else if (model instanceof JoinConnection) {
			part = new ConnectionPart();
//			part.addEditPartListener(outputQueryListener);
		}else{
			part = new SchemaPart();
//			part.addEditPartListener(outputQueryListener);
		}

		part.setModel(model);
		return part;
	}
}