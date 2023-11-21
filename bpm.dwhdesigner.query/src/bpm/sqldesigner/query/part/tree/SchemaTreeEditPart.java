package bpm.sqldesigner.query.part.tree;

import java.beans.PropertyChangeEvent;
import java.util.List;

import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Schema;

public class SchemaTreeEditPart extends AppAbstractTreeEditPart {

	protected List<Node> getModelChildren() {
		return ((Schema) getModel()).getChildrenArray();
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_ADD))
			refreshChildren();
		if (evt.getPropertyName().equals(Node.PROPERTY_REMOVE))
			refreshChildren();
	}
}