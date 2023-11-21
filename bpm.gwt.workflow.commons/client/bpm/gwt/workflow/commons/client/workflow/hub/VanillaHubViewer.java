package bpm.gwt.workflow.commons.client.workflow.hub;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCell;
import bpm.gwt.commons.client.custom.IDoubleClickHandler;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.custom.v2.CommonAsyncProvider;
import bpm.gwt.commons.client.custom.v2.CommonAsyncProvider.ILoadDataHandler;
import bpm.gwt.commons.client.custom.v2.SimplePagerFR;
import bpm.gwt.commons.client.dialog.ItemNameDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.IWorkflowManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.dialog.DialogProgressWorkflow;
import bpm.gwt.workflow.commons.client.dialog.DialogWorkflowRuns;
import bpm.gwt.workflow.commons.client.dialog.ParametersDialog;
import bpm.gwt.workflow.commons.client.dialog.SchedulerDialog;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.popup.ISelectInstance;
import bpm.gwt.workflow.commons.client.popup.WorkflowInstancePopup;
import bpm.gwt.workflow.commons.client.popup.WorkflowMenu;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.utils.CustomButtonCell;
import bpm.gwt.workflow.commons.client.utils.CustomImageCell;
import bpm.gwt.workflow.commons.client.utils.DatagridHandler;
import bpm.gwt.workflow.commons.client.utils.PlanificationImageCell;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public class VanillaHubViewer extends CompositeWaitPanel implements IDoubleClickHandler<Workflow>, DatagridHandler<Workflow>, ISelectInstance, IWorkflowManager, ILoadDataHandler {

	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	
	private static final int PAGE_SIZE = 30;

	private static ConsultPanelUiBinder uiBinder = GWT.create(ConsultPanelUiBinder.class);

	interface ConsultPanelUiBinder extends UiBinder<Widget, VanillaHubViewer> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String imgGrid();

		String imgPlanned();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel gridPanel;

	@UiField
	Image btnAdd, btnLaunch, btnHistoric, btnSchedule, btnClear;
	
	@UiField
	SimplePanel panelPager;

	@UiField
	TextHolderBox txtSearch;

	private IHubManager hubManager;
	private InfoUser infoUser;

	private DataGrid<Workflow> dataGrid;
	
	private CommonAsyncProvider<Workflow> dataProvider;
	private SingleSelectionModel<Workflow> selectionModel;
	private SimplePagerFR pager;

	private boolean isAdmin = false;

	public VanillaHubViewer(IHubManager hubManager, InfoUser infoUser) {
		this.hubManager = hubManager;
		this.infoUser = infoUser;
		this.isAdmin = infoUser.getUser().isSuperUser();

		initWidget(uiBinder.createAndBindUi(this));
		this.addStyleName(style.mainPanel());

		DataGrid<Workflow> datagrid = createGridWorkflows();
		gridPanel.setWidget(datagrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		this.pager = new SimplePagerFR(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(datagrid);
		
		panelPager.setWidget(pager);
		
		loadWorkflows();
		updateToolbar(false);

		defineInterfacePermission(isAdmin);
	}

	private void defineInterfacePermission(boolean isAdmin) {
		if (!isAdmin) {
			btnAdd.removeFromParent();
			btnSchedule.removeFromParent();
		}
		else if (!hubManager.canEditWorkflow()) {
			btnAdd.removeFromParent();
		}
	}

	private void updateToolbar(boolean visible) {
		btnLaunch.setVisible(visible);
		btnHistoric.setVisible(visible);
		btnSchedule.setVisible(visible);
	}

//	public void loadData(List<Workflow> result) {
//		if (result != null) {
////			dataProvider.setList(result);
////			sortHandler.setList(dataProvider.getList());
//		}
//		else {
////			dataProvider.setList(new ArrayList<Workflow>());
//		}
//
//		updateToolbar(false);
//	}

	private DataGrid<Workflow> createGridWorkflows() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		CustomImageCell<Workflow> imageCell = new CustomImageCell<>(this);
		Column<Workflow, ImageResource> colStatus = new Column<Workflow, ImageResource>(imageCell) {

			@Override
			public ImageResource getValue(Workflow object) {
				if (object.getLastRun() != null && object.getLastRun().getResult() == Result.ERROR) {
					return Images.INSTANCE.ic_flag_red_18dp();
				}
				else if (object.getLastRun() != null && object.getLastRun().getResult() == Result.SUCCESS) {
					return Images.INSTANCE.ic_flag_green_18dp();
				}
				else {
					return Images.INSTANCE.ic_flag_grey600_18dp();
				}
			}
		};

		CustomCell<Workflow> cell = new CustomCell<>(this);
		Column<Workflow, String> colSchedule = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				if (object.isScheduleDefine()) {
					return LabelsCommon.lblCnst.Yes();
				}
				else {
					return LabelsCommon.lblCnst.No();
				}
			}
		};
//		colSchedule.setSortable(true);

		Column<Workflow, String> colNextExecution = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				if (object.getNextExecution() != null && object.scheduleStillValid()) {
					return dateFormatter.format(object.getNextExecution());
				}
				else if (object.getNextExecution() != null) {
					return LabelsCommon.lblCnst.StopDateReached();
				}
				else {
					return LabelsCommon.lblCnst.NotDefined();
				}
			}
		};
