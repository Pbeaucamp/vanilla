package bpm.vanilla.server.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.vanilla.server.ui.Activator;

public class ActionHistorize implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		if (Activator.getDefault().getServerRemote() == null){
			MessageDialog.openInformation(sh, "Information", "You need to be connected to a server first.");
			return;
		}
		try{
			Activator.getDefault().getServerRemote().historize();
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Problem Occured", ex.getMessage());
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
