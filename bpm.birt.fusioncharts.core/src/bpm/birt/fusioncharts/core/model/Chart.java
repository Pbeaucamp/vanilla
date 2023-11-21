package bpm.birt.fusioncharts.core.model;

public class Chart implements IChart{
	private String name;
	
	//The chart is a single series chart or multi-series chart
	private boolean isMultiSeries;
	
	//The chart is in 2D or in 2D with depth
	private boolean is3D;
	
	//The chart is scrollable or not
	private boolean isScrollable;
	
	//The chart is a column chart, bar chart, ....
	private int type;
	
	//The chart is a side-by-side chart, overlay chart
	private int subType;
	
	private String xmlExemple;
	private String xmlExempleWithGlassStyle;

	public Chart(String name, boolean isMultiSeries, boolean is3D, boolean isScrollable, 
			int type, int subType, String xmlExemple, String xmlExempleWithGlassStyle){
		this.name = name;
		this.isMultiSeries = isMultiSeries;
		this.is3D = is3D;
		this.isScrollable = isScrollable;
		this.type = type;
		this.subType = subType;
		this.xmlExemple = xmlExemple;
		this.xmlExempleWithGlassStyle = xmlExempleWithGlassStyle;
	}
	
	@Override
	public String getChartName() {
		return name;
	}
	
	@Override
	public boolean isMultiSeries(){
		return isMultiSeries;
	}
	
	@Override
	public boolean is3D(){
		return is3D;
	}
	
	@Override
	public boolean isScrollable() {
		return isScrollable;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public int getSubType() {
		return subType;
	}
	
	@Override
	public String getXmlExemple(boolean useRoundEdge){
		if(useRoundEdge){
			return xmlExempleWithGlassStyle;
		}
		else{
			return xmlExemple;
		}
	}
	
}
