package bpm.workflow.ui.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;

public class MacroProcessCreationCommand extends Command {

	private Object parent;
	private MacroProcessModel annote;

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
	public final void setNewObject(MacroProcessModel annote) {
		this.annote = annote;
	}

	public void setLayout(Rectangle layout) {
		if(annote == null) {
			return;
		}
		this.annote.setX(layout.x);
		this.annote.setY(layout.y);
		this.annote.setWidth(layout.width);
		this.annote.setHeight(layout.height);
	}

	public void execute() {
		if(parent instanceof ContainerPanelModel) {
			((ContainerPanelModel) parent).addChild(annote);
		}
	}

}
