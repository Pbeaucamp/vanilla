package bpm.sqldesigner.ui.command.creation;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.SchemaNull;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.ChooseNameShell;

public class CatalogCreateCommand extends Command implements CanValidate {
	private DatabaseCluster cluster = null;
	private Catalog catalog = null;
	private Shell shell;
	private Text text;

	public CatalogCreateCommand() {
		super();
	}

	public void setCluster(Object cluster) {
		if (cluster instanceof DatabaseCluster)
			this.cluster = (DatabaseCluster) cluster;
	}

	@Override
	public boolean canExecute() {
		if (cluster == null)
			return false;
		return true;
	}

	@Override
	public void execute() {

		ChooseNameShell popup = new ChooseNameShell(Activator.getDefault()
				.getWorkbench().getDisplay(), SWT.DIALOG_TRIM
				| SWT.SYSTEM_MODAL, this);

		shell = popup.getShell();
		shell.setText(Messages.CatalogCreateCommand_0);

		text = popup.getText();

		shell.pack();
		shell.open();
	}

	public void validate() {
		if (cluster.getCalalog(text.getText()) == null) {
			catalog = new Catalog();
			catalog.setCommit(false);
			catalog.setName(text.getText());
			catalog.setNotFullLoaded(false);

			if (cluster.getProductName().equals("MySQL")) { //$NON-NLS-1$
				SchemaNull schema = new SchemaNull();
				schema.setCatalog(catalog);
				catalog.addSchema(schema);
			}

			catalog.setDatabaseCluster(cluster);
			cluster.addCatalog(catalog);

		} else {
			MessageDialog.openError(shell, Messages.CatalogCreateCommand_2, Messages.CatalogCreateCommand_3);
		}
		shell.dispose();
	}

	@Override
	public boolean canUndo() {
		if (catalog == null || cluster == null)
			return false;
		return cluster.getCalalog(catalog.getName()) != null;
	}

	@Override
	public void undo() {
		cluster.removeCatalog(catalog);
	}

	public Catalog getCatalog() {
		return catalog;
	}

	public Shell getShell() {
		return shell;
	}
}