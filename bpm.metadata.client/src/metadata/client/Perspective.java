package metadata.client;

import metadata.client.views.ViewDimensionMeasure;
import metadata.client.views.ViewProperties;
import metadata.client.views.ViewTree;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	public static final String ID = "metadataclient.perspective"; //$NON-NLS-1$
	public void createInitialLayout(IPageLayout layout) {
		String editoArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);
		
		IFolderLayout folder = layout.createFolder("folder", IPageLayout.LEFT, 0.5f, editoArea); //$NON-NLS-1$
		folder.addView(ViewTree.ID);
		layout.getViewLayout(ViewTree.ID).setCloseable(false);
		
		folder.addView(ViewDimensionMeasure.ID);
		layout.getViewLayout(ViewDimensionMeasure.ID).setCloseable(false);
		
		
//		layout.addStandaloneView(ViewProperties.ID, true, IPageLayout.RIGHT, 0.5f, ViewTree.ID);
		layout.addView(ViewProperties.ID, IPageLayout.RIGHT, 0.5f, editoArea);
		layout.getViewLayout(ViewProperties.ID).setCloseable(false);
		
	}
}
