package bpm.vanilla.server.client.ui.clustering.menu.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.views.fmdt.FmdtLogsView;

public class PerspectiveFmdtLogs implements IPerspectiveFactory{
	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.perspectives.PerspectiveFmdtLogs"; //$NON-NLS-1$
	public void createInitialLayout(IPageLayout layout) {
		String editor = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		layout.addStandaloneView(FmdtLogsView.ID, false, IPageLayout.LEFT, 1.0f, editor);
		layout.getViewLayout(FmdtLogsView.ID).setCloseable(false);
		
	
		
	}
}
