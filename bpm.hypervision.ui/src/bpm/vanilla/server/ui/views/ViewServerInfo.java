package bpm.vanilla.server.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.dialogs.DialogListManager;
import bpm.vanilla.server.ui.dialogs.DialogServerMemory;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.composite.ConfigComposite;
import bpm.vanilla.server.ui.views.helpers.ViewServerInfoHelper;
import bpm.vanilla.server.ui.wizard.RunReportWizard;

public class ViewServerInfo extends ViewPart implements IPropertyChangeListener {
	private FormToolkit toolkit;
	private ScrolledForm form;

	private Text serverState;
	private Button startButton, stopButton/* , connectButton */;

	private TableViewer viewerConfig;
	private ComboViewer runtimeNodeViewer;
	private ComboViewer componentTypeViewer;
	private ComboViewer repositoryViewer;

	private Button resetConfig, cancel;
	private IPropertyChangeListener configChangeListener;
	private HashMap<String, List<Server>> mapServerByUrl = new HashMap<String, List<Server>>();
	private ConfigComposite configComposite;

	private Button btnServMemory, btnRunTask, btnTaskManager, btnHistorize;

	public ViewServerInfo() {
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());

		form = toolkit.createScrolledForm(parent);
		form.setExpandHorizontal(false);
		form.setAlwaysShowScrollBars(true);
		form.setExpandVertical(true);
		form.getHorizontalBar().setEnabled(true);
		// form.getHorizontalBar().dispose();
//		GridData gridData = new GridData(GridData.FILL_BOTH);
//		gridData.widthHint = 100;

//		form.setLayoutData(gridData);
		form.setText(Messages.ViewServerInfo_46);
		toolkit.decorateFormHeading(form.getForm());

