package bpm.fd.design.ui.editor.commands;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.editor.editparts.CellWrapper;
import bpm.fd.design.ui.editor.editparts.ComponentPart;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;

public class RemoveItemCommand extends Command{
	private EditPart host;
	private Object item;
	/**
	 * @param host the host to set
	 */
	public void setHost(EditPart host) {
		this.host = host;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(Object item) {
		this.item = item;
	}
	
	@Override
	public boolean canExecute() {
		return host != null && item != null;
	}
	
	@Override
	public void execute() {
		IStructureElement container = (IStructureElement)host.getModel();
		
		
		for(EditPart it : (List<EditPart>)item){
			
			if (it instanceof IFdObjectProvider){
				IBaseElement el = ((IFdObjectProvider)it).getFdObject();
				if (el instanceof IStructureElement){
					container.removeFromContent((IStructureElement)el);
				}
				else if (el instanceof IComponentDefinition){
					Cell cell = ((CellWrapper)((ComponentPart)it).getModel()).getCell();
					container.removeFromContent(cell);
					container.removeComponent((IComponentDefinition)el);
				}
			}
		}
		
		
	}
}
