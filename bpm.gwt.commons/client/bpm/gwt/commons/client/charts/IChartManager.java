package bpm.gwt.commons.client.charts;

import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.analysis.ChartData;
import bpm.vanilla.platform.core.beans.chart.ChartType;
import bpm.vanilla.platform.core.beans.chart.IChartColumn;
import bpm.vanilla.platform.core.beans.chart.Serie;

public interface IChartManager<T extends IChartColumn> {
	
	public void addSerie();

	public void removeSerie(SeriePanel<T> serie);
	
	public void refresh();
	
	public void buildChart(ChartType type, List<Serie<T>> series, List<ChartData> chartData);
	
	public FWRReport buildWebReport() throws ServiceException;
}
