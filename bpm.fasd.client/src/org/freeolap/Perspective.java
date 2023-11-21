package org.freeolap;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.fasd.views.CubeView;
import org.fasd.views.DetailView;
import org.fasd.views.DimensionView;
import org.fasd.views.MeasureView;
import org.fasd.views.SQLView;


public class Perspective implements IPerspectiveFactory {
	
	public static final String ID = "org.freeolap.perspective"; //$NON-NLS-1$

	//private IFolderLayout dashfolder;
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
//		dashfolder = layout.createFolder("SQL View", IPageLayout.LEFT, 0.2f, editorArea);
//		dashfolder.addView(SQLView.ID);
//		dashfolder.addPlaceholder("*");
		
		layout.addView(SQLView.ID, IPageLayout.LEFT, 0.22f, editorArea);
		layout.addView(DetailView.ID, IPageLayout.BOTTOM, 0.46f, editorArea);
		layout.addView(DimensionView.ID, IPageLayout.LEFT, 0.4f, editorArea);
		layout.addView(MeasureView.ID, IPageLayout.LEFT, 0.5f, editorArea);
		layout.addView(CubeView.ID, IPageLayout.RIGHT, 0.5f, editorArea);
		
//		dashfolder = layout.createFolder("Table", IPageLayout.TOP, 0.6f, editorArea);
//		dashfolder.addPlaceholder(DimensionView.ID + ":*");
//		dashfolder.addView(DimensionView.ID);
		
		layout.getViewLayout(SQLView.ID).setCloseable(false);
		layout.getViewLayout(DimensionView.ID).setCloseable(false);
		layout.getViewLayout(CubeView.ID).setCloseable(false);
		layout.getViewLayout(DetailView.ID).setCloseable(false);
		layout.getViewLayout(MeasureView.ID).setCloseable(false);
		
		
//		layout.addPerspectiveShortcut("org.freeolap.securityperspective");
		
		
		//TODO uncomment for security
//		layout.addStandaloneView(RoleView.ID, true, IPageLayout.RIGHT, 0.50f, DetailView.ID);
//		layout.getViewLayout(RoleView.ID).setCloseable(false);
	}
}

