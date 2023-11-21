package bpm.vanilla.server.client.ui.clustering.menu.uolap.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.designer.ui.common.dialogs.DialogGroupPickers;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.UOlapPreloadBean;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.dialogs.DialogStatistics;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers.GroupDialogCellEditor;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers.QueryLogsLabelProvider;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers.SchemaViewerLabelProvider;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers.QueryLogsLabelProvider.Column;

public class ManagementView extends ViewPart {
	// locker to avoid to set selection from a viewer to another in a loop
	private boolean synchronizing = false;
	private IRepositoryContext repositoryContext;
	private ComboViewer repViewer;

	private List<SchemaViewerLabelProvider> columnLabelProviders = new ArrayList<SchemaViewerLabelProvider>();

	private static class QueryLogContentProvider implements ITreeContentProvider {

		private HashMap<String, List<UOlapQueryBean>> logs = null;

		@Override
		@SuppressWarnings("unchecked")
		public Object[] getChildren(Object parentElement) {
			List l = logs.get(parentElement);
			return l.toArray(new Object[l.size()]);
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof String) {
				return true;
			}
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			logs = (HashMap<String, List<UOlapQueryBean>>) inputElement;
			Object[] keys = logs.keySet().toArray(new Object[logs.keySet().size()]);
			return keys;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private static class QueryPreloadContentProvider implements ITreeContentProvider {

		private HashMap<String, List<UOlapPreloadBean>> logs = null;

		@Override
		@SuppressWarnings("unchecked")
		public Object[] getChildren(Object parentElement) {
			List l = logs.get(parentElement);
			return l.toArray(new Object[l.size()]);
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof String) {
				return true;
			}
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			logs = (HashMap<String, List<UOlapPreloadBean>>) inputElement;
			Object[] keys = logs.keySet().toArray(new Object[logs.keySet().size()]);
			return keys;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private TableViewer schemaViewer;
	private TreeViewer queryLogs;
	private TreeViewer preloadQueries;

	public ManagementView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		Form frmUnitedolapModelsManagement = formToolkit.createForm(parent);
		frmUnitedolapModelsManagement.setImage(Activator.getDefault().getImageRegistry().get(Icons.MDX_QUERY_MGMT));
		formToolkit.paintBordersFor(frmUnitedolapModelsManagement);
		frmUnitedolapModelsManagement.setText(Messages.ManagementView_0);
		frmUnitedolapModelsManagement.getBody().setLayout(new GridLayout(1, false));
		formToolkit.decorateFormHeading(frmUnitedolapModelsManagement);

		Composite composite = new Composite(frmUnitedolapModelsManagement.getBody(), SWT.NONE);
		composite.setLayout(new GridLayout(3, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);

		Section sctnRepositorySection = formToolkit.createSection(composite, Section.DESCRIPTION | Section.TITLE_BAR);
		sctnRepositorySection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sctnRepositorySection.setText(Messages.ManagementView_1);
		sctnRepositorySection.setExpanded(true);
		sctnRepositorySection.setDescription(Messages.ManagementView_2);

		formToolkit.paintBordersFor(sctnRepositorySection);

		Composite compositeRepository = formToolkit.createComposite(sctnRepositorySection, SWT.NONE);

		ToolBar repositoryToolbar = createRepositoryToolbar(compositeRepository);
		repositoryToolbar.setBackground(formToolkit.getColors().getBackground());
		repositoryToolbar.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 2));

