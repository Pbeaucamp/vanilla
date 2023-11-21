package bpm.vanilla.map.core.design.kml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

public class KmlParser {
	private final static String STROKE_COLOR = "strokeColor";
	private final static String FILL_COLOR = "fillColor";
	
	private final static String STROKE_COLOR_DEFAULT_VALUE = "000000";
	private final static String FILL_COLOR_DEFAULT_VALUE = "FFFFFF";
	
	private double latitudeCenter;
	private double longitudeCenter;
	private List<ColorRange> colorRange;
	private HashMap<String, String> placemarks = new HashMap<String, String>();
	
	private Kml kml;
	private IKmlObject kmlObject;
	private IFactoryKml factoryKml;
	
	public KmlParser(String path, List<ColorRange> colorRange){
		this.colorRange = colorRange;
		Kml kml = readFileKml(path);
		prepareHashMap(kml);
	}
	
	public KmlParser(File file, IKmlObject kmlObject, IFactoryKml factoryKml){
		this.kml = readFileKml(file);
		this.kmlObject = kmlObject;
		this.factoryKml = factoryKml;
	}
	
	public KmlParser(String kml) {
		this.kml = readStringKml(kml);
	}
	
	public double getLatitudeCenter(){
		return latitudeCenter;
	}
	
	public double getLongitudeCenter(){
		return longitudeCenter;
	}
	
	private Kml readStringKml(String kml) {
		return Kml.unmarshal(kml);
	}
	
