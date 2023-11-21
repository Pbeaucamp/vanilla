package bpm.fd.design.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.fd.design.ui.datas.ViewDictionary;
import bpm.fd.design.ui.editor.part.palette.ViewPalette;
import bpm.fd.design.ui.project.views.ProjectView;

public class DesignPerspective implements IPerspectiveFactory {
	public static final String ID = "bpm.fd.design.ui.perspectives.DesignPerspective"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();

		IFolderLayout folder1 = layout.createFolder("bpm.fd.design.ui.perspectives.DesignPerspective.folder1", IPageLayout.LEFT, 0.2f, editor); //$NON-NLS-1$
		folder1.addView(ViewPalette.ID);
		folder1.addView(ViewDictionary.ID);
		folder1.addView(ProjectView.ID);
		
		layout.getViewLayout(ProjectView.ID).setCloseable(false);
		layout.getViewLayout(ViewDictionary.ID).setCloseable(false);
		layout.getViewLayout(ViewPalette.ID).setCloseable(false);

		layout.addStandaloneView("bpm.fd.design.ui.properties.views.PropertiesViewPart", false, IPageLayout.RIGHT, 0.7f, editor);
	}
}
