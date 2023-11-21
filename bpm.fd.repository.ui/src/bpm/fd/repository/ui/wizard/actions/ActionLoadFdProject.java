package bpm.fd.repository.ui.wizard.actions;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.design.ui.nature.FdNature;
import bpm.fd.repository.ui.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class ActionLoadFdProject {

	private class RunnableAction implements IRunnableWithProgress{
		FdProject p = null;
		Exception ex;
		public void run(IProgressMonitor monitor)throws InvocationTargetException, InterruptedException {
			monitor.setTaskName(Messages.ActionLoadFdProject_0);
			monitor.beginTask(Messages.ActionLoadFdProject_1, 2);
			
			monitor.subTask(Messages.ActionLoadFdProject_2);
			
			try{
				
				p = ModelLoader.loadProject(sock, item, Platform.getInstallLocation().getURL().getPath() + "/temp/" ); //$NON-NLS-1$
				
			}catch(Exception ex){
				ex.printStackTrace();
				this.ex = new Exception(Messages.ActionLoadFdProject_4 + ex.getMessage(), ex);
				return;
			}

			monitor.subTask(Messages.ActionLoadFdProject_5);
			try{
				createWorkspaceProject(p);
			}catch(Exception ex){
				ex.printStackTrace();
				this.ex = new Exception(Messages.ActionLoadFdProject_6 + ex.getMessage(), ex);
			}
		}
	}
	
	
	private IRepositoryApi sock;
	private RepositoryItem item;
	
	public ActionLoadFdProject(IRepositoryApi sock, RepositoryItem item){
		this.sock = sock;
		this.item = item;
	}
	
	public FdProject perform() throws Exception{
		if (sock == null){
			throw new Exception(Messages.ActionLoadFdProject_7);
		}
		
		if (item == null){
			throw new Exception(Messages.ActionLoadFdProject_8);
		}

		
		
		RunnableAction r = new RunnableAction();

		
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
	     try {
	    	 service.run(true, false, r);
	    	 
	    	 if (r.ex == null){
	    		 MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionLoadFdProject_9, Messages.ActionLoadFdProject_10); 
	    	 }
	    	 else{
	    		 r.ex.printStackTrace();
	    		 MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionLoadFdProject_11, Messages.ActionLoadFdProject_12 + r.ex.getMessage());
	    	 }
	    	 
	     } catch (Exception e) {
	       throw new Exception(Messages.ActionLoadFdProject_13, e);
	     } 
		return r.p;
		
	}
	
	private void createWorkspaceProject(FdProject project) throws Exception{
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		final IProject p = root.getProject(project.getProjectDescriptor().getProjectName());
		
		
		if (p.exists()){
			
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					boolean b = MessageDialog.openQuestion(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), 
							Messages.ActionLoadFdProject_14, Messages.ActionLoadFdProject_15 + p.getName() +  Messages.ActionLoadFdProject_16);
					
					if (!b){
						 MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionLoadFdProject_17, Messages.ActionLoadFdProject_18);
//						throw new Exception("import aborted");
					}
					else{
						try{
//							if (p.isOpen()){
								for(IEditorReference e : Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()){
									if (e.getEditorInput() instanceof FdProjectEditorInput){
										if (((FdProjectEditorInput)e.getEditorInput()).getModel().getProject().getProjectDescriptor().getProjectName().equals(p.getName())){
											Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditors(new IEditorReference[]{e}, false);
										
										}
										
									}
								}
//							}
//							p.close(null);
							p.delete(true, true, null);
						}catch(Exception ex){
							 MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionLoadFdProject_19, Messages.ActionLoadFdProject_20 + ex.getMessage());
//							throw ex;
						}
					
					}
					
				}
			});
			
		}
		
		p.create(null);
		p.open(null);
		
		
		IProjectDescription pD = p.getDescription();
		pD.setNatureIds(new String[]{FdNature.ID});
		p.setDescription(pD, null);


		IFile f = p.getFile(project.getProjectDescriptor().getModelName() + ".freedashboard"); //$NON-NLS-1$
		try {
			Document d = DocumentHelper.createDocument(project.getFdModel().getElement());
							
			XMLWriter w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + f.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
			w.write(d);
			w.close();
			
//			f.createLink(Platform.getLocation().append(f.getFullPath()), IResource.REPLACE, null);
			f.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
			
		} catch (Exception e) {
			throw new Exception (Messages.ActionLoadFdProject_23, e);
		}
		
		
		
			
		IFile dicoFile = p.getFile( project.getProjectDescriptor().getDictionaryName() + ".dictionary"); //$NON-NLS-1$
		try {
			Document d = DocumentHelper.createDocument(project.getDictionary().getElement());
			
			
//			XMLWriter w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + dicoFile.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
//			w.write(d);
//			w.close();

			dicoFile.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
//			dicoFile.createLink(Platform.getLocation().append(dicoFile.getFullPath()), IResource.REPLACE, null);

		} catch (Exception e) {
			throw new Exception (Messages.ActionLoadFdProject_26, e);
		}

		if (project instanceof MultiPageFdProject){
			for(FdModel m : ((MultiPageFdProject)project).getPagesModels()){
				IFile _mf = p.getFile( m.getName() + ".freedashboard"); //$NON-NLS-1$
				try {
					Document d = DocumentHelper.createDocument(m.getElement());
					
					
//					XMLWriter w = new XMLWriter(new FileOutputStream(Platform.getLocation().toOSString() + _mf.getFullPath().toOSString()), OutputFormat.createPrettyPrint());
//					w.write(d);
//					w.close();

//					dico_mf.createLink(Platform.getLocation().append(_mf.getFullPath()), IResource.REPLACE, null);
					_mf.create(IOUtils.toInputStream(d.asXML(), "UTF-8"), true, null); //$NON-NLS-1$
				} catch (Exception e) {
					throw new Exception (Messages.ActionLoadFdProject_29 + m.getName(), e);
				}

			}
		}

		for(bpm.fd.api.core.model.resources.IResource r : project.getResources()){
			IFile rF = p.getFile( r.getName());
			try{
//				FileOutputStream fos = new FileOutputStream(rF.getLocation().toOSString());
				FileInputStream fis = new FileInputStream(r.getFile());
				
//				byte[] buffer = new byte[1024];
//				int sz ;
//				
//				while((sz = (fis.read(buffer))) > 0){
//					fos.write(buffer, 0, sz);
//					
//				}
//				r.setFile(rF.getLocation().toFile());
//				fos.close();
//				fis.close();
				rF.create(fis, true, null);
				r.setFile(rF.getLocation().toFile());
//				rF.createLink(Platform.getLocation().append(rF.getFullPath()), IResource.REPLACE, null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
			
					
		
	
		
	}
	
	
}
