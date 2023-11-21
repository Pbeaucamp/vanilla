package bpm.norparena.ui.menu.client.dialog.item;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class DialogCustom extends DialogItem {
	
	private String xml;
	private int subtype;
	
	public DialogCustom(Shell parentShell, RepositoryDirectory target, String xml, int subtype) {
		super(parentShell, target);
		this.xml = xml;
		this.subtype = subtype;
	}
	
	@Override
	protected void okPressed() {
		IRepositoryApi sock = Activator.getDefault().getSocket();
		Integer id = null;
		try {
			id = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.CUST_TYPE, subtype, target, name.getText(), 
					comment.getText(), internalversion.getText(), "", xml, true).getId();
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogCustom_1, e.getMessage());
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