//		colNextExecution.setSortable(true);
		
		final CustomButtonCell btnCellIsRunning = new CustomButtonCell();
		Column<Workflow, String> colIsRunning = new Column<Workflow, String>(btnCellIsRunning) {

			@Override
			public String getValue(Workflow object) {
				if (object.isRunning()) {
					return LabelsCommon.lblCnst.Running();
				}
				else {
					return LabelsCommon.lblCnst.NotRunning();
				}
			}
		};
		colIsRunning.setFieldUpdater(new FieldUpdater<Workflow, String>() {

			@Override
			public void update(int index, final Workflow object, String value) {
				if (object.isRunning()) {
					ScheduledCommand cmd = new ScheduledCommand() {
						
						@Override
						public void execute() {
							showInstances(object, btnCellIsRunning.getClientX(), btnCellIsRunning.getClientY());
						}
					};
					Scheduler.get().scheduleDeferred(cmd);
				}
			}
		});

		Column<Workflow, String> colName = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				return object.getName();
			}
		};
		colName.setSortable(true);
		colName.setDataStoreName("name");

		Column<Workflow, String> colDescription = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				return object.getDescription();
			}
		};
		colDescription.setSortable(true);
		colDescription.setDataStoreName("description");

		Column<Workflow, String> colCreationDate = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				if (object.getCreationDate() != null) {
					return dateFormatter.format(object.getCreationDate());
				}
				else {
					return LabelsCommon.lblCnst.Unknown();
				}
			}
		};
		colCreationDate.setSortable(true);
		colCreationDate.setDataStoreName("creationDate");

		Column<Workflow, String> colAuthor = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				return object.getAuthorName();
			}
		};
		colAuthor.setSortable(true);
		colAuthor.setDataStoreName("authorName");

		Column<Workflow, String> colModificationDate = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				if (object.getModificationDate() != null) {
					return dateFormatter.format(object.getModificationDate());
				}
				else {
					return LabelsCommon.lblCnst.NoModification();
				}
			}
		};
		colModificationDate.setSortable(true);
		colModificationDate.setDataStoreName("modificationDate");

		Column<Workflow, String> colModifiedBy = new Column<Workflow, String>(cell) {

			@Override
			public String getValue(Workflow object) {
				return object.getModificatorName();
			}
		};
		colModifiedBy.setSortable(true);
		colModifiedBy.setDataStoreName("modificatorName");

		PlanificationImageCell planificationCell = new PlanificationImageCell(style.imgPlanned());
		Column<Workflow, String> colActivate = new Column<Workflow, String>(planificationCell) {

			@Override
			public String getValue(Workflow object) {
				if (object.getSchedule() == null) {
					return PlanificationImageCell.PLANIFICATION;
				}
				else if (object.getSchedule().isOn()) {
					return PlanificationImageCell.DEACTIVATE;
				}
				else {
					return PlanificationImageCell.ACTIVATE;
				}
			}
		};
		colActivate.setFieldUpdater(new FieldUpdater<Workflow, String>() {

			@Override
			public void update(int index, Workflow object, String value) {
				if (value.equals(PlanificationImageCell.PLANIFICATION)) {
					scheduleWorkflow(object);
				}
				else if (value.equals(PlanificationImageCell.DEACTIVATE)) {
					object.getSchedule().setOn(false);

					updateWorkflow(object);
				}
				else if (value.equals(PlanificationImageCell.ACTIVATE)) {
					object.getSchedule().setOn(true);

					updateWorkflow(object);
				}
			}
		});

		ButtonImageCell editCell = new ButtonImageCell(Images.INSTANCE.ic_edit_black_18dp(), style.imgGrid());
		Column<Workflow, String> colEdit = new Column<Workflow, String>(editCell) {

			@Override
			public String getValue(Workflow object) {
				return "";
			}
		};
		colEdit.setFieldUpdater(new FieldUpdater<Workflow, String>() {

			@Override
			public void update(int index, Workflow object, String value) {
				hubManager.displayWorkflow(object);
			}
		});

		ButtonImageCell duplicateCell = new ButtonImageCell(CommonImages.INSTANCE.ic_control_point_duplicate_black_18dp(), style.imgGrid());
		Column<Workflow, String> colDuplicate = new Column<Workflow, String>(duplicateCell) {

			@Override
			public String getValue(Workflow object) {
				return "";
			}
		};
		colDuplicate.setFieldUpdater(new FieldUpdater<Workflow, String>() {

			@Override
			public void update(int index, final Workflow object, String value) {
				final ItemNameDialog dial = new ItemNameDialog(LabelsConstants.lblCnst.DuplicateWorkflow(), false);
				dial.center();
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						String name = dial.getName();
						if (!name.isEmpty()) {
							duplicateWorkflow(object, name);
						}
					}
				});
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<Workflow, String> colDelete = new Column<Workflow, String>(deleteCell) {

			@Override
			public String getValue(Workflow object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Workflow, String>() {

			@Override
			public void update(int index, final Workflow object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsCommon.lblCnst.Information(), LabelsCommon.lblCnst.Confirmation(), LabelsCommon.lblCnst.Cancel(), LabelsCommon.lblCnst.DeleteWorkflowConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteWorkflow(object);
						}
					}
				});
				dial.center();
			}
		});

