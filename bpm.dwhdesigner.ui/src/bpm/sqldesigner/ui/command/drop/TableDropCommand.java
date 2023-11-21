package bpm.sqldesigner.ui.command.drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.view.RequestsView;

public class TableDropCommand extends NodeDropCommand {

	private ArrayList<LinkForeignKey> links = null;

	@Override
	public String getText() {
		return "table " + node.getName(); //$NON-NLS-1$
	}

	@Override
	public void removeNode() {
		Table table = (Table) node;
		table.getSchema().removeTable(table.getName());

		HashMap<String, Column> columns = table.getColumns();
		Iterator<String> it = columns.keySet().iterator();

		links = new ArrayList<LinkForeignKey>();

		while (it.hasNext()) {
			Column c = columns.get(it.next());

			/*******************************************************************
			 * Links disconnection
			 ******************************************************************/
			ArrayList<LinkForeignKey> currentLinks = new ArrayList<LinkForeignKey>();
			currentLinks.addAll(c.getSourceForeignKeys());
			if (c.isForeignKey())
				currentLinks.add(c.getTargetPrimaryKey());

			for (LinkForeignKey link : currentLinks)
				link.disconnect();

			links.addAll(currentLinks);
		}

		RequestsView view = (RequestsView) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(RequestsView.ID);
		view.getTab(node.getCluster()).nodeDropped(node);
	}

	@Override
	public void undo() {
		Table table = (Table) node;

		for (LinkForeignKey link : links)
			link.reconnect();

		table.getSchema().addTable(table);
	}
}