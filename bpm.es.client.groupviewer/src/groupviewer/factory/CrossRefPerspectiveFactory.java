package groupviewer.factory;

import groupviewer.views.CrossRefView;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.views.ViewCrossing;

public class CrossRefPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);	
		
		layout.addStandaloneView(ViewTree.ID, true, IPageLayout.RIGHT, 0.65f, editorArea);
		layout.getViewLayout(ViewTree.ID).setCloseable(false);
		
		IFolderLayout folder = layout.createFolder("folder", IPageLayout.RIGHT, 0.35f, ViewTree.ID); //$NON-NLS-1$
		folder.addView(ViewCrossing.ID);
		folder.addView(CrossRefView.ID);
		
		layout.addStandaloneView(ViewCrossing.ID, true, IPageLayout.RIGHT, 0.35f, editorArea);
		layout.getViewLayout(ViewCrossing.ID).setCloseable(false);
		//layout.addStandaloneView(CrossRefView.ID, true, IPageLayout.RIGHT, 0.67f, editorArea);
		layout.getViewLayout(CrossRefView.ID).setCloseable(false);
	}

}
