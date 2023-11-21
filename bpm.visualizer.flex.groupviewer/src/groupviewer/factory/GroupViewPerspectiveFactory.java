package groupviewer.factory;

import groupviewer.views.GroupViewerView;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
/**
 * This is use to Create the perspective view. 
 * it contains 2 views :
 * GroupViewerView : the main data representation.
 * StructureView : the tree representation of the data.
 * 
 * @author admin
 *
 */
public class GroupViewPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();		
		layout.setEditorAreaVisible(false);		
		layout.addStandaloneView(GroupViewerView.ID, true, IPageLayout.LEFT, 1, editorArea);
		//layout.addStandaloneView(StructureView.ID, true, IPageLayout.RIGHT, 0.15f, editorArea);
	}

}
