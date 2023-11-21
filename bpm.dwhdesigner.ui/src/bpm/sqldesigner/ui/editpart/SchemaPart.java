package bpm.sqldesigner.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.ui.editpolicie.AppEditLayoutPolicy;
import bpm.sqldesigner.ui.figure.SchemaFigure;

public class SchemaPart extends AppAbstractEditPart {

	@Override
	protected IFigure createFigure() {
		IFigure figure = new SchemaFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());

	}

	@Override
	protected void refreshVisuals() {
		SchemaFigure figure = (SchemaFigure) getFigure();
		Schema model = (Schema) getModel();

		if (model.getCluster().getProductName().equals("MySQL")) //$NON-NLS-1$
			figure.setName(model.getCatalog().getName());
		else
			figure.setName(model.getName());
	}

	@Override
	public List<Node> getModelChildren() {
		return ((Schema) getModel()).getTablesList();
	}

	public void refreshAll() {
		refreshChildren();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_ADD)) {
			refreshChildren();
			requestListener.nodeCreated(evt.getNewValue());
		}
		if (evt.getPropertyName().equals(Node.PROPERTY_REMOVE))
			refreshChildren();
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT))
			refreshVisuals();
	}
}