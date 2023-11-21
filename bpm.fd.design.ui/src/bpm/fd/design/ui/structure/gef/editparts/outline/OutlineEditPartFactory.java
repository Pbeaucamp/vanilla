package bpm.fd.design.ui.structure.gef.editparts.outline;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.structure.gef.editparts.palette.ComponentTreeEditPart;

public class OutlineEditPartFactory implements EditPartFactory{
	
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;

        if (model instanceof IStructureElement){
        	part = new TreeStructurePart();
        }
        else if (model instanceof IComponentDefinition){
        	part = new ComponentTreeEditPart();
        }
        if (part != null){
        	part.setModel(model);
        }
          

        return part;
     }
}
