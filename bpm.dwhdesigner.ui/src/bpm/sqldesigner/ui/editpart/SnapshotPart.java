package bpm.sqldesigner.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.ui.editpolicie.AppEditLayoutPolicy;
import bpm.sqldesigner.ui.figure.SchemaFigure;
import bpm.sqldesigner.ui.figure.SnapshotFigure;

public class SnapshotPart extends AppAbstractEditPart {

	@Override
	protected IFigure createFigure() {
		IFigure figure = new SnapshotFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());

	}

	@Override
	protected void refreshVisuals() {
		SnapshotFigure figure = (SnapshotFigure) getFigure();
		DocumentSnapshot model = (DocumentSnapshot) getModel();

		figure.setName(model.getName());
	}

	@Override
	public List<Node> getModelChildren() {
		return (List)((DocumentSnapshot) getModel()).getTables();
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