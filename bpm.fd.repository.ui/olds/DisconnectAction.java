package bpm.fd.repository.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.fd.repository.ui.Activator;
import bpm.fd.repository.ui.Messages;

/**
 * @deprecated
 * @author ludo
 *
 */
public abstract class DisconnectAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
//		Activator.getDefault().setRepositorySocket(null);
		MessageDialog.openInformation(window.getShell(), Messages.DisconnectAction_0, Messages.DisconnectAction_1);

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
