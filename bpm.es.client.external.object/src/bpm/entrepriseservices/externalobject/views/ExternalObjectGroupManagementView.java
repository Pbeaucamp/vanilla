package bpm.entrepriseservices.externalobject.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;

import adminbirep.Activator;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.entrepriseservices.externalobject.Messages;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ExternalObjectGroupManagementView extends bpm.birep.admin.client.views.historic.GroupManagementView {

	public static final String ID = "bpm.entrepriseservices.externalobject.GroupManagementView"; //$NON-NLS-1$

	protected void createGroupsViewers(Composite main) {
		Label l0 = new Label(main, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l0.setText(Messages.ExternalObjectGroupManagementView_1);

		groups = new CheckboxTreeViewer(main, SWT.H_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.BORDER);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		groups.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				if (parentElement == currentgroups) {
					List<Group> l = new ArrayList<Group>((List<Group>) parentElement);

					List<Group> toRemove = new ArrayList<Group>();
					for (Group g : l) {
						if (g.getParentId() != null) {
							toRemove.add(g);
						}
					}
					l.removeAll(toRemove);

					return l.toArray(new Group[l.size()]);
				}
				else {
					List<Group> l = new ArrayList<Group>();
					for (Group g : currentgroups) {
						if (((Group) parentElement).getId().equals(g.getParentId())) {
							l.add(g);
						}
					}
					if (l.size() == 0) {
						return null;
					}
					return l.toArray(new Group[l.size()]);
				}

			}

			public Object getParent(Object element) {
				if (element instanceof Group && ((Group) element).getParentId() == null) {
					return currentgroups;
				}
				else {
					for (Group g : currentgroups) {
						if (g.getId().equals(((Group) element).getParentId())) {
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				for (Group g : currentgroups) {
					if (((Group) element).getId().equals(g.getParentId())) {
						return true;
					}
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				List<Group> l = new ArrayList<Group>((List<Group>) inputElement);

				List<Group> toRemove = new ArrayList<Group>();
				for (Group g : l) {
					if (g.getParentId() != null) {
						toRemove.add(g);
					}
				}
				l.removeAll(toRemove);

				return l.toArray(new Group[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}

		});
		groups.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {

					if (selectedObject instanceof TreeItem) {
						try {
							Activator.getDefault().getRepositoryApi().getAdminService().addGroupForItem(((Group) event.getElement()).getId(), (((TreeItem) selectedObject).getItem()).getId());
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.ExternalObjectGroupManagementView_2, e.getMessage());
						}
					}

				}
				else {
					if (selectedObject instanceof TreeItem) {

						RepositoryItem item = (((TreeItem) selectedObject).getItem());

						try {
							Activator.getDefault().getRepositoryApi().getAdminService().removeGroupForItem(((Group) event.getElement()).getId(), item.getId());
						} catch (Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), Messages.ExternalObjectGroupManagementView_3, e.getMessage());
						}
					}
				}

			}

		});
		groups.setInput(currentgroups);

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;

		if (ss.isEmpty()) {
			groups.setInput(new ArrayList<Group>());

			selectedObject = null;
			ExternalCallsView.selectedObject = null;
			loadGroups.setEnabled(false);
			return;
		}

		if (ss.getFirstElement() instanceof TreeItem) {
			groups.setInput(currentgroups);

			selectedObject = (TreeItem) ss.getFirstElement();
			ExternalCallsView.selectedObject = (TreeItem) ss.getFirstElement();
			loadGroups.setEnabled(true);
		}
		else {
			groups.setInput(new ArrayList<Group>());

			selectedObject = null;
			ExternalCallsView.selectedObject = null;
			loadGroups.setEnabled(false);
			return;
		}
		initGroups();
	}

	protected void initGroups() {
		int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);

		if (selectedObject != null && selectedObject instanceof TreeItem) {

			try {
				int directoryItemId = ((TreeItem) selectedObject).getItem().getId();
				List<Group> authorizedGroups = Activator.getDefault().getRepositoryApi().getAdminService().getGroupsForItemId(directoryItemId);

				currentgroups = (List<Group>) groups.getInput();
				for (Group gr : currentgroups) {

					boolean found = false;
					for (Group autGr : authorizedGroups) {
						if (autGr.getId().equals(gr.getId())) {
							found = true;
							break;
						}
					}

					groups.setChecked(gr, found);
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getSite().getShell(), Messages.ExternalObjectGroupManagementView_5, e.getMessage());

			}

		}
		groups.refresh();

		currentState.setText(Messages.ExternalObjectGroupManagementView_6 + step + Messages.ExternalObjectGroupManagementView_7);
	}

	protected void update(Object object) {
		if (object instanceof TreeItem) {
			selectedObject = ((TreeItem) object).getItem();
		}
	}
}
