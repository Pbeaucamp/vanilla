package bpm.birt.gmaps.core.tools;

import java.util.List;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public class GoogleMapXmlBuilder {
	
	private String map;
	private StringBuffer buf;
	
	private Double[] center;//west_north, est_south; 
	
	
	public Double[] getCenter(){
	
//		Double[] center = new Double[2];
//		
//		center[0] = (est_south[0] - west_north[0])/2.0;
//		center[1] = (est_south[1] - west_north[1])/2.0;
//		return est_south;
		return center;
	}
	
//	public int getZoom(Double[] center, int width, int height){
//		for(int i = 0; i < 20 ; i++){
//			
//		}
//	}
	
	
	public GoogleMapXmlBuilder(String map){
		this.map = map;
		buildXmlStart();
	}
	
	public String close(){
		return buf.toString();
	}
	
	private void buildXmlStart(){
		buf = new StringBuffer();
	}

	public void addEntity(String latitude, String longitude, String value, String label, List<ColorRange> colorRanges){
		
		String color = null;
		Double val = Double.valueOf(value);
		buf.append(point(latitude, longitude));
		
		
		center = new Double[]{new Double(longitude), new Double(latitude)};
//		
//		if (est_south == null){
//			est_south = current.clone();
//		}
//		else{
//			if (est_south[0] < current[0]){
//				est_south[0] = current[0];
//			}
//			if (est_south[1] < current[1]){
//				est_south[1] = current[1];
//			}
//		}
//		
//		
//		if (west_north == null){
//			west_north = current.clone();
//		}
//		else{
//			if (west_north[0] > current[0]){
//				west_north[0] = current[0];
//			}
//			if (west_north[1] > current[1]){
//				west_north[1] = current[1];
//			}
//		}

		
//		for(ColorRange range : colorRanges){
//			
//			if (range.getMin() <= val.doubleValue() && val <= range.getMax().doubleValue()){
//				color = range.getHex();
//				break;
//			}
//		}
		
		
		buf.append(marker(value, label, color));
	}
	
	private String point(String latitude, String longitude){
		StringBuffer buf = new StringBuffer();
		buf.append("var myLatlng = new google.maps.LatLng(" + latitude + "," + longitude + ");\n");
		return buf.toString();
	}
	
	private String marker(String value, String label, String color){
		StringBuffer builder = new StringBuffer();
		/*var styleIconClass = ;
		
		new StyledMarker(
			{styleIcon:new StyledIcon(
								StyledIconTypes.MARKER,
								{text:"A"},
								new StyledIcon(StyledIconTypes.CLASS,{color:"#ff0000"})),
			position:myLatlng,map:map});
		 * 
		 */
		

		
		builder.append("var myMarker = new google.maps.Marker({\n");
		builder.append("	position: myLatlng,\n");
		builder.append("	map: " + map + ",\n");
		builder.append("	title: \"" + label + " " + value + "\"\n");
//		if (color != null){
//			builder.append("	color: \"" + color + "\"\n");
//		}
		
		builder.append("});\n");
		return builder.toString();
	}
}

/**
 * 
 * Code if we want to add line string to our google map
 * 
 */

//		if(placemark.getGeometry() instanceof LineString){
//			LineString line = (LineString) placemark.getGeometry();
//			String nameVar = "tableauPointsPolyline";
//			xmlData.append("    			var " + nameVar + " = [\n");
//			List<Coordinate> coordinates = line.getCoordinates();
//			
//			int index = coordinates.size() - 1;
//			int indexCenter = coordinates.size()/2;
//			for(int i = 0; i<coordinates.size();i++){
//				if(i == indexCenter){
//					latitudeCenter = coordinates.get(i).getLatitude();
//					longitudeCenter = coordinates.get(i).getLongitude();
//				}
//				if(i != index){
//					xmlData.append("        		    new google.maps.LatLng(" + coordinates.get(i).getLatitude() + "," + coordinates.get(i).getLongitude() + "),\n");
//				}
//			}
//			xmlData.append("        		    new google.maps.LatLng(" + coordinates.get(index).getLatitude() + "," + coordinates.get(index).getLongitude() + ")\n");		
//			xmlData.append("                ];\n");
//			
////					LineStyle lineStyle = null;
////					for (StyleSelector styleSelector : doc.getStyleSelector()) {
////						if(styleSelector instanceof Style){
////							if(placemark.getStyleUrl().substring(1).equals(styleSelector.getId())){
////								lineStyle = ((Style)styleSelector).getLineStyle();
////								break;
////							}
////						}
////					}
//			
//			xmlData.append("                var optionsPolyline = {\n");
//			xmlData.append("                          map: map,\n");
//			xmlData.append("                          path: " + nameVar + ",\n");				
//			xmlData.append("                          strokeColor: \"#" + STROKE_COLOR_DEFAULT_VALUE + "\"\n");
////					xmlData.append("                          strokeWeight: \"" + lineStyle.getWidth() + "\"\n");
//			xmlData.append("                     };\n");
//			
//			xmlData.append("                var maPolyline = new google.maps.Polyline(optionsPolyline);\n");
//		
//			placemarks.put(idPlacemark, xmlData.toString());
//		}
//		else if(placemark.getGeometry() instanceof Point){
//		}
