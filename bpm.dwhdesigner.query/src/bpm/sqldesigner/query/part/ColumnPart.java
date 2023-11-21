package bpm.sqldesigner.query.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;

import bpm.sqldesigner.query.editpolicies.FlowAppEditLayoutPolicy;
import bpm.sqldesigner.query.figure.ColumnFigure;
import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.selected.SelectedColumnsManager;

public class ColumnPart extends AppAbstractEditPart {

	private ConnectionAnchor anchorLeft;
	private Figure anchorFigureRight;
	private ConnectionAnchor anchorRight;

	@Override
	protected IFigure createFigure() {
		IFigure figure = new ColumnFigure(((Column) getModel()).isSelected());
		figure.addPropertyChangeListener(this);
		((Column) getModel()).setSelected(true);
		((ColumnFigure)figure).setSelected(true);

		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FlowAppEditLayoutPolicy());

	}

	protected void refreshVisuals() {
		ColumnFigure figure = (ColumnFigure) getFigure();
		Column model = (Column) getModel();

		figure.setLabelColumn(model.getName(), model.getType(), model.isKey());
		figure.setLayout(model.getLayout());
	}



	
	public List<Node> getModelChildren() {
		return new ArrayList<Node>();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT))
			refreshVisuals();
		if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
			refreshSourceConnections();
			fireSelectionChanged();
		}
		if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)) {
			refreshTargetConnections();
			fireSelectionChanged();
		}
		if (evt.getPropertyName().equals(ColumnFigure.CHECKBOX_ACTION_ADD)) {
			SelectedColumnsManager.getInstance().addColumn((Column) getModel());
			fireSelectionChanged();
		}
		if (evt.getPropertyName().equals(ColumnFigure.CHECKBOX_ACTION_REMOVE)) {
			SelectedColumnsManager.getInstance().removeColumn(
					(Column) getModel());
			fireSelectionChanged();
		}
		if (evt.getPropertyName().equals(Column.FILTRED)) {
			((ColumnFigure) getFigure()).setFiltred(Boolean.valueOf((String) evt.getNewValue()));
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