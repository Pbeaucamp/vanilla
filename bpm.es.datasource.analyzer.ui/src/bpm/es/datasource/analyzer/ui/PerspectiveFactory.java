package bpm.es.datasource.analyzer.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.birep.admin.client.views.ViewProperties;
import bpm.es.datasource.analyzer.ui.views.AnalysisView;
import bpm.es.datasource.analyzer.ui.views.ViewFilter;
import bpm.es.datasource.analyzer.ui.views.ViewHierarchical;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView(ViewFilter.ID, true, IPageLayout.LEFT, 0.35f, editorArea);
		layout.addView(ViewFilter.ID, IPageLayout.LEFT, 0.35f, editorArea);
		layout.getViewLayout(ViewFilter.ID).setCloseable(false);
		
		IFolderLayout folder = layout.createFolder("folder", IPageLayout.RIGHT, 0.20f, ViewFilter.ID); //$NON-NLS-1$
		folder.addView(AnalysisView.ID);
		folder.addView(ViewHierarchical.ID);
		layout.getViewLayout(AnalysisView.ID).setCloseable(false);

//		layout.addStandaloneView(ViewProperties.ID, true, IPageLayout.RIGHT, 0.70f, AnalysisView.ID);
		layout.addView(ViewProperties.ID, IPageLayout.RIGHT, 0.70f, AnalysisView.ID);
		layout.getViewLayout(ViewProperties.ID).setCloseable(false);

	}

}
