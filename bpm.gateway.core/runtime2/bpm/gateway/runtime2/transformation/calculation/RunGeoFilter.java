package bpm.gateway.runtime2.transformation.calculation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.calcul.GeoCondition;
import bpm.gateway.core.transformations.calcul.GeoFilter;
import bpm.gateway.core.transformations.calcul.GeoFilter.PointGeo;
import bpm.gateway.core.transformations.calcul.GeoFilter.Polygon;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunGeoFilter extends RuntimeStep {

	private int geoColumnIndex;
//	private List<PointGeo> polygon;
	
	//used for circle
//	private double latKm;
//	private double lonKm;
//	private int radius;
	private RuntimeStep trashOutput;
	
	private Map<GeoCondition, List<PointGeo>> geoConditions;
	private Map<RuntimeStep, List<Row>> kmlRows = new HashMap<>();

	public RunGeoFilter(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}
	
	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {
		if(caller.getTransformation() instanceof KMLInput) {
			if(kmlRows.get(caller) == null) {
				kmlRows.put(caller, new ArrayList<Row>());
			}
			kmlRows.get(caller).add(data);
		}
		else {
			super.insertRow(data, caller);
		}
	}

	@Override
	public void performRow() throws Exception {
//		System.out.println(new Date().getTime());
		
		boolean finishedKml = false;
		while(!(finishedKml)) {
			try {
				if(inputs.size() == 1) {
					finishedKml = true;
					break;
				}
				for(RuntimeStep t : inputs) {
					if(t.getTransformation() instanceof KMLInput) {
						if(t.isEnd()) {
							finishedKml = true;
						}
						else {
							finishedKml = false;
							break;
						}
					}
				}
				if(!finishedKml) {
					Thread.sleep(50);
				}
			} catch(Exception e) {

			}

		}
		
		
		if(areInputStepAllProcessed()) {
			if(inputEmpty()) {
				setEnd();
			}
		}

		if(isEnd() && inputEmpty()) {
			return;
		}

		if(!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch(Exception e) {

			}
		}
		if(geoConditions == null) {
			geoConditions = new HashMap<>();
			for(RuntimeStep input : inputs) {
				if(input.getTransformation() instanceof KMLInput) {
					for(Row r : kmlRows.get(input)) {
						
						String value = r.get(4).toString();
						
						GeoCondition cond = findGeocondition(input, value);
						if(cond != null) {
							if(geoConditions.get(cond) == null) {
								geoConditions.put(cond, new ArrayList<PointGeo>());
							}
							PointGeo p = new PointGeo();
							p.x = Double.parseDouble(r.get(0).toString());
							p.y = Double.parseDouble(r.get(1).toString());
							geoConditions.get(cond).add(p);
						}
					}
				}
			}
			for(GeoCondition cond : ((GeoFilter)getTransformation()).getConditions()) {
				if(cond.getInputKml().equals("Polygons")) {
					for(Polygon p : ((GeoFilter)getTransformation()).getPolygons()) {
						if(cond.getPlaceMarkName().equals(p.getName())) {
							geoConditions.put(cond, p.getPoints());
							break;
						}
					}
				}
			}
			
		}

		Row row = readRow();

		GeoCondition output = findOutput(row);
		if(output == null) {
			trashRow(row);
		}
		else {
			writeRow(row, output.targetName);
		}
		
//		if(filterValidated(row)) {
//			writeRow(row);
//		}
//		else {
//			trashRow(row);
//		}
	}
	
	private GeoCondition findGeocondition(RuntimeStep input, String r) {
		for(GeoCondition c : ((GeoFilter)getTransformation()).getConditions()) {
			if(c.getInputKml().equals(input.getTransformation().getName()) && c.getPlaceMarkName().equals(r)) {
				return c;
			}
		}
		return null;
	}

	private GeoCondition findOutput(Row row) {
		Date date = new Date();
		try {
			
			String coord = row.get(geoColumnIndex).toString();
			coord = coord.replace(" ", "");
			String[] c = coord.split(";");
			if(c.length <= 1) {
				c = coord.split(",");
			}
			double x = Double.parseDouble(c[0]);
			double y = Double.parseDouble(c[1]);
			
			for(GeoCondition condition : geoConditions.keySet()) {
				List<PointGeo> polygon = geoConditions.get(condition);
				int i;
				int j;
				boolean result = false;
				for(i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
					if((polygon.get(i).y > y) != (polygon.get(j).y > y) && (x < (polygon.get(j).x - polygon.get(i).x) * (y - polygon.get(i).y) / (polygon.get(j).y - polygon.get(i).y) + polygon.get(i).x)) {
						result = !result;
					}
				}
				if(result) {
//					System.out.println("time : " + (new Date().getTime() - date.getTime()));
					return condition;
				}
			}
		} catch(Exception e) {
		} 
//		System.out.println("time : " + (new Date().getTime() - date.getTime()));
		return null;
	}

	protected void writeRow(Row row, String transfo) throws InterruptedException{
		for(RuntimeStep r : getOutputs()){
			if(r.getTransformation().getName().equals(transfo)) {
				r.insertRow(row, this);
			}
		}

		writedRows++;
	}
	
	private void trashRow(Row row) throws InterruptedException {
		if(trashOutput != null) {
			trashOutput.insertRow(row, this);
			writedRows++;
		}
	}
	
	@Override
	protected void writeRow(Row row) throws InterruptedException {
		for(RuntimeStep r : getOutputs()) {
			if(r != trashOutput) {
				r.insertRow(row, this);
			}
		}
		writedRows++;
	}

//	private boolean filterValidated(Row row) {
//		if(polygon != null) {
//			try {
//				String coord = row.get(geoColumnIndex).toString();
//				coord = coord.replace(" ", "");
//				String[] c = coord.split(";");
//				if(c.length <= 1) {
//					c = coord.split(",");
//				}
//				double x = Double.parseDouble(c[0]);
//				double y = Double.parseDouble(c[1]);
//
//				int i;
//				int j;
//				boolean result = false;
////				System.out.println("x : " + x + " -- y : " + y);
//				for(i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
//					if((polygon.get(i).y > y) != (polygon.get(j).y > y) && (x < (polygon.get(j).x - polygon.get(i).x) * (y - polygon.get(i).y) / (polygon.get(j).y - polygon.get(i).y) + polygon.get(i).x)) {
//						result = !result;
//					}
//				}
//				return result;
//			} catch(Exception e) {
//				return false;
//			}
//		}
//		else {
//			try {
//				String coord = row.get(geoColumnIndex).toString();
//				coord = coord.replace(" ", "");
//				String[] c = coord.split(";");
//				if(c.length <= 1) {
//					c = coord.split(",");
//				}
//
//				double x = Double.parseDouble(c[0]);
//				double y = Double.parseDouble(c[1]);
//				
//				y = y * (40075 * Math.cos(x) / 360);
//				x = x * 111.32;
//				
//				return isInside(latKm, lonKm, radius, x, y);
//			} catch(Exception e) {
//				return false;
//			}
//		}
//	}

	private boolean isInside(double circle_x, double circle_y, int rad, double x, double y) {
		// Compare radius of circle with distance
		// of its center from given point
		if((x - circle_x) * (x - circle_x) + (y - circle_y) * (y - circle_y) <= rad * rad)
			return true;
		else
			return false;
	}

	@Override
	public void releaseResources() {
		info("Resources released");
	}

	@Override
	public void init(Object adapter) throws Exception {

		for(RuntimeStep rs : getOutputs()) {
			if(rs.getTransformation() == ((GeoFilter) getTransformation()).getTrashTransformation()) {
				trashOutput = rs;
			}
		}
		int i = 0;
		int shift = 0;
		for(Transformation t : ((GeoFilter) transformation).getInputs()) {
			if(!(t instanceof KMLInput || t instanceof FileInputShape)) {
				break;
			}
			i++;
		}
		shift = i * 5;
		i = 0;
		for(StreamElement elem : ((GeoFilter) transformation).getDescriptor(null).getStreamElements()) {
			if(elem.name.equals(((GeoFilter) transformation).getGeoElementName())) {
				geoColumnIndex = i;
				break;
			}
			i++;
		}
//		geoColumnIndex = geoColumnIndex + shift;

//		if(((GeoFilter) transformation).getGeoShape().contains("circle")) {
//			String[] coords = ((GeoFilter) transformation).getGeoShape().replace("circle(", "").replace(")", "").split(",");
//
//			double lat = Double.parseDouble(coords[1]);
//			double lon = Double.parseDouble(coords[0]);
//			radius = Integer.parseInt(coords[2]);
//
//			latKm = lat * 111.32;
//			lonKm = lon * (40075 * Math.cos(lat) / 360);
//			// minLat = latKm - radius;
//			// maxLat = latKm + radius;
//			// minLon = lonKm - radius;
//			// maxLon = lonKm + radius;
//
//		}
//		else {
//			String[] coords = ((GeoFilter) transformation).getGeoShape().split("\\r?\\n");
//			if(coords.length <= 1) {
//				coords = ((GeoFilter) transformation).getGeoShape().split(" ");
//			}
//			polygon = new ArrayList<>();
//			for(String coord : coords) {
//				String[] c = coord.split(",");
//				PointGeo g = new PointGeo();
//				g.y = Double.parseDouble(c[0]);
//				g.x = Double.parseDouble(c[1]);
//				polygon.add(g);
//			}
//		}

		info(" inited");
	}


}
