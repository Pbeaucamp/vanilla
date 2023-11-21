package bpm.fd.runtime.engine.chart.ofc.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import jofc2.model.Chart;
import jofc2.model.axis.Axis;
import jofc2.model.axis.Label;
import jofc2.model.axis.XAxis;
import jofc2.model.axis.YAxis;
import jofc2.model.axis.Label.Rotation;
import jofc2.model.elements.BarChart;
import jofc2.model.elements.Element;
import jofc2.model.elements.Legend;
import jofc2.model.elements.LineChart;
import jofc2.model.elements.RadarChart;
import jofc2.model.elements.BarChart.Bar;
import jofc2.model.elements.LineChart.Dot;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.runtime.engine.chart.jfree.generator.chart.MultiSeriesHelper;
import bpm.fd.runtime.engine.chart.jfree.generator.chart.MultiSeriesHelper.Measure;

public class MultiOpenFlashChartJsonGenerator extends OpenFlashChartJsonGenerator{
	
	
	private  Chart createChart(List<List<Object>> values, Properties chartProperties, int valuesSeriesNumber, String[] measureNames, String[][] colorSeries, String lineSerieName, Properties drillProperties) throws Exception{
		
		List<String[]> categorieNames = MultiSeriesHelper.getCategories(values, valuesSeriesNumber);
		Collections.sort(categorieNames, new Comparator<String[]>() {

			public int compare(String[] l1, String[] l2) {
				if (l1.length == 0 || l2.length == 0){
					return -1;
				}
				else if (l1[0] == null){
					return 1;
				}
				else if (l2[0] == null){
					return -1;
				}
				return l1[0].compareTo(l2[0]);
				
			}
		});
		
		boolean[] _splits = new boolean[valuesSeriesNumber];
		for(int i = 0; i < valuesSeriesNumber; i++){
			_splits[i] = false;
		}
		
		List<Measure> measures =  MultiSeriesHelper.getMeasures(values, valuesSeriesNumber, measureNames, colorSeries, false, lineSerieName, null);
		
		
		
		HashMap<Measure, String[]> dataSets = new HashMap<Measure, String[]>();
		
		for(List<Object> row : values){
			
			String currentCategorie = row.get(valuesSeriesNumber).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
			
			for (int i = 0; i < categorieNames.size(); i++){
				if(currentCategorie.equals(categorieNames.get(i)[0])){
					for(int mNum = 0; mNum < valuesSeriesNumber; mNum++){
						Measure m = null;
						
						
						m = MultiSeriesHelper.getMeasure(measures, mNum, row.get(valuesSeriesNumber).toString(),false);
						
						if (m != null) {
							if (dataSets.get(m) == null){
								dataSets.put(m, new String[categorieNames.size()]);
							}
							
							dataSets.get(m)[i] = row.get(mNum ).toString();
						}
						else{
							System.out.println();
						}
						
											
					}
					
					
				}
			}
		}
		
		Chart chart = new Chart();
		
		
		
		/*
		 * build XAXIS
		 */
		Rotation rotation = Rotation.HORIZONTAL;
		
		if (Integer.parseInt(chartProperties.getProperty(rotateLabels, "0")) == 1){
			if (Integer.parseInt(chartProperties.getProperty(slantLabels, "0")) == 1){
				rotation = Rotation.HALF_DIAGONAL;
			}
			else{
				rotation = Rotation.VERTICAL;
			}
		}
		XAxis xaxis = new XAxis();
		chart.setXAxis(xaxis);
		
		for(String[] s : categorieNames){
			Label l = new Label(s[1].toString());
			l.setRotation(rotation);
			xaxis.addLabels( l);
		}
		
		Integer chartNature = Integer.parseInt(chartProperties.getProperty("chartNature"));		
		Number[] min_max = new Number[2];
		chart.setLegend(new Legend());

		for(Measure m : measures){
			
			Element serieChart = null;
			int categorieNumber = 0;
			switch(chartNature){
			case ChartNature.LINE_MULTI:
				
				serieChart = new LineChart();
				((LineChart)serieChart).setText(m.name);
				((LineChart)serieChart).setColour(m.color);
				//values
				categorieNumber = 0;
				if (dataSets.get(m) == null){
					continue;
				}
				for(String s : dataSets.get(m)){
					Dot sl = null;
					
					Double value = Double.parseDouble(s);
					if (min_max[0] == null || ((Comparable)value).compareTo(min_max[0]) < 0){
						min_max[0] = value;
					}
					
					if (min_max[1] == null || ((Comparable)value).compareTo(min_max[1]) > 0){
						min_max[1] = value;
					}
					
					if (drillProperties != null){
						sl = new DrillableDot((Number)value, m.name);
						setDrill(value.toString(), categorieNames.get(categorieNumber)[0], drillProperties, (OfcDrillable)sl);
					}
					else{
						sl = new Dot((Number)value, m.name);
					}
			
					sl.setColour(m.color);
					((LineChart)serieChart).addDots(sl);
					categorieNumber++;
				}

				break;
			case ChartNature.COLUMN_3D_LINE:
				if (m.isLineSerie){
					serieChart = new LineChart();
					((LineChart)serieChart).setText(m.name);
					((LineChart)serieChart).setColour(m.color);
					//values
					 categorieNumber = 0;
					if (dataSets.get(m) == null){
						continue;
					}
					for(String s : dataSets.get(m)){
						Dot sl = null;
						
						Double value = Double.parseDouble(s);
						if (min_max[0] == null || ((Comparable)value).compareTo(min_max[0]) < 0){
							min_max[0] = value;
						}
						
						if (min_max[1] == null || ((Comparable)value).compareTo(min_max[1]) > 0){
							min_max[1] = value;
						}
						
						if (drillProperties != null){
							sl = new DrillableDot((Number)value, m.name);
							setDrill(value.toString(), categorieNames.get(categorieNumber)[0], drillProperties, (OfcDrillable)sl);
						}
						else{
							sl = new Dot((Number)value, m.name);
						}
				
						sl.setColour(m.color);
						((LineChart)serieChart).addDots(sl);
						categorieNumber++;
					}
					break;
				}
				
			case ChartNature.COLUMN_MULTI:
				
				
				serieChart = new BarChart();
				((BarChart)serieChart).setText(m.name);
				((BarChart)serieChart).setColour(m.color);
				//values
				categorieNumber = 0;
				if (dataSets.get(m) == null){
					continue;
				}
				for(String s : dataSets.get(m)){
					Bar sl = null;
					
					Double value = Double.parseDouble(s);
					if (min_max[0] == null || ((Comparable)value).compareTo(min_max[0]) < 0){
						min_max[0] = value;
					}
					
					if (min_max[1] == null || ((Comparable)value).compareTo(min_max[1]) > 0){
						min_max[1] = value;
					}
					
					//TODO : drill
					if (drillProperties != null){
						sl = new DrillableBar((Number)value, m.name);
						//((DrillableSlice)sl).setUrl(link);
						setDrill(value.toString(), categorieNames.get(categorieNumber)[0], drillProperties, (OfcDrillable)sl);
					}
					else{
						sl = new Bar((Number)value, m.name);
					}
//					sl = new Bar((Number)value, m.name);
					sl.setTooltip(m.name + " #val#");
					sl.setColour(m.color);
					((BarChart)serieChart).addBars(sl);
					categorieNumber++;
				}
				
				
				
				break;
				
			case ChartNature.RADAR:
				serieChart = new RadarChart();
				((RadarChart)serieChart).setText(m.name);
				((RadarChart)serieChart).setColour("#" + m.color);
				((RadarChart)serieChart).setFillColor("#FF2512");
				//values
				 categorieNumber = 0;
				if (dataSets.get(m) == null){
					continue;
				}
				for(String s : dataSets.get(m)){
					Dot sl = null;
					
					Double value = Double.parseDouble(s);
					if (min_max[0] == null || ((Comparable)value).compareTo(min_max[0]) < 0){
						min_max[0] = value;
					}
					
					if (min_max[1] == null || ((Comparable)value).compareTo(min_max[1]) > 0){
						min_max[1] = value;
					}
					
					if (drillProperties != null){
						sl = new DrillableDot((Number)value, null);
						setDrill(value.toString(), categorieNames.get(categorieNumber)[0], drillProperties, (OfcDrillable)sl);
					}
					else{
						sl = new Dot((Number)value);
					}
			
//					sl.setColour(m.color);
					((RadarChart)serieChart).addDots(sl);
					categorieNumber++;
				}
				break;
			}
			if (serieChart != null){
				chart.addElements(serieChart);
			}
			
		}
		YAxis yaxis = new YAxis();
		if (min_max[0] != null){
			if (min_max[0] != null && min_max[0].toString().contains(".")){
				yaxis.setMin(Double.valueOf(min_max[0].toString())- Double.valueOf(min_max[1].toString()) * 0.25);
			}
			else{
				yaxis.setMin(Integer.valueOf(min_max[0].toString())- Double.valueOf(min_max[1].toString()) * 0.25);
			}
		}
		
		
		if (min_max[1]!= null){
			if (min_max[1].toString().contains(".")){
				yaxis.setMax(Double.valueOf(min_max[1].toString()) + Double.valueOf(min_max[1].toString()) * 0.25);
			}
			else{
				yaxis.setMax(Integer.valueOf(min_max[1].toString()) + Double.valueOf(min_max[1].toString()) * 0.25);
			}
		}
		
		
		if (chartNature == ChartNature.RADAR){
			chart.setRadarAxis(yaxis);
			yaxis.setMin(0);
			chart.setXAxis(null);
			chart.setYAxis(null);
			chart.setLegend(null);
//			chart.setRadarAxis(null);
		}
		else{
			chart.setYAxis(yaxis);
		}
		
		
		
		
		
		
		return chart;
	}

