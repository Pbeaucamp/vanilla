package org.freeolap;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.fasd.views.XMLAView;

public class XMLAPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		//layout.setEditorAreaVisible(false);
		layout.addStandaloneView(XMLAView.ID, true, IPageLayout.LEFT, 0.5f, editorArea);
	}

}
