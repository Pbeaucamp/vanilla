package bpm.sqldesigner.ui.command.creation;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.wizard.TableCreateWizard;

public class TableCreateCommand extends Command implements CanValidate {
	private Schema schema;
	private Table table;
	private Shell shell;
	private TableCreateWizard wizard;
	private WizardDialog dialog;

	public TableCreateCommand() {
		super();
		schema = null;
		table = null;
	}

	public void setTable(Object table) {
		if (table instanceof Table)
			this.table = (Table) table;
	}

	public void setSchema(Object schema) {
		if (schema instanceof Schema)
			this.schema = (Schema) schema;
	}

	public void setLayout(int[] rect) {
		if (table == null)
			return;
		table.setLayout(rect[0], rect[1], rect[2], rect[3]);
	}

	@Override
	public boolean canExecute() {
		if (table == null || schema == null)
			return false;
		return true;
	}

	@Override
	public void execute() {

		Shell shell = new Shell(Activator.getDefault().getWorkbench()
				.getDisplay());
		wizard = new TableCreateWizard(schema.getCluster());
		dialog = new WizardDialog(shell, wizard);

		
		dialog.create();
		dialog.getShell().setLayout(new GridLayout());
//		dialog.getShell().pack();
		dialog.getShell().setSize(400,600);
		if (dialog.open() == Window.OK) {
			validate();
		}
	}

	public void validate() {
		if (schema.getTable(wizard.getTableName()) == null) {
			table.setName(wizard.getTableName());
			table.setCommit(false);
			table.setNotFullLoaded(false);

			table.setSchema(schema);
			schema.addTable(table);

			List<Column> list = wizard.getListColumns();

			for (Column column : list) {
				column.setTable(table);
				table.addColumn(column);
			}

		} else {
			MessageDialog.openError(shell, Messages.TableCreateCommand_0, Messages.TableCreateCommand_1);
		}
	}

	@Override
	public boolean canUndo() {
		if (schema == null || table == null)
			return false;
		return schema.getTable(table.getName()) != null;
	}

	@Override
	public void undo() {
		schema.removeTable(table.getName());
	}
}