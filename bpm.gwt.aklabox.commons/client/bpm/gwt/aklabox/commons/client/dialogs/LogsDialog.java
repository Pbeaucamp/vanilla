package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.document.management.core.model.ILog;
import bpm.document.management.core.model.Log;
import bpm.document.management.core.model.Log.LogStatus;
import bpm.document.management.core.model.Log.LogType;
import bpm.document.management.core.model.aklademat.ChorusLog;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.CustomResources;
import bpm.gwt.aklabox.commons.client.utils.MessageHelper;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class LogsDialog extends AbstractDialogBox {
	private static final String DATE_FORMAT = "HH:mm:ss - dd/MM/yyyy";

	private static SchedulerHistoricDialogUiBinder uiBinder = GWT.create(SchedulerHistoricDialogUiBinder.class);

	interface SchedulerHistoricDialogUiBinder extends UiBinder<Widget, LogsDialog> {
	}

	interface MyStyle extends CssResource {
		String pager();

		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;

	@UiField
	SimplePanel panelContent;

	private ListDataProvider<Log<? extends ILog>> dataProvider;

	public LogsDialog(LogType type) {
		super(LabelsConstants.lblCnst.logs(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		panelContent.setWidget(createGridData(type));

		loadLogs(type);
	}

	private void loadLogs(LogType type) {
		AklaCommonService.Connect.getService().getLogs(type, new GwtCallbackWrapper<List<Log<? extends ILog>>>(this, true, true) {

			@Override
			public void onSuccess(List<Log<? extends ILog>> result) {
				loadLogs(result);
			}
		}.getAsyncCallback());
	}

	private void loadLogs(List<Log<? extends ILog>> jobRuns) {
		dataProvider.setList(jobRuns);
	}

	private HTMLPanel createGridData(LogType type) {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		CustomCell cell = new CustomCell();
		Column<Log<? extends ILog>, String> dateStartColumn = new Column<Log<? extends ILog>, String>(cell) {

			@Override
			public String getValue(Log<? extends ILog> object) {
				Date date = object.getStartDate();
				if (date == null || date.equals(new Date(0))) {
					return LabelsConstants.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}
		};

		Column<Log<? extends ILog>, String> dateEndColumn = new Column<Log<? extends ILog>, String>(cell) {

			@Override
			public String getValue(Log<? extends ILog> object) {
				Date date = object.getEndDate();
				if (date == null || date.equals(new Date(0))) {
					return LabelsConstants.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}
		};

		Column<Log<? extends ILog>, String> colDuration = new Column<Log<? extends ILog>, String>(cell) {

			@Override
			public String getValue(Log<? extends ILog> object) {
				if (object.getDuration() == -1) {
					return LabelsConstants.lblCnst.Unknown();
				}
				else {
					return object.getDurationAsString();
				}
			}
		};

		Column<Log<? extends ILog>, String> statusColumn = new Column<Log<? extends ILog>, String>(cell) {

			@Override
			public String getValue(Log<? extends ILog> object) {
				switch (object.getLogStatus()) {
				case RUNNING:
					return LabelsConstants.lblCnst.RunningTask();
				case ERROR:
					return LabelsConstants.lblCnst.Error();
				case SUCCESS:
					return LabelsConstants.lblCnst.Success();
				case UNKNOWN:
					return LabelsConstants.lblCnst.Unknown();
				default:
					break;
				}
				return LabelsConstants.lblCnst.Unknown();
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Log<? extends ILog>> dataGrid = new DataGrid<Log<? extends ILog>>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(dateStartColumn, LabelsConstants.lblCnst.BeginDate());
		dataGrid.addColumn(dateEndColumn, LabelsConstants.lblCnst.EndDate());
		dataGrid.addColumn(colDuration, LabelsConstants.lblCnst.Duration());
		dataGrid.addColumn(statusColumn, LabelsConstants.lblCnst.Status());
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoResult()));
		
		if (type == LogType.CHECK_CPP) {
			Column<Log<? extends ILog>, String> colNbBills = new Column<Log<? extends ILog>, String>(cell) {

				@Override
				public String getValue(Log<? extends ILog> object) {
					ChorusLog log = (ChorusLog) object.getLog();
					return log.getFoundBills() + "";
				}
			};
			
			Column<Log<? extends ILog>, String> colNbNew = new Column<Log<? extends ILog>, String>(cell) {

				@Override
				public String getValue(Log<? extends ILog> object) {
					ChorusLog log = (ChorusLog) object.getLog();
					return log.getNbNew() + "";
				}
			};
			
			Column<Log<? extends ILog>, String> colNbModify = new Column<Log<? extends ILog>, String>(cell) {

				@Override
				public String getValue(Log<? extends ILog> object) {
					ChorusLog log = (ChorusLog) object.getLog();
					return log.getNbModify() + "";
				}
			};

			dataGrid.addColumn(colNbBills, LabelsConstants.lblCnst.billsFound());
			dataGrid.addColumn(colNbNew, LabelsConstants.lblCnst.newBills());
			dataGrid.addColumn(colNbModify, LabelsConstants.lblCnst.modifyBills());
		}

		dataProvider = new ListDataProvider<Log<? extends ILog>>();
		dataProvider.addDataDisplay(dataGrid);

		// Add a selection model so we can select cells.
		SelectionModel<Log<? extends ILog>> selectionModel = new SingleSelectionModel<Log<? extends ILog>>();
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		SimplePanel panelDataGrid = new SimplePanel();
		panelDataGrid.setSize("900px", "350px");
		panelDataGrid.setWidget(dataGrid);

		SimplePanel panelPager = new SimplePanel();
		panelPager.setWidget(pager);

		HTMLPanel panel = new HTMLPanel("");
		panel.add(panelDataGrid);
		panel.add(panelPager);

		return panel;
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			LogsDialog.this.hide();
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
			return consumedEvents;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			Log<? extends ILog> item = (Log<? extends ILog>) context.getKey();
			if (event.getType().equals("dblclick") && item.getLogStatus() == LogStatus.ERROR) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), item.getMessage());
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	@Override
	public int getThemeColor() {
		return 0;
	}
}
