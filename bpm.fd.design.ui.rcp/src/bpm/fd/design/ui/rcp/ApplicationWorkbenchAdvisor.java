package bpm.fd.design.ui.rcp;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.perspectives.DesignPerspective;



public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

     public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return DesignPerspective.ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		
		configurer.declareImage(IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEM_CATEGORY,Activator.getDefault().getImageRegistry().getDescriptor(Icons.error_16),	true );
		configurer.declareImage(IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH,Activator.getDefault().getImageRegistry().getDescriptor(Icons.error_16),true);
		configurer.declareImage(IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH,Activator.getDefault().getImageRegistry().getDescriptor(Icons.error_16),true );
		configurer.declareImage(IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH,Activator.getDefault().getImageRegistry().getDescriptor(Icons.error_16),true ); 
	}

	@Override
	public boolean preShutdown() {
		
		return super.preShutdown();
	}

	
	
	@Override
	public boolean preWindowShellClose(IWorkbenchWindowConfigurer configurer) {

		return super.preWindowShellClose(configurer);
	}

}
