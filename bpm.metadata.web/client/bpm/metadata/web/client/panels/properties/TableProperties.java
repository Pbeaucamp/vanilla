package bpm.metadata.web.client.panels.properties;

import java.util.ArrayList;
import java.util.Comparator;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.fmdt.metadata.Row;
import bpm.metadata.web.client.I18N.Labels;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class TableProperties extends Composite implements IPanelProperties {

	private static TablePropertiesUiBinder uiBinder = GWT.create(TablePropertiesUiBinder.class);

	interface TablePropertiesUiBinder extends UiBinder<Widget, TableProperties> {
	}
	
	interface MyStyle extends CssResource {
		String pager();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	LabelTextBox txtName, txtLimit;
	
	@UiField
	SimplePanel panelData, panelPager;
	
	private IWait waitPanel;
	
	private Metadata metadata;
	private DatabaseTable table;

	public TableProperties(IWait waitPanel, Metadata metadata, DatabaseTable table) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.metadata = metadata;
		this.table = table;
		
		txtName.setText(table.getName());
		txtName.setEnabled(false);
		
//		panelData.setWidget(w);
	}
	
	private void loadData(MetadataData data) {
		DataGrid<Row> gridData = buildGridData(data);
		panelData.setWidget(gridData);
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

	@Override
	public boolean isValid() {
		String name = txtName.getText();
		return name != null && !name.isEmpty();
	}

	@Override
	public void apply() {
		String name = txtName.getText();
		table.setName(name);
	}

	@UiHandler("btnLoadData")
	public void onLoadData(ClickEvent e) {
		waitPanel.showWaitPart(true);
		
		int limit = 100;
		try {
			limit = Integer.parseInt(txtLimit.getText());
		} catch(Exception ex) {
			txtLimit.setText("100");
		}
		
		FmdtServices.Connect.getInstance().getTableData(metadata.getDatasource(), table, null, limit, false, new GwtCallbackWrapper<MetadataData>(waitPanel, true) {

			@Override
			public void onSuccess(MetadataData result) {
				loadData(result);
			}
		}.getAsyncCallback());
	}
}
