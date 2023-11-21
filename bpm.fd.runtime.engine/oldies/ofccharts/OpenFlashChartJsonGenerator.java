package bpm.fd.runtime.engine.chart.ofc.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import jofc2.model.Chart;
import jofc2.model.Text;
import jofc2.model.axis.HorizontalBarYAxis;
import jofc2.model.axis.Label;
import jofc2.model.axis.XAxis;
import jofc2.model.axis.YAxis;
import jofc2.model.axis.Label.Rotation;
import jofc2.model.elements.BarChart;
import jofc2.model.elements.Element;
import jofc2.model.elements.HorizontalBarChart;
import jofc2.model.elements.LineChart;
import jofc2.model.elements.PieChart;
import jofc2.model.elements.BarChart.Bar;
import jofc2.model.elements.LineChart.Dot;
import jofc2.model.elements.PieChart.Slice;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;

public class OpenFlashChartJsonGenerator {
	
	protected final static String borderColor = "borderColor";
	protected final static String borderThickness= "borderThickness";
	protected final static String numberSuffix = "numberSuffix";
	protected final static String decimalSeparator = "decimalSeparator";
	protected final static String subCaption = "subCaption";
	protected final static String bgColor = "bgColor";
	protected final static String formatNumber = "formatNumber";
	protected final static String pieSliceDepth = "pieSliceDepth";
	protected final static String showBorder = "showBorder";
	protected final static String thousandSeparator = "thousandSeparator";
	protected final static String slicingDistance = "slicingDistance";
	protected final static String caption = "caption";
	protected final static String showLabel = "showLabel";
	protected final static String showValues = "showValues";
	
	protected final static String bgAlpha = "bgAlpha";
	protected final static String bgSWFAlpha = "bgSWFAlpha";
	
	protected final static String rotateLabels = "rotateLabels";
	protected final static String slantLabels = "slantLabels";
	protected final static String placeValuesInside = "placeValuesInside";
	protected final static String rotateYAxisName = "rotateYAxisName";
	protected final static String lineSerieName = "lineSerieName";
	protected final static String PYAxisName = "PYAxisName";
	protected final static String SYAxisName = "SYAxisName";
	

