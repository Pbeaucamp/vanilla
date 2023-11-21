package bpm.sqldesigner.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.ui.figure.TableFigure;

public class ConnectionPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			((LinkForeignKey) getModel()).addPropertyChangeListener(this);
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
		// installEditPolicy(EditPolicy.CONNECTION_ROLE, new AppDeletePolicy());
	}

	@Override
	protected IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super
				.createFigure();
		connection.setSourceDecoration(new PolygonDecoration());
		connection.setLineStyle(Graphics.LINE_SOLID);
		return connection;
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((LinkForeignKey) getModel()).removePropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(LinkForeignKey.CONNECTIONS_PROP)) {
			refreshSourceAnchor();
			refreshTargetAnchor();
		}

	}
}
