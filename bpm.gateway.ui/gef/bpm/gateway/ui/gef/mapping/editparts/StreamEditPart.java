package bpm.gateway.ui.gef.mapping.editparts;

import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import bpm.gateway.ui.gef.mapping.figures.StreamFigure;
import bpm.gateway.ui.gef.mapping.model.StreamModel;

public class StreamEditPart extends AbstractGraphicalEditPart{

	
	
	
	@Override
	protected IFigure createFigure() {
//		Panel rect = new Panel();
//		
//		rect.setLocation(new Point(((StreamModel)getModel()).getX(), 50));
//		rect.setBackgroundColor(new Color(Display.getDefault(), 200, 0, 0));
//	
//		ToolbarLayout layout = new ToolbarLayout();
//		rect.setLayoutManager(layout);
		
		
		StreamFigure rect = new StreamFigure(((StreamModel)getModel()).getName());
		
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(rect));


		
		
		return rect;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		
	}
	


	protected List getModelChildren(){
		 return ((StreamModel)getModel()).getFields();
	}

	



	
}
