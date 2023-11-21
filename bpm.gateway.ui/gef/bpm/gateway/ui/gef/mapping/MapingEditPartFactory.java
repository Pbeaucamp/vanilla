package bpm.gateway.ui.gef.mapping;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.ui.gef.mapping.editparts.FieldEditPart;
import bpm.gateway.ui.gef.mapping.editparts.MappingEditPart;
import bpm.gateway.ui.gef.mapping.editparts.RelationEditPart;
import bpm.gateway.ui.gef.mapping.editparts.StreamEditPart;
import bpm.gateway.ui.gef.mapping.model.FieldModel;
import bpm.gateway.ui.gef.mapping.model.MappingModel;
import bpm.gateway.ui.gef.mapping.model.Relation;
import bpm.gateway.ui.gef.mapping.model.StreamModel;

public class MapingEditPartFactory implements EditPartFactory{

	private Composite parent ;
	public MapingEditPartFactory(Composite parent) {
		this.parent = parent;
	}

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if (model instanceof MappingModel){
			part =  new MappingEditPart();
		}
		else if (model instanceof StreamModel){
			part = new StreamEditPart();
		}
		else if (model instanceof FieldModel){
			MappingModel m = ((FieldModel)model).getParent().getParent();
			if (m.getChildren().get(0) == ((FieldModel)model).getParent()){
				part = new FieldEditPart(PositionConstants.RIGHT, this.parent);
			}
			else{
				part = new FieldEditPart(PositionConstants.LEFT, this.parent);
			}
			
			
		}
		else if (model instanceof Relation){
			part = new RelationEditPart();
		}
		
		if (part != null){
			part.setModel(model);
		}
		return part;
	}

}
