package bpm.sqldesigner.ui.action;

import java.io.FileInputStream;

import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.document.SchemaView;
import bpm.sqldesigner.api.xml.SaveData;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.editor.SQLDesignEditorInput;
import bpm.sqldesigner.ui.editor.SQLDesignGraphicalEditor;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.internal.Workspace;
import bpm.sqldesigner.ui.internal.WorkspaceSerializer;
import bpm.sqldesigner.ui.preferences.PreferenceConstants;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditor;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;

public class ActionOpenWorkspace extends Action {

	String path;

	public ActionOpenWorkspace(String path) {
		this.path = path;
	}

	@Override
	public void run() {
		if (!SaveData.checkFile(path)) {
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.ActionOpenWorkspace_0,Messages.ActionOpenWorkspace_1+ path+  Messages.ActionOpenWorkspace_2);
			return;
		}



		try {
			
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			
			
			Workspace wks = WorkspaceSerializer.load(new FileInputStream(path));
			wks.setFileName(path);
			
			Activator.getDefault().setWorkspace(wks);
			
			
			/*
			 *open views 
			 */
			for(SchemaView v : wks.getOpenedSchemaViews()){
				try{
					SQLDesignEditorInput editorInput = new SQLDesignEditorInput(v);
					SQLDesignGraphicalEditor editor = (SQLDesignGraphicalEditor)page.openEditor(editorInput, SQLDesignGraphicalEditor.ID);

					editor.setSchemaLoaded(true);

					((ScalableRootEditPart) editor.getViewer().getRootEditPart()).getZoomManager().setZoom(v.getScale());

					editor.setName(v.getCluster().getProductName() + " - " + v.getName()); //$NON-NLS-1$
					editor.getViewer().setContents(v.getSchema());
				}catch(Exception ex){
					ex.printStackTrace();
				}
							
			}
			
			/*
			 * open datawarehouse views 
			 */
			for(DocumentSnapshot v : wks.getOpenedDocumentSnapshots()){
				try{
					SnapshotEditorInput editorInput = new SnapshotEditorInput(v);
					page.openEditor(editorInput, SnapshotEditor.ID);

				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionOpenWorkspace_4, Messages.ActionOpenWorkspace_5 + path + "\n" + e.getMessage());  //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
		}

		
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

		

		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getShell().setText("BiDwhDesigner - " + path); //$NON-NLS-1$

		
	}
}