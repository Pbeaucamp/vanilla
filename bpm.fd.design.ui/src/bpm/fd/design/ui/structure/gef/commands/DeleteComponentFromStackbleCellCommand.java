package bpm.fd.design.ui.structure.gef.commands;

import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.StackableCell;

public class DeleteComponentFromStackbleCellCommand extends Command{

	private StackableCell parent;
	private IComponentDefinition newElement;
	
	public DeleteComponentFromStackbleCellCommand(){
		
	}
	
	public void setParent(StackableCell parent){
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
