/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package bpm.fd.design.ui.css.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.project.views.ProjectView;

public class PictureImportWizard extends Wizard implements IImportWizard {
	
	CssImportWizardPage mainPage;

	public PictureImportWizard() throws Exception{
		super();
		if (Activator.getDefault().getProject() == null){
			throw new Exception(Messages.PictureImportWizard_0);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		try{
			IFile file = mainPage.createNewFile();
			
			FileImage cssF = new FileImage(file.getName(), file.getLocation().toFile());
			Activator.getDefault().getProject().addResource(cssF);
			
			try{
				ProjectView v = (ProjectView)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ProjectView.ID);
				v.setContent(Activator.getDefault().getProject());
			}catch(Exception ex){
				
			}
			
			
			return true;
		}catch(Exception e){
			
		}
		
        return false;
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.PictureImportWizard_1); //NON-NLS-1
		setNeedsProgressMonitor(true);
			}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
    	mainPage = new CssImportWizardPage(Messages.PictureImportWizard_2, FileImage.extensions); //NON-NLS-1
    	mainPage.setDescription(Messages.PictureImportWizard_3);
        addPage(mainPage);        
    }

}
