package bpm.smart.web.client.panels.properties.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.smart.core.model.workflow.activity.custom.ColumnFilter;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class ColumnFilterDialog extends AbstractDialogBox {

	private static ColumnFilterDialogUiBinder uiBinder = GWT.create(ColumnFilterDialogUiBinder.class);

	interface ColumnFilterDialogUiBinder extends UiBinder<Widget, ColumnFilterDialog> {
	}

	private DataGrid<ColumnFilter> datagrid;
	private ListDataProvider<ColumnFilter> dataprovider;
	private SingleSelectionModel<ColumnFilter> selectionModel;
	
	private DataColumn dataColumn;
	
	private boolean isConfirm;
	
	@UiField
	SimplePanel gridPanel;
	
	public ColumnFilterDialog(List<ColumnFilter> filters, DataColumn column) {
		super(LabelsConstants.lblCnst.Filter() + " - " + column.getColumnLabel(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		this.dataColumn = column;
		
		datagrid = new DataGrid<ColumnFilter>();
		datagrid.setSize("100%", "300px");
		
		SelectionCell selectionCell = new SelectionCell(ColumnFilter.operators);
		Column<ColumnFilter, String> colOp = new Column<ColumnFilter, String>(selectionCell) {
			@Override
			public String getValue(ColumnFilter object) {
				return object.getOperator();
			}
		};
		colOp.setFieldUpdater(new FieldUpdater<ColumnFilter, String>() {		
			@Override
			public void update(int index, ColumnFilter object, String value) {
				object.setOperator(value);
			}
		});
		EditTextCell cell = new EditTextCell();
		Column<ColumnFilter, String> colVal = new Column<ColumnFilter, String>(cell) {
			@Override
			public String getValue(ColumnFilter object) {
				return object.getValue();
			}
		};
		colVal.setFieldUpdater(new FieldUpdater<ColumnFilter, String>() {
			
			@Override
			public void update(int index, ColumnFilter object, String value) {
				object.setValue(value);
			}
		});
		
		datagrid.addColumn(colOp, LabelsConstants.lblCnst.FilterOperator());
		datagrid.addColumn(colVal, LabelsConstants.lblCnst.FilterValue());
		
		dataprovider = new ListDataProvider<ColumnFilter>();
		dataprovider.addDataDisplay(datagrid);
		dataprovider.setList(filters);
		
		selectionModel = new SingleSelectionModel<ColumnFilter>();
		datagrid.setSelectionModel(selectionModel);
		
		gridPanel.add(datagrid);
	}
	
	public List<ColumnFilter> getColumnFilters() {
		return new ArrayList<ColumnFilter>(dataprovider.getList());
	}

	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		ColumnFilter f = new ColumnFilter();
		f.setColumn(dataColumn);
		dataprovider.getList().add(f);
	}
	
	@UiHandler("btnRemove")
	public void onRemove(ClickEvent event) {
		dataprovider.getList().remove(selectionModel.getSelectedObject());
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			isConfirm = true;
			ColumnFilterDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ColumnFilterDialog.this.hide();
		}
	};
	
}
