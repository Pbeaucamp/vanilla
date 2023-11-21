package bpm.vanilla.server.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.wizard.RunReportWizard;

public class ActionRunTask implements IWorkbenchWindowActionDelegate {
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
		RunReportWizard wiz = new RunReportWizard();
		WizardDialog dial = new WizardDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
		
		dial.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
