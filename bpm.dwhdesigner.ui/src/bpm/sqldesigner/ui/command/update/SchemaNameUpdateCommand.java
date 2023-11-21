package bpm.sqldesigner.ui.command.update;

import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.ui.i18N.Messages;

public class SchemaNameUpdateCommand extends NodeNameUpdateCommand {

	@Override
	public boolean checkExists(String name) {
		return ((Schema) node).getCatalog().getSchema(name) == null;
	}

	@Override
	public String getText() {
		return Messages.SchemaNameUpdateCommand_0;
	}

}