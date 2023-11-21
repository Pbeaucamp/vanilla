package bpm.vanilla.server.ui.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.StepInfos;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.actions.ActionFullDescription;
import bpm.vanilla.server.ui.views.actions.gateway.ActionPreviousStepsInfos;

public class ItemHistoView extends ViewPart implements ISelectionListener {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private static final int COL_NAME = 0;
	private static final int COL_STATE = 1;
	private static final int COL_FAILURE = 2;
	private static final int COL_START = 3;
	private static final int COL_STOPED = 4;
	private static final int COL_DURATION = 5;

	private class InternalColComparator extends ViewerComparator {

		private int columIndex = COL_NAME;

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			IRuntimeState runtime1 = ((ItemInstance) e1).getState();
			IRuntimeState runtime2 = ((ItemInstance) e2).getState();
			if (e1 != null && e2 != null) {
				switch (columIndex) {
				case COL_NAME:
					return super.compare(viewer, runtime1.getName(), runtime2.getName());
				case COL_STATE:
					return super.compare(viewer, runtime1.getState(), runtime2.getState());
				case COL_FAILURE:
					return super.compare(viewer, runtime1.getFailureCause(), runtime2.getFailureCause());
				case COL_START:
					return runtime2.getStartedDate().before(runtime1.getStartedDate()) ? -1 : runtime2.getStartedDate().after(runtime1.getStartedDate()) ? 1 : 0;
				case COL_STOPED:
					if (runtime1.getStoppedDate() == null) {
						return -1;
					}
					else if (runtime2.getStoppedDate() == null) {
						return 1;
					}
					return runtime2.getStoppedDate().before(runtime1.getStoppedDate()) ? -1 : runtime2.getStoppedDate().after(runtime1.getStoppedDate()) ? 1 : 0;
				case COL_DURATION:
					if (runtime1.getDurationTime() == null) {
						return -1;
					}
					else if (runtime2.getDurationTime() == null) {
						return 1;
					}
					return runtime1.getDurationTime() > runtime2.getDurationTime() ? -1 : runtime1.getDurationTime() < runtime2.getDurationTime() ? 1 : 0;
				}
			}
			return super.compare(viewer, e1, e2);
		}
	}

	protected InternalColComparator comparator = new InternalColComparator();

	private FormToolkit toolkit;
	private Form form;

	private ComboViewer groupViewer;
	private Button checkStart, checkEnd;
	private DateTime dateStart, dateEnd;

	private TableViewer tasksViewer;
	private ChartComposite chartComposite;

	private RepositoryItem selectedItem;

	public ItemHistoView() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		toolkit = new FormToolkit(parent.getDisplay());

		form = toolkit.createForm(parent);
		form.setImage(Activator.getDefault().getImageRegistry().get(Icons.MANAGER));
		form.setText(Messages.ItemHistoView_0);
		form.getBody().setLayout(new GridLayout(1, false));
		toolkit.paintBordersFor(form);
		toolkit.decorateFormHeading(form);

		Composite composite = new Composite(form.getBody(), SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);

		createFilterSection(composite);
		createTaskTableSection(composite);

		fillToolbar();
		loadGroups();
	}

	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removeSelectionListener(this);
		super.dispose();
	}

	private void createFilterSection(Composite parent) {
		Section section = toolkit.createSection(parent, Section.TWISTIE | Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		section.setBounds(0, 0, 98, 21);
		toolkit.paintBordersFor(section);
		section.setText(Messages.ItemHistoView_1);
		section.setExpanded(true);

		Composite composite = new Composite(section, SWT.NONE);
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		toolkit.paintBordersFor(section);

		Label l = toolkit.createLabel(composite, Messages.ItemHistoView_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		groupViewer = new ComboViewer(new CCombo(composite, SWT.READ_ONLY | SWT.BORDER));
		groupViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groupViewer.setContentProvider(new ArrayContentProvider());
		groupViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Group) {
					return ((Group) element).getName();
				}
				return super.getText(element);
			}
		});

		checkStart = toolkit.createButton(composite, Messages.ItemHistoView_3, SWT.CHECK);
		checkStart.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				updateUi(dateStart, source.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		dateStart = new DateTime(composite, SWT.DATE);
		dateStart.setEnabled(false);

		checkEnd = toolkit.createButton(composite, Messages.ItemHistoView_4, SWT.CHECK);
		checkEnd.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				updateUi(dateEnd, source.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		dateEnd = new DateTime(composite, SWT.DATE);
		dateEnd.setEnabled(false);

		Button btnFilter = toolkit.createButton(composite, Messages.ItemHistoView_5, SWT.PUSH);
		btnFilter.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, true, false, 2, 1));
		btnFilter.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateInput();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void updateUi(DateTime dateTime, boolean enable) {
		dateTime.setEnabled(enable);
	}

	private Date getDate(DateTime dateTime) {
		int year = dateTime.getYear();
		int month = dateTime.getMonth();
		int day = dateTime.getDay();

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, 1, 0, 0);
		return cal.getTime();
	}

	private void createTaskTableSection(Composite parent) {
		// Section section = toolkit.createSection(parent, Section.TITLE_BAR |
		// Section.DESCRIPTION | Section.EXPANDED);
		// section.setText(Messages.ActivityServerContent_3);
		// section.setLayout(new TableWrapLayout());

		Section section = toolkit.createSection(parent, Section.TWISTIE | Section.TITLE_BAR);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		section.setBounds(0, 0, 98, 21);
		toolkit.paintBordersFor(section);
		section.setText(Messages.ItemHistoView_6);
		section.setExpanded(true);

		Composite composite = new Composite(section, SWT.NONE);
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout());
		toolkit.paintBordersFor(section);

		CTabFolder folder = new CTabFolder(composite, SWT.BOTTOM);
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		folder.setLayout(new GridLayout());

		CTabItem it = new CTabItem(folder, SWT.NONE);
		it.setText(Messages.ItemHistoView_7);

		tasksViewer = new TableViewer(toolkit.createTable(folder, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL));
		tasksViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		tasksViewer.getTable().setHeaderVisible(true);
		tasksViewer.getTable().setLinesVisible(true);
		tasksViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<ItemInstance> runtimeStates = (List<ItemInstance>) inputElement;
				return runtimeStates.toArray(new ItemInstance[runtimeStates.size()]);
			}
		});

		it.setControl(tasksViewer.getTable());

		TableViewerColumn name = new TableViewerColumn(tasksViewer, SWT.NONE);
		name.getColumn().setText(Messages.ServerPreviousContent_1);
		name.getColumn().setWidth(50);
		name.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((ItemInstance) element).getState().getName();
			}
		});

		TableViewerColumn state = new TableViewerColumn(tasksViewer, SWT.NONE);
		state.getColumn().setText(Messages.ActivityServerContent_11);
		state.getColumn().setWidth(100);
		state.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				switch (((ItemInstance) element).getState().getState()) {
				case ENDED:
					return Messages.ActivityServerContent_12;
				case RUNNING:
					return Messages.ActivityServerContent_13;
				case WAITING:
					return Messages.ActivityServerContent_14;
				case FAILED:
					return Messages.ServerPreviousContent_2;
				default:
					return Messages.ActivityServerContent_15;
				}
			}
		});

		TableViewerColumn failure = new TableViewerColumn(tasksViewer, SWT.NONE);
		failure.getColumn().setText(Messages.ActivityServerContent_21);
		failure.getColumn().setWidth(100);
		failure.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((ItemInstance) element).getState().getFailureCause() != null) {
					return ((ItemInstance) element).getState().getFailureCause();
				}
				return ""; //$NON-NLS-1$
			}
		});

		TableViewerColumn start = new TableViewerColumn(tasksViewer, SWT.NONE);
		start.getColumn().setText(Messages.ActivityServerContent_23);
		start.getColumn().setWidth(100);
		start.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {

				try {
					return sdf.format(((ItemInstance) element).getState().getStartedDate());
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}

			}

		});

		TableViewerColumn stoped = new TableViewerColumn(tasksViewer, SWT.NONE);
		stoped.getColumn().setText(Messages.ActivityServerContent_27);
		stoped.getColumn().setWidth(100);
		stoped.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				try {
					return sdf.format(((ItemInstance) element).getState().getStoppedDate());
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}
			}
		});

		TableViewerColumn duration = new TableViewerColumn(tasksViewer, SWT.NONE);
		duration.getColumn().setText(Messages.ActivityServerContent_31);
		duration.getColumn().setWidth(100);
		duration.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				try {
					return getElapsedTime(((ItemInstance) element).getState().getDurationTime());
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}
			}
		});

		tasksViewer.setComparator(comparator);

		for (TableColumn col : tasksViewer.getTable().getColumns()) {
			col.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					comparator.columIndex = tasksViewer.getTable().indexOf((TableColumn) e.widget);
					tasksViewer.refresh();
				}
			});
		}

		createTaskContextMenu();

		chartComposite = new ChartComposite(folder, SWT.NONE);

		it = new CTabItem(folder, SWT.NONE);
		it.setText(Messages.ItemHistoView_8);
		it.setControl(chartComposite);

		folder.setSelection(0);
	}

	private void fillToolbar() {
		Action refreshPrevious = new Action() {
			public void run() {
				refresh();
			}
		};
		refreshPrevious.setToolTipText(Messages.ItemHistoView_9);
		refreshPrevious.setImageDescriptor((Activator.getDefault().getImageRegistry().getDescriptor(Icons.REFRESH)));

		ActionContributionItem itRefresh = new ActionContributionItem(refreshPrevious);
		form.getToolBarManager().add(itRefresh);
		form.getToolBarManager().update(true);
	}

	private void loadGroups() {
		IVanillaAPI vanillaApi = Activator.getDefault().getVanillaApi();
		try {
			List<Group> groups = vanillaApi.getVanillaSecurityManager().getGroups();
			if (groups == null) {
				groups = new ArrayList<Group>();
			}
			groupViewer.setInput(groups);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.ItemHistoView_10, Messages.ItemHistoView_11);
		}

	}

	private void refresh() {
		checkStart.setSelection(false);
		dateStart.setEnabled(false);

		checkEnd.setSelection(false);
		dateEnd.setEnabled(false);

		groupViewer.setSelection(null);

		if (selectedItem != null) {
			loadInput(selectedItem.getId(), null, null, null);
		}
	}

	private void updateInput() {
		Group group = ((IStructuredSelection) groupViewer.getSelection()).getFirstElement() != null ? (Group) ((IStructuredSelection) groupViewer.getSelection()).getFirstElement() : null;
		Date startDate = checkStart.getSelection() ? getDate(dateStart) : null;
		Date endDate = checkEnd.getSelection() ? getDate(dateEnd) : null;

		if (selectedItem != null) {
			loadInput(selectedItem.getId(), startDate, endDate, group);
		}
	}

	private void loadInput(int itemId, Date startDate, Date endDate, Group group) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi();
			if (repositoryApi != null) {
				List<ItemInstance> instances = repositoryApi.getAdminService().getItemInstances(itemId, startDate, endDate, group != null ? group.getId() : null);
				tasksViewer.setInput(instances);

				loadChart(instances);
			}
			else {
				tasksViewer.setInput(new ArrayList<ItemInstance>());
				loadChart(new ArrayList<ItemInstance>());
			}
		} catch (Exception e) {
			e.printStackTrace();
			tasksViewer.setInput(new ArrayList<ItemInstance>());
			loadChart(new ArrayList<ItemInstance>());
		}
	}

	private void loadChart(List<ItemInstance> instances) {
		DefaultCategoryDataset ds = new DefaultCategoryDataset();

		GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
		KeyToGroupMap map = new KeyToGroupMap("G1"); //$NON-NLS-1$

		for (ItemInstance instance : instances) {
			if (instance.getState() instanceof GatewayRuntimeState) {
				GatewayRuntimeState state = (GatewayRuntimeState) instance.getState();
				if (state.getStepsInfo() != null) {
					for (StepInfos step : state.getStepsInfo()) {
						ds.addValue(step.getDuration(), step.getStepName(), instance.getRunDate());
						map.mapKeyToGroup(step.getStepName(), "G1"); //$NON-NLS-1$
					}
				}
				else {
					ds.addValue(instance.getState().getDurationTime(), "Instances", instance.getRunDate()); //$NON-NLS-1$
				}
			}
			else if (instance.getState() instanceof WorkflowInstanceState) {
				WorkflowInstanceState state = (WorkflowInstanceState) instance.getState();
				if (state.getStepInfos() != null) {
					for (bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState.StepInfos step : state.getStepInfos()) {
						ds.addValue(step.getDuration(), step.getStepName(), String.valueOf(instance.getRunDate()));
						map.mapKeyToGroup(step.getStepName(), "G1"); //$NON-NLS-1$
					}
				}
				else {
					ds.addValue(instance.getState().getDurationTime(), "Instances", instance.getRunDate()); //$NON-NLS-1$
				}
			}
			else {
				ds.addValue(instance.getState().getDurationTime(), "Instances", instance.getRunDate()); //$NON-NLS-1$
			}
		}

		JFreeChart chart = ChartFactory.createStackedBarChart(Messages.ItemHistoView_15, Messages.ItemHistoView_16, Messages.ItemHistoView_17, ds, PlotOrientation.VERTICAL, true, true, false);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setRenderer(renderer);
		chartComposite.setChart(chart);
	}

	private IRepositoryApi getRepositoryApi() {
		IVanillaContext ctx = Activator.getDefault().getVanillaApi().getVanillaContext();
		Repository rep = Activator.getDefault().getSelectedRepository();

		Group grp = new Group();
		grp.setId(-1);

		if (ctx != null && rep != null) {
			return new RemoteRepositoryApi(new BaseRepositoryContext(ctx, grp, rep));
		}
		return null;
	}

	public String getElapsedTime(Long time) {
		if (time == null) {
			return " - - "; //$NON-NLS-1$
		}
		else {
			Double mill = time.doubleValue() % 1000;

			double secTmp = Math.floor(time.doubleValue() / 1000);
			Double sec = secTmp % 60;

			double minTmp = Math.floor(secTmp / 60);
			Double min = minTmp % 60;

			double hTmp = Math.floor(minTmp / 60);
			Double h = hTmp % 24;

			StringBuffer buf = new StringBuffer();
			if (h.intValue() > 0) {
				buf.append(h.intValue() + Messages.ServerPreviousContent_4);
			}
			if (min.intValue() > 0) {
				buf.append(min.intValue() + Messages.ServerPreviousContent_5);
			}
			if (sec.intValue() > 0) {
				buf.append(sec.intValue() + Messages.ServerPreviousContent_6);
			}
			buf.append(mill.intValue() + Messages.ServerPreviousContent_7);

			return buf.toString();
		}
	}

	@Override
	public void setFocus() {
	}

	private void createTaskContextMenu() {
		MenuManager mgr = new MenuManager("bpm.vanilla.server.ui.views.StateTaskView.previouscontextmenu"); //$NON-NLS-1$ //$NON-NLS-2$

		mgr.add(new ActionPreviousStepsInfos(tasksViewer));
		mgr.add(new ActionFullDescription(this));
		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {

				boolean showLogs = false;

				ISelection s = tasksViewer.getSelection();
				if (s != null && ((IStructuredSelection) s).getFirstElement() != null && ((IStructuredSelection) s).getFirstElement() instanceof ItemInstance) {
					ItemInstance runtime = ((ItemInstance) ((IStructuredSelection) s).getFirstElement());
					if (runtime.getState().getFailureCause() != null && !runtime.getState().getFailureCause().isEmpty()) {
						showLogs = true;
					}

					for (IContributionItem i : manager.getItems()) {
						if (VisualConstants.ACTION_GATEWAY_PREVIOUS_STEPS_INFOS_ID.equals(i.getId())) {
							i.setVisible(runtime.getItemType() == IRepositoryApi.BIW_TYPE || runtime.getItemType() == IRepositoryApi.GTW_TYPE);
						}
						else if (VisualConstants.ACTION_SHOW_LOGS.equals(i.getId())) {
							boolean visible = showLogs && (runtime.getItemType() == IRepositoryApi.BIW_TYPE || runtime.getItemType() == IRepositoryApi.GTW_TYPE);
							i.setVisible(visible);
						}
					}
				}
				else {
					for (IContributionItem i : manager.getItems()) {
						i.setVisible(false);
					}
				}

				manager.update(true);
			}
		});

		tasksViewer.getTable().setMenu(mgr.createContextMenu(tasksViewer.getControl()));
		getSite().registerContextMenu("bpm.vanilla.server.ui.views.StateTaskView.previouscontextmenu", mgr, tasksViewer); //$NON-NLS-1$
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty()) {
			this.selectedItem = null;
			tasksViewer.setInput(new ArrayList<ItemInstance>());
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object o = ss.getFirstElement();

		if (o instanceof RepositoryItem) {
			this.selectedItem = (RepositoryItem) o;
			refresh();
		}
	}
}
