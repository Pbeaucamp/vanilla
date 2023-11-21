package bpm.fd.runtime.engine.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.ComponentStyle;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDataFilter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.IComponentRenderer;
import bpm.fd.api.core.model.components.definition.IDimensionableDatas;
import bpm.fd.api.core.model.components.definition.OutputParameter;
import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillReportOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.LineCombinationOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.ChartDrillInfo.TypeTarget;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.filter.MenuOptions;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.datas.JSPDataGenerator;
import bpm.vanilla.platform.core.VanillaConstants;

public class ChartGenerator {

	public static String generateJspBlock(int offset, ComponentChartDefinition chart, String outputParameterName, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil profil)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		/*
		 * check the filter controll a DrillDrivenStackableCell
		 */
		
		List<String> drivenCellsId = new ArrayList<String>();
		
		for(ComponentConfig c : configs){
			if (c instanceof DrillDrivenComponentConfig){
				
				if (((DrillDrivenComponentConfig)c).getController() == chart){
					drivenCellsId.add(((DrillDrivenComponentConfig)c).getDrillDrivenCell().getId());
				}
				
			}
		}
		
		
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		return generateFusionChart(chart,
				buf.toString(),
				chart.getDatas().getDataSet().getId() + "Query",
				(IChartData)chart.getDatas(), 
				
				outputParameterName, pOuts, configs, profil,
				drivenCellsId);
		
//		if (chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART) || 
//			chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART_FREE) 	){
//			
//		}
//		else if (chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.JFREE_CHART)){
//			return generateJFreeChart(chart,
//					buf.toString(),
//					chart.getDatas().getDataSet().getId() + "Query",
//					(IChartData)chart.getDatas(), 
//					
//					outputParameterName, pOuts, configs, profil);
//		}
//		else if (chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.OPEN_FLASH_CHART)){
//			return generateOpenFlashChart(chart,
//					buf.toString(),
//					chart.getDatas().getDataSet().getId() + "Query",
//					(IChartData)chart.getDatas(), 
//					
//					outputParameterName, pOuts, configs, profil);
//		}
//		return buf.toString();
	}
	
		
	private static String generateFusionChart(final ComponentChartDefinition chart, String spacing, String query, IChartData data, String outputParameterName, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil profil, List<String> drivenCellsId){
		
		String resultSet = chart.getId() + "ResultSet";
		
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     String " + chart.getId() + "Xml = null;\n");
		if (data instanceof IDimensionableDatas){
			buf.append(spacing + "     " + resultSet + " = null;\n");
		}
		else{
			buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		}
		
		buf.append(spacing + "     List<List<Object>> " + chart.getId() + "ValuesSeries = null;\n");
		buf.append(spacing + "     List<List<Object>> " + chart.getId() + "Values = null;\n");
		buf.append(spacing + "     try{\n");
		
		
		
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, chart));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		
		String filters = generateDataFilterString((IChartData)chart.getDatas());
		if (chart.getDatas() instanceof MultiSerieChartData){
			MultiSerieChartData d = (MultiSerieChartData)chart.getDatas();
			
			buf.append(spacing + "        List<java.awt.Point> splitedpt = new java.util.ArrayList<java.awt.Point>();\n");
			buf.append(spacing + "        List<java.awt.Point> pt = new java.util.ArrayList<java.awt.Point>();\n");
			
//			buf.append(spacing + "        List<java.awt.Point> pt = new java.util.ArrayList<java.awt.Point>();\n");
			
			for(DataAggregation a : d.getAggregation()){
				if (a.isApplyOnDistinctSerie()){
					buf.append(spacing + "        splitedpt.add(new java.awt.Point(" + (a.getValueFieldIndex() + 1) + ", "+ a.getAggregator()+ "));\n");
					
				}
				else{
					buf.append(spacing + "        pt.add(new java.awt.Point(" + (a.getValueFieldIndex() + 1) + ", "+ a.getAggregator()+ "));\n");
				}
//				buf.append(spacing + "        pt.add(new java.awt.Point(" + (a.getValueFieldIndex() + 1) + ", "+ a.getAggregator()+ "));\n");
			}
			buf.append(spacing + "        " + chart.getId() + "ValuesSeries = Aggregationer.performAggregation(" +resultSet + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex() + ", " + d.getSerieFieldIndex() +", " + data.getOrderFieldIndex() +  ", splitedpt, new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() + "}," + data.getCategorieFieldLabelIndex() +", \""+ filters +  "\");\n");
			buf.append(spacing + "        " + chart.getId() + "Values = Aggregationer.performAggregation(" +query + ".executeQuery()" + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex()+ ", null, " + data.getOrderFieldIndex() + ", pt, new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() +"}," + data.getCategorieFieldLabelIndex() + ",\"" + filters + "\");\n");
		}else{
			ChartData d = (ChartData)chart.getDatas();
			buf.append(spacing + "        " + chart.getId() + "ValuesSeries = Aggregationer.performAggregation(" +resultSet + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex() + ", " + d.getValueFieldIndex() + ", " + d.getOrderFieldIndex() + ", " + d.getAggregator() + ", new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() +  "},\"" + filters + "\");\n");
		}
		
		
		buf.append(spacing + "        // create chartProperties\n");
		buf.append(generateOptions( spacing + "        ", chart.getOptions(), chart.getName(), chart.getId(), null));
		
		/*
		 * styleProperties
		 */
		buf.append(chart.getId() + "ChartP.setProperty(\"_style_bold\",\"" + (chart.getValueFormat().isBold() ? "1" : "0") + "\");\n");
		buf.append(chart.getId() + "ChartP.setProperty(\"_style_italic\",\"" + (chart.getValueFormat().isItalic() ? "1" : "0") + "\");\n");
		buf.append(chart.getId() + "ChartP.setProperty(\"_style_underline\",\"" + (chart.getValueFormat().isUnderline() ? "1" : "0") + "\");\n");
		
		if (chart.getValueFormat().getBackgroundColor() != null){
			String c = "";
			String t = Integer.toHexString(chart.getValueFormat().getBackgroundColor().getRed());
			if (t.length() == 1){
				t = "0" + t;
			}
			c = c + t;
			
			t = Integer.toHexString(chart.getValueFormat().getBackgroundColor().getGreen());
			if (t.length() == 1){
				t = "0" + t;
			}
			c = c + t;
			
			t = Integer.toHexString(chart.getValueFormat().getBackgroundColor().getBlue()) ;
			if (t.length() == 1){
				t = "0" + t;
			}
			c = c + t;
			
			
			buf.append(chart.getId() + "ChartP.setProperty(\"_style_background\",\"" + c +"\");\n");
		}
		if (chart.getValueFormat().getFontColor() != null){
			String c = "";
			String t = Integer.toHexString(chart.getValueFormat().getFontColor().getRed());
			if (t.length() == 1){
				t = "0" + t;
			}
			c = c + t;
			
			t = Integer.toHexString(chart.getValueFormat().getFontColor().getGreen());
			if (t.length() == 1){
				t = "0" + t;
			}
			c = c + t;
			
			t = Integer.toHexString(chart.getValueFormat().getFontColor().getBlue()) ;
			if (t.length() == 1){
				t = "0" + t;
			}
			c = c + t;
			buf.append(chart.getId() + "ChartP.setProperty(\"_style_fontColor\",\"" + c +"\");\n");
		}
		
		//links
		buf.append(spacing + "        Properties " + chart.getId() + "Drill = new Properties();\n");
		
		if (!chart.getOutputParameters().isEmpty() ){
			buf.append(chart.getId()+ "Drill.setProperty(\"drillJs\", \"drillJs\");\n");
		}
		
		
		if (chart.getDrillDatas().getTypeTarget() == TypeTarget.ZoomPopup){
			buf.append(chart.getId()+ "Drill.setProperty(\"zoomDivName\", \"" + chart.getId() + "\");\n");
			buf.append(chart.getId()+ "Drill.setProperty(\"zoomWidth\", \"" + chart.getDrillDatas().getZoomWidth() + "\");\n");
			buf.append(chart.getId()+ "Drill.setProperty(\"zoomHeight\", \"" + chart.getDrillDatas().getZoomHeight() + "\");\n");
		}
		
			
		
		
		
		if (chart.getDrillDatas().isDrillable()){
			StringBuffer b = new StringBuffer();
			StringBuffer _url = new StringBuffer();
			/*_url.append("String _url = request.getRequestURI() + \"?\";\n");
			_url.append("if( request.getQueryString() != null){\n");
			_url.append("    _url = _url + request.getQueryString();\n");
			_url.append("}\n");
			boolean first = true;
			if (chart.getDrillDatas().getUrl() == null || "".equals(chart.getDrillDatas().getUrl())){

				for(IComponentDefinition p : pOuts.keySet()){
					if (p == chart){
						_url.append(" if (_url.contains(\"" + p.getId() + "=\")){\n");
						_url.append("    int _p = _url.indexOf(\""+ p.getId() + "=\");\n");
						_url.append("    String _firstPart = _url.substring(0,_p);\n");
						_url.append("    int _end = _url.indexOf(\"&\", _p+1);\n");
						_url.append("    if (_end > 0){\n");
						_url.append("    _url = _firstPart + _url.substring(_end)+  \"&" + p.getId() + "=\";\n");
						_url.append("    }\n");
						_url.append("    else{\n");
						_url.append("    _url = _firstPart + \"&" + p.getId() + "=\";\n");
						_url.append("    }\n");
						_url.append("   }\n");
						continue;
					}
					
					
					_url.append(" if (_url.contains(\"" + p.getId() + "=\")){\n");
					_url.append("    int _p = _url.indexOf(\""+ p.getId() + "=\") + \""+ p.getId() + "=\".length();\n");
					_url.append("     String _firstPart = _url.substring(0,_p);\n");
					_url.append("     String _value = " + pOuts.get(p).replace("Parameter", "")+ "Parameter;\n"); 
					_url.append("     String _lastPart = _url.substring(_p);\n");
					_url.append("     if (URLDecoder.decode(_lastPart,\"UTF-8\").startsWith(_value)){\n");
					_url.append("         _lastPart = _lastPart.replace(URLEncoder.encode(_value.toString(),\"UTF-8\"),\"\");\n");

					_url.append("     }\n");
					_url.append("     if (!_lastPart.startsWith(\"&\")&& _lastPart.contains(\"&\")){\n");
					_url.append("         _lastPart = _lastPart.substring(_lastPart.indexOf(\"&\"));\n");
					_url.append("     }\n");
					
					_url.append("     _url = _firstPart + URLEncoder.encode(_value.toString(),\"UTF-8\") + _lastPart;\n");
					_url.append(" Logger.getLogger(\"runtimeFdLogger\").debug(\"_firstPart=\"+_firstPart ); \n");
					_url.append(" Logger.getLogger(\"runtimeFdLogger\").debug(\"_value=\"+_value ); \n");
					_url.append(" Logger.getLogger(\"runtimeFdLogger\").debug(\"_value(enc)=\"+URLEncoder.encode(_value.toString(),\"UTF-8\") ); \n");
					_url.append(" Logger.getLogger(\"runtimeFdLogger\").debug(\"_lastPart=\"+_lastPart ); \n");
					_url.append(" Logger.getLogger(\"runtimeFdLogger\").debug(\"drillUrl=\"+_url);\n");
					
					_url.append(" }\n");
					_url.append(" else{\n");
					_url.append("     String _pVal = _parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\");\n");
					_url.append("     if (_pVal == null)_pVal=\"\";\n");
					_url.append("    _url = _url + \"&" + p.getId() + "=\"+ _pVal;\n");
					_url.append(" }\n");
					if (first){
						first = false;
						b.append(p.getId() + "=\"+_parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\")+\"");
						
					}
					else{
						b.append("&" + p.getId() + "=\"+_parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\")+\"");
					}
				}
				if (!first){
					b.append("\"");
				}
				else{
					b.append("request.getContextPath() + request.getServletPath() + \"?\"");
				}
				buf.append(_url);
				buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"url\",_url);\n");
				*/
			/*			}
			else{
				b.append("\"" + chart.getDrillDatas().getUrl() + "\"");

				boolean b1 = true;
				if (!chart.getOutputParameters().isEmpty()){
						
					for(ComponentParameter p : chart.getParameters()){
						if (p instanceof OutputParameter){
							if (!chart.getDrillDatas().getUrl().contains("?") && b1){
								b.append(" + \"?");
								
							}
							else{
								b1 = false;
//								b.append("+\"");
							}
							if(b1){
								b1 = false;
							}
							else{
								b.append("+\"&");
							}
							boolean found = false;
							for(IComponentDefinition def : pOuts.keySet()){
								if ((p.getName()+"Parameter").equals(pOuts.get(def))){
									b.append(p.getName() + "=");
									found = true;
									if (def == chart){
										buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"pName\",\"" + p.getName() + "\");\n");
										b.append("\"");
									}
									else{
										b.append("\"+_parameterMap.get(\"" + p.getName() + "\")");
									}
									
									
									break;
								}
							}
							if (!found){
								b.append(p.getName() + "=");
								b.append("\"+_parameterMap.get(\"" + p.getName() + "\")");
							}
							
						}
					}
					
				}
				buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"url\"," + b.toString() + ");\n");
			}
			
			
			
			
			*/
			for(ComponentConfig c : configs){
				for(ComponentParameter p : c.getParameters()){
					if (p instanceof OutputParameter){
						continue;
					}
					if (c.getComponentNameFor(p).equals(chart.getName()) ){
						String cName = c.getComponentNameFor(p).replace(" ", "_");
						buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"pName\",\"" + cName + "\");\n");
						break;
					}
				}
			}
			
			buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"categoryAsValue\",\"\" + " + chart.getDrillDatas().isCategorieAsParameterValue() + ");\n");
			
			String event = "";
			
			final StringBuffer drivenComponentJS = new StringBuffer();
			for(String s : drivenCellsId){
				drivenComponentJS.append("setParameter('"+ s + "', '" + chart.getId() + "');");
			}
			
			event = drivenComponentJS.toString() + "setLocation();";

			buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"event\",\"" +event + "\");\n");
		}
		
		
		//generate colors String[]
		StringBuffer colors = new StringBuffer();
		colors.append("new String[][]{" );
		boolean first = true;
		for(DataAggregation s : ((IChartData)chart.getDatas()).getAggregation()){
			if (first){
				first = false;
				
			}
			else{
				colors.append(", ");
			}
			
			colors.append("new String[]{");
			boolean fCol = true;
			for(String c : s.getColorsCode()){
				if (fCol){
					fCol = false;
										}
				else{
					colors.append(", ");
				}
				colors.append("\"" + c + "\"");
			}
			colors.append("}");
		}
		colors.append("}");
		
		
		if (chart.getDatas() instanceof MultiSerieChartData){
			MultiSerieChartData d = (MultiSerieChartData)data;
			
			//generate MeasureName String[][]
			StringBuffer measureNames = new StringBuffer();
			first = true;
			
			List<DataAggregation> _aggs = new ArrayList<DataAggregation>( d.getAggregation());
//			Collections.sort(_aggs,new DataAggregation.DataAggragationComparator() );
			
			StringBuffer msRendering = new StringBuffer();
			StringBuffer dualAxis = new StringBuffer();
			
			for(DataAggregation s : _aggs){
				if (chart.getNature().isCombination()){
					if (msRendering.length() != 0){
						msRendering.append(";");
					}
					msRendering.append(s.getMeasureName() + ":"+ s.getRendering());
				}
				
				if (chart.getNature().isDualY()){
					if (dualAxis.length() != 0){
						dualAxis.append(";");
					}
					if (s.isPrimaryAxis()){
						dualAxis.append(s.getMeasureName() + ":P");
					}
					else{
						dualAxis.append(s.getMeasureName() + ":S");
					}
					
				}
				
				if (first){
					first = false;
					measureNames.append("new String[]{");
				}
				else{
					measureNames.append(", ");
				}
				measureNames.append("\"" + s.getMeasureName() + "\"");
			}
			measureNames.append("}");
			buf.append(chart.getId() + "ChartP.setProperty(\"_dualYPrimaries\",\"" + dualAxis.toString() +"\");\n");
			buf.append(chart.getId() + "ChartP.setProperty(\"_combinationRenderings\",\"" + msRendering.toString() +"\");\n");
			
			String lineSerieName = chart.getNature().isLineCombnation() ? "\"" + ((LineCombinationOption)chart.getOptions(LineCombinationOption.class)).getLineSerieName() + "\"": "null";
			
			String containsSeries = " new boolean[]{";
			boolean _bb = true;
			for(DataAggregation a : d.getAggregation()){
				if (_bb){_bb=false;}else{containsSeries +=", ";};
				containsSeries += a.isApplyOnDistinctSerie();
			}

			containsSeries += "} ";
			
			buf.append(spacing + "        " + chart.getId() + "Xml =  new FusionChartMultiSeriesXmlGenerator(true).createMultiSerieXml(" + chart.getId() + "Values, " +chart.getId() + "ChartP, " + d.getNonSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", " + chart.getId() + "Drill ) ;\n");
			if (d.getSplitedMesureNumber() >0 && d.getSerieFieldIndex() != null && d.getSerieFieldIndex() >=0 ){
				buf.append(spacing + "        " + chart.getId() + "Xml = new FusionChartMultiSeriesXmlGenerator().appendSplitedSeriesXml(" + chart.getId() + "ChartP," + chart.getId() + "ValuesSeries, " + d.getSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", " + chart.getId() + "Drill," + chart.getId() + "Xml, " + d.getNonSplitedMesureNumber() + ");\n");
			}
			
//			if (chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART)){
//				
//				
//			}
//			else if (chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART_FREE)){
//				buf.append(spacing + "        " + chart.getId() + "Xml = \"\\\"\" + new FFCMultiSeriesXmlGenerator(true).createMultiSerieXml(" + chart.getId() + "Values, " +chart.getId() + "ChartP, " + d.getNonSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", " + chart.getId() + "Drill ) + \"\\\"\" ;\n");
//				if (d.getSplitedMesureNumber() >0 && d.getSerieFieldIndex() != null && d.getSerieFieldIndex() >=0 ){
//					buf.append(spacing + "        " + chart.getId() + "Xml = \"\\\"\" + new FFCMultiSeriesXmlGenerator().appendSplitedSeriesXml(" + chart.getId() + "ChartP," +chart.getId() + "ValuesSeries, " + d.getSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", " + chart.getId() + "Drill," + chart.getId() + "Xml, " + d.getNonSplitedMesureNumber() + ") + \"\\\"\" ;\n");
//				}
//			}
			
		}
		else{
			buf.append(spacing + "        " + chart.getId() + "Xml =  new FusionChartXmlGenerator(true).createMonoSerieXml(" + chart.getId() + "ValuesSeries, "+chart.getId() + "ChartP, "+ chart.getId() + "Drill, " + colors.toString() + ") ;\n");
			
//			if (chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART)){
//				
//			}
//			else if (chart.getRenderer() == ChartRenderer.getRenderer(ChartRenderer.FUSION_CHART_FREE)){
//				buf.append(spacing + "        " + chart.getId() + "Xml = \"\\\"\" + new FFCXmlGenerator().createMonoSerieXml(" + chart.getId() + "ValuesSeries, "+chart.getId() + "ChartP, "+ chart.getId() + "Drill, " + colors.toString() + ") + \"\\\"\" ;\n");
//			}
			
			
		}
		
		
		buf.append(spacing + "     }catch(Exception e){\n");
		buf.append(spacing + "         Logger.getLogger(\"runtimeFdLogger\").error(\"error when getting xmlDatas on Chart" + chart.getName() + "\", e);");
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		
		
		buf.append(spacing + "     <p id='" + chart.getId() + "'");
		
		
		if (chart.getCssClass() != null || ! "".equals(chart.getCssClass())){
			buf.append(" class=\"" + chart.getCssClass() + "\" ");
		}
		
		buf.append(">\n");
		
		buf.append(spacing + "         <script type=\"text/javascript\">\n");
		buf.append(spacing + "             var chart_monChart = new FusionCharts(\"../../FusionCharts/" + getSwf(chart.getNature(), chart.getRenderer()) + "\", \"Id_monChart\", \"" + chart.getWidth() + "\", \"" + chart.getHeight() + "\", \"0\", \"0\");\n");
		buf.append(spacing + "             chart_monChart.setDataXML( \"<%= " + chart.getId() + "Xml%>\" );\n");    
		buf.append(spacing + "             chart_monChart.setTransparent(true);\n");	        
		buf.append(spacing + "             chart_monChart.render(\""+ chart.getId() +"\");\n");	     
