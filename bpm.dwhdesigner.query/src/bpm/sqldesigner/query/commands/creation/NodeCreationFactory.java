package bpm.sqldesigner.query.commands.creation;


import org.eclipse.gef.requests.CreationFactory;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.Node;
import bpm.sqldesigner.query.model.Table;

public class NodeCreationFactory implements CreationFactory {
	private Node object;

	public NodeCreationFactory(Node object) {
		this.object = object;
	}

	public Node getNewObject() {
		if (object instanceof Table) {
			return object;
		}if (object instanceof Column) {
			return object;
		}
		return null;
	}

	public Object getObjectType() {
		return null;
	}
}