package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.OFC;
import jofc2.model.elements.BarChart.Bar;
import jofc2.model.metadata.Alias;
import jofc2.model.metadata.Converter;

@Converter(BarChartDrillableSliceConverter.class)
public class DrillableBar  extends Bar implements OfcDrillable{
	static{
		 OFC.getInstance().doAlias(DrillableBar.class);
		 OFC.getInstance().doRegisterConverter(DrillableBar.class);
	}
	
	@Alias ("on-click")
	private String url; 
	
	public DrillableBar(Number arg0, String arg1) {
		super(arg0, arg1);
		
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
