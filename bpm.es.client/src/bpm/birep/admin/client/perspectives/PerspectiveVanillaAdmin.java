package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.ViewVanillaAdmin;

public class PerspectiveVanillaAdmin implements IPerspectiveFactory {
	public static final String ID = "adminBIRep.perspective.setup"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewVanillaAdmin.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.addView(ViewVanillaAdmin.ID, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(ViewVanillaAdmin.ID).setCloseable(false);
	}
}
