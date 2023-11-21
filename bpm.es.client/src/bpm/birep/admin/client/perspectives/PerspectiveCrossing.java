package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.views.ViewCrossing;

public class PerspectiveCrossing implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);	
		
//		layout.addStandaloneView(ViewTree.ID, true, IPageLayout.RIGHT, 0.65f, editorArea);
		layout.addView(ViewTree.ID, IPageLayout.RIGHT, 0.65f, editorArea);
		layout.getViewLayout(ViewTree.ID).setCloseable(false);
		
		IFolderLayout folder = layout.createFolder("bpm.birep.admin.client.perspectives.crossing.folder", IPageLayout.RIGHT, 0.35f, ViewTree.ID); //$NON-NLS-1$
		folder.addView(ViewCrossing.ID);
		layout.getViewLayout(ViewCrossing.ID).setCloseable(false);
		folder.addView("bpm.es.datasource.analyzer.ui.remapper.ViewRemapObjects"); //$NON-NLS-1$
		layout.getViewLayout("bpm.es.datasource.analyzer.ui.remapper.ViewRemapObjects").setCloseable(false); //$NON-NLS-1$
		
		folder.addPlaceholder("groupviewer.views.CrossRefView"); //$NON-NLS-1$
		
		
//		folder.addView("groupviewer.views.CrossRefView");
//		
//		layout.addStandaloneView(ViewCrossing.ID, true, IPageLayout.RIGHT, 0.35f, editorArea);
//		layout.getViewLayout(ViewCrossing.ID).setCloseable(false);
//		//layout.addStandaloneView(CrossRefView.ID, true, IPageLayout.RIGHT, 0.67f, editorArea);
//		layout.getViewLayout("groupviewer.views.CrossRefView").setCloseable(false);

	}

}
