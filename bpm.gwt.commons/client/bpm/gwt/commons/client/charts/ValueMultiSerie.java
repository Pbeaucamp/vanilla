package bpm.gwt.commons.client.charts;

import java.util.ArrayList;
import java.util.List;

public class ValueMultiSerie extends ChartValue {

	private String serieName;
	
	private List<ChartValue> serieValues;

	public String getSerieName() {
		return serieName;
	}

	public void setSerieName(String serieName) {
		this.serieName = serieName;
	}

	public List<ChartValue> getSerieValues() {
		return serieValues;
	}

	public void setSerieValues(List<ChartValue> serieValues) {
		this.serieValues = serieValues;
	}

	public void addValue(ValueSimpleSerie val) {
		if(serieValues == null) {
			serieValues = new ArrayList<ChartValue>();
		}
		serieValues.add(val);
		
	}
	
	public void addValue(ChartValue val) {
		if(serieValues == null) {
			serieValues = new ArrayList<ChartValue>();
		}
		serieValues.add(val);
		
	}
	
}