//		buf.append(spacing + "             chart_monChart.addParam(\"WMode\", \"Transparent\");\n");
		buf.append(spacing + "         </script>\n");    
		
		
		//
		if (chart.getDrillDatas().getTypeTarget() == TypeTarget.DetailReport){
			buf.append(generateReportDrill2(chart, profil));
		}
		
		
		buf.append(spacing + "     </p>\n");
	    
		
		StringBuffer fullbuf = new StringBuffer();
		if (data instanceof IDimensionableDatas){
			ComponentStyle style = chart.getComponentStyle();
			IDimensionableDatas dimDt = (IDimensionableDatas)data;
			
			try{
				ComponentFilterDefinition _dumyFilter = new ComponentFilterDefinition(chart.getId(), chart.getDictionary());
				_dumyFilter.setRenderer(FilterRenderer.getRenderer(FilterRenderer.MENU));
				((MenuOptions)_dumyFilter.getOptions(MenuOptions.class)).setWidth(chart.getWidth());
				((MenuOptions)_dumyFilter.getOptions(MenuOptions.class)).setSize(chart.getWidth() / 100);
				((MenuOptions)_dumyFilter.getOptions(MenuOptions.class)).setIsVertical(false);
				
				
				
				FilterData _dumyData = (FilterData)_dumyFilter.getDatas();
				 
				_dumyData.setDataSet(dimDt.getDimensionDataSet());
				_dumyData.setColumnLabelIndex(dimDt.getDimensionLabelIndex());
				_dumyData.setColumnValueIndex(dimDt.getDimensionValueIndex());
				
				
				fullbuf.append(FilterGenerator.generateJspBlock(spacing.length(), _dumyFilter, outputParameterName, pOuts, configs));
			}catch(Exception ex){
				ex.printStackTrace();
			}
			fullbuf.append(buf.toString());
		}
		else{
			fullbuf.append(buf.toString());
		}
		return fullbuf.toString();
	}
	
	
	
