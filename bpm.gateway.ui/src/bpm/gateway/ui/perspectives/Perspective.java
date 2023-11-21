package bpm.gateway.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.views.ModelViewPart;
import bpm.gateway.ui.views.ResourceViewPart;
import bpm.gateway.ui.views.RuntimeConsoleViewPart;
import bpm.gateway.ui.views.ViewPalette;

public class Perspective implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = "bpm.gateway.ui.perspective"; //$NON-NLS-1$
	private static final String FOLDER_ID = "bpm.gateway.ui.perspective.folder"; //$NON-NLS-1$
	
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		layout.addStandaloneView(ViewPalette.ID, false, IPageLayout.LEFT, 0.125f, editorArea);
		
		IFolderLayout folder = layout.createFolder("ssss", IPageLayout.BOTTOM, 0.7f, editorArea); //$NON-NLS-1$
		folder.addPlaceholder(RuntimeConsoleViewPart.ID);
		
		folder.addView(IPageLayout.ID_PROP_SHEET);//layout.addStandaloneView(IPageLayout.ID_PROP_SHEET, true, IPageLayout.BOTTOM, 0.8f, editorArea);
//		folder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		IFolderLayout folderLayout = layout.createFolder(FOLDER_ID, IPageLayout.RIGHT, 0.8f, editorArea);
		folderLayout.addView(ResourceViewPart.ID);
//		folderLayout.addView(DesignViewPart.ID);
		folderLayout.addView(IPageLayout.ID_OUTLINE);
		folderLayout.addView(ModelViewPart.ID);
		layout.getViewLayout(IPageLayout.ID_PROP_SHEET).setCloseable(false);
		layout.getViewLayout(ResourceViewPart.ID).setCloseable(false);
		layout.getViewLayout(ResourceViewPart.ID).setMoveable(false);
		
//		layout.addStandaloneView(IPageLayout.ID_OUTLINE, true, IPageLayout.LEFT, 0.3f, editorArea);

		
//		layout.getViewLayout(DesignViewPart.ID).setCloseable(false);
//		layout.getViewLayout(DesignViewPart.ID).setMoveable(false);
		
		layout.getViewLayout(ModelViewPart.ID).setCloseable(false);
		layout.getViewLayout(ModelViewPart.ID).setMoveable(false);
		
		layout.getViewLayout(ViewPalette.ID).setCloseable(false);
		
		
		
	
	}
}
