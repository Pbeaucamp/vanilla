package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentMapWMS;
import bpm.fd.api.core.model.components.definition.maps.openlayers.MapWMSOptions;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class WmsMapRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentMapWMS>{
	
	public String getJavaScriptFdObjectVariable(ComponentMapWMS filter){
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + filter.getName() + "\"]= new FdWMSMap(\"" + filter.getName() + "\")" + ";\n");
		
		buf.append("</script>\n");
		return buf.toString();
	}
	public String getHTML(Rectangle layout, ComponentMapWMS map, DashState state, IResultSet datas, boolean refresh){
		
		
		
		MapWMSOptions opt = (MapWMSOptions)map.getOptions(MapWMSOptions.class);
		StringBuffer buf = new StringBuffer();

		if(!refresh) {
			
			String legendStyle = getLayoutStyleCss(new Rectangle((layout.x - 200), (layout.y + 50), 200, 500), null);
			
			buf.append("<div id='legendWMS' " + legendStyle + ">\n");
			
			for(ColorRange color : map.getColorRanges()) {
				
				buf.append("<div style='height: 30px;'>\n");
				
				buf.append("<div style='height: 20px; width: 20px; background-color: #" + color.getHex() + "; float:left;'>\n");
				buf.append("</div>\n");
				buf.append("<label style='margin-left: 10px;'>"+color.getMin() + " to " + color.getMax()+"</label>\n");
				
				
				buf.append("</div>\n");
			}
			
			buf.append("</div>\n");
			
			buf.append(getComponentDefinitionDivStart(layout, map));
			buf.append("<script id='scriptWms'>\n");
		}
		
		//create the map, projection, controls and extent objects
		createMapElements(buf, opt, map);
		
		//create the base layer (the raster, can be WMTS, OSM)
		createBaseLayer(buf, opt, map);
		
		//add a raster vector if needed
		if(opt.getBaseVectorGeometry() != null && !opt.getBaseVectorGeometry().isEmpty()) {
			createBaseVectorLayer(buf, opt, map);
		}
		
		//create the vector layer
		//WFS
		if(opt.getVectorLayerType().equals(MapWMSOptions.LAYER_TYPE_WFS)) {
			createWfsVector(buf, opt, map, datas);
		}
		
		buf.append("	map1.addLayers(layers);\n");
		buf.append("	map1.zoomToMaxExtent();\n");
		if(opt.getBaseLayerType().equals(MapWMSOptions.LAYER_TYPE_OSM)) {
			buf.append("	map1.setCenter(new OpenLayers.LonLat("+opt.getTileOrigin()+"), 15);\n");
		}
		
		//MARKERS
		else if(opt.getVectorLayerType().equals(MapWMSOptions.LAYER_TYPE_MARKERS)) {
			createMarkersVector(buf, opt, map, datas);
		}
		
		
		
		if(!refresh) {
			buf.append("</script>\n");
			buf.append(getComponentDefinitionDivEnd());
			
			buf.append(getJavaScriptFdObjectVariable(map));
		}
		

		
		return buf.toString();
	}
	
	private void createBaseVectorLayer(StringBuffer buf, MapWMSOptions opt, ComponentMapWMS map) {
		buf.append("		layer_vectors = new OpenLayers.Layer.Vector(\"Drawings\", { displayInLayerSwitcher: true } );map1.addLayer(layer_vectors); \n");
		
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(opt.getBaseVectorGeometry()));
			String geom = new String(encoded, Charset.forName("UTF-8"));
			
			buf.append("		var vector = drawPolygon([" + geom + "],{strokeColor:\"#0000FF\",strokeWidth: 1,fillColor: \"#0000ff\",fillOpacity: 0.1});\n");
			buf.append("		layer_vectors.addFeatures(vector);\n");
			
			buf.append("		layers.push(layer_vectors);\n");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	private void createMarkersVector(StringBuffer buf, MapWMSOptions opt, ComponentMapWMS map, IResultSet datas) {
		buf.append("		layer_markers = new OpenLayers.Layer.Markers(\"Marker\", { projection: new OpenLayers.Projection(\""+opt.getProjection().toUpperCase()+"\"),visibility: true, displayInLayerSwitcher: false });\n");
		buf.append("		map1.addLayer(layer_markers);\n");
		
		buf.append("		function createPopup(mark) {\n");
		buf.append("		if(mark.test.popup != null) {destroyPopup(mark.test);}else {\n");
		buf.append("		    mark.test.popup = new OpenLayers.Popup.FramedCloud(\"pop\",\n");
		buf.append("		        mark.test.data.lonlat,\n");
	    buf.append("		        null,\n");
	    buf.append("		        mark.test.data.valueZone,\n");
	    buf.append("		       null,\n");
	    buf.append("		       true\n");
	    buf.append("		       \n");
	    buf.append("		   );\n");
	    buf.append("		   map1.addPopup(feature.popup);\n");
	    buf.append("		 }}\n");

	    buf.append("function destroyPopup(feature) {\n");
		buf.append("feature.popup.destroy();\n");
		buf.append("feature.popup = null;\n");
		buf.append("}\n");
			try {
				MapDatas data = (MapDatas) map.getDatas();
				
				while(datas.next()) {
					String zone = datas.getString(data.getZoneIdFieldIndex());
					String value = datas.getString(data.getValueFieldIndex());
					
					//create the marker
					double latitude = datas.getDouble(data.getLatitudeIndex());
					double longitude = datas.getDouble(data.getLongitudeIndex());
					
					String imgPath = datas.getString(data.getImgPathIndex());
					int[] imgSize = computeImgSize(data, datas);
					
					buf.append("		var coord = new OpenLayers.LonLat(Lon2Merc(" + longitude + "), Lat2Merc(" + latitude + "));\n");
					buf.append("		var feature = new OpenLayers.Feature(layer_markers, coord);\n");
					
				    //icon
					buf.append("		var iconTab = new Array('"+imgPath+"','"+imgSize[0]+"','"+imgSize[1]+"','"+imgSize[2]+"','"+imgSize[3]+"');");
					buf.append("		feature.data.icon = makeIcon(iconTab);");
					buf.append("		feature.data.valueZone = \""+value+"\";");
					
					buf.append("		var marker = feature.createMarker();\n");
					buf.append("	    marker.test = feature;\n");
					buf.append("		var markerClick = function(evt) {createPopup(evt.object);\n");
					buf.append("		};\n");
				    buf.append("		marker.events.register(\"mousedown\", feature, markerClick);\n");				    
					buf.append("		layer_markers.addMarker(marker);\n");
				}
				
				
				buf.append("	layer_markers.redraw();\n");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * One day it will be done better
	 * @param data
	 * @param datas
	 * @return
	 * @throws Exception
	 */
	private int[] computeImgSize(MapDatas data, IResultSet datas) throws Exception {
		String size = datas.getString(data.getImgSizeIndex());
		
		String[] sizes = size.split(";");
		
		int[] res = new int[4];
		
		res[0] = Integer.parseInt(sizes[0]);
		res[1] = Integer.parseInt(sizes[1]);
		res[2] = Integer.parseInt(sizes[2]);
		res[3] = Integer.parseInt(sizes[3]);
		
		return res;
	}
	private void createWfsVector(StringBuffer buf, MapWMSOptions opt, ComponentMapWMS map, IResultSet datas) {
		
		buf.append("		var communesLayer = new OpenLayers.Layer.Vector( \""+opt.getVectorLayerName()+"\", {\n");
		buf.append("			strategies: [new OpenLayers.Strategy.BBOX()],projection: new OpenLayers.Projection(\"" + opt.getProjection().toUpperCase() + "\"),isBaseLayer: false,\n");
		buf.append("			protocol: new OpenLayers.Protocol.WFS({\n");
		buf.append("				version: \"1.0.0\", url: \""+opt.getVectorLayerUrl()+"\",\n");
		buf.append("				featureType: \""+opt.getVectorLayerName()+"\",\n" +
				"				featureNS: \"http://mapserver.gis.umn.edu/mapserver\", featurePrefix: \"ms\", geometryName: \"msGeometry\", srsName: \"" + opt.getProjection().toUpperCase() + "\"");
		buf.append("		})});\n");
		
		buf.append("		layers.push(communesLayer);\n");
		
		buf.append("	communesLayer.events.register(\"loadend\", communesLayer, function (e) {\n");
		
		try {
			MapDatas data = (MapDatas) map.getDatas();
			
			while(datas.next()) {
				String zone = datas.getString(data.getZoneIdFieldIndex());
				int value = datas.getInt(data.getValueFieldIndex());
				
				ColorRange color = null;			
				for(ColorRange range : map.getColorRanges()) {
					if(range.getMin() <= value && range.getMax() >= value) {
						color = range;
						break;
					}
				}

				buf.append("for (var index = 0; index < communesLayer.features.length; ++index) {\n");
				buf.append("	if(communesLayer.features[index].data.id == "+zone+") {\n");					
				buf.append("		communesLayer.features[index].style = "+createStyle(color.getHex(), opt.getOpacity())+";\n");
				buf.append("		break;\n");
				buf.append("	}\n");
				buf.append("}\n");
			}
			
			buf.append("	communesLayer.redraw();\n");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		buf.append("	});\n");
	}
	private void createBaseLayer(StringBuffer buf, MapWMSOptions opt, ComponentMapWMS map) {
		if(opt.getBaseLayerType().equals(MapWMSOptions.LAYER_TYPE_WMTS)) {
			
			buf.append("	layers.push(new OpenLayers.Layer.WMS(\n");
			buf.append("	        \""+opt.getBaseLayerName()+"\",\n");
			buf.append("	        \""+opt.getBaseLayerUrl()+"\",\n");
			buf.append("			{layers: '"+opt.getBaseLayerName()+"',\n");
			buf.append("			projection: new OpenLayers.Projection(\"" + opt.getProjection().toUpperCase() + "\"),\n");
			buf.append("			format: \"image/png\"},\n");
			buf.append("	        {tileSize: new OpenLayers.Size("+ opt.getTileOrigin() +")}\n");
			buf.append("	)); \n");
		}
		else if(opt.getBaseLayerType().equals(MapWMSOptions.LAYER_TYPE_OSM)) {
			buf.append("	layers.push(new OpenLayers.Layer.OSM.Mapnik(\"Mapnik\"));\n");
		}
	}
	
	private void createMapElements(StringBuffer buf, MapWMSOptions opt, ComponentMapWMS map) {
		buf.append("	var extent = new OpenLayers.Bounds("+opt.getBounds()+");\n");
		buf.append("	var controls = [\n");
		buf.append("		new OpenLayers.Control.Navigation(),\n");
		buf.append("		new OpenLayers.Control.PanZoomBar(),\n");
		buf.append("		new OpenLayers.Control.MousePosition(),\n");
		buf.append("		new OpenLayers.Control.LayerSwitcher(),\n");
		buf.append("		new OpenLayers.Control.KeyboardDefaults()\n");
		buf.append("		];\n");
		buf.append("	var	proj = new OpenLayers.Projection(\""+opt.getProjection().toUpperCase()+"\");\n");
		buf.append("	var map1 = new OpenLayers.Map({\n");
		buf.append("		div: \""+map.getName()+"\",\n");
		buf.append("		maxExtent : extent,\n");
		buf.append("		controls: controls,\n");
		buf.append("		projection: proj,\n");
		buf.append("		maxResolution: 256\n");
		buf.append("	});\n");
		buf.append("	var layers = [];\n");
	}
	
	private String createStyle(String hex, String opacity) {
		if(opacity != null && !opacity.isEmpty() && !opacity.equals("100")) {
			
			return "{fill: true, fillColor: \"#"+hex+"\", fillOpacity: "+((double)Integer.parseInt(opacity)) / 100+"}";
		}
		return "{fill: true, fillColor: \"#"+hex+"\"}";
	}
}
