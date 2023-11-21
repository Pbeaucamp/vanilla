package bpm.workflow.ui.gef.part;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import bpm.workflow.ui.gef.commands.LinkDeleteCommand;
import bpm.workflow.ui.gef.model.Link;

public class LinkEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

	public final static Color BLUE = new Color(Display.getCurrent(), 0, 0, 255);
	public final static Color GREEN = new Color(Display.getCurrent(), 0, 221, 0);
	private String color;

	public void activate() {
		if(!isActive()) {
			super.activate();
		}
	}

	public LinkEditPart(String color) {
		this.color = color;
	}

	public LinkEditPart() {

	}

	@Override
	protected void createEditPolicies() {

		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new ConnectionEditPolicy() {
			protected Command getDeleteCommand(GroupRequest request) {
				return new LinkDeleteCommand((Link) getModel());
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

		if(this.color != null) {
			if(this.color.equalsIgnoreCase("blue")) { //$NON-NLS-1$

				connection.setForegroundColor(BLUE);

			}
			else if(this.color.equalsIgnoreCase("green")) { //$NON-NLS-1$
				connection.setForegroundColor(GREEN);
			}
		}
		connection.setLineStyle(Graphics.LINE_SOLID); // line drawing style

		return connection;
	}

	public void deactivate() {
		if(isActive()) {
			super.deactivate();

		}
	}

	public void propertyChange(PropertyChangeEvent event) {

	}

	@Override
	public void refreshVisuals() {

		if(getSource() != null && getTarget() != null) {

			if(color != null) {
				if(color.equalsIgnoreCase("blue")) { //$NON-NLS-1$
					getFigure().setForegroundColor(BLUE);
				}
				else if(color.equalsIgnoreCase("green")) { //$NON-NLS-1$
					getFigure().setForegroundColor(GREEN);
				}
			}
			else {
				getFigure().setForegroundColor(null);
			}
		}

		super.refreshVisuals();
	}

}
