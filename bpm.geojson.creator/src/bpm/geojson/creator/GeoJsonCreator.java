package bpm.geojson.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;

public class GeoJsonCreator {

	private static final String ARG_HELP = "h";
	private static final String ARG_INPUT_FILE = "i";
	private static final String ARG_OUTPUT_FILE = "o";
	private static final String ARG_COLUMN_ID = "id";
	private static final String ARG_RESOURCE_SEPARATOR = "s";
	private static final String ARG_RESOURCE_ENCODING = "e";
	private static final String ARG_COLUMN_COORDINATE = "coor";
	private static final String ARG_COLUMN_COORDINATE_SEPARATOR = "cs";
	private static final String ARG_LIMIT = "limit";
	private static final String ARG_RESOURCE_ID = "r";
	private static final String ARG_CKAN_URL = "u";

	private static final char DEFAULT_SEPARATOR = ',';
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_ID_COLUMN_NAME = "id";
	private static final String DEFAULT_COORDINATE_COLUMN_NAME = "geo_point_2d";
	private static final String DEFAULT_COORDINATE_SEPARATOR = ",";
	private static final int DEFAULT_LIMIT = -1;

	public static void main(String[] args) throws Exception {
		// create Options object
		Options options = new Options();
		options.addOption(ARG_HELP, false, "Help");
		options.addOption(ARG_INPUT_FILE, true, "Path to the intput file");
		options.addOption(ARG_OUTPUT_FILE, true, "Path to the output file");
		options.addOption(ARG_RESOURCE_SEPARATOR, true, "File separator (Default is '" + DEFAULT_SEPARATOR + "')");
		options.addOption(ARG_RESOURCE_ENCODING, true, "File encoding (Default is '" + DEFAULT_ENCODING + "')");
		options.addOption(ARG_COLUMN_ID, true, "Name of the column ID (Default is '" + DEFAULT_ID_COLUMN_NAME + "')");
		options.addOption(ARG_COLUMN_COORDINATE, true, "Name of the column Coordinate (Default is '" + DEFAULT_COORDINATE_COLUMN_NAME + "')");
		options.addOption(ARG_COLUMN_COORDINATE_SEPARATOR, true, "Separator of the column Coordinate (Default is '" + DEFAULT_COORDINATE_SEPARATOR + "')");
		options.addOption(ARG_RESOURCE_ID, true, "Id of the ckan csv resource");
		options.addOption(ARG_CKAN_URL, true, "Url Ckan");
		options.addOption(ARG_LIMIT, true, "Limit of line to treat (Default is 'no limit')");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		boolean help = cmd.hasOption(ARG_HELP);
		if (help) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("GeoJson Generator", options);
			return;
		}

		String ouputFile = cmd.getOptionValue(ARG_OUTPUT_FILE);
		if (ouputFile == null) {
			throw new Exception("Input file parameter is not defined.");
		}
		
		String separatorValue = cmd.getOptionValue(ARG_RESOURCE_SEPARATOR);
		char separator = separatorValue != null ? separatorValue.charAt(0) : DEFAULT_SEPARATOR;

		String encoding = cmd.getOptionValue(ARG_RESOURCE_ENCODING);
		encoding = encoding != null ? encoding : DEFAULT_ENCODING;

		String columnId = cmd.getOptionValue(ARG_COLUMN_ID);
		String columnCoordinate = cmd.getOptionValue(ARG_COLUMN_COORDINATE);
		String columnCoordinateSeparator = cmd.getOptionValue(ARG_COLUMN_COORDINATE_SEPARATOR);
		String limitValue = cmd.getOptionValue(ARG_LIMIT);

