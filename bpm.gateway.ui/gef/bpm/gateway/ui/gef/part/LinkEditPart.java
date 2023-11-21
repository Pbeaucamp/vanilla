package bpm.gateway.ui.gef.part;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.gef.commands.LinkDeleteCommand;
import bpm.gateway.ui.gef.editpolicies.LinkBendPointEditPolicy;
import bpm.gateway.ui.gef.model.Link;
import bpm.gateway.ui.gef.model.Node;



public class LinkEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	public void activate() {
		if (!isActive()) {
			super.activate();
			((Link)getModel()).addPropertyListener(this);
//			((Workflow) getModel()).addPropertyChangeListener(this);
		}
	}
	
	@Override
	protected void createEditPolicies() {
		
		
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			protected Command getDeleteCommand(GroupRequest request) {
				return new LinkDeleteCommand((Link)getModel());
			}

			@Override
			public Command getCommand(Request request) {
				
				return super.getCommand(request);
			}
			
			
		});
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new LinkBendPointEditPolicy());
	}
	
	protected IFigure createFigure() {
//		PolylineConnectionEx connection = new PolylineConnectionEx();//(PolylineConnection) super.createFigure();
//		connection.setSmoothness(PolylineConnectionEx.SMOOTH_NORMAL);

//		PolylineConnection connection = (PolylineConnection)super.createFigure();
		PolylineConnection connection = new PolylineConnection();
		
//		Bez
//		PipeConnection connection = new PipeConnection();
		
		connection.setTargetDecoration(new PolygonDecoration()); // arrow at target endpoint
		connection.setLineStyle(Graphics.LINE_SOLID);  // line drawing style
		connection.setAntialias(64);

		
//		Transformation source = ((Node)getSource().getModel()).getGatewayModel();
//		Transformation target = ((Node)getTarget().getModel()).getGatewayModel();
//		
//		if (source instanceof Trashable && ((Trashable)source).getTrashTransformation() == target){
//			ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
//			
//			connection.setForegroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.TRASH_LINK_KEY));
//		}
		
		return connection;
	}
	
	public void deactivate() {
		if (isActive()) {
			((Link)getModel()).removePropertyListener(this);
			super.deactivate();
			
		}
	}

	

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(Link.BEND_POINT_CHANGED)){
			refreshVisuals();
		}
	}

	@Override
	protected void refreshVisuals() {
		Connection connection = getConnectionFigure();
	    List<Point> modelConstraint = ((Link)getModel()).getBendPoints();
	    List<AbsoluteBendpoint> figureConstraint = new ArrayList<AbsoluteBendpoint>();
	    for (Point p : modelConstraint) {
	      figureConstraint.add(new AbsoluteBendpoint(new org.eclipse.draw2d.geometry.Point(p.x, p.y)));
	    }
	    connection.setRoutingConstraint(figureConstraint);
	    
		if (getSource() != null && getTarget() != null){
			Transformation source = ((Node)getSource().getModel()).getGatewayModel();
			Transformation target = ((Node)getTarget().getModel()).getGatewayModel();
			
			if (source instanceof Trashable && ((Trashable)source).getTrashTransformation() == target){
				ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
				
				getFigure().setForegroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.TRASH_LINK_KEY));
			}
			else{
				getFigure().setForegroundColor(null);
			}
			source.setBendPoints(target, modelConstraint);
		}
		
		
		
		super.refreshVisuals();
	}
	

}
