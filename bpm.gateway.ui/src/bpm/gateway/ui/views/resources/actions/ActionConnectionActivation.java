package bpm.gateway.ui.views.resources.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;

public class ActionConnectionActivation extends Action {

	private Viewer viewer;
	
	public ActionConnectionActivation(String name, Viewer viewer){
		super(name);
		this.viewer = viewer;
	}
	
	public void run(){
		IStructuredSelection ss = getSelection();
		
		if (ss.isEmpty() || !(ss.getFirstElement() instanceof IServerConnection)){
			return;
		}
		
		IServerConnection sock = (IServerConnection)ss.getFirstElement();
		
		if (sock.getServer().getCurrentConnection(null) != sock){
			try {
				sock.getServer().setCurrentConnection(sock);
				viewer.refresh();
			} catch (ServerException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				e.printStackTrace();
				MessageDialog.openError(viewer.getControl().getShell(), Messages.ActionConnectionActivation_0, e.getMessage());

			}
		}
		
		
		
	}
	
	
	private IStructuredSelection getSelection(){
		return (IStructuredSelection)viewer.getSelection();
	}
}
