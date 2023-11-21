package bpm.norparena.mapmanager.openlayers;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class OpenLayersMapPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(OpenLayersMapView.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(OpenLayersMapView.ID).setCloseable(false);
	}

}
