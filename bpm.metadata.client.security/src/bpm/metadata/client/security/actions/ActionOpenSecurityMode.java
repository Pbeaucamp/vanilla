package bpm.metadata.client.security.actions;

import metadataclient.Activator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import bpm.metadata.client.security.perspectives.SecurityPerspective;

public class ActionOpenSecurityMode implements IWorkbenchWindowActionDelegate {
	private boolean actif = false;
	private IAction action;
	public void dispose() {
		

	}

	public void init(IWorkbenchWindow window) {
		window.addPerspectiveListener(new IPerspectiveListener() {
			
			public void perspectiveChanged(IWorkbenchPage page,	IPerspectiveDescriptor perspective, String changeId) {
				
				
			}
			
			public void perspectiveActivated(IWorkbenchPage page,IPerspectiveDescriptor perspective) {
				actif = perspective.getId() != SecurityPerspective.ID;
				if (action != null){
					action.setEnabled(actif);	
				}
			}
		});

	}

	public void run(IAction action) {
		this.action = action;
		for(IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
  			if (pd.getId().equals(SecurityPerspective.ID)){
  				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
  				action.setEnabled(false);
  				break;
  			}
    	}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		

	}

}
