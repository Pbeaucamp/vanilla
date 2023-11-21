package bpm.profiling.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.profiling.ui.views.ViewAnalysis;
import bpm.profiling.ui.views.ViewConnections;
import bpm.profiling.ui.views.ViewDesign;
import bpm.profiling.ui.views.ViewResult;

public class DefaultPerspective implements IPerspectiveFactory {

	public static final String ID = "bpm.profiling.ui.perspectives.default";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		IFolderLayout folder = layout.createFolder("leftFolder", IPageLayout.LEFT, 0.80f, editorArea);
//		folder.addPlaceholder(ViewConnections.ID);
//		folder.addPlaceholder(ViewAnalysis.ID);
		folder.addView(ViewAnalysis.ID);
		folder.addView(ViewConnections.ID);
		
//		layout.addView(ViewConnections.ID, IPageLayout.LEFT, 0.3f, editorArea);
//		layout.addView(ViewAnalysis.ID, IPageLayout.LEFT, 0.3f, ViewConnections.ID);
		
		
		layout.getViewLayout(ViewConnections.ID).setCloseable(false);
		layout.getViewLayout(ViewAnalysis.ID).setCloseable(false);
		
		IFolderLayout folder2 = layout.createFolder("rightFolder", IPageLayout.RIGHT, 0.20f, ViewConnections.ID);
//		folder2.addPlaceholder(ViewDesign.ID);
//		folder2.addPlaceholder(ViewResult.ID);
		folder2.addView(ViewDesign.ID);
		folder2.addView(ViewResult.ID);
		
		
		layout.getViewLayout(ViewDesign.ID).setCloseable(false);
		layout.getViewLayout(ViewResult.ID).setCloseable(false);
		

	}

}
