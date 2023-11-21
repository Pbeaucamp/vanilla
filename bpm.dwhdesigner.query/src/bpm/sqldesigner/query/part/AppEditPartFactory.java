package bpm.sqldesigner.query.part;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Schema;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.model.connection.JoinConnection;
import bpm.sqldesigner.query.output.SqlOutputManager;

public class AppEditPartFactory implements EditPartFactory {
	private EditPartListener outputQueryListener = null;
	

	public EditPart createEditPart(EditPart context, Object model) {
		AbstractGraphicalEditPart part = null;

		
		if (model instanceof Schema) {
			part = new SchemaPart();
			
		} else if (model instanceof Column) {
			part = new ColumnPart();
			part.addEditPartListener(outputQueryListener);

		} else if (model instanceof Table) {
			if (outputQueryListener == null)
				outputQueryListener = SqlOutputManager.getInstanceIfExists();
			part = new TablePart();
			part.addEditPartListener(outputQueryListener);
			
		} else if (model instanceof JoinConnection) {
			part = new ConnectionPart();
			part.addEditPartListener(outputQueryListener);
		}

		part.setModel(model);
		return part;
	}
}