package bpm.metadata.client.security.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SecurityPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.metadata.client.security.SecurityPerspective";

	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView("bpm.metadata.client.security.SecurityView", false, IPageLayout.LEFT, 1.0f, editor);

	}

}
