package bpm.entrepriseservices.externalobject.wizard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import adminbirep.Activator;
import bpm.entrepriseservices.externalobject.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.VanillaParameterComponent;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class PropertiesPage extends WizardPage {
	protected Group selectedGroup;
	protected User selectedUser;
	protected String selectedOutput = "html"; //$NON-NLS-1$
	// protected ObjectAdressable currentAdressable;
	protected DataSource selectedDataSource;

	private RepositoryItem directoryItem;
	private List<Group> currentgroups;
	private List<User> currentUsers;

	private ComboViewer users;
	private ComboViewer cbOutputs;
	private ComboViewer datasources;

	private String[] formats = new String[] { "html", "pdf", "xls", "xlsx", "ods", "doc", "docx", "odt", "ppt", "pht", "pptx", "postscript" }; //$NON-NLS-1$ //$NON-NLS-2$
	protected Date selectedEndDate = new Date();

	private DateTime date, time;

	private ExternalUrlWizard wizard;
	private List<Parameter> parameters;
	
	private boolean loadParam = true;

	protected PropertiesPage(ExternalUrlWizard wizard, String pageName, RepositoryItem directoryItem) {
		super(pageName);
		this.wizard = wizard;
		this.directoryItem = directoryItem;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));

		Label lblGroup = new Label(main, SWT.NONE);
		lblGroup.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblGroup.setText(Messages.PropertiesPage_4);

		try {
			currentgroups = Activator.getDefault().getRepositoryApi().getAdminService().getGroupsForItemId(directoryItem.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		ComboViewer groups = new ComboViewer(main, SWT.READ_ONLY);
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
					getContainer().updateButtons();
					return;
				}

				Object o = ss.getFirstElement();
				if (o instanceof Group) {
					selectedGroup = (Group) o;
					selectedUser = null;
					try {
						currentUsers = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsersForGroup(selectedGroup);
						users.setInput(currentUsers);
					} catch (Exception e) {
						e.printStackTrace();
						users.setInput(new ArrayList<User>());
					}
					
					loadParam = true;

				}
				getContainer().updateButtons();

			}
		});

		groups.setInput(currentgroups);

		Label lblUser = new Label(main, SWT.NONE);
		lblUser.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblUser.setText(Messages.PropertiesPage_5);

		users = new ComboViewer(main, SWT.READ_ONLY);
		users.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		users.setContentProvider(new ITreeContentProvider() {

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
				List<User> l = new ArrayList<User>((List<User>) inputElement);
				return l.toArray(new User[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		users.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();

				if (ss.isEmpty()) {
					getContainer().updateButtons();
					return;
				}

				Object o = ss.getFirstElement();
				if (o instanceof User) {
					selectedUser = (User) o;
					getContainer().updateButtons();
				}
			}
		});

		users.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((User) element).getName();
			}

		});

		Label lblOutput = new Label(main, SWT.NONE);
		lblOutput.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblOutput.setText(Messages.PropertiesPage_6);

		cbOutputs = new ComboViewer(main, SWT.READ_ONLY);
		cbOutputs.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));

		cbOutputs.setContentProvider(new ITreeContentProvider() {

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
				String[] s = (String[]) inputElement;
				return s;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		cbOutputs.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});

		cbOutputs.setInput(formats);

		cbOutputs.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();

				if (ss.isEmpty()) {
					getContainer().updateButtons();
					return;
				}

				Object o = ss.getFirstElement();
				if (o instanceof String)
					selectedOutput = (String) o;
			}
		});

		if (directoryItem.getType() == IRepositoryApi.CUST_TYPE && directoryItem.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
			Label lblDataSource = new Label(main, SWT.NONE);
			lblDataSource.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
			lblDataSource.setText(Messages.PropertiesPage_7);

			datasources = new ComboViewer(main, SWT.READ_ONLY);
			datasources.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

			datasources.setContentProvider(new ITreeContentProvider() {

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
					List<DataSource> l = new ArrayList<DataSource>((List<DataSource>) inputElement);
					return l.toArray(new DataSource[l.size()]);
				}

				public void dispose() {
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

			});

			datasources.addSelectionChangedListener(new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					StructuredSelection ss = (StructuredSelection) event.getSelection();

					if (ss.isEmpty()) {
						getContainer().updateButtons();
						return;
					}

					Object o = ss.getFirstElement();
					if (o instanceof DataSource) {
						selectedDataSource = (DataSource) o;
						getContainer().updateButtons();
					}
				}
			});

			datasources.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					return ((DataSource) element).getName();
				}

			});

			try {
				List<DataSource> ds = Activator.getDefault().getRepositoryApi().getImpactDetectionService().getAllDatasources();
				if (ds != null)
					datasources.setInput(ds);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		org.eclipse.swt.widgets.Group grdEndDate = new org.eclipse.swt.widgets.Group(main, SWT.NONE);
		grdEndDate.setLayout(new GridLayout(3, false));
		grdEndDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label lblEndDate = new Label(grdEndDate, SWT.NONE);
		lblEndDate.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		lblEndDate.setText(Messages.PropertiesPage_8);

		date = new DateTime(grdEndDate, SWT.DATE);
		date.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				setDate();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		time = new DateTime(grdEndDate, SWT.TIME);
		time.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				setDate();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		setControl(main);

	}

	@Override
	public boolean canFlipToNextPage() {
		if (loadParam && selectedGroup != null) {
			this.parameters = loadParameters();
			this.loadParam = false;
			
			wizard.loadParameters(parameters);
		}
		return (selectedUser != null && selectedGroup != null && parameters != null && !parameters.isEmpty());
	}

	private List<Parameter> loadParameters() {
		try {
			if (directoryItem.getType() == IRepositoryApi.FWR_TYPE) {
				return getFWRParameters(directoryItem, selectedGroup);
			}
			else {
				return Activator.getDefault().getRepositoryApi().getRepositoryService().getParameters(directoryItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Parameter> getFWRParameters(RepositoryItem directoryItem, Group selectedGroup) throws Exception {
		IVanillaContext ctx = Activator.getDefault().getVanillaApi().getVanillaContext();
		int repositoryId = Activator.getDefault().getCurrentRepository().getId();

		List<Parameter> urlParams = new ArrayList<Parameter>();

		ObjectIdentifier ident = new ObjectIdentifier(repositoryId, directoryItem.getId());
		IRuntimeConfig config = new ReportRuntimeConfig(ident, null, selectedGroup.getId());

		VanillaParameterComponent paramComponent = new RemoteVanillaParameterComponent(ctx);
		List<VanillaGroupParameter> params = paramComponent.getParameters(config);
		if (params != null) {
			for (VanillaGroupParameter grp : params) {
				if (grp.getParameters() != null) {
					for (VanillaParameter param : grp.getParameters()) {
						Parameter urlParam = new Parameter();
						urlParam.setDirectoryItemId(directoryItem.getId());
						urlParam.setName(param.getName());

						urlParams.add(urlParam);
					}
				}
			}
		}
		return urlParams;
	}

	@Override
	public boolean isPageComplete() {
		return (selectedUser != null && selectedGroup != null);
	}

	private void setDate() {
		int year = date.getYear();
		int month = date.getMonth();
		int day = date.getDay();

		int hours = time.getHours();
		int minutes = time.getMinutes();
		int second = time.getSeconds();

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hours, minutes, second);
		selectedEndDate = cal.getTime();
	}

}