private static String generateJFreeChart(ComponentChartDefinition chart, String spacing, String query, IChartData data, String outputParameterName, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil profil){
		
		String resultSet = chart.getId() + "ResultSet";
		
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     String " + chart.getId() + "HTML = null;\n");
		if (data instanceof IDimensionableDatas){
			buf.append(spacing + "     " + resultSet + " = null;\n");
		}
		else{
			buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		}
		
		buf.append(spacing + "     List<List<Object>> " + chart.getId() + "ValuesSeries = null;\n");
		buf.append(spacing + "     List<List<Object>> " + chart.getId() + "Values = null;\n");
		buf.append(spacing + "     try{\n");
		
		
		
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, chart));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		if (chart.getDatas() instanceof MultiSerieChartData){
			MultiSerieChartData d = (MultiSerieChartData)chart.getDatas();
			buf.append(spacing + "        List<java.awt.Point> pt = new java.util.ArrayList<java.awt.Point>();\n");
			
			for(DataAggregation a : d.getAggregation()){
				buf.append(spacing + "        pt.add(new java.awt.Point(" + (a.getValueFieldIndex() + 1) + ", "+ a.getAggregator()+ "));\n");
			}
			buf.append(spacing + "        " + chart.getId() + "ValuesSeries = Aggregationer.performAggregation(" +resultSet + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex() + ", " + d.getSerieFieldIndex() +", " + data.getOrderFieldIndex() +  ", pt, new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize()+ "}," + data.getCategorieFieldLabelIndex() + ");\n");
			buf.append(spacing + "        " + chart.getId() + "Values = Aggregationer.performAggregation(" +query + ".executeQuery()" + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex()+ ", " + d.getSerieFieldIndex()  + ", " + data.getOrderFieldIndex() + ", pt, new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() + "}," + data.getCategorieFieldLabelIndex() + ");\n");
		}else{
			ChartData d = (ChartData)chart.getDatas();
			buf.append(spacing + "        " + chart.getId() + "ValuesSeries = Aggregationer.performAggregation(" +resultSet + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex() + ", " + d.getValueFieldIndex() + ", " + d.getOrderFieldIndex() + ", " + d.getAggregator() + ", new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() + "});\n");
		}
		
		
		buf.append(spacing + "        // create chartProperties\n");
		buf.append(generateOptions( spacing + "        ", chart.getOptions(), chart.getName(), chart.getId(), null));
		
		
		//links
		buf.append(spacing + "        Properties " + chart.getId() + "Drill = new Properties();\n");
		
		if (!chart.getOutputParameters().isEmpty() ){
			buf.append(chart.getId()+ "Drill.setProperty(\"drillJs\", \"drillJs\");\n");
		}
		
		if (chart.getDrillDatas().isDrillable()){
			StringBuffer b = new StringBuffer();
			boolean first = true;
			if (chart.getDrillDatas().getUrl() == null || "".equals(chart.getDrillDatas().getUrl())){
				for(IComponentDefinition p : pOuts.keySet()){
					if (first){
						first = false;
						b.append("request.getContextPath() + request.getServletPath() +\"?" + p.getId() + "=\"+_parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\")+\"");
						
					}
					else{
						b.append("&" + p.getId() + "=\"+_parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\")+\"");
					}
				}
				if (!first){
					b.append("\"");
				}
				else{
					b.append("request.getContextPath() + request.getServletPath() + \"?\"");
				}
				
			}
			else{
				b.append("\"" + chart.getDrillDatas().getUrl() + "\"");
//				if (!chart.getDrillDatas().getUrl().contains("?")){
//					b.append(" + \"?\"");
//					
//				}
				boolean b1 = true;
				if (!chart.getOutputParameters().isEmpty()){
						
					for(ComponentParameter p : chart.getParameters()){
						if (p instanceof OutputParameter){
							if (!chart.getDrillDatas().getUrl().contains("?") && b1){
								b.append(" + \"?");
								
							}
							else{
								b1 = false;
//								b.append("+\"");
							}
							if(b1){
								b1 = false;
							}
							else{
								b.append("+\"&");
							}
							boolean found = false;
							for(IComponentDefinition def : pOuts.keySet()){
								if ((p.getName()+"Parameter").equals(pOuts.get(def))){
									b.append(p.getName() + "=");
									found = true;
									if (def == chart){
										buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"pName\",\"" + p.getName() + "\");\n");
										b.append("\"");
									}
									else{
										b.append("\"+_parameterMap.get(\"" + p.getName() + "\")");
									}
									
									
									break;
								}
							}
							if (!found){
								b.append(p.getName() + "=");
								b.append("\"+_parameterMap.get(\"" + p.getName() + "\")");
							}
							
						}
					}
					
				}
			}
			
			
			
			for(ComponentConfig c : configs){
				for(ComponentParameter p : c.getParameters()){
					if (p instanceof OutputParameter){
						continue;
					}
					if (c.getComponentNameFor(p).equals(chart.getName()) ){
						String cName = c.getComponentNameFor(p).replace(" ", "_");
						buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"pName\",\"" + cName + "\");\n");
						break;
					}
				}
			}
			
			buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"url\"," + b.toString() + ");\n");
			
			buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"categoryAsValue\",\"\" + " + chart.getDrillDatas().isCategorieAsParameterValue() + ");\n");
		}
		
		
		//generate colors String[]
		StringBuffer colors = new StringBuffer();
		colors.append("new String[][]{" );
		boolean first = true;
		for(DataAggregation s : ((IChartData)chart.getDatas()).getAggregation()){
			if (first){
				first = false;
				
			}
			else{
				colors.append(", ");
			}
			
			colors.append("new String[]{");
			boolean fCol = true;
			for(String c : s.getColorsCode()){
				if (fCol){
					fCol = false;
										}
				else{
					colors.append(", ");
				}
				colors.append("\"" + c + "\"");
			}
			colors.append("}");
		}
		colors.append("}");
		
		
		if (chart.getDatas() instanceof MultiSerieChartData){
			MultiSerieChartData d = (MultiSerieChartData)data;
			
			//generate MeasureName String[][]
			StringBuffer measureNames = new StringBuffer();
			first = true;
			for(DataAggregation s : d.getAggregation()){
				if (first){
					first = false;
					measureNames.append("new String[]{");
				}
				else{
					measureNames.append(", ");
				}
				measureNames.append("\"" + s.getMeasureName() + "\"");
			}
			measureNames.append("}");
			
			
			String lineSerieName = chart.getNature().isLineCombnation() ? "\"" + ((LineCombinationOption)chart.getOptions(LineCombinationOption.class)).getLineSerieName() + "\"": "null";
			
			String containsSeries = " new boolean[]{";
			boolean _bb = true;
			for(DataAggregation a : d.getAggregation()){
				if (_bb){_bb=false;}else{containsSeries +=", ";};
				containsSeries += a.isApplyOnDistinctSerie();
			}

			containsSeries += "} ";
			

			buf.append(spacing + "        " + chart.getId() + "HTML = new JFREEMultiSerieGenerator().createMultiSerieXml(" +
					"\"" + chart.getId() + "JFREE\", " + 
					chart.getWidth() + ", " + 
					chart.getHeight() + ", " +
					chart.getNature().getNature() + ", " + 
					chart.getId() + "ValuesSeries, "+
					chart.getId() + "Values, " +
					chart.getId() + "ChartP, "+ 
					chart.getId() + "Drill, " + 
					colors.toString() + ", " +
					d.getAggregation().size() + ", " + 
					(d.getSerieFieldIndex() != null) + ", " + 
					measureNames.toString() + ", " + 
					lineSerieName +", " +
					containsSeries + 
					") ;\n");
		}
		else{
				buf.append(spacing + "        " + chart.getId() + "HTML = new JFREEMonoSerieGenerator().createMonoSerieXml(" +
						"\"" + chart.getId() + "JFREE\", " + 
						chart.getWidth() + ", " + 
						chart.getHeight() + ", " +
						chart.getNature().getNature() + ", " + 
						chart.getId() + "ValuesSeries, "+
						chart.getId() + "ChartP, "+ 
						chart.getId() + "Drill, " + 
						colors.toString() + ") ;\n");
						
			
		}
		
		
		buf.append(spacing + "     }catch(Exception e){\n");
		buf.append(spacing + "         Logger.getLogger(\"runtimeFdLogger\").error(\"error when getting xmlDatas on Chart" + chart.getName() + "\", e);");
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		
		
		buf.append(spacing + "     <p id='" + chart.getId() + "'");
		
		
		if (chart.getCssClass() != null || ! "".equals(chart.getCssClass())){
			buf.append(" class=\"" + chart.getCssClass() + "\" ");
		}
		buf.append(">\n");
		buf.append("<%\n");
		buf.append(spacing + "    out.write("+ chart.getId() + "HTML);\n");
		buf.append("%>\n");
		
		
		
		//
		if (chart.getDrillDatas().getTypeTarget() == TypeTarget.DetailReport){
			buf.append(generateReportDrill2(chart, profil));
		}
		
		
		buf.append(spacing + "     </p>\n");
	    
		
		StringBuffer fullbuf = new StringBuffer();
		if (data instanceof IDimensionableDatas){
			ComponentStyle style = chart.getComponentStyle();
			IDimensionableDatas dimDt = (IDimensionableDatas)data;
			
			try{
				ComponentFilterDefinition _dumyFilter = new ComponentFilterDefinition(chart.getId(), chart.getDictionary());
				_dumyFilter.setRenderer(FilterRenderer.getRenderer(FilterRenderer.MENU));
				((MenuOptions)_dumyFilter.getOptions(MenuOptions.class)).setWidth(chart.getWidth());
				((MenuOptions)_dumyFilter.getOptions(MenuOptions.class)).setSize(chart.getWidth() / 100);
				((MenuOptions)_dumyFilter.getOptions(MenuOptions.class)).setIsVertical(false);
				
				
				
				FilterData _dumyData = (FilterData)_dumyFilter.getDatas();
				 
				_dumyData.setDataSet(dimDt.getDimensionDataSet());
				_dumyData.setColumnLabelIndex(dimDt.getDimensionLabelIndex());
				_dumyData.setColumnValueIndex(dimDt.getDimensionValueIndex());
				
				
				fullbuf.append(FilterGenerator.generateJspBlock(spacing.length(), _dumyFilter, outputParameterName, pOuts, configs));
			}catch(Exception ex){
				ex.printStackTrace();
			}
			fullbuf.append(buf.toString());
		}
		else{
			fullbuf.append(buf.toString());
		}
		return fullbuf.toString();
	}


	private static String generateOptions(String spacing, List<IComponentOptions> opts, String name, String id, String drillUrl){
		StringBuffer buf = new StringBuffer();
		buf.append(spacing + "Properties " + id + "ChartP = new Properties();\n");
		for(IComponentOptions opt : opts){
			
			
			for(String key : opt.getInternationalizationKeys()){
				buf.append(spacing + id + "ChartP.setProperty(\"" + key + "\", i18nReader.getLabel(clientLocale, \"" + name + "." + key + "\", _parameterMap));\n");
			}
			
			for(String key : opt.getNonInternationalizationKeys()){
				if (opt.getValue(key) != null){
					buf.append(spacing + id + "ChartP.setProperty(\"" + key + "\", \"" + opt.getValue(key)  + "\");\n");
				}
				
			}
			
			if(opt instanceof GenericOptions) {
				boolean multiLabels = ((GenericOptions)opt).isMultiLineLabels();
				buf.append(spacing + id + "ChartP.setProperty(\"multiLineLabels\", \"" + String.valueOf(multiLabels) + "\");\n");
			}
		}
		

		
		
		buf.append("\n\n");
		return buf.toString();
	}
	
	public static String getSwf(ChartNature nature, IComponentRenderer renderer) {
		switch(nature.getNature()){
		case ChartNature.BAR:
			return "Bar2D.swf";
		case ChartNature.COLUMN:
			return "Column2D.swf";
		case ChartNature.COLUMN_3D:
			return "Column3D.swf";
		case ChartNature.FUNNEL:
			return "Funnel.swf";
		case ChartNature.GAUGE:
			return "AngularGauge.swf";
		case ChartNature.LINE:
			return "Line.swf";
		case ChartNature.PIE:
			return "Pie2D.swf";
		case ChartNature.PIE_3D:
			return "Pie3D.swf";
		case ChartNature.PYRAMID:
			return "Pyramid.swf";
		case ChartNature.RADAR:
			return "Radar.swf";
		case ChartNature.STACKED_BAR:
			return "StackedBar2D.swf";
		case ChartNature.STACKED_BAR_3D:
			return "StackedBar3D.swf";
		case ChartNature.STACKED_COLUMN:
			return "StackedColumn2D.swf";
		case ChartNature.STACKED_COLUMN_3D:
			return "StackedColumn3D.swf";
		case ChartNature.COLUMN_3D_MULTI:
			return "MSColumn3D.swf";
		case ChartNature.COLUMN_MULTI:
			return "MSColumn2D.swf";
		case ChartNature.LINE_MULTI:
			return "MSLine.swf";
		case ChartNature.COLUMN_3D_LINE:
			return "MSColumnLine3D.swf";
		case ChartNature.COLUMN_3D_LINE_DUAL:
			return "MSColumn3DLineDY.swf";
		case ChartNature.COLUMN_LINE_DUAL:
			return "MSCombiDY2D.swf";
		case ChartNature.STACKED_COLUMN_3D_LINE_DUAL:	
			return "StackedColumn3DLineDY.swf";
		case ChartNature.BAR_3D:	
			return "MSBar3D.swf";
		case ChartNature.STACKED_AREA_2D:
			return "MSArea.swf";
		case ChartNature.MARIMEKO:
			return "Marimekko.swf";
		case ChartNature.SINGLE_Y_2D_COMBINATION:
			return "MSCombi2D.swf";
		case ChartNature.SINGLE_Y_3D_COMBINATION:
			return "MSCombi3D.swf";
		case ChartNature.STACKED_2D_LINE_DUAL_Y:
			return "StackedColumn2DLine.swf";
		case ChartNature.DUAL_Y_2D_COMBINATION:
			return "MSCombiDY2D.swf";
		}
		return "";
	}
	
	private static String generateReportDrill2(ComponentChartDefinition chart, VanillaProfil profil) {
		StringBuffer buf = new StringBuffer();
		buf.append("<%\n");
		
		buf.append("HashMap<String, String> _chartReportParams = new HashMap<String, String>();\n");
		
		for(ComponentParameter p : chart.getParameters()){
			if (p instanceof OutputParameter){
				continue;
			}
			buf.append("   <input type='hidden' name='" + chart.getId() +"_Parameter_" + p.getName() + "' value=\"<%=" + p.getName() + "Parameter %>\" />");
			buf.append("_chartReportParams.put(p.getName(), " + p.getName()+ "Parameter;\n");
		}
		buf.append("ChartReportHelper _chartReportHelper = new ChartReportHelper(\"" + profil.getVanillaLogin() + "\", \""+ profil.getVanillaPassword() + "\");\n");
		
		buf.append("String reportHtml =  _chartReportHelper.getHTML(\"" + profil.getVanillaUrl() + VanillaConstants.VANILLA_PLATFORM_DISPATCHER_SERVLET +"\","+
				profil.getVanillaGroupId() +"," +
				profil.getRepositoryId()+"," +
				profil.getDirectoryItemId()+"," +
				chart.getDatas().getDataSet().getDataSourceName() + ", " +  
				chart.getDatas().getDataSet().getName() +"DataSet, _chartReportParams);\n" );
		
		buf.append("%>\n");
		
		StringBuffer sb = new StringBuffer();
//		sb.append("f=eval('<%=reportHtml.replace(\"'\",\"\\\"\")%>');window.open('./','popup');f.target='popup';f.submit();");
		sb.append("popWin=window.open('<%=reportHtml%>','popup');");
		String label = ((ChartDrillReportOption)chart.getOptions(ChartDrillReportOption.class)).getChartDataDetails();
		
		String cssName =((ChartDrillReportOption)chart.getOptions(ChartDrillReportOption.class)).getCssName();
		if (cssName!= null && !"".equals(cssName)){
			cssName = " class='" + cssName + "' ";
		}
		
		buf.append("<a " +  cssName + " href=\"javascript:" + sb.toString() + "\">" + label +  "</a>");

		
		
		return buf.toString();
	}
	
