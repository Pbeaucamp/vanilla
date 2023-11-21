package bpm.sqldesigner.query.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import bpm.sqldesigner.query.editpolicies.AppEditLayoutPolicy;
import bpm.sqldesigner.query.figure.SchemaFigure;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Schema;

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
	

	protected void refreshVisuals() {
		SchemaFigure figure = (SchemaFigure) getFigure();
		Schema model = (Schema) getModel();

		figure.setName(model.getName());
	}

	public List<Node> getModelChildren() {
		return ((Schema) getModel()).getChildrenArray();
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_ADD))
			refreshChildren();
		if (evt.getPropertyName().equals(Node.PROPERTY_REMOVE))
			refreshChildren();
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT))
			refreshVisuals();
		if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
			refreshSourceConnections();
		}
		if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP) ){
			refreshTargetConnections();
		}
	}
}