package bpm.fd.web.client.panels.properties.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.components.definition.datagrid.Aggregation;
import bpm.fd.core.component.DatagridColumn;
import bpm.fd.core.component.DatagridComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.custom.v2.HeaderCheckboxCell;
import bpm.gwt.commons.client.custom.v2.IHeaderCheckboxManager;
import bpm.gwt.commons.client.custom.v2.ParameterizedCheckboxCell;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

public class DatagridColumnsPanel extends Composite implements IHeaderCheckboxManager<DatagridColumn> {

	private static DatagridColumnsPanelUiBinder uiBinder = GWT.create(DatagridColumnsPanelUiBinder.class);

	interface DatagridColumnsPanelUiBinder extends UiBinder<Widget, DatagridColumnsPanel> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	GridPanel<DatagridColumn> grid;

	private HeaderCheckboxCell<DatagridColumn> header;
	private SingleSelectionModel<DatagridColumn> selectionModel;
	
	private List<DatagridColumn> columns;

	public DatagridColumnsPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		buildGrid();
	}
	
	@UiHandler("btnDown")
	public void onDown(ClickEvent event) {
		DatagridColumn column = selectionModel.getSelectedObject();
		if (column != null) {
			updateOrder(column, true);
		}
	}
	
	@UiHandler("btnUp")
	public void onUp(ClickEvent event) {
		DatagridColumn column = selectionModel.getSelectedObject();
		if (column != null) {
			updateOrder(column, false);
		}
	}
	
	private void updateOrder(DatagridColumn column, boolean isDown) {
		if (columns != null) {
			int i = 0;
			int index = 0;
			for (DatagridColumn col : columns) {
				if (col.equals(column)) {
					index = i;
					break;
				}
				i++;
			}
			
			int newIndex = isDown ? index + 1 : index - 1;
			
			if (newIndex >= 0 && newIndex <= columns.size() - 1) {
				DatagridColumn oldColumn = columns.get(newIndex);
				if (oldColumn.isGroup() && column.isGroup() || !oldColumn.isGroup() && !column.isGroup()) {
					columns.remove(column);
					
					if (newIndex >= columns.size()) {
						columns.add(column);
					}
					else {
						columns.add(newIndex, column);
					}
				}
				
				grid.refresh();
			}
		}
	}
	
	private void updateGroup(DatagridColumn column) {
		columns.remove(column);
		for (int i=0; i<columns.size(); i++) {
			DatagridColumn col = columns.get(i);
			if (!col.isGroup()) {
				columns.add(i, column);
				break;
			}
		}
		
		grid.refresh();
	}
	
	public void loadColumns(DatagridComponent component, List<DataColumn> columns, boolean refresh) {
		List<DatagridColumn> datagridColumns = buildColumns(component, columns, refresh);
		
		this.columns = datagridColumns;
		grid.loadItems(datagridColumns);
		header.loadItems(grid.getItems());
	}
	
	public List<DatagridColumn> getColumns() {
		return columns;
	}

	private List<DatagridColumn> buildColumns(DatagridComponent component, List<DataColumn> columns, boolean refresh) {
		if (!refresh && component.getColumns() != null && !component.getColumns().isEmpty()) {
			return component.getColumns();
		}
		else if (columns != null) {
			List<DatagridColumn> datagridColumns = new ArrayList<>();
			for (DataColumn column : columns) {
				datagridColumns.add(new DatagridColumn(column, null, true));
			}
			return datagridColumns;
		}
		
		return new ArrayList<>();
	}
	
	private void buildGrid() {
		grid.setTopManually(0);
		
		Column<DatagridColumn, Boolean> columnIsVisible = new Column<DatagridColumn, Boolean>(new ParameterizedCheckboxCell()) {

			@Override
			public Boolean getValue(DatagridColumn object) {
				return object.isVisible();
			}
		};
		columnIsVisible.setFieldUpdater(new FieldUpdater<DatagridColumn, Boolean>() {
			@Override
			public void update(int index, DatagridColumn object, Boolean value) {
				object.setVisible(value);
			}
		});

		TextCell txtCell = new TextCell();
		Column<DatagridColumn, String> columnName = new Column<DatagridColumn, String>(txtCell) {

			@Override
			public String getValue(DatagridColumn object) {
				return object.getName();
			}
		};

		EditTextCell txtModifyCell = new EditTextCell();
		Column<DatagridColumn, String> columnCustomName = new Column<DatagridColumn, String>(txtModifyCell) {

			@Override
			public String getValue(DatagridColumn object) {
				return object.getCustomName() != null ? object.getCustomName() : "";
			}
		};
		columnCustomName.setFieldUpdater(new FieldUpdater<DatagridColumn, String>() {
			@Override
			public void update(int index, DatagridColumn object, String value) {
				// Called when the user changes the value.
				object.setCustomName(value);
			}
		});
		
		Column<DatagridColumn, Boolean> columnIsGroup = new Column<DatagridColumn, Boolean>(new ParameterizedCheckboxCell()) {

			@Override
			public Boolean getValue(DatagridColumn object) {
				return object.isGroup();
			}
		};
		columnIsGroup.setFieldUpdater(new FieldUpdater<DatagridColumn, Boolean>() {
			@Override
			public void update(int index, DatagridColumn object, Boolean value) {
				object.setGroup(value);
				updateGroup(object);
			}
		});

		List<String> aggregations = new ArrayList<String>();
		aggregations.add("");
		for (Aggregation agg : Aggregation.values()) {
			aggregations.add(agg.toString());
		}

		SelectionCell selectionCell = new SelectionCell(aggregations);
		Column<DatagridColumn, String> colAggregation = new Column<DatagridColumn, String>(selectionCell) {
			@Override
			public String getValue(DatagridColumn object) {
				return object.getAggregation() != null ? object.getAggregation().toString() : "";
			}
		};
		colAggregation.setFieldUpdater(new FieldUpdater<DatagridColumn, String>() {
			@Override
			public void update(int index, DatagridColumn object, String value) {
				Aggregation agg = value != null && !value.isEmpty() ? Aggregation.valueOf(value) : null;
				object.setAggregation(agg);
			}
		});
		

		this.header = new HeaderCheckboxCell<DatagridColumn>(this, null);
		Header<Boolean> headerCheck = new Header<Boolean>(header) {

			@Override
			public Boolean getValue() {
				return true;
			}
		};

		grid.addColumn(headerCheck, columnIsVisible, "40px", null);
		grid.addColumn(Labels.lblCnst.Column(), columnName, null, null);
		grid.addColumn(Labels.lblCnst.CustomName(), columnCustomName, "200px", null);
		grid.addColumn(Labels.lblCnst.IsGroup(), columnIsGroup, "70px", null);
		grid.addColumn(Labels.lblCnst.Aggregation(), colAggregation, "130px", null);
		
		selectionModel = new SingleSelectionModel<DatagridColumn>();
		grid.setSelectionModel(selectionModel);
	}

	@Override
	public void update(DatagridColumn object, Boolean value) {
		object.setVisible(value);
	}
	
	@Override
	public void refreshGrid() {
		grid.refresh();
	}
}
