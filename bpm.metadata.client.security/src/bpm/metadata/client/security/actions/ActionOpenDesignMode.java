package bpm.metadata.client.security.actions;

import metadata.client.Perspective;
import metadataclient.Activator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class ActionOpenDesignMode implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	private IAction action;
	private boolean actif = false;
	/**
	 * The constructor.
	 */
	public ActionOpenDesignMode() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		this.action = action;
		for(IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
  			if (pd.getId().equals(Perspective.ID)){
  				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
  				action.setEnabled(false);
  				break;
  			}
    	}
		
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
		
		window.addPerspectiveListener(new IPerspectiveListener() {
			
			public void perspectiveChanged(IWorkbenchPage page,	IPerspectiveDescriptor perspective, String changeId) {
				
				
			}
			
			public void perspectiveActivated(IWorkbenchPage page,IPerspectiveDescriptor perspective) {
				actif = perspective.getId() != Perspective.ID;
				if (action != null){
					action.setEnabled(actif);	
				}
				
			}
		});
	}
}