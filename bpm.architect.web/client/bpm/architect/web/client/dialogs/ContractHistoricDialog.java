package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.shared.HistoricLog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.DatagridHandler;
import bpm.mdm.model.supplier.Contract;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ContractHistoricDialog extends AbstractDialogBox implements DatagridHandler<HistoricLog> {

	private static final String DATE_FORMAT = "HH:mm:ss - dd/MM/yyyy";

	private static DialogStepsUiBinder uiBinder = GWT.create(DialogStepsUiBinder.class);

	interface DialogStepsUiBinder extends UiBinder<Widget, ContractHistoricDialog> {
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

	private Contract contract;

	private ListDataProvider<HistoricLog> dataProvider;
	private ListHandler<HistoricLog> sortHandler;

	public ContractHistoricDialog(Contract contract) {
		super(Labels.lblCnst.Historic() + " : " + contract.getName(), true, true);
		this.contract = contract;

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		DataGrid<HistoricLog> grid = createGridSteps();
		panelGrid.setWidget(grid);

		loadHistoric();
	}

	private void loadHistoric() {
		showWaitPart(true);

		ArchitectService.Connect.getInstance().buildHistoricLogs(contract, new GwtCallbackWrapper<List<HistoricLog>>(this, true) {

			@Override
			public void onSuccess(List<HistoricLog> result) {
				loadHistoric(result);
			}
		}.getAsyncCallback());
	}

	private void loadHistoric(List<HistoricLog> result) {
		if (result == null) {
			result = new ArrayList<>();
		}

		dataProvider.setList(result);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<HistoricLog> createGridSteps() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<HistoricLog> dataGrid = new DataGrid<HistoricLog>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoHistoric()));

		CustomCell<HistoricLog> cell = new CustomCell<HistoricLog>(this);
		Column<HistoricLog, String> colType = new Column<HistoricLog, String>(cell) {

			@Override
			public String getValue(HistoricLog object) {
				switch (object.getType()) {
				case ADD_VERSION:
					return Labels.lblCnst.AddVersion();
				case CHANGE_CURRENT_VERSION:
					return Labels.lblCnst.ChangeCurrentVersion();
				case CHANGE_DATABASE:
					return Labels.lblCnst.ChangeDatabaseLink();
				case CREATION:
					return Labels.lblCnst.Creation();
				case DOWNLOAD_VIEW:
					return Labels.lblCnst.DownloadOrView();
				}
				return Labels.lblCnst.Unknown();
			}
		};
		colType.setSortable(true);

		Column<HistoricLog, String> colDate = new Column<HistoricLog, String>(cell) {

			@Override
			public String getValue(HistoricLog object) {
				Date date = object.getDate();
				if (date == null || date.equals(new Date(0))) {
					return Labels.lblCnst.Unknown();
				}
				return dateFormatter.format(date);
			}
		};
		colDate.setSortable(true);

		Column<HistoricLog, String> colUser = new Column<HistoricLog, String>(cell) {

			@Override
			public String getValue(HistoricLog object) {
				if (object.getUser() == null) {
					return Labels.lblCnst.Unknown();
				}
				return object.getUser().getLogin();
			}
		};
		colUser.setSortable(true);

		Column<HistoricLog, String> colMessage = new Column<HistoricLog, String>(cell) {

			@Override
			public String getValue(HistoricLog object) {
				return object.getMessage();
			}
		};
		colMessage.setSortable(true);

		dataProvider = new ListDataProvider<HistoricLog>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<HistoricLog>(dataProvider.getList());
		sortHandler.setComparator(colType, new Comparator<HistoricLog>() {

			@Override
			public int compare(HistoricLog o1, HistoricLog o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
		sortHandler.setComparator(colDate, new Comparator<HistoricLog>() {

			@Override
			public int compare(HistoricLog o1, HistoricLog o2) {
				if (o1.getDate() == null) {
					return -1;
				}
				else if (o2.getDate() == null) {
					return 1;
				}

				return o2.getDate().before(o1.getDate()) ? -1 : o2.getDate().after(o1.getDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(colUser, new Comparator<HistoricLog>() {

			@Override
			public int compare(HistoricLog o1, HistoricLog o2) {
				if (o1.getUser() == null) {
					return -1;
				}
				else if (o2.getUser() == null) {
					return 1;
				}

				return o1.getUser().getLogin().compareTo(o2.getUser().getLogin());
			}
		});
		sortHandler.setComparator(colMessage, new Comparator<HistoricLog>() {

			@Override
			public int compare(HistoricLog o1, HistoricLog o2) {
				return o1.getMessage().compareTo(o2.getMessage());
			}
		});

		dataGrid.addColumn(colType, Labels.lblCnst.Type());
		dataGrid.addColumn(colDate, Labels.lblCnst.Date());
		dataGrid.addColumn(colUser, Labels.lblCnst.User());
		dataGrid.addColumn(colMessage, Labels.lblCnst.Message());

		dataGrid.addColumnSortHandler(sortHandler);

		SelectionModel<HistoricLog> selectionModel = new SingleSelectionModel<HistoricLog>();
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
	public void onRightClick(HistoricLog item, NativeEvent event) {
	}

	@Override
	public void onDoubleClick(final HistoricLog item) { }
}
