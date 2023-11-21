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
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.icons.Icons;
import bpm.vanilla.server.ui.views.VisualConstants;
import bpm.vanilla.server.ui.views.actions.ActionFullDescription;
import bpm.vanilla.server.ui.views.actions.gateway.ActionStepsInfos;

public class ServerContent extends ViewPart {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private List<TaskInfo> tasksInfo = new ArrayList<TaskInfo>();
	private TaskInfo selectedTask;

	private static final int COL_ID = 0;
	private static final int COL_NAME = 1;
	private static final int COL_PRIORITY = 2;
	private static final int COL_STATE = 3;
	private static final int COL_RESULT = 4;
	private static final int COL_FAILURE = 5;
	private static final int COL_START = 6;
	private static final int COL_CREATED = 7;
	private static final int COL_STOPED = 8;
	private static final int COL_ELASPED = 9;
	private static final int COL_DURATION = 10;
	private static final int COL_GROUP = 11;

	private class InternalColComparator extends ViewerComparator {

		int columIndex = COL_ID;

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			TaskInfo task1 = (TaskInfo) e1;
			TaskInfo task2 = (TaskInfo) e2;
			if (e1 != null && e2 != null) {
				switch (columIndex) {
					case COL_ID:
						return task1.getId().compareTo(task2.getId());
					case COL_NAME:
						return super.compare(viewer, task1.getItemName(), task2.getItemName());
					case COL_PRIORITY:
						return super.compare(viewer, task1.getPriority(), task2.getPriority());
					case COL_CREATED:
						if(task1.getCreationDate() == null) {
							return -1;
						}
						else if(task2.getCreationDate() == null) {
							return 1;
						}
						return task2.getCreationDate().before(task1.getCreationDate()) ? -1 : task2.getCreationDate().after(task1.getCreationDate()) ? 1 : 0;
					case COL_DURATION:
						if(task1.getDurationTime() == null) {
							return -1;
						}
						else if(task2.getDurationTime() == null) {
							return 1;
						}
						return task1.getDurationTime() > task2.getDurationTime() ? -1 : task1.getDurationTime() < task2.getDurationTime() ? 1 : 0;
					case COL_ELASPED:
						if(task1.getElapsedTime() == null) {
							return -1;
						}
						else if(task2.getElapsedTime() == null) {
							return 1;
						}
						return task1.getElapsedTime() > task2.getElapsedTime() ? -1 : task1.getElapsedTime() < task2.getElapsedTime() ? 1 : 0;
					case COL_FAILURE:
						return super.compare(viewer, task1.getFailureCause(), task2.getFailureCause());
					case COL_RESULT:
						return super.compare(viewer, task1.getResult(), task2.getResult());
					case COL_START:
						if(task1.getStartedDate() == null) {
							return -1;
						}
						else if(task2.getStartedDate() == null) {
							return 1;
						}
						return task2.getStartedDate().before(task1.getStartedDate()) ? -1 : task2.getStartedDate().after(task1.getStartedDate()) ? 1 : 0;
					case COL_STATE:
						return super.compare(viewer, task1.getState(), task2.getState());
					case COL_STOPED:
						if(task1.getStoppedDate() == null) {
							return -1;
						}
						else if(task2.getStoppedDate() == null) {
							return 1;
						}
						return task2.getStoppedDate().before(task1.getStoppedDate()) ? -1 : task2.getStoppedDate().after(task1.getStoppedDate()) ? 1 : 0;
					case COL_GROUP:
						return super.compare(viewer, task1.getGroupName(), task2.getGroupName());

				}
			}
			return super.compare(viewer, e1, e2);
		}
	}

	protected InternalColComparator comparator = new InternalColComparator();
	private TableViewer tasksViewer;

	private TaskMonitor taskMonitor;

	private FormToolkit toolkit;
	private ScrolledForm form;

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

		Action taskItem = new Action() {
			public void run() {
				tasksInfo = new ArrayList<TaskInfo>();
				tasksViewer.refresh();

				if (taskMonitor == null) {
					taskMonitor = new TaskMonitor();
					taskMonitor.start();
					this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.STOP_MONITORING));
					this.setToolTipText(Messages.ActivityServerContent_4);
				}
				else {
					this.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.START_MONITORING));
					this.setToolTipText(Messages.ActivityServerContent_5);

					taskMonitor.active = false;
					try {
						taskMonitor.join();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					taskMonitor = null;
				}
			}
		};
		taskItem.setToolTipText(Messages.ActivityServerContent_6);

		taskItem.setImageDescriptor((Activator.getDefault().getImageRegistry().getDescriptor(Icons.START_MONITORING)));

		ActionContributionItem it = new ActionContributionItem(taskItem);

		form.getToolBarManager().add(it);
		form.getToolBarManager().update(true);
	}

	private Composite createTaskTable(Composite parent) {
		tasksViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
		tasksViewer.getControl().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));// new
		// GridData(GridData.FILL_BOTH));
		tasksViewer.getTable().setHeaderVisible(true);
		tasksViewer.getTable().setLinesVisible(true);
		tasksViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<TaskInfo> infos = (List<TaskInfo>) inputElement;
				return infos.toArray(new TaskInfo[infos.size()]);
			}
		});

		TableViewerColumn id = new TableViewerColumn(tasksViewer, SWT.NONE);
		id.getColumn().setText(Messages.ActivityServerContent_8);
		id.getColumn().setWidth(50);
		id.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((TaskInfo) element).getId();
			}

		});

		TableViewerColumn name = new TableViewerColumn(tasksViewer, SWT.NONE);
		name.getColumn().setText(Messages.ServerContent_2);
		name.getColumn().setWidth(100);
		name.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((TaskInfo) element).getItemName();
			}
		});

		TableViewerColumn priority = new TableViewerColumn(tasksViewer, SWT.NONE);
		priority.getColumn().setText(Messages.ActivityServerContent_9);
		priority.getColumn().setWidth(50);
		priority.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((TaskInfo) element).getPriority();
			}
		});

		TableViewerColumn state = new TableViewerColumn(tasksViewer, SWT.NONE);
		state.getColumn().setText(Messages.ActivityServerContent_11);
		state.getColumn().setWidth(100);
		state.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				switch (((TaskInfo) element).getState()) {
					case ENDED:
						return Messages.ActivityServerContent_12;
					case RUNNING:
						return Messages.ActivityServerContent_13;
					case WAITING:
						return Messages.ActivityServerContent_14;
					default:
						return Messages.ActivityServerContent_15;
				}
			}
		});

		TableViewerColumn result = new TableViewerColumn(tasksViewer, SWT.NONE);
		result.getColumn().setText(Messages.ActivityServerContent_16);
		result.getColumn().setWidth(100);
		result.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				switch (((TaskInfo) element).getResult()) {
					case FAILED:
						return Messages.ActivityServerContent_17;
					case SUCCEED:
						return Messages.ActivityServerContent_18;
					case UNDEFINED:
						return Messages.ActivityServerContent_19;
					default:
						return Messages.ActivityServerContent_20;
				}
			}

		});

		TableViewerColumn failure = new TableViewerColumn(tasksViewer, SWT.NONE);
		failure.getColumn().setText(Messages.ActivityServerContent_21);
		failure.getColumn().setWidth(100);
		failure.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (((TaskInfo) element).getFailureCause() != null) {
					return ((TaskInfo) element).getFailureCause();
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
					return sdf.format(((TaskInfo) element).getStartedDate());
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}

			}

		});

		TableViewerColumn created = new TableViewerColumn(tasksViewer, SWT.NONE);
		created.getColumn().setText(Messages.ActivityServerContent_25);
		created.getColumn().setWidth(100);
		created.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				try {
					return sdf.format(((TaskInfo) element).getCreationDate());
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
					return sdf.format(((TaskInfo) element).getStoppedDate());
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}

			}

		});

		TableViewerColumn elapsed = new TableViewerColumn(tasksViewer, SWT.NONE);
		elapsed.getColumn().setText(Messages.ActivityServerContent_29);
		elapsed.getColumn().setWidth(100);
		elapsed.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				try {
					return getElapsedTime(((TaskInfo) element).getElapsedTime());
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
					return getElapsedTime(((TaskInfo) element).getDurationTime());
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}

			}

		});

		TableViewerColumn group = new TableViewerColumn(tasksViewer, SWT.NONE);
		group.getColumn().setText(Messages.ActivityServerContent_33);
		group.getColumn().setWidth(100);
		group.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				try {
					return ((TaskInfo) element).getGroupName();
				} catch (Exception ex) {
					return ""; //$NON-NLS-1$
				}
			}
		});

		tasksViewer.setComparator(comparator);
		tasksViewer.setInput(tasksInfo);

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

			return h.intValue() + Messages.ServerContent_4 + min.intValue() + Messages.ServerContent_5 + sec.intValue() + Messages.ServerContent_6 + mill.intValue() + Messages.ServerContent_7;
		}
	}

	@Override
	public void setFocus() {
	}

	protected class TaskMonitor extends Thread {
		int refresh = 5000;
		String servetUrl;
		boolean active = true;

		public void run() {
			while (active) {

				if (Activator.getDefault().getServerType() == null) {
					active = false;
					tasksInfo = new ArrayList<TaskInfo>();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							tasksViewer.setInput(tasksInfo);
						}
					});
				}

				try {
					tasksInfo = Activator.getDefault().getRemoteServerManager().getTasksInfo();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(1000);
				} catch (Exception ex) {

				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						tasksViewer.setInput(tasksInfo);

					}
				});

			}
		}
	}

	private void createTaskContextMenu() {
		MenuManager mgr = new MenuManager("Actions", "bpm.vanilla.server.ui.views.StateTaskView.contextMenu"); //$NON-NLS-1$ //$NON-NLS-2$

		mgr.add(new ActionStepsInfos());
		mgr.add(new ActionFullDescription(this));
		mgr.add(new Separator());
		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) tasksViewer.getSelection();
				if (selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof TaskInfo) {
					selectedTask = (TaskInfo) selection.getFirstElement();
				}

				for (IContributionItem i : manager.getItems()) {
					if (VisualConstants.ACTION_GATEWAY_STEPS_INFOS_ID.equals(i.getId())) {
						i.setVisible(Activator.getDefault().getServerType() != null && (Activator.getDefault().getServerType() == ServerType.GATEWAY || Activator.getDefault().getServerType() == ServerType.WORKFLOW));
					}
					else if (VisualConstants.ACTION_SHOW_LOGS.equals(i.getId())) {
						i.setVisible(selectedTask != null && selectedTask.getFailureCause() != null && !selectedTask.getFailureCause().isEmpty() && Activator.getDefault().getServerType() != null && (Activator.getDefault().getServerType() == ServerType.GATEWAY || Activator.getDefault().getServerType() == ServerType.WORKFLOW));
					}
				}

				manager.update(true);
			}
		});

		tasksViewer.getTable().setMenu(mgr.createContextMenu(tasksViewer.getControl()));
		getSite().registerContextMenu("bpm.vanilla.server.ui.views.StateTaskView.contextMenu", mgr, tasksViewer); //$NON-NLS-1$

	}

	public TaskInfo getSelectedTask() {
		return selectedTask;
	}
}
