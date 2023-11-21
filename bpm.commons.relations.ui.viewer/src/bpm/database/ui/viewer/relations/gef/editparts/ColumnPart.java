package bpm.database.ui.viewer.relations.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;

import bpm.database.ui.viewer.relations.gef.figures.ColumnFigure;
import bpm.database.ui.viewer.relations.gef.policies.FlowAppEditLayoutPolicy;
import bpm.database.ui.viewer.relations.model.Column;
import bpm.database.ui.viewer.relations.model.Node;

public class ColumnPart extends AppAbstractEditPart implements NodeEditPart{
	
	
	
	class MyAnchor extends AbstractConnectionAnchor{
		private boolean isLeft;
		public MyAnchor(IFigure owner, boolean isLeft) {
			super(owner);
			this.isLeft = isLeft;
		}
		@Override
		public Point getLocation(Point reference){
			Rectangle r = getOwner().getBounds().getCopy();
			getOwner().translateToAbsolute(r);
			Rectangle p = getOwner().getParent().getBounds().getCopy();
			getOwner().getParent().translateToAbsolute(p);
			int off = -r.height / 2;
			if (isLeft){
				return r.getBottomLeft().translate(-1, off);
			}
			else{
				
				return r.getBottomRight().translate(1, off);
			}
			
		}
		
	}
	private ConnectionAnchor anchorLeft;
//	private Figure anchorFigureRight;
	private ConnectionAnchor anchorRight;
	public ConnectionAnchor getAnchorLeft() {
		return anchorLeft;
	}

	public ConnectionAnchor getAnchorRight() {
		return anchorRight;
	}

	private Column model;

	public ColumnPart(Column model) {
		this.model = model;
	}

	@Override
	protected IFigure createFigure() {
		IFigure figure = new ColumnFigure(model, this);
	//	figure.addPropertyChangeListener(this);

		return figure;
	}

	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.CONTAINER_ROLE, new FlowAppEditLayoutPolicy());

	}

	@Override
	public org.eclipse.gef.DragTracker getDragTracker(Request request) {
		return getParent().getDragTracker(request);
	};
	
	public void refreshVisuals() {
		
		
		ColumnFigure figure = (ColumnFigure) getFigure();
		Node model = (Node) getModel();
		
		figure.setModel(this.model);
		figure.setLabelColumn(model.getName());
		figure.setLayout(model.getLayout());
		
		super.refreshVisuals();
	}


	public void refreshAnchor(){
		List <ConnectionPart> sources = getSourceConnections();
		if (sources!=null)
		{
			for (ConnectionPart connectionSource : sources){
				connectionSource.setSourceAnchor(getSourceConnectionAnchor(connectionSource));
			}
		}
		
		List <ConnectionPart> targets = getTargetConnections();
		if (targets!=null){
			for (ConnectionPart connectionTarget : targets){
				connectionTarget.setTargetAnchor(getTargetConnectionAnchor(connectionTarget));
			}
		}
		
	}

	
	public List<Node> getModelChildren() {
		return new ArrayList<Node>();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)){
			refreshVisuals();
			
		}
		
	
	}

//	protected ConnectionAnchor getConnectionAnchor(int i) {
//
//		int j = ((TablePart) getParent()).getFigure().getBounds().x;
//
//		if (i > j) {
//			if (anchorRight == null) {
//				anchorRight = new ChopboxAnchor(anchorFigureRight);
//				
//			}
//			return anchorRight;
//		} else {
//			if (anchorLeft == null) {
//				anchorLeft = new ChopboxAnchor(((ColumnFigure) getFigure())
//						.getAnchorLeft());
//			}
//			return anchorLeft;
//		}
//
//	}
	
	

	@Override
	protected void addSourceConnection(ConnectionEditPart connection, int index) {
		super.addSourceConnection(connection, index);
	}

	@Override
	protected void addTargetConnection(ConnectionEditPart connection, int index) {
		super.addTargetConnection(connection, index);
	}

	@Override
	protected List getModelSourceConnections() {
		return ((Node) getModel()).getSourceConnections();
	}

	@Override
	protected List getModelTargetConnections() {
		return ((Node) getModel()).getTargetConnections();
	}
	
	public ConnectionAnchor getAnchor(
			ConnectionEditPart connection) {
		
		EditPart targetPart = connection.getSource();
		EditPart sourcePart = connection.getTarget();
		
		if (targetPart == null || sourcePart == null){
			return null;
		}
		//((GraphicalEditPart)targetPart.getParent().getParent()).getFigure().translateToAbsolute(((GraphicalEditPart)targetPart.getParent()).getFigure());
		Rectangle r1 = ((TablePart)targetPart.getParent()).getFigure().getClientArea().getCopy();
		((TablePart)targetPart.getParent()).getFigure().translateToAbsolute(r1);
		
		Rectangle r2 = ((TablePart)sourcePart.getParent()).getFigure().getClientArea().getCopy();
		((TablePart)sourcePart.getParent()).getFigure().translateToAbsolute(r2);
//		((SchemaPart)sourcePart.getParent().getParent()).getFigure().translateToRelative(r2);
//		Object o = ((FlowLayout)((SchemaPart)sourcePart.getParent().getParent()).getFigure().getLayoutManager()).getConstraint(((TablePart)sourcePart.getParent()).getFigure());
		if (anchorLeft == null) {
			anchorLeft = new MyAnchor(getFigure(), true);
			
		}
		if (anchorRight == null) {
			anchorRight = new MyAnchor(getFigure(), false);
			
		}
		
		
		
		
		if (targetPart == this){
			if (r1.x < r2.x){
				if (r1.x + r1.width < r2.x){
					return anchorRight;
				}
				else{
					return anchorLeft;
				}
			}
			else{
				return anchorLeft;
			}
			
		}
		else{
			if (r1.x > r2.x){
				return anchorRight;
			}
			else{
				return anchorLeft;
			}
			
		}
		
	
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		
		return getAnchor(connection);
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		
		return null;
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		
		return getAnchor(connection);
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		
		return null;
	}

//	public void setAnchorFigure(Figure anchorFigureRight) {
//		this.anchorFigureRight = anchorFigureRight;
//	}
}