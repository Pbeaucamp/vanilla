package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.datalists.ViewLists;
import bpm.birep.admin.client.views.datasources.ViewDataSource;

public class PerspectiveMisc implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewDataSource.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.addView(ViewDataSource.ID, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(ViewDataSource.ID).setCloseable(false);

//		layout.addStandaloneView(ViewLists.ID, true, IPageLayout.RIGHT, 0.50f, ViewDataSource.ID);
		layout.addView(ViewLists.ID, IPageLayout.RIGHT, 0.50f, ViewDataSource.ID);
		layout.getViewLayout(ViewLists.ID).setCloseable(false);

//		layout.addStandaloneView(ViewLists.ID, true, IPageLayout.BOTTOM, 0.50f, ViewDataSource.ID);
//		layout.getViewLayout(ViewLists.ID).setCloseable(false);
	}

}
