package bpm.es.sessionmanager.gef;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bpm.es.sessionmanager.gef.editparts.FieldEditPart;
import bpm.es.sessionmanager.gef.editparts.MapEditPart;
import bpm.es.sessionmanager.gef.editparts.ServerEditPart;
import bpm.es.sessionmanager.gef.model.FieldModel;
import bpm.es.sessionmanager.gef.model.Link;
import bpm.es.sessionmanager.gef.model.MapModel;
import bpm.es.sessionmanager.gef.model.ServerModel;
import bpm.es.sessionmanager.gef.model.UserModel;

public class MapEditPartFactory implements EditPartFactory{

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if (model instanceof MapModel) {
			part = new MapEditPart();
		}
		else if (model instanceof ServerModel){
			part = new ServerEditPart();
		}	
		else if (model instanceof FieldModel){
			part = new FieldEditPart(0);
		}
		else {
			System.err.println("warning: unable to find edit part for model " + 
					model.getClass().getName() + ", ignoring.");
		}
		
//		if (model instanceof MappingModel){
//			part =  new MappingEditPart();
//		}
//		else if (model instanceof StreamModel){
//			part = new StreamEditPart();
//		}
//		else if (model instanceof FieldModel){
//			MappingModel m = ((FieldModel)model).getParent().getParent();
//			if (m.getChildren().get(0) == ((FieldModel)model).getParent()){
//				part = new FieldEditPart(PositionConstants.RIGHT);
//			}
//			else{
//				part = new FieldEditPart(PositionConstants.LEFT);
//			}
//			
//			
//		}
//		
//		else if (model instanceof Link){
//			part = new LinkEditPart();
//		}
		
		
		if (part != null) {
			part.setModel(model);
		}
		
		return part;
	}

}
