package bpm.vanilla.server.ui.views.actions.gateway;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;

import bpm.vanilla.server.client.communicators.gateway.GatewayServerClient;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.VisualConstants;

public class ActionStepsInfos extends Action{
	
	public ActionStepsInfos() {
		setId(VisualConstants.ACTION_GATEWAY_STEPS_INFOS_ID);
		setText("Gateway Execution Details");
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.GATEWAYDETAILS));
	}


	

	public void run() {
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		IViewPart view = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VisualConstants.SERVER_STATE_VIEW_ID);
		
		
		ISelection s = view.getSite().getSelectionProvider().getSelection();
		
		if (s.isEmpty()){
			MessageDialog.openInformation(sh, "Information", "No task selected");
		}
		
		Object o = ((IStructuredSelection)s).getFirstElement();
		
		if ( o instanceof Integer){
			
			try{
				List<Properties> props = ((GatewayServerClient)Activator.getDefault().getServerRemote()).getGatewayStepsInfos((Integer)o);
				DialogStepsInfos d = new DialogStepsInfos(sh, (Integer)o, props);
				d.open();
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(sh, "Problem encountered",  ex.getMessage());
			}
			
			
		}
	}

	
	
	
}

