package bpm.sqldesigner.ui.command.update;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.ui.i18N.Messages;

public class ColumnNameUpdateCommand extends NodeNameUpdateCommand {

	@Override
	public boolean checkExists(String name)  {
		return ((Column) node).getTable().getColumn(name) == null;
	}

	@Override
	public String getText() {
		return Messages.ColumnNameUpdateCommand_0;
	}

}