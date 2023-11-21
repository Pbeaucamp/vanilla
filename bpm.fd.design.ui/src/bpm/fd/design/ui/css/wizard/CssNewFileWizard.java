package bpm.fd.design.ui.css.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.project.views.ProjectView;

public class CssNewFileWizard  extends Wizard implements IImportWizard {
	
	private NewCssPage page;

	public CssNewFileWizard() throws Exception{
		super();
		if (Activator.getDefault().getProject() == null){
			throw new Exception(Messages.CssNewFileWizard_0);
		}
	}

	@Override
	public boolean performFinish() {
		String rName = page.getCssResourceName();
		IResource r = page.getImageResource();
		
		try{
			IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
			IWorkspaceRoot root = workspace.getRoot();
			IProject p = root.getProject(Activator.getDefault().getProject().getProjectDescriptor().getProjectName());
			;
			
			IFile f = p.getFile(rName);
			

			try{
				
				File physicalFile = new File(p.getLocation().toOSString() + "/" + rName); //$NON-NLS-1$
				physicalFile.createNewFile();
				
				PrintWriter pw = new PrintWriter(physicalFile, "UTF-8"); //$NON-NLS-1$
				pw.write("/* the cell class is used by all cell presents on the dashboard */");
				pw.write(".cell{overflow:auto;}\n");
				/*
				 * write the folder css
				 */
				pw.write("/* the menu class is the default class for Folders */");
				pw.write(".menu { list-style-type: none; } \n");
				pw.write("ul.menu {font: bold 11px verdana, arial, sans-serif;list-style-type: none;padding-bottom: 24px;border-bottom: 1px solid #6c6;margin: 0;}\n");
				pw.write("ul.menu li {float: left;height: 21px;background-color: #cfc;margin: 2px 2px 0 2px;border: 1px solid #6c6;}\n");
				pw.write(".menu li:active {border-bottom: 1px solid #fff;background-color: #fff;} \n");
				pw.write(".menu a:active{color: #000;}\n");
				pw.write(".active a{color: #000;background: #fff;}\n");
				pw.write(".active li{border-bottom: 1px solid #fff;background-color: #fff;background: #fff;}\n");
				pw.write(".menu a {float: left;display: block;color: #666;text-decoration: none;padding: 4px;}\n");
				pw.write(".menu a:hover {background: #fff;}\n\n");
				
				if (r != null){
					
					
					pw.write("body{\n"); //$NON-NLS-1$
					pw.write("\tbackground-image:url('" + r.getName() + "');\n"); //$NON-NLS-1$ //$NON-NLS-2$
					pw.write("}\n"); //$NON-NLS-1$
					
				}
				pw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			f.createLink(Platform.getLocation().append(f.getFullPath()), org.eclipse.core.resources.IResource.REPLACE, null);
			
			
			
			
			
			FileCSS cssF = new FileCSS(rName, f.getLocation().toFile());
			Activator.getDefault().getProject().addResource(cssF);
			
			if (r != null){
				f = p.getFile(r.getFile().getName());
				if (!f.exists()){
					f.create(new FileInputStream(r.getFile()), true, null);
				}
				
			}
			Activator.getDefault().getProject().addResource(r);
			
			try{
				ProjectView v = (ProjectView)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ProjectView.ID);
				v.setContent(Activator.getDefault().getProject());
			}catch(Exception ex){
				
			}
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}

	@Override
	public void addPages() {
		page = new NewCssPage("bpm.fd.design.ui.css.wizard.newcssresource"); //$NON-NLS-1$
		page.setTitle(Messages.CssNewFileWizard_8);
		addPage(page);
	}

}
