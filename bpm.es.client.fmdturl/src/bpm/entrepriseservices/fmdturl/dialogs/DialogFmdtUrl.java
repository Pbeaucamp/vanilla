package bpm.entrepriseservices.fmdturl.dialogs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import bpm.entrepriseservices.fmdturl.Messages;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogFmdtUrl extends Dialog {

	private Text name;
	private Text description;
	private ComboViewer groups;
	private ComboViewer models;
	private ComboViewer packages;

	private FmdtUrl fmdtUrl;
	private RepositoryItem item;

	private List<Group> currentgroups;
	private Group selectedGroup;

	private List<IBusinessModel> currentmodels;
	private IBusinessModel selectedModel;

	private List<IBusinessPackage> currentpackages;
	private IBusinessPackage selectedPackage;

	private boolean update = false;

	public DialogFmdtUrl(Shell parentShell, RepositoryItem item, FmdtUrl fmdturl) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

		this.item = item;
		if (fmdturl != null) {
			this.update = true;
			this.fmdtUrl = fmdturl;
		}
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogFmdtUrl_0);
		getShell().setSize(450, 500);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		org.eclipse.swt.widgets.Group required = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		required.setLayout(new GridLayout(1, false));
		required.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		required.setText(Messages.DialogFmdtUrl_1);

		Composite container = new Composite(required, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogFmdtUrl_2);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		l2.setText(Messages.DialogFmdtUrl_3);

		description = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 3));

		Label _l = new Label(container, SWT.NONE);
		_l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		_l.setText(""); //$NON-NLS-1$

		Label _ll = new Label(container, SWT.NONE);
		_ll.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		_ll.setText(""); //$NON-NLS-1$

		Label lblGroup = new Label(container, SWT.NONE);
		lblGroup.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblGroup.setText(Messages.DialogFmdtUrl_6);

		if (update) {
			try {
				selectedGroup = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupByName(fmdtUrl.getGroupName());
				currentgroups = new ArrayList<Group>();
				currentgroups.add(selectedGroup);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		else {
			try {
				currentgroups = Activator.getDefault().getRepositoryApi().getAdminService().getGroupsForItemId(item.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		groups = new ComboViewer(container, SWT.READ_ONLY);
		groups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

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
		groups.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				Object o = ss.getFirstElement();
				if (o instanceof Group) {
					selectedGroup = (Group) o;
					String result;
					try {
						result = Activator.getDefault().getRepositoryApi().getRepositoryService().loadModel(item);

						InputStream input = IOUtils.toInputStream(result);

						Collection<IBusinessModel> bmodels = MetaDataReader.read(selectedGroup.getName(), input, Activator.getDefault().getRepositoryApi());

						currentmodels = new ArrayList<IBusinessModel>();
						for (IBusinessModel b : bmodels) {
							currentmodels.add(b);
						}

						models.setInput(currentmodels);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});
		groups.setInput(currentgroups);

		if (update)
			groups.setSelection(new StructuredSelection(selectedGroup));

		Label lblModel = new Label(container, SWT.NONE);
		lblModel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblModel.setText(Messages.DialogFmdtUrl_8);

		models = new ComboViewer(container, SWT.READ_ONLY);
		models.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		models.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				return null;
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public Object[] getElements(Object inputElement) {
				List<IBusinessModel> l = new ArrayList<IBusinessModel>((List<IBusinessModel>) inputElement);

				return l.toArray(new IBusinessModel[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		models.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((IBusinessModel) element).getName();
			}

		});
		models.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				Object o = ss.getFirstElement();
				if (o instanceof IBusinessModel) {
					selectedModel = (IBusinessModel) o;
					Collection<IBusinessPackage> bpackages = selectedModel.getBusinessPackages(selectedGroup.getName());
					currentpackages = new ArrayList<IBusinessPackage>();
					for (IBusinessPackage p : bpackages) {
						currentpackages.add(p);
					}
					packages.setInput(currentpackages);
				}

			}
		});

		org.eclipse.swt.widgets.Group optional = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		optional.setLayout(new GridLayout(1, false));
		optional.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		optional.setText(Messages.DialogFmdtUrl_9);

		Composite _container = new Composite(optional, SWT.NONE);
		_container.setLayout(new GridLayout(2, false));
		_container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblPackage = new Label(_container, SWT.NONE);
		lblPackage.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblPackage.setText(Messages.DialogFmdtUrl_10);

		packages = new ComboViewer(_container, SWT.READ_ONLY);
		packages.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		packages.setContentProvider(new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				return null;
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<IBusinessPackage> l = new ArrayList<IBusinessPackage>((List<IBusinessPackage>) inputElement);

				return l.toArray(new IBusinessPackage[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

		});
		packages.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((IBusinessPackage) element).getName();
			}

		});
		packages.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				Object o = ss.getFirstElement();
				if (o instanceof IBusinessPackage) {
					selectedPackage = (IBusinessPackage) o;
				}

			}
		});

		if (update) {
			try {
				fillForUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return container;
	}

	private void fillForUpdate() throws Exception {
		name.setEnabled(false);
		name.setText(fmdtUrl.getName());

		if (fmdtUrl.getDescription() != null)
			description.setText(fmdtUrl.getDescription());

		String result = Activator.getDefault().getRepositoryApi().getRepositoryService().loadModel(item);
		InputStream input = IOUtils.toInputStream(result, "UTF-8"); //$NON-NLS-1$

		Collection<IBusinessModel> bmodels = MetaDataReader.read(selectedGroup.getName(), input, Activator.getDefault().getRepositoryApi());

		currentmodels = new ArrayList<IBusinessModel>();
		for (IBusinessModel b : bmodels) {
			if (b.getName().equals(fmdtUrl.getModelName())) {
				currentmodels.add(b);
				selectedModel = b;
				break;
			}
		}
		models.setInput(currentmodels);
		models.setSelection(new StructuredSelection(selectedModel));

		Collection<IBusinessPackage> bpackages = selectedModel.getBusinessPackages(selectedGroup.getName());
		currentpackages = new ArrayList<IBusinessPackage>();
		for (IBusinessPackage p : bpackages) {
			if (fmdtUrl.getPackageName() == null)
				currentpackages.add(p);
			else if (p.getName().equals(fmdtUrl.getPackageName())) {
				currentpackages.add(p);
				selectedPackage = p;
				break;
			}
		}
		packages.setInput(currentpackages);
		if (selectedPackage != null)
			packages.setSelection(new StructuredSelection(selectedPackage));
	}

	@Override
	protected void okPressed() {
		if (selectedGroup == null || selectedModel == null || "".equalsIgnoreCase(name.getText())) { //$NON-NLS-1$
			MessageDialog.openWarning(getShell(), Messages.DialogFmdtUrl_12, Messages.DialogFmdtUrl_13);
		}
		else if (update) {
			fmdtUrl.setDescription(description.getText());
			if (selectedPackage != null)
				fmdtUrl.setPackageName(selectedPackage.getName());

			try {

				Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().updateFmdtUrl(fmdtUrl);
				super.okPressed();
			} catch (IOException e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.DialogFmdtUrl_14, e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.DialogFmdtUrl_15, e.getMessage());
			}
		}
		else {
			FmdtUrl fmdtUrl = new FmdtUrl();
			fmdtUrl.setItemId(item.getId());
			fmdtUrl.setGroupName(selectedGroup.getName());
			fmdtUrl.setModelName(selectedModel.getName());
			fmdtUrl.setName(name.getText());
			fmdtUrl.setDescription(description.getText());

			if (selectedPackage != null)
				fmdtUrl.setPackageName(selectedPackage.getName());

			try {
				fmdtUrl.setRepositoryId(Activator.getDefault().getCurrentRepository().getId());
				int id = Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().addFmdtUrl(fmdtUrl);
				fmdtUrl.setId(id);
				super.okPressed();
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.DialogFmdtUrl_16, e.getMessage());
			}
		}

	}

	public FmdtUrl getFmdtUrl() {
		return fmdtUrl;
	}
}
