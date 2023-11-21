package bpm.workflow.ui.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.Pool;

public class NodeCreationCommand extends Command {
	private Object parent;
	private Node newNode;

	/**
	 * @param parent
	 *            the parent to set
	 */
	public final void setParent(Object parent) {
		this.parent = parent;
	}

	/**
	 * @param newNode
	 *            the newNode to set
	 */
	public final void setNewObject(Node newNode) {
		this.newNode = newNode;
	}

	public void setLayout(Rectangle layout, Rectangle bounds) {
		if(newNode == null) {
			return;
		}

		((Node) this.newNode).setLayout(layout, bounds.x, bounds.y);

	}

	public void setLayout(Rectangle layout) {
		if(newNode == null) {
			return;
		}
		((Node) this.newNode).setLayout(layout);

	}

	public void execute() {
		if(parent instanceof ContainerPanelModel) {
			((ContainerPanelModel) parent).addChild(newNode);

		}
		else if(parent instanceof LoopModel) {
			((IActivity) newNode.getWorkflowObject()).setParent(((LoopModel) parent).getWorkflowObject().getName());

			((LoopModel) parent).addChild(newNode);
		}
		else if(parent instanceof MacroProcessModel) {
			((IActivity) newNode.getWorkflowObject()).setParent(((MacroProcessModel) parent).getWorkflowObject().getName());

			((MacroProcessModel) parent).addChild(newNode);
		}
		else if(parent instanceof Pool) {
			((Pool) parent).addNewChild(newNode);
		}

	}

}
