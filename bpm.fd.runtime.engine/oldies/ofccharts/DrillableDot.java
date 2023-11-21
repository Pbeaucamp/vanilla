package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.OFC;
import jofc2.model.elements.LineChart.Dot;
import jofc2.model.metadata.Alias;
import jofc2.model.metadata.Converter;

@Converter(LineChartDrillableSliceConverter.class)
public class DrillableDot  extends Dot implements OfcDrillable{
	static{
		 OFC.getInstance().doAlias(DrillableDot.class);
		 OFC.getInstance().doRegisterConverter(DrillableDot.class);
	}
	
	@Alias ("on-click")
	private String url; 
	
	public DrillableDot(Number arg0, String arg1) {
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
