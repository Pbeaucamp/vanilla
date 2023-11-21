package bpm.fd.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.core.IComponentData;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.ChartDrill;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.dialogs.ChartAgregationDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CompositeCellHelper;
import bpm.gwt.commons.client.custom.HasActionCell;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class ChartDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static ChartDataPropertiesUiBinder uiBinder = GWT.create(ChartDataPropertiesUiBinder.class);

	interface ChartDataPropertiesUiBinder extends UiBinder<Widget, ChartDataProperties> {
	}

	@UiField
	ListBoxWithButton<DataColumn> groupField, groupFieldLabel, subGroupField;

	@UiField
	ListBoxWithButton<ChartOrderingType> orderType;

	@UiField
	Image btnAdd;

	@UiField
	SimplePanel panelDatagrid;

	@UiField
	HTMLPanel panelChartData, panelAggregs;

	@UiField
	CheckBox drill;

	private DataGrid<DataAggregation> datagrid;
	private ListDataProvider<DataAggregation> dataprovider;

	private Dataset selectedDataset;

	private ChartComponent component;

	public ChartDataProperties(ChartComponent component) {
		initWidget(uiBinder.createAndBindUi(this));

		this.component = component;

		datagrid = new DataGrid<DataAggregation>();

		this.datagrid = buildGrid();
		panelDatagrid.add(datagrid);

		if (component.getChartDrill() != null) {
			drill.setValue(true);
		}
	}

	private DataGrid<DataAggregation> buildGrid() {
		TextCell cell = new TextCell();

		Column<DataAggregation, String> colName = new Column<DataAggregation, String>(cell) {
			@Override
			public String getValue(DataAggregation object) {
				StringBuffer buf = new StringBuffer();
				buf.append(object.getMeasureName());
				buf.append(" (" + object.getAggregatorName() + ")");
				buf.append(" (" + object.getRendering().toString() + ")");
				return buf.toString();
			}
		};

		HasActionCell<DataAggregation> editCell = new HasActionCell<DataAggregation>(CommonImages.INSTANCE.edit(), Labels.lblCnst.EditTheAgregation(), new Delegate<DataAggregation>() {

			@Override
			public void execute(final DataAggregation object) {
				edit(object);
			}
		});

		HasActionCell<DataAggregation> deleteCell = new HasActionCell<DataAggregation>(CommonImages.INSTANCE.ic_clear_16px(), Labels.lblCnst.DeleteTheAgregation(), new Delegate<DataAggregation>() {

			@Override
			public void execute(final DataAggregation object) {
				delete(object);
			}
		});

		CompositeCellHelper<DataAggregation> compCell = new CompositeCellHelper<DataAggregation>(editCell, deleteCell);
		Column<DataAggregation, DataAggregation> colAction = new Column<DataAggregation, DataAggregation>(compCell.getCell()) {
			@Override
			public DataAggregation getValue(DataAggregation object) {
				return object;
			}
		};

		dataprovider = new ListDataProvider<DataAggregation>();

		DataGrid<DataAggregation> datagrid = new DataGrid<DataAggregation>();
		datagrid.setSize("100%", "100%");
		datagrid.addColumn(colName, LabelsConstants.lblCnst.Name());
		datagrid.addColumn(colAction);
		datagrid.setColumnWidth(colAction, "100px");
		datagrid.setEmptyTableWidget(new Label(Labels.lblCnst.NoMeasures()));

		dataprovider.addDataDisplay(datagrid);
		orderType.setList(ChartOrderingType.getOrderTypes());

		if (component.getAggregations() != null) {
			dataprovider.setList(new ArrayList<DataAggregation>(component.getAggregations()));
		}
		orderType.setSelectedIndex(ChartOrderingType.getOrderTypes().indexOf(component.getOrderType()));

		return datagrid;
	}

	@Override
	public void buildProperties(IComponentData component) {
		ChartComponent comp = (ChartComponent) component;

		comp.setGroupFieldIndex(groupField.getSelectedIndex());
		comp.setGroupFieldLabelIndex(groupFieldLabel.getSelectedIndex());
		if (subGroupField.getSelectedIndex() > -1) {
			comp.setSubGroupFieldIndex(subGroupField.getSelectedIndex());
		}

		comp.setOrderType((ChartOrderingType) orderType.getSelectedObject());

		List<DataAggregation> aggs = getAggregations();
		if (aggs != null) {
			for (int i = 0; i < aggs.size(); i++) {
				aggs.get(i).setColorCode(DataAggregation.COLORS[i % DataAggregation.COLORS.length], 0);
			}
		}
		comp.setAggregations(aggs);

		if (drill.getValue()) {
			comp.setChartDrill(new ChartDrill());
		}
		else {
			comp.setChartDrill(null);
		}
	}

	private List<DataAggregation> getAggregations() {
		return new ArrayList<DataAggregation>(dataprovider.getList());
	}
	
	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		selectedDataset = dataset;

		groupField.setList(dataset.getMetacolumns());
		groupFieldLabel.setList(dataset.getMetacolumns());
		subGroupField.setList(dataset.getMetacolumns(), true);

		if (component.getDataset() != null && component.getDataset().getId() == dataset.getId()) {
			groupField.setSelectedIndex(component.getGroupFieldIndex());
			groupFieldLabel.setSelectedIndex(component.getGroupFieldLabelIndex());
			if (component.getSubGroupFieldIndex() != null) {
				subGroupField.setSelectedIndex(component.getSubGroupFieldIndex() + 1);
			}
		}
		orderType.setList(ChartOrderingType.getOrderTypes());
	}

	@Override
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}

	@UiHandler("btnAdd")
	public void addClick(ClickEvent event) {
		List<DataColumn> metacolumns = getSelectedDataset() != null ? getSelectedDataset().getMetacolumns() : new ArrayList<DataColumn>();

		ChartAgregationDialog dial = new ChartAgregationDialog(this, null, metacolumns);
		dial.center();
	}

	public void add(DataAggregation aggreg) {
		List<DataAggregation> aggregs = new ArrayList<DataAggregation>(dataprovider.getList());
		aggregs.add(aggreg);
		dataprovider.setList(aggregs);
	}

	private void edit(DataAggregation agg) {
		ChartAgregationDialog dial = new ChartAgregationDialog(this, agg, getSelectedDataset().getMetacolumns());
		dial.center();
	}

	private void delete(DataAggregation agg) {
		List<DataAggregation> aggregs = new ArrayList<DataAggregation>(dataprovider.getList());
		aggregs.remove(agg);
		dataprovider.setList(aggregs);
	}

	public void refresh() {
		dataprovider.refresh();
	}

	public Dataset getSelectedDataset() {
		return selectedDataset;
	}
}
