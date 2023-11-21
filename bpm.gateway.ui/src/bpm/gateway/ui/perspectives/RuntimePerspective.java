package bpm.gateway.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import bpm.gateway.ui.views.RuntimeConsoleViewPart;

public class RuntimePerspective implements IPerspectiveFactory {
	public static final String PERSPECTIVE_ID = "bpm.gateway.ui.perspectives.RuntimePerspective"; //$NON-NLS-1$
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.RIGHT, 0.6f, editorArea);
		
		IFolderLayout folder = layout.createFolder("ssss", IPageLayout.BOTTOM, 0.5f, editorArea); //$NON-NLS-1$

		folder.addView(IPageLayout.ID_PROP_SHEET);  
		folder.addView(RuntimeConsoleViewPart.ID);  

		layout.getViewLayout(IPageLayout.ID_PROP_SHEET).setCloseable(false);
		layout.getViewLayout(RuntimeConsoleViewPart.ID).setCloseable(false);
		layout.getViewLayout(IConsoleConstants.ID_CONSOLE_VIEW).setCloseable(false);

		
		

	}

}
