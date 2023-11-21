package bpm.sqldesigner.query.output;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

import bpm.sqldesigner.query.SQLDesignerComposite;
import bpm.sqldesigner.query.figure.constants.Fonts;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.model.connection.ConnectionsManager;
import bpm.sqldesigner.query.model.connection.JoinConnection;
import bpm.sqldesigner.query.model.selected.SelectedColumnsManager;

public class SqlOutput extends StyledText implements EditPartListener {

	private SQLDesignerComposite viewer;
	private String filter;

	public SqlOutput(Composite parent, int style) {
		super(parent, style);
		viewer = (SQLDesignerComposite) parent.getParent();
		setText("SQL Query\n\n\n\n");
		setEditable(false);
		setFont(Fonts.bigSimple);
		computeTrim(2, 2, 200, 40);
	}

	public void refreshQuery() {
		List<String> fromTables = new ArrayList<String>();

		/***********************************************************************
		 * SELECT
		 **********************************************************************/
		String select = "";
		setText("SELECT");
		Iterator<Column> itColumns = SelectedColumnsManager.getInstance()
				.getColumns().iterator();

		boolean first = true;
		while (itColumns.hasNext()) {
			if (!first) {
				select = select + ",";

			} else
				first = false;

			Column c = itColumns.next();
			Table t = (Table) c.getParent();

			if (!fromTables.contains(t.getName()))
				fromTables.add(t.getName());

			select = select + " ";
			select = select + t.getName() + "." + c.getName();

		}
		/***********************************************************************
		 * WHERE
		 **********************************************************************/
		String where = "";

		Iterator<JoinConnection> itConnections = ConnectionsManager
				.getInstance().getConnections().iterator();

		first = true;

		while (itConnections.hasNext()) {
			if (!first) {
				where = where + " AND";
			} else {
				where = where + '\n' + "WHERE";
				first = false;
			}

			JoinConnection link = itConnections.next();
			Column cSource = (Column) link.getSource();
			Column cTarget = (Column) link.getTarget();
			Table tSource = (Table) cSource.getParent();
			Table tTarget = (Table) cTarget.getParent();

			if (!fromTables.contains(tSource.getName()))
				fromTables.add(tSource.getName());
			if (!fromTables.contains(tTarget.getName()))
				fromTables.add(tTarget.getName());

			where = where + " ";
			where = where + tSource.getName() + "." + cSource.getName();
			where = where + "=";
			where = where + tTarget.getName() + "." + cTarget.getName();
		}

		if (filter != null && !filter.equals("")) {
			if (where.equals(""))
				where = where + '\n' + "WHERE";
			else
				where = where + " AND";

			where = where + " ( " + filter + " )";
		}

		/***********************************************************************
		 * FROM
		 **********************************************************************/
		String from = "";
		Iterator<String> itTables = fromTables.iterator();

		first = true;
		from = from + '\n' + "FROM";

		while (itTables.hasNext()) {
			if (!first) {
				from = from + ",";
			} else if (first) {
				first = false;
			}

			String t = itTables.next();

			from = from + " ";
			from = from + t;

		}

		append(select);
		append(from);
		append(where);

		viewer.setImageTestQueryVisible(false);
		redraw();

	}

	public void childAdded(EditPart child, int index) {
	}

	public void partActivated(EditPart editpart) {
	}

	public void partDeactivated(EditPart editpart) {
	}

	public void removingChild(EditPart child, int index) {
	}

	public void selectedStateChanged(EditPart editpart) {
		refreshQuery();
	}

	public void setFilter(String text) {
		filter = text;
		refreshQuery();
	}

}
