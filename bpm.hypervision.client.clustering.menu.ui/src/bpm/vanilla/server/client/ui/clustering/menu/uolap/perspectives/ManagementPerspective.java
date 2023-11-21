package bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.views.ManagementView;

public class ManagementPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives.ManagementPerspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(ManagementView.class.getName(), false, IPageLayout.LEFT, 1.0f, editor);
		layout.getViewLayout(ManagementView.class.getName()).setCloseable(false);
		
	}

}
