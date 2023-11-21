package bpm.gateway.core.server.file;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.kml.KMLInput;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Schema;
import de.micromata.opengis.kml.v_2_2_0.SimpleField;

public class KMLHelper {
	
	public static List<String> getKmlPlacemarks(KMLInput t) throws Exception {
//		KMLInput fXml;
//		if(t instanceof MdmContractFileInput) {
//			fXml = (KMLInput) ((MdmContractFileInput)t).getFileTransfo();
//		}
//		else {
//			fXml = (KMLInput) t;
//		}
		InputStream file = ((AbstractFileServer) t.getServer()).getInpuStream(t);
		Kml kml = Kml.unmarshal(file);
		
		Document document;
		try {
			document = (Document) ((Document) kml.getFeature()).getFeature().get(0);
		} catch(Exception e) {
			document = ((Document) kml.getFeature());
		}
		
		
		Iterator<Feature> rowIterator;
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
		
		List<String> placeMarks = new ArrayList<>();
		
		while(rowIterator.hasNext()) {
			Feature feature = rowIterator.next();
			if(feature instanceof Placemark) {
				Placemark placemark = (Placemark) feature;
				placeMarks.add(placemark.getName());
			}
		}
		
		
		return placeMarks;
	}

	public static StreamDescriptor getKmlDescriptor(String transfoName, KMLInput transfo) throws Exception {

		DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

		InputStream file = ((AbstractFileServer) transfo.getServer()).getInpuStream(transfo);
		Kml kml = Kml.unmarshal(file);
		
		Document doc = (Document) kml.getFeature();
		try {
			Schema schema = doc.getSchema().get(0);
			
			List<SimpleField> columns = schema.getSimpleField();
			for(SimpleField field : columns) {
				StreamElement e = new StreamElement();
				e.className = getClassName(field.getType());
				e.name = field.getName();
				e.typeName = field.getType();
				e.originTransfo = transfoName;
				e.transfoName = transfoName;
				descriptor.addColumn(e);
			}
		} catch(Exception e1) {
		}
		
		//add coordinates data
		StreamElement e = new StreamElement();
		e.className = getClassName("string");
		e.name = "latitude";
		e.typeName = "string";
		e.transfoName = transfoName;
		e.originTransfo = transfoName;
		descriptor.addColumn(e);
		
		e = new StreamElement();
		e.className = getClassName("string");
		e.name = "longitude";
		e.typeName = "string";
		e.transfoName = transfoName;
		e.originTransfo = transfoName;
		descriptor.addColumn(e);
		
		e = new StreamElement();
		e.className = getClassName("string");
		e.name = "altitude";
		e.typeName = "string";
		e.transfoName = transfoName;
		e.originTransfo = transfoName;
		descriptor.addColumn(e);
		
		e = new StreamElement();
		e.className = getClassName("string");
		e.name = "sub_id";
		e.typeName = "string";
		e.transfoName = transfoName;
		e.originTransfo = transfoName;
		descriptor.addColumn(e);
		
		e = new StreamElement();
		e.className = getClassName("string");
		e.name = "placemark_name";
		e.typeName = "string";
		e.transfoName = transfoName;
		e.originTransfo = transfoName;
		descriptor.addColumn(e);
		
		return descriptor;
	}
	
	private static String getClassName(String type) {
		switch(type.toLowerCase()) {
			case "string":
				return "java.lang.String";
			case "int":
				return "java.lang.Integer";
			case "double":
				return "java.lang.Double";
			case "float":
				return "java.lang.Float";
			case "long":
				return "java.lang.Long";
				
		}
		
		return "java.lang.String";
	}
}
