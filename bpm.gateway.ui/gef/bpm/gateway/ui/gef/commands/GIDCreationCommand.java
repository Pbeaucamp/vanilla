package bpm.gateway.ui.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.GIDModel;

public class GIDCreationCommand extends Command {

	private ContainerPanelModel parent;
	private GIDModel annote;
	
	/**
	 * @param parent the parent to set
	 */
	public final void setParent(ContainerPanelModel parent) {
		this.parent = parent;
	}
	/**
	 * @param newNode the newNode to set
	 */
	public final void setNewObject(GIDModel annote) {
		this.annote = annote;
	}
	
	public void setLayout(Rectangle layout){
		if (annote == null){
			return;
		}
		this.annote.setX(layout.x);
		this.annote.setY(layout.y);
		this.annote.setWidth(layout.width);
		this.annote.setHeight(layout.height);
	}
	
	
	public void execute(){
		parent.addChild(annote);
	}
	
	
}
