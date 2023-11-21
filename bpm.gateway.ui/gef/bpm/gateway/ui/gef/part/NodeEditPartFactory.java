package bpm.gateway.ui.gef.part;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.gateway.core.Comment;
import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Link;
import bpm.gateway.ui.gef.model.Node;

public class NodeEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		AbstractGraphicalEditPart part = null;  
		
		if (model instanceof ContainerPanelModel){
			part = new ContentPanelEditPart();
		}
		else if (model instanceof GIDModel){
			part = new GIDEditPart();
		}
		else if (model instanceof Node){
			part = new NodePart();
		}
		else if (model instanceof Link){
			part = new LinkEditPart();
		}
		else if (model instanceof Comment){
			part = new CommentPart();
		}
		
		part.setModel(model);
		
		return part;
	}

}
