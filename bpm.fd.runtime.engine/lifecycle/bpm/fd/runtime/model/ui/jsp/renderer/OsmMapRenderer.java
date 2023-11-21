package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmData;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerie;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerieLine;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSerieMarker;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmDataSeriePolygon;
import bpm.fd.api.core.model.components.definition.maps.openlayers.OsmMapOptions;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.DashState;
import bpm.fd.runtime.model.datas.map.OsmValue;

public class OsmMapRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentOsmMap>{

	@Override
	public String getHTML(Rectangle layout, ComponentOsmMap map, DashState state, IResultSet datas, boolean refresh) {
		
		
		
		Date start = new Date();
		
		OsmMapOptions opt = (OsmMapOptions)map.getOptions(OsmMapOptions.class);
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
			
			if(opt.isShowLegend()) {
				Rectangle newLayout = new Rectangle();
				newLayout.x = layout.x;
				newLayout.y = layout.y;
				
				if(opt.getLegendOrientation().equals(OsmMapOptions.ORIENTATION_VERTICAL)) {
					if(opt.getLegendLayout().equals(OsmMapOptions.LAYOUT_LEFT)) {
						newLayout.x = layout.x + 150;
						newLayout.width = layout.width - 150;
					}
					else {
						newLayout.width = layout.width - 150;
					}
					
					newLayout.height = layout.height;
					
				}
				else {
					if(opt.getLegendLayout().equals(OsmMapOptions.LAYOUT_BOTTOM)) {
						newLayout.height = layout.height - 50;
					}
					else {
						newLayout.height = layout.height - 50;
						newLayout.y = layout.y + 50;
					}
					newLayout.width = layout.width;
				}
				String legendStyle = "";
				if(opt.getLegendLayout().equals(OsmMapOptions.LAYOUT_BOTTOM)) {				
					legendStyle = getLayoutStyleCss(new Rectangle((newLayout.x), (newLayout.y + newLayout.height), newLayout.width, 50), "text-align:center;");
				}
				else if(opt.getLegendLayout().equals(OsmMapOptions.LAYOUT_TOP)) {
					legendStyle = getLayoutStyleCss(new Rectangle((newLayout.x), (newLayout.y - 50), newLayout.width, 50), "text-align:center;");
				}
				else if(opt.getLegendLayout().equals(OsmMapOptions.LAYOUT_LEFT)) {
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
		createMapElements(buf, opt, map);
		HashMap<OsmDataSerie, List<OsmValue>> values = null;
		try {
			values = readValues(map, datas, state.getDashInstance(), level, parentValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//create the base layer (the raster, can be WMTS, OSM)
		createBaseLayer(buf, opt, map);
		
		//create the vector layer
		if(values != null) {
			createVectorLayer(buf, opt, map, values, level, parentValue);
		}
		
		
		buf.append("	map1.addLayers(layers);\n");
		buf.append("	map1.zoomToMaxExtent();\n");

		buf.append("	map1.setCenter(new OpenLayers.LonLat("+opt.getOriginLong() + "," + opt.getOriginLat() + ").transform( fromProjection, toProjection), " + opt.getZoom() + ");\n");
		
		if(!opt.isShowBaseLayer()) {
			buf.append("	baseOsmLayer.setVisibility(false);\n");
		}
		
		if(!refresh) {
			buf.append("</script>\n");
			buf.append(getComponentDefinitionDivEnd());
			
			buf.append(getJavaScriptFdObjectVariable(map));
		}
		
//		System.out.println(buf.toString());
		
		Date endDate = new Date();
		
		System.out.println("map generation : " + (endDate.getTime() - start.getTime()));
		
		return buf.toString();
	}

	private void createVectorLayer(StringBuffer buf, OsmMapOptions opt, ComponentOsmMap map, HashMap<OsmDataSerie, List<OsmValue>> values, String level, String parentValue) {
		int i = 1;
		for(OsmDataSerie serie : values.keySet()) {
			if(((serie.getParentSerie() == null || serie.getParentSerie().isEmpty()) && level == null) || serie.getParentSerie().equals(level)) {
				if(serie instanceof OsmDataSeriePolygon) {
					
//					if(serie.isDrillable()) {
					
						buf.append("		var layerListeners = {\n");
						if(serie.isDrillable()) {
							buf.append("			    featureclick: function(e) {\n");
							buf.append("			        var feat =  e.feature;\n");
	//						if(!serie.isDrillable()) {
	//							buf.append("			        var param = feat.data;\n");
	//						}
	//						else {
								buf.append("			        var param = '" + serie.getName() + ";' +  feat.data;\n");
	//						}	
							
								buf.append("			        setParameter('" + map.getName() + "', param, true);\n");
								buf.append("			        return false;\n");
							
							buf.append("			    },\n");
						}
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
						buf.append("			    		 null, true, null);\n");
						buf.append("			    	feat.popup = popup;\n");
						buf.append("			    	map1.addPopup(popup);\n");
						buf.append("			    },\n");
						buf.append("			    featureout: function(e) {\n");
						buf.append("			    	var feature = e.feature;\n");
						buf.append("			    	if(feature.popup) {\n");
						buf.append("			    		map1.removePopup(feature.popup);\n");
						buf.append("			    		feature.popup.destroy();\n");
						buf.append("			    		delete feature.popup;\n");
						buf.append("			    	}\n");
						buf.append("			    }\n");
						buf.append("			};\n");
						
						buf.append("		var layer_vectors" + serie.getName() + i + " = new OpenLayers.Layer.Vector(\"" + serie.getName() + "\", { displayInLayerSwitcher: true, eventListeners: layerListeners } );\n");
//					}
//					else {
//						buf.append("		var layer_vectors" + serie.getName() + i + " = new OpenLayers.Layer.Vector(\"" + serie.getName() + "\", { displayInLayerSwitcher: true} );\n");
//					}
										
					try {
						int j = 1;
						
						HashMap<String, Double> zoneValForColors = new HashMap<String, Double>();
						
						for(OsmValue val : values.get(serie)) {
							
							buf.append("		var pointList" + serie.getName() + j + " = [];\n");
							
							for(int p = 0 ; p < val.getLatitudes().size() ; p++) {
								zoneValForColors.put("polygonFeature" + serie.getName() + j + ";" + val.getZoneLabel(), val.getValue());
								if(p% ((OsmDataSeriePolygon)serie).getPoints() != 0) {
									continue;
								}
								buf.append("		var newPoint" + (j) + "" + (p) + " = new OpenLayers.Geometry.Point(" + val.getLongitudes().get(p) + ",");
								buf.append("" + val.getLatitudes().get(p) + ").transform( fromProjection, toProjection);\n");
								buf.append("		pointList" + serie.getName() + j + ".push(newPoint" + (j) + "" + (p) + ");\n");
							}
							
							buf.append("		var linearRing" + serie.getName() + j + " = new OpenLayers.Geometry.LinearRing(pointList" + serie.getName() + j + ");\n");
							buf.append("		var polygonFeature" + serie.getName() + j + " = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing" + serie.getName() + j + "]));\n");
							
							buf.append("		polygonFeature" + serie.getName() + j + ".data = '" + val.getZoneId().replace("'", "&apos;") + "';\n");
							buf.append("		polygonFeature" + serie.getName() + j + ".dataLabel = '" + val.getZoneLabel().replace("'", "&apos;") + "';\n");
							if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
								DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
								buf.append("		polygonFeature" + serie.getName() + j + ".dataVal = '" + format.format(val.getValue()) + "';\n");
							}
							else {
								buf.append("		polygonFeature" + serie.getName() + j + ".dataVal = '" + val.getValue() + "';\n");
							}
							
							
							
							buf.append("		layer_vectors" + serie.getName() + i + ".addFeatures(polygonFeature" + serie.getName() + j + ");\n");
							j++;
						}
						
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
						
						
						for(String zone : zoneValForColors.keySet()) {
							
							double ratio;
							try {
								ratio = (zoneValForColors.get(zone) - min) / (max - min);
							} catch (Exception e) {
								ratio = 0;
							}
							
							String hex = colorFromRatio(color1, color2, ratio);
							

							String color = "#" + hex;	
							
							String[] zoneParts = zone.split(";");
							
							buf.append("	" + zoneParts[0] + ".style = {fill: true, fillColor: \""+color+"\", fillOpacity:\"0.5\"};\n");
						}
						
						
						buf.append("		layers.push(layer_vectors" + serie.getName() + i + ");\n");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				}
				else if(serie instanceof OsmDataSerieLine) {
					//XXX
					
					buf.append("		var layerListeners = {\n");
					if(serie.isDrillable()) {
						buf.append("			    featureclick: function(e) {\n");
						buf.append("			        var feat =  e.feature;\n");
//						if(!serie.isDrillable()) {
//							buf.append("			        var param = feat.data;\n");
//						}
//						else {
							buf.append("			        var param = '" + serie.getName() + ";' +  feat.data;\n");
//						}	
						
							buf.append("			        setParameter('" + map.getName() + "', param, true);\n");
							buf.append("			        return false;\n");
						
						buf.append("			    },\n");
					}
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
					buf.append("			    		 null, true, null);\n");
					buf.append("			    	feat.popup = popup;\n");
					buf.append("			    	map1.addPopup(popup);\n");
					buf.append("			    },\n");
					buf.append("			    featureout: function(e) {\n");
					buf.append("			    	var feature = e.feature;\n");
					buf.append("			    	if(feature.popup) {\n");
					buf.append("			    		map1.removePopup(feature.popup);\n");
					buf.append("			    		feature.popup.destroy();\n");
					buf.append("			    		delete feature.popup;\n");
					buf.append("			    	}\n");
					buf.append("			    }\n");
					buf.append("			};\n");
					
					buf.append("		var layer_vectors" + serie.getName() + i + " = new OpenLayers.Layer.Vector(\"" + serie.getName() + "\", { displayInLayerSwitcher: true, eventListeners: layerListeners} );\n");
					
					
					try {
						int j = 1;
						
						HashMap<String, Double> zoneValForColors = new HashMap<String, Double>();
						
						for(OsmValue val : values.get(serie)) {
							
							buf.append("		var pointList" + serie.getName() + j + " = [];\n");
							
							for(int p = 0 ; p < val.getLatitudes().size() ; p++) {
								zoneValForColors.put("lineFeature" + serie.getName() + j + ";" + val.getZoneLabel(), val.getValue());
								buf.append("		var newPoint" + (j) + "" + (p) + " = new OpenLayers.Geometry.Point(" + val.getLongitudes().get(p) + ",");
								buf.append("" + val.getLatitudes().get(p) + ").transform( fromProjection, toProjection);\n");
								buf.append("		pointList" + serie.getName() + j + ".push(newPoint" + (j) + "" + (p) + ");\n");
							}
							
//							buf.append("		var linearRing" + serie.getName() + j + " = new OpenLayers.Geometry.LinearRing(pointList" + serie.getName() + j + ");\n");
							buf.append("		var lineFeature" + serie.getName() + j + " = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(pointList" + serie.getName() + j + "));\n");
							
							buf.append("		lineFeature" + serie.getName() + j + ".data = '" + val.getZoneId().replace("'", "&apos;") + "';\n");
							buf.append("		lineFeature" + serie.getName() + j + ".dataLabel = '" + val.getZoneLabel().replace("'", "&apos;") + "';\n");
							if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
								DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
								buf.append("		lineFeature" + serie.getName() + j + ".dataVal = '" + format.format(val.getValue()) + "';\n");
							}
							else {
								buf.append("		lineFeature" + serie.getName() + j + ".dataVal = '" + val.getValue() + "';\n");
							}
							
							
							
							buf.append("		layer_vectors" + serie.getName() + i + ".addFeatures(lineFeature" + serie.getName() + j + ");\n");
							j++;
						}
						
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
						
						double rat = 1.0 / (double)zoneValForColors.size();
						int valIndex = 0;
						for(String zone : zoneValForColors.keySet()) {
							
//							double ratio;
//							try {
//								ratio = (zoneValForColors.get(zone) - min) / (max - min);
//							} catch (Exception e) {
//								ratio = 0;
//							}
							
							String hex = colorFromRatio(color1, color2, (0.0 + (valIndex * rat)));
							

							String color = "#" + hex;	
							
							String[] zoneParts = zone.split(";");
							
							buf.append("	" + zoneParts[0] + ".style = {strokeColor: \""+color+"\", strokeWidth: 5};\n");
							valIndex++;
						}
						
						
						buf.append("		layers.push(layer_vectors" + serie.getName() + i + ");\n");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					//XXX
				}
				//point serie
				else {
					
					buf.append("		var markers = new OpenLayers.Layer.Markers( \"" + serie.getName() + "\" );\n");
					
					double min = Double.MAX_VALUE;
					double max = 0;
					
					for(OsmValue val : values.get(serie)) {
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
					
					int valuesOffset = (int) (max - min);
					int settingOffset = ((OsmDataSerieMarker)serie).getMaxMarkerSize() - ((OsmDataSerieMarker)serie).getMinMarkerSize();
					
					int j = 1;
					
					for(OsmValue val : values.get(serie)) {
						if(val.getLatitude() == null || val.getLatitude().isEmpty()) {
							continue;
						}
						int size = 10;
						if( ((OsmDataSerieMarker)serie).getMaxMarkerSize() == ((OsmDataSerieMarker)serie).getMinMarkerSize()) {
							size = ((OsmDataSerieMarker)serie).getMinMarkerSize();
						}
						else {
							size = (int) (settingOffset * val.getValue() / valuesOffset);
						}
						
						buf.append("		var size = new OpenLayers.Size(" + size + ", "+ size + ");\n");
						buf.append("		var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);\n");
						if(((OsmDataSerieMarker)serie).getMarkerUrl() != null && ! ((OsmDataSerieMarker)serie).getMarkerUrl().isEmpty()) {
							buf.append("		var icon = new OpenLayers.Icon('" + ((OsmDataSerieMarker)serie).getMarkerUrl() + "',size,offset);\n");
						}
						else {
							buf.append("		var icon = new OpenLayers.Icon('http://dev.openlayers.org/img/marker.png',size,offset);\n");
						}
						
						buf.append("		var marker"+ serie.getName() + j +" = new OpenLayers.Marker(new OpenLayers.LonLat(" + val.getLongitude() + "," + val.getLatitude() + ").transform( fromProjection, toProjection), icon);\n");
						buf.append("		markers.addMarker(marker"+ serie.getName() + j +");\n");
						
						//XXX
						if(((OsmDataSerieMarker)serie).isDrillable() && ((OsmDataSerieMarker)serie).getTargetPage() != null) {
							buf.append("		marker"+ serie.getName() + j + ".events.register(\"click\", marker"+ serie.getName() + j  +", function(e){\n");
							buf.append("		setParameter('" + map.getName() + "','"+val.getZoneId()+"');\n");
							try {
								int width = 500;
								int height = 500;
								for(IBaseElement elem : ((OsmDataSerieMarker)serie).getTargetPage().getContent()) {
									if(elem instanceof Cell) {
										width = ((Cell)elem).getSize().x + ((Cell)elem).getPosition().x;
										height = ((Cell)elem).getSize().y + ((Cell)elem).getPosition().y + 8;
									}
								}
								
								buf.append("			popupModelPage('" + URLEncoder.encode(((OsmDataSerieMarker)serie).getTargetPage().getName(), "UTF-8") + "','" + width + "','" + height + "');");
							} catch(UnsupportedEncodingException e) {
							}
							buf.append("		}); \n");
//							buf.append("		marker"+ serie.getName() + j + ".className = \"test\";\n");
							buf.append("		icon.imageDiv.className = \"cursorPointer\";\n");
//							buf.append("		icon.imageDiv.style = {cursor: \"pointer\"};\n");
						}
						
							
						if(opt.getNumberFormat() != null && !opt.getNumberFormat().isEmpty()) {
							DecimalFormat format = new DecimalFormat(opt.getNumberFormat());
							buf.append("		marker"+ serie.getName() + j +".icon.imageDiv.title = \"" + val.getZoneLabel().replace("'", "&apos;") + " -> " + format.format(val.getValue()) + "\";\n");
						}
						else {
							buf.append("		marker"+ serie.getName() + j +".icon.imageDiv.title = \"" + val.getZoneLabel().replace("'", "&apos;") + " -> " + val.getValue() + "\";\n");
//							System.out.println("\t\tmarker"+ serie.getName() + j +".icon.imageDiv.title = \"" + val.getZoneLabel().replace("'", "&apos;") + " -> " + val.getValue() + "\";\\n");
						}
					
						
					}
					
//					buf.append("		map1.addLayer(markers);\n");
					
					buf.append("		layers.push(markers);\n");
					
				}
				i++;
			}
		}
		
	}

	private String colorFromRatio(String color1, String color2, double ratio) {
		int r = (int) Math.ceil(Integer.parseInt(color2.substring(0,2), 16) * ratio + Integer.parseInt(color1.substring(0,2), 16) * (1-ratio));
		int g = (int) Math.ceil(Integer.parseInt(color2.substring(2,4), 16) * ratio + Integer.parseInt(color1.substring(2,4), 16) * (1-ratio));
		int b = (int) Math.ceil(Integer.parseInt(color2.substring(4,6), 16) * ratio + Integer.parseInt(color1.substring(4,6), 16) * (1-ratio));
		
		String hexR = Integer.toHexString(r);
		if(hexR.length() == 1) {
			hexR = "0" + hexR;
		}
		String hexG = Integer.toHexString(g);
		if(hexG.length() == 1) {
			hexG = "0" + hexG;
		}
		String hexB = Integer.toHexString(b);
		if(hexB.length() == 1) {
			hexB = "0" + hexB;
		}
		
		return hexR + hexG + hexB;	
	}

	private String createLegend(ComponentOsmMap map, OsmMapOptions opt, double min, double max, OsmDataSerie serie) {
		
		StringBuilder buf = new StringBuilder();
		
		int excess = (map.getWidth() - 300) / 150;
		
		if(serie instanceof OsmDataSeriePolygon) {
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

	private void createBaseLayer(StringBuffer buf, OsmMapOptions opt, ComponentOsmMap map) {
		buf.append("	var baseOsmLayer = new OpenLayers.Layer.OSM();\n");
		buf.append("	layers.push(baseOsmLayer);\n");
		
//		buf.append("	layers.push(new OpenLayers.Layer.OSM());\n");
	}

	private HashMap<OsmDataSerie, List<OsmValue>> readValues(ComponentOsmMap map, IResultSet datas, DashInstance dashInstance, String level, String parentValue) throws Exception {
		HashMap<OsmDataSerie, List<OsmValue>> values = new LinkedHashMap<OsmDataSerie, List<OsmValue>>();
		
		OsmData mapData = (OsmData) map.getDatas();
		
		//read the main dataset
		while(datas.next()) {
			double value = datas.getDouble(mapData.getValueFieldIndex());
			String zoneId = datas.getString(mapData.getZoneFieldIndex());
			String zoneLabelId = zoneId;
			try {
				zoneLabelId = datas.getString(mapData.getZoneFieldLabelIndex());
				
			} catch (Exception e1) {
			}
			
			for(OsmDataSerie serie : mapData.getSeries()) {
				if(values.get(serie) == null) {
					values.put(serie, new ArrayList<OsmValue>());
				}
				
				OsmValue val = new OsmValue();
				val.setValue(value);
				val.setZoneId(zoneId);
				val.setZoneLabel(zoneLabelId);
				
				if(serie instanceof OsmDataSerieMarker) {
					OsmDataSerieMarker marker = (OsmDataSerieMarker) serie;
					String latitude = datas.getString(marker.getLatitudeFieldIndex());
					String longitude = datas.getString(marker.getLongitudeFieldIndex());
					
					if(marker.getParentSerie() != null && !marker.getParentSerie().isEmpty() ) {
						if(!marker.getParentSerie().equals(level)) {
							continue;
						}
						String parentVal = datas.getString(marker.getParentIdFieldIndex());
						if(parentVal == null || parentVal.isEmpty() || !parentVal.equals(parentValue)) {
							continue;
						}
					}
					
					val.setLatitude(latitude);
					val.setLongitude(longitude);
				}
				
				values.get(serie).add(val);
			}
		}
		
		//read the polygon datasets and try to match with the values
		for(OsmDataSerie serie : mapData.getSeries()) {
			if(((serie.getParentSerie() == null || serie.getParentSerie().isEmpty()) && level == null) || serie.getParentSerie().equals(level)) {
				if(serie instanceof OsmDataSeriePolygon) {
					
					OsmDataSeriePolygon polygon = (OsmDataSeriePolygon) serie;
					
					List<OsmValue> polygonVals = values.get(polygon);
					
					IQuery query = dashInstance.getDashBoard().getQueryProvider().getQuery(dashInstance.getGroup(), polygon.getDataset());
					
					IResultSet resultSet = query.executeQuery();
					
					while(resultSet.next()) {
						boolean found = false;
						String zoneId = resultSet.getString(polygon.getZoneFieldIndex());
						
						String parentZone = null;
						if(polygon.getParentIdFieldIndex() != null && polygon.getParentIdFieldIndex() != 0) {
							parentZone = resultSet.getString(polygon.getParentIdFieldIndex());
						}
						
						for(OsmValue val : polygonVals) {
							if(val.getZoneId().equals(zoneId)) {
								
								if((parentZone == null && level == null) || (parentZone != null && parentZone.equals(parentValue))) {
								
									String latitude = resultSet.getString(polygon.getLatitudeFieldIndex());
									String longitude = resultSet.getString(polygon.getLongitudeFieldIndex());
									
									val.getLatitudes().add(latitude);
									val.getLongitudes().add(longitude);
									
									found = true;
									break;
								}
							}
						}
						
						if(!found) {
							
						}
						 
					}
					
					resultSet.close();
					query.close();
				}
				else if(serie instanceof OsmDataSerieLine) {
					
					OsmDataSerieLine line = (OsmDataSerieLine) serie;
					
					List<OsmValue> polygonVals = values.get(line);
					
					IQuery query = dashInstance.getDashBoard().getQueryProvider().getQuery(dashInstance.getGroup(), line.getDataset());
					
					IResultSet resultSet = query.executeQuery();
					
					while(resultSet.next()) {
						boolean found = false;
						String zoneId = resultSet.getString(line.getZoneFieldIndex());
						
						String parentZone = null;
						if(line.getParentIdFieldIndex() != null && line.getParentIdFieldIndex() != 0) {
							parentZone = resultSet.getString(line.getParentIdFieldIndex());
						}
						
						for(OsmValue val : polygonVals) {
							if(val.getZoneId().equals(zoneId)) {
								
								if((parentZone == null && level == null) || (parentZone != null && parentZone.equals(parentValue))) {
								
									String latitude = resultSet.getString(line.getLatitudeFieldIndex());
									String longitude = resultSet.getString(line.getLongitudeFieldIndex());
									
									val.getLatitudes().add(latitude);
									val.getLongitudes().add(longitude);
									
									found = true;
									break;
								}
							}
						}
						
						if(!found) {
							
						}
						 
					}
					
					resultSet.close();
					query.close();
				}
			}
		}
		
		return values;
	}

	private void createMapElements(StringBuffer buf, OsmMapOptions opt, ComponentOsmMap map) {
		buf.append("	var extent = new OpenLayers.Bounds("+opt.getBoundLeft() + "," + opt.getBoundBottom() + "," + opt.getBoundRight() + "," + opt.getBoundTop() +").transform( fromProjection, toProjection);\n");
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

	@Override
	public String getJavaScriptFdObjectVariable(ComponentOsmMap definition) {
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("    fdObjects[\"" + definition.getName() + "\"]= new FdWMSMap(\"" + definition.getName() + "\")" + ";\n");
		
		buf.append("</script>\n");
		return buf.toString();
	}

}
