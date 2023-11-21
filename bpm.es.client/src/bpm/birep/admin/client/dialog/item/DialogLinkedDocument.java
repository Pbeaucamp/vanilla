package bpm.birep.admin.client.dialog.item;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogLinkedDocument extends DialogItem {
	private RepositoryItem item;
	private File file;
	private IRepositoryApi admin;

	public DialogLinkedDocument(Shell parent, RepositoryItem item, File file) {
		super(parent, null);
		this.item = item;
		this.file = file;

		try {
			admin = Activator.getDefault().getRepositoryApi();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control c = super.createDialogArea(parent);

		internalversion.setText("1"); //$NON-NLS-1$
		internalversion.setEnabled(false);

		return c;
	}

	@Override
	protected void okPressed() {
		LinkedDocument ld = null;
		try {
			ld = admin.getDocumentationService().attachDocumentToItem(item, new FileInputStream(file), name.getText(), comment.getText(), file.getName().substring(file.getName().lastIndexOf(".") + 1), ""); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage()); //$NON-NLS-1$
			super.okPressed();
		}

		try {
			for (Object g : groups.getCheckedElements()) {
				admin.getAdminService().addGroupForLinkedDocument((Group) g, (LinkedDocument) ld);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.okPressed();
	}

}
