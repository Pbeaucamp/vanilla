package bpm.database.ui.viewer.relations.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import bpm.database.ui.viewer.relations.gef.figures.TableFigure;
import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Node;

public class ConnectionPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	public void activate() {
		if (!isActive()) {
			super.activate();
//			((JoinConnection) getModel()).addPropertyChangeListener(this);
		}
	}

	@Override
	protected ConnectionAnchor getSourceConnectionAnchor() {

		if (getSource() instanceof ColumnPart
				&& getTarget() instanceof ColumnPart) {
			ColumnPart source = (ColumnPart) getSource();
			ColumnPart target = (ColumnPart) getTarget();
			TableFigure tf = (TableFigure) ((TablePart) (target.getParent()))
					.getFigure();
			return source.getConnectionAnchor(tf.getBounds().x );
		}
		return super.getSourceConnectionAnchor();
	}

	@Override
	protected ConnectionAnchor getTargetConnectionAnchor() {
		if (getSource() instanceof ColumnPart
				&& getTarget() instanceof ColumnPart) {
			ColumnPart source = (ColumnPart) getSource();
			ColumnPart target = (ColumnPart) getTarget();
			TableFigure tf = (TableFigure) ((TablePart) (source.getParent())).getFigure();
			return target.getConnectionAnchor(tf.getBounds().x);
		}
		return super.getTargetConnectionAnchor();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		
	}

	protected IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super
				.createFigure();
		connection.setTargetDecoration(new PolygonDecoration());
		connection.setLineStyle(Graphics.LINE_SOLID);
		return connection;
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
//			((Node) getModel()).removePropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		

	}

	

	
}
