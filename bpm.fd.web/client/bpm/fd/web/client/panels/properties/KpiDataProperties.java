package bpm.fd.web.client.panels.properties;

import java.util.List;

import bpm.fd.core.IComponentData;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.kpi.KpiAggreg;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.fd.web.client.panels.properties.widgets.KpiChartDataPanel;
import bpm.fm.api.model.Level;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class KpiDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static KpiDataPropertiesUiBinder uiBinder = GWT.create(KpiDataPropertiesUiBinder.class);

	interface KpiDataPropertiesUiBinder extends UiBinder<Widget, KpiDataProperties> {
	}

	@UiField
	SimplePanel dataPanel;
	
	private KpiChartDataPanel chartPanel;

	private KpiChartComponent component;
	
	public KpiDataProperties(KpiChartComponent component, DataProperties parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.component = component;
		parent.hideDatasetSelection();
		chartPanel = new KpiChartDataPanel(component);
		dataPanel.add(chartPanel);
	}
	
	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataset(Dataset dataset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buildProperties(IComponentData component) {
		Dataset dataset = chartPanel.getDataset();
		component.setDataset(dataset);
		
		List<KpiAggreg> aggs = chartPanel.getAggregations();
		Level level = chartPanel.getLevel();
		
		KpiChart chart = (KpiChart) ((KpiChartComponent)component).getElement();
		chart.setAggregs(aggs);
		chart.setLevel(level);
		
		chartPanel.refreshTypeChart();
		
		this.component.setElement(chart);
		
	}

}
