package bpm.oda.driver.reader;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.oda.driver.reader.impl.ui.OdaDatasExplorer;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
	//View : Data explorer
		layout.addStandaloneView(OdaDatasExplorer.ID,  false, IPageLayout.LEFT, 0.3f, editorArea);
	
	}

}
