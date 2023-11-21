package bpm.workflow.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.workflow.ui.views.ModelViewPart;
import bpm.workflow.ui.views.ResourceViewPart;
import bpm.workflow.ui.views.ViewPalette;


/**
 * The perspective of the BiWorkflow
 * @author CHARBONNIER, MARTIN
 *
 */
public class Perspective implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = "bpm.workflow.ui.perspective"; //$NON-NLS-1$
	
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		layout.addStandaloneView(ViewPalette.ID, false, IPageLayout.LEFT, 0.125f, editorArea);
		
		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.6f, editorArea); //$NON-NLS-1$
		topRight.addView(ResourceViewPart.ID);
		topRight.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		topRight.addView(IPageLayout.ID_OUTLINE);
		layout.getViewLayout(IPageLayout.ID_OUTLINE).setCloseable(false);
		topRight.addView(ModelViewPart.ID);
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.5f, "topRight"); //$NON-NLS-1$
		layout.getViewLayout(ResourceViewPart.ID).setCloseable(false);
		layout.getViewLayout(ResourceViewPart.ID).setMoveable(false);
		

	}

}
