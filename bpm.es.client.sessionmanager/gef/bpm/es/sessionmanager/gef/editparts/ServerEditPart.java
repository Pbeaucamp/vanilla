package bpm.es.sessionmanager.gef.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import bpm.es.sessionmanager.gef.figures.ServerFigure;
import bpm.es.sessionmanager.gef.figures.StreamFigure;
import bpm.es.sessionmanager.gef.model.ServerModel;
import bpm.es.sessionmanager.gef.model.UserModel;

public class ServerEditPart extends AbstractGraphicalEditPart{
	
//	public ServerEditPart() {
//		super();
//	}
	
	protected IFigure createFigure() {
//		Panel rect = new Panel();
//		
//		rect.setLocation(new Point(((StreamModel)getModel()).getX(), 50));
//		rect.setBackgroundColor(new Color(Display.getDefault(), 200, 0, 0));
//	
//		ToolbarLayout layout = new ToolbarLayout();
//		rect.setLayoutManager(layout);
		
		
		ServerFigure fig = new ServerFigure(((ServerModel)getModel()));
		//fig.set
		
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(fig));


		
		
		return fig;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		
	}
	


	protected List getModelChildren(){
		 return new ArrayList<Object>();
	}

	



	
}
