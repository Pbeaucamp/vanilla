package bpm.birep.admin.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Messages;
import bpm.birep.admin.client.composites.CompositePickupItem;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogPickupItem extends Dialog {

	private CompositePickupItem compositeItem;
	private Repository repository;

	private RepositoryItem selectedItem;

	public DialogPickupItem(Shell parentShell, Repository repository) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.repository = repository;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setSize(300, 250);
		getShell().setText(Messages.DialogPickupItem_0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		compositeItem = new CompositePickupItem(c, SWT.NONE);
		compositeItem.setInput(repository);

		return c;
	}

	@Override
	protected void okPressed() {
		this.selectedItem = compositeItem.getSelectedItem();
		if (selectedItem == null) {
			return;
		}
		super.okPressed();
	}

	public RepositoryItem getSelectedItem() {
		return selectedItem;
	}
}
