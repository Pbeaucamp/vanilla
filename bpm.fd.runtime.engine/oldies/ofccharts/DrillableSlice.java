package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.OFC;
import jofc2.model.elements.PieChart.Slice;
import jofc2.model.metadata.Alias;
import jofc2.model.metadata.Converter;

@Converter(PieChartDrillableSliceConverter.class)
public class DrillableSlice extends Slice implements OfcDrillable{

	static{
		 OFC.getInstance().doAlias(DrillableSlice.class);
		 OFC.getInstance().doRegisterConverter(DrillableSlice.class);
	}
	
	@Alias ("on-click")
	private String url; 
	
	public DrillableSlice(Number arg0, String arg1) {
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
