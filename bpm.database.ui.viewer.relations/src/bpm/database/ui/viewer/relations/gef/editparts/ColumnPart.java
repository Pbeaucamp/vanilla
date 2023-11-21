package bpm.database.ui.viewer.relations.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;

import bpm.database.ui.viewer.relations.gef.figures.ColumnFigure;
import bpm.database.ui.viewer.relations.gef.policies.FlowAppEditLayoutPolicy;
import bpm.database.ui.viewer.relations.model.Node;

public class ColumnPart extends AppAbstractEditPart {

	private ConnectionAnchor anchorLeft;
	private Figure anchorFigureRight;
	private ConnectionAnchor anchorRight;

	@Override
	protected IFigure createFigure() {
		IFigure figure = new ColumnFigure();
		figure.addPropertyChangeListener(this);
		

		return figure;
	}

	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FlowAppEditLayoutPolicy());

	}

	protected void refreshVisuals() {
		
		
		ColumnFigure figure = (ColumnFigure) getFigure();
		Node model = (Node) getModel();

		figure.setLabelColumn(model.getName());
		figure.setLayout(model.getLayout());
		
		super.refreshVisuals();
	}



	
	public List<Node> getModelChildren() {
		return new ArrayList<Node>();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)){
			refreshVisuals();
			
		}
		
	
	}

	protected ConnectionAnchor getConnectionAnchor(int i) {

		int j = ((TablePart) getParent()).getFigure().getBounds().x;

		if (i > j) {
			if (anchorRight == null) {
				anchorRight = new ChopboxAnchor(anchorFigureRight);
			}
			return anchorRight;
		} else {
			if (anchorLeft == null) {
				anchorLeft = new ChopboxAnchor(((ColumnFigure) getFigure())
						.getAnchorLeft());
			}
			return anchorLeft;
		}

	}

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

	public void setAnchorFigure(Figure anchorFigureRight) {
		this.anchorFigureRight = anchorFigureRight;
	}
}