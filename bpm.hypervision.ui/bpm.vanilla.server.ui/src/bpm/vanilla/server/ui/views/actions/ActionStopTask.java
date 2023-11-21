package bpm.vanilla.server.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.vanilla.server.ui.Activator;

public class ActionStopTask implements IViewActionDelegate{
	private IViewPart view;
	public ActionStopTask() {
	}


	public void init(IViewPart view) {
		this.view = view;
		
	}

	public void run(IAction action) {
		Shell sh = view.getSite().getShell();
		ISelection s = view.getSite().getSelectionProvider().getSelection();
		
		if (s.isEmpty()){
			MessageDialog.openInformation(sh, "Information", "No task selected");
		}
		
		Object o = ((IStructuredSelection)s).getFirstElement();
		
		if ( o instanceof Integer){
			
			try{
				Activator.getDefault().getServerRemote().stopTask((Integer)o);
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(sh, "Problem encountered", "Unable to Stop Task : \n\t-" + ex.getCause().getMessage());
			}
			
			
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
		
	}

	
	
}
