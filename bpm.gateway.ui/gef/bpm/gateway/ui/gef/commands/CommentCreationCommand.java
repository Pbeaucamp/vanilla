package bpm.gateway.ui.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.gateway.core.Comment;
import bpm.gateway.ui.gef.model.ContainerPanelModel;

public class CommentCreationCommand extends Command {

	private ContainerPanelModel parent;
	private Comment annote;
	
	/**
	 * @param parent the parent to set
	 */
	public final void setParent(ContainerPanelModel parent) {
		this.parent = parent;
	}
	/**
	 * @param newNode the newNode to set
	 */
	public final void setNewObject(Comment annote) {
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
		parent.addAnnote(annote);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		parent.addAnnote(annote);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		parent.removeAnnote(annote);
	}
	
	
}
