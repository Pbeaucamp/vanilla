package bpm.es.gedmanager.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.es.gedmanager.views.GeneralView;

public class GedManager implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(GeneralView.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.addView(GeneralView.ID, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(GeneralView.ID).setCloseable(false);
	}

	
}
