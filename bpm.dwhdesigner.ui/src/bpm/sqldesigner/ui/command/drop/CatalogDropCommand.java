package bpm.sqldesigner.ui.command.drop;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.view.RequestsView;

public class CatalogDropCommand extends NodeDropCommand {

	@Override
	public String getText() {
		return "catalog " + node.getName(); //$NON-NLS-1$
	}

	@Override
	public void removeNode() {
		Catalog catalog = (Catalog) node;
		catalog.getCluster().removeCatalog(catalog.getName());

		RequestsView view = (RequestsView) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(RequestsView.ID);
		view.getTab(node.getCluster()).nodeDropped(node);
	}

	@Override
	public void undo() {
		Catalog catalog = (Catalog) node;
		catalog.getCluster().addCatalog(catalog);
	}
}