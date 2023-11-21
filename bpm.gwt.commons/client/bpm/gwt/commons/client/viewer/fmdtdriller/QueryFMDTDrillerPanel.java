package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.api.beans.components.ChartTypes;
import bpm.fwr.api.beans.components.OptionsFusionChart;
import bpm.fwr.api.beans.components.VanillaChartComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ChartPanel;
import bpm.gwt.commons.client.charts.IChartConstructor;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.client.viewer.FmdtVanillaViewer;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.DisplayRowDialog.IRefreshValuesHandler;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.chart.ChartColumn;
import bpm.vanilla.platform.core.beans.chart.ChartType;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.beans.chart.Serie;
import bpm.vanilla.platform.core.beans.fmdt.FmdtAggregate;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFormula;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class QueryFMDTDrillerPanel extends Composite implements IChartConstructor<FmdtData>, IRefreshValuesHandler {
	public static final int WIDTH_REPORT = 813;
	public static final int HEIGHT_REPORT = 1150;

	private static QueryFMDTDrillerPanelUiBinder uiBinder = GWT.create(QueryFMDTDrillerPanelUiBinder.class);

	interface QueryFMDTDrillerPanelUiBinder extends UiBinder<Widget, QueryFMDTDrillerPanel> {
	}

	interface MyStyle extends CssResource {
		String labelRowDocument();

		String textDocument();

		String imgArrowDocument();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel contentPanel;

	private FmdtVanillaViewer viewer;
	private IWait waitPanel;
	private InfoUser infoUser;

	private FmdtQueryDriller driller;
	private FmdtQueryDatas querydatas;
	private FmdtRow columnNames;
	private FmdtTable selectedTable;
	private List<Group> availableGroups;

	private DesignerPanel designerPanel;
	private FMDTResponsePanel resultPanel = null;
	private ChartPanel<FmdtData> graphePanel;
	private CubeDesignerPanel cubePanel = null;
	private AnalysisDesignerPanel analysisPanel = null;

	private FmdtQueryBuilder chartBuilder;

	public QueryFMDTDrillerPanel(IWait waitPanel, FmdtQueryDriller driller, SavedChart selectedChart, List<Group> availableGroups, FmdtVanillaViewer viewer, InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.driller = driller;
		this.availableGroups = availableGroups;
		this.viewer = viewer;
		this.infoUser = infoUser;

		loadData(selectedChart);
	}

	public void loadData(final SavedChart selectedChart) {
		waitPanel.showWaitPart(true);

		FmdtServices.Connect.getInstance().getTables(driller, new AsyncCallback<FmdtQueryDatas>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				waitPanel.showWaitPart(false);
			}

			@Override
			public void onSuccess(FmdtQueryDatas result) {
				waitPanel.showWaitPart(false);
				querydatas = result;
				if (driller.getBuilders() == null || driller.getBuilders().isEmpty())
					driller.addBuilders(new FmdtQueryBuilder("Request_1"));

				designerPanel = new DesignerPanel(driller.getBuilders().get(0), querydatas, driller.getMetadataId(), driller.getModelName(), driller.getPackageName());
				driller.setCurrentIndex(0);

				contentPanel.setWidget(designerPanel);
				
				if (driller.hasChart()) {
					SavedChart chart = selectedChart != null ? selectedChart : driller.getCharts().get(0);
					QueryFMDTDrillerPanel.this.graphePanel = buildGraphPanel(chart);
					
					if (selectedChart != null) {
						showGraphePanel(false);
					}
				}
			}
		});
	}

	public void loadRequestValues() {
		waitPanel.showWaitPart(true);

		FmdtServices.Connect.getInstance().getRequestCount(driller.getBuilder(), driller, new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(Integer result) {
				waitPanel.showWaitPart(false);
				if(result > 10000) {
					StringBuffer buf = new StringBuffer();
					buf.append(LabelsConstants.lblCnst.EstimatedNumberOfData() + ": " + result + "<br/>");
					buf.append(LabelsConstants.lblCnst.TooManyDataMessage());
					SafeHtml html = SafeHtmlUtils.fromTrustedString(buf.toString());
					
					final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), html, true);
					dial.center();
					dial.addCloseHandler(new CloseHandler<PopupPanel>() {
						
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (dial.isConfirm()) {
								loadRequest();
							}
						}
					});
				}
				else {
					loadRequest();
				}
			}
		});
		

	}
	
	private void loadRequest() {
		FmdtServices.Connect.getInstance().getRequestValue(driller.getBuilder(), driller, true, new AsyncCallback<List<FmdtTable>>() {
			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(List<FmdtTable> table) {
				waitPanel.showWaitPart(false);
				buildTableData(table);
			}
		});
	}

	public void loadDesigner() {
		designerPanel.setChange(false);
		contentPanel.setWidget(designerPanel);
	}

	public void buildTableData(final List<FmdtTable> table) {
		// selectedTable = table;
		resultPanel = new FMDTResponsePanel(table, driller, waitPanel, this);

		contentPanel.setWidget(resultPanel);
	}

	public void showCubePanel() {
		cubePanel = new CubeDesignerPanel(driller.getBuilder(), driller, waitPanel, this);
		contentPanel.clear();
		contentPanel.setWidget(cubePanel);
	}

	public void showAnalysisPanel() {
		analysisPanel = new AnalysisDesignerPanel(driller.getBuilder(), driller, waitPanel, this);
		contentPanel.clear();
		contentPanel.setWidget(analysisPanel);
	}

	public void getAnalysisPanel() {
		if (analysisPanel == null)
			analysisPanel = new AnalysisDesignerPanel(driller.getBuilder(), driller, waitPanel, this);
		contentPanel.clear();
		contentPanel.setWidget(analysisPanel);
	}

	public void getResult() {
		contentPanel.setWidget(resultPanel);
	}

	public void getCubePanel() {
		contentPanel.clear();
		if (cubePanel == null)
			cubePanel = new CubeDesignerPanel(driller.getBuilder(), driller, waitPanel, this);
		contentPanel.setWidget(cubePanel);
	}

	public void showGraphePanel(boolean refresh) {
		if (graphePanel == null || refresh) {
			this.graphePanel = buildGraphPanel(null);
		}
		else {
			this.graphePanel.refresh();
		}
		contentPanel.setWidget(graphePanel);
	}

	private ChartPanel<FmdtData> buildGraphPanel(SavedChart selectedChart) {
		FmdtQueryBuilder builder = new FmdtQueryBuilder();
		builder.setDistinct(driller.getBuilder().isDistinct());
		builder.setFilters(driller.getBuilder().getFilters());
		builder.setLimit(driller.getBuilder().isLimit());
		builder.setName(driller.getBuilder().getName());
		builder.setNbLimit(driller.getBuilder().getNbLimit());
		builder.setPromptFilters(driller.getBuilder().getPromptFilters());

		this.chartBuilder = builder;

		List<ChartColumn<FmdtData>> columns = new ArrayList<ChartColumn<FmdtData>>();
		if (driller.getBuilder().getListColumns() != null) {
			for (FmdtData col : driller.getBuilder().getListColumns()) {
				columns.add(new ChartColumn<FmdtData>(col.getLabel(), col));
			}
		}
		
		if (selectedChart != null) {
			String title = selectedChart.getTitle();
			ChartType type = selectedChart.getChartType();
			ChartColumn<FmdtData> axeX = selectedChart.getAxeX();
			String axeXLabel = selectedChart.getAxeXLabel();
			List<Serie<FmdtData>> series = selectedChart.getSeries();
			
			for (ChartColumn<FmdtData> col : columns) {
				if (col.getItem().equals(axeX.getItem())) {
					axeX = col;
				}
				
				if (series != null) {
					for (Serie<FmdtData> serie : series) {
						if (col.getItem().equals(serie.getData())) {
							serie.setData(col);
						}
					}
				}
			}
			
			return new ChartPanel<FmdtData>(this, columns, title, type, axeX, axeXLabel, series);
		}
		else {
			return new ChartPanel<FmdtData>(this, columns);
		}
	}

	@Override
	public void refreshChart(final ChartType type, FmdtData axeX, final List<Serie<FmdtData>> series) {
		List<FmdtColumn> cols = new ArrayList<FmdtColumn>();
		List<FmdtFormula> formulas = new ArrayList<FmdtFormula>();
		List<FmdtAggregate> aggregates = new ArrayList<FmdtAggregate>();
		List<FmdtData> ordonable = new ArrayList<FmdtData>();

		ordonable.add(axeX);

		if (axeX instanceof FmdtColumn)
			cols.add((FmdtColumn) axeX);
		else if (axeX instanceof FmdtFormula)
			formulas.add((FmdtFormula) axeX);
		else if (axeX instanceof FmdtAggregate)
			aggregates.add((FmdtAggregate) axeX);

		for (Serie<FmdtData> serie : series) {
			if (serie.getData() instanceof FmdtColumn) {
				FmdtColumn col = (FmdtColumn) serie.getData();
				aggregates.add(new FmdtAggregate(col.getName(), col.getTableName(), col.getLabel(), col.getDescription(), serie.getAggregation()));
			}
			else if (serie.getData() instanceof FmdtFormula) {
				FmdtFormula col = (FmdtFormula) serie.getData();
				aggregates.add(new FmdtAggregate(col.getScript(), col.getLabel(), col.getDescription(), serie.getAggregation(), col.getDataStreamInvolved()));
			}
			else if (serie.getData() instanceof FmdtAggregate) {
				FmdtAggregate aggregate = (FmdtAggregate) serie.getData();
				if (!aggregate.isBasedOnFormula()) {
					aggregates.add(new FmdtAggregate(aggregate.getCol(), aggregate.getTable(), aggregate.getOutputName(), aggregate.getDescription(), serie.getAggregation()));
				}
				else {
					aggregates.add(new FmdtAggregate(aggregate.getFunction(), aggregate.getOutputName(), aggregate.getDescription(), serie.getAggregation(), aggregate.getFormulaData()));
				}
			}
		}
		chartBuilder.setColumns(cols);
		chartBuilder.setFormulas(formulas);
		chartBuilder.setAggregates(aggregates);
		chartBuilder.setListColumns(ordonable);

		FmdtServices.Connect.getInstance().getChartData(chartBuilder, driller, false, new GwtCallbackWrapper<List<ChartData>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<ChartData> chartData) {
				graphePanel.buildChart(type, series, chartData);
			}
			
			@Override
			public void onFailure(Throwable t) {
				graphePanel.buildChart(type, series, null);
			}
		}.getAsyncCallback());
	}

	public DesignerPanel getDesignerPanel() {
		return designerPanel;
	}

	public FmdtQueryDriller getDriller() {
		return driller;
	}

	public FmdtTable getSelectedTable() {
		return selectedTable;
	}

	public FmdtRow getColumnNames() {
		return columnNames != null ? columnNames : new FmdtRow();
	}

	public List<Group> getAvailableGroups() {
		return availableGroups;
	}

	public void launchFreeAnalysis() {
		cubePanel.launchFreeAnalysis();
	}

	public FmdtVanillaViewer getViewer() {
		return viewer;
	}

	public FMDTResponsePanel getResultPanel() {
		return resultPanel;
	}

	public ChartPanel<FmdtData> getGraphePanel() {
		return graphePanel;
	}

	public SavedChart getChart() {
		if (graphePanel != null && graphePanel.isComplete()) {
			String title = graphePanel.getTitle();
			ChartType chartType = graphePanel.getChartType();
			ChartColumn<FmdtData> axeX = graphePanel.getAxeX();
			String axeXLabel = graphePanel.getAxeXLabel();
			List<Serie<FmdtData>> series = graphePanel.getSeries();
			
			return new SavedChart(title, chartType, axeX, axeXLabel, series);
		}
		return null;
	}

	public FWRReport buildWebReport() throws ServiceException {
		return graphePanel.buildWebReport();
	}

	@Override
	public FWRReport buildWebReport(String title, final ChartType type, FmdtData axeX, final List<Serie<FmdtData>> series) throws ServiceException {
		VanillaChartComponent chartComponent = buildWebReportChart(title, type, axeX, series);

		FWRReport report = new FWRReport();
		report.setWidth(WIDTH_REPORT);
		report.setHeight(HEIGHT_REPORT);
		report.setMargins(getMargins(10));
		report.setOutput("HTML");
		report.setOrientation(Orientation.PORTRAIT);
		report.setPageSize("A4");
		
		report.addComponent(chartComponent);

		return report;
	}	
	
	private HashMap<String, String> getMargins(int margin){
		HashMap<String, String> margins = new HashMap<String, String>();
		
		margins.remove("top");
		margins.put("top", String.valueOf(margin));
		
		margins.remove("left");
		margins.put("left", String.valueOf(margin));

		margins.remove("right");
		margins.put("right", String.valueOf(margin));

		margins.remove("bottom");
		margins.put("bottom", String.valueOf(margin));
		
		return margins;
	}

	private VanillaChartComponent buildWebReportChart(String title, final ChartType type, FmdtData axeX, final List<Serie<FmdtData>> series) throws ServiceException {
		bpm.fwr.api.beans.components.ChartType chartType = getWebReportChart(type);

		if (chartType == null) {
			throw new ServiceException(LabelsConstants.lblCnst.TheSelectedChartIsNotSupported());
		}

		DataSet dataset = createDataSet();

		Column columnGroup = buildColumn(driller.getMetadataId(), driller.getModelName(), driller.getPackageName(), axeX, dataset);
		dataset.addColumn(columnGroup);
		List<Column> columnSeries = new ArrayList<Column>();
		for (Serie<FmdtData> serie : series) {
			String defaultOperation = serie.getAggregation().toLowerCase();

			if (defaultOperation.equals("distinct count")) {
				throw new ServiceException(LabelsConstants.lblCnst.WebReportDoesNotSupportDistinctCount());
			}

			Column columnSerie = buildColumn(driller.getMetadataId(), driller.getModelName(), driller.getPackageName(), serie.getData(), dataset);
			columnSerie.setAgregate(true);
			columnSerie.setCalculated(true);
			columnSerie.setFormula(defaultOperation + "(" + columnSerie.getOriginName() + ")");
			
			List<String> involvedDatastreams = new ArrayList<String>();
			involvedDatastreams.add(columnSerie.getDatasourceTable());
			columnSerie.setInvolvedDatastreams(involvedDatastreams);
			
			columnSeries.add(columnSerie);
			dataset.addColumn(columnSerie);
		}

		VanillaChartComponent chart = new VanillaChartComponent();
		// if (chartType.getType() == ChartTypes.PIE || chartType.getType() ==
		// ChartTypes.DOUGHNUT) {
		// List<Column> detail = new ArrayList<Column>();
		// detail.add(columnSeries.get(0));
		// chart.setColumnDetails(detail);
		// }
		// else {
		List<Column> detail = new ArrayList<Column>();
		for (Column column : columnSeries) {
			detail.add(column);
		}
		chart.setColumnDetails(detail);
		// }
		chart.setChartType(chartType);
		chart.setColumnGroup(columnGroup);
		chart.setDataset(dataset);

		OptionsFusionChart chartOptions = new OptionsFusionChart();
		chartOptions.setChartTitle(title);
//		chartOptions.setChartOperation(defaultOperation);
		chartOptions.setHeight(300);
		chartOptions.setWidth(600);
		chartOptions.set3D(false);
		chartOptions.setGlassEnabled(false);
		chartOptions.setStacked(chart.equals(ChartObject.RENDERER_MULTI_STACKEDCOLUMN) || chart.equals(ChartObject.RENDERER_MULTI_STACKEDAREA));

		chart.setOptions(chartOptions);

		return chart;
	}

	private bpm.fwr.api.beans.components.ChartType getWebReportChart(ChartType type) {
		String chart = type.getValue();
		if (chart.equals(ChartObject.RENDERER_PIE)) {
			return new bpm.fwr.api.beans.components.ChartType("Pie Chart", ChartTypes.PIE, false, true, false, false);
		}
		else if (chart.equals(ChartObject.RENDERER_DONUT)) {
			return new bpm.fwr.api.beans.components.ChartType("Doughtnut Chart", ChartTypes.DOUGHNUT, false, true, false, false);
		}
		else if (chart.equals(ChartObject.RENDERER_COLUMN) || chart.equals(ChartObject.RENDERER_MULTI_COLUMN) || chart.equals(ChartObject.RENDERER_MULTI_STACKEDCOLUMN)) {
			return new bpm.fwr.api.beans.components.ChartType("Column Chart", ChartTypes.COLUMN, false, true, true, true);
		}
		else if (chart.equals(ChartObject.RENDERER_BAR) || chart.equals(ChartObject.RENDERER_MULTI_BAR)) {
			return new bpm.fwr.api.beans.components.ChartType("Bar Chart", ChartTypes.BAR, false, true, true, true);
		}
		else if (chart.equals(ChartObject.RENDERER_LINE) || chart.equals(ChartObject.RENDERER_MULTI_LINE)) {
			return new bpm.fwr.api.beans.components.ChartType("Line Chart", ChartTypes.LINE, false, false, false, false);
		}
		else if (chart.equals(ChartObject.RENDERER_AREA) || chart.equals(ChartObject.RENDERER_MULTI_AREA) || chart.equals(ChartObject.RENDERER_MULTI_STACKEDAREA)) {
			return new bpm.fwr.api.beans.components.ChartType("Area Chart", ChartTypes.AREA, true, false, false, false);
		}
		else if (chart.equals(ChartObject.RENDERER_RADAR)) {
			return new bpm.fwr.api.beans.components.ChartType("Radar Chart", ChartTypes.RADAR, false, false, false, false);
		}

		// Not supported
		// RENDERER_PARETO, RENDERER_SPLINE, RENDERER_FUNNEL, RENDERER_PYRAMID,
		// RENDERER_MULTI_STEPLINE
		return null;
	}

	private DataSet createDataSet() {
		// Datasource creation
		DataSource dataS = new DataSource();
		dataS.setBusinessModel(driller.getModelName());
		dataS.setBusinessPackage(driller.getPackageName());
		dataS.setConnectionName("Default");
		dataS.setGroup(infoUser.getGroup().getName());
		dataS.setRepositoryId(infoUser.getRepository().getId());
		dataS.setItemId(driller.getMetadataId());
		dataS.setName("datasource_" + System.currentTimeMillis());
		dataS.setPassword(infoUser.getUser().getPassword());
		dataS.setUrl(infoUser.getRepository().getUrl());
		dataS.setUser(infoUser.getUser().getLogin());
		dataS.setOnOlap(false);

		DataSet dataset = new DataSet();
		dataset.setLanguage("fr");
		dataset.setDatasource(dataS);
		dataset.setName("dataset_" + System.currentTimeMillis());
		return dataset;
	}

	private Column buildColumn(int metadataId, String businessModel, String businessPack, FmdtData data, DataSet dataset) {
		String originName = data instanceof FmdtColumn ? ((FmdtColumn) data).getOriginName() : "";
		String tableName = data instanceof FmdtColumn ? ((FmdtColumn) data).getTableName() : "";
		String tableOriginName = data instanceof FmdtColumn ? ((FmdtColumn) data).getTableOriginName() : "";
		
		Column column = new Column(data.getName(), originName, tableName, data.getJavaType(), tableOriginName);
		column.setMetadataId(metadataId);
		column.setBusinessModelParent(businessModel);
		column.setBusinessPackageParent(businessPack);
		column.setDatasetParent(dataset);
		return column;
	}

	@Override
	public void refresh() {
		loadRequestValues();
	}

}