//		sortHandler = new ListHandler<Workflow>(new ArrayList<Workflow>());
//		sortHandler.setComparator(colName, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
//		sortHandler.setComparator(colSchedule, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				return ((Boolean) o1.isScheduleDefine()).compareTo((Boolean) o2.isScheduleDefine());
//			}
//		});
//		sortHandler.setComparator(colNextExecution, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				if (o1.getNextExecution() == null) {
//					return -1;
//				}
//				else if (o2.getNextExecution() == null) {
//					return 1;
//				}
//
//				return o2.getNextExecution().before(o1.getNextExecution()) ? -1 : o2.getNextExecution().after(o1.getNextExecution()) ? 1 : 0;
//			}
//		});
//		sortHandler.setComparator(colDescription, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				return o1.getDescription().compareTo(o2.getDescription());
//			}
//		});
//		sortHandler.setComparator(colCreationDate, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				if (o1.getCreationDate() == null) {
//					return -1;
//				}
//				else if (o2.getCreationDate() == null) {
//					return 1;
//				}
//
//				return o2.getCreationDate().before(o1.getCreationDate()) ? -1 : o2.getCreationDate().after(o1.getCreationDate()) ? 1 : 0;
//			}
//		});
//		sortHandler.setComparator(colAuthor, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				return o1.getAuthorName().compareTo(o2.getAuthorName());
//			}
//		});
//		sortHandler.setComparator(colModificationDate, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				if (o1.getModificationDate() == null) {
//					return -1;
//				}
//				else if (o2.getModificationDate() == null) {
//					return 1;
//				}
//
//				return o2.getModificationDate().before(o1.getModificationDate()) ? -1 : o2.getModificationDate().after(o1.getModificationDate()) ? 1 : 0;
//			}
//		});
//		sortHandler.setComparator(colModifiedBy, new Comparator<Workflow>() {
//
//			@Override
//			public int compare(Workflow o1, Workflow o2) {
//				if (o1.getModificatorName() == null) {
//					return -1;
//				}
//				else if (o2.getModificatorName() == null) {
//					return 1;
//				}
//
//				return o1.getModificatorName().compareTo(o2.getModificatorName());
//			}
//		});

		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<Workflow>(PAGE_SIZE, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colStatus, LabelsCommon.lblCnst.Status());
		dataGrid.setColumnWidth(colStatus, "50px");
		dataGrid.addColumn(colSchedule, LabelsCommon.lblCnst.Scheduled());
		dataGrid.setColumnWidth(colSchedule, "70px");
		dataGrid.addColumn(colNextExecution, LabelsCommon.lblCnst.NextExecution());
		dataGrid.setColumnWidth(colNextExecution, "150px");
		dataGrid.addColumn(colIsRunning, LabelsCommon.lblCnst.IsRunning());
		dataGrid.setColumnWidth(colIsRunning, "150px");
		dataGrid.addColumn(colName, LabelsCommon.lblCnst.Name());
		dataGrid.setColumnWidth(colName, "250px");
		dataGrid.addColumn(colDescription, LabelsCommon.lblCnst.Description());
		dataGrid.setColumnWidth(colDescription, "300px");
		dataGrid.addColumn(colCreationDate, LabelsCommon.lblCnst.CreationDate());
		dataGrid.setColumnWidth(colCreationDate, "150px");
		dataGrid.addColumn(colAuthor, LabelsCommon.lblCnst.Author());
		dataGrid.setColumnWidth(colAuthor, "150px");
		dataGrid.addColumn(colModificationDate, LabelsCommon.lblCnst.ModificationDate());
		dataGrid.setColumnWidth(colModificationDate, "150px");
		dataGrid.addColumn(colModifiedBy, LabelsCommon.lblCnst.ModifiedBy());
		dataGrid.setColumnWidth(colModifiedBy, "150px");
		if (isAdmin) {
			dataGrid.addColumn(colActivate, LabelsCommon.lblCnst.Planification());
			dataGrid.setColumnWidth(colActivate, "150px");
			if (hubManager.canEditWorkflow()) {
				dataGrid.addColumn(colEdit);
				dataGrid.setColumnWidth(colEdit, "70px");
			}
			dataGrid.addColumn(colDuplicate);
			dataGrid.setColumnWidth(colDuplicate, "70px");
			dataGrid.addColumn(colDelete);
			dataGrid.setColumnWidth(colDelete, "70px");
		}
		
	    AsyncHandler sortHandler = new AsyncHandler(dataGrid);
		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider = new CommonAsyncProvider<Workflow>(this);
		dataProvider.addDataDisplay(dataGrid);
		
