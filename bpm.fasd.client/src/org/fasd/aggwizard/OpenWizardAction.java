package org.fasd.aggwizard;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenWizardAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	public void run(IAction action) {
		WizardAggregate wiz = new WizardAggregate();
		WizardDialog d = new WizardDialog(window.getShell(), wiz);
		d.open();

	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
