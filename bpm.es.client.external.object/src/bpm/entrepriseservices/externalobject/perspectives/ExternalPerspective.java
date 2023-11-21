package bpm.entrepriseservices.externalobject.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.entrepriseservices.externalobject.views.ExternalCallsUrlManagementView;
import bpm.entrepriseservices.externalobject.views.ExternalCallsView;
import bpm.entrepriseservices.externalobject.views.ExternalObjectGroupManagementView;

public class ExternalPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ExternalCallsView.ID, true, IPageLayout.LEFT, 0.30f, editorArea);
		layout.addView(ExternalCallsView.ID, IPageLayout.LEFT, 0.30f, editorArea);
		layout.getViewLayout(ExternalCallsView.ID).setCloseable(false);

		IFolderLayout folder = layout.createFolder("bpm.entrepriseservices.externalobject.perspectives.folder", IPageLayout.RIGHT, 0.5f, ViewTree.ID); //$NON-NLS-1$
		folder.addView(ExternalObjectGroupManagementView.ID);

		layout.getViewLayout(ExternalObjectGroupManagementView.ID).setCloseable(false);
		folder.addView(ExternalCallsUrlManagementView.ID);
		layout.getViewLayout(ExternalCallsUrlManagementView.ID).setCloseable(false);
		


	}

}
