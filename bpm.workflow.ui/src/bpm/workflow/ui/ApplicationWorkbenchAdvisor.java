package bpm.workflow.ui;

import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.xml.sax.SAXException;

import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.ListServerDigester;
import bpm.workflow.ui.perspectives.Perspective;
import bpm.workflow.ui.preferences.PreferencesConstants;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return Perspective.PERSPECTIVE_ID;
	}

	@Override
	public void postStartup() {

		String resourcesFileName = "resources/resources.xml"; //$NON-NLS-1$
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.RESOURCES_FILE_NAME, resourcesFileName);
		try {
			ListServerDigester.getListServer(ListServer.class.getClassLoader(), resourcesFileName);

		} catch(IOException e) {
			e.printStackTrace();
		} catch(SAXException e) {
			e.printStackTrace();
		}
		super.postStartup();
	}

	@Override
	public boolean preShutdown() {
		String resourcesFileName = Activator.getDefault().getPreferenceStore().getString(PreferencesConstants.RESOURCES_FILE_NAME);

		try {
			/*
			 * Write the servers in the resources file
			 */

			ListServer.getInstance().saveXml(new FileOutputStream(resourcesFileName));

		} catch(Exception e) {
			e.printStackTrace();
		}
		return super.preShutdown();
	}

}
