package bpm.fd.runtime.engine.chart.jfree.generator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.runtime.engine.chart.jfree.generator.chart.MultiSeriesHelper;
import bpm.fd.runtime.engine.chart.jfree.generator.chart.MultiSeriesHelper.Measure;
import bpm.fd.runtime.engine.utils.ColorParser;


public class JFREEMultiSerieGenerator {

	protected String ROOT = "chart";
	protected String CATEGORIES = "categories";
	protected String CATEGORIE = "category";
	protected String CATEGORIE_LABEL = "label";
	protected String DATA_SET = "dataset";
	protected String DATA_SET_SERIESNAME = "seriesName";
	protected String DATA_SET_RENDER_AS = "renderAs";
	protected String DATA_SET_PARENTYAXIS = "parentYAxis";
	protected String DATA_SET_COLOR = "color";
	
	protected String DATA_SET_SET = "set";
	protected String DATA_SET_SET_VALUE = "value";
	
	protected String DRILL = "link";

		
	
		
	public  String createMultiSerieXml(String chartName, int width, int heigth, int nature, List<List<Object>> seriesvalues, List<List<Object>> values, Properties chartProperties, Properties drillProperties, String[][] colors, int valuesSeriesNumber, boolean containGroupingSerie, String[] measureNames, String lineSerieName, boolean[] splitMeasureOnSerie) throws Exception{
		
		List<String[]> categorieNames = MultiSeriesHelper.getCategories(seriesvalues, valuesSeriesNumber);
		Collections.sort(categorieNames, new Comparator<String[]>() {

			public int compare(String[] l1, String[] l2) {
				if (l1.length == 0 || l2.length == 0){
					return -1;
				}
				else if (l1[2] == null){
					return 1;
				}
				else if (l2[2] == null){
					return -1;
				}
				return l1[2].compareTo(l2[2]);
				
			}
		});
		
		int measureNameOffset = 0;
		for(boolean b : splitMeasureOnSerie){
			if (!b){
				measureNameOffset ++;
			}
		}
		List<Measure> measures = MultiSeriesHelper.getMeasures(seriesvalues, valuesSeriesNumber, measureNames, colors, containGroupingSerie, lineSerieName, measureNameOffset);
		
		Collections.sort(measures, new Comparator<Measure>() {

			public int compare(Measure l1, Measure l2) {
				
				return l1.label.compareTo(l2.label);
				
			}
		});
		
		//remove nonSplitedMeasure
		List<Measure> _toRemove= new ArrayList<Measure>();
		List<String> toAdd = new ArrayList<String>();
		
		
		
		for(int i = 0; i < splitMeasureOnSerie.length; i++){
			if (!splitMeasureOnSerie[i]){
				toAdd.add(measureNames[i]);
				for(Measure m : measures){
					if (m.name.startsWith(measureNames[i])){
						_toRemove.add(m);
					}
				}
			}
		}
		
		measures.removeAll(_toRemove);
		
		List<Measure> nonSplitedMeasures = new ArrayList<Measure>();
		for(String s : toAdd){
			Measure m = new Measure();
			m.name = s;
			m.isLineSerie = s.equals(lineSerieName);
			m.label = "";
			for(int i = 0; i < _toRemove.size(); i++){
				if (_toRemove.get(i).name.equals(s)){
					m.index = _toRemove.get(i).index;
				}
			}
			m.isSplitedSerie = false;
			int k = toAdd.indexOf(s);
			if (k < colors[0].length && k >= 0){
				m.color = colors[0][k];
			}
			nonSplitedMeasures.add(m);
		}
		
		
		HashMap<Measure, String[]> dataSets = new HashMap<Measure, String[]>();
		
		for(List<Object> row : seriesvalues){
			
			String currentCategorie = row.get(valuesSeriesNumber).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
			
			for (int i = 0; i < categorieNames.size(); i++){
				if(currentCategorie.equals(categorieNames.get(i)[0])){
					for(int mNum = 0; mNum < valuesSeriesNumber; mNum++){
						Measure m = null;
						if (containGroupingSerie){
							m = MultiSeriesHelper.getMeasure(measures, mNum, row.get(valuesSeriesNumber + 1).toString(),containGroupingSerie);
						}
						else{
							m = MultiSeriesHelper.getMeasure(nonSplitedMeasures, mNum, row.get(valuesSeriesNumber).toString(),containGroupingSerie);
						}
						
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
		
		
		
		
		//
		for(List<Object> row : values){
			
			String currentCategorie = row.get(valuesSeriesNumber).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"");
			
			for (int i = 0; i < categorieNames.size(); i++){
				if(currentCategorie.equals(categorieNames.get(i)[0])){
					for(int mNum = 0; mNum < valuesSeriesNumber; mNum++){
						Measure m = null;
						
						
						for(int k = 0; k < nonSplitedMeasures.size(); k++){
							if (nonSplitedMeasures.get(k).index == mNum){
								m = nonSplitedMeasures.get(k);
							}
						}
						
						if (m != null) {
							if (dataSets.get(m) == null){
								dataSets.put(m, new String[categorieNames.size()]);
							}
							
							dataSets.get(m)[i] = row.get(mNum ).toString();
						}
					}
				}
			}
		}
		
		/*
		 * properties
		 */
		
		measures.addAll(nonSplitedMeasures);
		
		
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		DefaultCategoryDataset lineDataSet = new DefaultCategoryDataset();
		
		for(Measure m : measures){
			if (!m.isLineSerie){
				for (int i = 0; i < categorieNames.size(); i++){
					dataset.addValue(Double.parseDouble(dataSets.get(m)[i]), m.label, categorieNames.get(i)[0]);				
				}
			}
			else{
				for (int i = 0; i < categorieNames.size(); i++){
					lineDataSet.addValue(Double.parseDouble(dataSets.get(m)[i]), m.label, categorieNames.get(i)[0]);				
				}
			}
		}
		
		boolean isLineDual = false;
		
		/*
		 * create Chart
		 */
		JFreeChart chart = null;
		switch(nature){
		case ChartNature.COLUMN_3D_MULTI:
			chart = ChartFactory.createBarChart3D(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			break;
		case ChartNature.COLUMN_MULTI:
			chart = ChartFactory.createBarChart(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			break;
		case ChartNature.COLUMN_3D_LINE:
			chart = ChartFactory.createBarChart3D(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			break;
		
		
		case ChartNature.COLUMN_LINE_DUAL:	
			chart = ChartFactory.createBarChart(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			isLineDual = true;
			break;
		case ChartNature.COLUMN_3D_LINE_DUAL:
			chart = ChartFactory.createBarChart3D(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			isLineDual = true;
			break;
			
		case ChartNature.LINE_MULTI:
			chart = ChartFactory.createLineChart(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			break;
		case ChartNature.STACKED_BAR:
			chart = ChartFactory.createStackedBarChart(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.HORIZONTAL, true, true, true);
			break;
		case ChartNature.STACKED_BAR_3D:
			chart = ChartFactory.createStackedBarChart3D(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.HORIZONTAL, true, true, true);
			break;
		case ChartNature.STACKED_COLUMN_3D:
			chart = ChartFactory.createStackedBarChart3D(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset,PlotOrientation.VERTICAL, true, true, true);
			break;
		case ChartNature.STACKED_COLUMN:
			chart = ChartFactory.createStackedBarChart(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			break;
		case ChartNature.STACKED_COLUMN_3D_LINE_DUAL:
			chart = ChartFactory.createStackedBarChart3D(chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "", "", "", (CategoryDataset)dataset, PlotOrientation.VERTICAL, true, true, true);
			isLineDual = true;
			break;
		}
		
		
		if (chartProperties.getProperty("PYAxisName") != null){
			((CategoryPlot)chart.getPlot()).getRangeAxis().setLabel(chartProperties.getProperty("PYAxisName"));
		}
		if (lineDataSet != null){
			CategoryPlot plot = (CategoryPlot)chart.getPlot();
			plot.setDataset(0, lineDataSet);
			plot.setDataset(1, dataset);
			
			CategoryItemRenderer renderer = new LineAndShapeRenderer(true, true);
			
			for(Measure m : measures){
				if (m.isLineSerie){
					renderer.setSeriesPaint(0, ColorParser.convert(m.color));
					break;
				}
			}
			
			
			
			CategoryItemRenderer _r = plot.getRenderer(0);
			plot.setRenderer(0, renderer);
			plot.setRenderer(1, _r);
			
			if (isLineDual){
				NumberAxis axis2 = new NumberAxis(chartProperties.getProperty("SYAxisName") != null ? chartProperties.getProperty("SYAxisName") : "");
				axis2.setAutoRangeIncludesZero(false);
				axis2.setLabelPaint(Color.black);
		        axis2.setTickLabelPaint(Color.black);
		        ValueAxis ax1 = plot.getRangeAxis(0);
		      
		        plot.setRangeAxis(0, axis2);
		        plot.setRangeAxis(1, ax1);
		        plot.setRangeAxisLocation(0, AxisLocation.BOTTOM_OR_RIGHT);
				plot.mapDatasetToRangeAxis(1,1);
				
			}

		}
		
		
		StringBuffer sb = new StringBuffer();
		
		boolean useMap = needDrill(nature, drillProperties, chart.getPlot());
		createColorRenderers(chart.getPlot(), colors);
		ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
		
		generateProperties(chart, chartProperties);
		BufferedImage img = chart.createBufferedImage(width, heigth, info);
		Base64 base64  = new Base64();
		byte[] encoded = base64.encode(ChartUtilities.encodeAsPNG(img));
		if (useMap){
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(os);
			
			ChartUtilities.writeImageMap(pw,chartName, info, false);
			pw.close();
			sb.append(os.toString());
		}
	  
	    
	    sb.append("<img src=\"");
	    sb.append("data:image/png;base64,");
	    String stringEncoded = new String(encoded);
	    sb.append(stringEncoded);	
	    sb.append("\"  USEMAP=\"#"+ chartName + "\" ></img>\n");
	   
	   return sb.toString();

	}
	
	private boolean needDrill(int nature, Properties drillProperties, Plot plot){
		if (drillProperties == null){
			return false;
		}
		
		String url = drillProperties.getProperty("url");
		String pName = drillProperties.getProperty("pName");
		String categorieAsValue = drillProperties.getProperty("categoryAsValue");
		boolean useValue = categorieAsValue != null ?  !Boolean.parseBoolean(categorieAsValue) : false;
		
		if (url == null && pName == null){
			return false;
		}
		
		String link = "" + url;
		
		if (url.contains(pName + "=")){
			int pInd = url.indexOf(pName + "=");
			link = url.substring(0, pInd);
			
			int end = url.indexOf("&", pInd);
			if (end == -1){
				
			}
			else{
				link += url.substring(end + 1);
			}
			
		}
		
		
			((CategoryPlot)plot).getRenderer().setBaseItemURLGenerator(new FdCategoryUrlGenerator(link, pName, useValue));
			
			
			return true;
	}
	
	private void generateProperties(JFreeChart chart, Properties props){
		if (props.getProperty("bgAlpha") != null ){
			chart.getPlot().setBackgroundAlpha(Float.parseFloat(props.getProperty("bgAlpha")) / 100.0f);
		}
		if (props.getProperty("subCaption") != null ){
			
		}
		if (props.getProperty("bgColor") != null ){
			chart.getPlot().setBackgroundPaint(ColorParser.convert(props.getProperty("bgColor")));
		}
		if (props.getProperty("borderColor") != null ){
			chart.getPlot().setOutlinePaint(ColorParser.convert(props.getProperty("borderColor")));
		}
		
		CategoryItemRenderer renderer = ((CategoryPlot)chart.getPlot()).getRenderer();
		
		if ((props.getProperty("showLabel") != null)){
			renderer.setItemLabelsVisible(Integer.parseInt(props.getProperty("showLabel")) == 1 ? true : false);
		}
		
		
		
		/*
		 * slant and rotate
		 */

		if (props.getProperty("slantLabels") != null && Integer.parseInt(props.getProperty("slantLabels")) == 1){
			((CategoryPlot)chart.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		}
		else if (props.getProperty("rotateLabels") != null &&Integer.parseInt(props.getProperty("rotateLabels")) == 1){
			((CategoryPlot)chart.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		}
		
		
	}
	
	private void createColorRenderers(Plot plot, String[][] colors){
		if (colors != null){
			
			for(int i = 0; i < colors[0].length; i++){
				((PiePlot)plot).setSectionPaint(i, ColorParser.convert(colors[0][i]));
				
			}
		}
	}
}
