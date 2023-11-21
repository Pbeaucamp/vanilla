package bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.views.ViewCacheDisk;

public class CachePerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives.CachePerspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(ViewCacheDisk.ID, false, IPageLayout.LEFT, 1.0f, editor);
		layout.getViewLayout(ViewCacheDisk.ID).setCloseable(false);
		
	}

}
