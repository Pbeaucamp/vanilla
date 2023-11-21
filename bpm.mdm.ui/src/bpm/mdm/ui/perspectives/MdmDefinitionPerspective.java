package bpm.mdm.ui.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.mdm.ui.views.GenericView;

public class MdmDefinitionPerspective implements IPerspectiveFactory {
	public static final String ID = "bpm.mdm.ui.perspectives.MdmDefinitionPerspective"; //$NON-NLS-1$
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.setEditorAreaVisible(false);

		layout.addStandaloneView(GenericView.ID, false, IPageLayout.LEFT, 0.70f, editorArea); //$NON-NLS-1$
		layout.getViewLayout("bpm.mdm.ui.views.GenericView").setCloseable(false); //$NON-NLS-1$


	}

}
