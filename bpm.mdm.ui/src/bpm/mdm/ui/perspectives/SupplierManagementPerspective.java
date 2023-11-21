package bpm.mdm.ui.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.mdm.ui.views.SupplierDetailView;
import bpm.mdm.ui.views.SupplierManagementView;

public class SupplierManagementPerspective implements IPerspectiveFactory {
	public static final String ID = "bpm.mdm.ui.perspectives.SupplierManagementPerspective"; //$NON-NLS-1$
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(SupplierManagementView.ID, false, IPageLayout.LEFT, 0.5f, editorArea); //$NON-NLS-1$
		layout.getViewLayout(SupplierManagementView.ID).setCloseable(false); //$NON-NLS-1$
		
		layout.addStandaloneView(SupplierDetailView.ID, false, IPageLayout.RIGHT, 0.5f, editorArea); //$NON-NLS-1$
		layout.getViewLayout(SupplierDetailView.ID).setCloseable(false); //$NON-NLS-1$
	}

}
