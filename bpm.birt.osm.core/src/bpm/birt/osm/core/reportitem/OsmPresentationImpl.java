package bpm.birt.osm.core.reportitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.extension.IBaseResultSet;
import org.eclipse.birt.report.engine.extension.IQueryResultSet;
import org.eclipse.birt.report.engine.extension.ReportItemPresentationBase;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;
import org.eclipse.birt.report.model.api.metadata.DimensionValue;

import bpm.birt.osm.core.model.ColorRange;
import bpm.birt.osm.core.model.OsmSerie;
import bpm.birt.osm.core.model.OsmSerieGeometry;
import bpm.birt.osm.core.model.OsmSerieMarker;
import bpm.birt.osm.core.model.OsmValue;
import bpm.birt.osm.core.utils.GeographicDataHelper;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapHelper;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class OsmPresentationImpl extends ReportItemPresentationBase{

	private OsmReportItem item;
	private ExtendedItemHandle modelHandle;

	public void setModelObject( ExtendedItemHandle modelHandle )
	{
		try {
			this.modelHandle = modelHandle;
			item = (OsmReportItem) modelHandle.getReportItem( );
		} catch (ExtendedElementException e){
			e.printStackTrace();
		}
	}	
	
	public int getOutputType() {
		return OUTPUT_AS_HTML_TEXT;
	}
	
	@Override
	public Object onRowSets(IBaseResultSet[] results) throws BirtException {
		StringBuilder html = new StringBuilder();
		
		String mapName = new Object().hashCode() + "_map";
		
		DimensionValue width = (DimensionValue) modelHandle.getProperty("width");
		DimensionValue height = (DimensionValue) modelHandle.getProperty("height");
		
		String widthString = width.getMeasure() + width.getUnits();
		String heightString = height.getMeasure() + height.getUnits();
		
		html.append("<html>\n");
		html.append("    <head>\n");
		html.append("        <title>Osm Map</title>\n");
		html.append("        <meta content=\"text/html;charset=UTF-8\" http-equiv=\"Content-Type\">\n");
		html.append("        <script language=\"JavaScript\" src=\"" + item.getVanillaUrl() + "/js/vanilla_map.js" + "\"></script>\n");
		html.append("        <script language=\"JavaScript\" src=\"" + item.getVanillaUrl() + "/Ol465/ol.js" + "\"></script>\n");
//		html.append("        <link rel=\"stylesheet\" href=\"https://openlayers.org/en/v4.6.5/css/ol.css\" type=\"text/css\">");
//		html.append("        <link rel=\"stylesheet\" href=\"" + item.getVanillaUrl() + "/js/vanilla_map.css\"" +" type=\"text/css\">");
//		html.append("        <script language=\"JavaScript\" src=\"" + item.getVanillaUrl() + "/OpenLayers/OpenStreetMap.js" + "\"></script>\n");
		html.append("    </head>\n");
		html.append("    <body>\n");
		html.append("    	<div id=\"" + mapName + "\" style=\"width:" + widthString + "; height:" + heightString + ";\">\n");
		html.append("        </div>\n");
		
		html.append("        <div id=\"popup\" class=\"ol-popup\">\n");
		html.append("        <div id=\"popup-content\" />\n");
		html.append("        <div id=\"popup-closer\" class=\"ol-popup-closer\"/>\n");
		
		
		
		html.append("        <div id=\"pagination\"  class=\"ol-popup-pagination\" style=\"color: black;float: left;padding: 0px 16px;text-decoration: none;transition: background-color .3s;font-size: 23px;\">\n");
		html.append("        	<div id=\"previous\" style=\"color: black;float: left;padding: 0px 16px;text-decoration: none;transition: background-color .3s;font-size: 23px;\" />\n");
		html.append("        	<div id=\"pager\" style=\"float: left;padding: 6px 16px;text-decoration: none;transition: background-color .3s;background-color: dodgerblue;color: white;display: inline-block;\" />\n");
		html.append("        	<div id=\"next\" style=\"color: black;float: left;padding: 0px 16px;text-decoration: none;transition: background-color .3s;font-size: 23px;\" />\n");
		html.append("        </div>\n");
		html.append("        </div>\n");
		
		html.append("        <div id=\"bullePopup\" class=\"ol-popup\">\n");
		html.append("        <div id=\"bullePopup-content\">\n");
		html.append("        </div>\n");

		
		
		IVanillaAPI api = new RemoteVanillaPlatform(item.getVanillaUrl(), item.getVanillaLogin(), item.getVanillaPassword());
		
		RemoteMapDefinitionService mapRemote = new RemoteMapDefinitionService();
		mapRemote.configure(item.getVanillaUrl());
//		try {
			try {
				MapVanilla map = mapRemote.getMapVanillaById(item.getMapId()).get(0);
				
				Map<String, MapZone> zones = MapHelper.getMapZone(map.getDataSetList().get(0), api, mapRemote);
				Map<String, OsmValue> values = new HashMap<>();
				
				String exprVal = "row[\""+ item.getValueColumn() + "\"]";
				String exprZone = "row[\""+ item.getZoneColumn() + "\"]";
				while(( (IQueryResultSet) results[0] ).next()) {
					double value = Double.parseDouble(results[0].evaluate( exprVal ).toString());
					String zone = results[0].evaluate( exprZone ).toString();
//					System.out.println(zone);
					try {
						MapZone val = zones.get(zone);
						if(values.get(zone) == null) {
							values.put(zone, new OsmValue(val));
						}
						val.getProperties().put("value", value+"");
						values.get(zone).setValue(value);
					} catch(Exception e) {
//						e.printStackTrace();
					}	
				}
				
				//XXX
				
				
				html.append("        <script type=\"text/javascript\">\n");
				
				html.append("        if(document.createStyleSheet) {\n");
				html.append("        	  document.createStyleSheet('" + item.getVanillaUrl() + "/js/vanilla_map.css');\n");
				html.append("        	}\n");
				html.append("        	else {\n");
				html.append("        	  var styles = \"@import url('" + item.getVanillaUrl() + "/js/vanilla_map.css');\";\n");
				html.append("        	  var newSS=document.createElement('link');\n");
				html.append("        	  newSS.rel='stylesheet';\n");
				html.append("        	  newSS.href='data:text/css,'+escape(styles);\n");
				html.append("        	  document.getElementsByTagName(\"head\")[0].appendChild(newSS);\n");
				html.append("        	}\n");
				
				html.append("        if(document.createStyleSheet) {\n");
				html.append("        	  document.createStyleSheet('https://openlayers.org/en/v4.6.5/css/ol.css');\n");
				html.append("        	}\n");
				html.append("        	else {\n");
				html.append("        	  var styles = \"@import url('https://openlayers.org/en/v4.6.5/css/ol.css');\";\n");
				html.append("        	  var newSS=document.createElement('link');\n");
				html.append("        	  newSS.rel='stylesheet';\n");
				html.append("        	  newSS.href='data:text/css,'+escape(styles);\n");
				html.append("        	  document.getElementsByTagName(\"head\")[0].appendChild(newSS);\n");
				html.append("        	}\n");
				
				String json = MapHelper.generateGeojson(zones, map.getProjection());
				
//				html.append("        	 renderMap(\""+mapName+"\", " + map.getOriginLat() + ", " + map.getOriginLong() + ", " + map.getZoom() + ", " + map.getBoundLeft() + ", " + map.getBoundBottom() + ", " + map.getBoundRight() + ", " + map.getBoundTop() + ", values, coordinates, '" + ((OsmSerie)item.getSerieList().get(0)).getType() + "', markerUrl, minSize, maxSize, colorRanges, '" + map.getProjection() + "');\n");
//				html.append("        	 previewMapV2(\""+mapName+"\", " + map.getOriginLat() + ", " + map.getOriginLong() + ", " + map.getZoom() + ", " + map.getBoundLeft() + ", " + map.getBoundBottom() + ", " + map.getBoundRight() + ", " + map.getBoundTop() + ", '" + map.getProjection() + "', '" + ((OsmSerie)item.getSerieList().get(0)).getType() + "',coordinates, markerUrl);\n");
				html.append("        	renderGeoJsonMap(\""+mapName+"\", '"+json+"', '', new Array(), new Array());\n");
				
				html.append("        </script>\n");
			} catch(Exception e) {
				e.printStackTrace();
			}

		html.append("     </body>\n");
		html.append("</html>");
		String h = html.toString();
		System.out.println(h);
		return h;
	}

	private List<OsmValue> getValues(MapDataSet dataset, MapVanilla map, IBaseResultSet[] results, String exprVal, String exprZone) throws Exception {
		
		HashMap<String, OsmValue> zones = GeographicDataHelper.getGeographicData(dataset, map, true);
		
		LOOP:while(( (IQueryResultSet) results[0] ).next()) {
			double value = Double.parseDouble(results[0].evaluate( exprVal ).toString());
			String zone = results[0].evaluate( exprZone ).toString();
			
			for(String resultId : zones.keySet()) {
				OsmValue val = zones.get(resultId);
				if(val.getLastLevelIds().contains(zone)) {
					val.setValue(value);
					continue LOOP;
				}
			}
			
		}
		return new ArrayList<OsmValue>(zones.values());
	}
	
}
