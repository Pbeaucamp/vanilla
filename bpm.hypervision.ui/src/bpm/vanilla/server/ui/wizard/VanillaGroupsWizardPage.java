package bpm.vanilla.server.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.server.ui.Messages;

public class VanillaGroupsWizardPage extends WizardPage {

	private CheckboxTableViewer viewer;

	protected VanillaGroupsWizardPage(String pageName) {
		super(pageName);
		setDescription(Messages.VanillaGroupsWizardPage_0);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.VanillaGroupsWizardPage_1);

		viewer = CheckboxTableViewer.newCheckList(main, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				List<Group> c = (List<Group>) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				getContainer().updateButtons();
			}
		});

		setControl(main);
	}

	public void fillContent(List<Group> groups) {
		viewer.setInput(groups);
	}

	@Override
	public boolean isPageComplete() {
		return viewer.getCheckedElements().length > 0;
	}

	public List<Group> getGroups() {
		List<Group> groups = new ArrayList<Group>();

		for (Object o : viewer.getCheckedElements()) {
			groups.add((Group) o);
		}
		return groups;
	}

}
