package bpm.gateway.runtime2.tools;

import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.tools.AdresseGeoLocHelper;
import bpm.gateway.core.tools.Coordinate;
import bpm.gateway.core.transformations.Geoloc;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RuntimeGeoloc extends RuntimeStep {

	private boolean onlyOneColumn;
	private Integer libelleIndex, postalCodeIndex;
	private Double score;
	
	private boolean onlyOneColumnOutput;
	
	private List<String> fields;

	public RuntimeGeoloc(Geoloc transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		Geoloc geoloc = (Geoloc) getTransformation();
		
		onlyOneColumn = geoloc.isOnlyOneColumnAdress();

		if (geoloc.getInputs().size() != 1) {
			String message = " Geoloc cannot have more or less than one Input at runtime";
			error(message);
			throw new Exception(message);
		}
		try {
			libelleIndex = geoloc.getLibelleIndex();
			if (libelleIndex < 0) {
				throw new Exception("");
			}
		} catch (Exception ex) {
			String message = " Geoloc need an address Field";
			error(message);
			throw new Exception(message);
		}
		
		if (!onlyOneColumn) {
			try {
				postalCodeIndex = geoloc.getPostalCodeIndex();
				if (postalCodeIndex < 0) {
					throw new Exception("");
				}
			} catch (Exception ex) {
				String message = " Geoloc need a postal code Field";
				error(message);
				throw new Exception(message);
			}
		}

		if (geoloc.getScore() != null) {
			score = geoloc.getScore().doubleValue() / 100;
		}
		
		onlyOneColumnOutput = geoloc.isOnlyOneColumnOutput();
		if (geoloc.getFirstColunmName() == null || geoloc.getFirstColunmName().isEmpty()) {
			String message = " Geoloc first column name must be defined ";
			error(message);
			throw new Exception(message);
		}
		
		if (!onlyOneColumnOutput && (geoloc.getSecondColunmName() == null || geoloc.getSecondColunmName().isEmpty())) {
			String message = " Geoloc second column name must be defined ";
			error(message);
			throw new Exception(message);
		}
		
		this.fields = geoloc.getFields();
		
//		if (geoloc.getScoreColumnName() == null || geoloc.getScoreColumnName().isEmpty()) {
//			String message = " Geoloc score column name must be defined ";
//			error(message);
//			throw new Exception(message);
//		}

		info(" inited");
		isInited = true;
	}

	@Override
	public void performRow() throws Exception {
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

		Row row = readRow();
		Row newRow = RowFactory.createRow(this, row);
		
		int index = row.getMeta().getSize();
		
		String libelle = row.get(libelleIndex) != null ? row.get(libelleIndex).toString() : "";
		String postalCode = null;
		if (!onlyOneColumn) {
			postalCode = row.get(postalCodeIndex) != null ? row.get(postalCodeIndex).toString() : "";
			//We tried to get the cleanest postalCode as possible.
			try {
				postalCode = String.valueOf(new Double(Double.parseDouble(postalCode)).intValue());
			} catch(Exception e) { }
		}
		
		try {
			HashMap<String, Object> values = AdresseGeoLocHelper.getGeoloc(onlyOneColumn, libelle, postalCode, score);
			if (values != null && !values.isEmpty()) {
				Coordinate coordinate = (Coordinate) values.get(AdresseGeoLocHelper.GEOLOC.getId());
				if (onlyOneColumnOutput) {
//					newRow.set(newRow.getMeta().getSize() - 2, coordinate.getScore());
					String coor = coordinate.getLatitude() + "," + coordinate.getLongitude();
					newRow.set(index, coor);
				}
				else {
//					newRow.set(newRow.getMeta().getSize() - 3, coordinate.getScore());
					newRow.set(index, coordinate.getLatitude());
					index++;
					newRow.set(index, coordinate.getLongitude());
				}
				index++;
				
				if (fields != null) {
					for (String field : fields) {
						newRow.set(index, values.get(field));
						index++;
					}
				}
			}
		} catch (Exception e) {
			if (e.getMessage().equals(AdresseGeoLocHelper.SCORE_EXCEPTION)) {
				e.printStackTrace();
			}
			else {
				throw e;
			}
		}

		writeRow(newRow);
	}

	@Override
	public void releaseResources() {
		info(" resources released");

	}
}