		String inputFile = cmd.getOptionValue(ARG_INPUT_FILE);
		String resourceCsv = cmd.getOptionValue(ARG_RESOURCE_ID);
		String ckanUrl = cmd.getOptionValue(ARG_CKAN_URL);
		if (inputFile == null) {
			if (resourceCsv != null) {
				GeoJsonCreator creator = new GeoJsonCreator();
				creator.createGeoJsonFromDatastore(resourceCsv, ckanUrl, ouputFile, columnId, columnCoordinate, columnCoordinateSeparator, limitValue);
			}
			throw new Exception("Input file parameter is not defined.");
		}

		try (InputStream inputStream = new FileInputStream(inputFile); FileOutputStream outputStream = new FileOutputStream(new File(ouputFile))) {
			createGeoJson(inputStream, outputStream, separator, encoding, columnId, columnCoordinate, columnCoordinateSeparator, limitValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("A problem happen during geojson generation : " + e.getMessage());
		}
	}

	private void createGeoJsonFromDatastore(String resourceCsv, String ckanUrl, String ouputFile, String columnId, String columnCoordinate, String columnCoordinateSeparator, String limitValue) throws Exception {
		throw new Exception("Not supported for now");
	}

	public static void createGeoJson(InputStream inputStream, OutputStream outputStream, String columnId, String columnCoordinate, String limitValue) throws Exception {
		createGeoJson(inputStream, outputStream, ",".charAt(0), "UTF-8", columnId, columnCoordinate, DEFAULT_COORDINATE_SEPARATOR, limitValue);
	}

	public static void createGeoJson(InputStream inputStream, OutputStream outputStream, char separator, String encoding, String columnId, String columnCoordinate, String limitValue) throws Exception {
		createGeoJson(inputStream, outputStream, separator, encoding, columnId, columnCoordinate, DEFAULT_COORDINATE_SEPARATOR, limitValue);
	}

