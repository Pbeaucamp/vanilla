package bpm.norparena.ui.menu.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory1 implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView("bpm.norparena.ui.menu.views.WelcomeView", true, IPageLayout.TOP, 1.0f, editor); //$NON-NLS-1$

		layout.getViewLayout("bpm.norparena.ui.menu.views.WelcomeView").setCloseable(false); //$NON-NLS-1$
	}

}