	private Kml readFileKml(String path){
		URL url;
		InputStream kmlInputStream = null;
		try {
			url = new URL(path);
			kmlInputStream = url.openStream();
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		//We parse the kml with the JavaApiForKml
		return Kml.unmarshal(kmlInputStream);
		
	}
	
	private Kml readFileKml(File file){	
		//We parse the kml with the JavaApiForKml
		return Kml.unmarshal(file);
	}
	
	public void putColor(String placemarkId, String value){
		String xml = placemarks.get(placemarkId);
		if(xml != null){
			for(ColorRange color : colorRange){
				if(Integer.parseInt(value) > color.getMin() && Integer.parseInt(value) < color.getMax()){
					if(xml.contains(STROKE_COLOR)){
						xml.replace(STROKE_COLOR_DEFAULT_VALUE, color.getHex());
						if(xml.contains(FILL_COLOR)){
							xml.replace(FILL_COLOR_DEFAULT_VALUE, color.getHex());
							break;
						}
					}
				}
			}
		}
	}
	
	public String close(){
		StringBuffer buf = new StringBuffer();
		for(Entry<String, String> placemark : placemarks.entrySet()) {
			buf.append(placemark.getValue());
		}
		return buf.toString();
	}

	private void prepareHashMap(Kml kml){

		Document doc = (Document)kml.getFeature();
		List<Feature> features = doc.getFeature();
		
		for(Feature feature : features){
			if(feature instanceof Placemark){
				Placemark placemark = (Placemark)feature;

				String idPlacemark = placemark.getId();
				StringBuffer xmlData = new StringBuffer();
				
				if(placemark.getGeometry() instanceof LineString){
					LineString line = (LineString) placemark.getGeometry();
					String nameVar = "tableauPointsPolyline";
					xmlData.append("    			var " + nameVar + " = [\n");
					List<Coordinate> coordinates = line.getCoordinates();
					
					int index = coordinates.size() - 1;
					int indexCenter = coordinates.size()/2;
					for(int i = 0; i<coordinates.size();i++){
						if(i == indexCenter){
							latitudeCenter = coordinates.get(i).getLatitude();
							longitudeCenter = coordinates.get(i).getLongitude();
						}
						if(i != index){
							xmlData.append("        		    new google.maps.LatLng(" + coordinates.get(i).getLatitude() + "," + coordinates.get(i).getLongitude() + "),\n");
						}
					}
					xmlData.append("        		    new google.maps.LatLng(" + coordinates.get(index).getLatitude() + "," + coordinates.get(index).getLongitude() + ")\n");		
					xmlData.append("                ];\n");
					
//					LineStyle lineStyle = null;
//					for (StyleSelector styleSelector : doc.getStyleSelector()) {
//						if(styleSelector instanceof Style){
//							if(placemark.getStyleUrl().substring(1).equals(styleSelector.getId())){
//								lineStyle = ((Style)styleSelector).getLineStyle();
//								break;
//							}
//						}
//					}
					
					xmlData.append("                var optionsPolyline = {\n");
					xmlData.append("                          map: map,\n");
					xmlData.append("                          path: " + nameVar + ",\n");				
					xmlData.append("                          strokeColor: \"#" + STROKE_COLOR_DEFAULT_VALUE + "\"\n");
//					xmlData.append("                          strokeWeight: \"" + lineStyle.getWidth() + "\"\n");
					xmlData.append("                     };\n");
					
					xmlData.append("                var maPolyline = new google.maps.Polyline(optionsPolyline);\n");
				
					placemarks.put(idPlacemark, xmlData.toString());
				}
//				else if(placemark.getGeometry() instanceof Polygon){
//					Polygon polygon = (Polygon) placemark.getGeometry();
//					String nameVar = "tableauPointsPolyline";
//					xmlData.append("    			var " + nameVar + " = [\n");
//					List<Coordinate> coordinates = polygon.getOuterBoundaryIs().getLinearRing().getCoordinates();
//					
//					int index = coordinates.size() - 1;
//					int indexCenter = coordinates.size()/2;
//					for(int i = 0; i<coordinates.size();i++){
//						if(i == indexCenter){
//							latitudeCenter = coordinates.get(i).getLatitude();
//							longitudeCenter = coordinates.get(i).getLongitude();
//						}
//						if(i != index){
//							xmlData.append("        		    new google.maps.LatLng(" + coordinates.get(i).getLatitude() + "," + coordinates.get(i).getLongitude() + "),\n");
//						}
//					}
//					xmlData.append("        		    new google.maps.LatLng(" + coordinates.get(index).getLatitude() + "," + coordinates.get(index).getLongitude() + ")\n");		
//					xmlData.append("                ];\n");
//					
//					
//					xmlData.append("                var optionsPolyline = {\n");
//					xmlData.append("                          map: map,\n");
////					if(polyStyle != null && lineStyle != null){
//						xmlData.append("                          path: " + nameVar + ",\n");
//						xmlData.append("                          strokeColor: \"#" + STROKE_COLOR_DEFAULT_VALUE + "\",\n");
//						xmlData.append("                          fillColor: \"#" + FILL_COLOR_DEFAULT_VALUE + "\"\n");
////					}
////					else if(polyStyle != null && lineStyle == null){
////						xmlData.append("                          path: " + nameVar + ",\n");
////						xmlData.append("                          fillColor: \"#" + polyStyle.getColor().substring(2) + "\"\n");
////					}
////					else if(polyStyle == null && lineStyle != null){
////						xmlData.append("                          path: " + nameVar + ",\n");
////						xmlData.append("                          strokeColor: \"#" + lineStyle.getColor().substring(2) + "\"\n");
////					}
////					else{
////						xmlData.append("                          path: " + nameVar + "\n");						
////					}
//					xmlData.append("                     };\n");
//					
//					xmlData.append("                var maPolyline = new google.maps.Polygon(optionsPolyline);\n");
//
//					placemarks.put(idPlacemark, xmlData.toString());
//				}
				else if(placemark.getGeometry() instanceof Point){
					Point point = (Point)placemark.getGeometry();
					List<Coordinate> coordinates = point.getCoordinates();
					for(Coordinate coor : coordinates){
						latitudeCenter = coor.getLatitude();
						longitudeCenter = coor.getLongitude();
						xmlData.append("var myLatlng = new google.maps.LatLng(" + coor.getLatitude() + "," + coor.getLongitude() + ");\n");
					}
					
					xmlData.append("var myMarker = new google.maps.Marker({\n");
					xmlData.append("	position: myLatlng,\n");
					xmlData.append("	map: map,\n");
					xmlData.append("	title: \"" + placemark.getName() + "\"\n");
					xmlData.append("});\n");
				
					placemarks.put(idPlacemark, xmlData.toString());
				}
			}
		}
	}

	public void prepareKmlSpecificationEntities(){
		Document doc = (Document)kml.getFeature();
		List<Feature> features = doc.getFeature();
		
		for(Feature feature : features){
			if(feature instanceof Placemark){
				IKmlSpecificationEntity kmlSpecificationEntity = null;
				try {
					kmlSpecificationEntity = factoryKml.createKmlSpecificationEntity();
				} catch (Exception e) {
					e.printStackTrace();
				}
				kmlSpecificationEntity.setPlacemarkId(((Placemark)feature).getId());
				if(((Placemark)feature).getGeometry() instanceof Polygon){
					kmlSpecificationEntity.setPlacemarkType(IKmlSpecificationEntity.POLYGON);
				}
				else if(((Placemark)feature).getGeometry() instanceof Point){
					kmlSpecificationEntity.setPlacemarkType(IKmlSpecificationEntity.POINT);
				}
				else if(((Placemark)feature).getGeometry() instanceof LineString){
					kmlSpecificationEntity.setPlacemarkType(IKmlSpecificationEntity.LINE_STRING);					
				}
				
				kmlObject.addSpecificationEntity(kmlSpecificationEntity);
			}
		}
	}

	public Kml getKml() {
		return kml;
	}
}
	
////	private static String prepareHtmlStatic(String name, String description, List<Line> lines){
////		String xmlData = "";
////		xmlData += "path=";
////		xmlData += "color:";
////		xmlData += lines.get(7).getStyleLine().getColor();
////		xmlData += "|weight:";
////		xmlData += lines.get(7).getStyleLine().getWidth();
////		List<Coordinate> coordinates = lines.get(7).getCoordinates();
////		int i = 0;
////		int index = coordinates.size() - 1;
////		for(Coordinate coor : coordinates){
////			if(i != index){
////				xmlData += "|" + coor.getY() + "," + coor.getX();
////				i++;
////			}
////		}
////		return xmlData;
////	}
//	
