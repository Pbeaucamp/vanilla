package bpm.gateway.ui.views.resources.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;

public class ActionDelete extends Action {

	private Viewer viewer;
	
	public ActionDelete(String name, Viewer viewer){
		super(name);
		this.viewer = viewer;
	}
	
	public void run(){
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		if (ss.isEmpty()){
			return;
		}
		
		for(Object o : ss.toList()){
			
			if (o instanceof Variable){
				Activator.getDefault().getCurrentInput().getDocumentGateway().removeVariable((Variable)o);
				ResourceManager.getInstance().removeVariable((Variable)o);
			}
			
			if(o instanceof Parameter) {
				Activator.getDefault().getCurrentInput().getDocumentGateway().removeParameter((Parameter) o);
				ResourceManager.getInstance().deleteParameter((Parameter) o);
			}
			
			if (o instanceof Server){
				
				try {
					((Server)o).disconnect();
				} catch (ServerException e) {
					e.printStackTrace();
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
				}
				ResourceManager.getInstance().removeServer((Server)o);
			}
			
			if (o instanceof IServerConnection){
				IServerConnection sock = (IServerConnection)o;
				
				if (sock.isOpened()){
					boolean perform = MessageDialog.openQuestion(viewer.getControl().getShell(), Messages.ActionDelete_0, Messages.ActionDelete_1);
					
					if (perform){
						try{
							sock.disconnect();
							sock.getServer().setCurrentConnection(null);
							sock.getServer().removeConnection(sock);
							
						}catch(Exception e){
							Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
							e.printStackTrace();
							MessageDialog.openError(viewer.getControl().getShell(), Messages.ActionDelete_2, e.getMessage());
						}
						
					}
					
				}
			}
			
		}
		viewer.refresh();
	}
}
