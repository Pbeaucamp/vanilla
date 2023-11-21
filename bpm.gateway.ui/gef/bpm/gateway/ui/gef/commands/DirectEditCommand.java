package bpm.gateway.ui.gef.commands;

import org.eclipse.gef.commands.Command;

import bpm.gateway.core.Comment;

public class DirectEditCommand extends Command{

	private Comment model;
	private String value;
	private String oldValue;
	
	
	public DirectEditCommand(Comment model, String value){
		this.model = model;
		
		this.value = value;
	}
	
	public void execute(){
		this.oldValue = model.getContent();
		model.setContent(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		model.setContent(value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		model.setContent(oldValue);
	}
	
	
}
