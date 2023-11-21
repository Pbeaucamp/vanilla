package bpm.fd.design.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class ProjectNature1 implements IProjectNature {

	private IProject p;
	public void configure() throws CoreException {
		

	}

	public void deconfigure() throws CoreException {
		

	}

	public IProject getProject() {
		
		return p;
	}

	public void setProject(IProject project) {
		p = project;

	}

}
