package bpm.birep.admin.client.dialog;

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
import bpm.vanilla.platform.core.beans.Group;

public class DilaogPickupGroup extends Dialog {

	private ComboViewer combo;
	private Group selectedGroup;

	public DilaogPickupGroup(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setText(Messages.DilaogPickupGroup_0);
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
				return ((Group) element).getName();
			}

		});
		combo.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((Group) e1).getName().compareTo(((Group) e2).getName());
			}

		});
		combo.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		try {
			combo.setInput(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(0, 1000));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return c;
	}

	@Override
	protected void okPressed() {
		selectedGroup = (Group) ((IStructuredSelection) combo.getSelection()).getFirstElement();
		super.okPressed();
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

}
