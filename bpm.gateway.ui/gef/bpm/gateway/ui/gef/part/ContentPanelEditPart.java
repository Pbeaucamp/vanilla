package bpm.gateway.ui.gef.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import bpm.gateway.ui.gef.editpolicies.NodeEditPolicy;
import bpm.gateway.ui.gef.model.ContainerPanelModel;

public class ContentPanelEditPart extends AbstractGraphicalEditPart implements
	PropertyChangeListener{
	
	@Override
	public void setModel(Object model) {
		
		super.setModel(model);
	}

	@Override
	protected IFigure createFigure() {
		
		IFigure figure = new FreeformLayer();
		figure.setBorder(new MarginBorder(3));
		figure.setLayoutManager(new FreeformLayout());
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		//connLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));
		connLayer.setConnectionRouter(new BendpointConnectionRouter());
		return figure;
	}
	
	@Override
	protected void createEditPolicies() {
		
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,new NodeEditPolicy());

	}

	

	@Override
	public IFigure getFigure() {
		
		return super.getFigure();
	}

	public void activate(){
		super.activate();
		 if (getModel() instanceof ContainerPanelModel){
			((ContainerPanelModel)getModel()).addPropertyChangeListener(this);
		 }
	}
	
	public void deactivate(){
		super.deactivate();
		if (getModel() instanceof ContainerPanelModel){
			((ContainerPanelModel)getModel()).removePropertyChangeListener(this);
		}
	}
	

	public void propertyChange(PropertyChangeEvent evt){

		if (evt.getPropertyName().equals(ContainerPanelModel.PROPERTY_ADD_CHILD)){
			refreshChildren();
		}
        if (evt.getPropertyName().equals(ContainerPanelModel.PROPERTY_REMOVE_CHILD)) {
        	refreshChildren();  
        }

	}

	@Override
	protected List getModelChildren() {
		return((ContainerPanelModel)getModel()).getChildren();
	}
}
