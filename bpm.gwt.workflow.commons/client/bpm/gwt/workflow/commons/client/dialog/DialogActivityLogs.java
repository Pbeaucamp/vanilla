package bpm.gwt.workflow.commons.client.dialog;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomCell;
import bpm.gwt.commons.client.custom.IDoubleClickHandler;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.Result;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
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

public class DialogActivityLogs extends AbstractDialogBox implements IDoubleClickHandler<ActivityLog> {

	private static final String DATE_FORMAT = "HH:mm:ss.SSS - dd/MM/yyyy";
	
	private static DialogStepsUiBinder uiBinder = GWT.create(DialogStepsUiBinder.class);

	interface DialogStepsUiBinder extends UiBinder<Widget, DialogActivityLogs> {
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
	
	private ListDataProvider<ActivityLog> dataProvider;
	private ListHandler<ActivityLog> sortHandler;

	public DialogActivityLogs(List<ActivityLog> steps) {
		super(LabelsCommon.lblCnst.WorkflowLogs(), true, true);
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsCommon.lblCnst.Close(), closeHandler);
		
		DataGrid<ActivityLog> grid = createGridSteps(steps);
		panelGrid.setWidget(grid);
	}
	
	private DataGrid<ActivityLog> createGridSteps(List<ActivityLog> steps) {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);
		
		DataGrid.Resources resources = new CustomResources();
		DataGrid<ActivityLog> dataGrid = new DataGrid<ActivityLog>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.setEmptyTableWidget(new Label(LabelsCommon.lblCnst.NoLogs()));
		dataGrid.setRowStyles(new RowStyles<ActivityLog>() {
			
			@Override
			public String getStyleNames(ActivityLog row, int rowIndex) {
				if(row.getResult() != Result.SUCCESS) {
					return style.redBackground();
				}
				return null;
			}
		});

		CustomCell<ActivityLog> cell = new CustomCell<ActivityLog>(this);
		Column<ActivityLog, String> colActivityName = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				return object.getActivityName();
			}
		};
		colActivityName.setSortable(true);
		
		Column<ActivityLog, String> colState = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
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

		Column<ActivityLog, String> colStartDate = new Column<ActivityLog, String>(cell) {

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

		Column<ActivityLog, String> colEndDate = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				Date date = object.getEndDate();
				if (date == null || date.equals(new Date(0))) {
					return LabelsCommon.lblCnst.Unknown();
				}
				return dateFormatter.format(date);
			}
		};
		colEndDate.setSortable(true);
		
		Column<ActivityLog, String> colDuration = new Column<ActivityLog, String>(cell) {

			@Override
			public String getValue(ActivityLog object) {
				if(object.getDuration() == -1) {
					return LabelsCommon.lblCnst.Unknown();
				}
				else {
					return object.getDurationAsString();
				}
			}
		};
		colDuration.setSortable(true);

		final ButtonImageCell detailCell = new ButtonImageCell(bpm.gwt.workflow.commons.client.images.Images.INSTANCE.ic_view_list_black_18dp(), style.imgGrid());
		Column<ActivityLog, String> colDetail = new Column<ActivityLog, String>(detailCell) {

			@Override
			public String getValue(ActivityLog object) {
				return "";
			}
		};
		colDetail.setFieldUpdater(new FieldUpdater<ActivityLog, String>() {

			@Override
			public void update(int index, ActivityLog object, String value) {
				showLogs(object);
			}
		});
		
		dataProvider = new ListDataProvider<ActivityLog>(steps);
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<ActivityLog>(dataProvider.getList());
		sortHandler.setComparator(colActivityName, new Comparator<ActivityLog>() {
			
			@Override
			public int compare(ActivityLog o1, ActivityLog o2) {
				return o1.getActivityName().compareTo(o2.getActivityName());
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

		dataGrid.addColumn(colActivityName, LabelsCommon.lblCnst.Name());
		dataGrid.addColumn(colState, LabelsCommon.lblCnst.Status());
		dataGrid.addColumn(colStartDate, LabelsCommon.lblCnst.StartDate());
		dataGrid.addColumn(colEndDate, LabelsCommon.lblCnst.EndDate());
		dataGrid.addColumn(colDuration, LabelsCommon.lblCnst.Duration());
		dataGrid.addColumn(colDetail, LabelsCommon.lblCnst.Details());
		dataGrid.setColumnWidth(colDetail, "90px");
		
		dataGrid.addColumnSortHandler(sortHandler);

		SelectionModel<ActivityLog> selectionModel = new SingleSelectionModel<ActivityLog>();
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
	public void run(ActivityLog item) {
		showLogs(item);
	}
	
	private void showLogs(ActivityLog log) {
		String title = log.getActivityName();
		String logs = log.toString(Level.ALL);
		
		DialogLogDetails dial = new DialogLogDetails(title, logs);
		dial.center();
	}
}
