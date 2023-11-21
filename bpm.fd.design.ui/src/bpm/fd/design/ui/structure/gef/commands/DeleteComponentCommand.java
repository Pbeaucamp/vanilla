package bpm.fd.design.ui.structure.gef.commands;

import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;

public class DeleteComponentCommand extends Command{

	private Cell parent;
	private IComponentDefinition newElement;
	
	public DeleteComponentCommand(){
		
	}
	
	public void setParent(Cell parent){
		this.parent = parent;
	}
	
	
	public void setNewElement(IComponentDefinition element){
		this.newElement = element;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return parent != null && newElement != null;
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	@Override
	public boolean canUndo() {
		return parent != null && newElement != null ;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	@Override
	public void execute() {
		parent.removeBaseElementToContent(newElement);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		parent.removeBaseElementToContent(newElement);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		parent.addBaseElementToContent(newElement);
		
	}
}
