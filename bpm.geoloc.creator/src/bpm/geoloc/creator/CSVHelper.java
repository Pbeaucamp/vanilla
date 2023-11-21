package bpm.geoloc.creator;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;

import bpm.geoloc.creator.model.CkanResource;
import bpm.geoloc.creator.model.Coordinate;

public class CSVHelper {
	
	private static final String GEOLOC_EXIST_EXCEPTION = "GeolocExistException";
	
	public static final String GEOLOCALISATION_COLUMN_NAME = "geo_point_2d";
	//We need to write the geolocalisation column in second position in order for D4C to choose this column to display coordinates
	private static final int DEFAULT_GEOLOCALISATION_INDEX = 1;

	public static void buildCSVWithGeolocAndUpload(String d4cUrl, String ckanUrl, String apiKey, String packId, String resourceId, char separator, String encoding, boolean onlyOneAddress, String columnNumero, String columnRue, String columnAddress, String columnPostalCode, String columnVille, Double minimumScore, String tempFilePath) throws Exception {
		CkanResource resource =  getResource(ckanUrl, packId, resourceId);
		String resourceUrl = resource.getUrl();
		String resourceName = resource.getName();

		String newFilePath = null;
		try (InputStream is = new URL(resourceUrl).openStream()) {
			newFilePath = buildCSVWithGeoloc(is, separator, encoding, onlyOneAddress, columnNumero, columnRue, columnAddress, columnPostalCode, columnVille, minimumScore, tempFilePath);
		
			System.out.println("Uploading file");
			uploadAndClean(d4cUrl, ckanUrl, apiKey, resourceName, newFilePath, packId, resourceId, GEOLOCALISATION_COLUMN_NAME, true);
			System.out.println("End upload");
		} catch (Exception e) {
			if (e.getMessage().equals(GEOLOC_EXIST_EXCEPTION)) {
				uploadAndClean(d4cUrl, ckanUrl, apiKey, resourceName, newFilePath, packId, resourceId, GEOLOCALISATION_COLUMN_NAME, false);
			}
			else {
				System.out.println("Erreur = " + e.getMessage());
				
				e.printStackTrace();
				throw e;
			}
		}
	}

	public static void buildCSVWithOneCoordinateColumnAndUpload(String d4cUrl, String ckanUrl, String apiKey, String packId, String resourceId, char separator, String encoding, String columnLatitude, String columnLongitude, String tempFilePath) throws Exception {
		CkanResource resource =  getResource(ckanUrl, packId, resourceId);
		String resourceUrl = resource.getUrl();
		String resourceName = resource.getName();

		String newFilePath = null;
		try (InputStream is =new URL(resourceUrl).openStream()) {
			newFilePath = buildCSVWithOneCoordinateColumn(is, separator, encoding, columnLatitude, columnLongitude, tempFilePath);
			
			System.out.println("Uploading file");
			uploadAndClean(d4cUrl, ckanUrl, apiKey, resourceName, newFilePath, packId, resourceId, GEOLOCALISATION_COLUMN_NAME, true);
			System.out.println("End upload");
		} catch (Exception e) {
			if (e.getMessage().equals(GEOLOC_EXIST_EXCEPTION)) {
				uploadAndClean(d4cUrl, ckanUrl, apiKey, resourceName, newFilePath, packId, resourceId, GEOLOCALISATION_COLUMN_NAME, false);
			}
			else {
				System.out.println("Erreur = " + e.getMessage());
				
				e.printStackTrace();
				throw e;
			}
		}
	}

