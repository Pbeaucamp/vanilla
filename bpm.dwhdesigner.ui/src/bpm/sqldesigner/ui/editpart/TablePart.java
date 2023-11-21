package bpm.sqldesigner.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.query.editor.SQLRootPart;
import bpm.sqldesigner.ui.editpolicie.FlowLayoutPolicy;
import bpm.sqldesigner.ui.figure.TableFigure;

public class TablePart extends AppAbstractEditPart {

	@Override
	protected IFigure createFigure() {
		IFigure figure = new TableFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FlowLayoutPolicy());
		// installEditPolicy(EditPolicy.COMPONENT_ROLE, new AppDeletePolicy());
	}

	@Override
	protected void setFigure(IFigure figure) {
		super.setFigure(figure);
		((SQLRootPart) getRoot()).getViewer().getControl().redraw();

	}

	@Override
	protected void refreshVisuals() {
		TableFigure figure = (TableFigure) getFigure();
		Table model = (Table) getModel();

		figure.setName(model.getName());
		int[] layout = model.getLayout();
		figure.setLayout(new Rectangle(layout[0], layout[1], layout[2],
				layout[3]));

		Iterator<?> it = getChildren().iterator();
		while (it.hasNext()) {
			ColumnPart child = ((ColumnPart) it.next());
			child.refreshSourceConnections();
			child.refreshTargetConnections();
		}
	}

	@Override
	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) getFigure();
		return figure.getColumnsCompartment();
	}

	@Override
	public List<Column> getModelChildren() {
		return new ArrayList<Column>(((Table) getModel()).getColumns().values());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT))
			refreshVisuals();
		if (evt.getPropertyName().equals(Node.PROPERTY_ADD)) {
			refreshChildren();

			Rectangle oldLayout = getFigure().getBounds();
			Rectangle rect = new Rectangle(oldLayout.x, oldLayout.y,
					oldLayout.width, oldLayout.height + 18);
			getFigure().setBounds(rect);
			((Table) getModel()).setLayout(rect.x, rect.y, rect.width,
					rect.height);

			requestListener.nodeCreated(evt.getNewValue());
		}
		if (evt.getPropertyName().equals(Node.FOCUS)) {
			getViewer().select(this);
		}
	}

	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {

		super.addChildVisual(childEditPart, index);
		Figure f = ((TableFigure) getFigure()).getRightAnchors().addAnchor(
				index);
		((ColumnPart) childEditPart).setAnchorFigure(f);
	}

}