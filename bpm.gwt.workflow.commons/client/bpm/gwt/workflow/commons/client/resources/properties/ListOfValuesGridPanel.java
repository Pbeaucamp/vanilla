package bpm.gwt.workflow.commons.client.resources.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class ListOfValuesGridPanel extends Composite {

	private static WebServiceParametersPanelUiBinder uiBinder = GWT.create(WebServiceParametersPanelUiBinder.class);

	interface WebServiceParametersPanelUiBinder extends UiBinder<Widget, ListOfValuesGridPanel> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	@UiField
	Label txtError;

	private ListDataProvider<Value> dataProvider;

	private List<Value> values;

	public ListOfValuesGridPanel(ListOfValues lov) {
		initWidget(uiBinder.createAndBindUi(this));

		DataGrid<Value> datagrid = createGrid();
		panelGrid.setWidget(datagrid);

		if (lov.getValues() != null) {
			this.values = buildValues(lov.getValues());
		}
		else {
			this.values = new ArrayList<Value>();
		}

		loadData(values);
	}

	private List<Value> buildValues(List<String> values) {
		List<Value> list = new ArrayList<Value>();
		for (String value : values) {
			list.add(new Value(value));
		}
		return list;
	}

	public List<String> getValues() {
		List<String> lov = new ArrayList<>();
		for (Value value : values) {
			lov.add(value.getValue());
		}
		return lov;
	}

	private void loadData(List<Value> result) {
		if (result != null) {
			dataProvider.setList(result);
		}
		else {
			dataProvider.setList(new ArrayList<Value>());
		}
	}

	private DataGrid<Value> createGrid() {
		Column<Value, String> colValue = new Column<Value, String>(new EditTextCell()) {

			@Override
			public String getValue(Value object) {
				return object.getValue();
			}
		};
		colValue.setFieldUpdater(new FieldUpdater<Value, String>() {

			@Override
			public void update(int index, Value object, String value) {
				object.setValue(value);
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<Value, String> colDelete = new Column<Value, String>(deleteCell) {

			@Override
			public String getValue(Value object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Value, String>() {

			@Override
			public void update(int index, final Value object, String value) {
				deleteValue(object);
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Value> dataGrid = new DataGrid<Value>(10000, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colValue, LabelsCommon.lblCnst.Values());
		dataGrid.addColumn(colDelete, LabelsCommon.lblCnst.Values());
		dataGrid.setColumnWidth(colDelete, "70px");

		dataProvider = new ListDataProvider<Value>();
		dataProvider.addDataDisplay(dataGrid);

		SingleSelectionModel<Value> selectionModel = new SingleSelectionModel<Value>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void addValue(ClickEvent event) {
		values.add(new Value());
		loadData(values);
	}

	private void deleteValue(Value value) {
		values.remove(value);
		loadData(values);
	}

	public void setTxtError(String error) {
		this.txtError.setText(error);
	}

	private class Value {
		private int id;
		private String value;

		public Value() {
			this.id = new Object().hashCode();
			this.value = "";
		}

		public Value(String value) {
			this.id = new Object().hashCode();
			this.value = value;
		}
		
		public int getId() {
			return id;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
