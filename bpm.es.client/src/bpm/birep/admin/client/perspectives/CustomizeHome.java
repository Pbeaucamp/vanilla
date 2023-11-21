package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import adminbirep.Messages;
import bpm.birep.admin.client.views.ViewCustomizeHome;
import bpm.birep.admin.client.views.ViewWidgets;

public class CustomizeHome implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		IFolderLayout folderView = layout.createFolder(Messages.CustomizeHome_0, IPageLayout.LEFT, 0.50f, editorArea);
		folderView.addView(ViewCustomizeHome.ID);
		folderView.addView(ViewWidgets.ID);
		
		layout.getViewLayout(ViewCustomizeHome.ID).setCloseable(false);
		
		layout.getViewLayout(ViewWidgets.ID).setCloseable(false);
		
//		layout.addStandaloneView(ViewCustomizeHome.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
//		layout.getViewLayout(ViewCustomizeHome.ID).setCloseable(false);
//		
//		layout.addStandaloneView(ViewWidgets.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
//		layout.getViewLayout(ViewWidgets.ID).setCloseable(false);
	}

}
