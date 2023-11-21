package bpm.es.pack.manager.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.es.pack.manager.views.ViewWorkplace;

public class WorkplacePerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.es.pack.manager.perspective.WorkplacePerspective"; //$NON-NLS-1$
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewWorkplace.ID, true, IPageLayout.LEFT, 0.70f, editorArea);
		layout.addView(ViewWorkplace.ID, IPageLayout.LEFT, 0.70f, editorArea);
		layout.getViewLayout(ViewWorkplace.ID).setCloseable(false);
	}
}
