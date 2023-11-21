package bpm.sqldesigner.ui.command;

import org.eclipse.draw2d.geometry.Rectangle;

import bpm.sqldesigner.api.model.Table;


public class TableChangeLayoutCommand extends AbstractLayoutCommand {
	private Table model;
	private Rectangle layout;
	private int[] oldLayout;

	@Override
	public void execute() {
		int[] oldLayout = model.getLayout();

		if (oldLayout[3] == layout.height
				&& oldLayout[2] == layout.width) {
			model.setLayout(layout.x,layout.y,layout.width,layout.height);

		}
	}

	@Override
	public void setConstraint(Rectangle rect) {
		layout = rect;
	}

	@Override
	public void setModel(Object model) {
		this.model = (Table) model;
		oldLayout = ((Table) model).getLayout();
	}

	@Override
	public void undo() {
		model.setLayout(oldLayout[0],oldLayout[1],oldLayout[2],oldLayout[3]);
	}
}