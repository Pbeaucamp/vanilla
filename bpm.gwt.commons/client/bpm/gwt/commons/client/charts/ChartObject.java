package bpm.gwt.commons.client.charts;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;

public class ChartObject {

	public static final String RENDERER_PIE = "pie3d";
	public static final String RENDERER_DONUT = "doughnut3d";
	public static final String RENDERER_BAR = "bar2d";
	public static final String RENDERER_COLUMN = "column3d";
	public static final String RENDERER_LINE = "line";
	public static final String RENDERER_PARETO = "pareto3d";
	public static final String RENDERER_AREA = "area2d";
	public static final String RENDERER_SPLINE = "spline";
	public static final String RENDERER_FUNNEL = "funnel";
	public static final String RENDERER_PYRAMID = "pyramid";
	public static final String RENDERER_RADAR = "radar";
	
	public static final String RENDERER_MULTI_BAR = "msbar2d";
	public static final String RENDERER_MULTI_COLUMN = "mscolumn3d";
	public static final String RENDERER_MULTI_LINE = "msline";
	public static final String RENDERER_MULTI_AREA = "msarea";
	public static final String RENDERER_MULTI_STACKEDCOLUMN = "stackedcolumn3d";
	public static final String RENDERER_MULTI_STACKEDAREA= "stackedarea2d";
	public static final String RENDERER_MULTI_STEPLINE= "msstepLine";
	
	
	public static final String RENDERER_GAUGE_ANGULAR = "angulargauge";
	public static final String RENDERER_GAUGE_LINEAR = "hled";
	
	public static final String RENDERER_SCATTER="scatter";
	
	public static final String SIMPLE_RENDERERS[] = {RENDERER_BAR, RENDERER_PIE, RENDERER_DONUT, RENDERER_COLUMN, RENDERER_LINE, RENDERER_PARETO, RENDERER_AREA, RENDERER_SPLINE, RENDERER_RADAR};
	public static final String MULTI_RENDERERS[] = {RENDERER_MULTI_BAR, RENDERER_MULTI_COLUMN, RENDERER_MULTI_LINE, RENDERER_MULTI_AREA, RENDERER_MULTI_STACKEDCOLUMN, RENDERER_MULTI_STACKEDAREA, RENDERER_MULTI_STEPLINE, RENDERER_RADAR};
	public static final String GAUGE_RENDERERS[] = {RENDERER_GAUGE_ANGULAR, RENDERER_GAUGE_LINEAR};
	
	public static final String TYPE_SIMPLE = "Simple";
	public static final String TYPE_MULTI = "Multi";
	public static final String TYPE_GAUGE = "Gauge";
	public static final String TYPE_SCATTER_BUBBLE = "ScatterBubble";
	
	public static final String[] TYPES = {TYPE_SIMPLE, TYPE_MULTI, TYPE_GAUGE};
	
	protected String title;

	protected String xAxisName = "";
	protected String yAxisName = "";

	protected List<ChartValue> values = new ArrayList<ChartValue>();
	protected List<String> colors=null;

	protected int width;
	protected int height;

	protected Unit widthUnit = Unit.PX;
	protected Unit heightUnit = Unit.PX;

	public String getTitle() {
		if(title != null) {
			return title.replace("'", "&apos;");
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getxAxisName() {
		return xAxisName;
	}

	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public String getyAxisName() {
		return yAxisName;
	}

	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public List<ChartValue> getValues() {
		return values;
	}

	public void setValues(List<ChartValue> values) {
		this.values = values;
	}

	public List<String> getColors() {
		return colors;
	}

	public void setColors(List<String> colors) {
		this.colors = colors;
	}
	
	public void addColor(String color) {
		if (colors == null) {
			this.colors = new ArrayList<String>();
		}
		this.colors.add(color);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Unit getWidthUnit() {
		return widthUnit;
	}

	public void setWidthUnit(Unit widthUnit) {
		this.widthUnit = widthUnit;
	}

	public Unit getHeightUnit() {
		return heightUnit;
	}

	public void setHeightUnit(Unit heightUnit) {
		this.heightUnit = heightUnit;
	}
	
	public boolean isMultiSeries() {
		return values.get(0) instanceof ValueMultiSerie;
	}

}
