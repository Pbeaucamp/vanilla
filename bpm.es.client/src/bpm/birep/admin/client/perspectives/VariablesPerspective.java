package bpm.birep.admin.client.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.ViewProperties;
import bpm.birep.admin.client.views.ViewVariable;

public class VariablesPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewVariable.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
		layout.addView(ViewVariable.ID, IPageLayout.LEFT, 0.50f, editorArea);
		layout.getViewLayout(ViewVariable.ID).setCloseable(false);
		

//		layout.addStandaloneView(ViewProperties.ID, true, IPageLayout.RIGHT, 0.50f, editorArea);
		layout.addView(ViewProperties.ID, IPageLayout.RIGHT, 0.50f, editorArea);
		layout.getViewLayout(ViewProperties.ID).setCloseable(false);

	}

}
