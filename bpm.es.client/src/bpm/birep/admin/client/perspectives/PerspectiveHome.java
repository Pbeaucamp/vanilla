package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.ViewHome;

public class PerspectiveHome implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewHome.ID, true, IPageLayout.LEFT, 1.0f, editorArea);
		layout.addView(ViewHome.ID, IPageLayout.LEFT, 1.0f, editorArea);
		layout.getViewLayout(ViewHome.ID).setCloseable(false);
	}
}
