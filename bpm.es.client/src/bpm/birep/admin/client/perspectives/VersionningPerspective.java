package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.views.ViewVersionning;

public class VersionningPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewDataSource.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
//		layout.getViewLayout(ViewDataSource.ID).setCloseable(false);

//		layout.addStandaloneView(ViewTree.ID, true, IPageLayout.RIGHT, 0.75f, editorArea);
		layout.addView(ViewTree.ID, IPageLayout.RIGHT, 0.75f, editorArea);
		layout.getViewLayout(ViewTree.ID).setCloseable(false);
		
		
//		layout.addStandaloneView(ViewVersionning.ID, true, IPageLayout.RIGHT, 0.50f, ViewTree.ID);
		layout.addView(ViewVersionning.ID, IPageLayout.RIGHT, 0.50f, ViewTree.ID);
		layout.getViewLayout(ViewVersionning.ID).setCloseable(false);


	}

}
