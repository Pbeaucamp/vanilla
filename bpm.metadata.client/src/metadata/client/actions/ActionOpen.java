package metadata.client.actions;


import java.io.FileInputStream;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.repository.ui.versionning.VersionningManager;

public class ActionOpen extends Action {
	private String path = null;
	
	public ActionOpen(){
		super(Messages.ActionOpen_0); //$NON-NLS-1$
	}
	
	public ActionOpen(String s){
		super(Messages.ActionOpen_1 + s); //$NON-NLS-1$
		path = s;
	}

	public void run(){
		
		
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		if (path == null){
			FileDialog d = new FileDialog(shell, SWT.OPEN);
			d.setFilterExtensions(new String[]{"*.freemetadata", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
			path = d.open();
			if (path == null){
				return;
			}
		}	
		try {
			Activator.getDefault().setCurrentModel(new FileInputStream(path), null);
			
			Activator.getDefault().getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(path) != null);
			Activator.getDefault().setFileName(path);
			
		} catch(Exception e){
			

			MessageDialog.openError(shell, Messages.ActionOpen_2, e.getMessage()); //$NON-NLS-1$
			e.printStackTrace();
			Activator.getLogger().error(Messages.ActionOpen_3, e); //$NON-NLS-1$
		}
			
			
	}
}
