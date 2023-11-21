package bpm.workflow.ui.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.swt.graphics.Point;

import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.Pool;

public class PasteCommand extends Command {

	private Object container;
	private Point point;

	@Override
	public void execute() {
		Node n = (Node) Clipboard.getDefault().getContents();

		if(container instanceof ContainerPanelModel) {
			n.setX(point.x);
			n.setY(point.y);

			n.getWorkflowObject().setPositionX(point.x);
			n.getWorkflowObject().setPositionX(point.y);

			((ContainerPanelModel) container).addChild(n);
		}
		else if(container instanceof Pool)
			((Pool) container).addChild(n);

		Clipboard.getDefault().setContents(null);
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public final void setContainerModel(Object container) {
		this.container = container;
	}

	public void setPosition(Point point) {
		this.point = point;
	}

}
