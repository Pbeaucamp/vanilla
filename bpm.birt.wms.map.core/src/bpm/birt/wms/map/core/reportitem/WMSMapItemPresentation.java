package bpm.birt.wms.map.core.reportitem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.extension.IBaseResultSet;
import org.eclipse.birt.report.engine.extension.IQueryResultSet;
import org.eclipse.birt.report.engine.extension.ReportItemPresentationBase;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;

public class WMSMapItemPresentation extends ReportItemPresentationBase {

	private WMSMapItem item;

	public void setModelObject(ExtendedItemHandle modelHandle) {
		try {
			item = (WMSMapItem) modelHandle.getReportItem();
		} catch(ExtendedElementException e) {
			e.printStackTrace();
		}
	}

	public int getOutputType() {
		return OUTPUT_AS_HTML_TEXT;
	}

	@Override
	public Object onRowSets(IBaseResultSet[] results) throws BirtException {
		StringBuilder buf = new StringBuilder();
		
		String baseLayerUrl = item.getPropertyString(WMSMapItem.P_BASE_LAYER_URL);
		String baseLayerName = item.getPropertyString(WMSMapItem.P_BASE_LAYER_NAME);
		String vectorLayerUrl = item.getPropertyString(WMSMapItem.P_VECTOR_LAYER_URL);
		String vectorLayerName = item.getPropertyString(WMSMapItem.P_VECTOR_LAYER_NAME);
		String bounds = item.getPropertyString(WMSMapItem.P_BOUNDS);
		String tileOrigin = item.getPropertyString(WMSMapItem.P_TILE_ORIGIN);
		String projection = item.getPropertyString(WMSMapItem.P_PROJECTION);
		String opacity = item.getPropertyString(WMSMapItem.P_OPACITY);
		String colors = item.getPropertyString(WMSMapItem.P_COLOR_RANGE);
		String width = item.getPropertyString(WMSMapItem.P_WIDTH);
		String height = item.getPropertyString(WMSMapItem.P_HEIGHT);
		String vanillaUrl = item.getPropertyString(WMSMapItem.P_VANILLARUNTIME);
		
		String exprRef = "row[\""+ item.getPropertyString(WMSMapItem.P_ZONE_ID_COLUMN) + "\"]";
		String exprVal = "row[\""+ item.getPropertyString(WMSMapItem.P_VALUE_COLUMN) + "\"]";
		
		List<ColorRange> colorRanges = recreateColorRanges(colors);
		
		String mapName = "wmsmap" + new Object().hashCode();
		
		IQueryResultSet datas = (IQueryResultSet) results[0];
		
		buf.append("<html>\n");
		buf.append("    <head>\n");
		buf.append("        <script type=\"text/javascript\" src=\""+vanillaUrl+"/freedashboardRuntime/jquery/jquery-1.7.2.min.js\"></script>\n");
		buf.append("        <script type=\"text/javascript\" src=\""+vanillaUrl+"/freedashboardRuntime/jquery/ui/js/jquery-ui-1.8.21.custom.min.js\"></script>\n");
		buf.append("        <script language=\"Javascript\" src=\""+vanillaUrl+"/freedashboardRuntime/OpenLayers/OpenLayers.js\"></script>\n");
		buf.append("    </head>\n");
		buf.append("    <body>\n");
		buf.append("<div name='" + mapName + "' id='" + mapName + "' style=\"height:" + height + ";width:" + width + "\"></div>\n");
		
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("	var extent = new OpenLayers.Bounds("+bounds+");\n");
		buf.append("	var controls = [\n");
		buf.append("		new OpenLayers.Control.Navigation(),\n");
		buf.append("		new OpenLayers.Control.PanZoomBar(),\n");
		buf.append("		new OpenLayers.Control.MousePosition(),\n");
		buf.append("		new OpenLayers.Control.LayerSwitcher(),\n");
		buf.append("		new OpenLayers.Control.KeyboardDefaults()\n");
		buf.append("		];\n");
		buf.append("	var	proj = new OpenLayers.Projection(\""+projection.toUpperCase()+"\");\n");
		buf.append("	var map1 = new OpenLayers.Map({\n");
		buf.append("		div: \""+mapName+"\",\n");
		buf.append("		maxExtent : extent,\n");
		buf.append("		controls: controls,\n");
		buf.append("		projection: proj,\n");
		buf.append("		maxResolution: 256\n");
		buf.append("	});\n");
		buf.append("	var layers = [];\n");
		
		buf.append("	layers.push(new OpenLayers.Layer.WMTS({\n");
		buf.append("	        name: \""+baseLayerName+"\",\n");
		buf.append("	        url: \""+baseLayerUrl+"\",\n");
		buf.append("			version: \"1.0.0\",\n");
		buf.append("			requestEncoding: \"KVP\",\n");
		buf.append("	        layer: \"\",\n");
		buf.append("			tileOrigin: new OpenLayers.LonLat("+tileOrigin+"),\n");
		buf.append("			tileSize: new OpenLayers.Size(256, 256),\n");
		buf.append("	        matrixSet: \""+projection.toLowerCase()+"\",\n");
		buf.append("	        format: \"image/jpeg\",\n");
		buf.append("	        style: \"default\",\n");
		buf.append("	        opacity: 1.0,\n");
		buf.append("	        isBaseLayer: true\n");
		buf.append("	})); \n");
		
		buf.append("		var communesLayer = new OpenLayers.Layer.Vector( \""+vectorLayerName+"\", {\n");
		buf.append("			strategies: [new OpenLayers.Strategy.BBOX()],\n");
		buf.append("			visibility: true,\n");
		buf.append("			protocol: new OpenLayers.Protocol.WFS.v1_0_0({\n");
		buf.append("				url: \""+vectorLayerUrl+"\",\n");
		buf.append("				featureType: \""+vectorLayerName+"\"\n");
		buf.append("		})});\n");
		
		buf.append("		layers.push(communesLayer);\n");
		
		buf.append("	map1.addLayers(layers);\n");
		buf.append("	map1.zoomToMaxExtent();\n");
		
		buf.append("	communesLayer.events.register(\"loadend\", communesLayer, function (e) {\n");
		
		try {
			
			while(datas.next()) {
				
				String zone = datas.evaluate( exprRef ).toString();
				int value = Integer.parseInt(datas.evaluate( exprVal ).toString());
				
				ColorRange color = null;			
				for(ColorRange range : colorRanges) {
					if(range.getMin() <= value && range.getMax() >= value) {
						color = range;
						break;
					}
				}

				buf.append("	var commune = communesLayer.getFeatureByFid('"+zone+"');\n");
				if(color != null) {
					buf.append("	commune.style = "+createStyle(color.getHex(), opacity)+";\n");
				}
				
				
			}
			
			buf.append("	communesLayer.redraw();\n");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		buf.append("	});\n");
		buf.append("</script>\n");
		buf.append("     </body>\n");
		buf.append("</html>\n");
		
		return buf.toString();
	}
	
	private String createStyle(String hex, String opacity) {
		if(opacity != null && !opacity.isEmpty() && !opacity.equals("100")) {
			
			return "{fill: true, fillColor: \"#"+hex+"\", fillOpacity: "+((double)Integer.parseInt(opacity)) / 100+"}";
		}
		return "{fill: true, fillColor: \"#"+hex+"\"}";
	}

	private List<ColorRange> recreateColorRanges(String colors) {
		List<ColorRange> colorRanges = new ArrayList<ColorRange>();
		String[] ranges = colors.split(";");
		
		for(String range : ranges) {
			String[] parts = range.split("\\|");
			ColorRange r = new ColorRange();
			r.setName(parts[0]);
			r.setMin(Integer.parseInt(parts[1]));
			r.setMax(Integer.parseInt(parts[2]));
			r.setHex(parts[3]);
			colorRanges.add(r);
		}
		
		return colorRanges;
	}
}
