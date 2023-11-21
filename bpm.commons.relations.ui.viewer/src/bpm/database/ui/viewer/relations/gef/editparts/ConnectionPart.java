package bpm.database.ui.viewer.relations.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import bpm.database.ui.viewer.relations.gef.additional.CurveConnection;
import bpm.database.ui.viewer.relations.model.JoinConnection;

public class ConnectionPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	private JoinConnection model;
	private CurveConnection connection;
	
	public void activate() {
		if (!isActive()) {
			super.activate();
//			((JoinConnection) getModel()).addPropertyChangeListener(this);
		}
	}
	
	
	public ConnectionPart(JoinConnection model){
		super();
		this.model=model;
	}

//	@Override
//	protected ConnectionAnchor getSourceConnectionAnchor() {
//
//		if (getSource() instanceof ColumnPart) {
//			ColumnPart source = (ColumnPart) getSource();
//			TableFigure tf = (TableFigure) ((TablePart) (source.getParent()))
//					.getFigure();
//			return source.getConnectionAnchor(tf.getBounds().x );
//		}
//		return super.getSourceConnectionAnchor();
//	}
//
//	@Override
//	protected ConnectionAnchor getTargetConnectionAnchor() {
//		if (getSource() instanceof ColumnPart
//				&& getTarget() instanceof ColumnPart) {
//			ColumnPart source = (ColumnPart) getSource();
//			ColumnPart target = (ColumnPart) getTarget();
//			TableFigure tf = (TableFigure) ((TablePart) (source.getParent())).getFigure();
//			return target.getConnectionAnchor(tf.getBounds().x);
//		}
//		return super.getTargetConnectionAnchor();
//	}

	
	public ConnectionPart() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		
	}

	protected IFigure createFigure() {
	//	PolylineConnection connection = (PolylineConnection) super
	//			.createFigure();
		this.connection = new CurveConnection();
		CurveConnection connection = new CurveConnection(model);
		connection.setTargetDecoration(new PolygonDecoration());
		connection.setLineStyle(Graphics.LINE_SOLID);
		
		EditPart sourcePart = getSource();
		EditPart targetPart = getTarget();
		
		//connection.setSourceAnchor(anchor);(anchor);
		
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

	public void setSourceAnchor(ConnectionAnchor anchor){
		connection.setSourceAnchor(anchor);
	}
	public void setTargetAnchor(ConnectionAnchor anchor){
		connection.setTargetAnchor(anchor);
	}
	
}
