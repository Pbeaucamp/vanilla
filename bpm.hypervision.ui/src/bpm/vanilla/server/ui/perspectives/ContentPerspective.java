package bpm.vanilla.server.ui.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.ui.views.VisualConstants;

public class ContentPerspective implements IPerspectiveFactory {

	public static final String ID = VisualConstants.CONTENT_PERSPECTIVE_ID;

	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(VisualConstants.CONTENT_VIEW_ID, false, IPageLayout.LEFT, 0.3f, editor);
		layout.addStandaloneView(VisualConstants.ITEM_HISTO_ID, false, IPageLayout.RIGHT, 0.7f, editor);
	}

}
