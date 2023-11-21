package bpm.vanilla.server.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.vanilla.server.ui.dialogs.DialogServerMemory;

public class ActionServerMemory implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
		DialogServerMemory d = new DialogServerMemory(window.getShell());
		d.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
