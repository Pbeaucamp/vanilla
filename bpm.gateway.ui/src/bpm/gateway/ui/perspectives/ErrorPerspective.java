package bpm.gateway.ui.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import bpm.gateway.ui.views.RuntimeErrorViewPart;

public class ErrorPerspective implements IPerspectiveFactory{
	
	public static final String PERSPECTIVE_ID = "bpm.gateway.ui.perspectives.ErrorPerspective"; //$NON-NLS-1$
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
	
		layout.addStandaloneView(RuntimeErrorViewPart.ID, true, IPageLayout.LEFT, 1f, editorArea);
		
		layout.getViewLayout(RuntimeErrorViewPart.ID).setCloseable(false);
	}

}
