package bpm.fd.core.component;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentData;
import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.kpi.KpiChart;
import bpm.fd.core.component.kpi.KpiElement;
import bpm.vanilla.platform.core.beans.data.Dataset;

public class KpiChartComponent extends DashboardComponent implements IComponentData, IComponentOption {

	public static String CHART_TYPE = "CHART";
	public static String FILTER_TYPE = "FILTER";
	public static String MAP_TYPE = "MAP";
	
	private static final long serialVersionUID = 1L;
	
	private Dataset dataset;
	
	private KpiElement element = new KpiChart();
	
	@Override
	public Dataset getDataset() {
		return dataset;
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.KPI_CHART;
	}

	public KpiElement getElement() {
		return element;
	}

	public void setElement(KpiElement element) {
		this.element = element;
	}

	@Override
	protected void clearData() {
		// TODO Auto-generated method stub
		
	}

}
