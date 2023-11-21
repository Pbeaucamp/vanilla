package bpm.fd.repository.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;



/**
 * @deprecated
 * @author ludo
 *
 */
public abstract class ConnectAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
//		DialogConnect d = new DialogConnect(window.getShell());
//		d.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
