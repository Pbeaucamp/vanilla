package bpm.birep.admin.client.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.User;

public class DialogPickupUsers extends Dialog {

	private CheckboxTableViewer tableUsers;

	private List<User> users;
	
	private List<User> alreadySelectedUsers;
	private List<User> selectedUsers;

	public DialogPickupUsers(Shell parentShell, List<User> alreadySelectedUsers) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.alreadySelectedUsers = alreadySelectedUsers;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setText(Messages.DialogPickupUsers_0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		Table table = new Table(c, SWT.BORDER | SWT.CHECK);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tableUsers = new CheckboxTableViewer(table);
		tableUsers.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<User> l = (List<User>) inputElement;
				return l.toArray(new User[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
		tableUsers.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((User) element).getName();
			}
		});

		loadUsers();

		return c;
	}

	private void loadUsers() {
		try {
			this.users = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (users == null) {
			users = new ArrayList<User>();
		}
		sortUsers(users);
		tableUsers.setInput(users);

		if (alreadySelectedUsers != null) {
			for (User selectedUser : alreadySelectedUsers) {
				for (User user : users) {
					if (selectedUser.getId().equals(user.getId())) {
						tableUsers.setChecked(user, true);
						break;
					}
				}
			}
		}
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
		this.selectedUsers = new ArrayList<User>();
		if (users != null) {
			for (User user : users) {
				if (tableUsers.getChecked(user)) {
					selectedUsers.add(user);
				}
			}
		}

		if (selectedUsers.isEmpty()) {
			return;
		}
		super.okPressed();
	}

	public List<User> getSelectedUser() {
		return selectedUsers;
	}

}
