package bpm.fd.design.ui.rcp.action;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.rcp.Messages;

public class ActionSaveAs extends Action{

	public ActionSaveAs(){
		super("Save As..."); //$NON-NLS-1$
		setText(Messages.ActionSaveAs_1);
	}
	
	public void run(){
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
	
		if (Activator.getDefault().getProject() == null){
			MessageDialog.openInformation(sh, Messages.ActionSaveAs_2, Messages.ActionSaveAs_3);
			return;
		}
//		DialogSaveAs d = new DialogSaveAs(sh, Activator.getDefault().getProject());
//		
//		if (d.open() == DialogSaveAs.OK){
//			MessageDialog.openInformation(sh, "Save As ...", "Files saved");
//		}
		IProject project = Activator.getDefault().getResourceProject();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
		IWorkspaceRoot r = workspace.getRoot();
		InputDialog d = new InputDialog(sh, Messages.ActionSaveAs_4, Messages.ActionSaveAs_5, "newProject", null); //$NON-NLS-3$
		
		if (d.open() != InputDialog.OK){
			return;
		}
		String projectName =d.getValue();
		
		IProject p = r.getProject(projectName);
		
		if (p.exists()){
			if (MessageDialog.openQuestion(sh, Messages.ActionSaveAs_7, Messages.ActionSaveAs_8)){
				try {
					p.delete(true, null);
				} catch (CoreException e) {
					
					e.printStackTrace();
				}
			}
			
			
		}
		try{
			p.create(null);
			p.open(null);
			
			for(IResource res : project.members()){
				IFile f = p.getFile(res.getName());
				if (f.getFileExtension().equals("project")){ //$NON-NLS-1$
					continue;
				}
				if (((IFile)res).getFileExtension().contains("freedashboard")){ //$NON-NLS-1$
					String s = IOUtils.toString(((IFile)res).getContents(), "UTF-8"); //$NON-NLS-1$
					s = s.replace("<projectName>" +  project.getName() +  "</projectName>", "<projectName>" +  p.getName() +  "</projectName>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					f.create(IOUtils.toInputStream(s), true, null);
					
				}
				else{
					f.create(((IFile)res).getContents(), true, null);
				}
				
				
			}
			
			new ActionOpenFdProject(p).run();
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
}
