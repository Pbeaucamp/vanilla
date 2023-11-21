package bpm.fd.design.ui.structure.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.Row;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.structure.dialogs.creation.DialogTable;

public class StructureCreateCommand extends Command{

	private Object parent;
	private IStructureElement newElement;
	private Shell shell;
	
	
	public StructureCreateCommand(Shell shell){
		this.shell = shell;
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
//		if (newElement == null){
//			if (parent instanceof FdModel){
//				return true;
//			}
//		}
		boolean b = parent != null && newElement != null && newElement.getParentStructureElement() == null;
		
		if (!b){
			return false;
		}
		
		if ((parent instanceof Table && newElement instanceof Cell) ||
			(parent instanceof Row && newElement instanceof Cell) ||
			(parent instanceof Cell && newElement instanceof Table) || 
			(parent instanceof FdModel && newElement instanceof Table) || 
			(parent instanceof FdModel && newElement instanceof Folder && Activator.getDefault().getProject() instanceof MultiPageFdProject && parent == Activator.getDefault().getProject().getFdModel()) ||
			(parent instanceof Folder && newElement instanceof FolderPage) ||
			(parent instanceof Cell && newElement instanceof StackableCell) ||
			(parent instanceof Cell && newElement instanceof DrillDrivenStackableCell) || 
			(parent instanceof Cell && newElement instanceof DivCell)){
			return true;
		}
		return false;
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
		if (newElement instanceof Table){
			DialogTable dial = new DialogTable(shell);
			
			if (dial.open() == DialogTable.OK){
				FactoryStructure factory = Activator.getDefault().getProject().getFdModel().getStructureFactory();
				newElement = factory.createTable(Messages.StructureCreateCommand_0);
				((Table)newElement).initSize(factory, dial.getCols(), dial.getRows());
			}
			else{
				return;
			}
			
		}
		else if (newElement instanceof Folder){
			FactoryStructure factory = Activator.getDefault().getProject().getFdModel().getStructureFactory();
			newElement = factory.createFolder(Messages.StructureCreateCommand_1);
		}
		else if (newElement instanceof FolderPage){
			FactoryStructure factory = Activator.getDefault().getProject().getFdModel().getStructureFactory();
			newElement = factory.createFolderPage(Messages.StructureCreateCommand_2);
		}
		
		
		
		((IStructureElement)parent).addToContent(newElement);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	@Override
	public void redo() {
		if (parent instanceof IStructureElement){
			((IStructureElement)parent).addToContent(newElement);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	@Override
	public void undo() {
		if (parent instanceof IStructureElement){
			((IStructureElement)parent).removeFromContent(newElement);
		}
		
	}
	
	
	
}
