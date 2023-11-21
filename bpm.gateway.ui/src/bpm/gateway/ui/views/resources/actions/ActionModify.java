package bpm.gateway.ui.views.resources.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.parameter.DialogParameter;

public class ActionModify extends Action {

	private Viewer viewer;
	
	public ActionModify(String name, Viewer viewer){
		super(name);
		this.viewer = viewer;
	}
	
	public void run(){
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		if (ss.isEmpty()){
			return;
		}
		
		if(ss.getFirstElement() != null && ss.getFirstElement() instanceof Parameter) {
			Parameter param = (Parameter)ss.getFirstElement();
			DialogParameter paramDial = new DialogParameter(viewer.getControl().getShell(), param);
			if(paramDial.open() == Dialog.OK) {
				Activator.getDefault().getCurrentInput().getDocumentGateway().removeParameter(param);
				ResourceManager.getInstance().deleteParameter(param);
				
				try {
					Activator.getDefault().getCurrentInput().getDocumentGateway().addParameter(paramDial.getParameter());
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(viewer.getControl().getShell(), Messages.ActionModify_0, e.getMessage());
				}
			}
		}
		
		viewer.refresh();
	}
}
