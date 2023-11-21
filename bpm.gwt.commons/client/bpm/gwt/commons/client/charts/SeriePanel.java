package bpm.gwt.commons.client.charts;

import java.util.List;

import bpm.gwt.commons.client.custom.ColorPanel;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.chart.ChartColumn;
import bpm.vanilla.platform.core.beans.chart.IChartColumn;
import bpm.vanilla.platform.core.beans.chart.Serie;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SeriePanel<T extends IChartColumn> extends Composite {

	private static SeriePanelUiBinder uiBinder = GWT.create(SeriePanelUiBinder.class);

	interface SeriePanelUiBinder extends UiBinder<Widget, SeriePanel<?>> {
	}

	@UiField
	SimplePanel panelRefColor;

	@UiField
	ListBoxWithButton<ChartColumn<T>> lstColumns;

	@UiField
	ListBoxWithButton<Aggregation> lstAgg;

	@UiField
	ColorPanel panelColor;

	private IChartManager<T> chartManager;

	public SeriePanel(IChartManager<T> chartManager, List<ChartColumn<T>> columns, List<Aggregation> aggregations, String color) {
		initWidget(uiBinder.createAndBindUi(this));
		this.chartManager = chartManager;

		lstColumns.setList(columns);
		lstAgg.setList(aggregations);
		
		panelColor.setColor(color, false);
	}

	public SeriePanel(IChartManager<T> chartManager, List<ChartColumn<T>> columns, List<Aggregation> aggregations, Serie<T> serie) {
		this(chartManager, columns, aggregations, serie.getColor());
	
		lstColumns.setSelectedObject(serie.getChartColumn());
		lstAgg.setSelectedObject(getAggregation(aggregations, serie.getAggregation()));
	}

	private void updateColor(String color) {
		panelRefColor.getElement().getStyle().setBackgroundColor("#" + color);
	}

	@UiHandler("lstColumns")
	public void onColumnChange(ChangeEvent event) {
		chartManager.refresh();
	}

	@UiHandler("lstAgg")
	public void onAggregationChange(ChangeEvent event) {
		chartManager.refresh();
	}

	@UiHandler("panelColor")
	public void onChangeColor(ChangeEvent event) {
		String color = panelColor.getColor();
		updateColor(color);
		chartManager.refresh();
	}

	@UiHandler("btnRemove")
	public void onRemove(ClickEvent event) {
		chartManager.removeSerie(this);
	}
	
	private Aggregation getAggregation(List<Aggregation> aggregations, String aggregation) {
		for (Aggregation agg : aggregations) {
			if (agg.getValue().equals(aggregation)) {
				return agg;
			}
		}
		return null;
	}

	public boolean isComplete() {
		ChartColumn<T> column = lstColumns.getSelectedObject();
		Aggregation agg = lstAgg.getSelectedObject();
		return column != null && agg != null;
	}

	public Serie<T> getSerie() {
		ChartColumn<T> column = lstColumns.getSelectedObject();
		Aggregation agg = lstAgg.getSelectedObject();
		String color = panelColor.getColor();

		return new Serie<T>(agg + " " + column.getLabel(), column, agg.getValue(), color);
	}
}
