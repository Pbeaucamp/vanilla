package bpm.vanilla.server.client.ui.clustering.menu.stress;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class ProfilingPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.stress.ProfilingPerspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(StressView.ID, false, IPageLayout.LEFT, 1.0f, editor);
		layout.getViewLayout(StressView.ID).setCloseable(false);


	}

}
