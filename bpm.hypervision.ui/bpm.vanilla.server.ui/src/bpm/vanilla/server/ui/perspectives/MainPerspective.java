package bpm.vanilla.server.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.internal.WorkbenchWindow;

import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.views.VisualConstants;

public class MainPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.vanilla.server.ui.perspectives.MainPerspective";
	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addView(VisualConstants.SERVER_INFO_VIEW_ID, IPageLayout.LEFT, 0.3f, editor);
//		layout.addView(VisualConstants.SERVER_WAITING_VIEW_ID, IPageLayout.RIGHT, 0.7f, editor);
//		layout.addView(VisualConstants.SERVER_RUNNING_VIEW_ID, IPageLayout.BOTTOM, 0.7f, VisualConstants.SERVER_WAITING_VIEW_ID);
//		layout.addView(VisualConstants.SERVER_STATE_VIEW_ID, IPageLayout.RIGHT, 0.7f, editor);
//		layout.addPlaceholder(VisualConstants.SERVER_STATE_VIEW_ID, IPageLayout.RIGHT, 0.7f, editor);
//		layout.addFastView(VisualConstants.SERVER_STATE_VIEW_ID);
		IFolderLayout folder = layout.createFolder(VisualConstants.FOLDER, IPageLayout.RIGHT, 0.7f, editor);
//		folder.addPlaceholder(VisualConstants.SERVER_STATE_VIEW_ID);
		folder.addView(VisualConstants.SERVER_STATE_VIEW_ID);
	
		
	}

}
