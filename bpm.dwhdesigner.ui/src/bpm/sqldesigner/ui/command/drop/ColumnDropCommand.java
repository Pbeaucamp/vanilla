package bpm.sqldesigner.ui.command.drop;

import java.util.List;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.view.RequestsView;

public class ColumnDropCommand extends NodeDropCommand {

	private LinkForeignKey tpk;
	private List<LinkForeignKey> links;

	@Override
	public String getText() {
		return "column " + node.getName(); //$NON-NLS-1$
	}

	@Override
	public void removeNode() {
		Column column = (Column) node;
		column.getTable().removeColumn(column.getName());
		tpk = column.getTargetPrimaryKey();
		links = column.getSourceForeignKeys();

		if (tpk != null)
			tpk.disconnect();

		for (LinkForeignKey link : links)
			link.disconnect();

		RequestsView view = (RequestsView) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(RequestsView.ID);
		view.getTab(node.getCluster()).nodeDropped(node);
	}

	@Override
	public void undo() {
		Column column = (Column) node;
		column.getTable().addColumn(column);

		if (tpk != null)
			tpk.reconnect();
		for (LinkForeignKey link : links)
			link.reconnect();

	}
}