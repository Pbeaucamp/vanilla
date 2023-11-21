package metadata.client;

import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogWelcome;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "metadataclient.perspective"; //$NON-NLS-1$

	private ApplicationWorkbenchWindowAdvisor advisor;
	
    @Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
    	advisor = new ApplicationWorkbenchWindowAdvisor(configurer);
        return advisor;
    }

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	@Override
	public void postStartup() {
		super.postStartup();
	}
}
