package bpm.gwt.commons.client.dataset;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataType;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DatasetColumnTypesDialog extends AbstractDialogBox {

	private static DatasetColumnTypesDialogUiBinder uiBinder = GWT.create(DatasetColumnTypesDialogUiBinder.class);

	interface DatasetColumnTypesDialogUiBinder extends UiBinder<Widget, DatasetColumnTypesDialog> {}

	@UiField
	HTMLPanel gridPanel;
	
	private Dataset dataset;
	
	private ListDataProvider<DataColumn> dataProvider = new ListDataProvider<DataColumn>();

	public DatasetColumnTypesDialog(Dataset dataset) {
		super(LabelsConstants.lblCnst.ColumnTypes(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.dataset = dataset;
		
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		DataGrid<DataColumn> dataGrid = new DataGrid<DataColumn>();
		
		TextCell cell = new TextCell();
		Column<DataColumn, String> nameColumn = new Column<DataColumn, String>(cell) {
			@Override
			public String getValue(DataColumn object) {
				return object.getColumnName();
			}
		};
		
		Column<DataColumn, String> typeColumn = new Column<DataColumn, String>(cell) {
			@Override
			public String getValue(DataColumn object) {
				return object.getColumnDataType().name();
			}
		};
		
		List<String> types = new ArrayList<String>(DataType.asList());
		
		SelectionCell listCell = new SelectionCell(types);
		Column<DataColumn, String> customTypeColumn = new Column<DataColumn, String>(listCell) {
			@Override
			public String getValue(DataColumn object) {			
				return object.getCustomDataType() != null ? object.getCustomDataType().name() : object.getColumnDataType().name();
			}
		};
		customTypeColumn.setFieldUpdater(new FieldUpdater<DataColumn, String>() {		
			@Override
			public void update(int index, DataColumn object, String value) {
				DataType dt = DataType.valueOf(value);
				object.setCustomDataType(dt);
			}
		});
		
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
		dataGrid.addColumn(nameColumn, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(typeColumn, LabelsConstants.lblCnst.TypeSql());
		dataGrid.addColumn(customTypeColumn, LabelsConstants.lblCnst.CustomType());
		
		
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoData()));

		dataProvider = new ListDataProvider<DataColumn>();
		dataProvider.addDataDisplay(dataGrid);
		
		gridPanel.add(dataGrid);
		
		dataProvider.setList(dataset.getMetacolumns());
	}

	public ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	public ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			CommonService.Connect.getInstance().updateDataset(dataset, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Success(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.DatasetCreated(), false);
					dial.center();

					hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.DatasetError(), caught.getMessage(), caught);
					dial.center();
				}
			});
		}
	};
	
}
