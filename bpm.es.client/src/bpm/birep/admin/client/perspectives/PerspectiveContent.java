package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.content.views.LinkedDocumentsView;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.historic.SecurityManager;
import bpm.birep.admin.client.views.ViewProperties;
import bpm.birep.admin.client.views.historic.GroupManagementView;
import bpm.birep.admin.client.views.validation.ViewValidation;

public class PerspectiveContent implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		try {
			String editorArea = layout.getEditorArea();
			
			layout.setEditorAreaVisible(false);

//			layout.addStandaloneView(ViewTree.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
			layout.addView(ViewTree.ID, IPageLayout.LEFT, 0.50f, editorArea);
			layout.getViewLayout(ViewTree.ID).setCloseable(false);
	
			IFolderLayout folder = layout.createFolder("folder", IPageLayout.RIGHT, 0.5f, ViewTree.ID); //$NON-NLS-1$
			folder.addView(ViewProperties.ID);
			layout.getViewLayout(ViewProperties.ID).setCloseable(false);
			
//			folder.addView(ViewTimeAdmin.ID);
//			layout.getViewLayout(ViewTimeAdmin.ID).setCloseable(false);
			
			folder.addView(GroupManagementView.ID);
			layout.getViewLayout(GroupManagementView.ID).setCloseable(false);
			
			folder.addView(LinkedDocumentsView.ID);
			layout.getViewLayout(LinkedDocumentsView.ID).setCloseable(false);
			
			folder.addView("bpm.es.parameters.ui.views.ViewParameterProvider"); //$NON-NLS-1$
			layout.getViewLayout("bpm.es.parameters.ui.views.ViewParameterProvider").setCloseable(false); //$NON-NLS-1$
			
			folder.addView(SecurityManager.ID);
			layout.getViewLayout(SecurityManager.ID).setCloseable(false);
			
			folder.addView(ViewValidation.ID);
			layout.getViewLayout(ViewValidation.ID).setCloseable(false);
			
			
//			layout.addStandaloneView(ViewProperties.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
//			layout.addView(ViewProperties.ID, IPageLayout.LEFT, 0.50f, editorArea);
//			layout.getViewLayout(ViewProperties.ID).setCloseable(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
