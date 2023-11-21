package bpm.sqldesigner.ui.command.drop;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;

public class NodeDropCommand extends Command {
	protected Node node = null;

	public NodeDropCommand() {
		super();
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public boolean canExecute() {
		if (node == null)
			return false;
		return true;
	}

	@Override
	public void execute() {

		MessageBox mb = new MessageBox(Activator.getDefault().getWorkbench()
				.getDisplay().getActiveShell(), SWT.YES | SWT.NO
				| SWT.SYSTEM_MODAL);
		mb.setText(Messages.NodeDropCommand_0);
		mb.setMessage(Messages.NodeDropCommand_1 + getText()+" ?"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$

		int val = mb.open();
		if (val == SWT.YES) {
			removeNode();
		}
	}

	public String getText() {
		return ""; //$NON-NLS-1$
	}
	
	public void removeNode() {
	}

	@Override
	public boolean canUndo() {
		return true;
	}
}