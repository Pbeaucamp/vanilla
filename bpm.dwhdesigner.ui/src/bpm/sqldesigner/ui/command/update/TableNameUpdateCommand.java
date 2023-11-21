package bpm.sqldesigner.ui.command.update;

import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.ui.i18N.Messages;

public class TableNameUpdateCommand extends NodeNameUpdateCommand {

	@Override
	public boolean checkExists(String name) {
		return ((Table) node).getSchema().getTable(name) == null;
	}

	@Override
	public String getText() {
		return Messages.TableNameUpdateCommand_0;
	}

}