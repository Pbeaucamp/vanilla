package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Color;
import java.awt.Rectangle;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmMapOptions;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapDataSerie;
import bpm.fd.api.core.model.components.definition.maps.openlayers.VanillaMapOption;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.datas.map.OsmValue;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class VanillaMapRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentOsmMap>{

	@Override
	public String getHTML(Rectangle layout, ComponentOsmMap map, DashState state, IResultSet datas, boolean refresh) {
		VanillaMapOption opt = (VanillaMapOption)map.getOptions(VanillaMapOption.class);
		VanillaMapData mapData = (VanillaMapData)map.getDatas();
		
		//init the map objects
		if(!refresh) {
			try {
				RemoteMapDefinitionService remote = new RemoteMapDefinitionService();
				remote.configure(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
				
				MapVanilla mapVanilla = remote.getMapVanillaById(mapData.getMapId()).get(0);
				mapData.setMap(mapVanilla);
				for(VanillaMapDataSerie serie : mapData.getSeries()) {
					for(MapDataSet ds : mapVanilla.getDataSetList()) {
						if(serie.getMapDatasetId().intValue() == ds.getId()) {
							serie.setMapDataset(ds);
							break;
						}
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		StringBuffer buf = new StringBuffer();
		
		String level = state.getComponentValue(map.getId());
		String parentValue = null;
		try {
			if(level != null && level.equals("null")) {
				level = null;
			}
			if(level != null) {
				String[] parts = level.split(";");
				level = parts[0];
				parentValue = parts[1];
			}
		} catch(Exception e1) {
			level = null;
		}

		if(!refresh) {
			boolean addListForColors = false;
			for(VanillaMapDataSerie serie : mapData.getSeries()) {
				if(serie.isDisplay() && serie.isUseColsForColors()) {
					addListForColors = true;
					break;
				}
			}
			
			if(addListForColors) {
				buf.append("<div id='" + map.getName() + "_colcolors'>\n");
				buf.append("	<select id='" + map.getName() + "_colcolors_select' style='position:absolute;top:" + (layout.y - 25) + "px;margin-bottom:5px;margin-left:" + ((layout.width/2)-50) + "px;display: block;'>\n");
				for(ColumnDescriptor col : map.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors()) {
					if(!(col.getColumnIndex() == mapData.getValueFieldIndex().intValue()) && !(col.getColumnIndex() == mapData.getZoneFieldIndex().intValue())) {
						buf.append("	<option value='" + col.getColumnLabel() + "' onclick=\"" + "setParameter('" + map.getName() + "_colcolors',this.value, true);\">" + col.getColumnLabel() + "</option>\n");
					}
				}
				buf.append("	</select>\n");
				buf.append("<script type=\"text/javascript\">\n");
				buf.append("	var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;\n");
				buf.append("	if (is_chrome){\n");
				buf.append("		var filterContainer = document.getElementById('" + map.getName() + "_colcolors_select');\n");
				buf.append("		filterContainer.setAttribute('onchange','this.options[this.selectedIndex].onclick();');\n");
				buf.append("	}\n");
				buf.append("</script>\n");
				buf.append("</div>\n");
			}
			
			if(opt.isShowLegend()) {
				Rectangle newLayout = new Rectangle();
				newLayout.x = layout.x;
				newLayout.y = layout.y;
				
				if(opt.getLegendOrientation().equals(VanillaMapOption.ORIENTATION_VERTICAL)) {
					if(opt.getLegendLayout().equals(VanillaMapOption.LAYOUT_LEFT)) {
						newLayout.x = layout.x + 150;
						newLayout.width = layout.width - 150;
					}
					else {
						newLayout.width = layout.width - 150;
					}
					
					newLayout.height = layout.height;
					
				}
				else {
					if(opt.getLegendLayout().equals(VanillaMapOption.LAYOUT_BOTTOM)) {
						newLayout.height = layout.height - 50;
					}
					else {
						newLayout.height = layout.height - 50;
						newLayout.y = layout.y + 50;
					}
					newLayout.width = layout.width;
				}
				String legendStyle = "";
				if(opt.getLegendLayout().equals(VanillaMapOption.LAYOUT_BOTTOM)) {				
					legendStyle = getLayoutStyleCss(new Rectangle((newLayout.x), (newLayout.y + newLayout.height), newLayout.width, 50), "text-align:center;");
				}
				else if(opt.getLegendLayout().equals(VanillaMapOption.LAYOUT_TOP)) {
					legendStyle = getLayoutStyleCss(new Rectangle((newLayout.x), (newLayout.y - 50), newLayout.width, 50), "text-align:center;");
				}
				else if(opt.getLegendLayout().equals(VanillaMapOption.LAYOUT_LEFT)) {
					legendStyle = getLayoutStyleCss(new Rectangle((newLayout.x - 150), newLayout.y, 150, newLayout.height), "display: table-cell;vertical-align:middle;");
				}
				else {
					legendStyle = getLayoutStyleCss(new Rectangle((newLayout.x + newLayout.width), newLayout.y, 150, newLayout.height), "display: table-cell;vertical-align:middle;");
				}
				
				
				buf.append("<div id='" + map.getName() + "_legendWMS' " + legendStyle + ">\n");
				buf.append("</div>\n");
				
				buf.append(getComponentDefinitionDivStart(newLayout, map));
				
			}
			else {
				buf.append(getComponentDefinitionDivStart(layout, map));
			}
			
			
			buf.append("<script id='scriptWms'>\n");
		}
		
		buf.append("var fromProjection = new OpenLayers.Projection(\"EPSG:4326\");\n");
		buf.append("var toProjection = new OpenLayers.Projection(\"EPSG:900913\");\n");
		
		//create the map, projection, controls and extent objects
		createMapElements(buf, map);
		HashMap<VanillaMapDataSerie, List<OsmValue>> values = null;
		try {
			values = readValues(map, datas, state.getDashInstance(), level, parentValue);
			Logger.getLogger(getClass()).debug("Found " + values.size() + " zones");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//create the base layer (the raster, can be WMTS, OSM)
		createBaseLayer(buf);
		
		//create the vector layer
		if(values != null) {
			createVectorLayer(buf, map, values, level, parentValue, state.getDashInstance());
		}
		
		
		
		buf.append("	map" + map.getName() + ".addLayers(layers);\n");
		buf.append("	map" + map.getName() + ".zoomToMaxExtent();\n");

		buf.append("	map" + map.getName() + ".setCenter(new OpenLayers.LonLat("+mapData.getMap().getOriginLong() + "," + mapData.getMap().getOriginLat() + ").transform( fromProjection, toProjection), " + mapData.getMap().getZoom() + ");\n");
		
		if(!opt.isShowBaseLayer()) {
			buf.append("	baseOsmLayer.setVisibility(false);\n");
		}
		
		if(!refresh) {
			buf.append("</script>\n");
			buf.append(getComponentDefinitionDivEnd());
			
			buf.append(getJavaScriptFdObjectVariable(map));
		}
		
		if(state.getComponentValue("exportdashboard") != null && Boolean.parseBoolean(state.getComponentValue("exportdashboard"))) {
			
			buf.append("<script id='exportscript'>\n");
			buf.append("	 var mapImageData;\n");
			
			buf.append("	baseOsmLayer.events.register(\"loadend\", baseOsmLayer, function() { \n"); 
			
	//		buf.append("	 var transform=$(\".gm-style>div:first>div\").css(\"transform\");\n");
	//		buf.append("	 var comp=transform.split(\",\"); //split up the transform matrix\n");
	//		buf.append("	 var mapleft=parseFloat(comp[4]); //get left value\n");
	//		buf.append("	 var maptop=parseFloat(comp[5]);  //get top value\n");
	//		buf.append("	 $(\".gm-style>div:first>div\").css({ //get the map container. not sure if stable\n");
	//		buf.append("	   \"transform\":\"none\",\n");
	//		buf.append("	   \"left\":mapleft,\n");
	//		buf.append("	   \"top\":maptop,\n");
	//		buf.append("	 });\n");
			
			buf.append("	html2canvas(document.getElementById('"+map.getName()+"'), {\n");
	//		buf.append("	layer_vectors_jeu_countries.events.register(\"loadend\", layer_vectors_jeu_countries, function() {  html2canvas(document.getElementById('"+map.getName()+"'), {\n");
			buf.append("	     useCORS: true,\n");
			buf.append("	     onrendered: function(canvas) {\n");
			
			buf.append("	     var ctx = canvas.getContext(\"2d\");\n");
			buf.append("		 var data = document.getElementById('"+map.getName()+"').getElementsByTagName('svg')[0];\n");
			buf.append("		 var xml = new XMLSerializer().serializeToString(data);\n");
			buf.append("		 var svg64 = btoa(xml);\n");
			buf.append("		 var b64Start = 'data:image/svg+xml;base64,';\n");
			buf.append("		 var image64 = b64Start + svg64;\n");
	//		buf.append("		 var DOMURL = window.URL || window.webkitURL || window;\n");
			buf.append("		 var img = new Image();\n");
	//		buf.append("		 var svg = new Blob([data], {type: \"image/svg+xml;charset=utf-8\"});\n");
	//		buf.append("		 var url = DOMURL.createObjectURL(svg);\n");
			buf.append("		 img.onload = function() {\n");
			buf.append("		 	ctx.drawImage(img, 0, 0);\n");
			buf.append("	        mapImageData = canvas.toDataURL('image/png');\n");
	//		buf.append("	       $(\".gm-style>div:first>div\").css({\n");
	//		buf.append("	         left:0,\n");
	//		buf.append("	         top:0,\n");
	//		buf.append("	         \"transform\":transform\n");
	//		buf.append("	       });\n");
//			buf.append("	 		var imgMap = document.createElement('img');\n");
//			buf.append("	  		imgMap.src=mapImageData;\n");
//			buf.append("	  		document.getElementById('"+map.getName()+"').appendChild(imgMap);\n");
			buf.append("	  		document.getElementById('"+map.getName()+"').innerHTML = '<img src=\"' + mapImageData + '\"/>';\n");
	//		buf.append("		     DOMURL.revokeObjectURL(url);\n");
			buf.append("		 };\n");
			buf.append("		 img.src = image64;\n");
	
			
			buf.append("	 }\n");
			buf.append("	 });});\n");
			
			
	//		buf.append("	 map1.once('postcompose', function(event) {\n");
	//		buf.append("	 	var canvas = event.context.canvas;\n");
	//		buf.append("	 	mapImageData = canvas.toDataURL('image/png');\n");
	//		buf.append("	 	var imgMap = document.createElement('img');\n");
	//		buf.append("	  	imgMap.src=mapImageData;\n");
	//		
	//		buf.append("	  	document.getElementById('"+map.getName()+"').appendChild(imgMap);\n");
	//		buf.append("	  });\n");
	//		buf.append("	  map1.renderSync();");
			
	
			buf.append("</script>\n");
		}
		
		return buf.toString();
	}
	
	private HashMap<String, String> valueColors = new HashMap<String, String>();
	
	private void createVectorLayer(StringBuffer buf, ComponentOsmMap map, HashMap<VanillaMapDataSerie, List<OsmValue>> values, String level, String parentValue, DashInstance dashInstance) {
		
		OsmMapOptions opt = (OsmMapOptions) map.getOptions(OsmMapOptions.class);
		
		for(VanillaMapDataSerie serie : values.keySet()) {
			
			//for polygon/lines
			if(serie.getType().equals("polygon") || serie.getType().equals("line")) {
				
				//events
				buf.append("		var layerListeners_" + serie.getName() + " = {\n");
				
				//TODO find a way to determine if drillable
				
				buf.append("			    featureclick: function(e) {\n");
				buf.append("			        var feat =  e.feature;\n");
				buf.append("			        var param = '" + serie.getName() + ";' +  feat.data;\n");
				buf.append("			        setParameter('" + map.getName() + "', param, true);\n");
				buf.append("			        return false;\n");
				buf.append("			    },\n");
				
				
				
				buf.append("			    nofeatureclick: function(e) {\n");
				buf.append("			    },\n");
				buf.append("			    featureover: function(e) {\n");
				buf.append("			    	var feat =  e.feature;\n");
				buf.append("			    	if(feat.popup) {\n");
				buf.append("			    		return;\n");
				buf.append("			    	}\n");
				buf.append("			    	var content = feat.dataLabel + ' ' + feat.dataVal;\n");
				buf.append("			    	popup = new OpenLayers.Popup.FramedCloud('chicken', \n");
				buf.append("			    		 feat.geometry.getBounds().getCenterLonLat(),\n");
				buf.append("			    		 new OpenLayers.Size(100,100),\n");
				buf.append("			    		 content,\n");
				buf.append("			    		 null, false, null);\n");
				buf.append("			    	feat.popup = popup;\n");
				buf.append("			    	map" + map.getName() + ".addPopup(popup);\n");
				buf.append("			    },\n");
				buf.append("			    featureout: function(e) {\n");
				buf.append("			    	var feature = e.feature;\n");
				buf.append("			    	if(feature.popup) {\n");
				buf.append("			    		map" + map.getName() + ".removePopup(feature.popup);\n");
				buf.append("			    		feature.popup.destroy();\n");
				buf.append("			    		delete feature.popup;\n");
				buf.append("			    	}\n");
				buf.append("			    }\n");
				buf.append("			};\n");
				
				//layer creation
				buf.append("		var layer_vectors_" + serie.getName() + " = new OpenLayers.Layer.Vector(\"" + serie.getName() + "\", { displayInLayerSwitcher: true, eventListeners: layerListeners_" + serie.getName() + "} );\n");
				
				//create the features
				int valIndex = 0;
				HashMap<String, Double> zoneValForColors = new HashMap<String, Double>();
				
				for(OsmValue val : values.get(serie)) {	
					if(val.getValue() == null) {
						continue;
					}
					buf.append("		var pointList" + serie.getName() + valIndex + " = [];\n");
					
					for(int p = 0 ; p < val.getLatitudes().size() ; p++) {
						zoneValForColors.put("polygonFeature" + serie.getName() + valIndex + ";" + val.getZoneLabel(), val.getValue());

						buf.append("		var newPoint" + (valIndex) + "" + (p) + " = new OpenLayers.Geometry.Point(" + val.getLongitudes().get(p) + ",");
						buf.append("" + val.getLatitudes().get(p) + ").transform( fromProjection, toProjection);\n");
						buf.append("		pointList" + serie.getName() + valIndex + ".push(newPoint" + (valIndex) + "" + (p) + ");\n");
					}
					
					if(serie.getType().equals("polygon")) {
						buf.append("		var linearRing" + serie.getName() + valIndex + " = new OpenLayers.Geometry.LinearRing(pointList" + serie.getName() + valIndex + ");\n");
						buf.append("		var polygonFeature" + serie.getName() + valIndex + " = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing" + serie.getName() + valIndex + "]));\n");
					}
					else {
						buf.append("		var polygonFeature" + serie.getName() + valIndex + " = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(pointList" + serie.getName() + valIndex + "));\n");
					}
					
					buf.append("		polygonFeature" + serie.getName() + valIndex + ".data = '" + val.getZoneId().replace("'", "&apos;") + "';\n");
					buf.append("		polygonFeature" + serie.getName() + valIndex + ".dataLabel = '" + val.getZoneLabel().replace("'", "&apos;") + "';\n");
					if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
						DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
						buf.append("		polygonFeature" + serie.getName() + valIndex + ".dataVal = '" + format.format(val.getValue()) + "';\n");
					}
					else {
						buf.append("		polygonFeature" + serie.getName() + valIndex + ".dataVal = '" + val.getValue() + "';\n");
					}

					buf.append("		layer_vectors_" + serie.getName() + ".addFeatures(polygonFeature" + serie.getName() + valIndex + ");\n");
					
					valIndex++;
				}
				
				//determine the min/max values and the legend
				String color1 = serie.getMinColor();
				String color2 = serie.getMaxColor();
				
				double min = Double.MAX_VALUE;
				double max = 0;
				
				for(Double val : zoneValForColors.values()) {
					if(val < min) {
						min = val;
					}
					if(val > max) {
						max = val;
					}
				}
				
				if(opt.isShowLegend()) {
					String legendHtml = createLegend(map, opt, min, max, serie);
					String legendDiv = map.getName() + "_legendWMS";
					buf.append("		document.getElementById('" + legendDiv + "').innerHTML = \"" + legendHtml + "\";\n");
				}
				
				//add the styles
				List<Map.Entry<String, Double>> entries = new ArrayList<Map.Entry<String, Double>>(zoneValForColors.entrySet());
				Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
					public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b){
						return a.getValue().compareTo(b.getValue());
					}
				});
				zoneValForColors = new LinkedHashMap<String, Double>();
				for (Map.Entry<String, Double> entry : entries) {
					zoneValForColors.put(entry.getKey(), entry.getValue());
				}
				
				double rat = 1.0 / max;
				valIndex = -1;
				int actualIndex = -1;
				Double prev = null;
				for(String zone : zoneValForColors.keySet()) {
					valIndex++;				
					if(prev == null) {
						prev = zoneValForColors.get(zone);
						actualIndex++;
					}
					else {
						if(prev.doubleValue() != zoneValForColors.get(zone).doubleValue()) {
							actualIndex = valIndex;
						}
					}
					String hex = colorFromRatio(color1, color2, (0.0 + (zoneValForColors.get(zone).doubleValue() * rat)));
					
//					System.out.println(zone + " - " + rat + " - " + hex);

					String color = "#" + hex;	
					
					String[] zoneParts = zone.split(";");
					
					if(serie.getType().equals("polygon")) {
						buf.append("	" + zoneParts[0] + ".style = {fill: true, fillColor: \""+color+"\", fillOpacity:\"0.5\"};\n");
					}
					else {
						buf.append("	" + zoneParts[0] + ".style = {strokeColor: \""+color+"\", strokeWidth: 5};\n");
					}
				}
				
				buf.append("		layers.push(layer_vectors_" + serie.getName() + ");\n");
			}
				
			else {
				
				buf.append("		var markers = new OpenLayers.Layer.Markers( \"" + serie.getName() + "\" );\n");
				
				double min = Double.MAX_VALUE;
				double max = 0;
				
				for(OsmValue val : values.get(serie)) {
					if(val.getValue() == null) {
						continue;
					}
					if(val.getValue() < min) {
						min = val.getValue();
					}
					if(val.getValue() > max) {
						max = val.getValue();
					}
				}
				
				if(opt.isShowLegend()) {
					String legendHtml = createLegend(map, opt, min, max, serie);
					String legendDiv = map.getName() + "_legendWMS";
					buf.append("		document.getElementById('" + legendDiv + "').innerHTML = \"" + legendHtml + "\";\n");
				}
				
				double valuesOffset = (max - min);
				if(valuesOffset == 0.0) {
					valuesOffset = 1.0;
				}
				double settingOffset = serie.getMaxMarkerSize() - serie.getMinMarkerSize();
				
				int j = 1;
				
				for(OsmValue val : values.get(serie)) {
					if(val.getValue() == null) {
						continue;
					}
					int size = 10;
					if( serie.getMaxMarkerSize() == serie.getMinMarkerSize()) {
						size = serie.getMinMarkerSize();
					}
					else {
						size = (int) ((settingOffset * (val.getValue() - min)) / valuesOffset + serie.getMinMarkerSize());
//						size = (int) (serie.getMinMarkerSize() + (val.getValue() / ((settingOffset) * valuesOffset)));
						
//						size = (int) (settingOffset * val.getValue() / valuesOffset);
//						Math.log(size);
					}
					
					if(serie.isUseColsForColors() || (serie.getMarkerColors() != null && !serie.getMarkerColors().isEmpty())) {
						buf.append("		var size = new OpenLayers.Size(" + settingOffset + ", "+ settingOffset + ");\n");
						buf.append("		var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);\n");
//						if(serie.getMapDataset().getMarkerUrl() != null && ! serie.getMapDataset().getMarkerUrl().isEmpty()) {
//							String url = dashInstance.getDashBoard().getCtx().getVanillaContext().getVanillaUrl().toLowerCase().replace("vanillaruntime", "");
//	//						String url = "http://localhost:8080";
//							if(!url.endsWith("/")) {
//								url += "/";
//							}
//							buf.append("		var icon = new OpenLayers.Icon('" + url + serie.getMapDataset().getMarkerUrl() + "',size,offset);\n");
//						}
//						else {
							buf.append("		var icon = new OpenLayers.Icon('/freedashboardRuntime/js/alpha100.png',size,offset);\n");
//						}
						
						buf.append("		var marker"+ serie.getName() + j +" = new OpenLayers.Marker(new OpenLayers.LonLat(" + val.getLongitudes().get(0) + "," + val.getLatitudes().get(0) + ").transform( fromProjection, toProjection), icon);\n");
						buf.append("		markers.addMarker(marker"+ serie.getName() + j +");\n");
						
						//XXX
						if(serie.isUseColsForColors()) {
							String columnLabel = dashInstance.getState().getComponentValue(map.getName() + "_colcolors");
							int colIndex = -1;
							for(ColumnDescriptor desc : map.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors()) {
								if(desc.getColumnLabel().equals(columnLabel)) {
									colIndex = desc.getColumnIndex();
									break;
								}
							}
							String valueColor = val.getOtherFields().get(colIndex);
							if(valueColors.get(valueColor) == null) {
								valueColors.put(valueColor, generateColor(valueColor));
							}
							buf.append("		icon.imageDiv.style = 'background-color:" + valueColors.get(valueColor) + ";';");
						}
						else {
							buf.append("		icon.imageDiv.style = 'background-color:" + getColorInRanges(val.getValue(), serie.getMarkerColors()) + ";';");
						}
//						buf.append("		icon.imageDiv.className += \" diamondShield\";");
//						buf.append("		icon.imageDiv.class += \" diamondShield\";");
					}
					else {
					
						buf.append("		var size = new OpenLayers.Size(" + size + ", "+ size + ");\n");
						buf.append("		var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);\n");
						if(serie.getMapDataset().getMarkerUrl() != null && ! serie.getMapDataset().getMarkerUrl().isEmpty()) {
							String url = dashInstance.getDashBoard().getCtx().getVanillaContext().getVanillaUrl().toLowerCase().replace("vanillaruntime", "");
	//						String url = "http://localhost:8080";
							if(!url.endsWith("/")) {
								url += "/";
							}
							buf.append("		var icon = new OpenLayers.Icon('" + url + serie.getMapDataset().getMarkerUrl() + "',size,offset);\n");
						}
						else {
							buf.append("		var icon = new OpenLayers.Icon('http://dev.openlayers.org/img/marker.png',size,offset);\n");
						}
						
						buf.append("		var marker"+ serie.getName() + j +" = new OpenLayers.Marker(new OpenLayers.LonLat(" + val.getLongitudes().get(0) + "," + val.getLatitudes().get(0) + ").transform( fromProjection, toProjection), icon);\n");
						buf.append("		markers.addMarker(marker"+ serie.getName() + j +");\n");
					
					}
					
					if(serie.getTargetPageName() != null) {
						
						FdModel	modelPage = null;
						for(FdModel model : ((MultiPageFdProject)dashInstance.getDashBoard().getProject()).getPagesModels()) {
							if(model.getName().equals(serie.getTargetPageName())) {
								modelPage = model;
								break;
							}
						}
						
						
						buf.append("		marker"+ serie.getName() + j + ".events.register(\"click\", marker"+ serie.getName() + j  +", function(e){\n");
						buf.append("		setParameter('" + map.getName() + "','"+val.getZoneId()+"');\n");
						try {
							int width = 500;
							int height = 500;
							for(IBaseElement elem : modelPage.getContent()) {
								if(elem instanceof Cell) {
									width = ((Cell)elem).getSize().x + ((Cell)elem).getPosition().x;
									height = ((Cell)elem).getSize().y + ((Cell)elem).getPosition().y + 8;
								}
							}
							
							buf.append("			popupModelPage('" + URLEncoder.encode(modelPage.getName(), "UTF-8") + "','" + width + "','" + height + "');");
						} catch(Exception e) {
						}
						buf.append("		}); \n");
						buf.append("		icon.imageDiv.className += \" cursorPointer\";\n");
					}
					
						
					if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
						DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
						buf.append("		marker"+ serie.getName() + j +".icon.imageDiv.title = \"" + val.getZoneLabel().replace("'", "&apos;") + " -> " + format.format(val.getValue()) + "\";\n");
					}
					else {
						buf.append("		marker"+ serie.getName() + j +".icon.imageDiv.title = \"" + val.getZoneLabel().replace("'", "&apos;") + " -> " + val.getValue() + "\";\n");
					}
				
					j++;
				}
				
				buf.append("		layers.push(markers);\n");
			}
			
		}
	}

	private String generateColor(String valueColor) {
		Random r = new Random();
		float red = r.nextFloat();
		float green = r.nextFloat();
		float blue = r.nextFloat();
		
		Color color = new Color(red, green, blue); 
		
		String res =  Integer.toHexString(color.getRGB());
		if(res.length() > 6) {
			return "#" + res.substring(res.length() - 6, res.length());
		}
		return "#" + res;
	}

	private String getColorInRanges(double value, List<ColorRange> markerColors) {
		for(ColorRange c : markerColors) {
			if(c.getMin() <= value && c.getMax() >= value) {
				return "#" + c.getHex();
			}
		}
		return "#FFFFFF";
	}

	private String colorFromRatio(String color1, String color2, double ratio) {
		int r = (int) Math.ceil(Integer.parseInt(color2.substring(0,2), 16) * ratio + Integer.parseInt(color1.substring(0,2), 16) * (1-ratio));
		int g = (int) Math.ceil(Integer.parseInt(color2.substring(2,4), 16) * ratio + Integer.parseInt(color1.substring(2,4), 16) * (1-ratio));
		int b = (int) Math.ceil(Integer.parseInt(color2.substring(4,6), 16) * ratio + Integer.parseInt(color1.substring(4,6), 16) * (1-ratio));
		
		String hexR = Integer.toHexString(r);
		if(hexR.length() == 1) {
			hexR = "0" + hexR;
		}
		if(hexR.length() == 3) {
			hexR = "ff";
		}
		String hexG = Integer.toHexString(g);
		if(hexG.length() == 1) {
			hexG = "0" + hexG;
		}
		if(hexG.length() == 3) {
			hexG = "ff";
		}
		String hexB = Integer.toHexString(b);
		if(hexB.length() == 1) {
			hexB = "0" + hexB;
		}
		if(hexB.length() == 3) {
			hexB = "ff";
		}
		
		return hexR + hexG + hexB;	
	}

	private String createLegend(ComponentOsmMap map, OsmMapOptions opt, double min, double max, VanillaMapDataSerie serie) {
		
		StringBuilder buf = new StringBuilder();
		
		int excess = (map.getWidth() - 300) / 150;
		
		if(serie.getType().equals("polygon") || serie.getType().equals("line")) {
			 //first element
			String color = serie.getMinColor();
				
			buf.append("<div style='display:inline-block;margin: 10px;'><div style='height: 20px; width: 20px; background-color: #" + color + ";float:left;'>");
			buf.append("</div>");
			if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
				DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
				buf.append("<label style='margin-left: 10px;'>" + format.format(min) + "</label></div>");
			}
			else {
				buf.append("<label style='margin-left: 10px;'>" + (int)min + "</label></div>");
			}
			
			//excess
			for(int i = 1 ; i <= excess ; i++) {
				
				double val = (min + ((max - min) *((double)i/(double)(excess + 1))) ); 
				
				double ratio;
				try {
					ratio = (val - min) / (max - min);
				} catch (Exception e) {
					ratio = 0;
				}
				
				color = colorFromRatio(serie.getMinColor(), serie.getMaxColor(), ratio);
				
				buf.append("<div style='display:inline-block;margin: 10px;'><div style='height: 20px; width: 20px; background-color: #" + color + ";float:left;'>");
				buf.append("</div>");
				if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
					DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
					buf.append("<label style='margin-left: 10px;'>" + format.format(val) + "</label></div>");
				}
				else {
					buf.append("<label style='margin-left: 10px;'>" + (int)val + "</label></div>");
				}
				
			}
			
			//last 
			color = serie.getMaxColor();
			
			buf.append("<div style='display:inline-block;margin: 10px;'><div style='height: 20px; width: 20px; background-color: #" + color + ";float:left;'>");
			buf.append("</div>");
			if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
				DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
				buf.append("<label style='margin-left: 10px;'>" + format.format(max) + "</label></div>");
			}
			else {
				buf.append("<label style='margin-left: 10px;'>" + (int)max + "</label></div>");
			}
		}
		else {
			
		}
		
		return buf.toString();
	}

	private HashMap<VanillaMapDataSerie, List<OsmValue>> readValues(ComponentOsmMap map, IResultSet datas, DashInstance dashInstance, String level, String parentValue) throws Exception {
		
		HashMap<VanillaMapDataSerie, HashMap<String, OsmValue>> result = new HashMap<VanillaMapDataSerie, HashMap<String, OsmValue>>();
		
		VanillaMapData data = (VanillaMapData) map.getDatas();
		//finding the serie to render
		List<VanillaMapDataSerie> series = new ArrayList<VanillaMapDataSerie>();
		if(level != null && !level.isEmpty()) {
			VanillaMapDataSerie parent = null;
			for(VanillaMapDataSerie serie : data.getSeries()) {
				if(serie.getName().equals(level)) {
					parent = serie;
					break;
				}
			}
			for(VanillaMapDataSerie serie : data.getSeries()) {
				if(serie.isDisplay() && (serie.getMapDataset().getParent() != null && serie.getMapDataset().getParentId() != null && serie.getMapDataset().getParentId().intValue() == parent.getMapDatasetId().intValue())) {
					series.add(serie);
				}
			}
		}
		else {
			for(VanillaMapDataSerie serie : data.getSeries()) {
				if(serie.isDisplay() && (serie.getMapDataset().getParentId() == null || serie.getMapDataset().getParentId().intValue() <= 0)) {
					series.add(serie);
				}
			}
		}
		
		Date start = new Date();
		
		//get the geographic data for the series
		for(VanillaMapDataSerie serie : series) {
			HashMap<String, OsmValue> values = getGeographicData(serie, map, parentValue, true);
			result.put(serie, values);
		}
		
		Logger.getLogger(getClass()).debug("Time to get geo data : " + (new Date().getTime() - start.getTime()));
		
		//map the values
		int valueWithNoZone = 0;
		
		start = new Date();
		int count = datas.getMetaData().getColumnCount();
		
		while(datas.next()) {
			double value = datas.getDouble(data.getValueFieldIndex());
			String zoneId = datas.getString(data.getZoneFieldIndex());
			
			LOOP:for(VanillaMapDataSerie serie : result.keySet()) {
				
				//TODO this loop, we might have to find something better
				for(String resultId : result.get(serie).keySet()) {
					OsmValue val = result.get(serie).get(resultId);
					if(val.getLastLevelIds().contains(zoneId)) {
						val.setValue(value);
						for(int i = 1 ; i < count ; i++) {
							if(i != data.getValueFieldIndex().intValue() && i != data.getZoneFieldIndex().intValue()) {
								val.getOtherFields().put(i, datas.getString(i));
							}
						}
						continue LOOP;
					}
				}
				
				valueWithNoZone++;
			}
		}
		
		Logger.getLogger(getClass()).debug("Time to map data : " + (new Date().getTime() - start.getTime()));
		
		Logger.getLogger(getClass()).debug("Found " + valueWithNoZone + " values with no matching zone");
		
		HashMap<VanillaMapDataSerie, List<OsmValue>> resultValues = new HashMap<VanillaMapDataSerie, List<OsmValue>>();
		
		for(VanillaMapDataSerie serie : result.keySet()) {
			resultValues.put(serie, new ArrayList<OsmValue>(result.get(serie).values()));
		}
		
		return resultValues;
	}

	private HashMap<String, OsmValue> getGeographicData(VanillaMapDataSerie serie, ComponentOsmMap map, String parentValue, boolean first) throws Exception {
		
		HashMap<String, OsmValue> values = new HashMap<String, OsmValue>();
		
		
		//if the dataset as children
		List<VanillaMapDataSerie> children = new ArrayList<VanillaMapDataSerie>();
		for(VanillaMapDataSerie s : ((VanillaMapData)map.getDatas()).getSeries()) {
			
			if(s.isDisplay() && s.getMapDataset().getParentId() != null && s.getMapDataset().getParentId().intValue() == serie.getMapDataset().getId()) {
				children.add(s);
			}
		}
		
		MapDataSource datasource = serie.getMapDataset().getDataSource();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = ConnectionManager.getInstance().getJdbcConnection(datasource.getUrl(), datasource.getLogin(), datasource.getMdp(), datasource.getDriver());
			stmt = connection.createStatement();
			rs = stmt.executeQuery(serie.getMapDataset().getQuery());
			
			while(rs.next()) {
				String zoneId = rs.getString(serie.getMapDataset().getIdZone());
				String latitude = rs.getString(serie.getMapDataset().getLatitude());
				String longitude = rs.getString(serie.getMapDataset().getLongitude());
				String label = zoneId;
				try {
					label = rs.getString(serie.getMapDataset().getZoneLabel());
					if(label == null) {
						label = zoneId;
					}
				} catch(Exception e) {
				}
				
				String parentId = null;
				if(serie.getMapDataset().getParentId() != null) {
					parentId = rs.getString(serie.getMapDataset().getParent());
				}
				
				boolean getZone = true;
				if(first && parentValue != null && !parentValue.isEmpty()) {
					if(parentId == null) {
						getZone = false;
					}
					else if(!parentId.equals(parentValue)) {
						getZone = false;
					}
					
				}
				
				if(getZone) {
					OsmValue val = values.get(zoneId);
					if(val == null) {
						val = new OsmValue();
						val.setZoneId(zoneId);
						val.setZoneLabel(label);
						val.setParentId(parentId);
						
						//trick to get the lastLevelIds in upper levels
						if(children.isEmpty()) {
							val.getLastLevelIds().add(zoneId);
						}
						values.put(zoneId, val);
					}
					val.getLatitudes().add(latitude);
					val.getLongitudes().add(longitude);
				}
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(connection != null) {
				ConnectionManager.getInstance().returnJdbcConnection(connection);
			}
		}

		if(!children.isEmpty()) {
			for(VanillaMapDataSerie s : children) {
				HashMap<String, OsmValue> childRes = getGeographicData(s, map, parentValue, false);
				
				//browse to match parent/child
				for(OsmValue val : childRes.values()) {
					OsmValue parentVal = values.get(val.getParentId());
					if(parentVal != null) {
						parentVal.getLastLevelIds().addAll(val.getLastLevelIds());
					}
				}
			}
		}
		
		return values;
	}

	private void createBaseLayer(StringBuffer buf) {
		buf.append("	var baseOsmLayer = new OpenLayers.Layer.OSM();\n");
		buf.append("	layers.push(baseOsmLayer);\n");
	}
	
	private void createMapElements(StringBuffer buf, ComponentOsmMap map) {
		VanillaMapData data = (VanillaMapData) map.getDatas();
		MapVanilla opt = data.getMap();
		buf.append("	var extent = new OpenLayers.Bounds("+opt.getBoundLeft() + "," + opt.getBoundBottom() + "," + opt.getBoundRight() + "," + opt.getBoundTop() +").transform( fromProjection, toProjection);\n");
		buf.append("	var controls = [\n");
		buf.append("		new OpenLayers.Control.Navigation(),\n");
		buf.append("		new OpenLayers.Control.PanZoomBar(),\n");
		buf.append("		new OpenLayers.Control.MousePosition(),\n");
		buf.append("		new OpenLayers.Control.LayerSwitcher(),\n");
		buf.append("		new OpenLayers.Control.KeyboardDefaults()\n");
		buf.append("		];\n");
		buf.append("	var	proj = new OpenLayers.Projection(\""+opt.getProjection().toUpperCase()+"\");\n");
		buf.append("	var map" + map.getName() + " = new OpenLayers.Map({\n");
		buf.append("		div: \""+map.getName()+"\",\n");
		buf.append("		maxExtent : extent,\n");
		buf.append("		controls: controls,\n");
		buf.append("		projection: proj,\n");
		buf.append("		maxResolution: 256\n");
		buf.append("	});\n");
		buf.append("	var layers = [];\n");
	}

	@Override
	public String getJavaScriptFdObjectVariable(ComponentOsmMap definition) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + definition.getName() + "\"]= new FdWMSMap(\"" + definition.getName() + "\")" + ";\n");
		
		buf.append("</script>\n");
		return buf.toString();
	}

}
