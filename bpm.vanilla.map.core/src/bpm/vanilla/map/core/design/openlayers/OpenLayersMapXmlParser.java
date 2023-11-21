package bpm.vanilla.map.core.design.openlayers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class OpenLayersMapXmlParser {

	
	/**
	 * 
	 * @param xml
	 * @return an hashMap with zoneId as key and a list of points as value
	 * @throws Exception
	 */
	public static HashMap<String, List<OpenLayersPoint>> parseMapZone(String xml) throws Exception {
		HashMap<String, List<OpenLayersPoint>> pointsByZone = new HashMap<String, List<OpenLayersPoint>>();
		Document doc = DocumentHelper.parseText(xml);
		
		Element root = doc.getRootElement();
		
		for(Element zoneElem : (List<Element>)root.elements("zone")) {
			
			String zoneId = zoneElem.attribute("id").getValue();
			List<OpenLayersPoint> points = new ArrayList<OpenLayersPoint>();
			
			for(Element pointElem : (List<Element>)zoneElem.elements("point")) {
				OpenLayersPoint point = new OpenLayersPoint();
				point.setLatitude(pointElem.attribute("lat").getValue());
				point.setLongitude(pointElem.attribute("long").getValue());
				points.add(point);
			}
			
			pointsByZone.put(zoneId, points);
		}
		
		return pointsByZone;
	}
	
}
