package bpm.birt.fusionmaps.core.reportitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.extension.IBaseResultSet;
import org.eclipse.birt.report.engine.extension.IQueryResultSet;
import org.eclipse.birt.report.engine.extension.ReportItemPresentationBase;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.fusionmap.FusionMapProperties;
import bpm.vanilla.map.core.design.fusionmap.FusionMapXmlBuilder;
import bpm.vanilla.platform.core.config.ConfigurationManager;


/**
 * Author : Charles Martin
 * Company : BPM-Conseil
 * 
 * ere debug : 
 * added transparency setup so we can overlay on flash map.setTransparent(false);
 */
public class FusionmapsPresentationImpl extends ReportItemPresentationBase
{
	private FusionmapsItem mapItem;
	
	private String unit;
	private String columnId;
	private String columnValues;
	private String parametersXML;
	private List<ColorRange> colorRanges;

	public void setModelObject( ExtendedItemHandle modelHandle )
	{
		try {
			mapItem = (FusionmapsItem) modelHandle.getReportItem( );
		} catch (ExtendedElementException e){
			e.printStackTrace();
		}
	}	

	public int getOutputType() {
		return OUTPUT_AS_HTML_TEXT;
	}

	@Override
	public Object onRowSets(IBaseResultSet[] results) throws BirtException {
		
		if ( mapItem == null )
		{
			throw new NullPointerException("FusionmapsItem is null");
		}	
		String vanillaRuntimeUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl();
			
		if (!vanillaRuntimeUrl.endsWith("/")){
			vanillaRuntimeUrl = vanillaRuntimeUrl + "/";
		}
		
		String swfURL = mapItem.getSwfURL();
		String mapID = mapItem.getMapID();
		String mapWidth = Integer.toString(mapItem.getMapWidth());
		String mapHeight = Integer.toString(mapItem.getMapHeight());
		String mapDataXML = mapItem.getMapDataXML();
		boolean isDrillDownable = mapItem.isDrillDownable();
		String link = mapItem.getDrillDownLink();
		
		Properties props = mapItem.getCustomProperties();
		
		
		
		//We prepare the xml parameters for the map
		decodeXml(mapDataXML);
		String propertiesXml = FusionMapProperties.getPropertiesXmlForMap(props);
		parametersXML = parametersXML + " " + propertiesXml;
		String mapXml = buildMapXml(results, isDrillDownable, link);
		
		//We built the html that we send for the report
		StringBuffer buf = new StringBuffer();
		buf.append("<html>\n");
		buf.append("    <head>\n");
		buf.append("        <title>Europe Sales Data Example</title>\n");
		buf.append("        <script language=\"JavaScript\" src=\"" + vanillaRuntimeUrl + "fusionMap/Maps/FusionMaps.js" + "\"></script>\n");
		buf.append("    </head>\n");
		buf.append("    <body>\n");
		buf.append("        <table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\">\n");
		buf.append("            <tr>\n");
		buf.append("                <td valign=\"top\" class=\"text\" align=\"center\">\n");
		buf.append("                    <div id=\"mapdiv\" align=\"center\">\n");
		buf.append("                        FusionMaps.\n");
		buf.append("                    </div>\n");
		buf.append("                    <script type=\"text/javascript\">\n");
		buf.append("                        var map = new FusionMaps(\"" + vanillaRuntimeUrl + "fusionMap/Maps/" + swfURL + "\", \"" + mapID + "\", \"" + mapWidth + "\", \"" + mapHeight + "\", \"0\", \"0\");\n");
		buf.append("                        map.setTransparent(false);\n");
		buf.append("                        map.setDataXML(\"" + mapXml + "\");\n");
		buf.append("                        map.render(\"mapdiv\");\n");
		buf.append("                    </script>\n");
		buf.append("                </td>\n");
		buf.append("             </tr>\n");
		buf.append("         </table>\n");
		buf.append("     </body>\n");
		buf.append("</html>\n");
		
		return buf.toString();
	}
	
	private void decodeXml(String xml){
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
			
		for(Element e : (List<Element>)document.getRootElement().elements("map")){
			try{
				if (e.element("unit") != null){
					
					Element d = e.element("unit");
					if(d != null){
						this.unit = d.getStringValue();
					}
				}
				if (e.element("id") != null){
					
					Element d = e.element("id");
					if(d != null){
						this.columnId = d.getStringValue();
					}
				}
				if (e.element("values") != null){
					
					Element d = e.element("values");
					if(d != null){
						this.columnValues = d.getStringValue();
					}
				}
				if (e.element("parameters") != null){
					
					Element d = e.element("parameters");
					if(d != null){
						this.parametersXML = d.getStringValue();
					}
				}
				colorRanges = new ArrayList<ColorRange>();
				if (e.element("colorRange") != null){
					for(Element g : (List<Element>) e.elements("colorRange")){
						if(g.element("color") != null){
							for(Element h : (List<Element>) g.elements("color")){
								ColorRange color = new ColorRange();
								if (h.element("name") != null){
									
									Element d = h.element("name");
									if(d != null){
										color.setName(d.getStringValue());
									}
								}
								if (h.element("hexa") != null){
									
									Element d = h.element("hexa");
									if(d != null){
										color.setHex(d.getStringValue());
									}
								}
								if (h.element("min") != null){
									
									Element d = h.element("min");
									if(d != null){
										color.setMin(Integer.parseInt(d.getStringValue()));
									}
								}
								if (h.element("max") != null){
									
									Element d = h.element("max");
									if(d != null){
										color.setMax(Integer.parseInt(d.getStringValue()));
									}
								}
								colorRanges.add(color);
							}
						}
					}
				}
			}catch(Throwable ex){
				ex.printStackTrace();
			}
			
		}
	}
	
	private String buildMapXml(IBaseResultSet[] results, boolean isDrillDownable, String link) throws BirtException {
		
		String exprRef = "row[\""+ columnId + "\"]";
		String exprVal = "row[\""+ columnValues + "\"]";
		FusionMapXmlBuilder xmlBuilder = new FusionMapXmlBuilder(unit, colorRanges, parametersXML);
		
		if(isDrillDownable){
			while(( (IQueryResultSet) results[0] ).next()){
				
				xmlBuilder.addEntity(results[0].evaluate( exprRef ).toString() , results[0].evaluate( exprVal ).toString(), link);
			}
		}
		else{
			while(( (IQueryResultSet) results[0] ).next()){
				
				xmlBuilder.addEntity(results[0].evaluate( exprRef ).toString() , results[0].evaluate( exprVal ).toString());
			}
		}
		
		String s =  xmlBuilder.close();
		return s;
	}
}
