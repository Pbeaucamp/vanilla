package bpm.sqldesigner.query.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Table;
import bpm.sqldesigner.query.model.connection.JoinConnection;

public class TableChangeLayoutCommand extends AbstractLayoutCommand {
	private Table model;
	private Rectangle layout;
	private Rectangle oldLayout;

	public void execute() {
		Rectangle oldLayout = model.getLayout();

		if (oldLayout.height == layout.height
				&& oldLayout.width == layout.width) {
			model.setLayout(layout);

			List<Node> columns = model.getChildren();
			Iterator<Node> it = columns.iterator();

			while (it.hasNext()) {
				Column c = (Column) it.next();

				List<JoinConnection> l = new ArrayList<JoinConnection>();
				l.addAll(c.getSourceConnections());
				l.addAll(c.getTargetConnections());

				Iterator<JoinConnection> it2 = l.iterator();

				while (it2.hasNext()) {

					it2.next().getListeners().firePropertyChange(
							Node.CONNECTIONS_PROP, null,null);
				}
			}
		}
	}

	public void setConstraint(Rectangle rect) {
		layout = rect;
	}

	public void setModel(Object model) {
		this.model = (Table) model;
		oldLayout = ((Table) model).getLayout();
	}

	public void undo() {
		model.setLayout(oldLayout);
	}
}