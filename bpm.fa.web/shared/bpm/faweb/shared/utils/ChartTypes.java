package bpm.faweb.shared.utils;


public class ChartTypes {
	
	public enum ChartType {
		BAR,
		COLUMN,
		PIE,
		CYLINDER,
		LINE, DONUT, STACKEDBAR, AREA, STACKEDAREA, SPLINE, STEPPED, BAR3D, COLUMN3D, PIE3D, STACKED3D
	} //SPIDERWEB,
	
	private static String BAR_CHART_LABEL = "Bar Chart";
	private static String COLUMN_CHART_LABEL = "Column Chart";
	private static String PIE_CHART_LABEL = "Pie Chart";
	private static String CYLINDER_CHART_LABEL = "Cylinder Chart";
	private static String LINE_CHART_LABEL = "Line Chart";
	private static String DONUT_CHART_LABEL = "Donut Chart";
	private static String STACKEDBAR_CHART_LABEL = "Stacked Bar Chart";
	private static String AREA_CHART_LABEL = "Area Chart";
	private static String STACKEDAREA_CHART_LABEL= "Stacked Area Chart";
	private static String SPLINE_CHART_LABEL= "Curve Line";
	private static String STEPPED_CHART_LABEL= "Stepped Area Chart";
	private static String BAR3D_CHART_LABEL = "Bar Chart 3D";
	private static String COLUMN3D_CHART_LABEL = "Column Chart 3D";
	private static String PIE3D_CHART_LABEL = "Pie Chart 3D";
	private static String STACKED3D_CHART_LABEL = "Stacked Bar Chart 3D";
	
	public static ChartType getTypeByLabel(String type) {
		if(type.equals(BAR_CHART_LABEL)) {
			return ChartType.BAR;
		}
		else if(type.equals(PIE_CHART_LABEL)) {
			return ChartType.PIE;
		}
		else if(type.equals(COLUMN_CHART_LABEL)) {
			return ChartType.COLUMN;
		}
		else if(type.equals(CYLINDER_CHART_LABEL)){
			return ChartType.CYLINDER;
		}
		else if(type.equals(LINE_CHART_LABEL)){
			return ChartType.LINE;
		}
		else if(type.equals(DONUT_CHART_LABEL)){
			return ChartType.DONUT;
		}
		else if(type.equals(STACKEDBAR_CHART_LABEL)){
			return ChartType.STACKEDBAR;
		}
		else if(type.equals(AREA_CHART_LABEL)){
			return ChartType.AREA;
		}
		else if(type.equals(STACKEDAREA_CHART_LABEL)){
			return ChartType.STACKEDAREA;
		}
		else if(type.equals(SPLINE_CHART_LABEL)){
			return ChartType.SPLINE;
		}
		else if(type.equals(STEPPED_CHART_LABEL)){
			return ChartType.STEPPED;
		}
		else if(type.equals(BAR3D_CHART_LABEL)){
			return ChartType.BAR3D;
		}
		else if(type.equals(COLUMN3D_CHART_LABEL)){
			return ChartType.COLUMN3D;
		}
		else if(type.equals(PIE3D_CHART_LABEL)){
			return ChartType.PIE3D;
		}
		else if(type.equals(STACKED3D_CHART_LABEL)){
			return ChartType.STACKED3D;
		}
		return null;
	}
	
	public static String getLabelByType(ChartType type) {
		if(type == ChartType.BAR) {
			return BAR_CHART_LABEL;
		}
		else if(type == ChartType.COLUMN) {
			return COLUMN_CHART_LABEL;
		}
		else if(type == ChartType.PIE) {
			return PIE_CHART_LABEL;
		}
		else if(type == ChartType.CYLINDER){
			return CYLINDER_CHART_LABEL;
		}
		else if(type == ChartType.LINE){
			return LINE_CHART_LABEL;
		}
		else if(type == ChartType.DONUT){
			return DONUT_CHART_LABEL;
		}
		else if(type == ChartType.STACKEDBAR){
			return STACKEDBAR_CHART_LABEL;
		}
		else if(type == ChartType.AREA){
			return AREA_CHART_LABEL;
		}
		else if(type == ChartType.STACKEDAREA){
			return STACKEDAREA_CHART_LABEL;
		}
		else if(type == ChartType.SPLINE){
			return SPLINE_CHART_LABEL;
		}
		else if(type == ChartType.STEPPED){
			return STEPPED_CHART_LABEL;
		}
		else if(type == ChartType.BAR3D){
			return BAR3D_CHART_LABEL;
		}
		else if(type == ChartType.COLUMN3D){
			return COLUMN3D_CHART_LABEL;
		}
		else if(type == ChartType.PIE3D){
			return PIE3D_CHART_LABEL;
		}
		else if(type == ChartType.STACKED3D){
			return STACKED3D_CHART_LABEL;
		}
		return "";
	}
}
