package bpm.vanilla.server.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskList;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.icons.IconsRegistry;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.internal.TaskListManager;
import bpm.vanilla.server.ui.wizard.TaskDataWizard;
import bpm.vanilla.server.ui.wizard.list.ListWizard;

public class DialogListManager extends Dialog {

	private class PropertiesContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object inputElement) {
			Properties c = (Properties) inputElement;

			List<String> l = new ArrayList<String>();
			Enumeration e = c.propertyNames();
			while (e.hasMoreElements()) {
				l.add((String) e.nextElement());
			}

			return l.toArray(new Object[l.size()]);
		}
	};

	private TreeViewer managerViewer;
	private TableViewer propertiesViewer;
	private TableViewer parameterViewer;

	private Button runSelectedList;
	private String vanillaUrl;

	private ToolItem createList, deleteList;

	public DialogListManager(Shell parentShell, String vanillaUrl) {
		super(parentShell);
		this.vanillaUrl = vanillaUrl;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lbl = new Label(main, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		lbl.setText(Messages.DialogListManager_4);

		createToolbar(main);

		buildManagerViewer(main);
		createMenu();

		TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		buildProperties(folder);
		buildParameters(folder);
		return main;
	}

	private void createToolbar(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));

		createList = new ToolItem(bar, SWT.PUSH);
		createList.setToolTipText(Messages.DialogListManager_0);
		createList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ListWizard wiz = new ListWizard();
				WizardDialog dial = new WizardDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);

				dial.open();
				managerViewer.refresh();
			}

		});
		createList.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));

		deleteList = new ToolItem(bar, SWT.PUSH);
		deleteList.setToolTipText(Messages.DialogListManager_1);
		deleteList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((IStructuredSelection) managerViewer.getSelection()).getFirstElement();

				if (o instanceof TaskList) {
					((TaskListManager) managerViewer.getInput()).removeList((TaskList) o);
				}

				managerViewer.refresh();
				try {
					Activator.getDefault().getTaskListManager().save();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		deleteList.setImage(Activator.getDefault().getImageRegistry().get(Icons.DELETE));
		deleteList.setEnabled(false);
	}

	private void buildManagerViewer(Composite main) {

		managerViewer = new TreeViewer(main, SWT.BORDER);
		managerViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof TaskList) {
					return ((TaskList) element).getName();
				}
				if (element instanceof IRuntimeConfig) {
					try {
						IVanillaContext ctx = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext();
						IVanillaAPI api = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi();
						Repository rep = api.getVanillaRepositoryManager().getRepositoryById(((IRuntimeConfig) element).getObjectIdentifier().getRepositoryId());

						Group group = new Group();
						group.setId(-1);
						IRepositoryContext repCtx = new BaseRepositoryContext(ctx, group, rep);
						IRepositoryApi sock = new RemoteRepositoryApi(repCtx);

						RepositoryItem it = sock.getRepositoryService().getDirectoryItem(((IRuntimeConfig) element).getObjectIdentifier().getDirectoryItemId());
						return it.getItemName();
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
				return super.getText(element);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object
			 * )
			 */
			@Override
			public Image getImage(Object element) {
				ImageRegistry reg = bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry();
				if (element instanceof TaskList) {
					return reg.get(IconsRegistry.FOLDER);
				}
				return super.getImage(element);
			}

		});
		managerViewer.setContentProvider(new ITreeContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			public void dispose() { }

			public Object[] getElements(Object inputElement) {
				TaskListManager mgr = (TaskListManager) inputElement;
				List l = mgr.getLists();
				return l.toArray(new Object[l.size()]);
			}

			public boolean hasChildren(Object element) {
				if (element instanceof TaskList) {
					return ((TaskList) element).getTasks().size() > 0;
				}
				return false;
			}

			public Object getParent(Object element) {
				if (element instanceof TaskList) {
					return null;
				}
				return null;
			}

			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof TaskList) {
					List l = ((TaskList) parentElement).getTasks();
					return l.toArray(new Object[l.size()]);
				}
				return null;
			}
		});
		managerViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		managerViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				deleteList.setEnabled(!managerViewer.getSelection().isEmpty());
				if (managerViewer.getSelection().isEmpty()) {
					runSelectedList.setEnabled(false);
					return;
				}
				runSelectedList.setEnabled(true);
				
				Object object = ((StructuredSelection)managerViewer.getSelection()).getFirstElement();
				if(object instanceof ReportRuntimeConfig) {
					ReportRuntimeConfig runtimeConfig = (ReportRuntimeConfig)object;
					
					String group = Messages.DialogListManager_5;
					try {
						group = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(runtimeConfig.getVanillaGroupId()).getName();
					} catch (Exception e) {
						e.printStackTrace();
					}

					Properties props = new Properties();
					props.put(Messages.DialogListManager_9, runtimeConfig.getOutputFormat());
					props.put(Messages.DialogListManager_13, group);
					
					propertiesViewer.setInput(props);

					Properties paramProps = new Properties();
					
					if(runtimeConfig.getParametersValues() != null) {
						for (VanillaGroupParameter grp : runtimeConfig.getParametersValues()) {
							if(grp.getParameters() != null) {
								for(VanillaParameter param : grp.getParameters()) {
									paramProps.put(param.getName(), param.getSelectedValues() != null && !param.getSelectedValues().isEmpty() ? param.getSelectedValues().get(0) : ""); //$NON-NLS-1$
								}
							}
						}
					}
					
					parameterViewer.setInput(paramProps);
				}
			}
		});

		managerViewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				ServerType type = Activator.getDefault().getServerType();

				if (element instanceof TaskList) {
					return type == ((TaskList) element).getType();
				}
				return true;

			}
		});
	}

	private void buildProperties(TabFolder folder) {
		TabItem parent = new TabItem(folder, SWT.NONE);
		parent.setText(Messages.DialogListManager_6);
		
		propertiesViewer = new TableViewer(folder, SWT.BORDER | SWT.FULL_SELECTION);
		propertiesViewer.getTable().setLinesVisible(true);
		propertiesViewer.getTable().setHeaderVisible(true);
		propertiesViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		propertiesViewer.setContentProvider(new PropertiesContentProvider());

		TableViewerColumn pName = new TableViewerColumn(propertiesViewer, SWT.NONE);
		pName.getColumn().setText(Messages.DialogListManager_7);
		pName.getColumn().setWidth(200);
		pName.setLabelProvider(new ColumnLabelProvider());

		TableViewerColumn pValue = new TableViewerColumn(propertiesViewer, SWT.NONE);
		pValue.getColumn().setText(Messages.DialogListManager_8);
		pValue.getColumn().setWidth(200);
		pValue.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Properties) propertiesViewer.getInput()).getProperty((String) element);
			}
		});