//	private static String generateReportDrill(ComponentChartDefinition chart, VanillaProfil profil) {
//		StringBuffer buf = new StringBuffer();
//		tttttttttt
//		buf.append("<form target=\"_blank\" name=\"" + chart.getId()  + "_form\" method=\"POST\" action=\"" + profil.getVanillaUrl() + ReportingComponent.RUN_REPORT_ODA_SERVLET + "\" >\n");
//		
//		
//		DataSource dataSource  = chart.getDictionary().getDatasource(chart.getDatas().getDataSet().getDataSourceName());
//		Enumeration en = dataSource.getPrivateProperties().propertyNames();
//		while(en.hasMoreElements()){
//			String pName = (String)en.nextElement();
//			buf.append("   <input type='hidden' name='" + chart.getId() +"_PrivateProperty_" + pName + "' value='" + dataSource.getPrivateProperties().getProperty(pName) + "' />");
//		}
//		
//		en = dataSource.getPublicProperties().propertyNames();
//		while(en.hasMoreElements()){
//			String pName = (String)en.nextElement();
//			buf.append("   <input type='hidden' name='" + chart.getId() +"_PublicProperty_" + pName + "' value='" + dataSource.getPublicProperties().getProperty(pName) + "' />");
//		}
//		
//		buf.append("   <input type='hidden' name='" + chart.getId() +"_OdaDriver' value='<%=" + chart.getDatas().getDataSet().getDataSourceName() + ".getOdaExtensionDataSourceId() %>' />");
//		buf.append("   <input type='hidden' name='" + chart.getId() +"_Query' value=\"<%=" + chart.getDatas().getDataSet().getName() + "DataSet.getQueryText() %>\" />");
//
//		for(ComponentParameter p : chart.getParameters()){
//			if (p instanceof OutputParameter){
//				continue;
//			}
//			buf.append("   <input type='hidden' name='" + chart.getId() +"_Parameter_" + p.getName() + "' value=\"<%=" + p.getName() + "Parameter %>\" />");
//		}
//		
//		String cols = "";
//		
//		for(ColumnDescriptor c : chart.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors()){
//			if (cols.length() != 0){
//				cols = cols + ";";
//			}
//			cols = cols + c.getColumnName();
//		}
//		buf.append("   <input type='hidden' name='" + chart.getId() +"_Columns' value=\"" + cols +  "\" />");
//		
//		String formName = chart.getId() + "_form";
//		StringBuffer sb = new StringBuffer();
//		sb.append("f=eval(" + formName + ");window.open('./','popup');f.target='popup';f.submit();");
//
////		buf.append("<a href=\"javascript:document." +  chart.getId() + "_form.submit()\">Drill</a>");
//		
//		String label = ((ChartDrillReportOption)chart.getOptions(ChartDrillReportOption.class)).getChartDataDetails();
//		
//		String cssName =((ChartDrillReportOption)chart.getOptions(ChartDrillReportOption.class)).getCssName();
//		if (cssName!= null && !"".equals(cssName)){
//			cssName = " class='" + cssName + "' ";
//		}
//		
//		buf.append("<a " +  cssName + " href=\"javascript:" + sb.toString() + "\">" + label +  "</a>");
//		
//		
//		//		my_form = eval(the_form)
////		window.open("./wait.php", "popup", "height=440,width=640,menubar='no',toolbar='no',location='no',status='no',scrollbars='no'");
////		 my_form.target = "popup";
////		  my_form.submit();
//		
//		buf.append("</form>");
//		return buf.toString();
//	}
	
	
	
	
	
	
	private static String generateOpenFlashChart(ComponentChartDefinition chart, String spacing, String query, IChartData data, String outputParameterName, HashMap<IComponentDefinition, String> pOuts, List<ComponentConfig> configs, VanillaProfil profil){
		
		String resultSet = chart.getId() + "ResultSet";
		
		StringBuffer buf = new StringBuffer();
		
		/*
		 * Datas
		 */
		buf.append(spacing + " <%\n");
		buf.append(spacing + "     String " + chart.getId() + "Xml = null;\n");
		if (data instanceof IDimensionableDatas){
			buf.append(spacing + "     " + resultSet + " = null;\n");
		}
		else{
			buf.append(spacing + "     IResultSet " + resultSet + " = null;\n");
		}
		
		buf.append(spacing + "     List<List<Object>> " + chart.getId() + "ValuesSeries = null;\n");
		buf.append(spacing + "     List<List<Object>> " + chart.getId() + "Values = null;\n");
		buf.append(spacing + "     try{\n");
		
		
		
		
		buf.append(JSPDataGenerator.prepareQuery(spacing, query, chart));
		
		buf.append(spacing + "        " + resultSet + " = " + query + ".executeQuery();\n");
		if (chart.getDatas() instanceof MultiSerieChartData){
			MultiSerieChartData d = (MultiSerieChartData)chart.getDatas();
			
			buf.append(spacing + "        List<java.awt.Point> splitedpt = new java.util.ArrayList<java.awt.Point>();\n");
			buf.append(spacing + "        List<java.awt.Point> pt = new java.util.ArrayList<java.awt.Point>();\n");
			
			
			for(DataAggregation a : d.getAggregation()){
				if (a.isApplyOnDistinctSerie()){
					buf.append(spacing + "        splitedpt.add(new java.awt.Point(" + (a.getValueFieldIndex() + 1) + ", "+ a.getAggregator()+ "));\n");
					
				}
				else{
					buf.append(spacing + "        pt.add(new java.awt.Point(" + (a.getValueFieldIndex() + 1) + ", "+ a.getAggregator()+ "));\n");
				}
			}
			buf.append(spacing + "        " + chart.getId() + "ValuesSeries = Aggregationer.performAggregation(" +resultSet + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex() + ", " + d.getSerieFieldIndex() +", " + data.getOrderFieldIndex() +  ", splitedpt, new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() + "}," + data.getCategorieFieldLabelIndex() + ");\n");
			buf.append(spacing + "        " + chart.getId() + "Values = Aggregationer.performAggregation(" +query + ".executeQuery()" + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex()+ ", null, " + data.getOrderFieldIndex() + ", pt, new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() +"}," + data.getCategorieFieldLabelIndex() + ");\n");
		}else{
			ChartData d = (ChartData)chart.getDatas();
			buf.append(spacing + "        " + chart.getId() + "ValuesSeries = Aggregationer.performAggregation(" +resultSet + ", " + data.getCategorieFieldIndex()+ ", " + data.getCategorieFieldLabelIndex() + ", " + d.getValueFieldIndex() + ", " + d.getOrderFieldIndex() + ", " + d.getAggregator() + ", new int[]{" + d.getLimit().getType() + "," + d.getLimit().getSize() +  "});\n");
		
//			if (d.getOrderFieldIndex() !=  null && d.getOrderFieldIndex() > -1){
//				buf.append(spacing + "        " + chart.getId() + "ValuesSeries = Sorter.sort(" + resultSet + ", " + (d.getOrderFieldIndex()) +");\n");
//			}
		}
		
		
		//buf.append(spacing + "        // create chartProperties\n");
		//buf.append(generateOptions( spacing + "        ", chart.getOptions(), chart.getName(), chart.getId(), null));
		
		
		
		
		
		
		
		
		buf.append(spacing + "     }catch(Exception e){\n");
		
		buf.append(spacing + "         e.printStackTrace();\n");
		buf.append(spacing + "     }\n");
		buf.append(spacing + "%>\n\n");
		
		
				
		
		/*
		 * colors
		 */
		StringBuffer colors = new StringBuffer();
		colors.append("new String[][]{" );
		boolean first = true;
		for(DataAggregation s : ((IChartData)chart.getDatas()).getAggregation()){
			if (first){
				first = false;
				
			}
			else{
				colors.append(", ");
			}
			
			colors.append("new String[]{");
			boolean fCol = true;
			for(String c : s.getColorsCode()){
				if (fCol){
					fCol = false;
										}
				else{
					colors.append(", ");
				}
				colors.append("\"" + c + "\"");
			}
			colors.append("}");
		}
		colors.append("}");
		
		/*
		 * Properties
		 */
		buf.append("<%\n");
		buf.append(generateOptions( spacing + "        ", chart.getOptions(), chart.getName(), chart.getId(), null));
		//add theNatureProp
		buf.append(chart.getId() + "ChartP.setProperty(\"chartNature\", \"" + chart.getNature().getNature() + "\");\n");
		
		
		
		/*
		 * Drill Properties
		 */
		
		
		if (chart.getDrillDatas().isDrillable()){
			
			
			buf.append(spacing + "        Properties " + chart.getId() + "Drill = new Properties();\n");
			if (!chart.getOutputParameters().isEmpty() ){
				buf.append(chart.getId()+ "Drill.setProperty(\"drillJs\", \"drillJs\");\n");
			}
			
			
			StringBuffer b = new StringBuffer();
			StringBuffer _url = new StringBuffer();
			_url.append("String _url = request.getRequestURL() + \"?\";\n");
			_url.append("if( request.getQueryString() != null){\n");
			_url.append("    _url = _url + request.getQueryString();\n");
			_url.append("}\n");
//			_url.append("System.out.println(\"incoming url : \" + _url);\n");
			first = true;
			if (chart.getDrillDatas().getUrl() == null || "".equals(chart.getDrillDatas().getUrl())){

				for(IComponentDefinition p : pOuts.keySet()){
					if (p == chart){
						_url.append(" if (_url.contains(\"" + p.getId() + "=\")){\n");
						_url.append("    int _p = _url.indexOf(\""+ p.getId() + "=\");\n");
						_url.append("    String _firstPart = _url.substring(0,_p);\n");
						_url.append("    int _end = _url.indexOf(\"&\", _p+1);\n");
						_url.append("    if (_end > 0){\n");
						_url.append("    _url = _firstPart + _url.substring(_end)+  \"&" + p.getId() + "=\";\n");
//						_url.append("System.out.println(\"*****\" + _url);\n");
						_url.append("    }\n");
						_url.append("    else{\n");
						_url.append("    _url = _firstPart + \"&" + p.getId() + "=\";\n");
						_url.append("    }\n");
						_url.append("   }\n");
						continue;
					}
					
					
					_url.append(" if (_url.contains(\"" + p.getId() + "=\")){\n");
					_url.append("    int _p = _url.indexOf(\""+ p.getId() + "=\") + \""+ p.getId() + "=\".length();\n");
					_url.append("     String _firstPart = _url.substring(0,_p);\n");
//					_url.append("System.out.println(\"_firstPart :\" +_firstPart);\n");
					_url.append("     String _value = " + pOuts.get(p).replace("Parameter", "")+ "Parameter;\n"); 
//					_url.append("System.out.println(\"_value :\" +_value);");
					_url.append("     String _lastPart = _url.substring(_p);\n");
					_url.append("     if (_lastPart.startsWith(_value)){\n");
					_url.append("         _lastPart = _lastPart.replace(_value,\"\");\n");
					_url.append("     }\n");
//					_url.append("System.out.println(\"_lastPart :\" +_lastPart);\n");
					
					_url.append("     _url = _firstPart + _value + _lastPart;\n");
//					_url.append("System.out.println(\"_url :\" +_url);\n");
					
					_url.append(" }\n");
					_url.append(" else{\n");
					_url.append("     String _pVal = _parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\");\n");
					_url.append("     if (_pVal == null)_pVal=\"\";\n");
					_url.append("    _url = _url + \"&" + p.getId() + "=\"+ _pVal;\n");
					_url.append(" }\n");
//					_url.append("System.out.println(\"post param url : \"+_url);\n");
					if (first){
						first = false;
						b.append(p.getId() + "=\"+_parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\")+\"");
						
					}
					else{
						b.append("&" + p.getId() + "=\"+_parameterMap.get(\"" + pOuts.get(p).replace("Parameter", "")+ "\")+\"");
					}
				}
				if (!first){
					b.append("\"");
				}
				else{
					b.append("request.getContextPath() + request.getServletPath() + \"?\"");
				}
//				_url.append("System.out.println(\"end url : \"+_url);\n");
				buf.append(_url);
//				buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"url\",_url);\n");
				buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"url\",\"_ofc_drill_" + chart.getId() + "\");\n");
			}
			else{
				b.append("\"" + chart.getDrillDatas().getUrl() + "\"");
//				if (!chart.getDrillDatas().getUrl().contains("?")){
//					b.append(" + \"?\"");
//					
//				}
				boolean b1 = true;
				if (!chart.getOutputParameters().isEmpty()){
						
					for(ComponentParameter p : chart.getParameters()){
						if (p instanceof OutputParameter){
							if (!chart.getDrillDatas().getUrl().contains("?") && b1){
								b.append(" + \"?");
								
							}
							else{
								b1 = false;
//								b.append("+\"");
							}
							if(b1){
								b1 = false;
							}
							else{
								b.append("+\"&");
							}
							boolean found = false;
							for(IComponentDefinition def : pOuts.keySet()){
								if ((p.getName()+"Parameter").equals(pOuts.get(def))){
									b.append(p.getName() + "=");
									found = true;
									if (def == chart){
										buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"pName\",\"" + p.getName() + "\");\n");
										b.append("\"");
									}
									else{
										b.append("\"+_parameterMap.get(\"" + p.getName() + "\")");
									}
									
									
									break;
								}
							}
							if (!found){
								b.append(p.getName() + "=");
								b.append("\"+_parameterMap.get(\"" + p.getName() + "\")");
							}
							
						}
					}
					
				}
