package bpm.gateway.ui.views.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.parameter.DialogParameter;
import bpm.gateway.ui.views.ResourceViewPart;

public class CreateParameterActionDelegate implements IViewActionDelegate {
	private ResourceViewPart view ;
	
	public void init(IViewPart view) {
		this.view = (ResourceViewPart)view;

	}

	public void run(IAction action) {
		
		
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return ;
		}
		DialogParameter dial = new DialogParameter(part.getSite().getShell());
		if (dial.open() == DialogParameter.OK){
			
			
			
			IEditorInput in = part.getEditorInput();
			
			
			if (in  instanceof GatewayEditorInput){
				try {
					((GatewayEditorInput)in).getDocumentGateway().addParameter(dial.getParameter());
					view.refresh();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(view.getSite().getShell(), Messages.CreateParameterActionDelegate_0, e.getMessage());
				}
				
			}
			
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
