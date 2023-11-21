package bpm.sqldesigner.ui.view.tab;

import java.sql.SQLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.CorrectRequestShell;

public class ExecuteSelectionListener implements SelectionListener, CanValidate {

	private TabRequests tabRequests;
	boolean validate = false;
	private Shell shell;
	private Text text;
	private String currentRequest;
	private int error;

	public ExecuteSelectionListener(TabRequests tabRequests) {
		this.tabRequests = tabRequests;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		TreeItem[] items = tabRequests.getTree().getItems();
		DatabaseCluster cluster = tabRequests.getCluster();

		error = 0;
		for (TreeItem item : items) {
			if (item.getChecked()) {
				RequestStatement req = tabRequests.getRequest(item);
				boolean ok = false;
				while (!ok)
					ok = executeStatement(item, req, cluster);

				for (TreeItem itemChild : item.getItems()) {
					if (itemChild.getChecked()) {
						req.setRequestString(itemChild.getText(1));
						ok = false;
						while (!ok)
							ok = executeStatement(itemChild, req, cluster);
					}
				}
			}
		}

		if (error > 0)
			MessageDialog
					.openError(
							Activator.getDefault().getWorkbench().getDisplay()
									.getActiveShell(),
							Messages.ExecuteSelectionListener_0,
							error
									+ Messages.ExecuteSelectionListener_1);

		else {
			MessageDialog
					.openInformation(
							Activator.getDefault().getWorkbench().getDisplay()
									.getActiveShell(),
							Messages.ExecuteSelectionListener_2,
							Messages.ExecuteSelectionListener_3);
		}

		tabRequests.reset();
	}

	public void validate() {
		validate = true;
		currentRequest = text.getText();
		shell.dispose();
	}

	private boolean executeStatement(TreeItem item, RequestStatement req,
			DatabaseCluster cluster) {
		if (!item.getText(1).equals("")) { //$NON-NLS-1$
			try {
				if (req.getRequestString().subSequence(0,
						"CREATE VIEW".length()).equals("CREATE VIEW")) //$NON-NLS-1$ //$NON-NLS-2$
					cluster.getDatabaseConnection().getSocket().setCatalog(
							req.getSchema().getCatalog().getName());
				req.execute(cluster.getDatabaseConnection());
				return true;
			} catch (SQLException e1) {
				e1.printStackTrace();
				CorrectRequestShell popup = new CorrectRequestShell(Activator
						.getDefault().getWorkbench().getDisplay(),
						SWT.DIALOG_TRIM | SWT.SYSTEM_MODAL, this, req
								.getRequestString(), e1.getMessage());

				shell = popup.getShell();
				shell.setText(Messages.ExecuteSelectionListener_7);

				text = popup.getText();
				validate = false;

				shell.pack();
				shell.open();
				while (!shell.isDisposed()) {
					if (!shell.getDisplay().readAndDispatch())
						shell.getDisplay().sleep();
				}
				
				
				if (validate == true) {
					req.setRequestString(currentRequest);
					return false;
				} else {
					error++;
					return true;
				}
			}
		}
		return true;
	}

}
