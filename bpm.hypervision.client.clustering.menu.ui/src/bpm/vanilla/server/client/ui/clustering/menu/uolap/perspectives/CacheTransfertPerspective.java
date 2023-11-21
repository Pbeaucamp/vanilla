package bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.client.ui.clustering.menu.uolap.views.ViewTransfertCache;

public class CacheTransfertPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives.CacheTransfertPerspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(ViewTransfertCache.ID, false, IPageLayout.LEFT, 1.0f, editor);
		layout.getViewLayout(ViewTransfertCache.ID).setCloseable(false);
		
	}

}
