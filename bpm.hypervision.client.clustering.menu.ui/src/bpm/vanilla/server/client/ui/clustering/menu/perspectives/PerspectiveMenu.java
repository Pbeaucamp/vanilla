package bpm.vanilla.server.client.ui.clustering.menu.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.client.ui.clustering.menu.views.VanillaView;

public class PerspectiveMenu implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(VanillaView.ID, false, IPageLayout.LEFT, 1.0f, editor);
		layout.getViewLayout(VanillaView.ID).setCloseable(false);
		
	
		
	}

}
