package bpm.es.sessionmanager.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class LinkEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	public void activate() {
		if (!isActive()) {
			super.activate();
		}
	}
	
	@Override
	protected void createEditPolicies() {
		
		
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
//		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
//			protected Command getDeleteCommand(GroupRequest request) {
//				return new LinkDeleteCommand((Link)getModel());
//			}
//
//			@Override
//			public Command getCommand(Request request) {
//				return super.getCommand(request);
//			}
//		});
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

	@Override
	protected void refreshVisuals() {
		
//		if (getSource() != null && getTarget() != null){
//			Transformation source = ((FieldModel)getSource().getModel()).getGatewayModel();
//			Transformation target = ((Node)getTarget().getModel()).getGatewayModel();
//			
//			if (source instanceof Trashable && ((Trashable)source).getTrashTransformation() == target){
//				ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
//				
//				getFigure().setForegroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.TRASH_LINK_KEY));
//			}
//			else{
//				getFigure().setForegroundColor(null);
//			}
//		}
		getFigure().setForegroundColor(new Color(Display.getDefault(), 100, 100, 100));
		
		
		super.refreshVisuals();
	}
	

}
