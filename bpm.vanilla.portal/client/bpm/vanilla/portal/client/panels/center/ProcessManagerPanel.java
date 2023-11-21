package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance;
import bpm.vanilla.platform.core.beans.scheduler.JobInstance.Result;
import bpm.vanilla.portal.client.dialog.SchedulerHistoricDialog;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;
import bpm.vanilla.portal.client.widget.custom.SchedulerImageCell;
import bpm.vanilla.portal.client.wizard.AddTaskWizard;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class ProcessManagerPanel extends Tab {

	private static ProcessManagerPanelUiBinder uiBinder = GWT.create(ProcessManagerPanelUiBinder.class);

	interface ProcessManagerPanelUiBinder extends UiBinder<Widget, ProcessManagerPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String pager();

		String imgPlanned();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent, panelPager;

	@UiField
	Image btnEditTask, btnDeleteTask, btnRunNowTask, btnProperties;

	private ListDataProvider<Job> dataProvider;
	private SingleSelectionModel<Job> selectionModel;
	private ListHandler<Job> sortHandler;

	private DateTimeFormat sdf = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);

	public ProcessManagerPanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.ProcessManager(), true);
		this.add(uiBinder.createAndBindUi(this));

		panelContent.add(createGridData());

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());

		loadEvent();
	}

	private void loadEvent() {
		showWaitPart(true);

		BiPortalService.Connect.getInstance().getJobs(new AsyncCallback<List<Job>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadJobs());
			}

			@Override
			public void onSuccess(List<Job> result) {
				showWaitPart(false);

				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());
				selectionModel.clear();
			}
		});
	}

	private DataGrid<Job> createGridData() {
		CustomCell cell = new CustomCell();

		Column<Job, ImageResource> pauseColumn = new Column<Job, ImageResource>(new ImageResourceCell()) {
			@Override
			public ImageResource getValue(Job object) {
				if (object.getLastRun() == null) {
					return PortalImage.INSTANCE.ic_flag_grey600_18dp();
				}
				else if (object.getLastRun().getResult() == Result.SUCCESS) {
					return PortalImage.INSTANCE.ic_flag_green_18dp();
				}
				else if (object.getLastRun().getResult() == Result.ERROR) {
					return PortalImage.INSTANCE.ic_flag_red_18dp();
				}
				else {
					return PortalImage.INSTANCE.ic_flag_red_18dp();
				}
			}
		};

		Column<Job, String> nameColumn = new Column<Job, String>(cell) {

			@Override
			public String getValue(Job object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);
		
		Column<Job, String> itemName = new Column<Job, String>(cell) {

			@Override
			public String getValue(Job object) {
				if (object.getItem() != null) {
					return object.getItem().getItemName();
				}
				return "";
			}
		};
		itemName.setSortable(true);

		Column<Job, String> beginDateColumn = new Column<Job, String>(cell) {

			@Override
			public String getValue(Job object) {
				return sdf.format(object.getBeginDate());
			}
		};
		beginDateColumn.setSortable(true);

		Column<Job, String> endDateColumn = new Column<Job, String>(cell) {

			@Override
			public String getValue(Job object) {
				if (object.getStopDate() != null) {
					return sdf.format(object.getStopDate());
				}
				return ToolsGWT.lblCnst.NoEnd();
			}
		};
		endDateColumn.setSortable(true);

		Column<Job, String> nextExecutionColumn = new Column<Job, String>(cell) {

			@Override
			public String getValue(Job object) {
				if (object.getNextExecution() != null) {
					return sdf.format(object.getNextExecution());
				}
				return ToolsGWT.lblCnst.NoFutureExecution();
			}
		};

		Column<Job, String> status = new Column<Job, String>(cell) {

			@Override
			public String getValue(Job object) {
				if (object.getLastRun() == null) {
					return ToolsGWT.lblCnst.JobNeverLaunch();
				}
				else if (object.getLastRun().getResult() == Result.SUCCESS) {
					return ToolsGWT.lblCnst.JobLastRunSuccess();
				}
				else if (object.getLastRun().getResult() == Result.ERROR) {
					return ToolsGWT.lblCnst.JobLastRunError();
				}
				else if (object.getLastRun().getResult() == Result.RUNNING) {
					return ToolsGWT.lblCnst.JobRunning();
				}
				else if (object.getLastRun().getResult() == Result.UNKNOWN) {
					return ToolsGWT.lblCnst.JobUnknown();
				}
				else {
					return ToolsGWT.lblCnst.JobNeverLaunch();
				}
			}
		};
		status.setSortable(true);

		SchedulerImageCell planificationCell = new SchedulerImageCell(style.imgPlanned());
		Column<Job, String> colActivate = new Column<Job, String>(planificationCell) {

			@Override
			public String getValue(Job object) {
				if (object.isOn()) {
					return SchedulerImageCell.DEACTIVATE + "";
				}
				else {
					return SchedulerImageCell.ACTIVATE + "";
				}
			}
		};
		colActivate.setFieldUpdater(new FieldUpdater<Job, String>() {

			@Override
			public void update(int index, Job object, String value) {
				int intValue = Integer.parseInt(value);
				if (intValue == SchedulerImageCell.DEACTIVATE) {
					object.getDetail().setOn(false);

					addOrEditJob(object);
				}
				else if (intValue == SchedulerImageCell.ACTIVATE) {
					object.getDetail().setOn(true);

					addOrEditJob(object);
				}
			}
		});

		Column<Job, String> needToBeLaunchColumn = new Column<Job, String>(new ButtonCell()) {

			@Override
			public String getValue(Job object) {
				if (object.isNeedToBeLaunch()) {
					return ToolsGWT.lblCnst.Launch();
				}
				else {
					return ToolsGWT.lblCnst.NoNeedToBeLaunch();
				}
			}
		};
		needToBeLaunchColumn.setFieldUpdater(new FieldUpdater<Job, String>() {

			@Override
			public void update(int index, Job object, String value) {
				if (object.isNeedToBeLaunch()) {
					launchJob(object);
				}
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Job> dataGrid = new DataGrid<Job>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(pauseColumn, ToolsGWT.lblCnst.TaskState());
		dataGrid.setColumnWidth(pauseColumn, "80px");
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.TaskName());
		dataGrid.addColumn(itemName, ToolsGWT.lblCnst.ItemName());
		dataGrid.addColumn(beginDateColumn, ToolsGWT.lblCnst.BeginDate());
		dataGrid.addColumn(endDateColumn, ToolsGWT.lblCnst.EndDate());
		dataGrid.addColumn(nextExecutionColumn, ToolsGWT.lblCnst.NextExecution());
		dataGrid.addColumn(status, ToolsGWT.lblCnst.LastTaskResult());
		dataGrid.addColumn(colActivate, ToolsGWT.lblCnst.Planification());
		dataGrid.addColumn(needToBeLaunchColumn, ToolsGWT.lblCnst.NeedToBeLaunch());
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<Job>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<Job>(new ArrayList<Job>());
		sortHandler.setComparator(nameColumn, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(itemName, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				if (o1.getItem() != null && o2.getItem() != null) {
					return o1.getItem().getItemName().compareTo(o2.getItem().getItemName());
				}
				if (o1.getItem() == null) {
					return -1;
				}
				return 0;
			}
		});
		sortHandler.setComparator(beginDateColumn, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				if (o1.getBeginDate() == null) {
					return -1;
				}
				else if (o2.getBeginDate() == null) {
					return 1;
				}

				return o2.getBeginDate().before(o1.getBeginDate()) ? -1 : o2.getBeginDate().after(o1.getBeginDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(endDateColumn, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				if (o1.getStopDate() == null) {
					return -1;
				}
				else if (o2.getStopDate() == null) {
					return 1;
				}

				return o2.getStopDate().before(o1.getStopDate()) ? -1 : o2.getStopDate().after(o1.getStopDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(status, new Comparator<Job>() {

			@Override
			public int compare(Job o1, Job o2) {
				String status1 = "";
				String status2 = "";

				if (o1.getLastRun() == null) {
					status1 = ToolsGWT.lblCnst.JobNeverLaunch();
				}
				else if (o1.getLastRun().getResult() == Result.SUCCESS) {
					status1 = ToolsGWT.lblCnst.JobLastRunSuccess();
				}
				else if (o1.getLastRun().getResult() == Result.ERROR) {
					status1 = ToolsGWT.lblCnst.JobLastRunError();
				}
				else if (o1.getLastRun().getResult() == Result.RUNNING) {
					status1 = ToolsGWT.lblCnst.JobRunning();
				}
				else if (o1.getLastRun().getResult() == Result.UNKNOWN) {
					status1 = ToolsGWT.lblCnst.JobUnknown();
				}
				else {
					status1 = ToolsGWT.lblCnst.JobNeverLaunch();
				}

				if (o2.getLastRun() == null) {
					status2 = ToolsGWT.lblCnst.JobNeverLaunch();
				}
				else if (o2.getLastRun().getResult() == Result.SUCCESS) {
					status2 = ToolsGWT.lblCnst.JobLastRunSuccess();
				}
				else if (o2.getLastRun().getResult() == Result.ERROR) {
					status2 = ToolsGWT.lblCnst.JobLastRunError();
				}
				else if (o2.getLastRun().getResult() == Result.RUNNING) {
					status2 = ToolsGWT.lblCnst.JobRunning();
				}
				else if (o2.getLastRun().getResult() == Result.UNKNOWN) {
					status2 = ToolsGWT.lblCnst.JobUnknown();
				}
				else {
					status2 = ToolsGWT.lblCnst.JobNeverLaunch();
				}

				return status1.compareTo(status2);
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new SingleSelectionModel<Job>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadEvent();
	}

	@UiHandler("btnAddTask")
	public void onAddTaskClick(ClickEvent event) {
		AddTaskWizard wiz = new AddTaskWizard(this);
		wiz.center();
	}

	public void addOrEditJob(Job job) {
		showWaitPart(true);

		BiPortalService.Connect.getInstance().addOrEditJob(job, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				showWaitPart(false);

				loadEvent();
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToAddAJob());
			}
		});
	}

	@UiHandler("btnEditTask")
	public void onEditClick(ClickEvent event) {
		final Job selectedJob = selectionModel.getSelectedObject();
		if (selectedJob == null) {
			return;
		}
		
		BiPortalService.Connect.getInstance().getItemDto(selectedJob.getItemId(), new GwtCallbackWrapper<PortailRepositoryItem>(this, true, true) {
			@Override
			public void onSuccess(PortailRepositoryItem result) {
				AddTaskWizard wiz = new AddTaskWizard(ProcessManagerPanel.this, selectedJob, result);
				wiz.center();
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnDeleteTask")
	public void onDeleteClick(ClickEvent event) {
		deleteTask();
	}

	private void deleteTask() {
		final Job selectedJob = selectionModel.getSelectedObject();
		if (selectedJob == null) {
			return;
		}

		final InformationsDialog dialConfirm = new InformationsDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.ConfirmDeleteTask(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					showWaitPart(true);

					BiPortalService.Connect.getInstance().deleteJob(selectedJob, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							showWaitPart(false);

							caught.printStackTrace();

							ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToDeleteJob());
						}

						@Override
						public void onSuccess(Void result) {
							showWaitPart(false);

							MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.DeleteTaskSuccessfull());

							loadEvent();
						}
					});
				}
			}
		});
		dialConfirm.center();
	}

	@UiHandler("btnRunNowTask")
	public void onRunNowClick(ClickEvent event) {
		Job selectedJob = selectionModel.getSelectedObject();
		if (selectedJob == null) {
			return;
		}
		launchJob(selectedJob);
	}

	private void launchJob(Job job) {
		showWaitPart(true);

		BiPortalService.Connect.getInstance().launchJob(job, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLaunchJob());
			}

			@Override
			public void onSuccess(Void result) {
				showWaitPart(false);

				loadEvent();
			}
		});
	}

	@UiHandler("btnProperties")
	public void onPropertiesClick(ClickEvent event) {
		final Job selectedJob = selectionModel.getSelectedObject();
		showProperties(selectedJob);
	}

	private void showProperties(final Job selectedJob) {
		if (selectedJob == null) {
			return;
		}

		showWaitPart(true);

		BiPortalService.Connect.getInstance().getJobHistoric(selectedJob, new AsyncCallback<List<JobInstance>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToGetJobHistoric());
			}

			@Override
			public void onSuccess(List<JobInstance> result) {
				SchedulerHistoricDialog dial = new SchedulerHistoricDialog(selectedJob, result);
				dial.center();

				showWaitPart(false);
			}
		});
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Job selectedJob = selectionModel.getSelectedObject();
			btnEditTask.setVisible(selectedJob != null);
			btnDeleteTask.setVisible(selectedJob != null);
			btnRunNowTask.setVisible(selectedJob != null);
			btnProperties.setVisible(selectedJob != null);
		}
	};

	private class CustomCell extends TextCell {

		public CustomCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			consumedEvents.add("contextmenu");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			Job obj = (Job) context.getKey();
			if (event.getType().equals("dblclick")) {
				showProperties(obj);
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

}