	public String generateMonoSerie(List<List<Object>> values, Properties chartProperties, Properties drillProperties,  String[][] colors) throws Exception{
		Integer chartNature = Integer.parseInt(chartProperties.getProperty("chartNature"));
		
		
		try{
			Collections.sort(values, new Comparator<List<Object>>() {

				public int compare(List<Object> l1, List<Object> l2) {
					if (l1.isEmpty() || l2.isEmpty()){
						return -1;
					}
					else if (l1.get(3) == null){
						return 1;
					}
					else if (l2.get(3) == null){
						return -1;
					}
					try{
						return ((Comparable)l1.get(3)).compareTo((Comparable)l2.get(3));
					}catch(Exception ex){
						
					}
					return l1.get(3).toString().compareTo(l2.get(3).toString());
					
				}
			});
		}catch(Exception ex){
			System.err.println("Error when sorting");
			ex.printStackTrace();
		}
		
		
		Chart chart = new Chart();
		
		chart.setBackgroundColour(chartProperties.getProperty(bgColor, ""));
		chart.setTitle(new Text(chartProperties.getProperty(caption, "")));
		Number[] min_max = new Number[2];
		
		try{
			switch(chartNature){
			case ChartNature.PIE:
				
				chart.addElements(generatePieChart(values, chartProperties, drillProperties, colors));
				break;
			case ChartNature.COLUMN:
				
				XAxis xaxis = new XAxis();
				chart.setXAxis(xaxis);
				chart.addElements(generateColumnChart(values, chartProperties, drillProperties, colors, xaxis, min_max));
				
				chart.setYAxis(new YAxis());
				if (min_max[0] != null){
					if (min_max[0] != null && min_max[0].toString().contains(".")){
						chart.getYAxis().setMin(Double.valueOf(min_max[0].toString()));
					}
					else{
						chart.getYAxis().setMin(Integer.valueOf(min_max[0].toString()));
					}
				}
				
				
				if (min_max[1]!= null){
					if (min_max[1].toString().contains(".")){
						chart.getYAxis().setMax(Double.valueOf(min_max[1].toString()));
					}
					else{
						chart.getYAxis().setMax(Integer.valueOf(min_max[1].toString()));
					}
				}
				

				break;
			case ChartNature.LINE:
				xaxis = new XAxis();
				chart.setXAxis(xaxis);
				
				chart.addElements(generateLineChart(values, chartProperties, drillProperties, colors, xaxis, min_max));
				
				chart.setYAxis(new YAxis());
				if (min_max[0] != null){
					if (min_max[0] != null && min_max[0].toString().contains(".")){
						chart.getYAxis().setMin(Double.valueOf(min_max[0].toString()));
					}
					else{
						chart.getYAxis().setMin(Integer.valueOf(min_max[0].toString()));
					}
				}
				
				
				if (min_max[1] != null){
					if (min_max[1].toString().contains(".")){
						chart.getYAxis().setMax(Double.valueOf(min_max[1].toString()));
					}
					else{
						chart.getYAxis().setMax(Integer.valueOf(min_max[1].toString()));
					}
				}
				break;	
			case ChartNature.BAR:
				HorizontalBarYAxis axis = new HorizontalBarYAxis();
				chart.setYAxis(axis);
				
				chart.addElements(generateBarHorizontalChart(values, chartProperties, drillProperties, colors, axis));
				break;
				
			default:
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

//		String s = "{ \"elements\": [ { \"type\": \"pie\", \"animate\": [ { \"type\": \"fade\" } ], \"label-colour\": \"#432BAF\", \"alpha\": 0.75, \"tip\": \"#label#	$#val# (#percent#)\", \"on-click\": \"pie_slice_clicked\", \"colours\": [ \"#77CC6D\", \"#FF5973\", \"#6D86CC\" ], \"values\": [ { \"value\": 120, \"label\": \"X\" }, { \"value\": 99, \"label\": \"Y\" }, { \"value\": 21, \"label\": \"Z\", \"on-click\": \"http:\\/\\/example.com\" } ] } ] }";
		
		String s = chart.toDebugString();
		return chart.toString();//URLEncoder.encode(chart.toString());
	}

	
	
	
	
	
	
	public  static String generateRandomColor(List<String> colors){
		boolean ok = false;
		String color = null;
		while(! ok){
			double dRed = Math.random();
			String red = Integer.toHexString(Double.valueOf(dRed * 255.0f).intValue());
			if (red.length() == 1){
				red = "0" + red;
			}
			
			double dBlue = Math.random();
			String blue = Integer.toHexString(Double.valueOf(dBlue * 255.0f).intValue());
			if (blue.length() == 1){
				blue = "0" + blue;
			}
			double dGreen = Math.random();
			String green = Integer.toHexString(Double.valueOf(dGreen * 255.0f).intValue());
			if (green.length() == 1){
				green = "0" + green;
			}
			color = red + blue + green;
			for(String s : colors){
				if (s.equals(color)){
					ok = false;
					continue;
				}
			}
			ok = true;
		}
		colors.add(color);
		return color;
	}
	
	protected  void setDrill(String value, String categorie, Properties drillProperties, OfcDrillable ofcElement){
		String url = drillProperties.getProperty("url");
		String pName = drillProperties.getProperty("pName");
		String categorieAsValue = drillProperties.getProperty("categoryAsValue");
		String drillJs = drillProperties.getProperty("drillJs");
		String link = "";
		
		//replace pName='XXX' by the value
		if (url == null && pName == null){
			return;
		}
		else if (pName == null){
			link = url;
			if (categorieAsValue == null || false == Boolean.parseBoolean(categorieAsValue)){
				link += value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
				
			}
			else{

				link += categorie.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");

				
				
			}
		}
		else if (pName != null){
			int start = url.indexOf(pName + "=");
			int end = url.indexOf("&", start);
			
			if (categorieAsValue == null || false == Boolean.parseBoolean(categorieAsValue)){
				if (start == -1){
					if (url.contains("?")){
						link = url + "&" + pName + "=" + value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
					}
					else{
						link = url + "?" + pName + "=" + value.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
					}

				}
				else{
					link = url.substring(0, start + (pName + "=").length());
					
					link += categorie.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
	
					
					
					if (end != -1){
						link += url.substring(end);
					}
				}
				
			}
			else{
				if (start == -1){
					if (url.contains("?")){
						link = url + "&" + pName + "=" + categorie.toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
					}
					else{
						link = url + "?" + pName + "=" + categorie.toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
					}

				}
				else{
					link = url.substring(0, start + (pName + "=").length());
					
					link += categorie.toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
	
					
					
					if (end != -1){
						link += url.substring(end);
					}
				}
				
			}
		}
		if (drillJs != null){
//			set.addAttribute(DRILL, "JavaScript:openUrl(&#39;" + URLEncoder.encode(link, "UTF-8") +"&#39;)");
		}
		else{
//			set.addAttribute(DRILL, URLEncoder.encode( link , "UTF-8"));
			if (categorieAsValue == null || false == Boolean.parseBoolean(categorieAsValue)){
				ofcElement.setUrl("function(){" + url + "('" + value + "')}");
			}
			else{
				ofcElement.setUrl("function(){" + url + "('" + categorie + "')}");
			}
			
//			((DrillableSlice)sl).setUrl("http://www.google.com");
		}
	}
	
	protected Element generatePieChart(List<List<Object>> values, Properties chartProperties, Properties drillProperties,  String[][] colors){
		PieChart pie = new PieChart();
		
		pie.setBorder(Integer.parseInt(chartProperties.getProperty(borderThickness, "1")));
		pie.setNoLabels(Boolean.parseBoolean(chartProperties.getProperty(showLabel, "true")));
		pie.setText(chartProperties.getProperty(subCaption, ""));
		
//		pie.setKey_on_click("pie_slice_clicked");
		List<String> _colors = new ArrayList<String>();
		if (colors != null){
			
			
			for(int i = 0; i < colors[0].length; i++){
				_colors.add(colors[0][i]);
			}
			
		}
		
		int count = 0;
		for(List<Object> row : values){
			
			Slice sl =  null;
			if (drillProperties != null){
				sl = new DrillableSlice((Number)row.get(1), row.get(0) != null ? row.get(0).toString() : "null");
				//((DrillableSlice)sl).setUrl(link);
				setDrill(row.get(1).toString(), row.get(2).toString(), drillProperties, (OfcDrillable)sl);
			}
			else{
				sl = new Slice((Number)row.get(1), row.get(0) != null ? row.get(0).toString() : "null");
			}
			
			
			
			pie.addSlices(sl);
			//generate a RandomColor
			if (count >= _colors.size()){
				generateRandomColor(_colors);
			}
			count++;
		}
		
		pie.setColours(_colors);
		return pie;
	}

	protected Element generateColumnChart(List<List<Object>> values, Properties chartProperties, Properties drillProperties,  String[][] colors, XAxis x_axis, Number[] min_max){
		BarChart bar = new BarChart();
		
		
//		bar.setKey_on_click("pie_slice_clicked");
		List<String> _colors = new ArrayList<String>();
		if (colors != null){
			
			
			for(int i = 0; i < colors[0].length; i++){
				_colors.add(colors[0][i]);
			}
			
		}
		
		int count = 0;
		List<Bar> bars = new ArrayList<Bar>();
		
		Rotation rotation = Rotation.HORIZONTAL;
		
		if (Integer.parseInt(chartProperties.getProperty(rotateLabels, "0")) == 1){
			if (Integer.parseInt(chartProperties.getProperty(slantLabels, "0")) == 1){
				rotation = Rotation.HALF_DIAGONAL;
			}
			else{
				rotation = Rotation.VERTICAL;
			}
		}
		
		
		for(List<Object> row : values){

			Bar sl = null;
			
			
			if (min_max[0] == null || ((Comparable)row.get(1)).compareTo(min_max[0]) < 0){
				min_max[0] = (Number)row.get(1);
			}
			
			if (min_max[1] == null || ((Comparable)row.get(1)).compareTo(min_max[1]) > 0){
				min_max[1] = (Number)row.get(1);
			}
			
			if (drillProperties != null){
				sl = new DrillableBar((Number)row.get(1), row.get(0) != null ? row.get(0).toString() : "null");
				//((DrillableSlice)sl).setUrl(link);
				setDrill(row.get(1).toString(), row.get(2).toString(), drillProperties, (OfcDrillable)sl);
			}
			else{
				sl = new Bar((Number)row.get(1), row.get(0) != null ? row.get(0).toString() : "null");
			}
			
			if (x_axis != null){
				Label l = new Label(row.get(0).toString());
				l.setRotation(rotation);
				x_axis.addLabels( l);
			}
			
			
			//generate a RandomColor
			if (count >= _colors.size()){
				generateRandomColor(_colors);
				
				
			}
			sl.setColour(_colors.get(count));
			count++;
			bars.add(sl);
		}
//		chart.setXAxis(x_axis);
		bar.addBars(bars);
		
		return bar;
	}
	
	protected Element generateBarHorizontalChart(List<List<Object>> values, Properties chartProperties, Properties drillProperties,  String[][] colors, HorizontalBarYAxis y_axis){
		HorizontalBarChart bar = new HorizontalBarChart();
		
		
//		bar.setKey_on_click("pie_slice_clicked");
		List<String> _colors = new ArrayList<String>();
		if (colors != null){
			
			
			for(int i = 0; i < colors[0].length; i++){
				_colors.add(colors[0][i]);
			}
			
		}
		
		int count = 0;
		List< HorizontalBarChart.Bar> bars = new ArrayList< HorizontalBarChart.Bar>();
		
		
		
		for(List<Object> row : values){

			 HorizontalBarChart.Bar sl = null;
			
			
			if (drillProperties != null){
				sl = new DrillableHorizontalBar((Number)row.get(1));
				//((DrillableSlice)sl).setUrl(link);
				setDrill(row.get(1).toString(), row.get(2).toString(), drillProperties, (OfcDrillable)sl);
			}
			else{
				sl = new  HorizontalBarChart.Bar((Number)row.get(1));
			}
			
			
			
			y_axis.setOffset(true);
			y_axis.addLabels( row.get(0).toString());
			//generate a RandomColor
			if (count >= _colors.size()){
				generateRandomColor(_colors);
				
				
			}
//			sl.setColour(_colors.get(count));
			count++;
			bars.add(sl);
		}
//		chart.setXAxis(x_axis);
		bar.addBars(bars);
		
		return bar;
	}

	protected Element generateLineChart(List<List<Object>> values, Properties chartProperties, Properties drillProperties,  String[][] colors, XAxis x_axis, Number[] min_max){
		LineChart pie = new LineChart();
		
	
		pie.setText(chartProperties.getProperty(subCaption, ""));
		
//		pie.setKey_on_click("pie_slice_clicked");
		List<String> _colors = new ArrayList<String>();
		if (colors != null){
			
			
			for(int i = 0; i < colors[0].length; i++){
				_colors.add(colors[0][i]);
			}
			
		}
		
		Rotation rotation = Rotation.HORIZONTAL;
		
		if (Integer.parseInt(chartProperties.getProperty(rotateLabels, "0")) == 1){
			if (Integer.parseInt(chartProperties.getProperty(slantLabels, "0")) == 1){
				rotation = Rotation.HALF_DIAGONAL;
			}
			else{
				rotation = Rotation.VERTICAL;
			}
		}
		int count = 0;
		for(List<Object> row : values){
			
			if (min_max[0] == null || ((Comparable)row.get(1)).compareTo(min_max[0]) < 0){
				min_max[0] = (Number)row.get(1);
			}
			
			if (min_max[1] == null || ((Comparable)row.get(1)).compareTo(min_max[1]) > 0){
				min_max[1] = (Number)row.get(1);
			}
			
			//generate a RandomColor
			if (count >= _colors.size()){
				generateRandomColor(_colors);
			}
			Dot sl =  null;
			if (drillProperties != null){
				sl = new DrillableDot((Number)row.get(1), row.get(0) != null ? row.get(0).toString() : "null");
				//((DrillableSlice)sl).setUrl(link);
				setDrill(row.get(1).toString(), row.get(2).toString(), drillProperties, (OfcDrillable)sl);
			}
			else{
				sl = new Dot((Number)row.get(1), colors[0][0]);
			}
			
			
			
			pie.addDots(sl);
			
			Label l = new Label(row.get(0).toString());
			l.setRotation(rotation);
			x_axis.addLabels( l);
			count++;
		}
		
		
		return pie;
	}
}
