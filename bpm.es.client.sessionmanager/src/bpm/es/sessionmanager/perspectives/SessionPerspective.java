package bpm.es.sessionmanager.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.es.sessionmanager.views.SessionView;

public class SessionPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.es.sessionmanager.perspectives.SessionPerspective"; //$NON-NLS-1$
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(SessionView.ID, true, IPageLayout.LEFT, 1.0f, editorArea);
		layout.addView(SessionView.ID, IPageLayout.LEFT, 1.0f, editorArea);
		layout.getViewLayout(SessionView.ID).setCloseable(false);
		
//		layout.addStandaloneView(ActiveSessions.ID, true, IPageLayout.LEFT, 1.0f, editorArea);
//		layout.getViewLayout(ActiveSessions.ID).setCloseable(false);
	}

}
