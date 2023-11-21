package bpm.gateway.core.transformations.calcul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.file.KMLHelper;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.calculation.RunGeoFilter;
import bpm.vanilla.platform.core.IRepositoryContext;

public class GeoFilter extends AbstractTransformation implements Trashable {

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	private String geoElementName = "";
	// private String geoShape = "";
	private String trashName;
	private Transformation trashTransformation;

	private List<GeoCondition> conditions = new ArrayList<>();
	private List<Polygon> polygons = new ArrayList<>();
	
	private Map<String, List<String>> placemarks = new HashMap<>();

	public GeoFilter() {
	}
	
	@Override
	public Transformation copy() {
		GeoFilter copy = new GeoFilter();

		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setGeoElementName(geoElementName);
		// copy.setGeoShape(geoShape);

		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		return "";
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("geoFilter");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		e.addElement("geoElementName").setText(geoElementName);
		// e.addElement("geoShape").setText(geoShape);
		
		if(conditions != null) {
			for(GeoCondition cond : conditions) {
				Element ce = e.addElement("condition");
				ce.addElement("targetName").setText(cond.targetName);
				ce.addElement("inputKml").setText(cond.inputKml);
				ce.addElement("placeMarkName").setText(cond.placeMarkName);
				ce.addElement("geoShape").setText(cond.geoShape);
			}
		}
		
		for(Polygon poly : polygons) {
			Element ce = e.addElement("polygon");
			ce.addElement("name").setText(poly.getName());
			for(PointGeo p : poly.getPoints()) {
				Element pe = ce.addElement("point");
				pe.addElement("x").setText(p.x + "");
				pe.addElement("y").setText(p.y + "");
			}
		}

		if(getTrashTransformation() != null) {
			e.addElement("trashOuput").setText(trashTransformation.getName());
		}

		if(descriptor != null) {
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunGeoFilter(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		try {
			if(!isInited()) {
				return;
			}
			for(Transformation t : getInputs()) {
				if(!(t instanceof KMLInput)) {
					for(StreamElement e : t.getDescriptor(this).getStreamElements()) {
						descriptor.addColumn(e.clone(getName(), t.getName()));
					}
				}
			}
		} catch(ServerException e) {
			e.printStackTrace();
		}
	}

	public String getGeoElementName() {
		return geoElementName;
	}

	public void setGeoElementName(String geoElementName) {
		this.geoElementName = geoElementName;
	}

	public Transformation getTrashTransformation() {
		if(trashName != null && getDocument() != null) {
			trashTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trashTransformation;
	}

	public void setTrashTransformation(Transformation transfo) {
		this.trashTransformation = transfo;

	}

	public void setTrashTransformation(String name) {
		this.trashName = name;
	}

	public List<GeoCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<GeoCondition> conditions) {
		this.conditions = conditions;
	}
	
	public void addCondition(GeoCondition cond) {
		this.conditions.add(cond);
	}

	
	public List<Polygon> getPolygons() {
		return polygons;
	}

	public void setPolygons(List<Polygon> polygons) {
		this.polygons = polygons;
	}
	
	public void addPolygon(Polygon p ) {
		polygons.add(p);
	}
	
	public List<String> getPlaceMarerskByKml(String kmlInput) throws Exception {
//		placemarks.clear();
		if(placemarks.get(kmlInput) != null) {
			return placemarks.get(kmlInput);
		}
		for(Transformation t : getInputs()) {
			if(t.getName().equals(kmlInput)) {
				List<String> ps =  KMLHelper.getKmlPlacemarks( (KMLInput) t);
				placemarks.put(kmlInput, ps);
				return ps;
			}
		}
		
		return getPolygonNames();
	}


	private List<String> getPolygonNames() {
		List<String> names = new ArrayList<>();
		for(Polygon p : polygons) {
			names.add(p.getName());
		}
		return names;
	}


	public static class Polygon {
		private String name;
		private List<PointGeo> points = new ArrayList<>();
		
		public Polygon() {
			
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<PointGeo> getPoints() {
			return points;
		}
		public void setPoints(List<PointGeo> points) {
			this.points = points;
		}
		public void addPoint(PointGeo p) {
			points.add(p);
		}
		public String getPointsAsString() {
			StringBuilder buf = new StringBuilder();
			boolean first = true;
			for(PointGeo p : getPoints()) {
				if(first) {
					first = false;
				}
				else {
					buf.append(" ");
				}
				buf.append(String.valueOf(p.y) + "," + String.valueOf(p.x));
			}
			
			return buf.toString();
		}
		public void setPointsFromString(String string) {
			String[] coords = string.split("\\r?\\n");
			if(coords.length <= 1) {
				coords = string.split(" ");
			}
			List<PointGeo> polygon = new ArrayList<>();
			for(String coord : coords) {
				String[] c = coord.split(",");
				PointGeo g = new PointGeo();
				g.y = Double.parseDouble(c[0]);
				g.x = Double.parseDouble(c[1]);
				polygon.add(g);
			}
			setPoints(polygon);
		}
	}
	
	public static class PointGeo {
		public double x;
		public double y;
		
		public PointGeo() {
			
		}
		
		public void setX(String x) {
			this.x = Double.parseDouble(x);
		}
		
		public void setY(String y) {
			this.y = Double.parseDouble(y);
		}
	}
	
}
