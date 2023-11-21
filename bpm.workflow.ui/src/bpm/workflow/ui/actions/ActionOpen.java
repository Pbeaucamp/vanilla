package bpm.workflow.ui.actions;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import bpm.vanilla.repository.ui.versionning.VersionningManager;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.preferences.PreferencesConstants;
import bpm.workflow.ui.views.ResourceViewPart;

/**
 * Open a workflow from a location
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class ActionOpen extends Action {

	String path;

	public ActionOpen(String path) {
		this.path = path;
	}

	public void run() {

		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		FileDialog fd = new FileDialog(sh);
		fd.setFilterExtensions(new String[] { "*.*" }); //$NON-NLS-1$
		String fileName = null;

		if(path != null) {
			fileName = path;
		}
		else {
			fileName = fd.open();
		}

		if(fileName == null) {
			return;
		}

		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			page.openEditor(new WorkflowEditorInput(new File(fileName)), WorkflowMultiEditorPart.ID, true);
			Activator.getDefault().getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(fileName) != null);
			addFileToList(fileName);
			Activator.getDefault().getSessionSourceProvider().setModelOpened(true);
			ResourceViewPart v = (ResourceViewPart) page.findView(ResourceViewPart.ID);
			if(v != null) {
				v.refresh();
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private void addFileToList(String path) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] list = { store.getString(PreferencesConstants.P_RECENTFILE1), store.getString(PreferencesConstants.P_RECENTFILE2), store.getString(PreferencesConstants.P_RECENTFILE3), store.getString(PreferencesConstants.P_RECENTFILE4), store.getString(PreferencesConstants.P_RECENTFILE5) };

		boolean isEverListed = false;
		for(int i = 0; i < list.length; i++) {
			if(list[i].equals(path))
				isEverListed = true;
		}

		if(!isEverListed) {
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
