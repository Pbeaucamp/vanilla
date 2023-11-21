package bpm.vanilla.server.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.vanilla.server.client.communicators.TaskInfo;
import bpm.vanilla.server.client.communicators.TaskMeta;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.DialogMeta;
import bpm.vanilla.server.ui.views.StateTaskView;

public class ActionFullDescription implements IViewActionDelegate{
	private IViewPart view;
	public ActionFullDescription() {
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
				TaskMeta meta = Activator.getDefault().getServerRemote().getTaskDefinition((Integer)o);
				TaskInfo info = ((StateTaskView)view).getTaskInfo((Integer)o);
				DialogMeta dial = new DialogMeta(sh, meta, info);
				dial.open();
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(sh, "Problem encountered", "Unable to get Task Definition: \n\t-" + ex.getMessage());
			}
			
			
		}
		else{
			MessageDialog.openInformation(sh, "Information", "Can only get Infrmation n Tasks");
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
		
	}

	
	
}