		Label l = formToolkit.createLabel(compositeRepository, Messages.ManagementView_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		repViewer = new ComboViewer(compositeRepository, SWT.READ_ONLY);
		repViewer.setContentProvider(new ArrayContentProvider());
		repViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((bpm.vanilla.platform.core.beans.Repository) element).getName();
			}
		});
		repViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		repViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setRepositoryContext((IStructuredSelection) event.getSelection());

			}
		});

		formToolkit.adapt(compositeRepository);
		formToolkit.paintBordersFor(compositeRepository);
		compositeRepository.setLayout(new GridLayout(3, false));

		schemaViewer = new TableViewer(compositeRepository, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = schemaViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		formToolkit.paintBordersFor(table);
		try {
			sctnRepositorySection.setClient(compositeRepository);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		final TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		// Current MDX
		Composite compositeQuery = new Composite(tabFolder, SWT.NONE);
		compositeQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		formToolkit.adapt(compositeQuery);
		formToolkit.paintBordersFor(compositeQuery);
		compositeQuery.setLayout(new GridLayout(1, false));

		queryLogs = new TreeViewer(compositeQuery, SWT.BORDER | SWT.FULL_SELECTION);
		queryLogs.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		formToolkit.paintBordersFor(queryLogs.getTree());

		TabItem histoMdx = new TabItem(tabFolder, SWT.NONE);
		histoMdx.setText(Messages.ManagementView_5);
		histoMdx.setControl(compositeQuery);

		// Pre Loaded MDX
		Composite compositePreload = new Composite(tabFolder, SWT.NONE);
		compositePreload.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ToolBar preloadToolbar = createPreloadedToolbar(compositePreload);
		preloadToolbar.setBackground(formToolkit.getColors().getBackground());
		preloadToolbar.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));

		formToolkit.adapt(compositePreload);
		formToolkit.paintBordersFor(compositePreload);
		compositePreload.setLayout(new GridLayout(1, false));

		preloadQueries = new TreeViewer(compositePreload, SWT.BORDER | SWT.FULL_SELECTION);
		preloadQueries.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		formToolkit.paintBordersFor(table);

		TabItem preMdx = new TabItem(tabFolder, SWT.NONE);
		preMdx.setText(Messages.ManagementView_4);
		preMdx.setControl(compositePreload);

		createViewers();
	}

	private ToolBar createPreloadedToolbar(Composite parent) {
		Action preload = new Action("") { //$NON-NLS-1$
			public void run() {
				if (schemaViewer.getSelection().isEmpty()) {
					MessageDialog.openInformation(getSite().getShell(), Messages.ManagementView_14, Messages.ManagementView_15);
					return;
				}
				RepositoryItem it = (RepositoryItem) ((IStructuredSelection) schemaViewer.getSelection()).getFirstElement();
				ObjectIdentifier identifier = new ObjectIdentifier(repositoryContext.getRepository().getId(), it.getId());

				Preloader run = new Preloader(identifier);

				IProgressService service = PlatformUI.getWorkbench().getProgressService();

				try {
					service.run(false, false, run);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				MessageDialog.openInformation(getSite().getShell(), Messages.ManagementView_16, Messages.ManagementView_17 + run.total + Messages.ManagementView_18 + run.expected);

			}
		};
		preload.setToolTipText(Messages.ManagementView_19);
		preload.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.PRELOAD_QUERY_16));

		ToolBarManager mgr = new ToolBarManager(SWT.FLAT | SWT.VERTICAL);
		mgr.add(preload);

		return mgr.createControl(parent);
	}

	private ToolBar createRepositoryToolbar(Composite parent) {

		Action connect = new Action("") { //$NON-NLS-1$
			public void run() {

				if (Activator.getDefault().getVanillaContext() == null) {
					MessageDialog.openInformation(getSite().getShell(), Messages.ManagementView_8, Messages.ManagementView_9);
					return;
				}

				try {
					repViewer.setInput(Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories());
				} catch (Exception ex) {
					ex.printStackTrace();
					repViewer.setInput(Collections.EMPTY_LIST);
					MessageDialog.openError(getSite().getShell(), Messages.ManagementView_10, Messages.ManagementView_11 + ex.getMessage());
				}

			}
		};
		connect.setToolTipText(Messages.ManagementView_12);
		connect.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.RESET));

		ToolBarManager mgr = new ToolBarManager(SWT.FLAT | SWT.VERTICAL);
		mgr.add(connect);

		return mgr.createControl(parent);
	}

	private void fillViewers() {
		if (repositoryContext == null) {
			schemaViewer.setInput(Collections.EMPTY_LIST);
			queryLogs.setInput(new HashMap<String, List<UOlapQueryBean>>());
			return;
		}
		final IRepositoryApi sock = new RemoteRepositoryApi(repositoryContext);

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

			@Override
			public void run() {
				try {
					List<RepositoryItem> l = new ArrayList<RepositoryItem>();

					for (RepositoryItem it : new Repository(sock, IRepositoryApi.FASD_TYPE).getAllItems()) {
						l.add(it);
					}

					schemaViewer.setInput(l);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ManagementView_20, Messages.ManagementView_21 + ex.getMessage());
					schemaViewer.setInput(Collections.EMPTY_LIST);
					queryLogs.setInput(new HashMap<String, List<UOlapQueryBean>>());
					return;
				}
			}
		});
	}

	private void createViewers() {
		schemaViewer.setContentProvider(new ArrayContentProvider());
		schemaViewer.getTable().setHeaderVisible(true);
		schemaViewer.getTable().setLinesVisible(true);
		schemaViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (schemaViewer.getSelection().isEmpty()) {
					queryLogs.setInput(new HashMap<String, List<UOlapQueryBean>>());
				}
				else {
					final RepositoryItem it = (RepositoryItem) ((IStructuredSelection) schemaViewer.getSelection()).getFirstElement();

					final ObjectIdentifier id = new ObjectIdentifier(repositoryContext.getRepository().getId(), it.getId());

					BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
						public void run() {
							try {
								HashMap<String, List<UOlapQueryBean>> map = new HashMap<String, List<UOlapQueryBean>>();

								for (UOlapQueryBean bean : Activator.getDefault().getVanillaApi().getVanillaLoggingManager().getUolapQueries(id)) {

									if (map.get(bean.getMdxQuery()) == null) {
										map.put(bean.getMdxQuery(), new ArrayList<UOlapQueryBean>());
									}
									map.get(bean.getMdxQuery()).add(bean);
								}

								queryLogs.setInput(map);
							} catch (Exception ex) {
								ex.printStackTrace();
								MessageDialog.openError(getSite().getShell(), Messages.ManagementView_22, Messages.ManagementView_23 + ex.getMessage());
								queryLogs.setInput(new HashMap<String, List<UOlapQueryBean>>());
							}
						}
					});

				}
				refreshPreloadQueries();

				preloadQueries.setSelection(StructuredSelection.EMPTY);

			}
		});

		TableViewerColumn col = new TableViewerColumn(schemaViewer, SWT.NONE);
		col.getColumn().setText(Messages.ManagementView_24);
		col.getColumn().setWidth(200);
		SchemaViewerLabelProvider lbProv = new SchemaViewerLabelProvider(SchemaViewerLabelProvider.Column.Path);
		col.setLabelProvider(lbProv);
		columnLabelProviders.add(lbProv);

		col = new TableViewerColumn(schemaViewer, SWT.NONE);
		col.getColumn().setText(Messages.ManagementView_25);
		col.getColumn().setWidth(50);

		lbProv = new SchemaViewerLabelProvider(SchemaViewerLabelProvider.Column.State);
		col.setLabelProvider(lbProv);
		columnLabelProviders.add(lbProv);

		/*
		 * create preloadViewer
		 */
		preloadQueries.getTree().setHeaderVisible(true);
		preloadQueries.setContentProvider(new QueryPreloadContentProvider());
		preloadQueries.getTree().setLinesVisible(true);
		preloadQueries.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object mdxRequest = ((IStructuredSelection)preloadQueries.getSelection()).getFirstElement();
				if(mdxRequest instanceof String) {
					MessageDialog.openConfirm(getSite().getShell(), Messages.ManagementView_7, (String) mdxRequest);
				}
			}
		});
		preloadQueries.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!synchronizing) {
					synchronizing = true;
				}
				else {
					return;
				}
				try {
					queryLogs.setSelection(new StructuredSelection(((IStructuredSelection) preloadQueries.getSelection()).getFirstElement()));
				} catch (Exception ex) {
					queryLogs.setSelection(StructuredSelection.EMPTY);
				}
				synchronizing = false;
			}
		});

		TreeViewerColumn tcol = new TreeViewerColumn(preloadQueries, SWT.LEFT);
		tcol.getColumn().setText(Messages.ManagementView_26);
		tcol.getColumn().setWidth(420);
		tcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UOlapPreloadBean) {
					return ""; //$NON-NLS-1$
				}
				return super.getText(element).replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});

		tcol = new TreeViewerColumn(preloadQueries, SWT.LEFT);
		tcol.getColumn().setText(Messages.ManagementView_30);
		tcol.getColumn().setWidth(150);
		tcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UOlapPreloadBean) {

					return QueryLogsLabelProvider.getGroupName(((UOlapPreloadBean) element).getVanillaGroupId());
				}
				return ""; //$NON-NLS-1$
			}
		});

		tcol.setEditingSupport(new EditingSupport(preloadQueries) {
			GroupDialogCellEditor editor = new GroupDialogCellEditor(preloadQueries.getTree());

			@Override
			@SuppressWarnings("unchecked")
			protected void setValue(Object element, Object value) {

				RepositoryItem it = (RepositoryItem) ((IStructuredSelection) schemaViewer.getSelection()).getFirstElement();

				HashMap<String, List<UOlapPreloadBean>> map = (HashMap<String, List<UOlapPreloadBean>>) preloadQueries.getInput();
				for (UOlapPreloadBean bean : map.get(element)) {
					boolean found = false;
					for (Group g : (List<Group>) value) {
						if (g.getId().intValue() == bean.getVanillaGroupId()) {
							found = true;
							break;
						}
					}

					if (!found) {
						try {
							Activator.getDefault().getVanillaApi().getUnitedOlapPreloadManager().removePreload(bean);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				for (Group g : (List<Group>) value) {
					boolean found = false;
					for (UOlapPreloadBean bean : map.get(element)) {
						if (g.getId() == bean.getVanillaGroupId()) {
							found = true;
							break;
						}

					}
					if (!found) {

						UOlapPreloadBean bean = new UOlapPreloadBean();
						bean.setDirectoryItemId(it.getId());
						bean.setMdxQuery((String) element);
						bean.setRepositoryId(repositoryContext.getRepository().getId());
						bean.setVanillaGroupId(g.getId());

						try {
							Activator.getDefault().getVanillaApi().getUnitedOlapPreloadManager().addPreload(bean);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				refreshPreloadQueries();
			}

			@Override
			@SuppressWarnings("unchecked")
			protected Object getValue(Object element) {

				if (element instanceof String) {
					HashMap<String, List<UOlapPreloadBean>> map = (HashMap<String, List<UOlapPreloadBean>>) preloadQueries.getInput();

					List<Group> groups = new ArrayList<Group>();
					for (UOlapPreloadBean bean : map.get(element)) {
						groups.add(QueryLogsLabelProvider.getGroup(bean.getVanillaGroupId()));
					}

					return groups;
				}
				return null;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (element instanceof String) {
					return editor;
				}
				return null;
			}

			@Override
			protected boolean canEdit(Object element) {
				if (element instanceof String) {
					return true;
				}
				return false;
			}
		});

		/*
		 * create queryLogs
		 */
		queryLogs.setContentProvider(new QueryLogContentProvider());
		queryLogs.getTree().setHeaderVisible(true);
		queryLogs.getTree().setLinesVisible(true);
		queryLogs.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				final Object mdxRequest = ((IStructuredSelection)queryLogs.getSelection()).getFirstElement();
				if(mdxRequest instanceof String) {
					MessageDialog.openInformation(getSite().getShell(), Messages.ManagementView_13, (String) mdxRequest);
				}
			}
		});

		final Menu menu = new Menu(queryLogs.getTree());
		queryLogs.getTree().setMenu(menu);
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].dispose();
				}

				final Object dragedObject = ((IStructuredSelection)queryLogs.getSelection()).getFirstElement();
				if(dragedObject instanceof String) {
				
					MenuItem itemPre = new MenuItem(menu, SWT.NONE);
					itemPre.setText(Messages.ManagementView_27);
					itemPre.addSelectionListener(new SelectionListener() {
	
						@Override
						public void widgetSelected(SelectionEvent e) {
							String query = (String) dragedObject;
							
							RepositoryItem item = (RepositoryItem) ((IStructuredSelection) schemaViewer.getSelection()).getFirstElement();
	
							DialogGroupPickers d = new DialogGroupPickers(getSite().getShell(), Activator.getDefault().getVanillaApi());
	
							if (d.open() == DialogGroupPickers.OK) {
	
								for (Group g : d.getGroups()) {
									UOlapPreloadBean bean = new UOlapPreloadBean();
									bean.setVanillaGroupId(g.getId());
									bean.setMdxQuery(query);
									bean.setDirectoryItemId(item.getId());
									bean.setRepositoryId(repositoryContext.getRepository().getId());
	
									try {
										Activator.getDefault().getVanillaApi().getUnitedOlapPreloadManager().addPreload(bean);
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
								refreshPreloadQueries();
							}
						}
	
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});
	
					MenuItem itemStats = new MenuItem(menu, SWT.NONE);
					itemStats.setText(Messages.ManagementView_28);
					itemStats.addSelectionListener(new SelectionListener() {
	
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (!synchronizing) {
								synchronizing = true;
							}
							else {
								return;
							}
	
							IStructuredSelection ss = (IStructuredSelection) queryLogs.getSelection();
							HashMap<String, List<UOlapQueryBean>> map = (HashMap<String, List<UOlapQueryBean>>) queryLogs.getInput();
							try {
								if (ss.getFirstElement() instanceof String) {
									DialogStatistics dial = new DialogStatistics(getSite().getShell(), map.get(ss.getFirstElement()));
									dial.open();
								}
								else {
									DialogStatistics dial = new DialogStatistics(getSite().getShell(), map.get(((UOlapQueryBean) ss.getFirstElement()).getMdxQuery()));
									dial.open();
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
	
							try {
								preloadQueries.setSelection(new StructuredSelection(((IStructuredSelection) queryLogs.getSelection()).getFirstElement()));
							} catch (Exception ex) {
								preloadQueries.setSelection(StructuredSelection.EMPTY);
							}
							synchronizing = false;
						}
	
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});
				}
			}
		});

		tcol = new TreeViewerColumn(queryLogs, SWT.LEFT);
		tcol.getColumn().setText(Messages.ManagementView_32);
		tcol.getColumn().setWidth(360);
		tcol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UOlapQueryBean) {
					return ""; //$NON-NLS-1$
				}
				return super.getText(element).replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});

		tcol = new TreeViewerColumn(queryLogs, SWT.LEFT);
		tcol.getColumn().setText(Messages.ManagementView_36);
		tcol.getColumn().setWidth(120);
		tcol.setLabelProvider(new QueryLogsLabelProvider(Column.Date, queryLogs));

		tcol = new TreeViewerColumn(queryLogs, SWT.LEFT);
		tcol.getColumn().setText(Messages.ManagementView_37);
		tcol.getColumn().setWidth(50);
		tcol.setLabelProvider(new QueryLogsLabelProvider(Column.Time, queryLogs));

		tcol = new TreeViewerColumn(queryLogs, SWT.LEFT);
		tcol.getColumn().setText(Messages.ManagementView_38);
		tcol.getColumn().setWidth(50);
		tcol.setLabelProvider(new QueryLogsLabelProvider(Column.Group, queryLogs));

	}

	@Override
	public void setFocus() {
		IActionBars bars = getViewSite().getActionBars();

		if (Activator.getDefault().getVanillaContext() == null) {
			bars.getStatusLineManager().setMessage(Messages.ManagementView_39);
		}
		else {
			if (repositoryContext == null) {
				bars.getStatusLineManager().setMessage(Messages.ManagementView_40 + Activator.getDefault().getVanillaContext().getVanillaUrl() + Messages.ManagementView_41);
				return;
			}
			StringBuilder s = new StringBuilder();

			s.append(Messages.ManagementView_42);
			s.append(repositoryContext.getVanillaContext().getVanillaUrl());
			s.append(Messages.ManagementView_43);
			s.append(repositoryContext.getRepository().getName());
			bars.getStatusLineManager().setMessage(s.toString());
		}

	}

	private void refreshPreloadQueries() {
		HashMap<String, List<UOlapPreloadBean>> map = new HashMap<String, List<UOlapPreloadBean>>();
		if (!schemaViewer.getSelection().isEmpty()) {
			try {
				RepositoryItem it = (RepositoryItem) ((IStructuredSelection) schemaViewer.getSelection()).getFirstElement();
				ObjectIdentifier id = new ObjectIdentifier(repositoryContext.getRepository().getId(), it.getId());
				List<UOlapPreloadBean> l = Activator.getDefault().getVanillaApi().getUnitedOlapPreloadManager().getPreloadForIdentifier(id);

				for (UOlapPreloadBean bean : l) {
					if (map.get(bean.getMdxQuery()) == null) {
						map.put(bean.getMdxQuery(), new ArrayList<UOlapPreloadBean>());
					}
					map.get(bean.getMdxQuery()).add(bean);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openWarning(getSite().getShell(), Messages.ManagementView_47, Messages.ManagementView_48 + ex.getMessage());

			}

		}

		Object[] xp = preloadQueries.getExpandedElements();
		preloadQueries.setInput(map);
		preloadQueries.setExpandedElements(xp);

	}

	private class Preloader implements IRunnableWithProgress {
		ObjectIdentifier identifier;
		int total = 0;
		int expected = 0;

		Preloader(ObjectIdentifier identifier) {
			this.identifier = identifier;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask(Messages.ManagementView_49, 10);
			List<UOlapPreloadBean> beans = null;
			try {
				monitor.subTask(Messages.ManagementView_50);
				beans = Activator.getDefault().getVanillaApi().getUnitedOlapPreloadManager().getPreloadForIdentifier(identifier);
				monitor.worked(2);
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(getSite().getShell(), Messages.ManagementView_51, Messages.ManagementView_52 + ex.getMessage());
				return;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {

			}
			expected = beans.size();
			for (UOlapPreloadBean o : beans) {
				monitor.subTask(Messages.ManagementView_53 + o.getVanillaGroupId() + " :" + o.getMdxQuery());//$NON-NLS-1$
				RemoteServiceProvider remote = new RemoteServiceProvider();
				remote.configure(Activator.getDefault().getVanillaContext());
				Group g = null;
				try {
					g = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(o.getVanillaGroupId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				IRuntimeContext ctx = new RuntimeContext(Activator.getDefault().getVanillaContext().getLogin(), Activator.getDefault().getVanillaContext().getPassword(), g.getName(), g.getId());

				// extract cubeName from query
				try {

					Schema sh = remote.getModelProvider().getSchema(identifier, ctx);
					String schemaId = null;
					if (sh == null) {
						schemaId = remote.getModelProvider().loadSchema(identifier, ctx);
					}
					else {
						schemaId = sh.getId();
					}
					remote.getRuntimeProvider().preload(o.getMdxQuery(), schemaId, o.extractCubeName(), ctx);
					total++;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				monitor.worked(1);

				try {
					Thread.sleep(1000);
				} catch (Exception ex) {

				}
			}

		}

	}

	private void setRepositoryContext(IStructuredSelection ss) {
		if (ss.isEmpty()) {
			repositoryContext = null;

		}
		else {
			bpm.vanilla.platform.core.beans.Repository rep = (bpm.vanilla.platform.core.beans.Repository) ss.getFirstElement();
			Group dummy = new Group();
			dummy.setId(-1);
			repositoryContext = new BaseRepositoryContext(Activator.getDefault().getVanillaContext(), dummy, rep);

		}
		for (SchemaViewerLabelProvider l : columnLabelProviders) {
			l.setRepositoryContext(repositoryContext);
		}
		fillViewers();
	}
}
