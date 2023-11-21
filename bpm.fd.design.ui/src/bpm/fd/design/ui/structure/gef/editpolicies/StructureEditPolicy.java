package bpm.fd.design.ui.structure.gef.editpolicies;


import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.structure.gef.commands.BuildComponentCommand;
import bpm.fd.design.ui.structure.gef.commands.BuildComponentInStackableCellCommand;
import bpm.fd.design.ui.structure.gef.commands.ComponentCreateCommand;
import bpm.fd.design.ui.structure.gef.commands.ComponentCreateInStackableCellCommand;
import bpm.fd.design.ui.structure.gef.commands.StructureCreateCommand;
import bpm.fd.design.ui.structure.gef.editparts.AbstractStructureElementEditPart;

public class StructureEditPolicy extends XYLayoutEditPolicy{

	
	private Command createCreateCommand(CreateRequest request){
		Shell shell = getHost().getRoot().getViewer().getControl().getShell();
		
		
		if (request.getNewObject() instanceof IComponentDefinition && getHost().getModel() instanceof Cell){
			ComponentCreateCommand command = new ComponentCreateCommand();
			command.setNewElement((IComponentDefinition)request.getNewObject());
			command.setParent((Cell)getHost().getModel());
			return command;
		}
		
		else if(request.getNewObject() instanceof IComponentDefinition && getHost().getModel() instanceof StackableCell){
			ComponentCreateInStackableCellCommand command = new ComponentCreateInStackableCellCommand();
			command.setNewElement((IComponentDefinition)request.getNewObject());
			command.setParent((StackableCell)getHost().getModel());
			return command;
		}
		
		else if (request.getNewObject() instanceof Class && IComponentDefinition.class.isAssignableFrom((Class)request.getNewObject()) && getHost().getModel() instanceof Cell){
			BuildComponentCommand command = new BuildComponentCommand(shell);
			command.setNewElement((Class)request.getNewObject());
			command.setParent(getHost().getModel());
			return command;
		}
		
		else if (request.getNewObject() instanceof Class && IComponentDefinition.class.isAssignableFrom((Class)request.getNewObject()) && getHost().getModel() instanceof StackableCell){
			BuildComponentInStackableCellCommand command = new BuildComponentInStackableCellCommand(shell);
			command.setNewElement((Class)request.getNewObject());
			command.setParent(getHost().getModel());
			return command;
		}
		
		
		
		
		else if (request.getNewObject() instanceof IStructureElement &&  getHost().getModel() instanceof IStructureElement){
			StructureCreateCommand command = new StructureCreateCommand(shell);
			command.setNewElement((IStructureElement)request.getNewObject());
			command.setParent(getHost().getModel());
			
			
			
			return command;
		}
		
		
		return null;

	}

	
	
	

	





	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getType() == this.REQ_CREATE){
			return createCreateCommand(request);
		}
		
		
		return null;
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
	 */
	@Override
	public void eraseTargetFeedback(Request request) {
		super.eraseTargetFeedback(request);
		if (getHost() instanceof AbstractStructureElementEditPart){
			AbstractStructureElementEditPart editPart = (AbstractStructureElementEditPart)getHost();
			IStructureElement model = (IStructureElement)editPart.getModel();
			
			model.firePropertyChange(IStructureElement.P_CONTENT_HOVER_RELEASE, null, new Object());
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#showTargetFeedback(org.eclipse.gef.Request)
	 */
	@Override
	public void showTargetFeedback(Request request) {
		super.showTargetFeedback(request);
		if (getHost() instanceof AbstractStructureElementEditPart){
			AbstractStructureElementEditPart editPart = (AbstractStructureElementEditPart)getHost();
			IStructureElement model = (IStructureElement)editPart.getModel();
			
			model.firePropertyChange(IStructureElement.P_CONTENT_HOVER, null, new Object());

		}
		
	}



	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		
		return null;
	}











	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	@Override
	public Command getCommand(Request request) {
		if (request.getType() == REQ_MOVE || request.getType() == REQ_RESIZE
			|| request.getType() == REQ_MOVE_CHILDREN || request.getType() == REQ_ADD ){
			return null;
		}
		return super.getCommand(request);
	}

	



	
	
}
