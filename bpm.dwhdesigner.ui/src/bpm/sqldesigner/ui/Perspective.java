package bpm.sqldesigner.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.sqldesigner.ui.view.RequestsView;
import bpm.sqldesigner.ui.view.TreeView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);

//		layout.addView(TreeView.ID, IPageLayout.RIGHT, 0.2f, editorArea);
		
		IFolderLayout folder = layout.createFolder("ssss", IPageLayout.LEFT, 0.8f, editorArea); //$NON-NLS-1$
		folder.addView(TreeView.ID);
		folder.addView(IPageLayout.ID_OUTLINE);
		
		layout.addStandaloneView(RequestsView.ID, false, IPageLayout.BOTTOM,0.2f, editorArea);
		
		
	}

}
