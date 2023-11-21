package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.GoogleMapDatas;
import bpm.fd.api.core.model.components.definition.maps.IMapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.MapRenderer;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.engine.datas.JSPDataGenerator;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class MapGenerator {
	public static String generateJspBlock(int offset, ComponentMap map, String outputParameterName, VanillaProfil vanillaProfile)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		
		String event = generateEvents(map, new HashMap<ElementsEventType, String>(){{put(ElementsEventType.onClick, "setLocation();");}}, false);
		
		String size = "style=\\\"width:" + map.getMapInfo().getWidth() + "px; height: " + map.getMapInfo().getHeight() + "px\\\" ";  
		
		//add the div for the metrics and date comboBox if its a map using FreeMetrics
		buf.append("<%\n");
		
		if(((MapInfo)map.getMapInfo()).getMapType() != null && ((MapInfo)map.getMapInfo()).getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
			
			generateFreeMetricsSelectionPanel(map, buf, vanillaProfile);
			
		}
		

		
		buf.append("                    ColorRange[] " + map.getId() + "Colors= new ColorRange[]{\n");
		
		boolean first = true;
		for(ColorRange r : map.getColorRanges()){
			if (first){
				first = false;
			}
			else{
				buf.append(",\n");
			}
			buf.append("                        new ColorRange(\"" + r.toString() + "\" , \"" + r.getHex() + "\", " + r.getMin() + ", " + r.getMax() + ")\n");
		}
		
		buf.append("                    };\n");
		
		if(map.getDatas() != null && map.getDatas().getDataSet() != null) {
			buf.append("                    IResultSet " + map.getId() + "ResultSet = null;\n") ;
			buf.append("                    try{\n");
			buf.append(JSPDataGenerator.prepareQuery("                    ", map.getDatas().getDataSet().getId() + "Query", map));
			buf.append("                        " + map.getId()+"ResultSet = " + map.getDatas().getDataSet().getId() + "Query.executeQuery();\n");
			buf.append("                    }catch(Exception e){\n");
			buf.append("                        Logger.getLogger(\"runtimeFdLogger\").error(\"error when getting xmlDatas on Map" + map.getName() + "\", e);");
			buf.append("                        e.printStackTrace();\n");
			buf.append("                    }\n");
		}
		
		if (map.getRenderer().getRendererStyle() == MapRenderer.VANILLA_FUSION_MAP){
			buf.append( "                    out.write(\"<div id=\\\""+  map.getId() +  "\\\" " + size + " >\\n\");\n");
			buf.append( "                    out.write(\"</div>\\n\");\n");
			generateFusionMapPart(map, buf, vanillaProfile);
		}
		else if (map.getRenderer().getRendererStyle() == MapRenderer.VANILLA_FLASH_MAP){
			buf.append( "                    out.write(\"<div id=\\\""+  map.getId() +  "\\\" " + size + " >\\n\");\n");
			buf.append( "                    out.write(\"</div>\\n\");\n");
			generateVanillaFlashMapPart(map, buf, vanillaProfile);
		}
		else if (map.getRenderer().getRendererStyle() == MapRenderer.VANILLA_OPENLAYERS_MAP) {
			
			generateOpenLayersPart(map, buf, vanillaProfile);
		}
		else{
			
			buf.append("                    /***************************************\n");
			buf.append("                    * generate GoogleMap XML/\n");
			buf.append("                    ***************************************/\n");
			IMapDatas datas = (IMapDatas)map.getDatas();
			buf.append("                    GoogleMapGenerator " + map.getId() + "Generator = new GoogleMapGenerator(" + 
					map.getId()+"ResultSet," + 
					"\"" + map.getId() + "_googleMap" + "\", " + 
					((GoogleMapDatas)datas).getLatitudeFieldIndex() + ", " +
					((GoogleMapDatas)datas).getLongitudeFieldIndex() + ", " +
					((GoogleMapDatas)datas).getValueFieldIndex() + ", " +
					((GoogleMapDatas)datas).getLabelFieldIndex() + ", " +
					map.getId() + "Colors"+ 
					
					");\n" );
			
			buf.append("                    String " + map.getId() + "Markers = " +  map.getId() + "Generator.generate();\n");
			buf.append("%>\n");

			
			
			buf.append( "                    <script type=\"text/javascript\">\n");
			buf.append("   	                     var " + map.getId() + "_googleMap = null;\n");
			buf.append("        		         var latlng = new google.maps.LatLng(<%= " + map.getId() + "Generator.getCenter()[0] %>,<%=" + map.getId() +"Generator.getCenter()[1]%>);\n");
			buf.append("   	 		             var myOptions = {\n");
			buf.append("     				         zoom: " + 8 + ",\n");
			buf.append("     				         center: latlng,\n");
			buf.append("     				         mapTypeId: google.maps.MapTypeId.ROADMAP\n");
			buf.append("   			             }\n");
			buf.append("         		        " + map.getId() + "_googleMap = new google.maps.Map(document.getElementById('" + map.getId() + "'), myOptions);\n");
			buf.append("         		        <%\n");
			buf.append("         		            out.write(" + map.getId() + "Markers);\n");
//			buf.append("         		            out.write(\"//ziziziz\");\n");
//			buf.append("         		            System.out.println(" + map.getId() + "Generator.generate());\n");
			buf.append("         		        %>\n");
			
			buf.append( "                    </script>\n");;
		}
		


		return buf.toString();
	}
	
	private static void generateOpenLayersPart(ComponentMap map, StringBuffer buf, VanillaProfil vanillaProfile) {
		MapWMSOptions opt = (MapWMSOptions)map.getOptions(MapWMSOptions.class);
		
		buf.append("out.write(\"<div name='" + map.getName() + "' id='" + map.getName() + "' style=\\\"height:" + map.getHeight() + "px;clear:both;width:" + map.getWidth() + "px\\\"></div>\\n\");");
		
		buf.append("                         out.write(\"<script defer=\\\"defer\\\" type=\\\"text/javascript\\\">\\n\");\n");
		
		buf.append("                         out.write(\"var map = new OpenLayers.Map('" + map.getId() + "');\\n\");\n");
		
		buf.append("                         out.write(\"var osm_layer = new OpenLayers.Layer.WMS(\\\"" + map.getId() + "\\\"" +
				",\\\"" + opt.getWmsUrl() + "\\\"" +
						", {maxResolution : 0.703125" +
						",type : \\\""+ opt.getType() + "\\\"" +
								", layers : \\\"" + opt.getLayerName() + "\\\"});\\n\");\n");
		
		buf.append("						 out.write(\"var polygonLayer = new OpenLayers.Layer.Vector(\\\"Polygon Layer\\\");\\n\");\n");
		
		buf.append("                         out.write(\"map.addLayers([osm_layer,polygonLayer]);\\n\");\n");
		
		buf.append("						 out.write(\"map.addControl( new OpenLayers.Control.LayerSwitcher() );\\n\");\n");
		
		if (((MapInfo)map.getMapInfo()).getMapType() != null && ((MapInfo)map.getMapInfo()).getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
			buf.append("					if(request.getParameter(\"" + map.getId() + "_metricdate_metric\") != null && request.getParameter(\"" + map.getId() + "_metricdate_date\") != null) {\n");
			buf.append("						HashMap<String,String> metricValues = fmGen.generateMapData(" + ((MapInfo)map.getMapInfo()).getMapId() + ", Integer.parseInt(request.getParameter(\"" + map.getId() + "_metricdate_metric\")), request.getParameter(\"" + map.getId() + "_metricdate_date\"), " + map.getId() + "Colors);\n");

			buf.append("						out.write(\"var points = new Array();\\n\");\n");
			buf.append("						out.write(\"var linear_ring, polygonFeature;\\n\");\n");
			
			buf.append("						for(String zoneId : metricValues.keySet()) {\n");

			buf.append("							 out.write(fmGen.getPolygonString(zoneId));\n");
			
			buf.append("						}\n");
			
			buf.append("					}\n");
			
		}
		
		buf.append("                         out.write(\"map.setCenter(new OpenLayers.LonLat(" + opt.getLongitude() + ", " + opt.getLatitude() + "), " + opt.getZoom() + ");\\n\");\n");
		
		
		buf.append("						 out.write(\"</script>\\n\");\n");
		buf.append("%>\n");
	}

	private static void generateFreeMetricsSelectionPanel(ComponentMap map, StringBuffer buf, VanillaProfil vanillaProfile) {
		MapInfo info = ((MapInfo)map.getMapInfo());
		buf.append("                    out.write(\"<div id=\\\"" + map.getId() + "_metricdate\\\"></div>\\n\");\n");
		
		buf.append("                    out.write(\"<script type=\\\"text/javascript\\\">\\n\");\n");
		
		buf.append("                    out.write(\"var metricSelected = \"+request.getParameter(\"" + map.getId() + "_metricdate_metric\")+\";\\n\");\n");
		buf.append("                    out.write(\"var fmpart = new FMMap(\\\"" + map.getId() + "_metricdate\\\");\\n\");\n");
		
		//Get back the metric list
		buf.append("                    FMContext fmCtx = " +
				"new FMContext(\"" + info.getFmUser() + "\", \"" + info.getFmPassword() + "\", false);\n");
		
		buf.append("                    MapFreeMetricsDataGenerator fmGen = " +
				"new MapFreeMetricsDataGenerator(\"" + vanillaProfile.getVanillaLogin() + "\", \"" + vanillaProfile.getVanillaPassword() + "\", \"" + vanillaProfile.getVanillaUrl() + "\", fmCtx);\n");
		
		//create javascript metric elements
		buf.append("                    List<FMMetricBean> metrics = fmGen.getMetrics(" + info.getMapId() + ");\n");
		
		buf.append("                    out.write(\"var " + map.getId() + "_list = new Array();\\n\");\n");
		buf.append("                    int i = 0;\n");
		buf.append("                    for(FMMetricBean metric : metrics) {\n");
		
		buf.append("	                    out.write(\"var metric_\" + i + \" = new MetricElement(\\\"\" + metric.getId() + \"\\\", \\\"\" + metric.getName() + \"\\\", \\\"\" + metric.getPeriodicity() + \"\\\");\\n\");\n");
		buf.append("	                    out.write(\"" + map.getId() + "_list[\"+i+\"] = metric_\" + i + \";\\n\");\n");
		
		buf.append("                    i++;}\n");
		
		//create the metric list
		buf.append("                    out.write(\"fmpart.createMetricList(" + map.getId() + "_list, metricSelected);\\n\");\n");
		
		//create the date part
		buf.append("                    out.write(\"var dateSelected = \\\"\"+request.getParameter(\"" + map.getId() + "_metricdate_date\")+\"\\\";\\n\");\n");
		
		buf.append("                    out.write(\"fmpart.createDatesLists(\\\"DAY\\\", \"+fmGen.getPossibleYears()+\",dateSelected);\\n\");\n");
		
		buf.append("                    out.write(\"fmpart.createEvents();\\n\");\n");
		
		buf.append("                    out.write(\" </script>\\n\");\n");
		
//		buf.append("<%if(request.getParameter(\"" + map.getId() + "_metric\") != null &&  request.getParameter(\"" + map.getId() + "_date\")) { \n%>");
//		buf.append("	");
//		buf.append("}\n%>");
	}

	private static void generateVanillaFlashMapPart(ComponentMap map, StringBuffer buf, VanillaProfil vanillaProfile) {
		buf.append("                    /***************************************\n");
		buf.append("                    * generate VanillaMap XML/\n");
		buf.append("                    ***************************************/\n");
		buf.append("					String " + map.getId() + "Xml = \"\";\n");
		if (((MapInfo)map.getMapInfo()).getMapType() != null && ((MapInfo)map.getMapInfo()).getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
			buf.append("					if(request.getParameter(\"" + map.getId() + "_metricdate_metric\") != null && request.getParameter(\"" + map.getId() + "_metricdate_date\") != null) {\n");
			buf.append("						HashMap<String,String> metricValues = fmGen.generateMapData(" + ((MapInfo)map.getMapInfo()).getMapId() + ", Integer.parseInt(request.getParameter(\"" + map.getId() + "_metricdate_metric\")), request.getParameter(\"" + map.getId() + "_metricdate_date\"));\n");
			buf.append("                   		" + map.getId() + "Xml = new VanillaMapXmlGenerator().generateXmlForMetrics(" + map.getId() + "Colors, metricValues);\n");
			buf.append("					}\n");
		}
		else {
			IMapDatas datas = (IMapDatas)map.getDatas();
			buf.append("                    " + map.getId() + "Xml = new VanillaMapXmlGenerator().generateXml(" + map.getId() + "Colors, "+ ((MapDatas)datas).getZoneIdFieldIndex() + ", " + datas.getValueFieldIndex() + ", " + map.getId()+"ResultSet);\n" );
		}
		buf.append("%>\n");
		
		
		buf.append( "                    <script type=\"text/javascript\">\n");
		//use the vanillaRuntimeUrl from profile if is deploed mode,
		//or the stored url if we are un design mode
		buf.append( "                        var map = new VanillaMap(\"" + map.getId()  + "\", \"" + (vanillaProfile.getVanillaUrl() != null ? vanillaProfile.getVanillaUrl() : ((MapInfo)map.getMapInfo()).getVanillaRuntimeUrl()) + "/fusionMap/Maps/" + ((MapInfo)map.getMapInfo()).getSwfFileName() + "\",\""+  map.getId() +  "\",\"" + map.getMapInfo().getWidth() + "\", \"" + map.getMapInfo().getHeight() + "\");\n");
		buf.append( "                        map.setDatas(\"<%=" + map.getId() + "Xml%>\");\n");
		buf.append( "                        map.draw();\n");
		buf.append( "                    </script>\n");;
	}

	private static void generateFusionMapPart(ComponentMap map, StringBuffer buf, VanillaProfil vanillaProfile) {
		buf.append("                    /***************************************\n");
		buf.append("                    * generate FusionMap XML/\n");
		buf.append("                    ***************************************/\n");
		buf.append("					String " + map.getId() + "Xml = \"\";\n");
		if (((MapInfo)map.getMapInfo()).getMapType() != null && ((MapInfo)map.getMapInfo()).getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
			buf.append("					if(request.getParameter(\"" + map.getId() + "_metricdate_metric\") != null && request.getParameter(\"" + map.getId() + "_metricdate_date\") != null) {\n");
			buf.append("						HashMap<String,String> metricValues = fmGen.generateMapData(" + ((MapInfo)map.getMapInfo()).getMapId() + ", Integer.parseInt(request.getParameter(\"" + map.getId() + "_metricdate_metric\")), request.getParameter(\"" + map.getId() + "_metricdate_date\"));\n");
			buf.append("                   		" + map.getId() + "Xml = new FusionMapXmlGenerator(" + map.isShowLabels() + ").generateXmlForMetrics(" + map.getId() + "Colors, metricValues);\n");
			buf.append("					}\n");
		}
		else {
			
			/*
			 * drill
			 */
			buf.append("MapDrillRuntime "+ map.getId() + "Drill=null;\n");
			if (map.getDrillInfo() != null && map.getDrillInfo().isDrillable()){
				buf.append(map.getId() + "Drill=new MapDrillRuntime(");
				buf.append("request.getServletPath()+\"?\"+request.getQueryString(), new String[]{\""+ map.getId()+ "\"}, " + map.getDrillInfo().isSendCategory() + ",");
				
				buf.append("\"" + map.getDrillInfo().getModelPageName() + ".jsp\",");
				
				
				if (map.getDrillInfo().getModelPage() != null ){
					IBaseElement e = map.getDrillInfo().getModelPage().getProject().getFdModel().getContent().get(0);
					buf.append("\"_folder_" + e.getId() + "\");\n");
				}
				else{
					buf.append("null);\n");
				}
				
	
			}
						
			
			IMapDatas datas = (IMapDatas) map.getDatas();
			buf.append("                    " + map.getId() + "Xml = new FusionMapXmlGenerator(" + map.isShowLabels() + ").generateXml(" + map.getId() + "Colors, " + ((MapDatas) datas).getZoneIdFieldIndex() + ", " + datas.getValueFieldIndex() + ", " + map.getId() + "ResultSet," + map.getId() + "Drill);\n");
		}
		buf.append("%>\n");
		
		
		buf.append( "                    <script type=\"text/javascript\">\n");
		//use the vanillaRuntimeUrl from profile if is deploed mode,
		//or the stored url if we are un design mode
		buf.append( "                        var map = new FusionMaps(\"" + (vanillaProfile.getVanillaUrl() != null ? vanillaProfile.getVanillaUrl() : ((MapInfo)map.getMapInfo()).getVanillaRuntimeUrl()) + "/fusionMap/Maps/" + ((MapInfo)map.getMapInfo()).getSwfFileName() + "\", \"" + map.getId()  + "\", \"" + map.getMapInfo().getWidth() + "\", \"" + map.getMapInfo().getHeight() + "\", \"0\", \"0\");\n");
		buf.append( "                        map.setDataXML(\"<%= " + map.getId()+ "Xml %>\");\n");
		buf.append( "                        map.setTransparent(false);\n");
		buf.append( "                        map.render(\"" + map.getId() + "\");\n");
		buf.append( "                    </script>\n");;
	}

	/**
	 * generate the STring for events
	 * 
	 * @param element
	 * @param defaultEvents : contains the default behavior for the given Type
	 * @return
	 */
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
				buf.append(defaultEvents.get(type));
				buf.append((pureHtml ? "\"" : "\\\"") + " ");
			}
			
		}
		
		return buf.toString();
	}
}
