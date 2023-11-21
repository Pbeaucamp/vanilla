package bpm.sqldesigner.ui;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import bpm.sqldesigner.ui.dialog.DialogWelcome;
import bpm.sqldesigner.ui.preferences.PreferenceConstants;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "bpm.sqldesigner.ui.perspective"; //$NON-NLS-1$

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void postStartup() {

		try {
	
			//Starting popup
			Shell shell = new Shell(Activator.getDefault().getWorkbench().getDisplay());

			if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_SHOW_MENU_AT_STARTUP)){
				DialogWelcome d = new DialogWelcome(shell);
				d.open();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public boolean preShutdown() {
		Activator.getDefault().getWorkspace().close();
		return super.preShutdown();
	}




}