//		pValue.setEditingSupport(new EditingSupport(propertiesViewer) {
//
//			ComboBoxCellEditor priority = new ComboBoxCellEditor(propertiesViewer.getTable(), 
//					new String[] { TaskPriority.LOW_PRIORITY.getLabel(), TaskPriority.NORMAL_PRIORITY.getLabel(), TaskPriority.HIGH_PRIORITY.getLabel() });
//
//			@Override
//			protected void setValue(Object element, Object value) {
//
//				((Properties) propertiesViewer.getInput()).setProperty((String) element, priority.getItems()[(Integer) value]);
//				propertiesViewer.refresh();
//
//			}
//
//			@Override
//			protected Object getValue(Object element) {
//				String k = ((Properties) propertiesViewer.getInput()).getProperty((String) element);
//				if (k == null) {
//					return -1;
//				}
//				for (int i = 0; i < priority.getItems().length; i++) {
//					if (k.equals(priority.getItems()[i])) {
//						return i;
//					}
//				}
//				return -1;
//			}
//
//			@Override
//			protected CellEditor getCellEditor(Object element) {
//				return priority;
//			}
//
//			@Override
//			protected boolean canEdit(Object element) {
//				if ("taskPriority".equals(element)) { //$NON-NLS-1$
//					return true;
//				}
//				return false;
//			}
//		});

		parent.setControl(propertiesViewer.getControl());
	}

	private void buildParameters(TabFolder parent) {
		TabItem tab = new TabItem(parent, SWT.NONE);
		tab.setText(Messages.DialogListManager_6);
		
		parameterViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		parameterViewer.getTable().setLinesVisible(true);
		parameterViewer.getTable().setHeaderVisible(true);
		parameterViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		parameterViewer.setContentProvider(new PropertiesContentProvider());

		TableViewerColumn pName = new TableViewerColumn(parameterViewer, SWT.NONE);
		pName.getColumn().setText(Messages.DialogListManager_11);
		pName.getColumn().setWidth(200);
		pName.setLabelProvider(new ColumnLabelProvider());

		TableViewerColumn pValue = new TableViewerColumn(parameterViewer, SWT.NONE);
		pValue.getColumn().setText(Messages.DialogListManager_12);
		pValue.getColumn().setWidth(200);
		pValue.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Properties) parameterViewer.getInput()).getProperty((String) element);
			}
		});
