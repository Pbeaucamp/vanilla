package bpm.birep.admin.client.dialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.User;

public class DilaogPickupUser extends Dialog {

	private ComboViewer combo;
	private User selectedUser;

	public DilaogPickupUser(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setText(Messages.DilaogPickupUser_0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		combo = new ComboViewer(c, SWT.READ_ONLY);
		combo.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}

			@Override
			public void dispose() { }

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		combo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((User) element).getName();
			}

		});
		combo.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((User) e1).getName().compareTo(((User) e2).getName());
			}

		});
		combo.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		try {
			List<User> users = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers();
			sortUsers(users);
			combo.setInput(users);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return c;
	}

	private void sortUsers(List<User> users) {
		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	@Override
	protected void okPressed() {

		selectedUser = (User) ((IStructuredSelection) combo.getSelection()).getFirstElement();
		super.okPressed();
	}

	public User getSelectedUser() {
		return selectedUser;
	}

}
