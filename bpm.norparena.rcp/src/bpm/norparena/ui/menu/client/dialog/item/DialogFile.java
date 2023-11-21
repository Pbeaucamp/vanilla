package bpm.norparena.ui.menu.client.dialog.item;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class DialogFile extends DialogItem {
	private File file;
	
	public DialogFile(Shell parentShell, RepositoryDirectory target, File file) {
		super(parentShell, target);
		this.file = file;
	}
	
	@Override
	protected void okPressed() {
		IRepositoryApi sock = Activator.getDefault().getSocket();
		Integer id = null;
		try {
			String savename = name.getText();
			savename = savename.replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			
			id = sock.getRepositoryService().addExternalDocumentWithDisplay(
					target, savename, comment.getText(), 
					internalversion.getText(), "", new FileInputStream(file), true, file.getName().substring(file.getName().lastIndexOf("."), file.getName().length())).getId();
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogFile_3, e.getMessage());
			return;
		}

		
		try {
			for(Object g : groups.getCheckedElements()){
				Activator.getDefault().getSocket().getAdminService().addGroupForItem(((Group)g).getId(), id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.okPressed();
	}

}
