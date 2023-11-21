package bpm.entrepriseservices.fmdturl.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.entrepriseservices.fmdturl.views.AvailableFmdtUrl;
import bpm.entrepriseservices.fmdturl.views.TreeFmdt;

public class FmdtUrlPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(TreeFmdt.ID, true, IPageLayout.LEFT, 0.30f, editorArea);
		layout.addView(TreeFmdt.ID, IPageLayout.LEFT, 0.30f, editorArea);
		layout.getViewLayout(TreeFmdt.ID).setCloseable(false);

		IFolderLayout folder = layout.createFolder("bpm.entrepriseservices.fmdturl.perspectives.folder", IPageLayout.RIGHT, 0.5f, ViewTree.ID); //$NON-NLS-1$
		folder.addView(AvailableFmdtUrl.ID);
		layout.getViewLayout(AvailableFmdtUrl.ID).setCloseable(false);

	}

}