	public  String createJson(List<List<Object>> values, Properties chartProperties, int valuesSeriesNumber, String[] measureNames, String[][] colorSeries, String lineSerieName, Properties drillProperties) throws Exception{
		Chart chart = createChart(values, chartProperties, valuesSeriesNumber, measureNames, colorSeries, lineSerieName, drillProperties);
		return chart.toString();
		
	}
	public String createJson(List<List<Object>> _values, Properties chartProperties, int valuesSeriesNumber, String[] measureNames, String[][] colorSeries, String lineSerieName, Properties drillProperties, List<List<Object>> seriesvalues, int measureNumber, int measureNameOffset) throws Exception{
		Chart chart = createChart(_values, chartProperties, valuesSeriesNumber, measureNames, colorSeries, lineSerieName, drillProperties);
		
		
		
		
		List<String> categories = new ArrayList<String>();
		if (chart.getXAxis() != null && chart.getXAxis().getLabels() != null && chart.getXAxis().getLabels().getLabels() != null){
			for(Object l : chart.getXAxis().getLabels().getLabels()){
				categories.add(((Label)l).getText());
			}
		}
		else{
			chart.setXAxis(new XAxis());
		}
		
		
		
		/*
		 * we check if there are some others categories
		 */
		List<String[]> categorieNames = MultiSeriesHelper.getCategories(seriesvalues, measureNumber);
		Collections.sort(categorieNames, new Comparator<String[]>() {

			public int compare(String[] l1, String[] l2) {
				if (l1.length == 0 || l2.length == 0){
					return -1;
				}
				else if (l1[0] == null){
					return 1;
				}
				else if (l2[0] == null){
					return -1;
				}
				return l1[0].compareTo(l2[0]);
				
			}
		});
		
		
		for(String[] s : categorieNames){
			boolean found = false;
			for(String e : categories){
				if (e.equals(s[1])){
					found = true;
					break;
				}
			}
			if (!found){
				chart.getXAxis().addLabels(s[1]);
				categories.add(s[1]);
			}
			
		}
		List<Measure> measures = MultiSeriesHelper.getMeasures(seriesvalues, measureNumber, measureNames, colorSeries, true, lineSerieName, measureNameOffset);
		HashMap<Measure, String[]> dataSets = new HashMap<Measure, String[]>();
		for(List<Object> row : seriesvalues){
			String currentCategorie = row.get(measureNumber).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
			int catPos = -1;
			for(int i = 0; i < categories.size(); i++){
				if (categories.get(i).equals(currentCategorie)){
					catPos = i;
					break;
				}
			}
			
			if (catPos == -1){
				for(int i = 0; i < categorieNames.size(); i++){
					if (categorieNames.get(i)[0].equals(currentCategorie)){
						catPos = i;
						break;
					}
				}
			}
						
			Measure m = null;
			for(Measure _m : measures){
				if (_m.label.equals(row.get(measureNumber + 1))){
					m = _m;
					break;
				}
			}
			if (dataSets.get(m) == null){
				dataSets.put(m, new String[categories.size()]);
			}
			dataSets.get(m)[catPos] = row.get(m.index).toString();
		}
		
		Number[] min_max = new Number[2];
		Integer chartNature = Integer.parseInt(chartProperties.getProperty("chartNature"));		
		for(Measure m : measures){
			
			Element serieChart = null;
			int categorieNumber = 0;
			switch(chartNature){
			case ChartNature.COLUMN_3D_LINE:
			case ChartNature.LINE_MULTI:
				if ((m.isLineSerie && ChartNature.COLUMN_3D_LINE == chartNature || chartNature == ChartNature.LINE_MULTI)){
					serieChart = new LineChart();
					((LineChart)serieChart).setText(m.name);
					((LineChart)serieChart).setColour(m.color);
					//values
					categorieNumber = 0;
					if (dataSets.get(m) == null){
						continue;
					}
					for(String s : dataSets.get(m)){
						Dot sl = null;
						
						Double value = Double.parseDouble(s);
						if (min_max[0] == null || ((Comparable)value).compareTo(min_max[0]) < 0){
							min_max[0] = value;
						}
						
						if (min_max[1] == null || ((Comparable)value).compareTo(min_max[1]) > 0){
							min_max[1] = value;
						}
						
						if (drillProperties != null){
							sl = new DrillableDot((Number)value, m.name);
							setDrill(value.toString(), categorieNames.get(categorieNumber)[0], drillProperties, (OfcDrillable)sl);
						}
						else{
							sl = new Dot((Number)value, m.name);
						}
				
						sl.setColour(m.color);
						((LineChart)serieChart).addDots(sl);
						categorieNumber++;
					}
					break;
				}
				
			case ChartNature.RADAR:
				serieChart = new RadarChart();
				((RadarChart)serieChart).setText(m.name);
				//((RadarChart)serieChart).setColour(m.color);
				//values
				 categorieNumber = 0;
				if (dataSets.get(m) == null){
					continue;
				}
				for(String s : dataSets.get(m)){
					Dot sl = null;
					
					Double value = Double.parseDouble(s);
					if (min_max[0] == null || ((Comparable)value).compareTo(min_max[0]) < 0){
						min_max[0] = value;
					}
					
					if (min_max[1] == null || ((Comparable)value).compareTo(min_max[1]) > 0){
						min_max[1] = value;
					}
					
					if (drillProperties != null){
						sl = new DrillableDot((Number)value, null);
						setDrill(value.toString(), categorieNames.get(categorieNumber)[0], drillProperties, (OfcDrillable)sl);
					}
					else{
						sl = new Dot((Number)value);
					}
			
					
					((RadarChart)serieChart).addDots(sl);
					categorieNumber++;
				}
				break;	
				
			default:
				serieChart = new BarChart();
				((BarChart)serieChart).setText(m.getName());
				((BarChart)serieChart).setColour(m.color);
				//values
				categorieNumber = 0;
				if (dataSets.get(m) == null){
					continue;
				}
				for(String s : dataSets.get(m)){
					Bar sl = null;
					
					Double value = s == null ? null : Double.parseDouble(s);
					if (value != null){
						if (min_max[0] == null || ((Comparable)value).compareTo(min_max[0]) < 0){
							min_max[0] = value;
						}
						
						if (min_max[1] == null || ((Comparable)value).compareTo(min_max[1]) > 0){
							min_max[1] = value;
						}
					}
					
					
					//TODO : drill
					if (drillProperties != null){
						sl = new DrillableBar((Number)value, m.name);
						//((DrillableSlice)sl).setUrl(link);
						if (value != null){
							setDrill(value.toString(), categorieNames.get(categorieNumber)[0], drillProperties, (OfcDrillable)sl);
						}
						
					}
					else{
						sl = new Bar((Number)value, m.name);
					}
//					sl = new Bar((Number)value, m.name);
					sl.setTooltip(m.getName() + " #val#");
					
					((BarChart)serieChart).addBars(sl);
					categorieNumber++;
				}
				
				
				
				break;
			}
			
			chart.addElements(serieChart);
		}
		
		Axis yAxis = chart.getYAxis();
		
		if (chartNature == ChartNature.RADAR){
			yAxis = chart.getRadarAxis();
		}
		
		if (yAxis != null){
			if (yAxis.getMin() == null && min_max[0] != null){
				yAxis.setMin(Double.parseDouble(min_max[0].toString()));
			}
			else if (min_max[0] != null && yAxis.getMin().compareTo(Double.parseDouble(min_max[0].toString()))> 0){
				yAxis.setMin(Double.parseDouble(min_max[0].toString()));
			}
			if (yAxis.getMax() == null && min_max[1] != null){
				yAxis.setMax(Double.parseDouble(min_max[1].toString()));
			}
			else if (min_max[1] != null && yAxis.getMax().compareTo(Double.parseDouble(min_max[1].toString()))< 0){
				yAxis.setMax(Double.parseDouble(min_max[1].toString()));
			}
		}
		else{
			yAxis = new YAxis();
			
			yAxis.setMin(Double.parseDouble(min_max[0].toString()));
			yAxis.setMax(Double.parseDouble(min_max[1].toString()));
			
			if (chartNature == ChartNature.RADAR){
				chart.setRadarAxis(yAxis);
				chart.setXAxis(null);
			}
			else{
				chart.setYAxis(yAxis);
			}
		}
		
		return chart.toString();
		
	}

	
}
