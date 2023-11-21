package bpm.birep.admin.client.dialog.item;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

abstract class DialogItem extends Dialog {
	protected Text name, comment, internalversion;
	protected Composite c;
	protected CheckboxTableViewer groups;
	protected RepositoryDirectory target;

	public DialogItem(Shell parentShell, RepositoryDirectory target) {
		super(parentShell);
		this.target = target;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 400);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l.setText(Messages.DialogItem_0);

		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l3.setText(Messages.DialogItem_1);

		comment = new Text(c, SWT.BORDER);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l5 = new Label(c, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l5.setText(Messages.DialogItem_3);

		internalversion = new Text(c, SWT.BORDER);
		internalversion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.DialogItem_4);

		groups = new CheckboxTableViewer(c, SWT.BORDER | SWT.V_SCROLL);
		groups.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		groups.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Group> l = (List<Group>) inputElement;
				return l.toArray(new Group[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}

		});

		try {
			groups.setInput(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return c;
	}

}
