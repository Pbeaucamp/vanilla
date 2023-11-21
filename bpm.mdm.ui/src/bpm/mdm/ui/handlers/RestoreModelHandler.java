package bpm.mdm.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;

public class RestoreModelHandler extends AbstractHandler {


	public RestoreModelHandler() {
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		if (!MessageDialog.openQuestion(window.getShell(), Messages.RestoreModelHandler_0, Messages.RestoreModelHandler_1)){
			return null;
		}
		
		try{
			Activator.getDefault().getMdmProvider().loadModel();
			Activator.getDefault().getControler().setModelReloaded();
			
		}catch(Exception ex){
			
		}
		return null;
	}
	
	
}
