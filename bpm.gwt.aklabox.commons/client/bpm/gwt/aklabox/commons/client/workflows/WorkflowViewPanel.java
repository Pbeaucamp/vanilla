package bpm.gwt.aklabox.commons.client.workflows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.aklabox.workflow.core.model.ActivityLog;
import bpm.aklabox.workflow.core.model.IInstanceStatus;
import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.Log;
import bpm.aklabox.workflow.core.model.Workflow;
import bpm.aklabox.workflow.core.model.Workflow.Type;
import bpm.aklabox.workflow.core.model.WorkflowLog;
import bpm.aklabox.workflow.core.model.activities.Activity;
import bpm.aklabox.workflow.core.model.activities.EndActivity;
import bpm.aklabox.workflow.core.model.activities.StartActivity;
import bpm.aklabox.workflow.core.model.activities.XorActivity;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.ActivityHelper;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.CustomResources;
import bpm.gwt.aklabox.commons.client.utils.LabelHelper;
import bpm.gwt.aklabox.commons.client.utils.WaitDialog;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class WorkflowViewPanel extends ChildDialogComposite {

	private static WorkflowViewPanelUiBinder uiBinder = GWT.create(WorkflowViewPanelUiBinder.class);

	interface WorkflowViewPanelUiBinder extends UiBinder<Widget, WorkflowViewPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
		String mainPanelExpand();
		String redBackground();
		String greenBackground();
		String blueBackground();
		String imgGrid();
		String rowGrid();
		String openBackground();
		String suspendBackground();
		String finishBackground();
		String archivedBackground();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelGrid;
	
	@UiField
	HTMLPanel workflowStatus;
	
	@UiField
	Label lblStatus, lblFolderStatus;
	
	@UiField
	Image imgFolderStatus;
	
	private Tree tree;

	private ListDataProvider<ActivityLog> dataProvider;
	private ListHandler<ActivityLog> sortHandler;
	private Workflow workflow;
	private Instance instance;
	
	private DateTimeFormat dateFormatter = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
	
	public WorkflowViewPanel(Tree tree) {
		initWidget(uiBinder.createAndBindUi(this));
		this.tree = tree;
		
		if(tree.getAssociatedWorkflow() != 0){
			WaitDialog.showWaitPart(true);
			AklaCommonService.Connect.getService().getWorkflow(tree.getAssociatedWorkflow(), new AsyncCallback<Workflow>() {

				@Override
				public void onFailure(Throwable caught) {
					WaitDialog.showWaitPart(false);
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(Workflow result) {
					workflow = result;
//					AklaCommonService.Connect.getService().getActivitiesByWorkflow(result, new AsyncCallback<List<Activity>>() {
//						
//						@Override
//						public void onSuccess(List<Activity> result) {
							AklaCommonService.Connect.getService().getLastWorkflowInstance(workflow, new AsyncCallback<Instance>() {
								
								@Override
								public void onSuccess(Instance result2) {
									WaitDialog.showWaitPart(false);
									instance = result2;
									DataGrid<ActivityLog> grid = createGridSteps(instance.getActivityLogs());
									panelGrid.setWidget(grid);
								}
								
								@Override
								public void onFailure(Throwable caught) {
									WaitDialog.showWaitPart(false);
									caught.printStackTrace();
								}
							});
//						}
//						
//						@Override
//						public void onFailure(Throwable caught) {
//							WaitDialog.showWaitPart(false);
//							caught.printStackTrace();
//						}
//					});
					
				}
			});
			
			if(tree.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_OPEN){
				workflowStatus.addStyleName(style.openBackground());
				lblStatus.setText(LabelsConstants.lblCnst.Open2());
			} else if(tree.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_SUSPEND){
				workflowStatus.addStyleName(style.suspendBackground());
				lblStatus.setText(LabelsConstants.lblCnst.Suspend());
			} else if(tree.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_FINISHED){
				workflowStatus.addStyleName(style.finishBackground());
				lblStatus.setText(LabelsConstants.lblCnst.Finished());
			} else if(tree.getWorkflowProcessStatus() == AklaboxConstant.WORKFLOW_PROCESS_ARCHIVED){
				workflowStatus.addStyleName(style.archivedBackground());
				lblStatus.setText(LabelsConstants.lblCnst.Archived());
			} else {
				workflowStatus.addStyleName(style.openBackground());
				lblStatus.setText(LabelsConstants.lblCnst.Open2());
			}
			if(tree.getWorkflowFolderStatus() == AklaboxConstant.WORKFLOW_FOLDER_STUDY){
				imgFolderStatus.setResource(CommonImages.INSTANCE.ic_workflow_etude());
				lblFolderStatus.setText(LabelsConstants.lblCnst.Study());
			} else if(tree.getWorkflowFolderStatus() == AklaboxConstant.WORKFLOW_FOLDER_INSTRUCTION){
				imgFolderStatus.setResource(CommonImages.INSTANCE.ic_workflow_instruction());
				lblFolderStatus.setText(LabelsConstants.lblCnst.Instruction2());
			} else if(tree.getWorkflowFolderStatus() == AklaboxConstant.WORKFLOW_FOLDER_WAITING){
				imgFolderStatus.setResource(CommonImages.INSTANCE.ic_workflow_attente());
				lblFolderStatus.setText(LabelsConstants.lblCnst.Waiting());
			} else if(tree.getWorkflowFolderStatus() == AklaboxConstant.WORKFLOW_FOLDER_FINALIZATION){
				imgFolderStatus.setResource(CommonImages.INSTANCE.ic_workflow_finalisation());
				lblFolderStatus.setText(LabelsConstants.lblCnst.Finalization());
			} else {
				imgFolderStatus.setUrl("");
				lblFolderStatus.setText("");
			}
			
			workflowStatus.setVisible(true);
		} else {
			DataGrid<ActivityLog> grid = createGridSteps(new ArrayList<ActivityLog>());
			panelGrid.setWidget(grid);
			workflowStatus.setVisible(false);
		}
		
		
		
	}
	
	public WorkflowViewPanel(Instance instance) {
		initWidget(uiBinder.createAndBindUi(this));
		this.tree = null;
		this.instance = instance;
		this.workflow = instance.getWorkflow();
		DataGrid<ActivityLog> grid = createGridSteps(instance.getActivityLogs());
		panelGrid.setWidget(grid);
		
		
		if(instance.getInstanceStatus().equals(IInstanceStatus.RUNNING)){
			workflowStatus.addStyleName(style.openBackground());
			lblStatus.setText(LabelsConstants.lblCnst.Running());
		} else if(instance.getInstanceStatus().equals(IInstanceStatus.STAND_BY)){
			workflowStatus.addStyleName(style.suspendBackground());
			lblStatus.setText(LabelsConstants.lblCnst.StandBy());
		} else if(instance.getInstanceStatus().equals(IInstanceStatus.STOPED)){
			workflowStatus.addStyleName(style.suspendBackground());
			lblStatus.setText(LabelsConstants.lblCnst.Stopped());
		} else if(instance.getInstanceStatus().equals(IInstanceStatus.FINISH)){
			workflowStatus.addStyleName(style.finishBackground());
			lblStatus.setText(LabelsConstants.lblCnst.Finished());
		} else {
			workflowStatus.addStyleName(style.openBackground());
			lblStatus.setText(LabelsConstants.lblCnst.Open2());
		}
		
		imgFolderStatus.setVisible(false);
		lblFolderStatus.setVisible(false);
		
		workflowStatus.setVisible(true);
		
	}

	private DataGrid<ActivityLog> createGridSteps(List<ActivityLog> steps) {
		
		DataGrid.Resources resources = new CustomResources();
		DataGrid<ActivityLog> dataGrid = new DataGrid<ActivityLog>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("350px");
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoAssociatedWorkflow()));
		dataGrid.setRowStyles(new RowStyles<ActivityLog>() {
			
			@Override
			public String getStyleNames(ActivityLog row, int rowIndex) {
				String rowStyle = style.rowGrid();
				if(row.getResult() != null){
					switch(row.getResult()) {
					case SUCCESS:
						rowStyle += " " + style.greenBackground();
						break;
					case ERROR:
						rowStyle += " " + style.redBackground();
						break;
					case RUNNING:
						rowStyle += " " + style.blueBackground();
						break;
					case UNKNOWN:
						
					default:
						
					}
				} 
				//rowStyle += " " + style.blueBackground();
				return rowStyle;
			}
		});

		TextCell cell = new TextCell();
		Column<ActivityLog, String> colActivityName = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				return object.getActivity().getActivityName();
			}
		};
		colActivityName.setSortable(true);
		
		Column<ActivityLog, String> colState = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				if(object.getResult() != null){
					switch(object.getResult()) {
					case SUCCESS:
						return LabelsConstants.lblCnst.Success();
					case ERROR:
						return LabelsConstants.lblCnst.Error();
					case RUNNING:
						return LabelsConstants.lblCnst.Running();
					case UNKNOWN:
						return LabelsConstants.lblCnst.Unknown();
					}
					return "";
				} else {
					return "";
				}
				
			}
		};
		colState.setSortable(true);
		
		Column<ActivityLog, String> colAuthor = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				if(instance.getWorkflow().getTypeWorkflow() != Type.ORBEON){
					return object.getAssignedUser();
				} else {
					Activity act = (Activity)object.getActivity();
					if(act instanceof StartActivity || act instanceof EndActivity || act instanceof XorActivity){
						return "";
					} else {
						return ActivityHelper.getAssignedUserOrbeon(act);
//						return act.getOrgaElement().getName() + " - " + LabelHelper.functionToLabel(act.getOrgaFunction());
					}
					
				}
			}
		};
		colAuthor.setSortable(true);

		Column<ActivityLog, String> colStartDate = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				Date date = object.getStartDate();
				if (date == null || date.equals(new Date(0))) {
					return "";
				}
				return dateFormatter.format(date);
			}
		};
		colStartDate.setSortable(true);

		Column<ActivityLog, String> colEndDate = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				Date date = object.getEndDate();
				if (date == null || date.equals(new Date(0))) {
					return "";
				}
				return dateFormatter.format(date);
			}
		};
		colEndDate.setSortable(true);
		
		Column<ActivityLog, String> colDuration = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				if(object.getDuration() == -1) {
					return "";
				}
				else {
					return object.getDurationAsString();
				}
			}
		};
		colDuration.setSortable(true);

		Column<ActivityLog, String> colActivityDesc = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				return object.getActivity().getDescription();
			}
		};
		colActivityDesc.setSortable(true);
		
		Column<ActivityLog, String> colLog = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				return displayLogs(object.getLogs());
			}
		};
		
		dataProvider = new ListDataProvider<ActivityLog>(steps);
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<ActivityLog>(dataProvider.getList());
		sortHandler.setComparator(colActivityName, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				return o1.getActivity().getActivityName().compareTo(o2.getActivity().getActivityName());
			}
		});
		sortHandler.setComparator(colActivityDesc, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				return o1.getActivity().getDescription().compareTo(o2.getActivity().getDescription());
			}
		});
		sortHandler.setComparator(colState, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				if(o1.getResult() == null) {
					return -1;
				}
				else if(o2.getResult() == null) {
					return 1;
				}
				return o1.getResult().compareTo(o2.getResult());
			}
		});
		sortHandler.setComparator(colAuthor, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				return o1.getAssignedUser().compareTo(o2.getAssignedUser());
			}
		});
		sortHandler.setComparator(colStartDate, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				if(o1.getStartDate() == null) {
					return -1;
				}
				else if(o2.getStartDate() == null) {
					return 1;
				}
				
				return o2.getStartDate().before(o1.getStartDate()) ? -1 : o2.getStartDate().after(o1.getStartDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(colEndDate, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				if(o1.getEndDate() == null) {
					return -1;
				}
				else if(o2.getEndDate() == null) {
					return 1;
				}
				
				return o2.getEndDate().before(o1.getEndDate()) ? -1 : o2.getEndDate().after(o1.getEndDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(colDuration, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				return ((Long) (o1.getDuration())).compareTo((Long) o2.getDuration());
			}
		});

		dataGrid.addColumn(colActivityName, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(colState, LabelsConstants.lblCnst.Status());
		if(instance.getWorkflow().getTypeWorkflow() == Type.ORBEON){
			dataGrid.addColumn(colAuthor, LabelsConstants.lblCnst.Operator());
		}
		
		dataGrid.addColumn(colStartDate, LabelsConstants.lblCnst.StartDate());
		dataGrid.addColumn(colEndDate, LabelsConstants.lblCnst.EndDate());
//		dataGrid.addColumn(colDuration, LabelsConstants.lblCnst.Duration());
//		dataGrid.addColumn(colActivityDesc, LabelsConstants.lblCnst.Description());
		dataGrid.addColumn(colLog, LabelsConstants.lblCnst.logs());
		dataGrid.setColumnWidth(colLog, "30%");
		
		dataGrid.addColumnSortHandler(sortHandler);

		SelectionModel<ActivityLog> selectionModel = new SingleSelectionModel<ActivityLog>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}
	
	private String displayLogs(List<Log> logs){
		String res = "";
		for(Log log : logs){
			res += log.getMessage() + "\n";
		}
		if(res.length() > 2){
			res.substring(0, res.length()-2);
		}
		
		return res;
	}
}
