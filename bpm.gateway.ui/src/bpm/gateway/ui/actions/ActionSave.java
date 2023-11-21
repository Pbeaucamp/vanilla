package bpm.gateway.ui.actions;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.preferences.PreferencesConstants;

public class ActionSave extends Action{
	
//	private boolean cancelled = false;
//	
//	private File file;
	
	public ActionSave(){
		super(Messages.ActionSave_0);
	}
	
//	public boolean isCancelled(){
//		return cancelled;
//	}
	
	public void run(){
		IEditorPart p = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		p.doSave(null);
		
//		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
//		
//		GatewayEditorInput doc = Activator.getDefault().getCurrentInput();
//		
//		String path = null;
//		if (doc.getName() == null || doc.getName().equals("")){ //$NON-NLS-1$
//			FileDialog fd = new FileDialog(sh, SWT.SAVE);
//			path = fd.open();
//			
//			
//		}
//		else{
//			path = doc.getName();
//		}
//		
//		if (path != null){
//			file = new File(path);
//			try {
//				doc.getDocumentGateway().write(new FileOutputStream(path));
//
//				addFileToList(path);
//			} catch (Exception e) {
//				e.printStackTrace();
//				MessageDialog.openError(sh, Messages.ActionSave_2, Messages.ActionSave_3 + e.getMessage());
//			} 
//			
//		}
//		else{
//			cancelled = true;
//		}
	}
	
	
//	public File getFile(){
//		return file;
//	}
//	
//	private void addFileToList(String path){
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		String[] list = {store.getString(PreferencesConstants.P_RECENTFILE1),
//						store.getString(PreferencesConstants.P_RECENTFILE2),
//						store.getString(PreferencesConstants.P_RECENTFILE3),
//						store.getString(PreferencesConstants.P_RECENTFILE4),
//						store.getString(PreferencesConstants.P_RECENTFILE5)};
//
//		boolean isEverListed = false;
//		for(int i=0;i<list.length;i++){
//			if (list[i].equals(path))
//				isEverListed = true;
//		}
//
//		if (!isEverListed){
//			list[4] = list[3];
//    		list[3] = list[2];
//    		list[2] = list[1];
//    		list[1] = list[0];
//    		list[0] = path;
//		}
//		
//		
//		store.setValue(PreferencesConstants.P_RECENTFILE1, list[0]);
//		store.setValue(PreferencesConstants.P_RECENTFILE2, list[1]);
//		store.setValue(PreferencesConstants.P_RECENTFILE3, list[2]);
//		store.setValue(PreferencesConstants.P_RECENTFILE4, list[3]);
//		store.setValue(PreferencesConstants.P_RECENTFILE5, list[4]);
//		
//	}
}
