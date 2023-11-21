package bpm.sqldesigner.query.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;

import bpm.sqldesigner.query.editpolicies.AppDeletePolicy;
import bpm.sqldesigner.query.editpolicies.AppEditLayoutPolicy;
import bpm.sqldesigner.query.figure.TableFigure;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Table;

public class TablePart extends AppAbstractEditPart {

	@Override
	protected IFigure createFigure() {
		IFigure figure = new TableFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AppDeletePolicy());
	}

	protected void refreshVisuals() {
		TableFigure figure = (TableFigure) getFigure();
		Table model = (Table) getModel();

		figure.setName(model.getName());
		figure.setLayout(model.getLayout());
	}

	@Override
	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) getFigure();

		return figure.getColumnsCompartment();
	}

	public List<Node> getModelChildren() {
		return ((Table) getModel()).getChildrenArray();
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT))
			refreshVisuals();
		if (evt.getPropertyName().equals(Node.REMOVE))
			fireSelectionChanged();
	}

	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		super.addChildVisual(childEditPart, index);
//		Rectangle childBound = (((GraphicalEditPart)childEditPart).getFigure()).getBounds();
//		
//		if (childBound.width > getFigure().getBounds().width || childBound.height * getChildren().size() > getFigure().getBounds().height ){
//			Rectangle layout = new Rectangle(childBound.x, ((GraphicalEditPart)getChildren().get(0)).getFigure().getBounds().y,
//					childBound.width + 5, childBound.height * getChildren().size() + 5);
//			((Table)getModel()).setLayout(layout);
//		}
		
		
		Figure f = ((TableFigure) getFigure()).getRightAnchors().addAnchor(
				index);
		((ColumnPart) childEditPart).setAnchorFigure(f);
		this.addNotify();
	}

}