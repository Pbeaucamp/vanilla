package bpm.vanilla.server.client.ui.clustering.menu.stress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeConfiguration;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryItemPicker;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;

public class StressView extends ViewPart {
	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.stress.StressView"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	private CheckboxTableViewer configsViewer;
	private ComboViewer repositoryViewer;
	private ComboViewer clusterViewer, groupViewer;
	private ComboViewer componentViewer;
	private TableViewer resultViewer;
	private RepositoryItem directoryItem;
	private ChartComposite chartComposite;

	private Text text, runNumber;
	private Button run;

//	private Job runningJob;
	private int numberOfRun;

	public StressView() {}

	@Override
	public void createPartControl(Composite parent) {

		Form frmClusterProfiling = formToolkit.createForm(parent);
		formToolkit.paintBordersFor(frmClusterProfiling);
		frmClusterProfiling.setImage(Activator.getDefault().getImageRegistry().get(Icons.STRESS));
		formToolkit.decorateFormHeading(frmClusterProfiling);
		frmClusterProfiling.setText(Messages.VanillaView_0);
		frmClusterProfiling.getBody().setLayout(new GridLayout(1, false));

		Composite composite = new Composite(frmClusterProfiling.getBody(), SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);

		/*
		 * section conf
		 */
		Section sctnConfiguration = formToolkit.createSection(composite, Section.TWISTIE | Section.TITLE_BAR);
		sctnConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sctnConfiguration.setBounds(0, 0, 98, 21);
		formToolkit.paintBordersFor(sctnConfiguration);
		sctnConfiguration.setText(Messages.StressView_2);
		sctnConfiguration.setExpanded(true);

		Composite composite_1 = new Composite(sctnConfiguration, SWT.NONE);
		formToolkit.adapt(composite_1);
		formToolkit.paintBordersFor(composite_1);
		sctnConfiguration.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, true));
		formToolkit.paintBordersFor(sctnConfiguration);

		/*
		 * section result
		 */

		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 2));
		formToolkit.adapt(composite_2);
		formToolkit.paintBordersFor(composite_2);
		composite_2.setLayout(new GridLayout(3, false));
		Label lblCluster = formToolkit.createLabel(composite_2, Messages.StressView_3, SWT.NONE);
		lblCluster.setBounds(0, 0, 55, 15);

		clusterViewer = new ComboViewer(composite_2, SWT.READ_ONLY);
		clusterViewer.setContentProvider(new ArrayContentProvider());
		clusterViewer.setLabelProvider(new LabelProvider());
		clusterViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection().isEmpty()) {
					componentViewer.setInput(Collections.EMPTY_LIST);
				}
				else {
					IStructuredSelection ss = (IStructuredSelection) event.getSelection();
					componentViewer.setInput(mapServerByUrl.get(ss.getFirstElement()));
				}
				updateRunBtn();
			}
		});

		Combo combo_1 = clusterViewer.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo_1.setBounds(0, 0, 91, 23);
		formToolkit.paintBordersFor(combo_1);

		Button b = formToolkit.createButton(composite_2, "", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadClusters();
				updateRunBtn();
			}
		});
		Label lblComponentRuntime = formToolkit.createLabel(composite_2, Messages.StressView_5, SWT.NONE);
		lblComponentRuntime.setBounds(0, 0, 55, 15);

		componentViewer = new ComboViewer(composite_2, SWT.READ_ONLY);
		componentViewer.setContentProvider(new ArrayContentProvider());
		componentViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {

				return ((Server) element).getName();
			}
		});
		componentViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateRunBtn();

			}
		});

		combo_1 = componentViewer.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		combo_1.setBounds(0, 0, 91, 23);
		formToolkit.paintBordersFor(combo_1);
		Label lblRepository = formToolkit.createLabel(composite_2, Messages.StressView_6, SWT.NONE);
		lblRepository.setBounds(0, 0, 55, 15);

		repositoryViewer = new ComboViewer(composite_2, SWT.READ_ONLY);
		repositoryViewer.setContentProvider(new ArrayContentProvider());
		repositoryViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Repository) element).getName();
			}
		});
		repositoryViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateRunBtn();

			}
		});
		repositoryViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		b = formToolkit.createButton(composite_2, "", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 2));
		b.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IObservableList l = null;
				try {
					l = new WritableList(Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories(), Repository.class);
				} catch (Exception ex) {
					ex.printStackTrace();
					l = new WritableList();
					MessageDialog.openError(getSite().getShell(), Messages.StressView_8, Messages.StressView_9 + ex.getMessage());
				}
				repositoryViewer.setInput(l);
				

				try {
					l = new WritableList(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(), Group.class);
				} catch (Exception ex) {
					ex.printStackTrace();
					l = new WritableList();
					MessageDialog.openError(getSite().getShell(), Messages.StressView_7, Messages.StressView_13 + ex.getMessage());
				}
				groupViewer.setInput(l);
				
				updateRunBtn();
			}

		});

		Label lblGroup = formToolkit.createLabel(composite_2, Messages.StressView_10, SWT.NONE);
		lblGroup.setBounds(0, 0, 55, 15);

		groupViewer = new ComboViewer(composite_2, SWT.READ_ONLY);
		groupViewer.setContentProvider(new ArrayContentProvider());
		groupViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
		groupViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateRunBtn();

			}
		});

