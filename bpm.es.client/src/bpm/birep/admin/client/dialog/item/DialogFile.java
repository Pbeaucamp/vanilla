package bpm.birep.admin.client.dialog.item;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogFile extends DialogItem {
	private File file;
	
	public DialogFile(Shell parentShell, RepositoryDirectory target, File file) {
		super(parentShell, target);
		this.file = file;
	}
	
	@Override
	protected void okPressed() {
		IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
		RepositoryItem id = null;
		try {
			String savename = name.getText();
			savename = savename.replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			id = sock.getRepositoryService().addExternalDocumentWithDisplay(
					target, 
					savename, comment.getText(), 
					internalversion.getText(), "", new FileInputStream(file), //$NON-NLS-1$
					false, file.getName().substring(file.getName().lastIndexOf(".") + 1)); //$NON-NLS-1$
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage()); //$NON-NLS-1$
			return;
		}

		
		try {
			for(Object g : groups.getCheckedElements()){
				Activator.getDefault().getRepositoryApi().getAdminService().addGroupForItem(((Group)g).getId(), id.getId());
				Activator.getDefault().getRepositoryApi().getAdminService().setObjectRunnableForGroup(((Group)g).getId(), id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage()); //$NON-NLS-1$
		}

		super.okPressed();
	}

}
