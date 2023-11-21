package bpm.birt.gmaps.core.reportitem;

import java.util.ArrayList;
import java.util.List;

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

import bpm.birt.gmaps.core.tools.GoogleMapXmlBuilder;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class GooglemapsPresentationImpl extends ReportItemPresentationBase{

	private GooglemapsItem gmapItem;
	private Double[] center;
	public void setModelObject( ExtendedItemHandle modelHandle )
	{
		try {
			gmapItem = (GooglemapsItem) modelHandle.getReportItem( );
		} catch (ExtendedElementException e){
			e.printStackTrace();
		}
	}	

	public int getOutputType() {
		return OUTPUT_AS_HTML_TEXT;
	}

	@Override
	public Object onRowSets(IBaseResultSet[] results) throws BirtException {
		
		if ( gmapItem == null )
		{
			throw new NullPointerException( );
		}
		
		String gmapID = gmapItem.getMapID();
//		String gmapZoom = Integer.toString(gmapItem.getZoom());
//		String gmapCenterLatitude = gmapItem.getCenterLatitude();
//		String gmapCenterLongitude = gmapItem.getCenterLongitude();
		String gmapWidth = Integer.toString(gmapItem.getMapWidth());
		String gmapHeight = Integer.toString(gmapItem.getMapHeight());
		String gmapColumnLatitude = gmapItem.getMapColumnLatitude();
		String gmapColumnLongitude = gmapItem.getMapColumnLongitude();
		String gmapColumnValue = gmapItem.getMapColumnValue();
		String gmapColumnLabel = gmapItem.getMapColumnLabel();
		
//		String gmapColorRanges = gmapItem.getMapColorRanges();
		
		String mapXml = buildMapXml(results, gmapID, gmapColumnLatitude, gmapColumnLongitude, gmapColumnValue, gmapColumnLabel, null/*rebuildColorRanges(gmapColorRanges)*/);
		
		StringBuffer html = new StringBuffer();
		html.append("<html>\n");
		html.append("  <head>\n");
		html.append("     <script src=\"http://maps.google.com/maps/api/js?sensor=false\" type=\"text/javascript\"></script>\n");
//		html.append("     <script src=\"http://google-maps-utility-library-v3.googlecode.com/svn/trunk/styledmarker/src/StyledMarker.js type=\"text/javascript\"></script>\n"); 
		html.append("     <script type=\"text/javascript\">\n");
		html.append("   	var " + gmapID + " = null;\n");
		html.append("   	function load(mapname)\n");
		html.append("   	{  \n");
		html.append("      		if (!" + gmapID + ")\n");
		html.append("      		{\n");
		html.append("        		var latlng = new google.maps.LatLng(" + center[1] + "," + center[0]+");\n");
		html.append("   	 		var myOptions = {\n");
		html.append("     				zoom: " + 8 + ",\n");
		html.append("     				center: latlng,\n");
  		html.append("     				mapTypeId: google.maps.MapTypeId.ROADMAP\n");
  		html.append("   			}\n");
  		html.append("         		" + gmapID + " = new google.maps.Map(document.getElementById(mapname), myOptions);\n");
  		html.append(mapXml);
  		html.append("        	if (!" + gmapID + ")\n");
  		html.append("         	{\n");
  		html.append("            	alert (\"Could not create Google map\");\n");
  		html.append("            	return;\n");
  		html.append("         	}\n");
  		html.append("     	}\n");
  		html.append("  	  }\n");
  		html.append("  	  window.onload = function launchTask(){\n");
  		html.append("		load('" + gmapID + "')\n");
  		html.append("     };\n");
  		html.append("     </script>\n");
      	html.append("   </head>\n");
  		html.append("   <body>\n");
  		html.append("       <div id=\"" + gmapID + "\" style=\"width: " + gmapWidth + "px; height: " + gmapHeight + "px\"></div>\n");
  		html.append("   </body>\n");
		html.append("</html>\n");
		
		return html.toString();
	}
	
	private List<ColorRange> rebuildColorRanges(String gmapColorRanges) {
		List<ColorRange> colorRanges = new ArrayList<ColorRange>();
		
		Document document = null;
		try {
			document = DocumentHelper.parseText(gmapColorRanges);
		} catch (DocumentException e1) {
			e1.printStackTrace();
			return colorRanges;
		}
			

		

		for(Element g : (List<Element>) document.getRootElement().elements("colorRange")){
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

		return colorRanges;
	}

	private String buildMapXml(IBaseResultSet[] results, String mapID, String gmapColumnLatitude, 
			String gmapColumnLongitude, String gmapColumnValue, String gmapColumnLabel, List<ColorRange> colorRanges) throws BirtException {
		
		String exprLat = "row[\""+ gmapColumnLatitude + "\"]";
		String exprLon = "row[\""+ gmapColumnLongitude + "\"]";
		String exprVal = "row[\""+ gmapColumnValue + "\"]";
		String labelVal = "row[\""+ gmapColumnLabel + "\"]";

		GoogleMapXmlBuilder xmlBuilder = new GoogleMapXmlBuilder(mapID);
		
		while(( (IQueryResultSet) results[0] ).next()){
			xmlBuilder.addEntity(
					results[0].evaluate( exprLat ).toString(), 
					results[0].evaluate( exprLon ).toString(), 
					results[0].evaluate( exprVal ).toString(), 
					results[0].evaluate( labelVal ).toString(), 
					colorRanges);
		}
		
		String s =  xmlBuilder.close();
		
		center = xmlBuilder.getCenter();
		return s;
	}
}