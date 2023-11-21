package bpm.database.ui.viewer.relations.gef.commands;

import org.eclipse.gef.commands.Command;

import bpm.database.ui.viewer.relations.gef.editparts.ColumnPart;
import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Node;



public class MoveColumnCommand extends Command {
	private ColumnPart target;
	private ColumnPart source;
	private JoinConnection j ;

	public MoveColumnCommand(ColumnPart source, ColumnPart target) {
		this.target = target;
		this.source = source;
	}

	public void execute() {
/*
		Node cTarget = (Node) target.getModel();
		Node cSource = (Node) source.getModel();

		if (cTarget.getParent().equals(cSource.getParent()))
			return;

		

		

			j = new JoinConnection(cTarget, cSource);

	*/	
		
	}

	public void undo() {
		
	}
}
