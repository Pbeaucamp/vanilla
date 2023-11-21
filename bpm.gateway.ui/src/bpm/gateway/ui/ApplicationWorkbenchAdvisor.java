package bpm.gateway.ui;


import java.io.FileOutputStream;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.ui.perspectives.Perspective;
import bpm.gateway.ui.preferences.PreferencesConstants;

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
			ResourceManager.loadFromFile(resourcesFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.postStartup();
	}

	@Override
	public boolean preShutdown() {
		String resourcesFileName = Activator.getDefault().getPreferenceStore().getString(PreferencesConstants.RESOURCES_FILE_NAME);
		
		
		try {
			ResourceManager.saveXmlDocument(new FileOutputStream(resourcesFileName));	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.preShutdown();
	}

	
}


