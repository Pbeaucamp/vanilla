package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.ViewGroup;
import bpm.birep.admin.client.views.ViewProperties;
import bpm.birep.admin.client.views.ViewRole;
import bpm.birep.admin.client.views.ViewUser;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewUser.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.addView(ViewUser.ID, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(ViewUser.ID).setCloseable(false);
		
//		layout.addStandaloneView(ViewProperties.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.addView(ViewProperties.ID, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(ViewProperties.ID).setCloseable(false);
		
//		layout.addStandaloneView(ViewRole.ID, true, IPageLayout.TOP, 0.50f, ViewProperties.ID);
		layout.addView(ViewRole.ID, IPageLayout.TOP, 0.50f, ViewProperties.ID);
		layout.getViewLayout(ViewRole.ID).setCloseable(false);
		
//		layout.addStandaloneView(ViewGroup.ID, true, IPageLayout.BOTTOM, 0.50f, ViewUser.ID);
		layout.addView(ViewGroup.ID, IPageLayout.BOTTOM, 0.50f, ViewUser.ID);
		layout.getViewLayout(ViewGroup.ID).setCloseable(false);
		
		
	}
	
	
}
