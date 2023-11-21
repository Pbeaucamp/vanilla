package bpm.gwt.commons.client.charts;

import java.util.List;

import bpm.fwr.api.beans.FWRReport;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.vanilla.platform.core.beans.chart.ChartType;
import bpm.vanilla.platform.core.beans.chart.IChartColumn;
import bpm.vanilla.platform.core.beans.chart.Serie;

public interface IChartConstructor<T extends IChartColumn> {
	
	public void refreshChart(ChartType type, T axeX, List<Serie<T>> series);
	
	public FWRReport buildWebReport(String title, ChartType type, T axeX, List<Serie<T>> series) throws ServiceException;
}
