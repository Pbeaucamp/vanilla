package bpm.fd.runtime.model.ui.jsp.renderer;

import java.awt.Rectangle;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.gauge.ComponentGauge;
import bpm.fd.api.core.model.components.definition.maps.ComponentMap;
import bpm.fd.api.core.model.components.definition.maps.MapDatas;
import bpm.fd.api.core.model.components.definition.maps.MapInfo;
import bpm.fd.api.core.model.components.definition.maps.openlayers.IOpenLayersOptions;
import bpm.fd.runtime.engine.map.freemetrics.WmsMapGenerator;
import bpm.fd.runtime.engine.map.fusionmap.FusionMapXmlGenerator;
import bpm.fd.runtime.engine.map.fusionmap.VanillaMapXmlGenerator;
import bpm.fd.runtime.model.DashState;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.platform.core.config.ConfigurationManager;

public class MapRenderer extends AsbtractCssApplier implements IHTMLRenderer<ComponentMap>{
	public String getHTML(Rectangle layout, ComponentMap map, DashState state, IResultSet datas, boolean refresh){
		StringBuffer buf = new StringBuffer();
		buf.append(getComponentDefinitionDivStart(layout, map));
		buf.append(getComponentDefinitionDivEnd());
		
		
		String jsVar = "map_" + map.getId();
		
		buf.append("<script type=\"text/javascript\">\n");
		
		if((map.getOptions() != null && map.getOptions().size() > 0 && map.getOptions().get(0) instanceof IOpenLayersOptions) || (((MapInfo)map.getMapInfo()).getMapType() != null && ((MapInfo)map.getMapInfo()).getMapType().equals(IMapDefinition.MAP_TYPE_OPEN_GIS))) {
			try {
				return new WmsMapGenerator(map, state, datas).generateHtml();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if((((MapInfo)map.getMapInfo()).isFusionMap())) {
			String swfUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl() + "/fusionMap/Maps/" + ((MapInfo)map.getMapInfo()).getSwfFileName();
		    
			buf.append("     var " + jsVar + " = new FusionMaps('" + swfUrl + "','"+ map.getId() + "_fs', '" + map.getWidth() + "', '" + map.getHeight() + "', '0', '0');\n");//,'','','','0','0'
			
			buf.append("     " + jsVar + ".setTransparent(true);\n");	        
			buf.append(jsVar + ".render(\"" + map.getName() + "\");\n");
			
			String xml = null;
			
			try {
				xml = generateXml(map, state, datas);
			} catch (Exception e) {
				Logger.getLogger(MapRenderer.class).error("Failed to generate Xml");
			}
			buf.append("     fdObjects[\""+map.getId() + "\"]=new FdMap(" + jsVar  + ", '"+ map.getName() + "', \""+ xml + "\");");
			
			if (refresh){
				return xml;
			}
		}
		else {
			String swfUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl() + "/fusionMap/Maps/" + ((MapInfo)map.getMapInfo()).getSwfFileName();	
			    
			buf.append("     var " + jsVar + " = new VanillaMap('"+ map.getId() + "','" + swfUrl + "','"+map.getId()+"', '" + map.getWidth() + "', '" + map.getHeight() + "');\n");
			buf.append("	"+jsVar + ".draw();\n");    
			
			String xml = null;
			
			try {
				xml = generateVanillaMapXml(map, state, datas);
			} catch (Exception e) {
				Logger.getLogger(MapRenderer.class).error("Failed to generate Xml");
			}
			buf.append("     fdObjects[\""+map.getId() + "\"]=new FdVanillaMap(" + jsVar  + ", '"+ map.getName() + "', \""+ xml + "\");");
			
			if (refresh){
				return xml;
			}

		}
		
		buf.append("</script>\n");
		
		return buf.toString();
	}
	private String generateVanillaMapXml(ComponentMap map, DashState state, IResultSet datas) throws Exception {
		MapDatas dt = (MapDatas)map.getDatas();
		return new VanillaMapXmlGenerator().generateXml(
				map.getColorRanges().toArray(new ColorRange[map.getColorRanges().size()]), 
				dt.getZoneIdFieldIndex(), 
				dt.getValueFieldIndex(), datas, "");
	}
	public String getJavaScriptFdObjectVariable(ComponentMap map){
		String jsVar = "map_" + map.getId();
		StringBuffer buf = new StringBuffer();
		buf.append("<script type=\"text/javascript\">\n");
		buf.append("     fdObjects[\""+map.getId() + "\"]=new FdMap(" + jsVar  + ", '"+ map.getName() + "', \"\");");
		buf.append("</script>\n");
		return buf.toString();
	}
	
	
	private static String generateXml(ComponentMap map, DashState state, IResultSet datas) throws Exception{
		MapDatas dt = (MapDatas)map.getDatas();
		
		
		
		//setting drill options
		
		String folderParameterName = null;
		if (map.getDrillInfo().getModelPage() != null ){
			IBaseElement e = map.getDrillInfo().getModelPage().getProject().getFdModel().getContent().get(0);
			folderParameterName = "_folder_" + e.getId();
		}
//		MapDrillRuntime drill = new MapDrillRuntime(
//				null, 
//				new String[]{map.getName()}, 
//				map.getDrillInfo().isSendCategory(), 
//				map.getDrillInfo().getModelPageName() + ".jsp", 
//				folderParameterName);
		
		return new FusionMapXmlGenerator(map.isShowLabels()).generateXml(
				map.getColorRanges().toArray(new ColorRange[map.getColorRanges().size()]), 
				dt.getZoneIdFieldIndex(), 
				dt.getValueFieldIndex(), datas, map.getDrillInfo());
	}
	

	private static Properties createGaugeProperties(ComponentGauge gauge){
		Properties p = new Properties();
		for(IComponentOptions opt : gauge.getOptions()){
			for(String key : opt.getInternationalizationKeys()){
				if (opt.getValue(key) != null){
					p.setProperty(key, opt.getValue(key));
				}
				
			}
			for(String key : opt.getNonInternationalizationKeys()){
				if (opt.getValue(key) != null){
					p.setProperty(key, opt.getValue(key));
				}
			}
		}
		
		return p;
	}

}
