package bpm.gateway.ui.actions;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.preferences.PreferencesConstants;
import bpm.gateway.ui.views.ResourceViewPart;
import bpm.vanilla.repository.ui.versionning.VersionningManager;

public class ActionOpen extends Action {
	
	String path ;
	public ActionOpen(String path){
		this.path = path;
	}
	
	public void run(){
		

		
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		
		FileDialog fd = new FileDialog(sh);
		fd.setFilterExtensions(new String[]{"*.*"}); //$NON-NLS-1$
		String fileName = null;
		
		if (path != null){
			fileName = path;
		}
		else{
			fileName = fd.open();
		}
		
		
		if (fileName == null){
			return;
		}
		
		
		        		
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			GatewayEditorInput input = new GatewayEditorInput(new File(fileName));
			
			ResourceManager mgr = input.getDocumentGateway().getResourceManager();
			
			//inti the GATEWAY_HOME 
			mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue(Platform.getInstallLocation().getURL().getPath());
			
			//add the variable TEMP folder
			
			mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
			Variable GATEWAY_TEMP = mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
			GATEWAY_TEMP.setValue(Platform.getInstallLocation().getURL().getPath() + "/temp/"); //$NON-NLS-1$
			
		
			File f = new File(GATEWAY_TEMP.getValueAsString());
			if (!f.exists()){

				f.mkdirs();
				
			}

			page.openEditor(input, GatewayEditorPart.ID, false);
			Activator.getDefault().getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(fileName) != null);
			addFileToList(fileName);

			ResourceViewPart v = (ResourceViewPart)page.findView(ResourceViewPart.ID);
			if (v != null){
				v.refresh();
			}
			
			Activator.getDefault().getSessionSourceProvider().setDirectoryItemId(null);
			Activator.getDefault().getSessionSourceProvider().setModelOpened(true);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionOpen_0,Messages.ActionOpen_1 + e.getMessage());
		}
		
		
		
	}
	
	private void addFileToList(String path){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = {store.getString(PreferencesConstants.P_RECENTFILE1),
						store.getString(PreferencesConstants.P_RECENTFILE2),
						store.getString(PreferencesConstants.P_RECENTFILE3),
						store.getString(PreferencesConstants.P_RECENTFILE4),
						store.getString(PreferencesConstants.P_RECENTFILE5)};

		boolean isEverListed = false;
		for(int i=0;i<list.length;i++){
			if (list[i].equals(path))
				isEverListed = true;
		}

		if (!isEverListed){
			list[4] = list[3];
    		list[3] = list[2];
    		list[2] = list[1];
    		list[1] = list[0];
    		list[0] = path;
		}
		
		
		store.setValue(PreferencesConstants.P_RECENTFILE1, list[0]);
		store.setValue(PreferencesConstants.P_RECENTFILE2, list[1]);
		store.setValue(PreferencesConstants.P_RECENTFILE3, list[2]);
		store.setValue(PreferencesConstants.P_RECENTFILE4, list[3]);
		store.setValue(PreferencesConstants.P_RECENTFILE5, list[4]);
		
	}
}
