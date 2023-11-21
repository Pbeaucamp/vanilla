package bpm.sqldesigner.ui.command.update;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Type;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.ChangeColumnTypeShell;

public class ColumnTypeUpdateCommand extends Command implements CanValidate {
	protected Column column = null;
	protected Shell shell;
	protected Text text;
	protected String oldName = ""; //$NON-NLS-1$
	private boolean isOk = false;
	private ChangeColumnTypeShell popup;

	public ColumnTypeUpdateCommand() {
		super();
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	@Override
	public boolean canExecute() {
		if (column == null)
			return false;
		return true;
	}

	@Override
	public void execute() {

		popup = new ChangeColumnTypeShell(Activator.getDefault().getWorkbench()
				.getDisplay(), SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL, this, column);

		shell = popup.getShell();
		shell.setText(Messages.ColumnTypeUpdateCommand_1);

		shell.pack();
		shell.open();
	}

	public void validate() {
		Type type = column.getCluster().getTypesLists().getType(
				popup.getComboType().getText());
		if (type == null) {
			MessageDialog.openError(shell, Messages.ColumnTypeUpdateCommand_2, Messages.ColumnTypeUpdateCommand_3);
			return;
		}
		column.setType(type);

		if (popup.getCheckSize().getSelection()) {
			int size = -1;
			try {
				size = Integer.valueOf(popup.getTextSize().getText());
			} catch (NumberFormatException e) {
				MessageDialog.openError(shell, Messages.ColumnTypeUpdateCommand_4,
						Messages.ColumnTypeUpdateCommand_5);
			}

			if (size > 0) {
				column.setSize(size);
				column.setNeedsSize(true);
			} else
				MessageDialog.openError(shell, Messages.ColumnTypeUpdateCommand_6,
						Messages.ColumnTypeUpdateCommand_7);
		}

		if (popup.getCheckDefault().getSelection())
			column.setDefaultValue(popup.getTextDefault().getText());

		column.setNullable(popup.getCheckNullable().getSelection());
		column.setUnsigned(popup.getCheckUnsigned().getSelection());

		column.setCommit(false);
		column.setNotFullLoaded(false);

		isOk = true;

		shell.dispose();
	}

	@Override
	public boolean canUndo() {
		if (column == null || oldName.equals("")) //$NON-NLS-1$
			return false;
		return !checkExists(column.getName());
	}

	@Override
	public void undo() {
		column.setName(oldName);
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

	public boolean checkExists(String name) {
		return (column).getTable().getColumn(name) == null;
	}
}