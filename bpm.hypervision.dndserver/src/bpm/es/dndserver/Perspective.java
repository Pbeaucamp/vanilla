package bpm.es.dndserver;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.es.dndserver.views.View;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

//		layout.addStandaloneView(View.ID,  false, IPageLayout.LEFT, 1.0f, editorArea);
		layout.addView(View.ID, IPageLayout.LEFT, 1.0f, editorArea);
	}

}
