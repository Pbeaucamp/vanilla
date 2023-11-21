package bpm.birep.admin.client.dialog.item;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogCustom extends DialogItem {
	private InputStream fis;
	private int subtype;

	private Combo encoding;
	private int type;

	public DialogCustom(Shell parentShell, RepositoryDirectory target, InputStream fis, int type, int subtype) {
		super(parentShell, target);
		this.fis = fis;
		this.subtype = subtype;
		this.type = type;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = (Composite) super.createDialogArea(parent);

		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogCustom_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		encoding = new Combo(main, SWT.NONE);
		encoding.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		encoding.setItems(Charset.availableCharsets().keySet().toArray(new String[Charset.availableCharsets().keySet().size()]));
		encoding.setText(Charset.defaultCharset().name());

		return main;
	}

	@Override
	protected void okPressed() {
		IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
		RepositoryItem id = null;

		String xml = null;

		try {
			xml = IOUtils.toString(fis, "UTF-8"); //$NON-NLS-1$

			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();

			xml = root.asXML();
		} catch(Exception ex) {
			MessageDialog.openError(getShell(), Messages.DialogCustom_2, Messages.DialogCustom_3 + ex.getMessage() + Messages.DialogCustom_4);
			return;
		}
		try {
			id = sock.getRepositoryService().addDirectoryItemWithDisplay(type, subtype, target, name.getText(), comment.getText(), internalversion.getText(), "", xml, true); //$NON-NLS-1$

		} catch(Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogCustom_1, e.getMessage());
			return;
		}

		StringBuffer errors = new StringBuffer();
		try {

			for(Object g : groups.getCheckedElements()) {
				try {
					Activator.getDefault().getRepositoryApi().getAdminService().addGroupForItem(((Group) g).getId(), id.getId());
				} catch(Exception ex) {
					errors.append(Messages.DialogCustom_5 + ((Group) g).getName() + "\n"); //$NON-NLS-1$
				}
				try {
					sock.getAdminService().setObjectRunnableForGroup(((Group) g).getId(), id);
				} catch(Exception ex) {
					errors.append(Messages.DialogCustom_7 + ((Group) g).getName() + "\n"); //$NON-NLS-1$
				}
				try {
					sock.getReportHistoricService().createHistoricAccess(id.getId(), ((Group) g).getId());
				} catch(Exception ex) {
					errors.append(Messages.DialogCustom_6 + ((Group) g).getName() + "\n"); //$NON-NLS-1$
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(errors.length() > 0) {
			MessageDialog.openError(getShell(), Messages.DialogCustom_11, Messages.DialogCustom_12 + errors.toString());

		}
		super.okPressed();
	}

}
