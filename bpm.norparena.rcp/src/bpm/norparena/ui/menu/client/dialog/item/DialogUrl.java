package bpm.norparena.ui.menu.client.dialog.item;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class DialogUrl extends DialogItem {
	private Text link;

	public DialogUrl(Shell parentShell, RepositoryDirectory target) {
		super(parentShell, target);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		super.createDialogArea(parent);
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false,1, 1));
		l.setText(Messages.DialogUrl_0);
		
		link = new Text(c, SWT.BORDER);
		link.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,1, 1));
		
		return c;
	}
	
	@Override
	protected void okPressed() {
		IRepositoryApi sock = Activator.getDefault().getSocket();
		Integer id = null;
		try {
			String savename = name.getText();
			savename = savename.replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
			id = sock.getRepositoryService().addExternalUrl(target, savename, comment.getText(), internalversion.getText(), "", link.getText()).getId(); //$NON-NLS-1$
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogUrl_1, e.getMessage());
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
