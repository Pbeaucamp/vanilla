package bpm.fd.design.ui.structure.gef.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.Row;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.api.core.model.structure.Table;

public class StructureEditPartFactory implements EditPartFactory{
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;  
		
		if (model instanceof FdModel){
			part = new StructureEditPart();
		}
		else if (model instanceof DrillDrivenStackableCell){
			part = new CellPart();
		}
		else if (model instanceof Cell){
			part = new CellPart();
		}
		else if (model instanceof Table){
			part = new TablePart();
		}
		else if (model instanceof Folder){
			part = new FolderPart();
		}
		else if(model instanceof StackableCell) {
			part = new StackableCellPart();
		}
		
		else if (model instanceof Row){
			part = new RowPart();
		}
		else if (model instanceof IComponentDefinition){
			part = new ComponentPart();
		}
		
		
		part.setModel(model);
		
		return part;
	}
}
