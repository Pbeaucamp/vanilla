package bpm.es.ui.menu;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory1 implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView("bpm.es.ui.menu.views.WelcomeView", true, IPageLayout.TOP, 1.0f, editor); //$NON-NLS-1$
//		layout.addView("bpm.es.ui.menu.views.WelcomeView", IPageLayout.TOP, 1.0f, editor); //$NON-NLS-1$
		layout.getViewLayout("bpm.es.ui.menu.views.WelcomeView").setCloseable(false); //$NON-NLS-1$
	}

}
