package bpm.fd.design.ui.structure.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.gef.figures.StackableCellFigure;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.structure.gef.commands.AddComponentToStackableCellCommand;
import bpm.fd.design.ui.structure.gef.commands.RemoveComponentFromStackableCellCommand;

public class StackableCellPart extends AbstractStructureElementEditPart {

	@Override
	protected IFigure createFigure() {
		StackableCellFigure fig = new StackableCellFigure(((StackableCell)getModel()).getContent().isEmpty());
		return fig;
	}

	@Override
	protected void refreshVisuals() {
		StackableCell model = (StackableCell)getModel();
		((StackableCellFigure)getFigure()).hasEvents(((IBaseElement)getModel()).hasEvents());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), new GridData(GridData.FILL, GridData.FILL, false, true, model.getColSpan(), model.getRowSpan()));
	}
	
	@Override
	protected List getModelChildren() {
		return((StackableCell)getModel()).getContent();
	}

	/* (non-Javadoc)
	 * @see bpm.fd.design.ui.structure.gef.editparts.AbstractStructureElementEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
	
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerEditPolicy(){
		
					@Override
					protected Command getCreateCommand(CreateRequest request) {
						
						return null;
					}
		
					/* (non-Javadoc)
					 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getAddCommand(org.eclipse.gef.requests.GroupRequest)
					 */
					@Override
					protected Command getAddCommand(GroupRequest request) {
						AddComponentToStackableCellCommand cmd = new AddComponentToStackableCellCommand(StackableCellPart.this.getRoot().getViewer().getEditDomain().getCommandStack());
						cmd.setTarget((StackableCell)getModel());
						for(EditPart ep : (List<EditPart>)request.getEditParts()){
							if (ep instanceof ComponentPart){
								cmd.addComponents((IComponentDefinition)ep.getModel());
							}
						}
						DragTracker dt = getDragTracker(request);
						
						return cmd;
					}
		
					/* (non-Javadoc)
					 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getOrphanChildrenCommand(org.eclipse.gef.requests.GroupRequest)
					 */
					@Override
					protected Command getOrphanChildrenCommand(GroupRequest request) {
						RemoveComponentFromStackableCellCommand cmd = new RemoveComponentFromStackableCellCommand(StackableCellPart.this.getRoot().getViewer().getEditDomain().getCommandStack());
						cmd.setTarget((StackableCell)getModel());
						for(EditPart ep : (List<EditPart>)request.getEditParts()){
							if (ep instanceof ComponentPart){
								cmd.addComponents((IComponentDefinition)ep.getModel());
							}
							else{
								return null;
							}
						}
						return cmd;
					}
					
					
					
				});
	}

	/* (non-Javadoc)
	 * @see bpm.fd.design.ui.structure.gef.editparts.AbstractStructureElementEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_ADDED) || evt.getPropertyName().equals(IStructureElement.P_CONTENT_REMOVED)){
			StackableCell def = (StackableCell)getModel();
			if (def.getContent().isEmpty()){
				((StackableCellFigure)getFigure()).setPicture(Activator.getDefault().getImageRegistry().get(Icons.empty_cell));
//				refreshVisuals();
				getRoot().refresh();
			}
			else{
				((StackableCellFigure)getFigure()).setPicture(null);
//				refreshVisuals();
				getRoot().refresh();
			}
			fireFdModelEvent(evt);
		}
		if (evt.getPropertyName().equals(IBaseElement.EVENTS_CHANGED)){
			refreshVisuals();
		}
	
		
		super.propertyChange(evt);
	}

	private void fireFdModelEvent(PropertyChangeEvent evt){
		EditPart o = getParent();
		
		while(!(o.getModel() instanceof FdModel)){
			o = o.getParent();
		}
		
		if (o != null && o.getModel() instanceof FdModel){
			((FdModel)o.getModel()).firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	@Override
	public DragTracker getDragTracker(Request request) {
		
		DragTracker dt =  super.getDragTracker(request);
		
		return dt;
	}

	/* (non-Javadoc)
	 * @see bpm.fd.design.ui.structure.gef.editparts.AbstractStructureElementEditPart#activate()
	 */
	@Override
	public void activate() {
		
		super.activate();

		
	}

	

	
}
