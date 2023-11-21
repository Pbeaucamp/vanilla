package bpm.gateway.ui.gef.mapping.editparts;

import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import bpm.gateway.ui.gef.mapping.model.MappingModel;
import bpm.gateway.ui.gef.mapping.model.StreamModel;

public class MappingEditPart extends AbstractGraphicalEditPart {

	@Override
	protected IFigure createFigure() {
		IFigure figure = new FreeformLayer();
		figure.setBorder(new MarginBorder(3));

		FlowLayout layout = new FlowLayout();
		layout.setMinorSpacing(200);
		
		figure.setLayoutManager(layout);
		
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));

		return figure;
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());

	}
	
	@Override
	protected List<StreamModel> getModelChildren() {
		return((MappingModel)getModel()).getChildren();
	}
}
