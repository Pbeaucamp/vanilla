package bpm.workflow.ui.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;

public class ChangeLayoutCommand extends Command {

	private Object model;
	private Rectangle layout;
	private int translateX;
	private int translateY;

	@Override
	public void execute() {

		if(model instanceof LoopModel) {
			((LoopModel) model).setWidth(layout.width);
			((LoopModel) model).setHeight(layout.height);
			int _translateX = layout.x - ((LoopModel) model).getLayout().x;
			int _translateY = layout.y - ((LoopModel) model).getLayout().y;
			((LoopModel) model).setLayout(layout, 0, 0);
			for(Node n : ((LoopModel) model).getContents()) {
				n.setLayout(n.getLayout(), _translateX, _translateY);
			}

		}
		else if(model instanceof MacroProcessModel) {

			((MacroProcessModel) model).setX(layout.x);
			((MacroProcessModel) model).setY(layout.y);
			((MacroProcessModel) model).setWidth(layout.width);
			((MacroProcessModel) model).setHeight(layout.height);
		}
		else if(model instanceof Node) {
			((Node) model).setLayout(layout, translateX, translateY);

		}
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public final void setModel(Object model) {
		this.model = model;
	}

	/**
	 * @param layout
	 *            the layout to set
	 */
	public final void setLayout(Rectangle layout, Rectangle bounds) {
		this.layout = layout;
		this.translateX = bounds.x;
		this.translateY = bounds.y;
	}

	public final void setLayout(Rectangle layout) {
		this.layout = layout;
	}

}
