package bpm.norparena.mapmanager.fusionmap;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class FusionMapPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(ViewFusionMap.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(ViewFusionMap.ID).setCloseable(false);
	}

}
