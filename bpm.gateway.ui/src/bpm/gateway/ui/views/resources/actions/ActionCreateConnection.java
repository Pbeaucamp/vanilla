package bpm.gateway.ui.views.resources.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;

import bpm.gateway.core.Server;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.resource.server.wizard.ServerWizard;

public class ActionCreateConnection extends Action {
	private Viewer viewer;
	
	public ActionCreateConnection(String name, Viewer viewer){
		super(name);
		this.viewer = viewer;
	}
	
	
	public void run(){
		
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		
		if (ss.isEmpty() || !(ss.getFirstElement() instanceof Server)){
			return;
		}
		
		
		Server s = (Server)ss.getFirstElement();
		
		
		ServerWizard wizard = new ServerWizard(s);
		
		wizard.init(Activator.getDefault().getWorkbench(),
					(IStructuredSelection)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());
		
		WizardDialog dialog = new WizardDialog(viewer.getControl().getShell(), wizard);
		dialog.create();
		dialog.getShell().setSize(800, 600);
		dialog.getShell().setText("Connection Wizard"); //$NON-NLS-1$
		
		if (dialog.open() == WizardDialog.OK){
			viewer.refresh();
			
			
			
			
		}
	}
}
