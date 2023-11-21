package bpm.sqldesigner.ui.command.update;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.popup.CanValidate;
import bpm.sqldesigner.ui.popup.EditRequestShell;

public class RequestUpdateCommand extends Command implements CanValidate {
	protected TreeItem item = null;
	protected Shell shell;
	protected Text textValue;
	private boolean isOk = false;
	private String oldValue;

	public RequestUpdateCommand() {
		super();
	}

	public void setItem(TreeItem item) {
		this.item = item;
	}

	@Override
	public boolean canExecute() {
		if (item == null)
			return false;
		return true;
	}

	@Override
	public void execute() {

		EditRequestShell popup = new EditRequestShell(Activator.getDefault()
				.getWorkbench().getDisplay(), SWT.DIALOG_TRIM
				| SWT.SYSTEM_MODAL | SWT.RESIZE, this);

		shell = popup.getShell();
		shell.setText(Messages.RequestUpdateCommand_0);

		textValue = popup.getText();
		textValue.setText(item.getText(1));

		shell.pack();
		shell.open();
	}

	public void validate() {
		if (!textValue.getText().equals("")) { //$NON-NLS-1$
			oldValue = item.getText(1);
			item.setText(1, textValue.getText());
			isOk = true;
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
		if (item == null || oldValue.equals("")) //$NON-NLS-1$
			return false;
		return true;
	}

	@Override
	public void undo() {
		item.setText(1, oldValue);
	}

	public Shell getShell() {
		return shell;
	}

	public boolean isOk() {
		return isOk;
	}
}