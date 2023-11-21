package bpm.gateway.ui.gef.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import bpm.gateway.core.Comment;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Node;

public class ChangeLayoutCommand extends Command {

	private Object model;
	private Rectangle layout;
	private Rectangle oldLayout;
	
	
	@Override
	public void execute() {
		
		oldLayout = new Rectangle(layout);
		
		
		if (model instanceof GIDModel){
			((GIDModel)model).setX(layout.x);
			((GIDModel)model).setY(layout.y);
			((GIDModel)model).setWidth(layout.width);
			((GIDModel)model).setHeight(layout.height);
		}
		else if (model instanceof Node){
			((Node)model).setLayout(layout);
		}
		else if (model instanceof Comment){
			((Comment)model).setX(layout.x);
			((Comment)model).setY(layout.y);
			((Comment)model).setWidth(layout.width);
			((Comment)model).setHeight(layout.height);
		}
		
		
	}



	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		if (model instanceof GIDModel){
			((GIDModel)model).setX(layout.x);
			((GIDModel)model).setY(layout.y);
			((GIDModel)model).setWidth(layout.width);
			((GIDModel)model).setHeight(layout.height);
		}
		else if (model instanceof Node){
			((Node)model).setLayout(layout);
		}
		else if (model instanceof Comment){
			((Comment)model).setX(layout.x);
			((Comment)model).setY(layout.y);
			((Comment)model).setWidth(layout.width);
			((Comment)model).setHeight(layout.height);
		}
	}



	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (model instanceof GIDModel){
			((GIDModel)model).setX(oldLayout.x);
			((GIDModel)model).setY(oldLayout.y);
			((GIDModel)model).setWidth(oldLayout.width);
			((GIDModel)model).setHeight(oldLayout.height);
		}
		else if (model instanceof Node){
			((Node)model).setLayout(oldLayout);
		}
		else if (model instanceof Comment){
			((Comment)model).setX(oldLayout.x);
			((Comment)model).setY(oldLayout.y);
			((Comment)model).setWidth(oldLayout.width);
			((Comment)model).setHeight(oldLayout.height);
		}
	}



	/**
	 * @param model the model to set
	 */
	public final void setModel(Object model) {
		this.model = model;
	}



	/**
	 * @param layout the layout to set
	 */
	public final void setLayout(Rectangle layout) {
		this.layout = layout;
	}
	
	
	
	
}
