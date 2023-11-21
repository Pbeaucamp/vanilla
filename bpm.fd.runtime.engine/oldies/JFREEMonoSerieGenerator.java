package bpm.fd.runtime.engine.chart.jfree.generator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.runtime.engine.utils.ColorParser;

public class JFREEMonoSerieGenerator {
	
	/**
	 * 
	 * @param chartName
	 * @param width
	 * @param heigth
	 * @param nature
	 * @param values
	 * @param chartProperties
	 * @param drillProperties
	 * @param colors
	 * @return
	 * @throws Exception
	 */
	public String createMonoSerieXml(String chartName, int width, int heigth, int nature, List<List<Object>> values, Properties chartProperties, Properties drillProperties, String[][] colors) throws Exception{
		   
		   
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
						
					}return l1.get(3).toString().compareTo(l2.get(3).toString());
					
				}
			});
			
			
			Dataset dataSet = getDataSet(nature, values);
			
			JFreeChart chart = getChart(nature, dataSet, 
					chartProperties.getProperty("caption") != null ? chartProperties.getProperty("caption") : "",
							"", "");
			
			
						
			if (chartProperties.getProperty("bgAlpha") != null ){
				chart.getPlot().setBackgroundAlpha(Float.parseFloat(chartProperties.getProperty("bgAlpha")) / 100.0f);
			}
			if (chartProperties.getProperty("subCaption") != null ){
				
			}
			if (chartProperties.getProperty("bgColor") != null ){
				chart.getPlot().setBackgroundPaint(ColorParser.convert(chartProperties.getProperty("bgColor")));
			}
			if (chartProperties.getProperty("borderColor") != null ){
				chart.getPlot().setOutlinePaint(ColorParser.convert(chartProperties.getProperty("borderColor")));
			}
			
			ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			
			StringBuffer sb = new StringBuffer();
			
			boolean useMap = needDrill(nature, drillProperties, chart.getPlot());
			createColorRenderers(chart.getPlot(), nature, colors);
			
			
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
	
	
	private void createColorRenderers(Plot plot, int nature, String[][] colors){
		switch(nature){
		case ChartNature.PIE:
		case ChartNature.PIE_3D:
			if (colors != null){
				
				for(int i = 0; i < colors[0].length; i++){
					((PiePlot)plot).setSectionPaint(i, ColorParser.convert(colors[0][i]));
					
				}
			}
			
			return;
		case ChartNature.BAR:
		case ChartNature.BAR_3D:
		case ChartNature.COLUMN:
		case ChartNature.COLUMN_3D:
			
			if (colors != null){
				
				for(int i = 0; i < colors[0].length; i++){
					((CategoryPlot)plot).getRenderer().setSeriesPaint(i, ColorParser.convert(colors[0][i]));
				}
			}
		}
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
		
		switch(nature){
		case ChartNature.PIE:
		case ChartNature.PIE_3D:
			((PiePlot)plot).setURLGenerator(new StandardPieURLGenerator(link, pName, null){
				@Override
				public String generateURL(PieDataset dataset, Comparable key, int pieIndex) {
					return super.generateURL(dataset, key, pieIndex);
				}
			});
			return true;
		
		case ChartNature.BAR:
		case ChartNature.BAR_3D:
		case ChartNature.COLUMN:
		case ChartNature.COLUMN_3D:
			((CategoryPlot)plot).getRenderer().setBaseItemURLGenerator(new FdCategoryUrlGenerator(link, pName, useValue));
			return true;
		}
		return false;
	}



	private JFreeChart getChart(int nature, Dataset dataSet, String chartTitle, String categoryAxisLabel, String valueAxisLabel) throws Exception{
		
		switch(nature){
		case ChartNature.PIE:
			return ChartFactory.createPieChart(chartTitle, (PieDataset)dataSet, true, 	true,  true);
		case ChartNature.PIE_3D:
			return ChartFactory.createPieChart3D(chartTitle, (PieDataset)dataSet, true, 	true,  true);
		case ChartNature.COLUMN:
			return 	ChartFactory.createBarChart(chartTitle, categoryAxisLabel, valueAxisLabel, (CategoryDataset)dataSet, PlotOrientation.VERTICAL, true, true, true);
		case ChartNature.COLUMN_3D:
			return 	ChartFactory.createBarChart3D(chartTitle, categoryAxisLabel, valueAxisLabel, (CategoryDataset)dataSet, PlotOrientation.VERTICAL, true, true, true);
		case ChartNature.LINE:
			return 	ChartFactory.createLineChart(chartTitle, categoryAxisLabel, valueAxisLabel, (CategoryDataset)dataSet, PlotOrientation.VERTICAL, true, true, true);
		case ChartNature.BAR:
			return 	ChartFactory.createBarChart(chartTitle, categoryAxisLabel, valueAxisLabel, (CategoryDataset)dataSet,PlotOrientation.HORIZONTAL, true, true, true);
		case ChartNature.BAR_3D:
			return 	ChartFactory.createBarChart3D(chartTitle, categoryAxisLabel, valueAxisLabel, (CategoryDataset)dataSet, PlotOrientation.HORIZONTAL, true, true, true);
		}
		
		throw new Exception("Nature not supported for the chart Renderer");
	}


	private Dataset getDataSet(int nature, List<List<Object>> values) throws Exception{
		switch(nature){
		case ChartNature.PIE:
		case ChartNature.PIE_3D:
			DefaultPieDataset pieDataset = new DefaultPieDataset();
			for(List<Object> row : values){
				try{
					pieDataset.setValue(row.get(2).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\""), 
							Double.parseDouble(row.get(1).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"")));
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			return pieDataset;
			
		case ChartNature.COLUMN:
		case ChartNature.COLUMN_3D:
		case ChartNature.BAR:
		case ChartNature.BAR_3D:
		
			
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			for(List<Object> row : values){
				try{
					dataset.setValue(
							Double.parseDouble(row.get(1).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"")),
							row.get(2).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\""), 
							"ss");
							
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			return dataset;
		case ChartNature.LINE:
			
			DefaultCategoryDataset  serie = new DefaultCategoryDataset ();
			
			for(List<Object> row : values){
				try{
					
					serie.setValue(
							Double.parseDouble(row.get(1).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"")),
					"z"		,
					row.get(2).toString().replace("<", "&lt;").replace(">", "&gt;").replace("'", "\"")
//							
							);
							
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			
			return serie;
		}

		throw new Exception("Nature not supported for the chart Renderer");
		
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
		
		
		if (chart.getPlot() instanceof CategoryPlot){
			CategoryItemRenderer renderer = ((CategoryPlot)chart.getPlot()).getRenderer();
			
			renderer.setItemLabelsVisible(Integer.parseInt(props.getProperty("showLabel")) == 1 ? true : false);
			
			
			/*
			 * slant and rotate
			 */
			if (Integer.parseInt(props.getProperty("slantLabels")) == 1){
				((CategoryPlot)chart.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			}
			else if (Integer.parseInt(props.getProperty("showLabel")) == 1){
				((CategoryPlot)chart.getPlot()).getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
			}
		}
		
		
		
	}
}