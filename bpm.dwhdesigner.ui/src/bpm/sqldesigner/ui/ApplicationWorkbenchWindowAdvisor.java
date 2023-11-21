package bpm.sqldesigner.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.internal.Workspace;
import bpm.sqldesigner.ui.preferences.PreferenceConstants;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(true);
		configurer.setShowMenuBar(true);
		configurer.setShowStatusLine(false);
		configurer.setTitle(Messages.ApplicationWorkbenchWindowAdvisor_0);
	}

	@Override
	public void postWindowClose() {

	}

	@Override
	public void postWindowOpen() {
		 Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()
		 .getShell().setMaximized(true);
		 Activator .getDefault().getPreferenceStore().setValue(PreferenceConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().toString().substring(6));
		 
		 // first init to make sure the Workspace will be able to
		 // create the IPartLIstener
		 Activator.getDefault().setWorkspace(new Workspace());
		 
	}

}
