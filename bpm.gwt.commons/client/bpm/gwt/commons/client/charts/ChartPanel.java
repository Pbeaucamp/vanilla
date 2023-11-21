package bpm.gwt.commons.client.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.vanilla.platform.core.beans.chart.ChartColumn;
import bpm.vanilla.platform.core.beans.chart.ChartType;
import bpm.vanilla.platform.core.beans.chart.IChartColumn;
import bpm.vanilla.platform.core.beans.chart.Serie;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChartPanel<T extends IChartColumn> extends CompositeWaitPanel implements IChartManager<T> {

	private List<String> colors = Arrays.asList("0075c2", "1aaf5d", "f2c500", "f45b00", "8e0000", "0e948c", "8cbb2c", "f3de00", "c02d00", "5b0101");

	private static GraphPanelUiBinder uiBinder = GWT.create(GraphPanelUiBinder.class);

	interface GraphPanelUiBinder extends UiBinder<Widget, ChartPanel<?>> {
	}

	@UiField
	LabelTextBox txtTitle, txtAxeX;

	@UiField
	ListBoxWithButton<ChartColumn<T>> lstAxeX;

	@UiField
	ListBoxWithButton<ChartType> lstChartTypes;

	@UiField
	HTMLPanel panelSeries;

	@UiField
	ChartRenderPanel panelChart;

	private IChartConstructor<T> constructor;

	private List<SeriePanel<T>> series;

	private List<ChartColumn<T>> columns;
	private List<Aggregation> aggregations;
	private List<ChartType> simpleTypes;
	private List<ChartType> multiTypes;

	private boolean isMultiSeries;

	public ChartPanel(IChartConstructor<T> constructor, List<ChartColumn<T>> columns) {
		this(constructor, columns, null, null, null, null, null);
	}

	public ChartPanel(IChartConstructor<T> constructor, List<ChartColumn<T>> columns, String title, ChartType type, ChartColumn<T> axeX, String axeXLabel, List<Serie<T>> series) {
		initWidget(uiBinder.createAndBindUi(this));
		this.constructor = constructor;
		this.columns = columns;
		this.aggregations = buildAggregations();
		buildChartTypes();

		txtTitle.setText(title != null ? title : "");

		lstAxeX.setList(columns);
		if (axeX != null) {
			lstAxeX.setSelectedObject(axeX);
		}
		txtAxeX.setText(axeXLabel != null ? axeXLabel : "");

		if (series != null && !series.isEmpty()) {
			for (Serie<T> serie : series) {
				addSerie(serie);
			}

			updateTypes(series.size() > 1);
			lstChartTypes.setSelectedObject(type);
		}
		else {
			addSerie();
		}
	}

	private void buildChartTypes() {
		this.simpleTypes = new ArrayList<ChartType>();
		String simpleType = ChartObject.TYPE_SIMPLE;
		for (String type : ChartObject.SIMPLE_RENDERERS) {
			ChartType chartType = null;

			if (type.equals(ChartObject.RENDERER_PIE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.pieChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_DONUT)) {
				chartType = new ChartType(LabelsConstants.lblCnst.donutChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_BAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.barChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_COLUMN)) {
				chartType = new ChartType(LabelsConstants.lblCnst.columnChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_LINE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.lineChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_AREA)) {
				chartType = new ChartType(LabelsConstants.lblCnst.areaChart(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_SPLINE)) {
//				chartType = new ChartType(LabelsConstants.lblCnst.spline(), type, simpleType);
				continue;
			}
			else if (type.equals(ChartObject.RENDERER_PARETO)) {
				continue;
//				chartType = new ChartType(LabelsConstants.lblCnst.pareto(), type, simpleType);
			}
			else if (type.equals(ChartObject.RENDERER_RADAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.radar(), type, simpleType);
			}

			if (chartType != null) {
				simpleTypes.add(chartType);
			}
		}

		this.multiTypes = new ArrayList<ChartType>();
		String multiType = ChartObject.TYPE_MULTI;
		for (String type : ChartObject.MULTI_RENDERERS) {
			ChartType chartType = null;

			if (type.equals(ChartObject.RENDERER_MULTI_BAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.barChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_COLUMN)) {
				chartType = new ChartType(LabelsConstants.lblCnst.columnChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_LINE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.lineChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_AREA)) {
				chartType = new ChartType(LabelsConstants.lblCnst.areaChart(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_STACKEDCOLUMN)) {
				chartType = new ChartType(LabelsConstants.lblCnst.stackedColumns(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_STACKEDAREA)) {
				chartType = new ChartType(LabelsConstants.lblCnst.stackedArea(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_MULTI_STEPLINE)) {
				chartType = new ChartType(LabelsConstants.lblCnst.steppedLine(), type, multiType);
			}
			else if (type.equals(ChartObject.RENDERER_RADAR)) {
				chartType = new ChartType(LabelsConstants.lblCnst.radar(), type, multiType);
			}

			if (chartType != null) {
				multiTypes.add(chartType);
			}
		}

		lstChartTypes.setList(simpleTypes);
	}

	@UiHandler("lstAxeX")
	public void onAxeXChange(ChangeEvent event) {
		refresh();
	}

	private void updateTypes(boolean isMultiSeries) {
		if (this.isMultiSeries == isMultiSeries) {
			return;
		}

		this.isMultiSeries = isMultiSeries;
		if (isMultiSeries) {
			lstChartTypes.setList(multiTypes);
		}
		else {
			lstChartTypes.setList(simpleTypes);
		}
	}

	@UiHandler("lstChartTypes")
	public void onChartTypeChange(ChangeEvent event) {
		refresh();
	}

	@UiHandler("btnAddSerie")
	public void onAddSerie(ClickEvent event) {
		addSerie();
	}

	@Override
	public void addSerie() {
		if (series == null) {
			series = new ArrayList<SeriePanel<T>>();
		}

		String color = colors.get(0);
		if (colors.size() > series.size() + 1) {
			color = colors.get(series.size() + 1);
		}
		SeriePanel<T> seriePanel = new SeriePanel<T>(this, columns, aggregations, color);
		series.add(seriePanel);
		panelSeries.add(seriePanel);

		refresh();
	}

	private void addSerie(Serie<T> serie) {
		if (series == null) {
			series = new ArrayList<SeriePanel<T>>();
		}

		SeriePanel<T> seriePanel = new SeriePanel<T>(this, columns, aggregations, serie);
		series.add(seriePanel);
		panelSeries.add(seriePanel);
	}

	@Override
	public void removeSerie(SeriePanel<T> serie) {
		if (series.size() <= 1) {
			return;
		}

		series.remove(serie);
		serie.removeFromParent();

		refresh();
	}

	private List<Aggregation> buildAggregations() {
		String[] aggregations = new String[] { "COUNT", "SUM", "AVG", "DISTINCT COUNT", "MAX", "MIN" };

		List<Aggregation> aggregs = new ArrayList<Aggregation>();
		for (String agg : aggregations) {
			aggregs.add(new Aggregation(getAggLabel(agg), agg));
		}
		return aggregs;
	}

	private String getAggLabel(String agg) {
		if (agg.equals("SUM")) {
			return LabelsConstants.lblCnst.Sum();
		}
		else if (agg.equals("AVG")) {
			return LabelsConstants.lblCnst.Average();
		}
		else if (agg.equals("COUNT")) {
			return LabelsConstants.lblCnst.Count();
		}
		else if (agg.equals("DISTINCT COUNT")) {
			return LabelsConstants.lblCnst.DistinctCount();
		}
		else if (agg.equals("MAX")) {
			return LabelsConstants.lblCnst.Max();
		}
		else if (agg.equals("MIN")) {
			return LabelsConstants.lblCnst.Min();
		}
		return "";
	}

	public boolean isComplete() {
		ChartColumn<T> column = lstAxeX.getSelectedObject();

		if (series != null && !series.isEmpty()) {
			for (SeriePanel<T> serie : series) {
				if (!serie.isComplete()) {
					return false;
				}
			}

			return column != null;
		}

		return false;
	}

	// RENDER

	@Override
	public void refresh() {
		if (isComplete()) {
			ChartColumn<T> axeX = getAxeX();
			List<Serie<T>> series = getSeries();
			updateTypes(series.size() > 1);

			ChartType type = getChartType();
			constructor.refreshChart(type, axeX.getItem(), series);
		}
	}

	@Override
	public String getTitle() {
		return txtTitle.getText();
	}

	public ChartType getChartType() {
		return lstChartTypes.getSelectedObject();
	}

	public ChartColumn<T> getAxeX() {
		return lstAxeX.getSelectedObject();
	}

	public String getAxeXLabel() {
		return txtAxeX.getText();
	}

	public List<Serie<T>> getSeries() {
		List<Serie<T>> series = new ArrayList<Serie<T>>();
		for (SeriePanel<T> serie : this.series) {
			series.add(serie.getSerie());
		}
		return series;
	}

	@Override
	public void buildChart(ChartType type, List<Serie<T>> series, List<ChartData> chartData) {
		// List<FmdtRow> rows = table.getDatas();
		// rows.remove(rows.get(0));

		String axeXName = txtAxeX.getText();

		ChartObject chart = new ChartObject();
		chart.setTitle(txtTitle.getText());

		List<ChartValue> multiseries = new ArrayList<ChartValue>();
		if (series.size() > 1) {
			int i = 0;
			for (Serie<T> serie : series) {
				chart.addColor(serie.getColor());

				ValueMultiSerie m = new ValueMultiSerie();
				m.setCategory(serie.getName());
				m.setSerieName(serie.getDataLabel());
				for (ChartData data : chartData) {
					try {
						ValueSimpleSerie s = new ValueSimpleSerie();
						s.setCategory(data.getGroupValue());
						Double value = Double.parseDouble(data.getSerieValues(i));
						s.setValue(value);
						m.addValue(s);
					} catch (Exception e) {
					}
				}
				multiseries.add(m);

				i++;
			}
		}
		else {
			Serie<T> serie = series.get(0);
			chart.addColor(serie.getColor());

			if (chartData != null) {
				for (ChartData data : chartData) {
					ValueSimpleSerie s = new ValueSimpleSerie();
					s.setCategory(data.getGroupValue());
					try {
						Double value = Double.parseDouble(data.getSerieValues(0));
						s.setValue(value);
					} catch (Exception e) {
					}
					multiseries.add(s);
				}
			}
		}
		chart.setValues(multiseries);

		// If PIE or DONUT, we set a palette of colors
		if (type.getValue().equals(ChartObject.RENDERER_PIE) || type.getValue().equals(ChartObject.RENDERER_DONUT)) {
			chart.setColors(colors);
		}

		// For now we set the YAxis to ""
		if (!type.getValue().equals(ChartObject.RENDERER_BAR)) {
			chart.setxAxisName(axeXName);
			chart.setyAxisName("");
		}
		else {
			chart.setxAxisName("");
			chart.setyAxisName(axeXName);
		}

		panelChart.loadChart(chart, type);
	}

	@Override
	public FWRReport buildWebReport() throws ServiceException {
		String title = txtTitle.getText();

		if (title == null || title.isEmpty()) {
			throw new ServiceException(LabelsConstants.lblCnst.TheTitleIsMandatory());
		}

		ChartColumn<T> axeX = lstAxeX.getSelectedObject();
		List<Serie<T>> series = new ArrayList<Serie<T>>();
		for (SeriePanel<T> serie : this.series) {
			if (serie.getSerie().getChartColumn().equals(axeX)) {
				throw new ServiceException(LabelsConstants.lblCnst.TheGroupColumnCannotBeUseAsSerieForAWebReport());
			}
			series.add(serie.getSerie());
		}

		ChartType type = lstChartTypes.getSelectedObject();
		return constructor.buildWebReport(title, type, axeX.getItem(), series);
	}

	public void loadChart(ChartObject chart, ChartType type) {
		panelChart.loadChart(chart, type);
	}
}
