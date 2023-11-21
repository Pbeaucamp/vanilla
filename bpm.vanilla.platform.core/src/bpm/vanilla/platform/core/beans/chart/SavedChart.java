package bpm.vanilla.platform.core.beans.chart;

import java.io.Serializable;
import java.util.List;

import bpm.vanilla.platform.core.beans.fmdt.FmdtData;

public class SavedChart implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private ChartType chartType;
	private ChartColumn<FmdtData> axeX;
	private String axeXLabel;
	private List<Serie<FmdtData>> series;
	
	public SavedChart() {
	}

	public SavedChart(String title, ChartType chartType, ChartColumn<FmdtData> axeX, String axeXLabel, List<Serie<FmdtData>> series) {
		this.title = title;
		this.chartType = chartType;
		this.axeX = axeX;
		this.axeXLabel = axeXLabel;
		this.series = series;
	}
	
	public String getTitle() {
		return title;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public ChartColumn<FmdtData> getAxeX() {
		return axeX;
	}
	
	public String getAxeXLabel() {
		return axeXLabel;
	}

	public List<Serie<FmdtData>> getSeries() {
		return series;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SavedChart other = (SavedChart) obj;
		if (axeX == null) {
			if (other.axeX != null)
				return false;
		}
		else if (!axeX.equals(other.axeX))
			return false;
		if (axeXLabel == null) {
			if (other.axeXLabel != null)
				return false;
		}
		else if (!axeXLabel.equals(other.axeXLabel))
			return false;
		if (chartType == null) {
			if (other.chartType != null)
				return false;
		}
		else if (!chartType.equals(other.chartType))
			return false;
		if (series == null) {
			if (other.series != null)
				return false;
		}
		else if (!series.equals(other.series))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	
}
