package bpm.es.pack.manager.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.ViewProperties;
import bpm.es.pack.manager.views.ViewHistoricImport;
import bpm.es.pack.manager.views.ViewPackage;

public class DeploymentsPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.es.pack.manager.perspective.DeploymentsPerspective"; //$NON-NLS-1$
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewPackage.ID, true, IPageLayout.LEFT, 0.70f, editorArea);
		layout.addView(ViewPackage.ID, IPageLayout.LEFT, 0.70f, editorArea);
		layout.getViewLayout(ViewPackage.ID).setCloseable(false);

//		layout.addStandaloneView(ViewHistoricImport.ID, true, IPageLayout.RIGHT, 0.30f, editorArea);
		layout.addView(ViewHistoricImport.ID, IPageLayout.RIGHT, 0.30f, editorArea);
		layout.getViewLayout(ViewHistoricImport.ID).setCloseable(false);

//		layout.addStandaloneView(ViewProperties.ID, true, IPageLayout.BOTTOM, 0.50f, ViewPackage.ID);
		layout.addView(ViewProperties.ID, IPageLayout.BOTTOM, 0.50f, ViewPackage.ID);
		layout.getViewLayout(ViewProperties.ID).setCloseable(false);
		
		


	}

}
