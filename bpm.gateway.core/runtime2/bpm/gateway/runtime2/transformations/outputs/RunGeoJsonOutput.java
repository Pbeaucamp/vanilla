package bpm.gateway.runtime2.transformations.outputs;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.transformations.outputs.GeoJsonOutput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class RunGeoJsonOutput extends RuntimeStep {

	private GeoJson json = new GeoJson();
	
	public RunGeoJsonOutput(GeoJsonOutput transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			  try {
			      Thread.sleep(10);
			  }
			  catch (InterruptedException e) {
			       Thread.currentThread().interrupt(); // restore interrupted status
			  }
			return;
		}
		
		
		Row row = readRow();
		
		if (row == null){
			return;
		}
		
		int indexLat = inputs.get(0).getTransformation().getDescriptor(null).getElementIndex(((GeoJsonOutput)transformation).getLatitudeColumn());
		int indexLong = inputs.get(0).getTransformation().getDescriptor(null).getElementIndex(((GeoJsonOutput)transformation).getLongitudeColumn());
		
		String lat = row.get(indexLat).toString();
		String longi = row.get(indexLong).toString();
		
		List<Property> props = new ArrayList<>();
		for(int i = 0 ; i < row.getMeta().getSize() ; i++) {
			if(i != indexLat && i != indexLong) {
				Property p = new Property();
				p.setName(inputs.get(0).getTransformation().getDescriptor(null).getColumnName(i));
				try {
					p.setValue(row.get(i).toString());
				} catch(Exception e) {
					p.setValue("");
				}
				props.add(p);
			}
		}
		
		
		Feature feature = null;
		if(((GeoJsonOutput)transformation).getGeometryType().equals(GeoJsonOutput.TYPE_POINT)) {
			feature = new Feature();
			
			GeoPoint geometry = new GeoPoint();
			geometry.setType(((GeoJsonOutput)transformation).getGeometryType());
			geometry.setCoordinates(new String[]{lat, longi});
			feature.setGeometry(geometry);
			
			json.getFeatures().add(feature);
		}
		else {
			if(((GeoJsonOutput)transformation).getGeometryType().equals(GeoJsonOutput.TYPE_LINESTRING) || ((GeoJsonOutput)transformation).getGeometryType().equals(GeoJsonOutput.TYPE_MULTIPOINT) || ((GeoJsonOutput)transformation).getGeometryType().equals(GeoJsonOutput.TYPE_POLYGON)) {
				feature = getFeature(props);
				if(feature == null) {
					feature = new Feature();
					json.getFeatures().add(feature);
				}
				if(feature.getGeometry() == null) {
					GeoLineMultiPoint geo = new GeoLineMultiPoint();
					geo.setType(((GeoJsonOutput)transformation).getGeometryType());
					feature.setGeometry(geo);
				}
				((GeoLineMultiPoint)feature.getGeometry()).getCoordinates().add(new String[]{lat, longi});
			}
//			else if(((GeoJsonOutput)transformation).getGeometryType().equals(GeoJsonOutput.TYPE_POLYGON) || ((GeoJsonOutput)transformation).getGeometryType().equals(GeoJsonOutput.TYPE_MULTILINESTRING)) {
//				Feature f = getFeature(props);
//				if(f.getGeometry() == null) {
//					GeoPolygonMultiLine geo = new GeoPolygonMultiLine();
//					geo.setType(((GeoJsonOutput)transformation).getGeometryType());
//					f.setGeometry(geo);
//				}
//				((GeoPolygonMultiLine)f.getGeometry()).getCoordinates().add(new String[]{lat, longi});
//			}
//			else if(((GeoJsonOutput)transformation).getGeometryType().equals(GeoJsonOutput.TYPE_MULTIPOLYGON)) {
//				
//			}
		}
		
		for(Property p : props) {
			feature.addProperty(p);
		}
	}

	private Feature getFeature(List<Property> props) {
		return json.getFeatureByProps(props);
	}

	@Override
	public void releaseResources() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Property.class, new JsonSerializer<Property>() {

			@Override
			public JsonElement serialize(Property arg0, Type arg1, JsonSerializationContext arg2) {
				JsonObject jsonObj = new JsonObject();

				jsonObj.addProperty(arg0.getName(), arg0.getValue());

		        return jsonObj;
			}
		});
		Gson gson = gsonBuilder.create();
		String result = gson.toJson(json);
		
		try {
			String fileName = ((GeoJsonOutput)transformation).getDocument().getStringParser().getValue(getTransformation().getDocument(), ((GeoJsonOutput)transformation).getFilePath());
			Files.write(Paths.get(fileName), result.getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		json = new GeoJson();
	}

	public class GeoJson {
		private String type = "FeatureCollection";
		private List<Feature> features = new ArrayList<>();

		public String getType() {
			return type;
		}

		public Feature getFeatureByProps(List<Property> props) {
			LOOP:for(Feature f : features) {
				for(Property p : props) {
					if(!f.getProperties().contains(p)) {
						continue LOOP;
					}
				}
				return f;
			}
			return null;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<Feature> getFeatures() {
			return features;
		}

		public void setFeatures(List<Feature> features) {
			this.features = features;
		}

	}

	public class Feature {
		private String type = "Feature";
		private Geometry geometry;
		private List<Property> properties = new ArrayList<>();

		public String getType() {
			return type;
		}

		public void addProperty(Property p) {
			if(!properties.contains(p)) {
				properties.add(p);
			}
		}

		public void setType(String type) {
			this.type = type;
		}

		public Geometry getGeometry() {
			return geometry;
		}

		public void setGeometry(Geometry geometry) {
			this.geometry = geometry;
		}

		public List<Property> getProperties() {
			return properties;
		}

		public void setProperties(List<Property> properties) {
			this.properties = properties;
		}

	}

	public abstract class Geometry {
		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public class GeoPoint extends Geometry {
		private String[] coordinates;

		public String[] getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(String[] coordinates) {
			this.coordinates = coordinates;
		}
	}

	public class GeoLineMultiPoint extends Geometry {
		private List<String[]> coordinates = new ArrayList<>();

		public List<String[]> getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(List<String[]> coordinates) {
			this.coordinates = coordinates;
		}

	}
	
	public class GeoPolygonMultiLine extends Geometry {
		private List<List<String[]>> coordinates = new ArrayList<>();

		public List<List<String[]>> getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(List<List<String[]>> coordinates) {
			this.coordinates = coordinates;
		}

	}
	
	public class GeoMultiPolygon extends Geometry {
		private List<List<List<String[]>>> coordinates = new ArrayList<>();

		public List<List<List<String[]>>> getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(List<List<List<String[]>>> coordinates) {
			this.coordinates = coordinates;
		}

	}

	public class Property {
		private String name;
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(getClass() != obj.getClass())
				return false;
			Property other = (Property) obj;
			if(!getOuterType().equals(other.getOuterType()))
				return false;
			if(name == null) {
				if(other.name != null)
					return false;
			}
			else if(!name.equals(other.name))
				return false;
			if(value == null) {
				if(other.value != null)
					return false;
			}
			else if(!value.equals(other.value))
				return false;
			return true;
		}

		private RunGeoJsonOutput getOuterType() {
			return RunGeoJsonOutput.this;
		}

	}
}
