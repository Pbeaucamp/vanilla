package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.vanilla.platform.core.beans.FmdtDriller;

public class DisplayRowDialog extends AbstractDialogBox {

	private static DisplayRowDialogUiBinder uiBinder = GWT.create(DisplayRowDialogUiBinder.class);

	interface DisplayRowDialogUiBinder extends UiBinder<Widget, DisplayRowDialog> {
	}

	interface MyStyle extends CssResource {
		String columnName();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel dataRowField;

	private ListDataProvider<FmdtRow> dataProvider = new ListDataProvider<FmdtRow>();

	private SingleSelectionModel<FmdtRow> selectionModel = new SingleSelectionModel<FmdtRow>();

	private DataGrid<FmdtRow> dataGrid = new DataGrid<FmdtRow>();
	private List<FmdtRow> rows = new ArrayList<FmdtRow>();

	private IWait waitPanel;
	
	private FmdtRow currentValue;
	private FmdtTable currentTable;

	public DisplayRowDialog(final IWait waitPanel, FmdtRow columnName, FmdtRow value, FmdtTable selectedTable, final FmdtQueryDriller driller, final IRefreshValuesHandler refreshValuesHandler) {
		super(LabelsConstants.lblCnst.Export(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.currentValue = value;
		this.currentTable = selectedTable;
		
		this.waitPanel = waitPanel;
		createButtonBar(LabelsConstants.lblCnst.Export(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		createButton(LabelsConstants.lblCnst.Update(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int i = 0;
				for(FmdtRow r : dataProvider.getList()) {
					currentValue.getValues().set(i, r.getValues().get(1));
					i++;
				}
				
				FmdtDriller drill = new FmdtDriller();
				drill.setMetadataId(driller.getMetadataId());
				drill.setModelName(driller.getModelName());
				drill.setPackageName(driller.getPackageName());
				
				FmdtServices.Connect.getInstance().updateData(currentTable, currentValue, drill, new GwtCallbackWrapper<Void>(waitPanel, true) {
					@Override
					public void onSuccess(Void result) {
						DisplayRowDialog.this.hide();
						refreshValuesHandler.refresh();
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Update(), LabelsConstants.lblCnst.DataUpdateSuccess());
					}
				}.getAsyncCallback());
				
			}
		});

		initTable(columnName, value);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			FmdtTable table = new FmdtTable();
			table.setDatas(rows);

			ExportDialog dial = new ExportDialog(waitPanel, table);
			dial.center();
			hide();
		}

	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	

	private void initTable(FmdtRow columnName, FmdtRow value) {
		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<FmdtRow>(100, resources);
		dataGrid.setSize("100%", "100%");

		Cell<String> txtCell = new TextCell();
		Column<FmdtRow, String> names = new Column<FmdtRow, String>(txtCell) {

			@Override
			public String getValue(FmdtRow object) {
				return object.getValues().get(0);
			}
		};
		names.setDefaultSortAscending(false);
		names.setCellStyleNames(style.columnName());
		dataGrid.addColumn(names);

		EditTextCell editCell = new EditTextCell();
		
		Column<FmdtRow, String> values = new Column<FmdtRow, String>(editCell) {

			@Override
			public String getValue(FmdtRow object) {
				return object.getValues().get(1);
			}
		};
		
		values.setFieldUpdater(new FieldUpdater<FmdtRow, String>() {		
			@Override
			public void update(int index, FmdtRow object, String value) {
				object.getValues().set(1, value);
			}
		});
		
		values.setDefaultSortAscending(false);
		dataGrid.addColumn(values);

		for (int i = 0; i < columnName.getValues().size(); i++) {
			FmdtRow row = new FmdtRow();
			row.getValues().add(columnName.getValues().get(i));
			row.getValues().add(value.getValues().get(i));
			rows.add(row);
		}

		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoData()));

		dataProvider = new ListDataProvider<FmdtRow>();
		dataProvider.addDataDisplay(dataGrid);
		dataGrid.setSelectionModel(selectionModel);
		dataRowField.clear();
		dataRowField.setWidget(dataGrid);
		dataProvider.setList(rows);
	}

	public interface IRefreshValuesHandler {
		
		public void refresh();
	}
}
