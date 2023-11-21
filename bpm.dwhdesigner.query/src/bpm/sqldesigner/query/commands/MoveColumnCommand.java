package bpm.sqldesigner.query.commands;

import org.eclipse.gef.commands.Command;

import bpm.sqldesigner.query.model.Column;
import bpm.sqldesigner.query.model.connection.ConnectionsManager;
import bpm.sqldesigner.query.model.connection.JoinConnection;
import bpm.sqldesigner.query.part.ColumnPart;

public class MoveColumnCommand extends Command {
	private ColumnPart target;
	private ColumnPart source;
	private JoinConnection j ;

	public MoveColumnCommand(ColumnPart source, ColumnPart target) {
		this.target = target;
		this.source = source;
	}

	public void execute() {

		Column cTarget = (Column) target.getModel();
		Column cSource = (Column) source.getModel();

		if (cTarget.getParent().equals(cSource.getParent()))
			return;

		if (!cTarget.getType().equals(cSource.getType()))
			return;

		if (!ConnectionsManager.getInstance().contains(cTarget, cSource)) {

			j = new JoinConnection(cTarget, cSource);

			ConnectionsManager.getInstance().addConnection(j);
			cSource.addConnection(j);
			cTarget.addConnection(j);
		}
	}

	public void undo() {
		new ConnectionDeleteCommand(j).execute();		
	}
}
