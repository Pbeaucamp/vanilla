package bpm.es.datasource.analyzer.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import bpm.es.datasource.analyzer.parsers.IAnalyzer;
import bpm.es.datasource.analyzer.parsers.IPattern;
import bpm.es.datasource.analyzer.parsers.ModelXmlLoader;
import bpm.es.datasource.analyzer.parsers.PatternDataSource;
import bpm.es.datasource.analyzer.parsers.PatternTable;
import bpm.es.datasource.analyzer.ui.Activator;
import bpm.es.datasource.analyzer.ui.Messages;
import bpm.es.datasource.analyzer.ui.icons.IconsNames;
import bpm.gateway.core.server.database.DBSchema;
import bpm.gateway.core.server.database.DBTable;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.icons.IconsRegistry;

public class ViewFilter extends ViewPart {

	public static final String ID = "bpm.es.datasource.analyzer.ui.views.ViewFilter"; //$NON-NLS-1$
	private ComboViewer dataSources;
	private Text dataSourceUrl;
	private CheckboxTreeViewer tables;
	private Button big, biw, fmdt, fasd, dico, birt;
	private Button go, useTableFilter, showDependantItems;

	private DataBaseConnection sock;

	public ViewFilter() {}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		b.setText(Messages.ViewFilter_1);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					dataSources.setInput(adminbirep.Activator.getDefault().getRepositoryApi().getImpactDetectionService().getDatasourcesByType(DataSource.DATASOURCE_JDBC));
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.ViewFilter_2);

		dataSources = new ComboViewer(main, SWT.READ_ONLY);
		dataSources.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			public void dispose() {}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}
		});
		dataSources.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((DataSource) element).getName();
			}
		});
		dataSources.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSources.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				DataSource ds = (DataSource) ((IStructuredSelection) dataSources.getSelection()).getFirstElement();
				String datasourceUrl = ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_URL);
				String user = ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_USER);
				String password = ds.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_PASS);
				
				dataSourceUrl.setText(datasourceUrl != null ? datasourceUrl : ""); //$NON-NLS-1$

				sock = new DataBaseConnection();
				sock.setFullUrl(datasourceUrl != null ? datasourceUrl : ""); //$NON-NLS-1$
				sock.setLogin(user != null ? user : ""); //$NON-NLS-1$
				sock.setPassword(password != null ? password : ""); //$NON-NLS-1$
				sock.setUseFullUrl(true);

				try {

					for(String s : JdbcConnectionProvider.getListDriver().getDriversName()) {
						if(sock.getFullUrl().startsWith(JdbcConnectionProvider.getListDriver().getInfo(s).getUrlPrefix())) {
							sock.setDriverName(s);
							break;
						}
					}
					final List schemas = new ArrayList();
					BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
						public void run() {
							try {
								schemas.addAll(DataBaseHelper.getDataBaseSchemas(sock, null));
								if(schemas.isEmpty()) {
									schemas.addAll(DataBaseHelper.getSchemasTables(sock, null, null));
								}
							} catch(Exception ex) {
								ex.printStackTrace();
							}

						}
					});

					tables.setInput(schemas);

				} catch(Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewFilter_3, ex.getMessage());

				}
			}
		});

		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.ViewFilter_4);

		dataSourceUrl = new Text(main, SWT.BORDER);
		dataSourceUrl.setEnabled(false);
		dataSourceUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		createFilter(main);

		go = new Button(main, SWT.PUSH);
		go.setEnabled(false);
		go.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		go.setText(Messages.ViewFilter_5);
		go.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IPattern p = null;
				try {
					if(useTableFilter.getSelection()) {
						List<String> tableName = new ArrayList<String>();
						for(Object o : tables.getCheckedElements()) {
							if(o instanceof DBTable) {
								tableName.add(((DBTable) o).getFullName());
							}
						}
						p = new PatternTable((DataSource) ((IStructuredSelection) dataSources.getSelection()).getFirstElement(), tableName);
					}
					else {
						p = new PatternDataSource((DataSource) ((IStructuredSelection) dataSources.getSelection()).getFirstElement());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				Activator.getDefault().setAnalyzers(getTypeToAnalyze());

				final List<IAnalyzer> analyzers = Activator.getDefault().getAnalyzers();

				final IPattern pattern = p;

				final List<RepositoryItem> sources = new ArrayList<RepositoryItem>();

				Repository rep = null;

				ModelXmlLoader _loader = null;
				try {
					_loader = new ModelXmlLoader(adminbirep.Activator.getDefault().getRepositoryApi(), new Repository(adminbirep.Activator.getDefault().getRepositoryApi()));
					rep = ((Repository) _loader.getRepositoryContent());
				} catch(Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewFilter_6, e1.getMessage());
					return;
				}

				final HashMap<IAnalyzer, Integer> sizes = new HashMap<IAnalyzer, Integer>();
				try {
					for(RepositoryItem it : rep.getAllItems()) {
						for(IAnalyzer a : analyzers) {

							if(it.getType() == a.getObjectType()) {
								sources.add(it);
								if(sizes.get(a) == null) {
									sizes.put(a, 1);
								}
								else {
									sizes.put(a, sizes.get(a) + 1);
								}
								break;
							}
						}

					}
				} catch(Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewFilter_6, e1.getMessage());
					return;
				}

				final List<RepositoryItem> items = new ArrayList<RepositoryItem>();
				final ModelXmlLoader loader = _loader;
				IRunnableWithProgress r = new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.setTaskName(Messages.ViewFilter_7);

						for(IAnalyzer analyzer : analyzers) {
							int i = 0;
							monitor.beginTask(Messages.ViewFilter_8 + analyzer.getObjectTypeName(), sizes.get(analyzer));

							for(RepositoryItem it : sources) {
								if(it.getType() == analyzer.getObjectType()) {
									try {
										monitor.subTask(Messages.ViewFilter_9 + it.getItemName() + "...");//$NON-NLS-1$
										String xml = loader.loadXml(it);
										monitor.subTask(Messages.ViewFilter_11 + it.getItemName() + "...");//$NON-NLS-1$
										if(analyzer.match(xml, pattern)) {
											items.add(it);
										}
										monitor.worked(++i);
									} catch(Exception ex) {
										ex.printStackTrace();
									}
								}

							}

						}

					}

				};

				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				try {
					service.run(true, false, r);
				} catch(InvocationTargetException ex) {} catch(InterruptedException ex) {}

				AnalysisView v = (AnalysisView) adminbirep.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("bpm.es.datasource.analyzer.ui.views.AnalysisView"); //$NON-NLS-1$
				v.setInput(items, showDependantItems.getSelection());

				ViewHierarchical v1 = (ViewHierarchical) adminbirep.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewHierarchical.ID);

				DataSource ds = (DataSource) ((IStructuredSelection) dataSources.getSelection()).getFirstElement();
				v1.setInput(ds, items, showDependantItems.getSelection());

			}
		});

		createTableFilter(main);
	}

	@Override
	public void setFocus() {}

	private void createFilter(Composite parent) {
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				go.setEnabled(getTypeToAnalyze().length > 0);
			}
		};
		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		g.setText(Messages.ViewFilter_14);

		big = new Button(g, SWT.CHECK);
		big.setImage(bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry().get(IconsRegistry.GTW));
		big.setLayoutData(new GridData());
		big.setText("BiGateway"); //$NON-NLS-1$
		big.addSelectionListener(adapter);

		fasd = new Button(g, SWT.CHECK);
		fasd.setLayoutData(new GridData());
		fasd.setImage(bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry().get(IconsRegistry.FASD));
		fasd.setText("FreeAnalysisSchema"); //$NON-NLS-1$
		fasd.addSelectionListener(adapter);

		dico = new Button(g, SWT.CHECK);
		dico.setLayoutData(new GridData());
		dico.setImage(bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry().get(IconsRegistry.DICO));
		dico.setText("FreeDashboard Dictionary"); //$NON-NLS-1$
		dico.addSelectionListener(adapter);

		fmdt = new Button(g, SWT.CHECK);
		fmdt.setLayoutData(new GridData());
		fmdt.setImage(bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry().get(IconsRegistry.FMDT));
		fmdt.setText("FreeMetaData"); //$NON-NLS-1$
		fmdt.addSelectionListener(adapter);

		biw = new Button(g, SWT.CHECK);
		biw.setLayoutData(new GridData());
		biw.setImage(bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry().get(IconsRegistry.BIW));
		biw.setText("BIWorkflow"); //$NON-NLS-1$
		biw.addSelectionListener(adapter);

		birt = new Button(g, SWT.CHECK);
		birt.setLayoutData(new GridData());
		birt.setImage(bpm.vanilla.repository.ui.Activator.getDefault().getImageRegistry().get(IconsRegistry.BIRT));
		birt.setText("Birt"); //$NON-NLS-1$
		birt.addSelectionListener(adapter);
	}

	private void createTableFilter(Composite parent) {
		showDependantItems = new Button(parent, SWT.CHECK);
		showDependantItems.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		showDependantItems.setText(Messages.ViewFilter_21);

		useTableFilter = new Button(parent, SWT.CHECK);
		useTableFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		useTableFilter.setText(Messages.ViewFilter_22);
		useTableFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tables.getControl().setEnabled(useTableFilter.getSelection());
			}
		});

		final Button checkAll = new Button(parent, SWT.TOGGLE);
		checkAll.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		checkAll.setText(Messages.ViewFilter_23);
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!checkAll.getSelection()) {
					tables.setCheckedElements(new Object[] {});
				}
				else {
					List l = new ArrayList();
					ITreeContentProvider tcp = (ITreeContentProvider) tables.getContentProvider();
					for(Object o : tcp.getElements(tables.getInput())) {
						l.add(o);

						if(tcp.hasChildren(o)) {
							for(Object c : tcp.getChildren(o)) {
								l.add(c);
							}
						}
					}
					tables.setCheckedElements(l.toArray(new Object[l.size()]));
				}
			}
		});

		tables = new CheckboxTreeViewer(parent, SWT.BORDER);
		tables.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tables.getControl().setEnabled(false);
		tables.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof DBSchema) {
					return ((DBSchema) element).getName();
				}
				else if(element instanceof DBTable) {
					return ((DBTable) element).getName();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				ImageRegistry reg = Activator.getDefault().getImageRegistry();
				if(element instanceof DBSchema) {
					return reg.get(IconsNames.DB_SCHEMA);
				}
				else if(element instanceof DBTable) {
					return reg.get(IconsNames.DB_TABLE);
				}
				return super.getImage(element);
			}
		});
		tables.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				if(ss.isEmpty()) {
					return;
				}
				if(ss.getFirstElement() instanceof DBSchema) {
					DBSchema schema = (DBSchema) ss.getFirstElement();
					if(schema.isTableLoaded()) {
						return;
					}
					else {
						try {
							DataBaseHelper.getSchemasTables(sock, schema, null);
							tables.refresh();
						} catch(Exception e) {
							e.printStackTrace();
							MessageDialog.openError(getSite().getShell(), "Problem when getting Schema'a tables", e.getMessage()); //$NON-NLS-1$
						}
					}
				}

			}
		});

		tables.setContentProvider(new ITreeContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public boolean hasChildren(Object element) {
				if(element instanceof DBSchema) {
					if(((DBSchema) element).isTableLoaded()) {
						return !((DBSchema) element).getTables().isEmpty();
					}
				}
				return false;
			}

			public Object getParent(Object element) {
				if(element instanceof DBTable) {
					return ((DBTable) element).getDBSchema();
				}
				return null;
			}

			public Object[] getChildren(Object parentElement) {
				if(parentElement instanceof DBSchema) {
					List l = ((DBSchema) parentElement).getTables();
					return l.toArray(new Object[l.size()]);
				}
				return null;
			}
		});
	}

	private Point[] getTypeToAnalyze() {
		List<Point> l = new ArrayList<Point>();

		if(big.getSelection()) {
			l.add(new Point(IRepositoryApi.GTW_TYPE, 0));
		}
		if(biw.getSelection()) {
			l.add(new Point(IRepositoryApi.BIW_TYPE, 0));
		}
		if(dico.getSelection()) {
			l.add(new Point(IRepositoryApi.FD_DICO_TYPE, 0));
		}

		if(fmdt.getSelection()) {
			l.add(new Point(IRepositoryApi.FMDT_TYPE, 0));
		}
		if(fasd.getSelection()) {
			l.add(new Point(IRepositoryApi.FASD_TYPE, 0));
		}
		if(birt.getSelection()) {
			l.add(new Point(IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE));
		}

		return l.toArray(new Point[l.size()]);
	}
}
