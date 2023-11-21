package bpm.es.datasource.audit.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.es.datasource.audit.ui.views.TestView;

public class AuditPerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "bpm.es.datasource.audit.ui.AuditPerspectiveFactory";  //$NON-NLS-1$
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

//		layout.addStandaloneView("bpm.birep.admin.client.content.views.tree", true, IPageLayout.LEFT, 0.35f, editorArea); //$NON-NLS-1$
		layout.addView("bpm.birep.admin.client.content.views.tree", IPageLayout.LEFT, 0.35f, editorArea); //$NON-NLS-1$
		layout.getViewLayout("bpm.birep.admin.client.content.views.tree").setCloseable(false); //$NON-NLS-1$

//		layout.addStandaloneView(TestView.ID, false, IPageLayout.RIGHT, 0.70f, editorArea);
		layout.addView(TestView.ID, IPageLayout.RIGHT, 0.70f, editorArea);
		layout.getViewLayout(TestView.ID).setCloseable(false);
	}

}
