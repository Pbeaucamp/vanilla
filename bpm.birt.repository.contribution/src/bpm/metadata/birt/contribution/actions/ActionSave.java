package bpm.metadata.birt.contribution.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.metadata.birt.contribution.dialogs.DialogSave;

public class ActionSave implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {

		Shell dialog = new Shell(window.getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL); 
		dialog.setSize(100,100);
      	//dialog.setText(""); 
	      
		DialogSave d = new DialogSave(dialog);
		//d.
		//d.getShell().getSize();
		//d.
		d.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
