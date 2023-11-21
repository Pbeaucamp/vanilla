package bpm.birt.fusioncharts.core.chart;

import bpm.birt.fusioncharts.core.model.Chart;
import bpm.birt.fusioncharts.core.model.IChart;
import bpm.birt.fusioncharts.core.xmldata.XmlData;

public class AvailableChart {
	
	/*
	 * List of all the charts available
	 */
	
	public static final Chart AREA_2D = new Chart("Area2D.swf", false, false, false, IChart.BAR, IChart.COLUMN_SUBTYPE_SIDE_BY_SIDE, "", "");
	public static final Chart BAR_2D = new Chart("Bar2D.swf", false, false, false, IChart.BAR, IChart.BAR_SUBTYPE_SIDE_BY_SIDE, XmlData.SIMPLE_DATA, XmlData.SIMPLE_DATA_GLASS);
//	public static final Chart BUBBLE = new Chart("Bubble.swf");
	public static final Chart COLUMN_2D = new Chart("Column2D.swf", false, false, false, IChart.COLUMN, IChart.COLUMN_SUBTYPE_SIDE_BY_SIDE, XmlData.SIMPLE_DATA, XmlData.SIMPLE_DATA_GLASS);
	public static final Chart COLUMN_3D = new Chart("Column3D.swf", false, true, false, IChart.COLUMN, IChart.COLUMN_SUBTYPE_SIDE_BY_SIDE, XmlData.SIMPLE_DATA, "");
	public static final Chart DOUGHNUT_2D = new Chart("Doughnut2D.swf", false, false, false, IChart.DOUGHNUT, IChart.DOUGHNUT_SUBTYPE, XmlData.SIMPLE_DATA_PIE_DOUGHNUT, "");
	public static final Chart DOUGHNUT_3D = new Chart("Doughnut3D.swf", false, true, false, IChart.DOUGHNUT, IChart.DOUGHNUT_SUBTYPE, XmlData.SIMPLE_DATA_PIE_DOUGHNUT, "");
	public static final Chart LINE = new Chart("Line.swf", false, false, false, IChart.LINE, IChart.LINE_SUBTYPE_OVERLAY, XmlData.SIMPLE_DATA, "");
//	public static final Chart MARIMEKKO = new Chart("Marimekko.swf");
//	public static final Chart MS_AREA = new Chart("MSArea.swf");
	public static final Chart MS_BAR_2D = new Chart("MSBar2D.swf", true, false, false, IChart.BAR, IChart.BAR_SUBTYPE_SIDE_BY_SIDE, XmlData.MS_DATA_ONE_SERIE, XmlData.MS_DATA_ONE_SERIE_GLASS);
	public static final Chart MS_BAR_3D = new Chart("MSBar3D.swf", true, true, false, IChart.BAR, IChart.BAR_SUBTYPE_SIDE_BY_SIDE, XmlData.MS_DATA_ONE_SERIE, "");
	public static final Chart MS_COLUMN_2D = new Chart("MSColumn2D.swf", true, false, false, IChart.COLUMN, IChart.COLUMN_SUBTYPE_SIDE_BY_SIDE, XmlData.MS_DATA_TWO_SERIE, XmlData.MS_DATA_TWO_SERIE_GLASS);
	public static final Chart MS_COLUMN_3D = new Chart("MSColumn3D.swf", true, true, false, IChart.COLUMN, IChart.COLUMN_SUBTYPE_SIDE_BY_SIDE, XmlData.MS_DATA_ONE_SERIE, "");
//	public static final Chart MS_COLUMN_3D_LINE_DY = new Chart("MSColumn3DLineDY.swf");
//	public static final Chart MS_COLUMN_LINE_3D = new Chart("MSColumnLine3D.swf");
//	public static final Chart MS_COMBI_2D = new Chart("MSCombi2D.swf");
//	public static final Chart MS_COMBI_3D = new Chart("MSCombi3D.swf");
//	public static final Chart MS_COMBI_DY_2D = new Chart("MSCombiDY2D.swf");
	public static final Chart MS_LINE = new Chart("MSLine.swf", true, false, false, IChart.LINE, IChart.LINE_SUBTYPE_OVERLAY, XmlData.MS_DATA_TWO_SERIE, "");
	public static final Chart MS_STACKED_COLUMN_2D = new Chart("MSStackedColumn2D.swf", true, false, false, IChart.COLUMN, IChart.COLUMN_SUBTYPE_STACKED, XmlData.MS_STACKED_DATA_TWO_SERIE, XmlData.MS_STACKED_DATA_TWO_SERIE_GLASS);
//	public static final Chart MS_STACKED_COLUMN_2D_LINE_DY = new Chart("MSStackedColumn2DLineDY.swf");
	public static final Chart PARETO_2D = new Chart("Pareto2D.swf", false, false, false, IChart.PARETO, IChart.PARETO_SUBTYPE, XmlData.SIMPLE_DATA, XmlData.SIMPLE_DATA_GLASS);
	public static final Chart PARETO_3D = new Chart("Pareto3D.swf", false, true, false, IChart.PARETO, IChart.PARETO_SUBTYPE, XmlData.SIMPLE_DATA, XmlData.SIMPLE_DATA_GLASS);
	public static final Chart PIE_2D = new Chart("Pie2D.swf", false, false, false, IChart.PIE, IChart.PIE_SUBTYPE, XmlData.SIMPLE_DATA_PIE_DOUGHNUT, "");
	public static final Chart PIE_3D = new Chart("Pie3D.swf", false, true, false, IChart.PIE, IChart.PIE_SUBTYPE, XmlData.SIMPLE_DATA_PIE_DOUGHNUT, "");
	public static final Chart RADAR = new Chart("Radar.swf", true, false, false, IChart.RADAR, IChart.RADAR_SUBTYPE, XmlData.MS_DATA_TWO_SERIE_RADAR, "");
//	public static final Chart SCATTER = new Chart("Scatter.swf");
//	public static final Chart SCROLL_AREA_2D = new Chart("ScrollArea2D.swf");
	public static final Chart SCROLL_COLUMN_2D = new Chart("ScrollColumn2D.swf", false, false, true, IChart.COLUMN, IChart.COLUMN_SUBTYPE_SIDE_BY_SIDE, "", "");
//	public static final Chart SCROLL_COMBI_2D = new Chart("ScrollCombi2D.swf");
//	public static final Chart SCROLL_COMBI_DY_2D = new Chart("ScrollCombiDY2D.swf");
	public static final Chart SCROLL_LINE_2D = new Chart("ScrollLine2D.swf", false, false, true, IChart.LINE, IChart.LINE_SUBTYPE_OVERLAY, "", "");
	public static final Chart SCROLL_STACKED_COLUMN_2D = new Chart("ScrollStackedColumn2D.swf", false, false, true, IChart.COLUMN, IChart.COLUMN_SUBTYPE_STACKED, "", "");
//	public static final Chart SS_GRID = new Chart("SSGrid.swf");
//	public static final Chart STACKED_AREA_2D = new Chart("StackedArea2D.swf");
	public static final Chart STACKED_BAR_2D = new Chart("StackedBar2D.swf", true, false, false, IChart.BAR, IChart.BAR_SUBTYPE_STACKED, XmlData.MS_DATA_TWO_SERIE, XmlData.MS_DATA_TWO_SERIE_GLASS);
	public static final Chart STACKED_BAR_3D = new Chart("StackedBar3D.swf", true, true, false, IChart.BAR, IChart.BAR_SUBTYPE_STACKED, XmlData.MS_DATA_TWO_SERIE, XmlData.MS_DATA_TWO_SERIE_GLASS);
	public static final Chart STACKED_COLUMN_2D = new Chart("StackedColumn2D.swf", true, false, false, IChart.COLUMN, IChart.COLUMN_SUBTYPE_STACKED, XmlData.MS_DATA_TWO_SERIE, XmlData.MS_DATA_TWO_SERIE_GLASS);
//	public static final Chart STACKED_COLUMN_2D_LINE = new Chart("StackedColumn2DLine.swf");
	public static final Chart STACKED_COLUMN_3D = new Chart("StackedColumn3D.swf", true, true, false, IChart.COLUMN, IChart.COLUMN_SUBTYPE_STACKED, XmlData.MS_DATA_TWO_SERIE, "");
//	public static final Chart STACKED_COLUMN_3D_LINE = new Chart("StackedColumn3DLine.swf");
//	public static final Chart STACKED_COLUMN_3D_LINE_DY = new Chart("StackedColumn3DLineDY.swf");
//	public static final Chart ZOOM_LINE = new Chart("ZoomLine.swf");
	public static final Chart DRAG_NODE = new Chart("DragNode.swf", true, false, false, IChart.DRAG_NODE, IChart.DRAG_NODE_SUB, "", "");
	
