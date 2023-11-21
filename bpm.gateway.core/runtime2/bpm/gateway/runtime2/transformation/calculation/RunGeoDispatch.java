package bpm.gateway.runtime2.transformation.calculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.tools.Coordinate;
import bpm.gateway.core.transformations.calcul.GeoDispatch;
import bpm.gateway.core.transformations.calcul.GeoFilter.PointGeo;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunGeoDispatch extends RuntimeStep {

	private RuntimeStep trashOutput;

	private boolean onlyOneColumnGeoloc;
	private int latitudeColumnIndex;
	private Integer longitudeColumnIndex;
	
	private Integer placemarkIdIndex;
	private Integer inputReferenceLatitudeIndex;
	private Integer inputReferenceLongitudeIndex;
	private String defaultPlacemarkId;

	private Map<String, List<PointGeo>> geoConditions;
	private Map<RuntimeStep, List<Row>> kmlRows = new HashMap<>();

	public RunGeoDispatch(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void insertRow(final Row data, final RuntimeStep caller) throws InterruptedException {
		if (caller.getTransformation() instanceof KMLInput) {
			if (kmlRows.get(caller) == null) {
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
		// System.out.println(new Date().getTime());

		boolean finishedKml = false;
		while (!(finishedKml)) {
			try {
				if (inputs.size() == 1) {
					finishedKml = true;
					break;
				}
				for (RuntimeStep t : inputs) {
					if (t.getTransformation() instanceof KMLInput) {
						if (t.isEnd()) {
							finishedKml = true;
						}
						else {
							finishedKml = false;
							break;
						}
					}
				}
				if (!finishedKml) {
					Thread.sleep(50);
				}
			} catch (Exception e) {

			}

		}

		if (areInputStepAllProcessed()) {
			if (inputEmpty()) {
				setEnd();
			}
		}

		if (isEnd() && inputEmpty()) {
			return;
		}

		if (!isEnd() && inputEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch (Exception e) {

			}
		}
		if (geoConditions == null) {
			geoConditions = new HashMap<String, List<PointGeo>>();
			for (RuntimeStep input : inputs) {
				if (input.getTransformation() instanceof KMLInput) {
					for (Row r : kmlRows.get(input)) {

						String value = r.get(placemarkIdIndex).toString();
						if (value != null) {
							if (geoConditions.get(value) == null) {
								geoConditions.put(value, new ArrayList<PointGeo>());
							}
							PointGeo p = new PointGeo();
							p.x = Double.parseDouble(r.get(inputReferenceLatitudeIndex).toString());
							p.y = Double.parseDouble(r.get(inputReferenceLongitudeIndex).toString());
							geoConditions.get(value).add(p);
						}
					}
				}
			}
		}

		Row row = readRow();
		
		Row newRow = RowFactory.createRow(this, row);
		for (int i=0; newRow.getMeta().getSize() < row.getMeta().getSize(); i++) {
			newRow.set(i, row.get(i));
		}
		String zoneId = findOutput(row);
		newRow.set(newRow.getMeta().getSize() - 1, zoneId != null ? zoneId : defaultPlacemarkId);

		if (zoneId == null && trashOutput != null) {
			trashRow(newRow);
		}
		else {
			writeRow(newRow);
		}
	}

	private String findOutput(Row row) {
		try {
			Coordinate coordinate = getCoordinate(row);

			for (String zoneId : geoConditions.keySet()) {
				List<PointGeo> polygon = geoConditions.get(zoneId);
				double x = coordinate.getLatitude();
				double y = coordinate.getLongitude();
				
				int i;
				int j;
				boolean result = false;
				for (i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
					if ((polygon.get(i).y > y) != (polygon.get(j).y > y) && (x < (polygon.get(j).x - polygon.get(i).x) * (y - polygon.get(i).y) / (polygon.get(j).y - polygon.get(i).y) + polygon.get(i).x)) {
						result = !result;
					}
				}
				if (result) {
					return zoneId;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	private Coordinate getCoordinate(Row row) {
		String latitude = row.get(latitudeColumnIndex).toString();
		String longitude = row.get(longitudeColumnIndex).toString();
		
		double x = -1;
		double y = -1;
		if (onlyOneColumnGeoloc) {
			latitude = latitude.replace(" ", "");
			String[] c = latitude.split(";");
			if (c.length <= 1) {
				c = latitude.split(",");
			}
			x = Double.parseDouble(c[0]);
			y = Double.parseDouble(c[1]);
		}
		else {
			x = Double.parseDouble(latitude);
			y = Double.parseDouble(longitude);
		}
		return new Coordinate(x, y, -1);
	}

	private void trashRow(Row row) throws InterruptedException {
		if (trashOutput != null) {
			trashOutput.insertRow(row, this);
			writedRows++;
		}
	}

	@Override
	protected void writeRow(Row row) throws InterruptedException {
		for (RuntimeStep r : getOutputs()) {
			if (r != trashOutput) {
				r.insertRow(row, this);
			}
		}
		writedRows++;
	}

	@Override
	public void releaseResources() {
		info("Resources released");
	}

	@Override
	public void init(Object adapter) throws Exception {
		this.onlyOneColumnGeoloc = ((GeoDispatch) transformation).isOnlyOneColumnGeoloc();
		this.latitudeColumnIndex = ((GeoDispatch) transformation).getLatitudeIndex();
		this.longitudeColumnIndex = ((GeoDispatch) transformation).getLongitudeIndex();
		this.placemarkIdIndex = ((GeoDispatch) transformation).getPlacemarkIdIndex();
		this.inputReferenceLatitudeIndex = ((GeoDispatch) transformation).getInputReferenceLatitudeIndex();
		this.inputReferenceLongitudeIndex = ((GeoDispatch) transformation).getInputReferenceLongitudeIndex();
		this.defaultPlacemarkId = ((GeoDispatch) transformation).getDefaultPlacemarkId();

		for (RuntimeStep rs : getOutputs()) {
			if (rs.getTransformation() == ((GeoDispatch) getTransformation()).getTrashTransformation()) {
				trashOutput = rs;
			}
		}
		info(" inited");
	}

}
