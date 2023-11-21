package bpm.sqldesigner.ui.command.creation;

import org.eclipse.gef.requests.CreationFactory;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.Type;
import bpm.sqldesigner.api.model.view.SQLView;

public class NodeCreationFactory implements CreationFactory {
	private Class<?> template;

	public NodeCreationFactory(Class<?> t) {
		template = t;
	}

	public Object getNewObject() {
		if (template == null)
			return null;
		if (template == Table.class) {
			Table table = new Table();
			table.setName("new Table"); //$NON-NLS-1$
			return table;
		} else if (template == Column.class) {
			Column column = new Column();
			column.setName("new Column"); //$NON-NLS-1$
			Type type = new Type();
			type.setId(10);
			type.setName("TYPE"); //$NON-NLS-1$
			column.setType(type);
			return column;
		} else if (template == SQLView.class) {
		}
		return null;
	}

	public Object getObjectType() {
		return template;
	}
}