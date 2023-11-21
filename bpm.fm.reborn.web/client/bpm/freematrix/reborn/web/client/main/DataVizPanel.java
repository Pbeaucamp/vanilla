package bpm.freematrix.reborn.web.client.main;

import java.util.List;

import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.main.home.metrics.MetricsPanel;
import bpm.gwt.commons.client.charts.ChartJsPanel;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ValueSimpleSerie;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizPanel extends Composite {

	private static DataVizPanelUiBinder uiBinder = GWT.create(DataVizPanelUiBinder.class);

	interface DataVizPanelUiBinder extends UiBinder<Widget, DataVizPanel> {}

	@UiField 
	HTMLPanel mainPanel;
	
	public DataVizPanel(FreeMatrixMain freeMatrixMain) {
		initWidget(uiBinder.createAndBindUi(this));
		ChartObject chartObject = new ChartObject();
		chartObject.setTitle("Comparatifs indicateurs");
		
		List<MetricValue> values = MetricsPanel.getInstance().getDatagrid().getDataProvider().getList();
		for(MetricValue val : values) {
			if(!val.isDummy()) {
				ValueSimpleSerie v = new ValueSimpleSerie();
				v.setCategory(val.getMetric().getName());
				v.setValue(val.getValue());
				chartObject.getValues().add(v);
			}
		}
		
		mainPanel.add(new ChartJsPanel(chartObject, ChartObject.TYPE_SIMPLE, ChartObject.RENDERER_PIE));
	}

}
