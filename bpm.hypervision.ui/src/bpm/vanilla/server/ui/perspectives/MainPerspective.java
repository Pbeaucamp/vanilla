package bpm.vanilla.server.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.ui.views.VisualConstants;

public class MainPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.vanilla.server.ui.perspectives.MainPerspective"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(VisualConstants.SERVER_INFO_VIEW_ID, false, IPageLayout.LEFT, 0.3f, editor);

		IFolderLayout folder = layout.createFolder("folder", IPageLayout.RIGHT, 0.7f, editor); //$NON-NLS-1$
		folder.addView(VisualConstants.SERVER_STATE_VIEW_ID);
		layout.getViewLayout(VisualConstants.SERVER_STATE_VIEW_ID).setCloseable(false);

		folder.addView(VisualConstants.SERVER_PREVIOUS_STATE_ID);
		layout.getViewLayout(VisualConstants.SERVER_PREVIOUS_STATE_ID).setCloseable(false);
	}

}