	public static void createGeoJson(InputStream inputStream, OutputStream outputStream, char separator, String encoding, String columnId, String columnCoordinate, String columnCoordinateSeparator, String limitValue) throws Exception {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding))) {
			createGeoJson(reader, outputStream, separator, columnId, columnCoordinate, columnCoordinateSeparator, limitValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createGeoJson(BufferedReader reader, OutputStream outputStream, char separator, String columnId, String columnCoordinate, String columnCoordinateSeparator, String limitValue) throws Exception {
		if (columnId == null || columnId.isEmpty()) {
			columnId = DEFAULT_ID_COLUMN_NAME;
		}

		if (columnCoordinate == null || columnCoordinate.isEmpty()) {
			columnCoordinate = DEFAULT_COORDINATE_COLUMN_NAME;
		}

		if (columnCoordinateSeparator == null || columnCoordinateSeparator.isEmpty()) {
			columnCoordinateSeparator = DEFAULT_COORDINATE_SEPARATOR;
		}

		int limit = DEFAULT_LIMIT;
		if (limitValue != null && !limitValue.isEmpty()) {
			try {
				limit = Integer.parseInt(limitValue);
			} catch (Exception e) {
				e.printStackTrace();
				limit = DEFAULT_LIMIT;
			}
		}
		
		parseCSVAndCreateGeoJson(reader, outputStream, separator, columnId, columnCoordinate, columnCoordinateSeparator, limit);
	}
	
	private static void parseCSVAndCreateGeoJson(BufferedReader reader, OutputStream outputStream, char separator, String columnId, String columnCoordinate, String columnCoordinateSeparator, int limit) throws Exception {
		// We create a JSON generator to stream json to a file output
		JsonFactory jsonFactory = new JsonFactory();
		try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8)) {

			// We create the global object
			jsonGenerator.writeStartObject();
			jsonGenerator.writeStringField("type", "FeatureCollection");

			// We create the array 'features'
			jsonGenerator.writeArrayFieldStart("features");

			boolean skipFirst = true;
			int index = 0;

			List<String> columnNames = new ArrayList<>();
			int indexId = -1;
			int indexCoordinate = -1;

			CSVParser parser = CSVFormat.newFormat(separator).withQuote('"').parse(reader);
			Iterator<CSVRecord> csvIterator = parser.iterator();

			// String line = reader.readLine();
			int idValue = 0;
			while (csvIterator.hasNext()) {
				CSVRecord recordHeader = csvIterator.next();
				String[] values = new String[recordHeader.size()];
				int t = 0;
				for (String s : recordHeader) {
					values[t] = s;
					t++;
				}
				// System.out.println(values[0]);
				if (skipFirst) {
					columnNames = new ArrayList<String>(Arrays.asList(values));
					columnNames.add(0, "_id");

					for (int i = 0; i < columnNames.size(); i++) {
						String columnName = columnNames.get(i);
						if (columnName.equalsIgnoreCase(columnId)) {
							indexId = i;
						}
						else if (columnName.equalsIgnoreCase(columnCoordinate)) {
							indexCoordinate = i;
						}
					}

					if (indexId < 0) {
						throw new Exception("No column id found with the name '" + columnId + "'");
					}

					if (indexCoordinate < 0) {
						throw new Exception("No column coordinate found with the name '" + columnCoordinate + "'");
					}

					skipFirst = false;
				}
				else {
					List<String> vals = new ArrayList<String>(Arrays.asList(values));
					vals.add(0, idValue + "");
					values = vals.toArray(new String[vals.size()]);
					writeValue(jsonGenerator, columnNames, values, indexId, indexCoordinate, columnCoordinateSeparator);
				}
				index++;

				if (limit > 0 && index >= limit) {
					break;
				}
				idValue++;
			}

			// We end the array 'features'
			jsonGenerator.writeEndArray();
			// We end the global object
			jsonGenerator.writeEndObject();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeValue(JsonGenerator jsonGenerator, List<String> columnNames, String[] values, int indexId, int indexCoordinate, String columnCoordinateSeparator) throws IOException {
		String coordonneesValues = null;
		try {
			coordonneesValues = values[indexCoordinate];
		} catch (Exception e) {
			e.printStackTrace();
		}

		Double lat = null;
		Double lon = null;

		try {
			if (coordonneesValues != null && !coordonneesValues.isEmpty()) {
				String[] coordonnes = coordonneesValues.split(columnCoordinateSeparator);
				String latValue = coordonnes[0];
				String lonValue = coordonnes[1];
				if (latValue != null && !latValue.isEmpty()) {
					lat = Double.parseDouble(latValue);
				}
				if (lonValue != null && !lonValue.isEmpty()) {
					lon = Double.parseDouble(lonValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		jsonGenerator.writeStartObject();

		jsonGenerator.writeStringField("type", "Feature");

		jsonGenerator.writeFieldName("geometry");
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("type", "Point");
		jsonGenerator.writeArrayFieldStart("coordinates");
		if (lat != null && lon != null) {
			jsonGenerator.writeObject(lon);
			jsonGenerator.writeObject(lat);
		}
		else {
			jsonGenerator.writeNull();
		}
		jsonGenerator.writeEndArray();
		jsonGenerator.writeEndObject();

		jsonGenerator.writeFieldName("properties");
		jsonGenerator.writeStartObject();
		for (int i = 0; i < columnNames.size(); i++) {
			String columnName = columnNames.get(i);
			if (i == indexCoordinate) {
				jsonGenerator.writeArrayFieldStart(columnName);
				if (lat != null && lon != null) {
					jsonGenerator.writeObject(lat);
					jsonGenerator.writeObject(lon);
				}
				else {
					jsonGenerator.writeNull();
				}
				jsonGenerator.writeEndArray();
			}
			else if (i == indexId) {
				Integer id = Integer.parseInt(values[i]);
				jsonGenerator.writeNumberField(columnName, id);
			}
			else {
				jsonGenerator.writeStringField(columnName, values[i]);
			}
		}
		jsonGenerator.writeEndObject();

		jsonGenerator.writeEndObject();
	}
}
