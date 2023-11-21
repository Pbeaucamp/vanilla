package bpm.fd.runtime.engine.map.freemetrics;

import java.util.HashMap;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.platform.core.beans.FMContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class WmsMapGenerator {

	private ComponentMap map;
	private DashState state;
	private IResultSet datas;
	
	private MapFreeMetricsDataGenerator fmData;

	public WmsMapGenerator(ComponentMap map, DashState state, IResultSet datas) {
		this.map = map;
		this.state = state;
		this.datas = datas;
		
		MapInfo info = ((MapInfo)map.getMapInfo());
		
//		fmData = new MapFreeMetricsDataGenerator(state.getDashInstance().getUser().getLogin(), 
//				state.getDashInstance().getUser().getPassword(),
//				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
//				new FMContext(info.getFmUser(), info.getFmPassword(), false));
	}

	public String generateHtml() throws Exception {
		StringBuffer buf = new StringBuffer();
		
		MapWMSOptions opt = (MapWMSOptions)map.getOptions(MapWMSOptions.class);
		
		buf.append("<div name='" + map.getName() + "' id='" + map.getName() + "' style=\"position:absolute;top:202px;left:483px; height:" + map.getHeight() + "px;clear:both;width:" + map.getWidth() + "px\">\n");
		
//		buf.append("function loadMaps() {\n");
		buf.append("<script>\n");
		
		buf.append("	var extent = new OpenLayers.Bounds(1369548.0,5210443.0,1435084.0,5275979.0);\n");
		buf.append("	var controls = [\n");
		buf.append("		new OpenLayers.Control.Navigation(),\n");
		buf.append("		new OpenLayers.Control.PanZoomBar(),\n");
		buf.append("		new OpenLayers.Control.MousePosition(),\n");
		buf.append("		new OpenLayers.Control.LayerSwitcher(),\n");
		buf.append("		new OpenLayers.Control.KeyboardDefaults()\n");
		buf.append("		];\n");
		buf.append("	var ceramikBaseUrl = \"http://sigar2.agglo-larochelle.fr:8080/CeramikServer/rest\";\n");
		buf.append("	var	proj = new OpenLayers.Projection(\"EPSG:3946\");\n");
		buf.append("	var map1 = new OpenLayers.Map({\n");
		buf.append("		div: \""+map.getName()+"\",\n");
		buf.append("		maxExtent : extent,\n");
		buf.append("		controls: controls,\n");
		buf.append("		projection: proj,\n");
		buf.append("		maxResolution: 256\n");
		buf.append("	});\n");
		buf.append("	var layers = [];\n");
	
		buf.append("	layers.push(new OpenLayers.Layer.WMTS({\n");
		buf.append("	        name: \"ortho2010\",\n");
		buf.append("	        url: ceramikBaseUrl+\"/wmts/cdalr/ortho2010\",\n");
		buf.append("			version: \"1.0.0\",\n");
		buf.append("			requestEncoding: \"KVP\",\n");
		buf.append("	        layer: \"\",\n");
		buf.append("			tileOrigin: new OpenLayers.LonLat(1369548.0, 5275979.0),\n");
		buf.append("			tileSize: new OpenLayers.Size(256, 256),\n");
		buf.append("	        matrixSet: \"epsg:3946\",\n");
		buf.append("	        format: \"image/jpeg\",\n");
		buf.append("	        style: \"default\",\n");
		buf.append("	        opacity: 1.0,\n");
		buf.append("	        isBaseLayer: true\n");
		buf.append("	})); \n");
		
		buf.append("	$.each([\"fr_regions\", \"fr_departements\", \"fr_zones_emploi\", \"fr_arrondissements\", \"fr_epci\", \"fr_communes\", \"cdalr_communes\"], function(index, name) {\n"); 			
		buf.append("		layers.push(new OpenLayers.Layer.Vector( name, {\n");
		buf.append("			strategies: [new OpenLayers.Strategy.BBOX()],\n");
		buf.append("			visibility: false,\n");
		buf.append("			protocol: new OpenLayers.Protocol.WFS.v1_0_0({\n");
		buf.append("				url:  ceramikBaseUrl+\"/wxs/wfs/observatoire/\"+name,\n");
		buf.append("				featureType: name\n");
		buf.append("		})}))\n");
		buf.append("	});\n");
		
		buf.append("	map1.addLayers(layers);\n");
		buf.append("	map1.zoomToMaxExtent();\n");
		
		buf.append("</script>\n");
		
//		buf.append("	}\n");
		
//		String svg = fmData.generateMapAsSvg(14, 1, 
//				"2012-01-01",  new ColorRange[]{new ColorRange("Good", "FF0000", 5001, Integer.MAX_VALUE),
//				new ColorRange("Medium", "FF6600", 1001, 5000),
//				new ColorRange("Bad", "00FF00", 0, 1000)});
//		
//		buf.append("<img id='"+ map.getName()+"_img' height=\"600px\" width=\"750px\" src=\"\"  />");
		
		
		buf.append("</div>\n");
		
//		buf.append("                         <script defer=\"defer\" type=\"text/javascript\">\n\n");
//		buf.append("                         OpenLayers.Map.TILE_WIDTH = 1000;\n");
//		buf.append("                         OpenLayers.Map.TILE_HEIGHT = 500;\n");
//		buf.append("                         var map = new OpenLayers.Map('" + map.getId() + "');\n");
//		
//		buf.append("                         var osm_layer = new OpenLayers.Layer.WMS(\"" + map.getId() + "\"" +
//				",\"" + opt.getWmsUrl() + "\"" +
//						", {maxResolution : 0.703125" +
//						",type : \""+ opt.getType() + "\"" +
//								", layers : \"" + opt.getLayerName() + "\", exceptions: \"INIMAGE\"});\n");
//		
//		buf.append("						 var polygonLayer = new OpenLayers.Layer.Vector(\"Polygon Layer\");\n");
//		
//		buf.append("                         map.addLayers([osm_layer,polygonLayer]);\n");
//		
//		buf.append("						 map.addControl( new OpenLayers.Control.LayerSwitcher() );\n");
//		
//		if (((MapInfo)map.getMapInfo()).getMapType() != null && ((MapInfo)map.getMapInfo()).getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
//			
//			MapInfo info = ((MapInfo)map.getMapInfo());
//			
//			HashMap<String, String> zones = fmData.generateMapData(10, 1, 
//					"2010-01-10",  new ColorRange[]{new ColorRange("Good", "FF0000", 5001, Integer.MAX_VALUE),
//													new ColorRange("Medium", "FF6600", 1001, 5000),
//													new ColorRange("Bad", "00FF00", 0, 1000)});
//			for(String zoneId : zones.keySet()) {
//				String polygon = fmData.getPolygonString(zoneId);
//				buf.append(polygon);
//			}
//			
////			buf.append("					if(request.getParameter(\"" + map.getId() + "_metricdate_metric\") != null && request.getParameter(\"" + map.getId() + "_metricdate_date\") != null) {\n");
////			buf.append("						HashMap<String,String> metricValues = fmGen.generateMapData(" + ((MapInfo)map.getMapInfo()).getMapId() + ", Integer.parseInt(request.getParameter(\"" + map.getId() + "_metricdate_metric\")), request.getParameter(\"" + map.getId() + "_metricdate_date\"), " + map.getId() + "Colors);\n");
////
////			buf.append("						out.write(\"var points = new Array();\\n\");\n");
////			buf.append("						out.write(\"var linear_ring, polygonFeature;\\n\");\n");
////			
////			buf.append("						for(String zoneId : metricValues.keySet()) {\n");
////
////			buf.append("							 out.write(fmGen.getPolygonString(zoneId));\n");
////			
////			buf.append("						}\n");
////			
////			buf.append("					}\n");
//			
//			
//			
//			
//		}
//		
////		buf.append("                         map.setCenter(new OpenLayers.LonLat(" + opt.getLongitude() + ", " + opt.getLatitude() + "), " + opt.getZoom() + ");\n");
//		buf.append("                         map.setCenter(new OpenLayers.LonLat(120, -5), " + opt.getZoom() + ");\n");
//		
//		
//		buf.append("						 </script>\n");
		
		return buf.toString();
	}

}
