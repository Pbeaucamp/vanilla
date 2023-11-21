package bpm.metadata.web.client.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomListBox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataFilter;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPrompt;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.gwt.commons.shared.fmdt.metadata.Row;
import bpm.metadata.web.client.I18N.Labels;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class ResourceDialog extends AbstractDialogBox {

	public enum TypeResource {
		PROMPT, FILTER;
	}

	private static ResourceDialogUiBinder uiBinder = GWT.create(ResourceDialogUiBinder.class);

	interface ResourceDialogUiBinder extends UiBinder<Widget, ResourceDialog> {
	}

	@UiField
	LabelTextBox txtName;

	@UiField
	CustomListBox lstOperator;

	@UiField
	SimplePanel panelData;

	private Datasource datasource;

	private MetadataResource resource;

	private DatabaseColumn column;
	private TypeResource type;

	private MultiSelectionModel<Row> selectionModel;

	private boolean confirm = false;

	public ResourceDialog(Datasource datasource, TypeResource type, DatabaseColumn column) {
		super(Labels.lblCnst.AddResource(), false, true);
		this.datasource = datasource;
		this.type = type;
		this.column = column;

		buildContent();
	}

	public ResourceDialog(Datasource datasource, MetadataResource resource) {
		super(Labels.lblCnst.EditResource(), false, true);
		this.datasource = datasource;
		this.resource = resource;
		this.type = resource instanceof MetadataFilter ? TypeResource.FILTER : TypeResource.PROMPT;
		this.column = resource.getColumn();

		buildContent();
	}

	private void buildContent() {
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		if (resource != null) {
			txtName.setText(resource.getName());
		}

		if (type == TypeResource.FILTER) {
			lstOperator.setVisible(false);

			loadData();
		}
		else if (type == TypeResource.PROMPT) {
			panelData.setVisible(false);

			MetadataPrompt prompt = resource != null ? (MetadataPrompt) resource : null;

			int index = 0;
			int i = 0;
			for (String operator : MetadataPrompt.OPERATORS) {
				lstOperator.addItem(operator);

				if (prompt != null && prompt.getOperator().equals(operator)) {
					index = i;
				}
				i++;
			}

			lstOperator.setSelectedIndex(index);
		}
	}

	private void loadData() {
		showWaitPart(true);

		FmdtServices.Connect.getInstance().getTableData(datasource, column.getParent(), column, 10000, true, new GwtCallbackWrapper<MetadataData>(this, true) {

			@Override
			public void onSuccess(MetadataData result) {
				DataGrid<Row> datagrid = buildGridData(result);
				panelData.setWidget(datagrid);

				if (resource != null) {
					MetadataFilter filter = (MetadataFilter) resource;
					List<String> values = filter.getValues();

					if (values != null) {
						for (Row row : result.getRows()) {

							for (String value : values) {
								if (row.getValues().get(0).equals(value)) {
									selectionModel.setSelected(row, true);
								}
							}
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	private DataGrid<Row> buildGridData(MetadataData data) {

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Row> dataGrid = new DataGrid<Row>(10000, resources);
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
		this.selectionModel = new MultiSelectionModel<Row>();
		dataGrid.setSelectionModel(selectionModel);

		ListDataProvider<Row> dataProvider = new ListDataProvider<Row>();
		dataProvider.addDataDisplay(dataGrid);
		dataProvider.setList(data.getRows());
		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}

	public MetadataResource getResource() {
		String name = txtName.getText();

		if (type == TypeResource.FILTER) {
			if (resource == null) {
				resource = new MetadataFilter();
			}
			((MetadataFilter) resource).setValues(getValues());
		}
		else if (type == TypeResource.PROMPT) {
			if (resource == null) {
				resource = new MetadataPrompt();
			}
			((MetadataPrompt) resource).setOperator(getOperator());
		}
		resource.setName(name);
		resource.setColumn(column);

		return resource;
	}

	private String getOperator() {
		String operator = lstOperator.getSelectedValue();
		return operator;
	}

	private List<String> getValues() {
		List<String> values = new ArrayList<>();

		if (selectionModel != null) {
			Set<Row> rows = selectionModel.getSelectedSet();
			if (rows != null) {
				for (Row row : rows) {
					values.add(row.getValues().get(0));
				}
			}
		}

		return values;
	}

	public boolean isConfirm() {
		return confirm;
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			hide();
		}
	};
}
