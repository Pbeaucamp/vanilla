package bpm.gateway.ui.gef.editpolicies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import bpm.gateway.core.Comment;
import bpm.gateway.core.transformations.gid.GidNode;
import bpm.gateway.ui.gef.commands.ChangeLayoutCommand;
import bpm.gateway.ui.gef.commands.CommentCreationCommand;
import bpm.gateway.ui.gef.commands.GIDCreationCommand;
import bpm.gateway.ui.gef.commands.NodeCreationCommand;
import bpm.gateway.ui.gef.model.ContainerPanelModel;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.CommentPart;
import bpm.gateway.ui.gef.part.ContentPanelEditPart;
import bpm.gateway.ui.gef.part.GIDEditPart;
import bpm.gateway.ui.gef.part.NodePart;

public class NodeEditPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		
		
		ChangeLayoutCommand command = new ChangeLayoutCommand();
		command.setModel(child.getModel());
		command.setLayout((Rectangle)constraint);
		
		
		return command;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
	    if (request.getType() == REQ_CREATE && getHost() instanceof ContentPanelEditPart) {
	    	if (request.getNewObject() instanceof GIDModel){
	    		GIDCreationCommand cmd = new GIDCreationCommand();
	            
	            cmd.setParent((ContainerPanelModel)getHost().getModel());
	            cmd.setNewObject((GIDModel)request.getNewObject());
	           
	            Rectangle constraint = (Rectangle)getConstraintFor(request);
	            constraint.x = (constraint.x < 0) ? 0 : constraint.x;
	            constraint.y = (constraint.y < 0) ? 0 : constraint.y;
	            constraint.width = 200;
	            constraint.height = 200;
	            cmd.setLayout(constraint);
	            return cmd;
	    	}
	    	else if (request.getNewObject() instanceof Node){
	    		NodeCreationCommand cmd = new NodeCreationCommand();
	            
	            cmd.setParent((ContainerPanelModel)getHost().getModel());
	            cmd.setNewObject((Node)request.getNewObject());
	           
	            Rectangle constraint = (Rectangle)getConstraintFor(request);
	            constraint.x = (constraint.x < 0) ? 0 : constraint.x;
	            constraint.y = (constraint.y < 0) ? 0 : constraint.y;
	            constraint.width = 50;
	            constraint.height = 50;
	            cmd.setLayout(constraint);
	            return cmd;
	    	}
	    	else if (request.getNewObject() instanceof Comment){
	    		CommentCreationCommand cmd = new CommentCreationCommand();
	            
	            cmd.setParent((ContainerPanelModel)getHost().getModel());
	            cmd.setNewObject((Comment)request.getNewObject());
	           
	            Rectangle constraint = (Rectangle)getConstraintFor(request);
	            constraint.x = (constraint.x < 0) ? 0 : constraint.x;
	            constraint.y = (constraint.y < 0) ? 0 : constraint.y;
	            constraint.width = 50;
	            constraint.height = 50;
	            cmd.setLayout(constraint);
	            return cmd;
	    	}
	    	
	    	
	    	
    }
    return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.XYLayoutEditPolicy#getConstraintFor(org.eclipse.gef.requests.ChangeBoundsRequest, org.eclipse.gef.GraphicalEditPart)
	 */
	@Override
	protected Object getConstraintFor(ChangeBoundsRequest request,
			GraphicalEditPart child) {
		if (child instanceof NodePart){
			
			if (request.getType() == REQ_RESIZE || request.getType() == REQ_RESIZE_CHILDREN){
				if (child instanceof GIDEditPart){
					return super.getConstraintFor(request, child);
				}
				else{
					return null;
				}
			}
		}
		else if (child instanceof CommentPart){
		}
		
		return super.getConstraintFor(request, child);
	}
	
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof  NodePart){
			if (child instanceof GIDEditPart){
				return super.createChildEditPolicy(child);
			}
			else{
				return new NonResizableEditPolicy();
			}
			
		}
		else{
			return super.createChildEditPolicy(child);
		}
	  
	}

}