	public static void buildCSVByMovingGeolocAndUpload(String d4cUrl, String ckanUrl, String apiKey, String packId, String resourceId, char separator, String encoding, String columnCoordinate, String coordinateSeparator, String tempFilePath) throws Exception {
		CkanResource resource =  getResource(ckanUrl, packId, resourceId);
		String resourceUrl = resource.getUrl();
		String resourceName = resource.getName();

		String newFilePath = null;
		try (InputStream is = new URL(resourceUrl).openStream()) {
			newFilePath = buildCSVByMovingGeoloc(is, separator, encoding, columnCoordinate, coordinateSeparator, tempFilePath);
			
			columnCoordinate = CSVHelper.GEOLOCALISATION_COLUMN_NAME;
		
			System.out.println("Uploading file");
			uploadAndClean(d4cUrl, ckanUrl, apiKey, resourceName, newFilePath, packId, resourceId, columnCoordinate, true);
			System.out.println("End upload");
		} catch (Exception e) {
			if (e.getMessage().equals(GEOLOC_EXIST_EXCEPTION)) {
				uploadAndClean(d4cUrl, ckanUrl, apiKey, resourceName, newFilePath, packId, resourceId, columnCoordinate, false);
			}
			else {
				System.out.println("Erreur = " + e.getMessage());
				
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	public static String buildCSVWithGeoloc(InputStream inputStream, char separator, String encoding, boolean onlyOneAddress, String columnNumero, String columnRue, String columnAddress, String columnPostalCode, String columnVille, Double minimumScore, String tempFilePath) throws Exception {
		System.out.println("Building geoloc");

		tempFilePath = tempFilePath.endsWith("/") ? tempFilePath : tempFilePath + "/";

		String newFilePath = tempFilePath + "CSV_" + new Object().hashCode() + ".csv";
		System.out.println("Writing new file = " + newFilePath);
		
		// We get the input stream for the resource
		try (PrintWriter writer = createFile(newFilePath, encoding)) {
			CSVFormat format = CSVFormat.newFormat(separator).withQuote('"');
			CSVParser parser = CSVParser.parse(inputStream, Charset.forName(encoding), format);

			Iterator<CSVRecord> csvIterator = parser.iterator();

			// We write the headers, add coordinate and we look for adress and
			// postal code
			Integer numeroIndex = null;
			Integer rueIndex = null;
			Integer addressIndex = null;
			Integer postalCodeIndex = null;
			Integer villeIndex = null;
			
			StringBuffer buf = new StringBuffer();

			CSVRecord record = csvIterator.next();
			Iterator<String> line = record.iterator();
			boolean first = true;
			int i = 0;
			while (line.hasNext()) {

				//We need to write the geolocalisation column in second position in order for D4C to choose this column to display coordinates
				if (i == DEFAULT_GEOLOCALISATION_INDEX) {
					buf.append(separator);
					buf.append("\"" + GEOLOCALISATION_COLUMN_NAME + "\"");
				}
				
				String header = line.next();

				if (first) {
					first = false;
				}
				else {
					buf.append(separator);
				}

				if (header != null) {
					//We check if the column already exist
					if (header.equals(GEOLOCALISATION_COLUMN_NAME)) {
						System.out.println("Column geolocalisation with name '" + GEOLOCALISATION_COLUMN_NAME + "' already exist. We don't create a new file.");
						throw new Exception(GEOLOC_EXIST_EXCEPTION);
					}
					
					
					if (columnNumero != null && header.equals(columnNumero)) {
						numeroIndex = i;
					}
					else if (columnRue != null && header.equals(columnRue)) {
						rueIndex = i;
					}
					else if (header.equals(columnAddress)) {
						addressIndex = i;
					}
					else if (columnPostalCode != null && header.equals(columnPostalCode)) {
						postalCodeIndex = i;
					}
					else if (columnVille != null && header.equals(columnVille)) {
						villeIndex = i;
					}

					buf.append("\"" + header + "\"");
				}

				i++;
			}

			buf.append("\r\n");
			writer.write(buf.toString());

			if (addressIndex == null && villeIndex == null && postalCodeIndex == null || (!onlyOneAddress && villeIndex == null && postalCodeIndex == null)) {
				throw new Exception("Address, City or Postal Code are not correctly defined.");
			}

			while (csvIterator.hasNext()) {
				record = csvIterator.next();

				line = record.iterator();
				buf = new StringBuffer();

				i = 0;

				String numeroValue = "";
				String rueValue = "";
				String addressValue = "";
				String postalCodeValue = "";
				String villeValue = "";
				
				//We first go through the line to get every informations to build the line with the geolocalisation after
				List<String> values = new ArrayList<String>();
				while (line.hasNext()) {
					String value = line.next();

					values.add(cleanValue(value));

					if (numeroIndex != null && i == numeroIndex) {
						numeroValue = value;
					}
					else if (rueIndex != null && i == rueIndex) {
						rueValue = value;
					}
					else if (addressIndex != null && i == addressIndex) {
						addressValue = value;
					}
					else if (!onlyOneAddress && postalCodeIndex != null && i == postalCodeIndex) {
						postalCodeValue = value;
					}
					else if (villeIndex != null && i == villeIndex) {
						villeValue = value;
					}

					i++;
				}
				

				first = true;
				i = 0;
				for (String value : values) {
					
					//We need to write the geolocalisation column in second position in order for D4C to choose this column to display coordinates
					if (i == DEFAULT_GEOLOCALISATION_INDEX) {

						buf.append(separator);

						if (addressValue != null && (onlyOneAddress || villeValue != null || postalCodeValue != null)) {
							try {
								Coordinate coordinate = AdresseGeoLocHelper.getGeoloc(onlyOneAddress, numeroValue, rueValue, addressValue, postalCodeValue, villeValue, minimumScore);
								buf.append(coordinate != null ? "\"" + String.valueOf(coordinate.getLatitude()) + "," + String.valueOf(coordinate.getLongitude()) + "\"" : "");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					if (first) {
						first = false;
					}
					else {
						buf.append(separator);
					}
					
					if (value != null) {
						buf.append("\"" + value + "\"");
					}
					
					i++;
				}

				buf.append("\r\n");
				writer.write(buf.toString());
			}
		} catch (Exception e) {
			System.out.println("Erreur = " + e.getMessage());
			
			e.printStackTrace();
			throw e;
		}
		System.out.println("Geoloc built");
		
		return newFilePath;
	}
	
	public static String buildCSVWithOneCoordinateColumn(InputStream inputStream, char separator, String encoding, String columnLatitude, String columnLongitude, String tempFilePath) throws Exception {
		System.out.println("Building one column coordinate");

		tempFilePath = tempFilePath.endsWith("/") ? tempFilePath : tempFilePath + "/";

		String newFilePath = tempFilePath + "CSV_" + new Object().hashCode() + ".csv";
		System.out.println("Writing new file = " + newFilePath);
		// We get the input stream for the resource
		try (PrintWriter writer = createFile(newFilePath, encoding)) {
			CSVFormat format = CSVFormat.newFormat(separator).withQuote('"');
			CSVParser parser = CSVParser.parse(inputStream, Charset.forName(encoding), format);

			Iterator<CSVRecord> csvIterator = parser.iterator();

			// We write the headers, add coordinate and we look for latitude and longitude
			Integer latitudeIndex = null;
			Integer longitudeIndex = null;

			StringBuffer buf = new StringBuffer();

			CSVRecord record = csvIterator.next();
			Iterator<String> line = record.iterator();
			boolean first = true;
			int i = 0;
			while (line.hasNext()) {
				
				//We need to write the geolocalisation column in second position in order for D4C to choose this column to display coordinates
				if (i == DEFAULT_GEOLOCALISATION_INDEX) {
					buf.append(separator);
					buf.append("\"" + GEOLOCALISATION_COLUMN_NAME + "\"");
				}
				
				String header = line.next();

				if (first) {
					first = false;
				}
				else {
					buf.append(separator);
				}

				if (header != null) {
					
					//We check if the column already exist
					if (header.equals(GEOLOCALISATION_COLUMN_NAME)) {
						System.out.println("Column geolocalisation with name '" + GEOLOCALISATION_COLUMN_NAME + "' already exist. We don't create a new file.");
						throw new Exception(GEOLOC_EXIST_EXCEPTION);
					}
					
					if (header.equals(columnLatitude)) {
						latitudeIndex = i;
					}
					if (header.equals(columnLongitude)) {
						longitudeIndex = i;
					}

					buf.append("\"" + header + "\"");
				}

				i++;
			}

			buf.append("\r\n");
			writer.write(buf.toString());

			if (latitudeIndex == null || longitudeIndex == null) {
				throw new Exception("Latitude or longitude are not correctly defined.");
			}

			while (csvIterator.hasNext()) {
				record = csvIterator.next();

				line = record.iterator();
				buf = new StringBuffer();

				first = true;
				i = 0;

				String latitudeValue = "";
				String longitudeValue = "";

				List<String> values = new ArrayList<String>();
				while (line.hasNext()) {
					String value = line.next();

					values.add(cleanValue(value));

					if (i == latitudeIndex) {
						latitudeValue = value;
					}
					else if (i == longitudeIndex) {
						longitudeValue = value;
					}

					i++;
				}

				first = true;
				i = 0;
				for (String value : values) {
					
					//We need to write the geolocalisation column in second position in order for D4C to choose this column to display coordinates
					if (i == DEFAULT_GEOLOCALISATION_INDEX) {

						buf.append(separator);

						if (latitudeValue != null && longitudeValue != null) {
							try {
								latitudeValue = cleanCoordinateValue(latitudeValue);
								longitudeValue = cleanCoordinateValue(longitudeValue);
								buf.append("\"" + String.valueOf(latitudeValue) + "," + String.valueOf(longitudeValue) + "\"");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					if (first) {
						first = false;
					}
					else {
						buf.append(separator);
					}
					
					if (value != null) {
						buf.append("\"" + value + "\"");
					}
					
					i++;
				}

				buf.append("\r\n");
				writer.write(buf.toString());
			}
		} catch (Exception e) {
			System.out.println("Erreur = " + e.getMessage());
			
			e.printStackTrace();
			throw e;
		}

		System.out.println("Geoloc built");
		return newFilePath;
	}
	
	/**
	 * This method allows to move the column coordinate in order for D4C to choose this column to display coordinates
	 * 
	 * @param inputStream
	 * @param separator
	 * @param encoding
	 * @param columnLatitude
	 * @param columnLongitude
	 * @param tempFilePath
	 * @return
	 * @throws Exception
	 */
	public static String buildCSVByMovingGeoloc(InputStream inputStream, char separator, String encoding, String columnCoordinate, String coordinateSeparator, String tempFilePath) throws Exception {
		System.out.println("Building CSV by moving coordinate");
		
		tempFilePath = tempFilePath.endsWith("/") ? tempFilePath : tempFilePath + "/";

		String newFilePath = tempFilePath + "CSV_" + new Object().hashCode() + ".csv";
		System.out.println("Writing new file = " + newFilePath);
		// We get the input stream for the resource
		try (PrintWriter writer = createFile(newFilePath, encoding)) {
			CSVFormat format = CSVFormat.newFormat(separator).withQuote('"');
			CSVParser parser = CSVParser.parse(inputStream, Charset.forName(encoding), format);

			Iterator<CSVRecord> csvIterator = parser.iterator();

			StringBuffer buf = new StringBuffer();

			CSVRecord record = csvIterator.next();
			Iterator<String> line = record.iterator();
			
			// We write the headers, add coordinate and we look for latitude and longitude
			Integer coordinateIndex = null;
			
			List<String> values = new ArrayList<String>();
			int i = 0;
			while (line.hasNext()) {
				String header = line.next();
				if (header != null) {
					
					if (header.equals(columnCoordinate)) {
						//We check if the column is already at the good index
						if (columnCoordinate.equals(GEOLOCALISATION_COLUMN_NAME) && i == DEFAULT_GEOLOCALISATION_INDEX) {
							System.out.println("Column geolocalisation with name '" + columnCoordinate + "' already exist at the good place. We don't create a new file.");
							throw new Exception(GEOLOC_EXIST_EXCEPTION);
						}
						
						values.add(DEFAULT_GEOLOCALISATION_INDEX, GEOLOCALISATION_COLUMN_NAME);
						coordinateIndex = i;
					}
					else {
						values.add(header);
					}
				}
				
				i++;
			}

			boolean first = true;
			for (String value : values) {
				if (first) {
					first = false;
				}
				else {
					buf.append(separator);
				}
				
				if (value != null) {
					buf.append("\"" + value + "\"");
				}
				
				i++;
			}

			buf.append("\r\n");
			writer.write(buf.toString());

			if (coordinateIndex == null) {
				throw new Exception("Column with coordinate '" + columnCoordinate + "' not found.");
			}
			
			boolean needChangeCoordinateSeparator = !coordinateSeparator.equals(",");
			if (needChangeCoordinateSeparator) {
				System.out.println("We need to rewrite the column geoloc with the separator ',' instead of '" + coordinateSeparator + "'");
			}

			while (csvIterator.hasNext()) {

				record = csvIterator.next();

				line = record.iterator();
				buf = new StringBuffer();

				i = 0;
				values = new ArrayList<String>();
				while (line.hasNext()) {
					String value = line.next();

					if (i == coordinateIndex) {
						if (needChangeCoordinateSeparator) {
							values.add(DEFAULT_GEOLOCALISATION_INDEX, cleanGeolocValue(coordinateSeparator, value));
						}
						else {
							values.add(DEFAULT_GEOLOCALISATION_INDEX, value);
						}
					}
					else {
						values.add(cleanValue(value));
					}

					i++;
				}

				first = true;
				for (String value : values) {
					if (first) {
						first = false;
					}
					else {
						buf.append(separator);
					}
					
					if (value != null) {
						buf.append("\"" + value + "\"");
					}
				}

				buf.append("\r\n");
				writer.write(buf.toString());
			}
		} catch (Exception e) {
			System.out.println("Erreur = " + e.getMessage());
			
			e.printStackTrace();
			throw e;
		}

		System.out.println("Geoloc built");
		return newFilePath;
	}

	/**
	 * We need to clean the value if it contains the char " (double quotes)
	 * 
	 * @param value
	 * @return
	 */
	private static String cleanCoordinateValue(String value) {
		return value.replace(",", ".");
	}
	
	private static String cleanValue(String value) {
		if (value.contains("\"") && !value.contains("\"\"")) {
			return value != null ? value.replace("\"", "\"\"") : value;
		}
		return value;
	}
	
	/**
	 * We need to clean the value if it contains another separator than comma
	 * 
	 * @param value
	 * @return
	 */
	private static String cleanGeolocValue(String coordinateSeparator, String value) {
		if (value != null && !value.isEmpty() && value.contains(coordinateSeparator)) {
			String[] values = value.split(coordinateSeparator);
			return cleanCoordinateValue(values[0]) + "," + cleanCoordinateValue(values[1]);
		}
		else {
			return value;
		}
	}

	private static void uploadAndClean(String d4cUrl, String ckanUrl, String apiKey, String resourceName, String newFilePath, String packId, String resourceId, String columnCoordinate, boolean uploadFile) {
		// We reupload the resource on ckan
		if (uploadFile) {
			try {
				CkanHelper.uploadCkanFile(ckanUrl, apiKey, resourceName, "CSV", newFilePath, packId, resourceId);
				
				//We test if the field is created or we wait until it is (we set a limit of 10 min by default)
				boolean timeoutReached = false;
				
				int timeoutMinutes = WaitHelper.TIMEOUT_DEFAULT;
				
				Date timeout = WaitHelper.getTimoutDate(timeoutMinutes);
				while(!timeoutReached) {

					TimeUnit.SECONDS.sleep(10);
					
					boolean fieldFound = false;
					JSONArray fields = CkanHelper.getResourceFields(ckanUrl, apiKey, resourceId);
					if (fields != null) {
						for (int i=0; i<fields.length(); i++) {
							JSONObject field = !fields.isNull(i) ? fields.getJSONObject(i) : null;
							//We check if the field coordinate exist and if the index is correct (DEFAULT_GEOLOCALISATION_INDEX + 1 because there is a new column _id)
							if (!field.isNull("id") && field.getString("id").equals(columnCoordinate) && i == DEFAULT_GEOLOCALISATION_INDEX + 1) {
								fieldFound = true;
								break;
							}
						}
					}
					
					if (fieldFound) {
						System.out.println("Field '" + columnCoordinate + "' was created. We move on.");
						break;
					}
					
					Date currentDate = new Date();
					if (currentDate.after(timeout)) {
						timeoutReached = true;
						System.out.println("Field '" + columnCoordinate + "' could not be created in " + timeoutMinutes + ". We were not able to upload it.");
						break;
					}
					
					CkanHelper.pushInDatastore(d4cUrl, apiKey, resourceId);
				}
			} catch (Exception e) {
				System.out.println("Erreur = " + e.getMessage());
				
				e.printStackTrace();
			}
		}

		deleteFile(newFilePath);
	}

	private static CkanResource getResource(String ckanUrl, String packId, String resourceId) throws Exception {
		CkanResource resource = CkanHelper.getCkanResourceURL(ckanUrl, packId, resourceId);
		if (resource == null) {
			throw new Exception("Unable to find resource with ID " + resourceId);
		}

		System.out.println("Found resource with URL = " + resource.getUrl() + " and name = " + resource.getName());
		return resource;
	}

	private static void deleteFile(String filePath) {
		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	private static PrintWriter createFile(String newFilePath, String encoding) throws Exception {
		File file = new File(newFilePath);
		if (file.exists()) {
			file.delete();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				throw e;
			}
		}

		return new PrintWriter(file, encoding);
	}
}
