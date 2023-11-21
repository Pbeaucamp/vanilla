package bpm.gwt.workflow.commons.client.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DialogProgressWorkflow extends AbstractDialogBox {

	private static final String DATE_FORMAT = "HH:mm:ss.SSS - dd/MM/yyyy";

	private static DialogStepsUiBinder uiBinder = GWT.create(DialogStepsUiBinder.class);

	interface DialogStepsUiBinder extends UiBinder<Widget, DialogProgressWorkflow> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String mainPanelExpand();

		String redBackground();

		String imgGrid();

		String imgResult();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	@UiField
	HTMLPanel panelResult;

	@UiField
	Label lblResult;

	@UiField
	Image imgResult;

	@UiField
	Button btnResult, btnOutputs;

	private Workflow workflow;

	private WorkflowInstance instance;

	private ListDataProvider<ActivityLog> dataProvider;

	private boolean finish = false;

	public DialogProgressWorkflow(Workflow workflow, WorkflowInstance workflowInstance) {
		super(LabelsCommon.lblCnst.Progress() + " : " + workflow.getName(), true, true);
		this.workflow = workflow;
		this.instance = workflowInstance;
		
//		instance.setWorkflow(workflow);

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsCommon.lblCnst.Close(), closeHandler);

		DataGrid<ActivityLog> grid = createGridSteps();
		panelGrid.setWidget(grid);

		refreshProgress();
	}

	private void refreshProgress() {
		WorkflowsService.Connect.getInstance().getWorkflowProgress(workflow, instance.getUuid(), new GwtCallbackWrapper<WorkflowInstance>(null, false) {

			@Override
			public void onSuccess(WorkflowInstance instance) {
				loadData(instance != null && instance.getActivityLogs() != null ? instance.getActivityLogs() : new ArrayList<ActivityLog>());
				if (instance != null) {
					setTitle(LabelsCommon.lblCnst.Progress() + " : " + instance.getWorkflowName() + " - " + instance.getWorkflowNumber() + "/" + instance.getTotalWorkflow());
				}
				updateResult(instance);

				if (!finish || instance.getResult() == null) {
					Timer timer = new Timer() {
						@Override
						public void run() {
							refreshProgress();
						}
					};

					timer.schedule(2000);
				}
			}
		}.getAsyncCallback());
	}

	private void loadData(List<ActivityLog> logs) {
		dataProvider.setList(logs);
	}

	private DataGrid<ActivityLog> createGridSteps() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<ActivityLog> dataGrid = new DataGrid<ActivityLog>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.setEmptyTableWidget(new Label(LabelsCommon.lblCnst.NoLogs()));

		Column<ActivityLog, String> colActivityName = new Column<ActivityLog, String>(new TextCell()) {

			@Override
			public String getValue(ActivityLog object) {
				return object.getActivityName();
			}
		};
		colActivityName.setSortable(true);

		Column<ActivityLog, String> colStartDate = new Column<ActivityLog, String>(new TextCell()) {

			@Override
			public String getValue(ActivityLog object) {
				Date date = object.getStartDate();
				if (date == null || date.equals(new Date(0))) {
					return LabelsCommon.lblCnst.Unknown();
				}
				return dateFormatter.format(date);
			}
		};
		colStartDate.setSortable(true);

		Column<ActivityLog, String> colDuration = new Column<ActivityLog, String>(new TextCell()) {

			@Override
			public String getValue(ActivityLog object) {
				if (object.getDuration() == -1) {
					return LabelsCommon.lblCnst.Unknown();
				}
				else {
					return object.getDurationAsString();
				}
			}
		};
		colDuration.setSortable(true);

		Column<ActivityLog, String> colNumberFileTreated = new Column<ActivityLog, String>(new TextCell()) {

			@Override
			public String getValue(ActivityLog object) {
				if (object.getLoop() > 1) {
					return object.getLoop() + "/" + object.getNumberTotalOfFiles();
				}
				else {
					return "";
				}
			}
		};
		colNumberFileTreated.setSortable(true);

		dataProvider = new ListDataProvider<ActivityLog>();
		dataProvider.addDataDisplay(dataGrid);

		dataGrid.addColumn(colActivityName, LabelsCommon.lblCnst.Name());
		// dataGrid.addColumn(colState, Labels.lblCnst.Status());
		dataGrid.addColumn(colStartDate, LabelsCommon.lblCnst.StartDate());
		// dataGrid.addColumn(colEndDate, Labels.lblCnst.EndDate());
		dataGrid.addColumn(colDuration, LabelsCommon.lblCnst.Duration());
		dataGrid.addColumn(colNumberFileTreated, LabelsCommon.lblCnst.FilesTreated());

		SelectionModel<ActivityLog> selectionModel = new SingleSelectionModel<ActivityLog>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	@UiHandler("btnResult")
	public void onStopWorkflowClick(ClickEvent event) {
		if (!finish) {
			showWaitPart(true);

			WorkflowsService.Connect.getInstance().stopWorkflow(workflow.getId(), instance.getUuid(), new GwtCallbackWrapper<Void>(this, true) {

				@Override
				public void onSuccess(Void result) {
				}
			}.getAsyncCallback());
		}
		else {
			String logs = instance.getLogs(Level.ALL);

			DialogLogDetails dial = new DialogLogDetails(workflow.getName(), logs);
			dial.center();
		}
	}

	private void updateResult(WorkflowInstance instance) {
		this.instance = instance;
		this.finish = instance != null && instance.isFinish();

		if (finish && instance != null && instance.getResult() != null) {
			if (instance.getResult() == Result.SUCCESS) {
				imgResult.setResource(Images.INSTANCE.success());
				lblResult.setText(LabelsCommon.lblCnst.SuccessRunWorkflow());
			}
			else if (instance.isStopByUser()) {
				imgResult.setResource(Images.INSTANCE.failed());
				lblResult.setText(LabelsCommon.lblCnst.StopByUser());
			}
			else {
				imgResult.setResource(Images.INSTANCE.failed());
				lblResult.setText(LabelsCommon.lblCnst.FailedRunWorkflow());
			}
			panelResult.setVisible(true);
			imgResult.addStyleName(style.imgResult());

			btnResult.setText(LabelsCommon.lblCnst.ShowLogs());
			
			if(instance.getOutputs() != null && !instance.getOutputs().isEmpty()) {
				btnOutputs.setVisible(true);
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		finish = true;
	}
	
	@UiHandler("btnOutputs")
	public void onOutputs(ClickEvent e) {
		DialogOutputs dial = new DialogOutputs(instance);
		dial.center();
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
}
