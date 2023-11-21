package bpm.sqldesigner.query.part.tree;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Schema;
import bpm.sqldesigner.query.model.Table;

public class AppTreeEditPartFactory implements EditPartFactory {

	
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;

		if (model instanceof Schema)
			part = new SchemaTreeEditPart();
		else if (model instanceof Table)
			part = new TableTreeEditPart();
		else if (model instanceof Column)
			part = new ColumnTreeEditPart();

		if (part != null)
			part.setModel(model);

		return part;
	}
}