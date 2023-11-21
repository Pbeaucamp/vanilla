package bpm.sqldesigner.query.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.model.connection.ConnectionsManager;
import bpm.sqldesigner.query.model.connection.JoinConnection;
import bpm.sqldesigner.query.model.filter.ColumnFiltersManager;
import bpm.sqldesigner.query.model.selected.SelectedColumnsManager;

public class TableDeleteCommand extends Command {
	private Node model;
	private Node parentModel;
	private List<JoinConnection> links;
	private List<Column> selectedColumns;

	public void execute() {

		Table table = ((Table) model);
		table.canBeCreated(true);
		List<Node> columns = table.getChildren();

		Iterator<Node> it = columns.iterator();

		links = new ArrayList<JoinConnection>();
		selectedColumns = new ArrayList<Column>();

		while (it.hasNext()) {
			Column c = (Column) it.next();

			ColumnFiltersManager.getInstance().removeForColumn(c);
			/*******************************************************************
			 * Links disconnection
			 ******************************************************************/
			ArrayList<JoinConnection> currenLinks = new ArrayList<JoinConnection>();
			currenLinks.addAll(c.getSourceConnections());
			currenLinks.addAll(c.getTargetConnections());
			Iterator<JoinConnection> itL = currenLinks.iterator();
			while (itL.hasNext()) {
				JoinConnection j = itL.next();
				j.disconnect();
				ConnectionsManager.getInstance().removeConnection(j);
			}
			links.addAll(currenLinks);

			/*******************************************************************
			 * Selects removing
			 ******************************************************************/
			List<Column> toRemove  = new ArrayList<Column>();
			for(Column col : SelectedColumnsManager.getInstance().getColumns()){
				if (col.getName().equals(c.getName()) && col.getParent().getName().equals(c.getParent().getName())){
					toRemove.add(c);
				}
			}
			
			for(Column col : toRemove){
				SelectedColumnsManager.getInstance().removeColumn(col);
				selectedColumns.add(col);
			}
			if (SelectedColumnsManager.getInstance().contains(c)) {
				SelectedColumnsManager.getInstance().removeColumn(c);
				selectedColumns.add(c);
			}
		}

		model.fireRemoveOrAdd();
		parentModel.removeChild(model);
	}

	public void setModel(Object model) {
		this.model = (Node) model;
	}

	public void setParentModel(Object model) {
		parentModel = (Node) model;
	}

	public void undo() {

		Iterator<Column> itC = selectedColumns.iterator();
		while (itC.hasNext())
			itC.next().setSelected(true);

		parentModel.addChild(model);

		((Table) model).canBeCreated(false);

		Iterator<JoinConnection> itL = links.iterator();
		while (itL.hasNext()) {
			JoinConnection j = itL.next();
			j.reconnect();
			ConnectionsManager.getInstance().addConnection(j);
		}
		SelectedColumnsManager.getInstance().addAllColumns(selectedColumns);

		model.fireRemoveOrAdd();
	}
}