package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.ViewProperties;
import bpm.birep.admin.client.views.ViewRepositoryDefinition;
import bpm.birep.admin.client.views.ViewUser;

public class PerspectiveRepositoryAdmin implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewProperties.ID, true, IPageLayout.RIGHT, 0.50f, editorArea);
		layout.addView(ViewProperties.ID, IPageLayout.RIGHT, 0.50f, editorArea);
		layout.getViewLayout(ViewProperties.ID).setCloseable(false);

//		layout.addStandaloneView(ViewRepositoryDefinition.ID, true, IPageLayout.TOP, 0.50f, editorArea);
		layout.addView(ViewRepositoryDefinition.ID, IPageLayout.TOP, 0.50f, editorArea);
		layout.getViewLayout(ViewRepositoryDefinition.ID).setCloseable(false);

//		layout.addStandaloneView(ViewUser.ID, true, IPageLayout.BOTTOM, 0.50f, editorArea);
		layout.addView(ViewUser.ID, IPageLayout.BOTTOM, 0.50f, editorArea);
		layout.getViewLayout(ViewUser.ID).setCloseable(false);


	}

}
