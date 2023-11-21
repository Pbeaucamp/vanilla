package bpm.architect.web.client.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.gwt.commons.client.charts.ChartJsPanel;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ChartValue;
import bpm.gwt.commons.client.charts.ValueSimpleSerie;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class DataVizChartPanel extends Composite {

	private static DataVizChartPanelUiBinder uiBinder = GWT.create(DataVizChartPanelUiBinder.class);

	interface DataVizChartPanelUiBinder extends UiBinder<Widget, DataVizChartPanel> {}

	@UiField
	HTMLPanel chartPanel;
	
	private DataPreparationResult prep;
	
	public DataVizChartPanel(DataPreparationResult prep, DataColumn column) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.prep = prep;
		
		generateChart(column);
		

	}

	public void generateChart(DataColumn column) {
		ChartObject chart = new ChartObject();
		chart.setTitle("Count");
		
		List<ChartValue> values = new ArrayList<ChartValue>();
		
		HashMap<String, Integer> valueCounts = new HashMap<>();
		for(Map<DataColumn, Serializable> map : prep.getValues()) {
			Serializable val = map.get(column);
			if(val == null) {
				val = "";
			}
			if(valueCounts.get(val.toString()) == null) {
				valueCounts.put(val.toString(), 0);
			}
			valueCounts.put(val.toString(), valueCounts.get(val.toString()) + 1);
		}
		
		for(String key : valueCounts.keySet()) {
			ValueSimpleSerie v = new ValueSimpleSerie();
			v.setCategory(key);
			v.setValue(valueCounts.get(key).doubleValue());
			values.add(v);
		}
		
		chart.setValues(values);
		ChartJsPanel fsPanel = new ChartJsPanel(chart, "Count", ChartObject.RENDERER_PIE);
//		FusionChartsPanel fsPanel = new FusionChartsPanel(chart, "Count", ChartObject.RENDERER_PIE, false);
		chartPanel.clear();
		chartPanel.add(fsPanel);
	}

}
