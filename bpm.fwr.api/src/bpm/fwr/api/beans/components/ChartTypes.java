package bpm.fwr.api.beans.components;

import java.io.Serializable;

public class ChartTypes implements Serializable {
	
	private static final long serialVersionUID = 7360410496231018335L;
	
	public static final ChartTypes COLUMN = new ChartTypes("Column Chart");
	public static final ChartTypes BAR = new ChartTypes("Bar Chart");
	public static final ChartTypes LINE = new ChartTypes("Line Chart");
	public static final ChartTypes PIE = new ChartTypes("Pie Chart");
	public static final ChartTypes SCATTER = new ChartTypes("Scatter Chart");
	public static final ChartTypes AREA = new ChartTypes("Area Chart");
	public static final ChartTypes DOUGHNUT = new ChartTypes("Doughnut Chart");
	public static final ChartTypes RADAR = new ChartTypes("Radar Chart");
	
	private String type;
	
	private ChartTypes(){ }
	
	private ChartTypes(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
}
