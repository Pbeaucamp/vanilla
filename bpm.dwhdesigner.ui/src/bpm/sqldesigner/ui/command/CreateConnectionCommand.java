package bpm.sqldesigner.ui.command;

import java.util.Date;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.ChooseNameShell;
import bpm.sqldesigner.ui.view.RequestsView;
import bpm.sqldesigner.ui.view.tab.TabRequests;

public class CreateConnectionCommand extends Command implements CanValidate {

	private Column columnTarget;
	private Column columnSource;
	private LinkForeignKey link = null;
	private Shell shell;
	private Text text;

	public CreateConnectionCommand(Column columnSource, Column columnTarget) {
		this.columnSource = columnSource;
		this.columnTarget = columnTarget;
	}

	@Override
	public boolean canExecute() {
		if (columnTarget != null)
			if (columnSource != null)
				if (columnSource != columnTarget)
					return true;
		return false;
	}

	@Override
	public boolean canUndo() {
		return link != null;
	}

	@Override
	public void execute() {

		if (!columnSource.getType().getName().equals(
				columnTarget.getType().getName())) {
			MessageDialog.openInformation(Activator.getDefault().getWorkbench()
					.getDisplay().getActiveShell(), Messages.CreateConnectionCommand_0,
					Messages.CreateConnectionCommand_1);
			return;
		}

		if (!columnTarget.isPrimaryKey()) {
			MessageDialog.openInformation(Activator.getDefault().getWorkbench()
					.getDisplay().getActiveShell(), Messages.CreateConnectionCommand_2,
					Messages.CreateConnectionCommand_3);
			return;
		}

		if (columnSource.getCluster().getDatabaseConnection() == null) {
			MessageDialog.openInformation(Activator.getDefault().getWorkbench()
					.getDisplay().getActiveShell(), Messages.CreateConnectionCommand_4,
					Messages.CreateConnectionCommand_5);
			return;
		}
		ChooseNameShell popup = new ChooseNameShell(Activator.getDefault()
				.getWorkbench().getDisplay(), SWT.DIALOG_TRIM
				| SWT.SYSTEM_MODAL, this);

		shell = popup.getShell();
		shell.setText(Messages.CreateConnectionCommand_6);

		text = popup.getText();

		shell.pack();
		shell.open();
	}

	@Override
	public void redo() {
		columnSource.addSourceForeignKey(link);
		columnTarget.setTargetPrimaryKey(link);
	}

	@Override
	public void undo() {
		columnTarget.setTargetPrimaryKey(null);
		columnSource.removeForeignKey(link);
	}


	public void validate() {
		link = new LinkForeignKey(columnTarget, columnSource);

		if (!text.getText().equals("")) //$NON-NLS-1$
			link.setName(text.getText());

		else
			link.setName("link_" + (new Date()).getTime()); //$NON-NLS-1$

		shell.dispose();

		columnTarget.addSourceForeignKey(link);
		columnSource.setTargetPrimaryKey(link);

		RequestsView view = (RequestsView) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(RequestsView.ID);

		TabRequests tab = view.getTab(columnTarget.getCluster());

		tab.nodeCreated(link);
	}

}
