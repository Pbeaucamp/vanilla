package bpm.sqldesigner.ui.command.drop;

import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.view.RequestsView;

public class SchemaDropCommand extends NodeDropCommand {

	@Override
	public String getText() {
		return "schema " + node.getName(); //$NON-NLS-1$
	}

	@Override
	public void removeNode() {
		Schema schema = (Schema) node;
		schema.getCatalog().removeSchema(schema.getName());

		RequestsView view = (RequestsView) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(RequestsView.ID);
		view.getTab(node.getCluster()).nodeDropped(node);
	}

	@Override
	public void undo() {
		Schema schema = (Schema) node;
		schema.getCatalog().addSchema(schema);
	}
}