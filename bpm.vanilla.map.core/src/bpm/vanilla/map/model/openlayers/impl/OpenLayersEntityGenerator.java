package bpm.vanilla.map.model.openlayers.impl;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

/**
 * Use this to create SpecificationEntities for an OpenLayers XML definition
 * @author Marc Lanquetin
 *
 */
public class OpenLayersEntityGenerator {

	private String xml;
	private boolean isKml;
	private String newXml;
	
	public OpenLayersEntityGenerator(String xml, boolean isKml) {
		this.xml = xml;
		this.isKml = isKml;
	}
	
	public List<IOpenLayersMapSpecificationEntity> createEntities() throws Exception {
		List<IOpenLayersMapSpecificationEntity> entities = null;
		
		if(isKml) {
			newXml = createEntitiesFromKml();
			entities = createEntitiesFromXml(newXml);
		}
		else {
			entities = createEntitiesFromXml(xml);
		}
		
		return entities;
	}

	private List<IOpenLayersMapSpecificationEntity> createEntitiesFromXml(String xml) throws Exception {
		List<IOpenLayersMapSpecificationEntity> entities = new ArrayList<IOpenLayersMapSpecificationEntity>();
		
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		for(Element zoneElem : (List<Element>) root.elements("zone")) {
			
			IOpenLayersMapSpecificationEntity entity = new OpenLayersMapSpecificationEntity();
			
			try {
				String internalId = zoneElem.attribute("id").getValue();
				entity.setInternalId(internalId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String shortName = zoneElem.attribute("shortname").getValue();
				entity.setShortName(shortName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String longName = zoneElem.attribute("longname").getValue();
				entity.setLongName(longName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			entities.add(entity);
		}
		
		return entities;
	}

	private String createEntitiesFromKml() {
		
		Kml kml = Kml.unmarshal(xml);
		
		de.micromata.opengis.kml.v_2_2_0.Document doc = (de.micromata.opengis.kml.v_2_2_0.Document)kml.getFeature();
		List<Feature> features = doc.getFeature();
		
		Document newXmlDoc = DocumentHelper.createDocument();
		Element root = newXmlDoc.addElement("openlayermap");
		
		for(Feature feature : features){
			if(feature instanceof Placemark){
				Placemark placemark = (Placemark)feature;
				
				if(placemark.getGeometry() instanceof Polygon){
					
					Polygon polygon = (Polygon) placemark.getGeometry();
					List<Coordinate> coordinates = polygon.getOuterBoundaryIs().getLinearRing().getCoordinates();
					
					String id = polygon.getId();
					
					Element zoneElem = root.addElement("zone");
					zoneElem.addAttribute("id", id);
					zoneElem.addAttribute("shortname", id);
					zoneElem.addAttribute("longname", id);
					
					for(Coordinate coord : coordinates) {
						double lat = coord.getLatitude();
						double longi = coord.getLongitude();
						
						Element ptElem = zoneElem.addElement("point");
						ptElem.addAttribute("long", longi+"");
						ptElem.addAttribute("lat", lat+"");
					}
					
				}
			}
		}
		
		
		
		return newXmlDoc.asXML();
	}
	
	public String getNewXml() {
		return newXml;
	}
}
