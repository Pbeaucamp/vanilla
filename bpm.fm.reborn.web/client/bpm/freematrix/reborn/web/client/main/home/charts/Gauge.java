package bpm.freematrix.reborn.web.client.main.home.charts;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MetricValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Gauge.Options;

public class Gauge extends Composite {

	private static GaugeUiBinder uiBinder = GWT.create(GaugeUiBinder.class);

	interface GaugeUiBinder extends UiBinder<Widget, Gauge> {
	}

	@UiField
	HTMLPanel gaugePanel;
	private double min;
	private double max;
	private double value;
	private String title;
	private double objectives;
	private MetricValue metricValue;

	public Gauge(MetricValue metricValue, String title) {
		initWidget(uiBinder.createAndBindUi(this));
		this.metricValue = metricValue;
		this.min = metricValue.getMinimum();
		this.max = metricValue.getMaximum();
		this.value = metricValue.getValue();
		this.title = title;
		this.objectives = metricValue.getObjective();
		generateGauge();
	}

	public void generateGauge() {
		
		
		Runnable onLoadCallback = new Runnable() {
		      public void run() {
		       
		    	 com.google.gwt.visualization.client.visualizations.Gauge gauge = new com.google.gwt.visualization.client.visualizations.Gauge(createTable(), createOptions());
		  		gaugePanel.add(gauge);
		      }
		    };

		  
		    VisualizationUtils.loadVisualizationApi(onLoadCallback, com.google.gwt.visualization.client.visualizations.Gauge.PACKAGE);
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setWidth(400);
		options.setHeight(240);
		
		double objMin = objectives - (objectives/10);
		double objMax = objectives + (objectives/10);
		if(metricValue.getMetric().getDirection().equals(Metric.DIRECTIONS.get(Metric.DIRECTION_BOTTOM))) {
			options.setGreenRange((int)min, (int)objectives);
			options.setYellowRange((int)objectives,(int)(objMax + max) / 2);			
			options.setRedRange((int)(objMax + max) / 2, (int)max);
		}
		else {
			options.setGreenRange((int)objectives, (int)max);
			options.setYellowRange((int)(objMin + min) / 2,(int)objectives);			
			options.setRedRange((int)(min), (int)objectives);
		}

		options.setGaugeRange((int)min, (int)max);
		
//		options.setGreenRange(0, ((int)max)/2);
//		options.setYellowRange(((int)max)/2,(int)(max/ (1.2)));
//		options.setRedRange((int)(max/ (1.2)), (int)max);
//		options.setGaugeRange((int)min, (int)max);
		return options;
	}

	private DataTable createTable() {
		DataTable data = DataTable.create();

		data.addColumn(ColumnType.STRING, "Category");
		data.addColumn(ColumnType.NUMBER, metricValue.getMetric().getName());
//		data.addColumn(ColumnType.NUMBER, "Obj");
		data.addRows(2);
		data.setValue(0, 0, title);
		data.setValue(0, 1, value);
//		data.setValue(0, 2, objectives);
		return data;
	}

}
