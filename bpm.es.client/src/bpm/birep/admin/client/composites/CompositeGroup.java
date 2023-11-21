package bpm.birep.admin.client.composites;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.views.ViewGroup;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class CompositeGroup extends Composite {

	private boolean buttonBar = false;
	private Text name, comment;

	private Group group;

	private ListViewer users;

	private Button ok, cancel;
	private Text maxFmdtWeight;

	public CompositeGroup(Composite parent, int style, Group group, boolean buttonBar) {
		super(parent, style);
		this.group = group;
		this.buttonBar = buttonBar;

		createContent();
		fillData();
	}

	private void createContent() {
		this.setLayout(new GridLayout(2, false));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l.setText(Messages.CompositeGroup_0);

		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		l.setText(Messages.CompositeGroup_1);

		maxFmdtWeight = new Text(this, SWT.BORDER);
		maxFmdtWeight.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 3));
		l2.setText(Messages.CompositeGroup_2);

		comment = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 3));

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l3.setText(Messages.CompositeGroup_3);

		users = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL);
		users.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		users.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((User) element).getName();
			}

		});
		users.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<User> l = (List<User>) inputElement;

				return l.toArray(new User[l.size()]);
			}

			@Override
			public void dispose() { }

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});

		if (buttonBar) {
			Composite c = new Composite(this, SWT.NONE);
			c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			c.setLayout(new GridLayout(2, true));

			ok = new Button(c, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
			ok.setText(Messages.CompositeGroup_4);
			ok.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						setGroup();
						Activator.getDefault().getVanillaApi().getVanillaSecurityManager().updateGroup(group);
						ViewGroup v = (ViewGroup) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
						if (v != null) {
							v.refresh();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), Messages.CompositeGroup_5, e1.getMessage());
					}
				}
			});

			cancel = new Button(c, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
			cancel.setText(Messages.CompositeGroup_6);
			cancel.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					fillData();
				}

			});
		}
	}

	private void fillWidget(String value, Text widget) {
		if (value == null) {

		}
		else {
			widget.setText(value);
		}
	}

	private void fillData() {
		if (group == null) {
			return;
		}
		fillWidget(group.getName(), name);
		fillWidget(group.getComment(), comment);
		fillWidget(group.getMaxSupportedWeightFmdt() + "", maxFmdtWeight); //$NON-NLS-1$
		try {
			users.setInput(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsersForGroup(group));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void setGroup() throws Exception {
		if (group == null) {
			group = new Group();
		}
		group.setName(name.getText());
		group.setComment(comment.getText());

		try {
			Integer weight = Integer.parseInt(maxFmdtWeight.getText());
			if (weight < 0) {
				throw new Exception();
			}
			group.setMaxSupportedWeightFmdt(weight);
		} catch (Exception ex) {
			maxFmdtWeight.setFocus();
			throw new Exception(Messages.CompositeGroup_8);
		}
	}

	public Group getGroup() {
		return group;
	}
}
