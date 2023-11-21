package bpm.vanilla.server.ui.views.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.platform.core.beans.IRuntimeState;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.VisualConstants;
import bpm.vanilla.server.ui.views.actions.ActionFullDescription;
import bpm.vanilla.server.ui.views.actions.gateway.ActionPreviousStepsInfos;

public class ServerPreviousContent extends ViewPart {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private List<IRuntimeState> runtimeStates;

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
			IRuntimeState runtime1 = (IRuntimeState) e1;
			IRuntimeState runtime2 = (IRuntimeState) e2;
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
	private TableViewer tasksViewer;

	private FormToolkit toolkit;
	private ScrolledForm form;

	private Action loadMoreAction;

	private int rangeIndex = 0;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		toolkit = new FormToolkit(parent.getDisplay());

		form = toolkit.createScrolledForm(parent);
		form.setExpandHorizontal(true);
		form.setAlwaysShowScrollBars(false);
		form.setExpandVertical(true);
		form.getHorizontalBar().setEnabled(true);
		form.setText(Messages.ActivityServerContent_2);
		toolkit.decorateFormHeading(form.getForm());

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(new FillLayout(SWT.VERTICAL));

		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION | Section.EXPANDED);
		section.setText(Messages.ActivityServerContent_3);
		section.setLayout(new TableWrapLayout());

		section.setClient(createTaskTable(section));

		fillToolbar();
	}

	private void fillToolbar() {

		Action refreshPrevious = new Action() {
			public void run() {
				rangeIndex = 0;

				Activator activator = Activator.getDefault();
				Repository selectedRepository = activator.getSelectedRepository();
				ServerType serverType = activator.getServerType();

				if (selectedRepository == null || serverType == null) {
					runtimeStates = new ArrayList<IRuntimeState>();
				}
				else {
					try {
						if (serverType == ServerType.GATEWAY || serverType == ServerType.REPORTING || serverType == ServerType.WORKFLOW) {
							runtimeStates = activator.getRemoteServerManager().getPreviousInfos(selectedRepository.getId(), rangeIndex, rangeIndex + 100);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				rangeIndex = rangeIndex + 100;

				loadMoreAction.setEnabled(true);
				tasksViewer.setInput(runtimeStates);
			}
		};
		refreshPrevious.setToolTipText(Messages.ServerPreviousContent_0);
		refreshPrevious.setImageDescriptor((Activator.getDefault().getImageRegistry().getDescriptor(Icons.REFRESH)));

		ActionContributionItem itRefresh = new ActionContributionItem(refreshPrevious);

		loadMoreAction = new Action() {
			public void run() {
				loadMoreAction();
			}
		};
		loadMoreAction.setToolTipText(Messages.ServerPreviousContent_3);
		loadMoreAction.setImageDescriptor((Activator.getDefault().getImageRegistry().getDescriptor(Icons.GATEWAYDETAILS)));
		loadMoreAction.setEnabled(false);

		ActionContributionItem itLoadMore = new ActionContributionItem(loadMoreAction);

		form.getToolBarManager().add(itLoadMore);
		form.getToolBarManager().add(itRefresh);
		form.getToolBarManager().update(true);
	}
	
	private void loadMoreAction() {
		Activator activator = Activator.getDefault();
		Repository selectedRepository = activator.getSelectedRepository();
		ServerType serverType = activator.getServerType();

		if (selectedRepository == null || serverType == null) {
			runtimeStates = new ArrayList<IRuntimeState>();
		}
		else {
			try {
				if (serverType == ServerType.GATEWAY || serverType == ServerType.REPORTING || serverType == ServerType.WORKFLOW) {
					runtimeStates.addAll(activator.getRemoteServerManager().getPreviousInfos(selectedRepository.getId(), rangeIndex, rangeIndex + 100));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		rangeIndex = rangeIndex + 100;

		tasksViewer.setInput(runtimeStates);
	}

	private Composite createTaskTable(Composite parent) {
		tasksViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
		tasksViewer.getControl().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));// new
		tasksViewer.getTable().setHeaderVisible(true);
		tasksViewer.getTable().setLinesVisible(true);
		tasksViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<IRuntimeState> runtimeStates = (List<IRuntimeState>) inputElement;
				return runtimeStates.toArray(new IRuntimeState[runtimeStates.size()]);
			}
		});

		TableViewerColumn name = new TableViewerColumn(tasksViewer, SWT.NONE);
		name.getColumn().setText(Messages.ServerPreviousContent_1);
		name.getColumn().setWidth(50);
		name.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((IRuntimeState) element).getName();
			}
		});

		TableViewerColumn state = new TableViewerColumn(tasksViewer, SWT.NONE);
		state.getColumn().setText(Messages.ActivityServerContent_11);
		state.getColumn().setWidth(100);
		state.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				switch (((IRuntimeState) element).getState()) {
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
				if (((IRuntimeState) element).getFailureCause() != null) {
					return ((IRuntimeState) element).getFailureCause();
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
					return sdf.format(((IRuntimeState) element).getStartedDate());
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
					return sdf.format(((IRuntimeState) element).getStoppedDate());
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
					return getElapsedTime(((IRuntimeState) element).getDurationTime());
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}
			}
		});

		tasksViewer.setComparator(comparator);
		tasksViewer.setInput(runtimeStates);

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

		getSite().setSelectionProvider(tasksViewer);
		createTaskContextMenu();
		return tasksViewer.getTable();
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

			return h.intValue() + Messages.ServerPreviousContent_4 + min.intValue() + Messages.ServerPreviousContent_5 + sec.intValue() + Messages.ServerPreviousContent_6 + mill.intValue() + Messages.ServerPreviousContent_7;
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

				ISelection s = getSite().getSelectionProvider().getSelection();
				if (s != null && ((IStructuredSelection) s).getFirstElement() != null && ((IStructuredSelection) s).getFirstElement() instanceof IRuntimeState) {
					IRuntimeState runtime = ((IRuntimeState) ((IStructuredSelection) s).getFirstElement());
					if (runtime.getFailureCause() != null && !runtime.getFailureCause().isEmpty()) {
						showLogs = true;
					}
				}

				for (IContributionItem i : manager.getItems()) {
					if (VisualConstants.ACTION_GATEWAY_PREVIOUS_STEPS_INFOS_ID.equals(i.getId())) {
						i.setVisible(Activator.getDefault().getServerType() != null && (Activator.getDefault().getServerType() == ServerType.GATEWAY || Activator.getDefault().getServerType() == ServerType.WORKFLOW));
					}
					else if (VisualConstants.ACTION_SHOW_LOGS.equals(i.getId())) {
						boolean visible = showLogs && Activator.getDefault().getServerType() != null && (Activator.getDefault().getServerType() == ServerType.GATEWAY || Activator.getDefault().getServerType() == ServerType.WORKFLOW);
						i.setVisible(visible);
					}
				}

				manager.update(true);
			}
		});

		tasksViewer.getTable().setMenu(mgr.createContextMenu(tasksViewer.getControl()));
		getSite().registerContextMenu("bpm.vanilla.server.ui.views.StateTaskView.previouscontextmenu", mgr, tasksViewer); //$NON-NLS-1$
	}
}