//		pValue.setEditingSupport(new EditingSupport(parameterViewer) {
//			TextCellEditor editor = new TextCellEditor(parameterViewer.getTable());
//
//			@Override
//			protected void setValue(Object element, Object value) {
//				((Properties) parameterViewer.getInput()).setProperty((String) element, (String) value);
//				parameterViewer.refresh();
//
//			}
//
//			@Override
//			protected Object getValue(Object element) {
//				String k = ((Properties) parameterViewer.getInput()).getProperty((String) element);
//				if (k == null) {
//					return ""; //$NON-NLS-1$
//				}
//				return k;
//			}
//
//			@Override
//			protected CellEditor getCellEditor(Object element) {
//				return editor;
//			}
//
//			@Override
//			protected boolean canEdit(Object element) {
//				return true;
//			}
//		});

		tab.setControl(parameterViewer.getControl());
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogListManager_20);
		managerViewer.setInput(Activator.getDefault().getTaskListManager());
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setLayout(new GridLayout(2, true));
		Button b = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}

		});

		runSelectedList = new Button(parent, SWT.PUSH);
		runSelectedList.setText(Messages.DialogListManager_15);
		runSelectedList.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		runSelectedList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final TaskList tl = (TaskList) ((IStructuredSelection) managerViewer.getSelection()).getFirstElement();
				final DialogGroupPickers d = new DialogGroupPickers(getShell(), vanillaUrl);

				if (d.open() != DialogGroupPickers.OK) {
					return;
				}
				IRunnableWithProgress r = new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						monitor.setTaskName(Messages.DialogListManager_16);

						for (String s : d.getGroups()) {
							monitor.beginTask(Messages.DialogListManager_17 + s, tl.getTasks().size());
							int counter = 0;
							for (IRuntimeConfig runtimeConfig : tl.getTasks()) {

								if (tl.getType() == ServerType.GATEWAY) {
									try {
										IVanillaAPI api = new RemoteVanillaPlatform(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext());
										Group g = api.getVanillaSecurityManager().getGroupByName(s);
										((GatewayRuntimeConfiguration) runtimeConfig).setVanillaGroupId(g.getId());
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								else {
									try {
										IVanillaAPI api = new RemoteVanillaPlatform(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext());
										Group g = api.getVanillaSecurityManager().getGroupByName(s);
										((ReportRuntimeConfig) runtimeConfig).setVanillaGroupId(g.getId());
									} catch (Exception ex) {
										ex.printStackTrace();
									}

								}

								monitor.subTask(Messages.DialogListManager_18);

								try {
									User user = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getUser();
									if (tl.getType() == ServerType.GATEWAY) {
										((GatewayComponent)Activator.getDefault().getRemoteServerManager()).runGatewayAsynch((GatewayRuntimeConfiguration)runtimeConfig, user);
									}
									else if(tl.getType() == ServerType.REPORTING) {
										((ReportingComponent)Activator.getDefault().getRemoteServerManager()).runReportAsynch((ReportRuntimeConfig)runtimeConfig, user);
									}
									else if(tl.getType() == ServerType.WORKFLOW) {
										((WorkflowService)Activator.getDefault().getRemoteServerManager()).startWorkflowAsync((RuntimeConfiguration)runtimeConfig);
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								monitor.worked(++counter);
							}

						}

					}

				};

				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				try {
					service.run(true, false, r);

				} catch (InvocationTargetException ex) {
					ex.printStackTrace();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}

			}

		});
		runSelectedList.setEnabled(false);
	}

	private void createMenu() {
		MenuManager mgr = new MenuManager();

		final Action removeTask = new Action("Remove Task From List") { //$NON-NLS-1$
			public void run() {
				// TODO Remove task
				managerViewer.refresh();
				try {
					Activator.getDefault().getTaskListManager().save();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		removeTask.setText(Messages.DialogListManager_21);

		final Action addTask = new Action("Add Task To List") { //$NON-NLS-1$
			public void run() {
				TaskList l = (TaskList) ((IStructuredSelection) managerViewer.getSelection()).getFirstElement();
				TaskDataWizard wiz = new TaskDataWizard(l);

				WizardDialog d = new WizardDialog(getShell(), wiz);
				d.open();
				managerViewer.refresh();

				try {
					Activator.getDefault().getTaskListManager().save();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		addTask.setText(Messages.DialogListManager_23);
		mgr.add(addTask);
		mgr.add(removeTask);

		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				if (managerViewer.getSelection().isEmpty()) {
					removeTask.setEnabled(false);
					addTask.setEnabled(false);
				}
				else {
					Object o = ((IStructuredSelection) managerViewer.getSelection()).getFirstElement();
					if (o instanceof TaskList) {
						removeTask.setEnabled(false);
						addTask.setEnabled(true);
					}
					else {
						removeTask.setEnabled(false);
						addTask.setEnabled(false);
					}
				}

			}
		});

		managerViewer.getTree().setMenu(mgr.createContextMenu(managerViewer.getControl()));
	}
}
