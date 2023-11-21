package bpm.birep.admin.client.reportsgroup;

import java.util.List;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.beans.Group;
import adminbirep.Activator;
import adminbirep.Messages;

public class ReportsGroupDefinitionPage extends WizardPage {

	private Text name;
	private Text description;
	private CheckboxTableViewer groups;
	
	public ReportsGroupDefinitionPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DisconnectedDefinitionPage_0);

		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(listener);

		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		l2.setText(Messages.DisconnectedDefinitionPage_1);

		description = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l3.setText(Messages.DialogItem_4);

		groups = new CheckboxTableViewer(main, SWT.BORDER | SWT.V_SCROLL);
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
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

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

		setControl(main);
	}

	@Override
	public boolean isPageComplete() {
		return !name.getText().isEmpty();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	public String getSelectedName() {
		return name.getText();
	}

	public String getDescription() {
		return description.getText();
	}
	
	public Object[] getGroupsChecked() {
		return groups.getCheckedElements();
	}

	private ModifyListener listener = new ModifyListener() {

		@Override
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};

}
