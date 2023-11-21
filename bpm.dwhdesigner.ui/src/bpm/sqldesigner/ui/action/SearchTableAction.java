package bpm.sqldesigner.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.editor.SQLDesignGraphicalEditor;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.wizard.SearchWizard;

public class SearchTableAction extends Action {

	public SearchTableAction() {
		setText(Messages.SearchTableAction_0);
		setImageDescriptor(Activator.getDefault().getImageRegistry()
				.getDescriptor("search")); //$NON-NLS-1$
	}

	@Override
	public void run() {
		Schema schema = ((SQLDesignGraphicalEditor) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor()).getSchema();
		Shell shell = new Shell(Activator.getDefault().getWorkbench()
				.getDisplay());
		SearchWizard wizard = new SearchWizard(schema);
		WizardDialog dialog = new WizardDialog(shell, wizard);

		dialog.create();
		shell.setLayout(new GridLayout());
		dialog.setBlockOnOpen(false);
		dialog.getShell().pack();
		dialog.open();
		dialog.setBlockOnOpen(true);
		wizard.loadTables();

		if (dialog.open() == Window.OK) {
			String tableName = wizard.getTableSelection();
			schema.getTable(tableName).setFocus();
		}
	}

}
