package bpm.sqldesigner.query.part;

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

import bpm.sqldesigner.query.editpolicies.AppDeletePolicy;
import bpm.sqldesigner.query.figure.TableFigure;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.connection.JoinConnection;

public class ConnectionPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	public void activate() {
		if (!isActive()) {
			super.activate();
			((Node) getModel()).addPropertyChangeListener(this);
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
			return source.getConnectionAnchor(tf.getBounds().x);
		}
		return super.getSourceConnectionAnchor();
	}

	@Override
	protected ConnectionAnchor getTargetConnectionAnchor() {
		if (getSource() instanceof ColumnPart
				&& getTarget() instanceof ColumnPart) {
			ColumnPart source = (ColumnPart) getSource();
			ColumnPart target = (ColumnPart) getTarget();
			TableFigure tf = (TableFigure) ((TablePart) (source.getParent()))
					.getFigure();
			return target.getConnectionAnchor(tf.getBounds().x);
		}
		return super.getTargetConnectionAnchor();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new AppDeletePolicy());
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
			((Node) getModel()).removePropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.CONNECTIONS_PROP)) {
			refreshSourceAnchor();
			refreshTargetAnchor();
		}

	}

	public void invert() {
		JoinConnection link = (JoinConnection) getModel();
		link.invert();
	}
}
