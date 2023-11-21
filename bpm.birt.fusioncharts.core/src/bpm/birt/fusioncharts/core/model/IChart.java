package bpm.birt.fusioncharts.core.model;

public interface IChart {
	
	//Type of chart
	public static final int COLUMN = 0;
	public static final int BAR = 1;
	public static final int LINE = 2;
	public static final int PIE = 3;
	public static final int DOUGHNUT = 4;
	public static final int RADAR = 5;
	public static final int PARETO = 6;
	public static final int DRAG_NODE = 7;
	
	public static final String COLUMN_NAME = "Column";
	public static final String BAR_NAME = "Bar";
	public static final String LINE_NAME = "Line";
	public static final String PIE_NAME = "Pie";
	public static final String DOUGHNUT_NAME = "Doughnut";
	public static final String RADAR_NAME = "Radar";
	public static final String PARETO_NAME = "Pareto";
	public static final String DRAG_NODE_NAME = "DragNode";
	
	public static final String[] CHART_TYPES = {COLUMN_NAME, BAR_NAME, LINE_NAME, PIE_NAME, DOUGHNUT_NAME, RADAR_NAME, PARETO_NAME, DRAG_NODE_NAME};
	
	//General sub-type
	
	//Column Sub-Type
	public static final int COLUMN_SUBTYPE_SIDE_BY_SIDE = 0;
	public static final int COLUMN_SUBTYPE_STACKED = 1;

	public static final String[] COLUMN_SUBTYPES = {"SIDE_BY_SIDE_COLUMN", "STACKED_COLUMN"};
	
	//Bar Sub-type
	public static final int BAR_SUBTYPE_SIDE_BY_SIDE = 0;
	public static final int BAR_SUBTYPE_STACKED = 1;
	
	public static final String[] BAR_SUBTYPES = {"SIDE_BY_SIDE_BAR", "STACKED_BAR"};
	
	//Line Sub-type
	public static final int LINE_SUBTYPE_OVERLAY = 0;
	
	public static final String[] LINE_SUBTYPES = {"OVERLAY_LINE"};
	
	//Pie Sub-type
	public static final int PIE_SUBTYPE = 0;
	
	public static final String[] PIE_SUBTYPES = {"PIE"};
	
	//Doughnut Sub-type
	public static final int DOUGHNUT_SUBTYPE = 0;
	
	public static final String[] DOUGHNUT_SUBTYPES = {"DOUGHNUT"};
	
	//Radar Sub-type
	public static final int RADAR_SUBTYPE = 0;
	
	public static final String[] RADAR_SUBTYPES = {"RADAR"};
	
	//Pareto Sub-type
	public static final int PARETO_SUBTYPE = 0;
	
	public static final String[] PARETO_SUBTYPES = {"PARETO"};
	
	public static final int DRAG_NODE_SUB = 0;
	public static final String[] DRAG_NODE_SUBS = {"DragNode"};
	
	//Dimension
	public static final String TWO_D = "2D";
	public static final String THREE_D = "3D";
	
	public static final String[] DIMENSIONS = {TWO_D, THREE_D};
	public static final String[] DIMENSION_TWO_D_ONLY = {TWO_D};
	
	
	/*
	 * Chart method
	 */
	public String getChartName();
	
	public boolean isMultiSeries();
	
	public boolean is3D();
	
	public boolean isScrollable();
	
	public int getType();
	
	public int getSubType();
	
	public String getXmlExemple(boolean useRoundEdges);
}
