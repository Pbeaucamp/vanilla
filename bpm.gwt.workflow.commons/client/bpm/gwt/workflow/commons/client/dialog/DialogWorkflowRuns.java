package bpm.gwt.workflow.commons.client.dialog;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomCell;
import bpm.gwt.commons.client.custom.IDoubleClickHandler;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.popup.ISelectLevel;
import bpm.gwt.workflow.commons.client.popup.LevelPopup;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DialogWorkflowRuns extends AbstractDialogBox implements IDoubleClickHandler<WorkflowInstance>, ISelectLevel {

	private static final String DATE_FORMAT = "HH:mm:ss.SSS - dd/MM/yyyy";
	
	private static DialogStepsUiBinder uiBinder = GWT.create(DialogStepsUiBinder.class);

	interface DialogStepsUiBinder extends UiBinder<Widget, DialogWorkflowRuns> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
		String mainPanelExpand();
		String redBackground();
		String imgGrid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelGrid;
	
	private Workflow workflow;
	
	private ListDataProvider<WorkflowInstance> dataProvider;
	private ListHandler<WorkflowInstance> sortHandler;

	public DialogWorkflowRuns(Workflow workflow) {
		super(workflow.getName() + " : " + LabelsCommon.lblCnst.HistoricsWorkflow(), true, true);
		this.workflow = workflow;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsCommon.lblCnst.Close(), closeHandler);
		
		DataGrid<WorkflowInstance> grid = createGridSteps(workflow.getRuns());
		panelGrid.setWidget(grid);
	}
	
	private DataGrid<WorkflowInstance> createGridSteps(List<WorkflowInstance> steps) {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);
		
		DataGrid.Resources resources = new CustomResources();
		DataGrid<WorkflowInstance> dataGrid = new DataGrid<WorkflowInstance>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.setEmptyTableWidget(new Label(LabelsCommon.lblCnst.NoLogs()));
		dataGrid.setRowStyles(new RowStyles<WorkflowInstance>() {
			
			@Override
			public String getStyleNames(WorkflowInstance row, int rowIndex) {
				if(row.getResult() != Result.SUCCESS) {
					return style.redBackground();
				}
				return null;
			}
		});

		CustomCell<WorkflowInstance> cell = new CustomCell<WorkflowInstance>(this);
		Column<WorkflowInstance, String> colState = new Column<WorkflowInstance, String>(cell) {

			@Override
			public String getValue(WorkflowInstance object) {
				switch(object.getResult()) {
				case SUCCESS:
					return LabelsCommon.lblCnst.Success();
				case ERROR:
					return LabelsCommon.lblCnst.Error();
				case RUNNING:
					return LabelsCommon.lblCnst.Running();
				case UNKNOWN:
					return LabelsCommon.lblCnst.Unknown();
				}
				return LabelsCommon.lblCnst.Unknown();
			}
		};
		colState.setSortable(true);

		Column<WorkflowInstance, String> colStartDate = new Column<WorkflowInstance, String>(cell) {

			@Override
			public String getValue(WorkflowInstance object) {
				Date date = object.getStartDate();
				if (date == null || date.equals(new Date(0))) {
					return LabelsCommon.lblCnst.Unknown();
				}
				return dateFormatter.format(date);
			}
		};
		colStartDate.setSortable(true);

		Column<WorkflowInstance, String> colEndDate = new Column<WorkflowInstance, String>(cell) {

			@Override
			public String getValue(WorkflowInstance object) {
				Date date = object.getEndDate();
				if (date == null || date.equals(new Date(0))) {
					return LabelsCommon.lblCnst.Unknown();
				}
				return dateFormatter.format(date);
			}
		};
		colEndDate.setSortable(true);
		
		Column<WorkflowInstance, String> colDuration = new Column<WorkflowInstance, String>(cell) {

			@Override
			public String getValue(WorkflowInstance object) {
				if(object.getDuration() == -1) {
					return LabelsCommon.lblCnst.Unknown();
				}
				else {
					return object.getDurationAsString();
				}
			}
		};
		colDuration.setSortable(true);
		
		final ButtonImageCell outputCell = new ButtonImageCell(bpm.gwt.workflow.commons.client.images.Images.INSTANCE.ic_launch_black_24dp(), style.imgGrid());
		Column<WorkflowInstance, String> colOutput = new Column<WorkflowInstance, String>(outputCell) {

			@Override
			public String getValue(WorkflowInstance object) {
				return "";
			}
		};
		colOutput.setFieldUpdater(new FieldUpdater<WorkflowInstance, String>() {

			@Override
			public void update(int index, final WorkflowInstance object, String value) {
				DialogOutputs dial = new DialogOutputs(object);
				dial.center();
			}
		});

		final ButtonImageCell detailCell = new ButtonImageCell(bpm.gwt.workflow.commons.client.images.Images.INSTANCE.ic_view_list_black_18dp(), style.imgGrid());
		Column<WorkflowInstance, String> colDetail = new Column<WorkflowInstance, String>(detailCell) {

			@Override
			public String getValue(WorkflowInstance object) {
				return "";
			}
		};
		colDetail.setFieldUpdater(new FieldUpdater<WorkflowInstance, String>() {

			@Override
			public void update(int index, final WorkflowInstance object, String value) {
				showActivityLogs(object);
			}
		});

		final ButtonImageCell downloadCell = new ButtonImageCell(Images.INSTANCE.ic_file_download_black_18dp(), style.imgGrid());
		Column<WorkflowInstance, String> colDownload = new Column<WorkflowInstance, String>(downloadCell) {

			@Override
			public String getValue(WorkflowInstance object) {
				return "";
			}
		};
		colDownload.setFieldUpdater(new FieldUpdater<WorkflowInstance, String>() {

			@Override
			public void update(int index, final WorkflowInstance object, String value) {
				ScheduledCommand cmd = new ScheduledCommand() {
					
					@Override
					public void execute() {
						downloadLog(object, downloadCell.getClientX(), downloadCell.getClientY());
					}
				};
				Scheduler.get().scheduleDeferred(cmd);
			}
		});

		
		dataProvider = new ListDataProvider<WorkflowInstance>(steps);
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<WorkflowInstance>(dataProvider.getList());
		sortHandler.setComparator(colState, new Comparator<WorkflowInstance>() {
			
			@Override
			public int compare(WorkflowInstance o1, WorkflowInstance o2) {
				if(o1.getResult() == null) {
					return -1;
				}
				else if(o2.getResult() == null) {
					return 1;
				}
				return o1.getResult().compareTo(o2.getResult());
			}
		});
		sortHandler.setComparator(colStartDate, new Comparator<WorkflowInstance>() {
			
			@Override
			public int compare(WorkflowInstance o1, WorkflowInstance o2) {
				if(o1.getStartDate() == null) {
					return -1;
				}
				else if(o2.getStartDate() == null) {
					return 1;
				}
				
				return o2.getStartDate().before(o1.getStartDate()) ? -1 : o2.getStartDate().after(o1.getStartDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(colEndDate, new Comparator<WorkflowInstance>() {
			
			@Override
			public int compare(WorkflowInstance o1, WorkflowInstance o2) {
				if(o1.getEndDate() == null) {
					return -1;
				}
				else if(o2.getEndDate() == null) {
					return 1;
				}
				
				return o2.getEndDate().before(o1.getEndDate()) ? -1 : o2.getEndDate().after(o1.getEndDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(colDuration, new Comparator<WorkflowInstance>() {
			
			@Override
			public int compare(WorkflowInstance o1, WorkflowInstance o2) {
				return ((Long) (o1.getDuration())).compareTo((Long) o2.getDuration());
			}
		});

		dataGrid.addColumn(colState, LabelsCommon.lblCnst.Status());
		dataGrid.addColumn(colStartDate, LabelsCommon.lblCnst.StartDate());
		dataGrid.addColumn(colEndDate, LabelsCommon.lblCnst.EndDate());
		dataGrid.addColumn(colDuration, LabelsCommon.lblCnst.Duration());
		dataGrid.addColumn(colOutput, LabelsCommon.lblCnst.HistoryOutput());
		dataGrid.setColumnWidth(colOutput, "90px");
		dataGrid.addColumn(colDetail, LabelsCommon.lblCnst.Details());
		dataGrid.setColumnWidth(colDetail, "90px");
		dataGrid.addColumn(colDownload, LabelsCommon.lblCnst.Download());
		dataGrid.setColumnWidth(colDownload, "120px");
		
		dataGrid.addColumnSortHandler(sortHandler);

		SelectionModel<WorkflowInstance> selectionModel = new SingleSelectionModel<WorkflowInstance>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	@Override
	public void maximize(boolean maximize) {
		if (maximize) {
			panelGrid.removeStyleName(style.mainPanel());
			panelGrid.addStyleName(style.mainPanelExpand());
		}
		else {
			panelGrid.removeStyleName(style.mainPanelExpand());
			panelGrid.addStyleName(style.mainPanel());
		}
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	@Override
	public void run(WorkflowInstance item) {
		showActivityLogs(item);
	}
	
	private void showActivityLogs(final WorkflowInstance item) {
		if(item.getActivityLogs() != null && !item.getActivityLogs().isEmpty()) {
			DialogActivityLogs dial = new DialogActivityLogs(item.getActivityLogs());
			dial.center();
		}
		else {
			WorkflowsService.Connect.getInstance().getWorkflowRun(workflow, item, new GwtCallbackWrapper<List<ActivityLog>>(this, true) {
	
				@Override
				public void onSuccess(List<ActivityLog> result) {
					item.setActivityLogs(result);
					
					DialogActivityLogs dial = new DialogActivityLogs(item.getActivityLogs());
					dial.center();
				}
			}.getAsyncCallback());
		}
	}
	
	private void downloadLog(WorkflowInstance instance, int left, int top) {
		LevelPopup popup = new LevelPopup(this, instance);
		popup.setPopupPosition(left, top);
		popup.show();
	}

	@Override
	public void selectLevel(WorkflowInstance instance, Level level) {
		showWaitPart(true);
		
		WorkflowsService.Connect.getInstance().downloadLogRun(workflow, instance, level, new GwtCallbackWrapper<String>(this, true) {

			@Override
			public void onSuccess(String result) {

				String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_LOG;
				ToolsGWT.doRedirect(fullUrl);
				
//				String fullUrl = GWT.getHostPageBaseURL() + Constants.DOWNLOAD_SERVLET + "?" + Constants.KEY_STREAM + "=" + result;
//				Window.open(fullUrl, "_blank", "status=0,toolbar=0,menubar=0,location=0");
			}
		}.getAsyncCallback());
	}
}
