package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.Comparator;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.dialogs.DatabaseSelectionDialog.IRefreshProvider;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.fmdt.metadata.Row;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class DatabaseLinkedDialog extends AbstractDialogBox implements IRefreshProvider {

	private static DimensionMeasureDialogUiBinder uiBinder = GWT.create(DimensionMeasureDialogUiBinder.class);

	interface DimensionMeasureDialogUiBinder extends UiBinder<Widget, DatabaseLinkedDialog> {
	}
	
	interface MyStyle extends CssResource {
		String imgCell();
		String imgGrid();
		String pager();
	}
	
	@UiField
	MyStyle style;
	
//	@UiField
//	LabelTextBox txtLimit;
	
	@UiField
	SimplePanel panelContent, panelPager;

	private IRefreshProvider refreshProvider;
	private User user;
	private Contract contract;
	
	public DatabaseLinkedDialog(IRefreshProvider refreshProvider, User user, Contract contract) {
		super(Labels.lblCnst.DatabaseLinked(), false, true);
		this.refreshProvider = refreshProvider;
		this.user = user;
		this.contract = contract;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		refresh(contract);
	}

	@Override
	public void refresh(Contract contract) {
		this.contract = contract;
		refreshProvider.refresh(contract);
		
		showWaitPart(true);
		
		//Not use for now
		int limit = 100;
//		try {
//			limit = Integer.parseInt(txtLimit.getText());
//		} catch(Exception ex) {
//			txtLimit.setText("100");
//		}
		
		CommonService.Connect.getInstance().getJdbcData(contract.getDatasourceId(), contract.getDatasetId(), limit, new GwtCallbackWrapper<MetadataData>(this, true) {

			@Override
			public void onSuccess(MetadataData result) {
				loadData(result);
			}
		}.getAsyncCallback());
	}

	private void loadData(MetadataData data) {
		DataGrid<Row> gridData = buildGridData(data);
		panelContent.setWidget(gridData);
	}
	
	private DataGrid<Row> buildGridData(MetadataData data) {

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Row> dataGrid = new DataGrid<Row>(100, resources);
		dataGrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoData()));
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		ListHandler<Row> sortHandler = new ListHandler<Row>(new ArrayList<Row>());
		dataGrid.addColumnSortHandler(sortHandler);
		
		TextCell cell = new TextCell();
		int i = 0;
		for (DatabaseColumn col : data.getColumns()) {
			final int index = i;
			Column<Row, String> gridColumn = new Column<Row, String>(cell) {

				@Override
				public String getValue(Row object) {
					return object.getValues() != null ? object.getValues().get(index) : "";
				}
			};
			gridColumn.setSortable(true);
			sortHandler.setComparator(gridColumn, new Comparator<Row>() {
				
				@Override
				public int compare(Row o1, Row o2) {
					if (o1.getValues() != null && o2.getValues() != null) {
						String value1 = o1.getValues().get(index);
						String value2 = o2.getValues().get(index);
						return value1.compareTo(value2);
					}
					return 0;
				}
			});
			
			dataGrid.addColumn(gridColumn, col.getName());
			dataGrid.setColumnWidth(gridColumn, "100px");
			
			i++;
		}
	
		// Add a selection model so we can select cells.
		SelectionModel<Row> selectionModel = new SingleSelectionModel<Row>();
		dataGrid.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);
		
		ListDataProvider<Row> dataProvider = new ListDataProvider<Row>();
		dataProvider.addDataDisplay(dataGrid);
		dataProvider.setList(data.getRows() != null ? data.getRows() : new ArrayList<Row>());
		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}
	
	@UiHandler("btnDefineDataset")
	public void onDefineDatasetClick(ClickEvent event) {
		DatabaseSelectionDialog dial = new DatabaseSelectionDialog(this, user, contract);
		dial.center();
//		final RepositoryDialog dial = new RepositoryDialog(-1);
//		dial.center();
//		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
//			
//			@Override
//			public void onClose(CloseEvent<PopupPanel> event) {
//				if (dial.isConfirm()) {
//					RepositoryItem item = dial.getSelectedItem();
//					saveDocumentItem(item);
//				}
//			}
//		});
	}

	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			DatabaseLinkedDialog.this.hide();
		}
	};
}
