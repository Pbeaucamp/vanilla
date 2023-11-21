package bpm.sqldesigner.ui.command.update;

import java.sql.SQLException;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.database.ExtractData;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.ChooseNameShell;

public class NodeNameUpdateCommand extends Command implements CanValidate {
	protected Node node = null;
	protected Shell shell;
	protected Text text;
	protected String oldName = ""; //$NON-NLS-1$
	private boolean isOk = false;

	public NodeNameUpdateCommand() {
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

		ChooseNameShell popup = new ChooseNameShell(Activator.getDefault()
				.getWorkbench().getDisplay(), SWT.DIALOG_TRIM
				| SWT.SYSTEM_MODAL, this);

		shell = popup.getShell();
		shell.setText(Messages.NodeNameUpdateCommand_1 + getText() + Messages.NodeNameUpdateCommand_2);

		text = popup.getText();

		shell.pack();
		shell.open();
	}

	public void validate() {
		if (checkExists(text.getText())) {
			oldName = node.getName();
			node.setCommit(false);

			try {
				ExtractData.extractWhenNotLoaded(node);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			node.setName(text.getText());

			node.getParent().updateName(node, oldName);

			isOk = true;

		} else {
			MessageDialog.openError(shell, Messages.NodeNameUpdateCommand_3, getText()
					+ Messages.NodeNameUpdateCommand_4);
		}
		shell.dispose();
	}

	public boolean checkExists(String name) {
		return false;
	}

	public String getText() {
		return ""; //$NON-NLS-1$
	}

	@Override
	public boolean canUndo() {
		if (node == null || oldName.equals("")) //$NON-NLS-1$
			return false;
		return !checkExists(node.getName());
	}

	@Override
	public void undo() {
		node.setName(oldName);
	}

	public Shell getShell() {
		return shell;
	}

	public String getOldName() {
		return oldName;
	}

	public boolean isOk() {
		return isOk;
	}
}