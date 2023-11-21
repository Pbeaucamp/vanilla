package bpm.fd.design.ui.editor.editparts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.StackableCell;

public class FdDesignEditPartFactory implements EditPartFactory{

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;  
		if (model instanceof FdModel){
			part = new ModelPart();
		}
		else if (model instanceof StackableCell){
			part = new StackableCellPart();
		}
		else if(model instanceof DivCell) {
			part = new DivCellPart();
		}
		else if (model instanceof IComponentDefinition || model instanceof Cell){
			FactoryStructure f = null;
			if (context != null){
				if (context.getRoot() != null && !context.getRoot().getChildren().isEmpty() &&
						((EditPart)context.getRoot().getChildren().get(0)).getModel() instanceof FdModel){
					f = ((FdModel)((EditPart)context.getRoot().getChildren().get(0)).getModel()).getStructureFactory();
				}
			}
			
			part = new ComponentPart(f);
		}
		else if (model instanceof Folder){
			part = new FolderPart();
		}
		if (part != null){
			part.setModel(model);
		}
		
		return part;
	}

}
