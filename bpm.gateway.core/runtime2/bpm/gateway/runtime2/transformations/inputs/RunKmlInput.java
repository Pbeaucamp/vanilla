package bpm.gateway.runtime2.transformations.inputs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.kml.KmlObjectType;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.SimpleData;
public class RunKmlInput extends RuntimeStep{

		
	private InputStream fis;
	private Document document;
	private Iterator<Feature> rowIterator;
	private List elements;
	private Integer id = null;
	
	private KmlObjectType objectType;;
	private boolean extractId = false;
	
	private KMLInput transfoKml;
	
	public RunKmlInput(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}
	
	@Override
	public void init(Object adapter) throws Exception {
		Kml kml = null;
		if (getTransformation() instanceof MdmContractFileInput) {
			KMLInput fXml = (KMLInput) ((MdmContractFileInput) getTransformation()).getFileTransfo();
			transfoKml = fXml;
			kml = Kml.unmarshal(((AbstractFileServer) fXml.getServer()).getInpuStream(fXml));
		}
		else {
			KMLInput fXml = (KMLInput)getTransformation();
			transfoKml = fXml;
			kml = Kml.unmarshal(((AbstractFileServer) fXml.getServer()).getInpuStream(fXml));
		}
		
		try {
			document = (Document) ((Document) kml.getFeature()).getFeature().get(0);
		} catch(Exception e) {
			document = ((Document) kml.getFeature());
		}
		
		
		if(document.getFeature().get(0) instanceof Folder) {
			List<Feature> features = new ArrayList<Feature>();
			for(Feature f : document.getFeature()) {
				features.addAll(((Folder)f).getFeature());
			}
			rowIterator = features.iterator();
		}
		else {
			rowIterator = document.getFeature().iterator();
		}
		
		isInited = true;
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if (!rowIterator.hasNext()){
			setEnd();
			return;
		}
		
		KMLInput fXml = transfoKml;
		
		Feature feature = rowIterator.next();
		if(feature instanceof Placemark) {
			Placemark placemark = (Placemark) feature;
			//get the schema data
			
			List<Object> schemaValues = new ArrayList<Object>();
			
			try {
				List<SimpleData> data = placemark.getExtendedData().getSchemaData().get(0).getSimpleData();
				for(StreamElement element : fXml.getDescriptor(fXml).getStreamElements()) {
					if(element.name.equals("latitude")) {
						break;
					}
					SimpleData d = findData(element, data);
					
					if(d != null) {
						schemaValues.add(castValue(d.getValue(), element.className));
					}
					else {
						schemaValues.add(castValue(null, element.className));
					}
				}
			} catch(Exception e) {
			}
			
			//get the coordinates
			int subId = 0;
			
			Geometry geo = placemark.getGeometry();
			if(geo instanceof MultiGeometry) {
				for(Geometry g : ((MultiGeometry) geo).getGeometry()) {
					writeGeometry(g, schemaValues, subId, placemark.getName());
					subId++;
				}
			}
			else {
				writeGeometry(geo, schemaValues, subId, placemark.getName());
			}
		}
		
		
//		Element current = (Element)rowIterator.next();
//		
//		
//		
//		
//		try {
//			switch(objectType){
//			case Point:
//				parsePoint(current);
//				break;
//			case Line:
//				parseLine(current);
//				break;
//			case Polygone:
//				parsePolygone(current);	
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		
		
		
	}

	
	
	private void writeGeometry(Geometry g, List<Object> schemaValues, int subId, String name) throws Exception {
		if(g instanceof Point) {
			Row row = getRowForCoordinates(((Point)g).getCoordinates().get(0), schemaValues, subId, name);
			writeRow(row);
		}
		else if(g instanceof LineString) {
			List<Coordinate> coords = ((LineString)g).getCoordinates();
			for(Coordinate coord : coords) {
				Row row = getRowForCoordinates(coord, schemaValues, subId, name);
				writeRow(row);
			}
		}
		else if(g instanceof Polygon) {
			List<Coordinate> coords = ((Polygon)g).getOuterBoundaryIs().getLinearRing().getCoordinates();
			for(Coordinate coord : coords) {
				Row row = getRowForCoordinates(coord, schemaValues, subId, name);
				writeRow(row);
			}
		}
			
		
	}
	
	private Row getRowForCoordinates(Coordinate coord, List<Object> schemaValues, int subId, String name) throws Exception {
		String longitude = String.valueOf(coord.getLongitude());
		String latitude = String.valueOf(coord.getLatitude());
		String altitude = String.valueOf(coord.getAltitude());
		
		Row row = RowFactory.createRow(this);
		int i = 0;
		for(Object o : schemaValues) {
			row.set(i, o);
			i++;
		}
		row.set(i, latitude);
		row.set(i+1, longitude);
		row.set(i+2, altitude);
		row.set(i+3, subId);
		row.set(i+4, name);
		
		return row;
	}

