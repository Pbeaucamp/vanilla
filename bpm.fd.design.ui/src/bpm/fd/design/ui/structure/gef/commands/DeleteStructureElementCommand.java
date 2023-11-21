package bpm.fd.design.ui.structure.gef.commands;

import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;

public class DeleteStructureElementCommand extends Command{

	private Object parent;
	private IStructureElement newElement;
	
	public DeleteStructureElementCommand(){
		
	}
	
	public void setParent(Object parent){
		this.parent = parent;
	}
	
	
	public void setNewElement(IStructureElement element){
		this.newElement = element;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	@Override
	public boolean canExecute() {
		return parent != null && newElement != null && (newElement instanceof Table || newElement instanceof Folder || newElement instanceof StackableCell || newElement instanceof DrillDrivenStackableCell);
		

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
		((IStructureElement)parent).removeFromContent(newElement);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		if (parent instanceof IStructureElement){
			((IStructureElement)parent).removeFromContent(newElement);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (parent instanceof IStructureElement){
			((IStructureElement)parent).addToContent(newElement);
		}
		
	}
}