//				buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"url\"," + b.toString() + ");\n");
				buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"url\",\"_ofc_drill_" + chart.getId() + "\");\n");
			}
			
			
			
			for(ComponentConfig c : configs){
				for(ComponentParameter p : c.getParameters()){
					if (p instanceof OutputParameter){
						continue;
					}
					if (c.getComponentNameFor(p).equals(chart.getName()) ){
						String cName = c.getComponentNameFor(p).replace(" ", "_");
						buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"pName\",\"" + cName + "\");\n");
						break;
					}
				}
			}
			
			
			
			buf.append(spacing + "        "+ chart.getId() + "Drill.setProperty(\"categoryAsValue\",\"\" + " + chart.getDrillDatas().isCategorieAsParameterValue() + ");\n");
		}
		
		
		/*
		 * generate JSON
		 */
		if (chart.getDatas() instanceof  MultiSerieChartData){
			MultiSerieChartData d = (MultiSerieChartData)data;
			
			//generate MeasureName String[][]
			StringBuffer measureNames = new StringBuffer();
			first = true;
			
			List<DataAggregation> _aggs = new ArrayList<DataAggregation>( d.getAggregation());
//			Collections.sort(_aggs,new DataAggregation.DataAggragationComparator() );
			for(DataAggregation s : _aggs){
				if (first){
					first = false;
					measureNames.append("new String[]{");
				}
				else{
					measureNames.append(", ");
				}
				measureNames.append("\"" + s.getMeasureName() + "\"");
			}
			measureNames.append("}");
			
			
			String lineSerieName = chart.getNature().isLineCombnation() ? "\"" + ((LineCombinationOption)chart.getOptions(LineCombinationOption.class)).getLineSerieName() + "\"": "null";
			
			String containsSeries = " new boolean[]{";
			boolean _bb = true;
			for(DataAggregation a : d.getAggregation()){
				if (_bb){_bb=false;}else{containsSeries +=", ";};
				containsSeries += a.isApplyOnDistinctSerie();
			}

			containsSeries += "} ";
			
			if (chart.getDrillDatas().isDrillable()){
				
				if (d.getSplitedMesureNumber() >0 && d.getSerieFieldIndex() != null && d.getSerieFieldIndex() >=0 ){
					//TODO
//					buf.append("String " + chart.getId() + "Json = \"\\\"\" + new MultiOpenFlashChartJsonGenerator().appendSplitedSeriesXml(" + chart.getId() + "ValuesSeries, " + d.getSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", " + chart.getId() + "Drill," + chart.getId() + "JsonJOFC, " + d.getNonSplitedMesureNumber() + ", " +chart.getId() + "ChartP) ;\n");
					buf.append("String " + chart.getId() + "Json= new MultiOpenFlashChartJsonGenerator().createJson(" + chart.getId() + "Values, " +chart.getId() + "ChartP, " + d.getNonSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", " + chart.getId() + "Drill, "+ chart.getId() + "ValuesSeries, " +  d.getSplitedMesureNumber() + ", " + d.getNonSplitedMesureNumber() + ").toString()  ;\n");
				}
				else{
					buf.append("String " + chart.getId() + "Json= new MultiOpenFlashChartJsonGenerator().createJson(" + chart.getId() + "Values, " +chart.getId() + "ChartP, " + d.getNonSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", " + chart.getId() + "Drill ).toString()  ;\n");
				}
				
				
			}
			else{
				
				if (d.getSplitedMesureNumber() >0 && d.getSerieFieldIndex() != null && d.getSerieFieldIndex() >=0 ){
					//TODO
//					buf.append(spacing + "        " + chart.getId() + "Xml = \"\\\"\" + new MultiOpenFlashChartJsonGenerator().appendSplitedSeriesXml(" + chart.getId() + "ValuesSeries, " + d.getSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", null," + chart.getId() + "JsonJOFC, " + d.getNonSplitedMesureNumber() + ", " +chart.getId() + "ChartP) ;\n");
//					buf.append("String " +  chart.getId() + "JsonJOFC= new MultiOpenFlashChartJsonGenerator().createJson( " + chart.getId()+ "Values, "+ chart.getId() + "ChartP, " + d.getNonSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", null ).toString()  ;\n");
					buf.append("String " +  chart.getId() + "Json= new MultiOpenFlashChartJsonGenerator().createJson(" + chart.getId() + "Values, " +chart.getId() + "ChartP, " + d.getNonSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", null , "+ chart.getId() + "ValuesSeries, " +  d.getSplitedMesureNumber() + ", " + d.getNonSplitedMesureNumber() + ").toString()  ;\n");
				}
				else{
					buf.append("String " +  chart.getId() + "Json= new MultiOpenFlashChartJsonGenerator().createJson( " + chart.getId()+ "Values, "+ chart.getId() + "ChartP, " + d.getNonSplitedMesureNumber() + ", " + measureNames.toString() + ", " + colors.toString() + ", " + lineSerieName + ", null ).toString()  ;\n");
				}
				
				
			}
		}
		else{
			if (chart.getDrillDatas().isDrillable()){
				buf.append("String " + chart.getId() + "Json= new OpenFlashChartJsonGenerator().generateMonoSerie( " + chart.getId()+ "ValuesSeries, "+ chart.getId() + "ChartP, "+ chart.getId() + "Drill, " + colors.toString() + ");\n");
			}
			else{
				buf.append("String " + chart.getId() + "Json= new OpenFlashChartJsonGenerator().generateMonoSerie( " + chart.getId()+ "ValuesSeries, "+ chart.getId() + "ChartP, null, " + colors.toString() + ");\n");
			}
		}
		
		
		
		
		
	
		buf.append("%>\n");
		
		
		
		buf.append("       <div id=\"" + chart.getId()+  "\"");
		
		
		
		if (chart.getCssClass() != null || ! "".equals(chart.getCssClass()) &&  "null".equals(chart.getCssClass())){
			buf.append(" class=\"" + chart.getCssClass() + "\" ");
		}
		buf.append("></div>\n");
		
		buf.append("     <script type=\"text/javascript\">\n");
		
		
		buf.append("     function _ofc_datas_" + chart.getId() + "()\n");
		buf.append("     {\n");
	
		buf.append("        var data = <%= "+ chart.getId() + "Json%>\n");
