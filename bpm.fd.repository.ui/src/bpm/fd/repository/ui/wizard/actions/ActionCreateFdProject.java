package bpm.fd.repository.ui.wizard.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.fd.repository.ui.Activator;
import bpm.fd.repository.ui.Messages;
import bpm.fd.repository.ui.dialogs.DialogItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ActionCreateFdProject {

	private IRepositoryApi sock;
	private IVanillaSecurityManager mng;
	private RepositoryDirectory target;
	private Exception error;
	
	public ActionCreateFdProject(IRepositoryApi sock, RepositoryDirectory directory, IVanillaSecurityManager mng){
		this.sock = sock;
		this.target = directory;
		this.mng = mng;
	}
	
	public RepositoryItem perform(final boolean isOnlyDictionary) throws Exception{
		if (sock == null){
			throw new Exception(Messages.ActionCreateFdProject_0);
		}
		
		if (target == null){
			throw new Exception(Messages.ActionCreateFdProject_1);
		}
		
		final DialogItem dial = new DialogItem(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), mng.getGroups());
		if (dial.open() == DialogItem.OK){
			RepositoryItem di = dial.getItem();
			di.setVisible(true);
			di.setDirectoryId(target.getId());
			
			IRunnableWithProgress r = new IRunnableWithProgress(){

				public void run(IProgressMonitor monitor)throws InvocationTargetException, InterruptedException {
					
					monitor.beginTask(Messages.ActionCreateFdProject_2, IProgressMonitor.UNKNOWN);
					try{
						if (!isOnlyDictionary){
							ModelLoader.save(bpm.fd.design.ui.Activator.getDefault().getProject(), 
									sock, target, dial.getGroupName(), dial.getItem().getItemName(), 
									bpm.fd.design.ui.Activator.getDefault().getProject().getDictionary().getName());
						}
						else{
							ModelLoader.save(bpm.fd.design.ui.Activator.getDefault().getProject().getDictionary(), 
									sock, target, dial.getGroupName(), dial.getItem().getItemName());
						}
					}catch(Exception ex){
						error = ex;
					}
					
					
				}
				
			};

			IProgressService service = PlatformUI.getWorkbench().getProgressService();
		     try {
		    	 service.run(false, false, r);
		     } catch (Exception e) {
		    	 if (error == null){
		    		 error = e;
		    	 }else{
		    		 e.printStackTrace();
		    	 }
		    	 
		     }finally{
		    	 if (error != null){
		    		 error.printStackTrace();
		    		 MessageDialog.openError(dial.getShell(), Messages.ActionCreateFdProject_3, Messages.ActionCreateFdProject_4 + error.getMessage());
		    	 }
		     }
			
			
			

			return di;
		}
		return null;
	}
	
	
}
