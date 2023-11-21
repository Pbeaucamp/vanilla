package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.OFC;
import jofc2.model.elements.HorizontalBarChart;
import jofc2.model.metadata.Alias;
import jofc2.model.metadata.Converter;

@Converter(HorizontalBarChartDrillableSliceConverter.class)
public class DrillableHorizontalBar  extends  HorizontalBarChart.Bar implements OfcDrillable{
	static{
		 OFC.getInstance().doAlias(DrillableHorizontalBar.class);
		 OFC.getInstance().doRegisterConverter(DrillableHorizontalBar.class);
	}
	
	@Alias ("on-click")
	private String url; 
	
	public DrillableHorizontalBar(Number arg0) {
		super(arg0);
		
	}

	public DrillableHorizontalBar(Number number, int count) {
		super(number, count);
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
