package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.DatagridHandler;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
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

public class ItemHistoricDialog extends AbstractDialogBox implements DatagridHandler<ItemInstance> {

	private static final String DATE_FORMAT = "HH:mm:ss - dd/MM/yyyy";
	
	private static DialogStepsUiBinder uiBinder = GWT.create(DialogStepsUiBinder.class);

	interface DialogStepsUiBinder extends UiBinder<Widget, ItemHistoricDialog> {
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
	
	private RepositoryItem item;
	
	private ListDataProvider<ItemInstance> dataProvider;
	private ListHandler<ItemInstance> sortHandler;

	public ItemHistoricDialog(RepositoryItem item) {
		super(Labels.lblCnst.Historic() + " : " + item.getName(), true, true);
		this.item = item;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
		
		DataGrid<ItemInstance> grid = createGridSteps();
		panelGrid.setWidget(grid);
		
		loadHistoric();
	}
	
	private void loadHistoric() {
		showWaitPart(true);
		
		ArchitectService.Connect.getInstance().getItemHistoric(item.getId(), null, null, new GwtCallbackWrapper<List<ItemInstance>>(this, true) {

			@Override
			public void onSuccess(List<ItemInstance> result) {
				loadHistoric(result);
			}
		}.getAsyncCallback());
	}

	private void loadHistoric(List<ItemInstance> result) {
		if (result == null) {
			result = new ArrayList<>();
		}
		
		dataProvider.setList(result);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<ItemInstance> createGridSteps() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);
		
		DataGrid.Resources resources = new CustomResources();
		DataGrid<ItemInstance> dataGrid = new DataGrid<ItemInstance>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoHistoric()));
		dataGrid.setRowStyles(new RowStyles<ItemInstance>() {
			
			@Override
			public String getStyleNames(ItemInstance row, int rowIndex) {
				if(row.getResult() != ActivityResult.SUCCEED) {
					return style.redBackground();
				}
				return null;
			}
		});

		CustomCell<ItemInstance> cell = new CustomCell<ItemInstance>(this);
		Column<ItemInstance, String> colState = new Column<ItemInstance, String>(cell) {

			@Override
			public String getValue(ItemInstance object) {
				switch(object.getResult()) {
				case SUCCEED:
					return Labels.lblCnst.Success();
				case FAILED:
					return Labels.lblCnst.Error();
				case UNDEFINED:
					return Labels.lblCnst.Unknown();
				}
				return Labels.lblCnst.Unknown();
			}
		};
		colState.setSortable(true);

		Column<ItemInstance, String> colStartDate = new Column<ItemInstance, String>(cell) {

			@Override
			public String getValue(ItemInstance object) {
				Date date = object.getRunDate();
				if (date == null || date.equals(new Date(0))) {
					return Labels.lblCnst.Unknown();
				}
				return dateFormatter.format(date);
			}
		};
		colStartDate.setSortable(true);

		Column<ItemInstance, String> colDuration = new Column<ItemInstance, String>(cell) {

			@Override
			public String getValue(ItemInstance object) {
				if(object.getDuration() == -1) {
					return Labels.lblCnst.Unknown();
				}
				else {
					return object.getDurationAsString();
				}
			}
		};
		colDuration.setSortable(true);

		final ButtonImageCell detailCell = new ButtonImageCell(Images.INSTANCE.image_grid(), Labels.lblCnst.Details(), style.imgGrid());
		Column<ItemInstance, String> colDetail = new Column<ItemInstance, String>(detailCell) {

			@Override
			public String getValue(ItemInstance object) {
				return "";
			}
		};
		colDetail.setFieldUpdater(new FieldUpdater<ItemInstance, String>() {

			@Override
			public void update(int index, final ItemInstance object, String value) {
				showActivityLogs(object);
			}
		});

//		final ButtonImageCell downloadCell = new ButtonImageCell(Images.INSTANCE.ic_file_download_black_18dp(), style.imgGrid());
//		Column<ItemInstance, String> colDownload = new Column<ItemInstance, String>(downloadCell) {
//
//			@Override
//			public String getValue(ItemInstance object) {
//				return "";
//			}
//		};
//		colDownload.setFieldUpdater(new FieldUpdater<ItemInstance, String>() {
//
//			@Override
//			public void update(int index, final ItemInstance object, String value) {
//				ScheduledCommand cmd = new ScheduledCommand() {
//					
//					@Override
//					public void execute() {
//						downloadLog(object, downloadCell.getClientX(), downloadCell.getClientY());
//					}
//				};
//				Scheduler.get().scheduleDeferred(cmd);
//			}
//		});

		
		dataProvider = new ListDataProvider<ItemInstance>();
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<ItemInstance>(dataProvider.getList());
		sortHandler.setComparator(colState, new Comparator<ItemInstance>() {
			
			@Override
			public int compare(ItemInstance o1, ItemInstance o2) {
				if(o1.getResult() == null) {
					return -1;
				}
				else if(o2.getResult() == null) {
					return 1;
				}
				return o1.getResult().compareTo(o2.getResult());
			}
		});
		sortHandler.setComparator(colStartDate, new Comparator<ItemInstance>() {
			
			@Override
			public int compare(ItemInstance o1, ItemInstance o2) {
				if(o1.getRunDate() == null) {
					return -1;
				}
				else if(o2.getRunDate() == null) {
					return 1;
				}
				
				return o2.getRunDate().before(o1.getRunDate()) ? -1 : o2.getRunDate().after(o1.getRunDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(colDuration, new Comparator<ItemInstance>() {
			
			@Override
			public int compare(ItemInstance o1, ItemInstance o2) {
				return ((Long) (o1.getDuration())).compareTo((Long) o2.getDuration());
			}
		});

		dataGrid.addColumn(colState, Labels.lblCnst.Status());
		dataGrid.addColumn(colStartDate, Labels.lblCnst.RunDate());
		dataGrid.addColumn(colDuration, Labels.lblCnst.Duration());
//		dataGrid.addColumn(colDetail, Labels.lblCnst.Details());
//		dataGrid.setColumnWidth(colDetail, "90px");
//		dataGrid.addColumn(colDownload, Labels.lblCnst.Download());
//		dataGrid.setColumnWidth(colDownload, "120px");
		
		dataGrid.addColumnSortHandler(sortHandler);

		SelectionModel<ItemInstance> selectionModel = new SingleSelectionModel<ItemInstance>();
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
	public void onRightClick(ItemInstance item, NativeEvent event) { }

	@Override
	public void onDoubleClick(final ItemInstance item) {
		showActivityLogs(item);
	}
	
	private void showActivityLogs(final ItemInstance item) {
//		if(item.getActivityLogs() != null && !item.getActivityLogs().isEmpty()) {
//			DialogActivityLogs dial = new DialogActivityLogs(item.getActivityLogs());
//			dial.center();
//		}
//		else {
//			RepositoryItemsService.Connect.getInstance().getRepositoryItemRun(item, item, new GwtCallbackWrapper<List<ActivityLog>>(this, true) {
//	
//				@Override
//				public void onSuccess(List<ActivityLog> result) {
//					item.setActivityLogs(result);
//					
//					DialogActivityLogs dial = new DialogActivityLogs(result);
//					dial.center();
//				}
//			}.getAsyncCallback());
//		}
	}
	
//	private void downloadLog(ItemInstance instance, int left, int top) {
//		LevelPopup popup = new LevelPopup(this, instance);
//		popup.setPopupPosition(left, top);
//		popup.show();
//	}
}