//		b = formToolkit.createButton(composite_2, "", SWT.PUSH); //$NON-NLS-1$
//		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
//		b.setImage(Activator.getDefault().getImageRegistry().get(Icons.RESET));
//		b.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				IObservableList l = null;
//				try {
//					l = new WritableList(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(), Group.class);
//				} catch (Exception ex) {
//					ex.printStackTrace();
//					l = new WritableList();
//					MessageDialog.openError(getSite().getShell(), Messages.StressView_12, Messages.StressView_13 + ex.getMessage());
//				}
//				groupViewer.setInput(l);
//				updateRunBtn();
//			}
//
//		});

		Combo combo = groupViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.setBounds(0, 0, 91, 23);
		formToolkit.paintBordersFor(combo);

		Label lblItemToRun = formToolkit.createLabel(composite_2, Messages.StressView_14, SWT.NONE);
		lblItemToRun.setBounds(0, 0, 55, 15);

		text = formToolkit.createText(composite_2, "", SWT.NONE); //$NON-NLS-1$
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setEditable(false);

		b = formToolkit.createButton(composite_2, "...", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) repositoryViewer.getSelection();
				if (ss.isEmpty()) {
					updateRunBtn();
					return;
				}
				Repository rep = (Repository) ss.getFirstElement();

				Server s = (Server) ((IStructuredSelection) componentViewer.getSelection()).getFirstElement();

				int[] type = null;
				int[] subtype = null;
				if(s.getComponentNature().equals(VanillaComponentType.COMPONENT_GATEWAY)) {
					type = new int[] { IRepositoryApi.GTW_TYPE };
				}
				else {
					type = new int[] { IRepositoryApi.FWR_TYPE, IRepositoryApi.CUST_TYPE };
					subtype = new int[] { IRepositoryApi.BIRT_REPORT_SUBTYPE, IRepositoryApi.JASPER_REPORT_SUBTYPE };
				}

				Group group = new Group();
				group.setId(-1);

				IRepositoryContext ctx = new BaseRepositoryContext(Activator.getDefault().getVanillaContext(), group, rep);

				IRepositoryApi sock = new RemoteRepositoryApi(ctx);
				DialogDirectoryItemPicker d = new DialogDirectoryItemPicker(getSite().getShell(), sock, type, subtype);

				if (d.open() == DialogDirectoryItemPicker.OK) {
					directoryItem = d.getDirectoryItem();
					text.setText(directoryItem.getItemName());
				}
				else {
					directoryItem = null;
					text.setText(""); //$NON-NLS-1$
				}

				updateRunBtn();
			}
		});

		Label lblNumToRun = formToolkit.createLabel(composite_2, Messages.StressView_19, SWT.NONE);
		lblNumToRun.setBounds(0, 0, 55, 15);

		runNumber = formToolkit.createText(composite_2, "5", SWT.NONE); //$NON-NLS-1$
		runNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		runNumber.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateRunBtn();
			}
		});

		Section sctnResult = formToolkit.createSection(composite, Section.TWISTIE | Section.TITLE_BAR);
		sctnResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		sctnResult.setBounds(0, 0, 98, 21);
		formToolkit.paintBordersFor(sctnConfiguration);
		sctnResult.setText(Messages.StressView_21);
		sctnResult.setExpanded(false);
		sctnResult.setLayout(new GridLayout(1, false));

		Composite composite_3 = new Composite(sctnResult, SWT.NONE);
		composite_3.setLayoutData(new GridData(GridData.FILL_BOTH));
		sctnResult.setClient(composite_3);
		formToolkit.adapt(composite_3);
		formToolkit.paintBordersFor(composite_3);
		composite_3.setLayout(new GridLayout(2, true));

		run = formToolkit.createButton(composite_3, Messages.StressView_22, SWT.PUSH);
		run.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		run.setEnabled(false);
		run.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				launch();
			}

		});

