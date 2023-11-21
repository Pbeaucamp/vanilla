package bpm.sqldesigner.ui.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.view.RequestsView;
import bpm.sqldesigner.ui.view.tab.TabRequests;

public class AppEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		AbstractGraphicalEditPart part = null;

		boolean knownModel = true;

		if (model instanceof Schema) {
			part = new SchemaPart();
		} else if (model instanceof DocumentSnapshot) {
			part = new SnapshotPart();
		}  else if (model instanceof Column) {
			part = new ColumnPart();
		} else if (model instanceof Table) {
			part = new TablePart();
		} else if (model instanceof LinkForeignKey) {
			part = new ConnectionPart();
		} else if (model instanceof SQLView) {
			part = new ViewPart();
		} else
			knownModel = false;

		if (knownModel) {
			part.setModel(model);

			Node node = (Node) model;

			if (node.getDatabaseConnection() != null)
				if (part instanceof AppAbstractEditPart) {
					TabRequests tab = ((RequestsView) Activator.getDefault()
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().findView(RequestsView.ID))
							.getTab(node.getCluster());
					((AppAbstractEditPart) part).requestListener = tab;
				}

		}
		return part;
	}
}