package bpm.gateway.ui.views.property;

import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;

import bpm.gateway.core.Comment;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.CommentPart;
import bpm.gateway.ui.gef.part.GIDEditPart;
import bpm.gateway.ui.gef.part.NodePart;

public class NodeTypeMapper extends AbstractTypeMapper {

	public Class mapType(Object object) {
		if (object instanceof GIDModel){
			return ((GIDModel)((GIDEditPart) object).getModel()).getClass();
		}
		
		if (object instanceof NodePart) {
//			System.out.println(((Node)((NodePart) object).getModel()).getGatewayModel().getClass().getName());
            return ((Node)((NodePart) object).getModel()).getGatewayModel().getClass();
        }
		
		if (object instanceof CommentPart){
			return Comment.class;
		}
		
		
        return super.mapType(object);
	}

}
