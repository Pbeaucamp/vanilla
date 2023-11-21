package bpm.fd.design.ui.structure.gef.editparts;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.structure.gef.editpolicies.StructureEditPolicy;


public class StructureEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IFdObjectProvider{
	
	
	public StructureEditPart() {
		super();
		
	}

	@Override
	protected IFigure createFigure() {
		IFigure figure = new FreeformLayer();
		LineBorder outerBorder = new LineBorder(3);
//		outerBorder.setColor(ColorManager.getColorRegistry().get(colorKey));
		
		MarginBorder innerBorder = new MarginBorder(3);
		
		figure.setBorder(new CompoundBorder(outerBorder, innerBorder));
//		((LineBorder)figure.getBorder()).setColor(new Color(Display.getDefault(), 0,0,0));
//		figure.setLayoutManager(new ToolbarLayout(false));
		figure.setLayoutManager(new GridLayout());
		
		return figure;

	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new StructureEditPolicy());
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
	protected List getModelChildren() {
		return((FdModel)getModel()).getContent();
	}
	
	public void propertyChange(PropertyChangeEvent evt){
		if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_ADDED)){
			refreshChildren();
		}
        if (evt.getPropertyName().equals(IStructureElement.P_CONTENT_REMOVED)) {
        	refreshChildren();  
        	
        }

	}

	@Override
	protected void refreshVisuals() {
		
		super.refreshVisuals();
	}
	
	@Override
	protected void refreshChildren() {
		
		super.refreshChildren();
	}

	@Override
	public IBaseElement getFdObject() {
		return (IBaseElement)getModel();
	}
	
}
