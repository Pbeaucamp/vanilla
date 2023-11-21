package bpm.fd.repository.ui.wizard.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import bpm.fd.repository.ui.Messages;
import bpm.fd.repository.ui.dialogs.DialogDirectory;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class ActionCreateDirectory {

	private TreeViewer viewer;
	private IRepositoryApi sock;
	private IVanillaSecurityManager mng;
	
	public ActionCreateDirectory(TreeViewer viewer, IRepositoryApi sock, IVanillaSecurityManager mng){
		this.viewer = viewer;
		this.sock = sock;
		this.mng = mng;
	}
	
	public void run(){
		StructuredSelection ss = (StructuredSelection)viewer.getSelection();
		try{
			DialogDirectory dial = new DialogDirectory(viewer.getControl().getShell(), mng.getGroups());
			dial.create();
			
			if(dial.open() == DialogDirectory.OK){
				RepositoryDirectory d = dial.getDirectory();
				
				d.setVisible(true);
//				d.setType(Messages.ActionCreateDirectory_0);
				RepositoryDirectory parent = null;
				if (!viewer.getSelection().isEmpty()){
					Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
					if (o instanceof RepositoryDirectory){
						parent = (RepositoryDirectory)o;
					}
				}
				d.setParentId(parent.getId());
				d = sock.getRepositoryService().addDirectory(
						d.getName(), d.getComment(), parent);
				
				((Repository)viewer.getInput()).add(d);
				
				
				
				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openError(viewer.getControl().getShell(), Messages.ActionCreateDirectory_3, e.getMessage());
		}
	}
}
