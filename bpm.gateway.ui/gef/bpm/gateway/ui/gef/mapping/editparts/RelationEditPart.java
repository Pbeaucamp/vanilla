package bpm.gateway.ui.gef.mapping.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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



public class RelationEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	public void activate() {
		if (!isActive()) {
			super.activate();
//			((Workflow) getModel()).addPropertyChangeListener(this);
		}
	}
	
	@Override
	protected void createEditPolicies() {
		
		
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			protected Command getDeleteCommand(GroupRequest request) {
				return null;
			}

			@Override
			public Command getCommand(Request request) {
				
				return super.getCommand(request);
			}
			
			
		});

	}
	
	protected IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super.createFigure();
		connection.setTargetDecoration(new PolygonDecoration()); // arrow at target endpoint
		connection.setLineStyle(Graphics.LINE_SOLID);  // line drawing style
		return connection;
	}
	
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			
		}
	}

	

	public void propertyChange(PropertyChangeEvent event) {
		


	}

	
	

}
