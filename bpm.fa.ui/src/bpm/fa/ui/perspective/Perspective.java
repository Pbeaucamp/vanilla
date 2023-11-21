package bpm.fa.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.fa.ui.views.CubeExplorationView;

public class Perspective implements IPerspectiveFactory {

	public static final String ID = "bpm.fa.ui.perspective.Perspective";
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView(CubeExplorationView.ID, false, IPageLayout.LEFT, 1.0f, layout.getEditorArea());
		

	}
}
