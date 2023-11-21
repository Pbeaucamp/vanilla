package bpm.gateway.ui.actions;

import java.io.OutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.runtime2.GatewayRuntimeException;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.perspectives.RuntimePerspective;
import bpm.gateway.ui.views.ConsoleRuntime;
import bpm.gateway.ui.views.RuntimeConsoleViewPart;

public class ActionPrepareRuntime extends Action {

	public ActionPrepareRuntime(){
		super(Messages.ActionPrepareRuntime_0);
	}
	
	public void run(){
		if (Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() == null){
			return;
		}
		
		for (IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
			if (pd.getId().equals(RuntimePerspective.PERSPECTIVE_ID)){
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
			}
		}
		
		GatewayEditorPart editor = (GatewayEditorPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		DocumentGateway doc = ((GatewayEditorInput)editor.getEditorInput()).getDocumentGateway();
		
		
		try {
			OutputStream os = null;
			for(IConsole c : ConsolePlugin.getDefault().getConsoleManager().getConsoles()){
				if (c instanceof ConsoleRuntime){
					os = ((ConsoleRuntime)c).getOutputStream();
				}
			}
			RuntimeConsoleViewPart view = (RuntimeConsoleViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(RuntimeConsoleViewPart.ID);
			Activator.localRuntimeEngine.init(Activator.getDefault().getRepositoryContext(), doc, view, os);
			if (view != null){
    			view.setInput(doc);
    		}
		} catch (GatewayRuntimeException e) {
			e.printStackTrace();
			MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionPrepareRuntime_1, e.getMessage());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
	
		
	}
}
