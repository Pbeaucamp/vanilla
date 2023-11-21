package bpm.database.ui.viewer.relations.gef.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Node;

public class TableChangeLayoutCommand extends AbstractLayoutCommand {
	private Node model;
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
				Node c = (Node) it.next();

				List<JoinConnection> l = new ArrayList<JoinConnection>();
				l.addAll(c.getSourceConnections());
				l.addAll(c.getTargetConnections());

				

				
			}
		}
	}

	public void setConstraint(Rectangle rect) {
		layout = rect;
	}

	public void setModel(Object model) {
		this.model = (Node) model;
		oldLayout = ((Node) model).getLayout();
	}

	public void undo() {
		model.setLayout(oldLayout);
	}
}