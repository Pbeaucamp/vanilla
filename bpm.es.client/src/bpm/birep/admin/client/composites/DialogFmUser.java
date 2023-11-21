package bpm.birep.admin.client.composites;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Messages;
import bpm.freemetrics.api.security.FmUser;

public class DialogFmUser extends Dialog {

	private ListViewer list;
	private HashMap<Integer, FmUser> users;
	private Integer selected;

	public DialogFmUser(Shell parentShell, HashMap<Integer, FmUser> users) {
		super(parentShell);
		this.users = users;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		list = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		list.getList().setLayoutData(new GridData(GridData.FILL_BOTH));

		list.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				Collection<Integer> c = (Collection<Integer>) inputElement;
				Integer[] i = new Integer[c.size()];
				int count = 0;
				for (Integer e : c) {
					i[count++] = e;
				}
				return i;
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		list.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if(((Integer) element).equals(new Integer(-1))) {
					return Messages.DialogFmUser_0;
				}
				return "" + users.get((Integer) element).getName(); //$NON-NLS-1$
			}

		});

		if (users != null) {
			list.setInput(users.keySet());
		}
		else {
			List<Integer> input = new ArrayList<Integer>();
			input.add(-1);
			list.setInput(input);
		}

		return parent;
	}

	@Override
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection) list.getSelection();
		if (ss.isEmpty() || ((Integer) ss.getFirstElement()).equals(new Integer(-1))) {
			cancelPressed();
		}

		selected = (Integer) ss.getFirstElement();

		super.okPressed();
	}

	public Integer getSelected() {
		return selected;
	}

}
