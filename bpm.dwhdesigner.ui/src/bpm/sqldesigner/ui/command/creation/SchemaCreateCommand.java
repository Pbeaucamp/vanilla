package bpm.sqldesigner.ui.command.creation;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.ChooseNameShell;

public class SchemaCreateCommand extends Command implements CanValidate {
	private Schema schema = null;
	private Catalog catalog = null;
	private Shell shell;
	private Text text;

	public SchemaCreateCommand() {
		super();
	}

	public void setCatalog(Object catalog) {
		if (catalog instanceof Catalog)
			this.catalog = (Catalog) catalog;
	}

	@Override
	public boolean canExecute() {
		if (catalog == null)
			return false;
		return true;
	}

	@Override
	public void execute() {

		ChooseNameShell popup = new ChooseNameShell(Activator.getDefault()
				.getWorkbench().getDisplay(), SWT.DIALOG_TRIM
				| SWT.SYSTEM_MODAL, this);

		shell = popup.getShell();
		shell.setText(Messages.SchemaCreateCommand_0);

		text = popup.getText();

		shell.pack();
		shell.open();
	}

	public void validate() {
		if (catalog.getSchema(text.getText()) == null) {
			schema = new Schema();
			schema.setCommit(false);
			schema.setName(text.getText());
			schema.setNotFullLoaded(false);

			schema.setCatalog(catalog);
			catalog.addSchema(schema);

		} else {
			MessageDialog.openError(shell, Messages.SchemaCreateCommand_1, Messages.SchemaCreateCommand_2);
		}
		shell.dispose();
	}

	@Override
	public boolean canUndo() {
		if (catalog == null || schema == null)
			return false;
		return catalog.getSchema(schema.getName()) != null;
	}

	@Override
	public void undo() {
		catalog.removeSchema(schema.getName());
	}

	public Schema getSchema() {
		return schema;
	}

	public Shell getShell() {
		return shell;
	}
}