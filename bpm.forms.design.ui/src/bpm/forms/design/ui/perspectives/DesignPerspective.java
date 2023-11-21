package bpm.forms.design.ui.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.forms.design.ui.views.ContentView;

public class DesignPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorId = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView(ContentView.ID, true, IPageLayout.LEFT, 1.0f, editorId);

	}

}