//		dataProvider = new ListDataProvider<Workflow>();
//		dataProvider.addDataDisplay(dataGrid);

		this.selectionModel = new SingleSelectionModel<Workflow>();
		selectionModel.addSelectionChangeHandler(selectionChange);
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}
	
	private void showInstances(Workflow workflow, int left, int top) {
		WorkflowInstancePopup popup = new WorkflowInstancePopup(this, workflow, workflow.getRunningRuns());
		popup.setPopupPosition(left, top);
		popup.show();
	}

	@Override
	public void selectInstance(Workflow workflow, WorkflowInstance instance) {
		DialogProgressWorkflow dial = new DialogProgressWorkflow(workflow, instance);
		dial.center();
	}
	
	public void loadWorkflows() {
		if (pager != null) {
			pager.firstPage();
		}
		
		loadData(0, PAGE_SIZE);

		updateToolbar(false);
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadWorkflows();
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		hubManager.displayWorkflow(null);
	}

	@UiHandler("btnLaunch")
	public void onLaunchClick(ClickEvent event) {
		Workflow workflow = selectionModel.getSelectedObject();
		if (workflow != null) {
			runWorkflow(workflow);
		}
	}

	@UiHandler("btnHistoric")
	public void onHistoricClick(ClickEvent event) {
		Workflow workflow = selectionModel.getSelectedObject();
		if (workflow != null) {
			showHistoricsWorkflow(workflow);
		}
	}

	@UiHandler("btnSchedule")
	public void onScheduleClick(ClickEvent event) {
		Workflow workflow = selectionModel.getSelectedObject();
		if (workflow != null) {
			scheduleWorkflow(workflow);
		}
	}

	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		txtSearch.setText("");
		btnClear.setVisible(false);
		loadWorkflows();
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		btnClear.setVisible(true);
		loadWorkflows();
	}

	@Override
	public void onRightClick(Workflow item, NativeEvent event) {
		WorkflowMenu itemMenu = new WorkflowMenu(this, item);
		itemMenu.setAutoHideEnabled(true);
		itemMenu.setPopupPosition(event.getClientX(), event.getClientY());
		itemMenu.show();
	}

	@Override
	public void onDoubleClick(Workflow item) {
		showHistoricsWorkflow(item);
	}

	@Override
	public void run(Workflow item) {
		showHistoricsWorkflow(item);
	}

	private Handler selectionChange = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			Workflow workflow = selectionModel.getSelectedObject();
			updateToolbar(workflow != null);
		}
	};

	public void redraw() {
		dataGrid.redraw();
	}
	
	@Override
	public void loadData(int start, int length) {
		String query = txtSearch.getText();
        ColumnSortList sortList = dataGrid.getColumnSortList();
        
        DataSort dataSort = null;
        if (sortList.size() > 0) {
            String columnName = sortList.get(0).getColumn().getDataStoreName();
            boolean ascending = sortList.get(0).isAscending();
            dataSort = new DataSort(columnName, ascending);
        }
        else {
        	dataSort = new DataSort("name", true);
        }

		WorkflowsService.Connect.getInstance().getWorkflows(query, start, length, dataSort, new GwtCallbackWrapper<DataWithCount<Workflow>>(this, true, true) {

			@Override
			public void onSuccess(DataWithCount<Workflow> result) {
				if (result != null) {
					dataProvider.updateRowCount(((Long) result.getItemsCount()).intValue(), true);
					dataProvider.updateRowData(start, result.getItems());
				}
			}
		}.getAsyncCallback());
	}
	
	@Override
	public void runWorkflow(final Workflow workflow) {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().getWorkflowParameters(workflow, new GwtCallbackWrapper<List<Parameter>>(this, true) {

			@Override
			public void onSuccess(final List<Parameter> parameters) {
				if (parameters != null && !parameters.isEmpty()) {
					boolean onlyDB = true;
					for (Parameter param : parameters) {
						if (param.getParameterType() != TypeParameter.DB)  {
							onlyDB = false;
							break;
						}
					}
					
					if (onlyDB) {
						runWorkflow(workflow, parameters);
					}
					else {
						List<ListOfValues> lovs = getResourceManager() != null ? getResourceManager().getListOfValues() : null;
						
						ParametersDialog dial = new ParametersDialog(VanillaHubViewer.this, workflow, parameters, lovs);
						dial.center();
					}
					
				}
				else {
					runWorkflow(workflow, new ArrayList<Parameter>());
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void runWorkflow(final Workflow workflow, final List<Parameter> parameters) {
		WorkflowsService.Connect.getInstance().initWorkflow(workflow, new GwtCallbackWrapper<String>(null, false) {

			@Override
			public void onSuccess(String result) {
				WorkflowInstance instance = new WorkflowInstance(result, workflow);
				
				DialogProgressWorkflow dial = new DialogProgressWorkflow(workflow, instance);
				dial.center();

				User launcher = infoUser.getUser();
				WorkflowsService.Connect.getInstance().runWorkflow(workflow, result, launcher, parameters, new GwtCallbackWrapper<WorkflowInstance>(null, false) {

					@Override
					public void onSuccess(WorkflowInstance result) {
						loadWorkflows();
					}
				}.getAsyncCallback());
			}
		}.getAsyncCallback());
	}

	@Override
	public void scheduleWorkflow(final Workflow workflow) {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().getWorkflowParameters(workflow, new GwtCallbackWrapper<List<Parameter>>(this, true) {

			@Override
			public void onSuccess(final List<Parameter> parameters) {
				if (parameters != null && !parameters.isEmpty()) {

					List<ListOfValues> lovs = getResourceManager() != null ? getResourceManager().getListOfValues() : null;

					SchedulerDialog dial = new SchedulerDialog(VanillaHubViewer.this, workflow, parameters, lovs);
					dial.center();
				}
				else {
					SchedulerDialog dial = new SchedulerDialog(VanillaHubViewer.this, workflow, null, null);
					dial.center();
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void showHistoricsWorkflow(final Workflow workflow) {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().getWorkflowRuns(workflow, new GwtCallbackWrapper<List<WorkflowInstance>>(this, true) {

			@Override
			public void onSuccess(List<WorkflowInstance> instances) {
				workflow.setRuns(instances);
				
				DialogWorkflowRuns dial = new DialogWorkflowRuns(workflow);
				dial.center();
			}
		}.getAsyncCallback());
	}
	
	@Override
	public void deleteWorkflow(Workflow workflow) {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().removeWorkflow(workflow, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				loadWorkflows();
			}
		}.getAsyncCallback());
	}
	
	@Override
	public void duplicateWorkflow(Workflow workflow, String name) {
		WorkflowsService.Connect.getInstance().duplicateWorkflow(workflow.getId(), name, new GwtCallbackWrapper<Workflow>(this, true, true) {

			@Override
			public void onSuccess(Workflow result) {
				loadWorkflows();
			}
		}.getAsyncCallback());
	}

	@Override
	public void updateWorkflow(Workflow workflow) {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().manageWorkflow(workflow, true, new GwtCallbackWrapper<Workflow>(this, true) {

			@Override
			public void onSuccess(Workflow result) {
				loadWorkflows();
			}
		}.getAsyncCallback());
	}

	public IResourceManager getResourceManager() {
		return hubManager.getResourceManager();
	}

	@Override
	public boolean canRunWorkflow() {
		return true;
	}

	@Override
	public boolean canShowHistoricsWorkflow() {
		return true;
	}

	@Override
	public boolean canScheduleWorkflow() {
		return true;
	}

	@Override
	public boolean canUpdateWorkflow() {
		return hubManager.canEditWorkflow();
	}

	@Override
	public boolean canDeleteWorkflow() {
		return true;
	}
}
