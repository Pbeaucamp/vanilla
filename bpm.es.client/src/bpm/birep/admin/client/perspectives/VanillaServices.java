package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class VanillaServices implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView("bpm.es.clustering.ui.view1", true, IPageLayout.LEFT, 0.70f, editorArea); //$NON-NLS-1$
		layout.addView("bpm.es.clustering.ui.view1", IPageLayout.LEFT, 0.70f, editorArea); //$NON-NLS-1$
		layout.getViewLayout("bpm.es.clustering.ui.view1").setCloseable(false); //$NON-NLS-1$
	}

}
