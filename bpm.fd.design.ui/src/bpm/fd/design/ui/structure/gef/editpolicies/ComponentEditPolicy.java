package bpm.fd.design.ui.structure.gef.editpolicies;


import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.structure.gef.editparts.AbstractStructureElementEditPart;

public class ComponentEditPolicy extends StructureEditPolicy{

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
	 * @see bpm.fd.design.ui.structure.gef.editpolicies.StructureEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	@Override
	public Command getCommand(Request request) {
		
		return super.getCommand(request);
	}

	



	
	
}
