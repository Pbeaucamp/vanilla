package bpm.es.recycled.ui.perspectives;


import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.content.views.ViewTree;
import bpm.es.recycled.ui.views.RecycledView;

public class RecycledPerspective implements IPerspectiveFactory {
		
	public void createInitialLayout(IPageLayout layout){
		try {
			String editorArea = layout.getEditorArea();
			
			layout.setEditorAreaVisible(false);

//			layout.addStandaloneView(ViewTree.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
			layout.addView(ViewTree.ID, IPageLayout.LEFT, 0.50f, editorArea);
			layout.getViewLayout(ViewTree.ID).setCloseable(false);

//			layout.addStandaloneView(RecycledView.ID, true, IPageLayout.LEFT, 0.50f, editorArea);
			layout.addView(RecycledView.ID, IPageLayout.RIGHT, 0.50f, editorArea);
			layout.getViewLayout(RecycledView.ID).setCloseable(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
