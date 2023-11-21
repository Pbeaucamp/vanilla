package metadata.client.actions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import metadata.client.i18n.Messages;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class ActionSave extends Action {

	private String path;
	private File file;
	
	public ActionSave(String actionName, String s){
		super(actionName);
		this.path = s;
	}
	
	public void run(){
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		try {
			saveDocument(shell, false);
		} catch (IOException e) {
			e.printStackTrace();
			Activator.getLogger().error(Messages.ActionSave_4, e);
			MessageDialog.openError(shell, Messages.ActionSave_5, Messages.ActionSave_6 + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			
			MessageDialog d = new MessageDialog(shell, "Error while saving.", null,
					e.getMessage() + "\n\n You can modify you document by clicking 'Cancel' or try to force save your document however be aware that some of your work may be lost.",
					MessageDialog.QUESTION, new String[]{"Force Saving", IDialogConstants.CANCEL_LABEL}, 0
					);
			int i = d.open();
			switch(i){
			case 0:
				try {
					saveDocument(shell, true);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(shell, Messages.ActionSave_5, e.getMessage());
				}
				break;
			case 1:
				break;
			}
		}
	}
	
	private void saveDocument(Shell shell, boolean forceSave) throws Exception {
		if (path == null || !path.contains(".freemetadata") || path.contains("*")){ //$NON-NLS-1$
			FileDialog fd = new FileDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
			path = fd.open(); 
			if (path == null){
				return;
			}
			
			if (!path.endsWith(".freemetadata")){ //$NON-NLS-1$
				path += ".freemetadata"; //$NON-NLS-1$
			}
		}
		String s = Activator.getDefault().getModel().getXml(forceSave);

		PrintWriter fw = new PrintWriter(path, "UTF-8");
		fw.write(s);
		fw.close();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Activator.getDefault().getModel().getProperties().setModification(sdf.format(Calendar.getInstance().getTime()));
		Activator.getDefault().setFileName(path);
		Activator.getDefault().setSaved();
		
		addFileToList(path);
		
		MessageDialog.openInformation(shell, Messages.ActionSave_2, Messages.ActionSave_3);
		file = new File(path);
	}

	public File getFile(){
		return file;
	}
	
	private void addFileToList(String path){
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = {store.getString(PreferenceConstants.P_RECENTFILE1),
						store.getString(PreferenceConstants.P_RECENTFILE2),
						store.getString(PreferenceConstants.P_RECENTFILE3),
						store.getString(PreferenceConstants.P_RECENTFILE4),
						store.getString(PreferenceConstants.P_RECENTFILE5)};

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
		
		
		store.setValue(PreferenceConstants.P_RECENTFILE1, list[0]);
		store.setValue(PreferenceConstants.P_RECENTFILE2, list[1]);
		store.setValue(PreferenceConstants.P_RECENTFILE3, list[2]);
		store.setValue(PreferenceConstants.P_RECENTFILE4, list[3]);
		store.setValue(PreferenceConstants.P_RECENTFILE5, list[4]);
		
	}
}