		Action refreshAction = new Action() {
			public void run() {
				refreshNodes();
			}
		};
		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.REFRESH));
		refreshAction.setToolTipText(Messages.ViewServerInfo_48);

		form.getToolBarManager().add(refreshAction);
		form.getToolBarManager().update(true);

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(layout);
		// form.getBody().setLayout(new GridLayout());

		Composite sectionToolbar = toolkit.createComposite(form.getBody());
		sectionToolbar.setLayout(new GridLayout(4, false));
		sectionToolbar.setLayoutData(new TableWrapData(TableWrapData.FILL));

		btnServMemory = toolkit.createButton(sectionToolbar, "", SWT.PUSH); //$NON-NLS-1$
		btnServMemory.setToolTipText(Messages.ViewServerInfo_3);
		btnServMemory.setLayoutData(new GridData());
		btnServMemory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogServerMemory d = new DialogServerMemory(getSite().getShell());
				d.open();
			}
		});
		btnServMemory.setImage(Activator.getDefault().getImageRegistry().get(Icons.MEMORY));

		btnRunTask = toolkit.createButton(sectionToolbar, "", SWT.PUSH); //$NON-NLS-1$
		btnRunTask.setToolTipText(Messages.ViewServerInfo_5);
		btnRunTask.setLayoutData(new GridData());
		btnRunTask.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getSite().getShell();
				if (Activator.getDefault().getServerType() == null) {
					MessageDialog.openInformation(sh, Messages.ActionRunTask_0, Messages.ActionRunTask_1);
					return;
				}
				RunReportWizard wiz = new RunReportWizard();
				WizardDialog dial = new WizardDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);

				dial.open();
			}
		});
		btnRunTask.setImage(Activator.getDefault().getImageRegistry().get(Icons.RUN_TASK));

		btnTaskManager = toolkit.createButton(sectionToolbar, "", SWT.PUSH); //$NON-NLS-1$
		btnTaskManager.setToolTipText(Messages.ViewServerInfo_7);
		btnTaskManager.setLayoutData(new GridData());
		btnTaskManager.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getSite().getShell();
				if (Activator.getDefault().getServerType() == null) {
					MessageDialog.openInformation(sh, Messages.ActionTaskManager_0, Messages.ActionTaskManager_1);
					return;
				}
				try {
					String vanillaUrl = Activator.getDefault().getRemoteServerManager().getServerConfig().getValue(Messages.ActionTaskManager_2);
					DialogListManager dial = new DialogListManager(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), vanillaUrl);
					dial.open();
				} catch (Exception ex) {

				}
			}
		});
		btnTaskManager.setImage(Activator.getDefault().getImageRegistry().get(Icons.MANAGER));

		btnHistorize = toolkit.createButton(sectionToolbar, "", SWT.PUSH); //$NON-NLS-1$
		btnHistorize.setToolTipText(Messages.ViewServerInfo_9);
		btnHistorize.setLayoutData(new GridData());
		btnHistorize.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getSite().getShell();
				if (Activator.getDefault().getServerType() == null) {
					MessageDialog.openInformation(sh, Messages.ActionHistorize_0, Messages.ActionHistorize_1);
					return;
				}
				try {
					Activator.getDefault().getRemoteServerManager().historize();
					MessageDialog.openInformation(sh, Messages.ViewServerInfo_12, Messages.ViewServerInfo_13);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ActionHistorize_2, ex.getMessage());
				}
			}
		});
		btnHistorize.setImage(Activator.getDefault().getImageRegistry().get(Icons.HISTORIZE));

		createServerInfoSection();

		createBaseServerConfigSection();

		createServerConfigSection();

		refreshNodes();
		updateButton(null);
	}

	public Point getSize() {
		return form.getParent().getSize();
	}

	private void createBaseServerConfigSection() {
		configComposite = new ConfigComposite(toolkit, form.getForm(), this);
		this.configChangeListener = configComposite;
	}

	private void createServerConfigSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION | Section.TWISTIE | Section.EXPANDED);
		section.setText(Messages.ViewServerInfo_0);
		section.setDescription(Messages.ViewServerInfo_1);
		section.setLayout(new GridLayout());
		section.setExpanded(false);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL));

		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(2, true));
		section.setClient(composite);

		viewerConfig = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		viewerConfig.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				ServerConfigInfo c = ((ServerConfigInfo) inputElement);
				List l = c.getPropertiesShortNames();
				return l.toArray(new Object[l.size()]);
			}
		});
		viewerConfig.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewerConfig.getTable().setLinesVisible(true);
		viewerConfig.getTable().setHeaderVisible(true);
		viewerConfig.addFilter(new ViewerFilter() {
			private final String[] baseProps = new String[] { "generationFolder", "birtReportEngineLogs", "imageUri", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					"homeFolder", "tempFolder", //$NON-NLS-1$ //$NON-NLS-2$
					"maximumRunningTasks", "reportPoolSize", "historizationfolder" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			};

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				for (String s : baseProps) {
					if (s.equals(element)) {
						return false;
					}
				}
				return true;
			}
		});

		TableViewerColumn cName = new TableViewerColumn(viewerConfig, SWT.NONE);
		cName.getColumn().setWidth(100);
		cName.getColumn().setText(Messages.ViewServerInfo_10);
		cName.setLabelProvider(new ColumnLabelProvider());

		TableViewerColumn cVal = new TableViewerColumn(viewerConfig, SWT.NONE);
		cVal.getColumn().setWidth(100);
		cVal.getColumn().setText(Messages.ViewServerInfo_11);
		cVal.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				String v = ((ServerConfigInfo) viewerConfig.getInput()).getValue((String) element);

				return v == null ? "" : v; //$NON-NLS-1$
			}

		});
		cVal.setEditingSupport(new EditingSupport(viewerConfig) {
			TextCellEditor editor = new TextCellEditor(viewerConfig.getTable());

			@Override
			protected void setValue(Object element, Object value) {
				ServerConfigInfo cfg = (ServerConfigInfo) viewerConfig.getInput();
				cfg.setValue((String) element, (String) value);
				resetConfig.setEnabled(true);
				cancel.setEnabled(true);
				viewerConfig.refresh();

			}

			@Override
			protected Object getValue(Object element) {
				String v = ((ServerConfigInfo) viewerConfig.getInput()).getValue((String) element);

				return v == null ? "" : v; //$NON-NLS-1$
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		resetConfig = toolkit.createButton(composite, Messages.ViewServerInfo_14, SWT.PUSH);
		resetConfig.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		resetConfig.setEnabled(false);
		resetConfig.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (Activator.getDefault().getServerType() == null) {
						Activator.getDefault().getRemoteServerManager().resetServerConfig((ServerConfigInfo) viewerConfig.getInput());
						viewerConfig.setInput(Activator.getDefault().getRemoteServerManager().getServerConfig());
					}
					cancel.setEnabled(false);
					resetConfig.setEnabled(false);
					configChangeListener.propertyChange(new PropertyChangeEvent(this, "config", null, viewerConfig.getInput())); //$NON-NLS-1$
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewServerInfo_16, Messages.ViewServerInfo_17 + e1.getMessage());
				}
			}

		});

		cancel = toolkit.createButton(composite, Messages.ViewServerInfo_18, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setEnabled(false);
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (Activator.getDefault().getServerType() == null) {
						viewerConfig.setInput(Activator.getDefault().getRemoteServerManager().getServerConfig());
					}
					cancel.setEnabled(false);
					resetConfig.setEnabled(false);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewServerInfo_19, Messages.ViewServerInfo_20 + e1.getMessage());
				}
			}

		});
	}

	private void createServerInfoSection() {
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION | Section.TWISTIE | Section.EXPANDED);
		section.setText(Messages.ViewServerInfo_21);
		section.setDescription(Messages.ViewServerInfo_22);
		section.setLayout(new GridLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL));

		Composite composite = toolkit.createComposite(section);
		composite.setLayout(new GridLayout(3, false));
		section.setClient(composite);

		Label l = toolkit.createLabel(composite, Messages.ViewServerInfo_23);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		runtimeNodeViewer = new ComboViewer(new CCombo(composite, SWT.READ_ONLY | SWT.BORDER));
		runtimeNodeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		runtimeNodeViewer.setContentProvider(new ArrayContentProvider());
		runtimeNodeViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Server) {
					return ((Server) element).getName();
				}
				return super.getText(element);
			}
		});
		runtimeNodeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				String serverUrl = (String) ((IStructuredSelection) runtimeNodeViewer.getSelection()).getFirstElement();
				List<Server> l = mapServerByUrl.get(serverUrl);
				try {
					componentTypeViewer.setInput(ViewServerInfoHelper.getRunningComponentTypes(l));
					Activator.getDefault().setServerUrl(null, null);
					serverState.setText(""); //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewServerInfo_25, Messages.ViewServerInfo_26 + serverUrl + " : " + ex.getMessage());//$NON-NLS-1$
				}

			}
		});

		toolkit.createLabel(composite, Messages.ViewServerInfo_28);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		componentTypeViewer = new ComboViewer(new CCombo(composite, SWT.READ_ONLY | SWT.BORDER));
		componentTypeViewer.setContentProvider(new ArrayContentProvider());
		componentTypeViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ServerType) element).getTypeName();
			}

		});
		componentTypeViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		componentTypeViewer.setSelection(new StructuredSelection(ServerType.REPORTING));
		componentTypeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				ServerType type = (ServerType) ((IStructuredSelection) componentTypeViewer.getSelection()).getFirstElement();
				String serverKey = (String) ((IStructuredSelection) runtimeNodeViewer.getSelection()).getFirstElement();

				Server server = null;

				for (Server s : mapServerByUrl.get(serverKey)) {
					if (type == ServerType.REPORTING && VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature())) {
						server = s;
						break;
					}
					else if (type == ServerType.GATEWAY && VanillaComponentType.COMPONENT_GATEWAY.equals(s.getComponentNature())) {
						server = s;
						break;
					}
					else if (type == ServerType.WORKFLOW && VanillaComponentType.COMPONENT_WORKFLOW.equals(s.getComponentNature())) {
						server = s;
						break;
					}
				}

				if (server != null) {
					Activator.getDefault().setServerUrl(type, server.getUrl());
					try {
						fillConfig(server);

						updateButton(server);
					} catch (Exception e1) {
						Activator.getDefault().setServerUrl(null, null);
						e1.printStackTrace();
						MessageDialog.openInformation(getSite().getShell(), Messages.ViewServerInfo_31, e1.getMessage());
					}
				}

				getViewSite().getActionBars().getMenuManager().updateAll(true);
			}
		});

		l = toolkit.createLabel(composite, Messages.ViewServerInfo_32);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		serverState = toolkit.createText(composite, ""); //$NON-NLS-1$
		serverState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
		serverState.setEditable(false);

		l = toolkit.createLabel(composite, Messages.ViewServerInfo_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		repositoryViewer = new ComboViewer(new CCombo(composite, SWT.READ_ONLY | SWT.BORDER));
		repositoryViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repositoryViewer.setContentProvider(new ArrayContentProvider());
		repositoryViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Repository) {
					return ((Repository) element).getName();
				}
				return super.getText(element);
			}
		});
		repositoryViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Repository selectedRepository = (Repository) ((IStructuredSelection) repositoryViewer.getSelection()).getFirstElement();
				Activator.getDefault().setSelectedRepository(selectedRepository);
			}
		});

		Composite bBar = toolkit.createComposite(composite);
		bBar.setLayout(new GridLayout(2, true));
		bBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		startButton = toolkit.createButton(bBar, Messages.ViewServerInfo_34, SWT.PUSH);
		startButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		startButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IRunnableWithProgress r = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							monitor.beginTask(Messages.ViewServerInfo_35, 2);

							ServerType type = (ServerType) ((IStructuredSelection) componentTypeViewer.getSelection()).getFirstElement();
							String serverKey = (String) ((IStructuredSelection) runtimeNodeViewer.getSelection()).getFirstElement();

							Server server = null;

							for (Server s : mapServerByUrl.get(serverKey)) {
								if (type == ServerType.REPORTING && VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature())) {
									server = s;
									break;
								}
								else if (type == ServerType.GATEWAY && VanillaComponentType.COMPONENT_GATEWAY.equals(s.getComponentNature())) {
									server = s;
									break;
								}
								else if (type == ServerType.WORKFLOW && VanillaComponentType.COMPONENT_WORKFLOW.equals(s.getComponentNature())) {
									server = s;
									break;
								}
							}

							Activator.getDefault().getVanillaApi().getVanillaSystemManager().startNodeComponent(server);

							try {
								Thread.sleep(1500);
							} catch (Exception ex) {

							}

							for (Server serv : Activator.getDefault().getRuntimeNodes()) {
								if (serv.getName().equals(server.getName())) {
									mapServerByUrl.get(serverKey).remove(server);
									mapServerByUrl.get(serverKey).add(serv);

									server = serv;
									break;
								}
							}

							updateButton(server);
							monitor.worked(1);
						} catch (Exception ex) {
							throw new InvocationTargetException(ex);
						}

					}
				};

				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				try {
					service.run(false, false, r);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewServerInfo_38, Messages.ViewServerInfo_39 + ex.getMessage());
				}

			}

		});
		startButton.setEnabled(false);

		stopButton = toolkit.createButton(bBar, Messages.ViewServerInfo_40, SWT.PUSH);
		stopButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		stopButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IRunnableWithProgress r = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							monitor.beginTask(Messages.ViewServerInfo_41, 2);

							ServerType type = (ServerType) ((IStructuredSelection) componentTypeViewer.getSelection()).getFirstElement();
							String serverKey = (String) ((IStructuredSelection) runtimeNodeViewer.getSelection()).getFirstElement();

							Server server = null;

							for (Server s : mapServerByUrl.get(serverKey)) {
								if (type == ServerType.REPORTING && VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature())) {
									server = s;
									break;
								}
								else if (type == ServerType.GATEWAY && VanillaComponentType.COMPONENT_GATEWAY.equals(s.getComponentNature())) {
									server = s;
									break;
								}
								else if (type == ServerType.WORKFLOW && VanillaComponentType.COMPONENT_WORKFLOW.equals(s.getComponentNature())) {
									server = s;
									break;
								}
							}

							Activator.getDefault().getVanillaApi().getVanillaSystemManager().stopNodeComponent(server);

							try {
								Thread.sleep(1500);
							} catch (Exception ex) {

							}

							boolean serverFound = false;
							for (Server serv : Activator.getDefault().getRuntimeNodes()) {
								if (serv.getName().equals(server.getName())) {
									mapServerByUrl.get(serverKey).remove(server);
									mapServerByUrl.get(serverKey).add(serv);

									server = serv;
									serverFound = true;
									break;
								}
							}

							if (serverFound) {
								updateButton(server);
							}
							else {
								updateButton(null);
							}
							monitor.worked(1);
						} catch (Exception ex) {
							throw new InvocationTargetException(ex);
						}

					}
				};

				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				try {

					service.run(false, false, r);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewServerInfo_44, Messages.ViewServerInfo_45 + ex.getMessage());
				}

			}

		});
		stopButton.setEnabled(false);
	}

	private void refreshNodes() {
		mapServerByUrl.clear();

		for (Server s : Activator.getDefault().getRuntimeNodes()) {
			String key = null;
			if (s.getUrl().equals(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext().getVanillaUrl())) {
				key = Messages.ViewServerInfo_47;
			}
			else {
				key = s.getUrl();
			}
			if (mapServerByUrl.get(key) == null) {
				mapServerByUrl.put(key, new ArrayList<Server>());
			}
			// ere, added to filter out non activer servers
			if (s.getComponentStatus().equalsIgnoreCase(Status.STARTED.getStatus()) || s.getComponentStatus().equalsIgnoreCase(Status.STOPPED.getStatus()) || s.getComponentStatus().equalsIgnoreCase(Status.STARTING.getStatus()) || s.getComponentStatus().equalsIgnoreCase(Status.STOPPING.getStatus()) || s.getComponentStatus().equalsIgnoreCase(Status.ERROR.getStatus()) || s.getComponentStatus().equalsIgnoreCase(Status.UNDEFINED.getStatus())) {
				mapServerByUrl.get(key).add(s);
			}
			else {
				// dont add
			}
		}

		runtimeNodeViewer.setInput(mapServerByUrl.keySet());

		if (mapServerByUrl != null && !mapServerByUrl.isEmpty()) {
			String firstKey = ""; //$NON-NLS-1$
			for (String key : mapServerByUrl.keySet()) {
				firstKey = key;
				break;
			}

			if (!firstKey.equals("")) { //$NON-NLS-1$
				runtimeNodeViewer.setSelection(new StructuredSelection(firstKey));
			}
		}

		try {
			List<Repository> repositories = Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories();
			repositoryViewer.setInput(repositories);

			if (repositories != null && !repositories.isEmpty()) {
				repositoryViewer.setSelection(new StructuredSelection(repositories.get(0)));
				Activator.getDefault().setSelectedRepository(repositories.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		updateButton(null);
	}

	private void updateButton(Server server) {
		ServerType cl = Activator.getDefault().getServerType();
		IVanillaServerManager remoteServer = cl != null && server != null ? Activator.getDefault().getRemoteServerManager() : null;

		if ((cl != null && (cl == ServerType.GATEWAY || cl == ServerType.REPORTING)) && (server != null && server.getComponentStatus().equals(Status.STARTED.getStatus()))) {
			btnServMemory.setEnabled(true);
			btnHistorize.setEnabled(true);
			btnRunTask.setEnabled(true);
			btnTaskManager.setEnabled(true);
		}
		else if ((cl != null && cl == ServerType.WORKFLOW) && (server != null && server.getComponentStatus().equals(Status.STARTED.getStatus()))) {
			btnServMemory.setEnabled(true);
			btnHistorize.setEnabled(false);
			btnRunTask.setEnabled(true);
			btnTaskManager.setEnabled(true);
		}
		else {
			btnServMemory.setEnabled(false);
			btnHistorize.setEnabled(false);
			btnRunTask.setEnabled(false);
			btnTaskManager.setEnabled(false);
			stopButton.setEnabled(false);
			startButton.setEnabled(false);
		}

		if (server != null) {
			try {
				if (server.getComponentStatus().equals(Status.STARTED.getStatus()) && cl != null && remoteServer.isStarted()) {
					serverState.setText(Messages.ViewServerInfo_29);
					stopButton.setEnabled(true);
					startButton.setEnabled(false);
				}
				else if (server.getComponentStatus().equals(Status.STARTING.getStatus())) {
					serverState.setText(Messages.ViewServerInfo_33);
					stopButton.setEnabled(false);
					startButton.setEnabled(false);
				}
				else if (server.getComponentStatus().equals(Status.STOPPING.getStatus())) {
					serverState.setText(Messages.ViewServerInfo_49);
					stopButton.setEnabled(false);
					startButton.setEnabled(false);
				}
				else if (server.getComponentStatus().equals(Status.ERROR.getStatus())) {
					serverState.setText(Messages.ViewServerInfo_50);
					stopButton.setEnabled(false);
					startButton.setEnabled(true);
				}
				else if (server.getComponentStatus().equals(Status.UNDEFINED.getStatus())) {
					serverState.setText(Messages.ViewServerInfo_51);
					stopButton.setEnabled(false);
					startButton.setEnabled(false);
				}
				else {
					serverState.setText(Messages.ViewServerInfo_30);
					stopButton.setEnabled(false);
					startButton.setEnabled(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFocus() {
		if (runtimeNodeViewer.getInput() == null) {
		}
	}

	private void fillConfig(Server server) {
		try {
			if (server != null && server.getComponentStatus().equals(Status.STARTED.getStatus()) && Activator.getDefault().getServerType() != null) {
				ServerConfigInfo conf = Activator.getDefault().getRemoteServerManager().getServerConfig();
				viewerConfig.setInput(conf);

				configComposite.setInput(Activator.getDefault().getServerType(), conf);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// TODO : empty config
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getNewValue() instanceof ServerConfigInfo) {
			viewerConfig.setInput((ServerConfigInfo) event.getNewValue());
		}

	}
}