//		buf.append("     	alert( navigator.appName + \"\\n\" + navigator.appVersion );\n");
		buf.append("     	return JSON.stringify(data);\n");
		buf.append("     }\n");
		
		
		buf.append("     function _ofc_drill_" + chart.getId() + "(value){\n");
		
		
		buf.append("     setParameter('"+ chart.getId() + "', value);setLocation();");
			
		
		buf.append("     }\n");
		buf.append("      swfobject.embedSWF(\"../../ofc/open-flash-chart.swf\", \"" + chart.getId() + "\", \""  + chart.getWidth() + "\", \"" + chart.getHeight() + "\", \"9.0.0\", \"expressInstall.swf\",\n");
//		buf.append("        {\"data-file\":'../../<%= " + chart.getId() + "Json2%>'}\n");
		buf.append("        {\"get-data\":'_ofc_datas_" + chart.getId() + "'}\n");
		buf.append("      );\n");
		buf.append("      </script>\n");
		
		
		return buf.toString();
	}

	private static String generateDataFilterString(IChartData datas){
		StringBuffer buf = new StringBuffer();
		
		boolean firstAgg = true;
		for(DataAggregation a : datas.getAggregation()){
			if (firstAgg){
				firstAgg = false;
			}
			else{
				buf.append(";");
			}
			
			boolean firstFilter = true;
			for(IComponentDataFilter f : a.getFilter()){
				if (firstFilter){
					firstFilter = false;
				}
				else{
					buf.append("+");
				}
				buf.append(f.toString());
			}
			//ensure that we are empty
			if (firstFilter){
				firstAgg = true;
			}
		}
		
		return buf.toString();
	}
	
	private static String generateEvents(IBaseElement element, HashMap<ElementsEventType, String> defaultEvents, boolean pureHtml){
		StringBuffer buf = new StringBuffer();
		
		for(ElementsEventType type : element.getEventsType()){
			String sc = element.getJavaScript(type);
			if (sc != null && !"".equals(sc.trim())){
				buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\"") + sc.replace("\r\n", "").replace("\n", ""));
				
				if (defaultEvents == null || defaultEvents.get(type) == null){
					buf.append((pureHtml ? "\"" : "\\\"") + " ");
					continue;
				}
				
			}
			if (defaultEvents != null && defaultEvents.get(type) != null){
				if (sc != null && !"".equals(sc.trim())){
					buf.append(";" );
				}
				else{
					buf.append(" " + type.name() + (pureHtml ? "=\"" : "=\\\""));
				}
				//whats the fuck
				buf.append(defaultEvents.get(type));
				buf.append((pureHtml ? "\"" : "\\\"") + " ;");
				
			}
			
		}
		
		return buf.toString();
	}
}
