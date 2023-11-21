package bpm.fd.design.ui.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class FdNature implements IProjectNature {

	public static final String ID = "bpm.fd.design.ui.nature.freedashboard"; //$NON-NLS-1$
	
	private IProject project;
	
	public void configure() throws CoreException {
		
//		System.out.println();
	}

	public void deconfigure() throws CoreException {
		
//		System.out.println();
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;

	}

}