//		showJob = formToolkit.createButton(composite_3, Messages.StressView_0, SWT.PUSH);
//		showJob.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
//		showJob.setEnabled(false);
//		showJob.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				IProgressService service = PlatformUI.getWorkbench().getProgressService();
//				if(runningJob != null && runningJob.getState() == Job.RUNNING) {
//					service.showInDialog(getSite().getShell(), runningJob);
//				}
//
//			}
//
//		});

		CTabFolder folder = new CTabFolder(composite_3, SWT.BOTTOM);
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		folder.setLayout(new GridLayout());

		CTabItem it = new CTabItem(folder, SWT.NONE);
		it.setText(Messages.StressView_23);

		resultViewer = new TableViewer(formToolkit.createTable(folder, SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL));
		resultViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		createResultTable();
		it.setControl(resultViewer.getTable());

		it = new CTabItem(folder, SWT.NONE);
		it.setText(Messages.StressView_24);
		chartComposite = new ChartComposite(folder, SWT.NONE);
		it.setControl(chartComposite);
		folder.setSelection(0);
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();

		/*
		 * item
		 */

		ToolBar toolBar = new ToolBar(composite_1, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolBar.setBounds(0, 0, 89, 23);
		formToolkit.adapt(toolBar);
		formToolkit.paintBordersFor(toolBar);

		ToolItem tltmNew = new ToolItem(toolBar, SWT.NONE);
		tltmNew.setText(Messages.StressView_25);
		tltmNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((List) configsViewer.getInput()).add(new ServerConfigurationProps(5, 5));
			}
		});

		ToolItem tltmDel = new ToolItem(toolBar, SWT.NONE);
		tltmDel.setText(Messages.StressView_26);
		tltmDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				((List) configsViewer.getInput()).removeAll(((IStructuredSelection) configsViewer.getSelection()).toList());
			}
		});

		ToolItem tltmCheckall = new ToolItem(toolBar, SWT.NONE);
		tltmCheckall.setText(Messages.StressView_27);
		tltmCheckall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List l = ((List) configsViewer.getInput());
				configsViewer.setCheckedElements(l.toArray(new Object[l.size()]));
				updateRunBtn();
			}
		});

		ToolItem tltmUncheckall = new ToolItem(toolBar, SWT.NONE);
		tltmUncheckall.setText(Messages.StressView_28);
		tltmUncheckall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				configsViewer.setCheckedElements(new Object[] {});
				updateRunBtn();
			}
		});

		configsViewer = CheckboxTableViewer.newCheckList(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		configsViewer.getTable().setHeaderVisible(true);
		configsViewer.getTable().setLinesVisible(true);
		configsViewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateRunBtn();

			}
		});

		contentProvider = new ObservableListContentProvider();

		configsViewer.setContentProvider(contentProvider);

		Table table = configsViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(table);

		TableViewerColumn col = new TableViewerColumn(configsViewer, SWT.NONE);
		col.getColumn().setText(Messages.StressView_29);
		col.getColumn().setWidth(250);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ServerConfigurationProps) element).numberTasks + ""; //$NON-NLS-1$
			}
		});

		col.setEditingSupport(new EditingSupport(configsViewer) {
			TextCellEditor editor = new TextCellEditor(configsViewer.getTable());

			@Override
			protected void setValue(Object element, Object value) {
				try {
					((ServerConfigurationProps) element).numberTasks = Integer.parseInt((String) value);
					configsViewer.refresh();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			protected Object getValue(Object element) {
				return ((ServerConfigurationProps) element).numberTasks + ""; //$NON-NLS-1$
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

		col = new TableViewerColumn(configsViewer, SWT.NONE);
		col.getColumn().setText(Messages.StressView_32);
		col.getColumn().setWidth(200);
		col.setEditingSupport(new EditingSupport(configsViewer) {
			TextCellEditor editor = new TextCellEditor(configsViewer.getTable());

			@Override
			protected void setValue(Object element, Object value) {
				try {
					((ServerConfigurationProps) element).numberRepConnection = Integer.parseInt((String) value);
					configsViewer.refresh();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

			@Override
			protected Object getValue(Object element) {
				return ((ServerConfigurationProps) element).numberRepConnection + ""; //$NON-NLS-1$
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
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ServerConfigurationProps) element).numberRepConnection + ""; //$NON-NLS-1$
			}
		});
		configsViewer.setInput(new WritableList(new ArrayList<ServerConfigurationProps>(), ServerConfigurationProps.class));
		
		loadClusters();
		if(mapServerByUrl != null && !mapServerByUrl.isEmpty()) {
			for(String key : mapServerByUrl.keySet()) {
				List<String> selection = new ArrayList<String>();
				selection.add(key);
				
				StructuredSelection structuredSelection = new StructuredSelection(selection);
				clusterViewer.setSelection(structuredSelection);
				break;
			}
		}
	}

	@Override
	public void setFocus() {

	}

	private void loadClusters() {
		mapServerByUrl = new HashMap<String, List<Server>>();

		IVanillaAPI api = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi();

		List<Server> servers = new ArrayList<Server>();
		try {
			for (Server s : api.getVanillaSystemManager().getServerNodes(false)) {
				if (VanillaComponentType.COMPONENT_GATEWAY.equals(s.getComponentNature()) || VanillaComponentType.COMPONENT_REPORTING.equals(s.getComponentNature()) || VanillaComponentType.COMPONENT_FMDT_DB.equals(s.getComponentNature())) {

					if (s.getUrl().equals(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext().getVanillaUrl())) {
						s.setName(s.getName() + Messages.StressView_35);
					}
					else {
						s.setName(s.getName() + "(" + s.getUrl() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					servers.add(s);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (Server s : servers) {
			String key = null;
			if(s.getUrl().equals(bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaContext().getVanillaUrl())) {
				key = Messages.StressView_36;
			}
			else {
				key = s.getUrl();
			}
			if(mapServerByUrl.get(key) == null) {
				mapServerByUrl.put(key, new ArrayList<Server>());
			}
			mapServerByUrl.get(key).add(s);
		}

		clusterViewer.setInput(mapServerByUrl.keySet());
	}

	private void createResultTable() {
		TableViewerColumn col = new TableViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.StressView_37);
		col.getColumn().setWidth(150);

		col = new TableViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.StressView_38);
		col.getColumn().setWidth(150);

		col = new TableViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.StressView_39);
		col.getColumn().setWidth(150);

		col = new TableViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.StressView_40);
		col.getColumn().setWidth(150);

		col = new TableViewerColumn(resultViewer, SWT.NONE);
		col.getColumn().setText(Messages.StressView_41);
		col.getColumn().setWidth(150);

		ObservableListContentProvider cp = new ObservableListContentProvider();

		resultViewer.setContentProvider(cp);
		resultViewer.getTable().setLinesVisible(true);
		resultViewer.getTable().setHeaderVisible(true);

		resultViewer.setLabelProvider(new ObservableMapLabelProvider(new IObservableMap[] { PojoProperties.value(ProfilingResult.class, "taskNumber").observeDetail(cp.getKnownElements()), //$NON-NLS-1$
				PojoProperties.value(ProfilingResult.class, "repositoryConnectionNumber").observeDetail(cp.getKnownElements()), //$NON-NLS-1$
				PojoProperties.value(ProfilingResult.class, "avgPublishingTime").observeDetail(cp.getKnownElements()), //$NON-NLS-1$
				PojoProperties.value(ProfilingResult.class, "avgWaitingTime").observeDetail(cp.getKnownElements()), //$NON-NLS-1$
				PojoProperties.value(ProfilingResult.class, "avgRunningTime").observeDetail(cp.getKnownElements()) //$NON-NLS-1$
				}));

	}

	private void launch() {
		numberOfRun = Integer.parseInt(runNumber.getText());
		final Group g = (Group) ((IStructuredSelection) groupViewer.getSelection()).getFirstElement();
		final int repositoryId = ((Repository) ((IStructuredSelection) repositoryViewer.getSelection()).getFirstElement()).getId();
		final int itemId = directoryItem.getId();
		
		final String runtimeUrl = ((Server) ((IStructuredSelection) componentViewer.getSelection()).getFirstElement()).getUrl();
		final String login = Activator.getDefault().getVanillaContext().getLogin();
		final String password = Activator.getDefault().getVanillaContext().getPassword();
		
		final List<ServerConfigurationProps> confs = new ArrayList<ServerConfigurationProps>();

		for (Object o : configsViewer.getCheckedElements()) {
			confs.add((ServerConfigurationProps) o);
		}
		
		final String componentNature = ((Server) ((IStructuredSelection) componentViewer.getSelection()).getFirstElement()).getComponentNature();

		final ReportProfilingTool tool = new ReportProfilingTool();
		
		Job runningJob = new Job(Messages.StressView_4) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				try {

					ServerType type = null;
					IRuntimeConfig config = null;
					if (VanillaComponentType.COMPONENT_GATEWAY.equals(componentNature)) {
						type = ServerType.GATEWAY;

						config = new GatewayRuntimeConfiguration(new ObjectIdentifier(repositoryId, itemId), null, g.getId());
					}
					else if (VanillaComponentType.COMPONENT_REPORTING.equals(componentNature)) {
						type = ServerType.REPORTING;

						config = new ReportRuntimeConfig(new ObjectIdentifier(repositoryId, itemId), null, g.getId());
						((ReportRuntimeConfig) config).setOutputFormat("html"); //$NON-NLS-1$
					}

					if (type == null) {
						throw new InterruptedException(Messages.StressView_52);
					}

					final List<ProfilingResult> res = tool.stress(monitor, type, config, numberOfRun, runtimeUrl, login, password, confs);

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							resultViewer.setInput(new WritableList(res, ProfilingResult.class));
							/*
							 * build chart
							 */
							DefaultCategoryDataset ds = new DefaultCategoryDataset();
							Collections.sort(res, new Comparator<ProfilingResult>() {
								@Override
								public int compare(ProfilingResult o1, ProfilingResult o2) {
									return o1.getTaskNumber().compareTo(o2.getTaskNumber());
								}
							});

							// Integer i = null;
							for (ProfilingResult p : res) {

								ds.addValue(p.avgPublishingTime, Messages.StressView_53, "(" + numberOfRun + "," + p.repositoryConnectionNumber + ")"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
								ds.addValue(p.avgRunningTime, Messages.StressView_57, "(" + numberOfRun + "," + p.repositoryConnectionNumber + ")"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
								ds.addValue(p.avgWaitingTime, Messages.StressView_61, "(" + numberOfRun + "," + p.repositoryConnectionNumber + ")"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							}

							JFreeChart chart = ChartFactory.createBarChart3D(Messages.StressView_65, Messages.StressView_66, Messages.StressView_67, ds, PlotOrientation.HORIZONTAL, true, false, false);

							chartComposite.setChart(chart);

						}
					});

					monitor.done();
					return Status.OK_STATUS;
				} catch (InterruptedException e) {
					monitor.done();
					return Status.CANCEL_STATUS;
				} catch(Exception e) {
					monitor.done();
					e.printStackTrace();
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.StressView_16 + e.getMessage());
				}
			}
		};
		runningJob.setUser(true);
		runningJob.schedule();
	}

	private HashMap<String, List<Server>> mapServerByUrl;

	private void updateRunBtn() {
		boolean disable = clusterViewer.getSelection().isEmpty() || componentViewer.getSelection().isEmpty() || groupViewer.getSelection().isEmpty() || configsViewer.getCheckedElements().length == 0 || directoryItem == null || repositoryViewer.getSelection().isEmpty();

		if (!disable) {
			try {
				Integer.parseInt(runNumber.getText());
			} catch (Exception ex) {
				disable = true;
			}
		}

		run.setEnabled(!disable);
	}
}
