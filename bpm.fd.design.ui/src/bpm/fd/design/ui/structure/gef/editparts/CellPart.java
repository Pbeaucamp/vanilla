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
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.gef.figures.CellFigure;
import bpm.fd.design.ui.gef.figures.DrillDrivenCellFigure;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.structure.gef.commands.AddComponentCommand;
import bpm.fd.design.ui.structure.gef.commands.RemoveComponentCommand;

public class CellPart extends AbstractStructureElementEditPart{

	@Override
	protected IFigure createFigure() {
		Cell cell = (Cell)getModel();
		
		CellFigure fig = null;
		
		if (cell instanceof DrillDrivenStackableCell){
			fig = new DrillDrivenCellFigure(cell.getContent().isEmpty());
		}
		else{
			fig = new CellFigure(cell.getContent().isEmpty());
		}
		return fig;
	}
	
	@Override
	protected void refreshVisuals() {
		Cell model = (Cell)getModel();
		((CellFigure)getFigure()).hasEvents(((IBaseElement)getModel()).hasEvents());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), new GridData(GridData.FILL, GridData.FILL, false, true, model.getColSpan(), model.getRowSpan()));
	}
	
	@Override
	protected List getModelChildren() {
		return((Cell)getModel()).getContent();
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
						
//						if (request.getNewObject() instanceof IStructureElement){
//							StructureCreateCommand cmd = new StructureCreateCommand(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
//							cmd.setNewElement((IStructureElement)request.getNewObject());
//							cmd.setParent(getModel());
//							return cmd;
//						}
//						else{
//							AddComponentCommand cmd = new AddComponentCommand(CellPart.this.getRoot().getViewer().getEditDomain().getCommandStack());
//							cmd.setTarget((Cell)getModel());
//							if (request.getNewObject() instanceof IComponentDefinition){
//								cmd.addComponents((IComponentDefinition)request.getNewObject());
//							}
//							else if (request.getNewObject() instanceof DrillDrivenStackableCell){
//								cmd.setDrillCell((DrillDrivenStackableCell)request.getNewObject());
//							}
//							
//							return cmd;
//						}
						return null;
						
						
					}
		
					/* (non-Javadoc)
					 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getAddCommand(org.eclipse.gef.requests.GroupRequest)
					 */
					@Override
					protected Command getAddCommand(GroupRequest request) {
						AddComponentCommand cmd = new AddComponentCommand(CellPart.this.getRoot().getViewer().getEditDomain().getCommandStack());
						cmd.setTarget((Cell)getModel());
						for(EditPart ep : (List<EditPart>)request.getEditParts()){
							if (ep instanceof ComponentPart){
								cmd.addComponents((IComponentDefinition)ep.getModel());
							}
							else if (ep instanceof CellPart && ep.getModel() instanceof DrillDrivenStackableCell){
								cmd.setDrillCell((DrillDrivenStackableCell)ep.getModel());
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
						RemoveComponentCommand cmd = new RemoveComponentCommand(CellPart.this.getRoot().getViewer().getEditDomain().getCommandStack());
						cmd.setTarget((Cell)getModel());
						for(EditPart ep : (List<EditPart>)request.getEditParts()){
							if (ep instanceof ComponentPart){
								cmd.addComponents((IComponentDefinition)ep.getModel());
							}
							else if (ep instanceof CellPart && ep.getModel() instanceof DrillDrivenStackableCell){
								cmd.setDrillDrivenStackableCell((DrillDrivenStackableCell)ep.getModel());
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
			Cell def = (Cell)getModel();
			if (def.getContent().isEmpty()){
				((CellFigure)getFigure()).setPicture(Activator.getDefault().getImageRegistry().get(Icons.empty_cell));
//				refreshVisuals();
				getRoot().refresh();
			}
			else{
				((CellFigure)getFigure()).setPicture(null);
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
