package bpm.fd.design.ui.structure.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.gef.figures.HoverableFigure;
import bpm.fd.design.ui.structure.gef.commands.DeleteStructureElementCommand;
import bpm.fd.design.ui.structure.gef.editpolicies.StructureEditPolicy;

public abstract class AbstractStructureElementEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IFdObjectProvider{

	@Override
	abstract protected IFigure createFigure();

	@Override
	protected void createEditPolicies() {
		
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new StructureEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){

			/* (non-Javadoc)
			 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
			 */
			@Override
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				DeleteStructureElementCommand command = new DeleteStructureElementCommand();
				command.setNewElement((IStructureElement)getHost().getModel());
				command.setParent(getHost().getParent().getModel());
				
				
				return command;
			}
			
		});
//		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerEditPolicy(){
//
//			@Override
//			protected Command getCreateCommand(CreateRequest request) {
//				
//				return null;
//			}
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getAddCommand(org.eclipse.gef.requests.GroupRequest)
//			 */
//			@Override
//			protected Command getAddCommand(GroupRequest request) {
//				AddComponentCommand cmd = new AddComponentCommand();
//				
//				for(EditPart ep : (List<EditPart>)request.getEditParts()){
//					if (ep instanceof ComponentPart){
//						cmd.addComponents((IComponentDefinition)ep.getModel());
//					}
//					else{
//						return null;
//					}
//				}
//				cmd.setTarget((Cell)getHost().getModel());
//				return cmd;
//			}
//
//			/* (non-Javadoc)
//			 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getOrphanChildrenCommand(org.eclipse.gef.requests.GroupRequest)
//			 */
//			@Override
//			protected Command getOrphanChildrenCommand(GroupRequest request) {
//				RemoveComponentCommand cmd = new RemoveComponentCommand();
//				
//				for(EditPart ep : (List<EditPart>)request.getEditParts()){
//					if (ep instanceof ComponentPart){
//						cmd.addComponents((IComponentDefinition)ep.getModel());
//					}
//					else{
//						return null;
//					}
//				}
//				cmd.setTarget((Cell)getHost().getModel());
//				return cmd;
//			}
//			
//			
//			
//		});
	}

	
	
	public void propertyChange(PropertyChangeEvent evt){

		if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_ADDED)){
			refreshChildren();
			
		}
        if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_REMOVED)) {
        	refreshChildren();  
        }
        if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_HOVER)){
        	((HoverableFigure)getFigure()).highlightBorder();
        	refreshVisuals();
        	getFigure().repaint();
        }
        if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_HOVER_RELEASE)){
        	((HoverableFigure)getFigure()).unhighlightBorder();
        	refreshVisuals();
        	getFigure().repaint();
        }

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		((IStructureElement)getModel()).addPropertyChangeListener(this);
		super.activate();
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		((IStructureElement)getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	protected void refreshVisuals() {
		if (getParent() instanceof AbstractStructureElementEditPart){
			((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), new GridData(GridData.FILL, GridData.FILL, true, true));
		}
//		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshChildren()
	 */
	@Override
	protected void refreshChildren() {
		
		super.refreshChildren();
	}
	public IBaseElement getFdObject(){
		return (IBaseElement)getModel();
	}
	
}
