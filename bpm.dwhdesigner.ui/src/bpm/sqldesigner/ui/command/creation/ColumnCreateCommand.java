package bpm.sqldesigner.ui.command.creation;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.Type;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.wizard.ColumnCreateWizard;

public class ColumnCreateCommand extends Command implements CanValidate {
	private Table table;
	private Column column;
	private Shell shell;
	private ColumnCreateWizard popup;
	private WizardDialog dialog;

	public ColumnCreateCommand() {
		super();
		table = null;
		column = null;
	}

	public void setTable(Object table) {
		if (table instanceof Table)
			this.table = (Table) table;
	}

	public void setColumn(Object column) {
		if (column instanceof Column)
			this.column = (Column) column;
	}

	public void setLayout(int[] rect) {
		if (column == null)
			return;
		column.setLayout(rect[0], rect[1], rect[2], rect[3]);
	}

	@Override
	public boolean canExecute() {
		if (table == null || column == null)
			return false;
		return true;
	}

	@Override
	public void execute() {
		// popup = new ColumnCreateShell(Activator.getDefault().getWorkbench()
		// .getDisplay(), SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL, this, table
		// .getCluster());
		//
		// shell = popup.getShell();
		// shell.setText("Create Column");
		//
		// textName = popup.getTextName();
		//
		// shell.pack();
		// shell.open();

		Shell shell = new Shell(Activator.getDefault().getWorkbench()
				.getDisplay());
		popup = new ColumnCreateWizard(table.getCluster());
		dialog = new WizardDialog(shell, popup);

		dialog.create();
		dialog.getShell().setLayout(new GridLayout());
		// dialog.getShell().setSize(350, 550);
		dialog.getShell().pack();
		if (dialog.open() == Window.OK) {
			validate();
		}

	}

	public void validate() {
		if (table.getColumn(popup.getName()) == null) {
			column.setName(popup.getName());

			Type type = table.getCluster().getTypesLists().getType(
					popup.getComboType());
			if (type == null) {
				MessageDialog.openError(shell, Messages.ColumnCreateCommand_0,
						Messages.ColumnCreateCommand_1);
				return;
			}
			column.setType(type);

			if (popup.getCheckSize()) {
				int size = -1;
				try {
					size = Integer.valueOf(popup.getTextSize());
				} catch (NumberFormatException e) {
					MessageDialog.openError(shell, Messages.ColumnCreateCommand_2,
							Messages.ColumnCreateCommand_3);
				}

				if (size > 0) {
					column.setSize(size);
					column.setNeedsSize(true);
				} else
					MessageDialog.openError(shell, Messages.ColumnCreateCommand_4,
							Messages.ColumnCreateCommand_5);
			}

			if (popup.getCheckDefault())
				column.setDefaultValue(popup.getTextDefault());

			column.setNullable(popup.getCheckNullable());
			column.setUnsigned(popup.getCheckUnsigned());
			column.setPrimaryKey(popup.getCheckPK());

			column.setCommit(false);
			column.setNotFullLoaded(false);

			column.setTable(table);
			table.addColumn(column);
		} else {
			MessageDialog.openError(shell, Messages.ColumnCreateCommand_6, Messages.ColumnCreateCommand_7);
		}
		// shell.dispose();
		dialog.close();
	}

	@Override
	public boolean canUndo() {
		if (table == null || column == null)
			return false;
		return table.getColumn(column.getName()) != null;
	}

	@Override
	public void undo() {
		table.removeColumn(column.getName());
	}
}