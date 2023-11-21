package bpm.norparena.mapmanager.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.norparena.mapmanager.views.MapView;

public class MapManagerPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(MapView.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(MapView.ID).setCloseable(false);
	}

}
