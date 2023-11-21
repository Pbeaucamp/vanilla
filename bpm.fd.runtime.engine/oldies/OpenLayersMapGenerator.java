package bpm.fd.runtime.engine.components;

import java.util.HashMap;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.api.core.model.events.ElementsEventType;
import bpm.fd.runtime.engine.VanillaProfil;

public class OpenLayersMapGenerator {
	public static String generateJspBlock(int offset, ComponentMapWMS map, String outputParameterName, VanillaProfil vanillaProfile)throws Exception{
		StringBuffer buf = new StringBuffer();
		
		for(int i = 0; i < offset * 4; i++){
			buf.append(" ");
		}
		
		String event = generateEvents(map, new HashMap<ElementsEventType, String>(){{put(ElementsEventType.onClick, "setLocation();");}}, false);
		
		buf.append("<div name='" + map.getName() + "' id='" + map.getName() + "' style=\"height:" + map.getHeight() + "px;width:" + map.getWidth() + "px\"></div>");
		
		buf.append(generateWmsLayer(offset, map.getName(), (MapWMSOptions)map.getOptions(MapWMSOptions.class), outputParameterName, vanillaProfile));


		return buf.toString();
	}
	
	public static Object generateWmsLayer(int offset, String mapName, MapWMSOptions opt, String outputParameterName, VanillaProfil vanillaProfile) {
		StringBuffer buf = new StringBuffer();

		buf.append("<script defer=\"defer\" type=\"text/javascript\">\n");
		
		buf.append("var map = new OpenLayers.Map('" + mapName + "');\n");
		
//		buf.append("var osm_layer = new OpenLayers.Layer.TMS(\"" + map.getName() + "\"" +
//				",\"" + opt.getWmsUrl() + "\"" +
//						", {maxResolution : 0.703125" +
//						",type : \""+ opt.getType() + "\"" +
//								", layername : \"" + opt.getLayerName() + "\"});\n");
		
		buf.append("var osm_layer = new OpenLayers.Layer.WMS(\"" + mapName + "\"" +
				",\"" + opt.getWmsUrl() + "\"" +
						", {maxResolution : 0.703125" +
						",type : \""+ opt.getType() + "\"" +
								", layers : \"" + opt.getLayerName() + "\"});\n");
		
		buf.append("map.addLayer(osm_layer);\n");
		buf.append("map.setCenter(new OpenLayers.LonLat(" + opt.getLongitude() + ", " + opt.getLatitude() + "), " + opt.getZoom() + ");\n");
		buf.append("map.addControl( new OpenLayers.Control.LayerSwitcher() );\n");
		buf.append("</script>");
		
		return buf.toString();
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