	public static final Chart[] AVAILABLE_CHARTS_COLUMN = {COLUMN_2D, COLUMN_3D, STACKED_COLUMN_2D, STACKED_COLUMN_3D, 
		MS_COLUMN_2D, MS_COLUMN_3D, MS_STACKED_COLUMN_2D, SCROLL_COLUMN_2D, SCROLL_STACKED_COLUMN_2D};
	
	public static final Chart[] AVAILABLE_CHARTS_BAR = {BAR_2D, MS_BAR_2D, MS_BAR_3D, STACKED_BAR_2D,
		STACKED_BAR_3D};
	
	public static final Chart[] AVAILABLE_CHARTS_LINE = {LINE, MS_LINE, SCROLL_LINE_2D};
	
	public static final Chart[] AVAILABLE_CHARTS_PIE = {PIE_2D, PIE_3D};
	
	public static final Chart[] AVAILABLE_CHARTS_DOUGHNUT = {DOUGHNUT_2D, DOUGHNUT_3D};
	
	public static final Chart[] AVAILABLE_CHARTS_RADAR = {RADAR};
	
	public static final Chart[] AVAILABLE_CHARTS_PARETO = {PARETO_2D, PARETO_3D};
	
	public static final Chart[] AVAILABLE_DRAG_NODE = {DRAG_NODE};
}