	private Object castValue(String value, String className) {
		if(className.equals("java.lang.Integer")) {
			if(value == null) {
				return 0;
			}
			return Integer.parseInt(value.trim());
		}
		else if(className.equals("java.lang.Float")) {
			if(value == null) {
				return 0.0;
			}
			return Float.parseFloat(value.trim());
		}
		else if(className.equals("java.lang.Double")) {
			if(value == null) {
				return 0.0;
			}
			return Double.parseDouble(value.trim());
		}
		else if(className.equals("java.lang.Long")) {
			if(value == null) {
				return 0;
			}
			return Long.parseLong(value.trim());
		}
		if(value == null) {
			return "";
		}
		return value.trim();
	}

	private SimpleData findData(StreamElement element, List<SimpleData> data) {
		for(SimpleData d : data) {
			if(element.name.toLowerCase().equals(d.getName().toLowerCase())) {
				return d;
			}
		}
		return null;
	}

//	private void parsePoint(Element element) throws Exception {
//		Row row = RowFactory.createRow(this);
//		
//		
//		String s = element.element("coordinates").getStringValue();
//		float[] coord = parseCoordinates(s);
//		
//		for(int i = 0; i < 3; i++){
//			row.set(i, coord[i]);
//		}
//		
//		if (!extractId){
//			row.set(3, id);
//			id++;
//		}
//		else{
//			Element placemarker = element.getParent();
//			
//			while(placemarker != null && !placemarker.getName().equals("Placemark")){
//				placemarker = placemarker.getParent();
//			}
//			if (placemarker != null && placemarker.element("name") != null){
//				row.set(3, placemarker.element("name").getStringValue());
//			}
//			else{
//				if (id == null){
//					id = 0;
//				}
//				row.set(3, id);
//				id++;
//			}
//			
//		}
//		readedRows++;
//		
//		
//		writeRow(row);
//	}
//	
//	private void parseLine(Element element)throws Exception {
//		String s = element.element("coordinates").getStringValue();
//		
//		for(String _c : s.split("\n|\r|\t")){
//			if (_c.length() == 0){
//				continue;
//			}
//			float[] coord = parseCoordinates(_c);
//			Row row = RowFactory.createRow(this);
//			for(int i = 0; i < 3; i++){
//				row.set(i, coord[i]);
//			}
//			if (!extractId){
//				row.set(3, id);
//				
//			}
//			else{
//				Element placemarker = element.getParent();
//				
//				while(placemarker != null && !placemarker.getName().equals("Placemark")){
//					placemarker = placemarker.getParent();
//				}
//				if (placemarker != null && placemarker.element("name") != null){
//					row.set(3, placemarker.element("name").getStringValue());
//				}
//				else{
//					row.set(3, id);
//					
//				}
//				
//			}
//			readedRows++;
//			
//			
//			writeRow(row);
//		}
//		
//		if (id != null){
//			id++;
//		}
//		
//		
//		
//	}
//	
//	private void parsePolygone(Element element)throws Exception {
//		HashMap map = new HashMap();
//		map.put( "kml", "http://www.opengis.net/kml/2.2");
//		org.dom4j.XPath xpath = element.createXPath( "*/kml:LinearRing");//new Dom4jXPath( "//kml:LinearRing");
//		xpath.setNamespaceContext( new SimpleNamespaceContext( map));
//		
//		List<Element> elements = xpath.selectNodes(element);
//		if(elements == null || elements.isEmpty()) {
//			map.put( "kml", "http://earth.google.com/kml/2.2");
//			xpath = element.createXPath( "*/kml:LinearRing");//new Dom4jXPath( "//kml:LinearRing");
//			xpath.setNamespaceContext( new SimpleNamespaceContext( map));
//			
//			elements = xpath.selectNodes(element);
//		}
//		for(Object o : elements){
//			String s = ((Element)o).element("coordinates").getStringValue();
//			
//			for(String _c : s.split("\n|\r|\t|\\s+")){
//				if (_c.length() == 0 || _c.replaceAll("\\s+", "").length() == 0){
//					continue;
//				}
//				float[] coord = parseCoordinates(_c);
//				Row row = RowFactory.createRow(this);
//				for(int i = 0; i < 3; i++){
//					row.set(i, coord[i]);
//				}
//				if (!extractId){
//					row.set(3, id);
//					
//				}
//				else{
//					Element placemarker = element.getParent();
//					
//					while(placemarker != null && !placemarker.getName().equals("Placemark")){
//						placemarker = placemarker.getParent();
//					}
//					if (placemarker != null && placemarker.element("name") != null){
//						row.set(3, placemarker.element("name").getStringValue());
//					}
//					else{
//						if (id == null){
//							id = 0;
//						}
//						row.set(3, id);
//						
//					}
//					
//				}
//				readedRows++;
//				
//				
//				writeRow(row);
//			}
//			if (id != null){
//				id = new Object().hashCode();
//			}
//		}
//	
//		
//	}
//	
//	private float[] parseCoordinates(String s){
//		float[] coords = new float[3];
//		int i = 0;
//		for(String c : s.split(",")){
//			coords[i++] = Float.parseFloat(c.replaceAll("\\s+", ""));
//		}
//		return coords;
//	}
	
	@Override
	public void releaseResources() {
		document = null;
//		rowIterator = null;
//		document = null;
//		try{
//			fis.close();
//		}catch(Exception ex){
//			
//		}
		info(" resources released");
		
	}

}
