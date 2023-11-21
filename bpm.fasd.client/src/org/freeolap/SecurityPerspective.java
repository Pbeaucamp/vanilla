package org.freeolap;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.fasd.views.DetailView;
import org.fasd.views.SecurityProviderView;
import org.fasd.views.ServerView;


public class SecurityPerspective implements IPerspectiveFactory {
	public static final String ID = "org.freeolap.securityperspective"; //$NON-NLS-1$
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		//layout.addStandaloneView(SecurityView.ID, true, IPageLayout.LEFT, 0.3f, editorArea);
		layout.addStandaloneView(DetailView.ID, true, IPageLayout.BOTTOM, 0.52f, editorArea);
		layout.addStandaloneView(ServerView.ID, true, IPageLayout.LEFT, 0.4f, editorArea);
		layout.addStandaloneView(SecurityProviderView.ID, true, IPageLayout.LEFT, 0.4f, editorArea);
		
		
		//layout.getViewLayout(SecurityView.ID).setCloseable(false);
		//layout.getViewLayout(SecurityView.ID).set
	}

}
