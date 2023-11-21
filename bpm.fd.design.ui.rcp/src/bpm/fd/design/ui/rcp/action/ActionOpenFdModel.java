package bpm.fd.design.ui.rcp.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.rcp.dialogs.DialogOpenModel;

public class ActionOpenFdModel extends Action{
	public ActionOpenFdModel(){
		
	}
	
	public void run(){
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	
		DialogOpenModel d = new DialogOpenModel(sh);
		if (d.open() == DialogOpenModel.OK){
			IProject p = d.getIProject();
			
			try {
				new ActionOpenFdProject(p).run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
