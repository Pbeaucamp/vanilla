package bpm.fd.web.client.panels.properties.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.kpi.KpiAggreg;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.fd.web.client.ClientSession;
import bpm.fd.web.client.MainPanel;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.panels.properties.ChartTypeProperties;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Theme;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dataset.DatasetHelper;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceKpi;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class KpiChartDataPanel extends Composite {

	private static KpiChartDataPanelUiBinder uiBinder = GWT.create(KpiChartDataPanelUiBinder.class);

	interface KpiChartDataPanelUiBinder extends UiBinder<Widget, KpiChartDataPanel> {
	}
	
	@UiField
	DateBox startDate, endDate;
	
	@UiField
	SimplePanel metricPanel, levelPanel, chartType;
	
	@UiField
	ListBoxWithButton<Theme> lstThemes;
	
	@UiField
	RadioButton startDateRadio, startParamRadio, endDateRadio, endParamRadio;
	
	@UiField
	CheckBox ckEndDate;
	
	private MultiSelectionModel<Metric> metricSelection = new MultiSelectionModel<Metric>();
	private SingleSelectionModel<Level> levelSelection = new SingleSelectionModel<Level>();
	private HashMap<Metric, String> metricOperator = new HashMap<Metric, String>();
	private HashMap<Metric, String> metricColumns = new HashMap<Metric, String>();

	private ListDataProvider<Metric> metricDataprovider;
	private ListDataProvider<Level> levelDataprovider;

	private ChartTypeProperties typeChart;

	private KpiChartComponent component;

	public KpiChartDataPanel(KpiChartComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		//FIXME Hide radio/check while not implemented
		startDateRadio.removeFromParent();
		startParamRadio.removeFromParent();
		endDateRadio.removeFromParent();
		endParamRadio.removeFromParent();
		ckEndDate.removeFromParent();
		
		this.component = component;
		
		typeChart = new ChartTypeProperties(((KpiChart)component.getElement()).getOption(), null);
		
		chartType.add(typeChart);
		startDateRadio.setValue(true);
		endDateRadio.setValue(true);
		
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		startDate.getDatePicker().setYearArrowsVisible(true);
		startDate.setFormat(new DateBox.DefaultFormat(dateFormat));
		startDate.setValue(new Date());

		endDate.getDatePicker().setYearArrowsVisible(true);
		endDate.setFormat(new DateBox.DefaultFormat(dateFormat));
		endDate.setValue(new Date());
		
		lstThemes.setList(ClientSession.getInstance().getThemes());
		
		createMetricDatagrid();
		createLevelDatagrid();
		
		metricDataprovider.setList(ClientSession.getInstance().getThemes().get(0).getMetrics());
		
		List<Level> allLevels = getAllLevels(ClientSession.getInstance().getThemes().get(0));
		
		levelDataprovider.setList(allLevels);
		
		if(component.getDataset() != null) {
			loadDataset(component);
		}
	}

	private void loadDataset(KpiChartComponent component) {
		KpiChart chart = (KpiChart) component.getElement();
		
		int themeId = ((DatasourceKpi)component.getDataset().getDatasource().getObject()).getThemeId();
		Theme selected = null;
		for(Theme th : ClientSession.getInstance().getThemes()) {
			if(th.getId() == themeId) {
				lstThemes.setSelectedObject(th);
				selected = th;
				break;
			}
		}
		
		DatasetHelper helper = new DatasetHelper();
		helper.parseKpiDatasetXml(component.getDataset().getRequest(), selected.getMetrics(), selected.getAxis());
		
		levelSelection.setSelected(chart.getLevel(), true);
		for(KpiAggreg agg : chart.getAggregs()) {
			metricSelection.setSelected(agg.getMetric(), true);
			metricOperator.put(agg.getMetric(), agg.getOperator());
			metricColumns.put(agg.getMetric(), agg.getColumn());
		}
		
		startDate.getDatePicker().setValue(helper.getStartDate(), true);
		endDate.getDatePicker().setValue(helper.getEndDate(), true);
	}
	
	@UiHandler("lstThemes")
	public void onThemeChange(ChangeEvent e) {
		metricDataprovider.setList(lstThemes.getSelectedObject().getMetrics());
		levelDataprovider.setList(getAllLevels(lstThemes.getSelectedObject()));
	}

	private List<Level> getAllLevels(Theme theme) {
		List<Level> levels = new ArrayList<Level>();
		for(Axis a : theme.getAxis()) {
			levels.addAll(a.getChildren());
		}
		return levels;
	}

	private void createLevelDatagrid() {
		DataGrid<Level> datagrid = new DataGrid<Level>();
		datagrid.setWidth("100%");
		datagrid.setHeight("100%");
		
		CheckboxCell cell = new CheckboxCell();
		Column<Level, Boolean> selectionColumn = new Column<Level, Boolean>(cell) {
			@Override
			public Boolean getValue(Level object) {
				return levelSelection.isSelected(object);
			}
		};
		
		TextCell txtCell = new TextCell();
		Column<Level, String> axisColumn = new Column<Level, String>(txtCell) {
			@Override
			public String getValue(Level object) {
				return object.getParent().getName();
			}
		};
		
		Column<Level, String> levelColumn = new Column<Level, String>(txtCell) {
			@Override
			public String getValue(Level object) {
				return object.getName();
			}
		};
		
		datagrid.addColumn(selectionColumn, Labels.lblCnst.GroupField());
		datagrid.setColumnWidth(selectionColumn, "10%");
		datagrid.addColumn(axisColumn, LabelsConstants.lblCnst.Axis());
		datagrid.setColumnWidth(selectionColumn, "45%");
		datagrid.addColumn(levelColumn, Labels.lblCnst.Level());
		datagrid.setColumnWidth(selectionColumn, "45%");
		
		levelDataprovider = new ListDataProvider<Level>();
		levelDataprovider.addDataDisplay(datagrid);
		
		datagrid.setSelectionModel(levelSelection);
		levelPanel.add(datagrid);
	}

	private void createMetricDatagrid() {
		DataGrid<Metric> datagrid = new DataGrid<Metric>();
		datagrid.setWidth("100%");
		datagrid.setHeight("100%");
		
		CheckboxCell cell = new CheckboxCell();
		Column<Metric, Boolean> selectionColumn = new Column<Metric, Boolean>(cell) {
			@Override
			public Boolean getValue(Metric object) {
				return metricSelection.isSelected(object);
			}
		};
		
		TextCell txtCell = new TextCell();
		Column<Metric, String> nameColumn = new Column<Metric, String>(txtCell) {
			@Override
			public String getValue(Metric object) {
				return object.getName();
			}
		};
		
		List<String> operators = Arrays.asList(DataAggregation.AGGREGATORS_NAME);
		SelectionCell selectionCell = new SelectionCell(operators);
		Column<Metric, String> aggColumn = new Column<Metric, String>(selectionCell) {		
			@Override
			public String getValue(Metric object) {
				if(metricOperator.get(object) != null) {
					return metricOperator.get(object);
				}
				return "NONE";
			}
		};
		aggColumn.setFieldUpdater(new FieldUpdater<Metric, String>() {		
			@Override
			public void update(int index, Metric object, String value) {
				metricOperator.put(object, value);
			}
		});
		
		List<String> metricCols = new ArrayList<String>();
		metricCols.add(KpiAggreg.COLUMN_VALUE);
		metricCols.add(KpiAggreg.COLUMN_OBJ);
		metricCols.add(KpiAggreg.COLUMN_MIN);
		metricCols.add(KpiAggreg.COLUMN_MAX);
		SelectionCell columnCell = new SelectionCell(metricCols);
		Column<Metric, String> columnColumn = new Column<Metric, String>(columnCell) {		
			@Override
			public String getValue(Metric object) {
				if(metricColumns.get(object) != null) {
					return metricColumns.get(object);
				}
				return KpiAggreg.COLUMN_VALUE;
			}
		};
		columnColumn.setFieldUpdater(new FieldUpdater<Metric, String>() {		
			@Override
			public void update(int index, Metric object, String value) {
				metricColumns.put(object, value);
			}
		});
		
		datagrid.addColumn(selectionColumn, Labels.lblCnst.Aggregations());
		datagrid.setColumnWidth(selectionColumn, "10%");
		datagrid.addColumn(nameColumn, Labels.lblCnst.Name());
		datagrid.setColumnWidth(selectionColumn, "40%");
		datagrid.addColumn(aggColumn, Labels.lblCnst.Aggregator());
		datagrid.setColumnWidth(selectionColumn, "25%");
		datagrid.addColumn(columnColumn, Labels.lblCnst.Column());
		datagrid.setColumnWidth(selectionColumn, "25%");
		
		metricDataprovider = new ListDataProvider<Metric>();
		metricDataprovider.addDataDisplay(datagrid);
		
		datagrid.setSelectionModel(metricSelection, DefaultSelectionEventManager.<Metric> createCheckboxManager());
		metricPanel.add(datagrid);
	}

	public Dataset getDataset() {
		//create the datasource/dataset
		Datasource datasource = new Datasource();
		datasource.setType(DatasourceType.KPI);
		datasource.setIdAuthor(MainPanel.getInstance().getInfoUser().getUser().getId());
		
		DatasourceKpi kpi = new DatasourceKpi();
		kpi.setGroupId(MainPanel.getInstance().getInfoUser().getGroup().getId());
		kpi.setUser(MainPanel.getInstance().getInfoUser().getUser().getLogin());
		kpi.setPassword(MainPanel.getInstance().getInfoUser().getUser().getPassword());
		kpi.setThemeId(lstThemes.getSelectedObject().getId());
		datasource.setName("kpi_" + new Object().hashCode());
		
		datasource.setObject(kpi);
		
		Dataset kpiDataset = new Dataset();
		kpiDataset.setDatasource(datasource);
		kpiDataset.setIdAuthor(MainPanel.getInstance().getInfoUser().getUser().getId());
		kpiDataset.setRequest(new DatasetHelper().generateKpiDatasetXml(new ArrayList<Metric>(metricSelection.getSelectedSet()), levelSelection.getSelectedObject().getParent(), startDate.getValue(), endDate.getValue(), endParamRadio.getValue() || startParamRadio.getValue()));
		kpiDataset.setName("kpi_dataset_" + new Object().hashCode());
		
		return kpiDataset;
	}

	public List<KpiAggreg> getAggregations() {	
		List<KpiAggreg> aggs = new ArrayList<KpiAggreg>();
		
		for(Metric m : metricSelection.getSelectedSet()) {
			KpiAggreg agg = new KpiAggreg();
			agg.setMetric(m);
			agg.setOperator(metricOperator.get(m) != null ? metricOperator.get(m) : "NONE");
			agg.setColumn(metricColumns.get(m) != null ? metricColumns.get(m) : KpiAggreg.COLUMN_VALUE);
			aggs.add(agg);
		}
		
		return aggs;
	}

	public Level getLevel() {
		return levelSelection.getSelectedObject();
	}
	
	public void refreshTypeChart() {
		for(KpiAggreg agg : getAggregations()) {
			((KpiChart)component.getElement()).getOption().addAggregation(new DataAggregation());
		}
		typeChart.updateUi();
	}

}
