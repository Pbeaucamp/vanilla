package bpm.metadata.birt.contribution.actions;

import java.io.PrintWriter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.metadata.birt.contribution.dialogs.DialogImport;

public class ActionLoad implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
		DialogImport d = new DialogImport(window.getShell());
		d.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
