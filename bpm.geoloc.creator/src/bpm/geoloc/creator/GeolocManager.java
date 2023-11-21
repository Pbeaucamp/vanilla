package bpm.geoloc.creator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class GeolocManager {

	private static final String ARG_HELP = "h";
	private static final String ARG_BUILD_GEOLOC = "g";
	private static final String ARG_NODE_URL = "n";
	private static final String ARG_NODE_PATH = "np";
	private static final String ARG_D4C_URL = "d4c";
	private static final String ARG_CKAN_URL = "d";
	private static final String ARG_CKAN_API_KEY = "k";
	private static final String ARG_PACK_NAME = "pid";
	private static final String ARG_RESOURCE_ID = "rid";
	private static final String ARG_RESOURCE_SEPARATOR = "rs";
	private static final String ARG_RESOURCE_ENCODING = "re";
	private static final String ARG_ONLY_ONE_ADDRESS = "oa";
	private static final String ARG_COL_COORDINATE = "coor";
	private static final String ARG_COL_COORDINATE_SEPARATOR = "cs";
	private static final String ARG_NUMERO = "num";
	private static final String ARG_RUE = "rue";
	private static final String ARG_ADDRESS = "a";
	private static final String ARG_POSTALCODE = "p";
	private static final String ARG_VILLE = "v";
	private static final String ARG_LATITUDE = "lat";
	private static final String ARG_LONGITUDE = "lon";
	private static final String ARG_MINIMUMSCORE = "s";
	private static final String ARG_TEMP_FILEPATH = "f";
	private static final String ARG_UPLOAD_GEOJSON = "ug";

	private static final char DEFAULT_SEPARATOR = ',';
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final double DEFAULT_SCORE = 50;
	
	private static final String DEFAULT_COORDINATE_SEPARATOR = ";";

	public static void main(String[] args) throws Exception {
		// create Options object
		Options options = new Options();
		options.addOption(ARG_HELP, false, "Help");
		options.addOption(ARG_BUILD_GEOLOC, true, "0 if we don't need it, 1 if we need to get geolocalisation from the API BAN, 2 if we need to merge two coordinate column");
		options.addOption(ARG_NODE_URL, true, "Node URL");
		options.addOption(ARG_NODE_PATH, true, "Node path");
		options.addOption(ARG_D4C_URL, true, "URL to D4C");
		options.addOption(ARG_CKAN_URL, true, "URL to CKAN");
		options.addOption(ARG_CKAN_API_KEY, true, "D4C API KEY");
		options.addOption(ARG_PACK_NAME, true, "Package Name");
		options.addOption(ARG_RESOURCE_ID, true, "Resource ID");
		options.addOption(ARG_RESOURCE_SEPARATOR, true, "Resource separator (Default is '" + DEFAULT_SEPARATOR + "')");
		options.addOption(ARG_RESOURCE_ENCODING, true, "Resource encoding (Default is '" + DEFAULT_ENCODING + "')");
		options.addOption(ARG_ONLY_ONE_ADDRESS, true, "True if the address is only in one column");
		options.addOption(ARG_COL_COORDINATE, true, "Coordinate column name");
		options.addOption(ARG_COL_COORDINATE_SEPARATOR, true, "Coordinate column separator (Default is '" + DEFAULT_COORDINATE_SEPARATOR + "')");
		options.addOption(ARG_NUMERO, true, "Address number column name");
		options.addOption(ARG_RUE, true, "Address street column name");
		options.addOption(ARG_ADDRESS, true, "Address column name");
		options.addOption(ARG_POSTALCODE, true, "Postal code column name");
		options.addOption(ARG_VILLE, true, "City code column name");
		options.addOption(ARG_LATITUDE, true, "Latitude column name");
		options.addOption(ARG_LONGITUDE, true, "Longitude  cocolumn name");
		options.addOption(ARG_MINIMUMSCORE, true, "Minimum score to accept geolocalisation (Between 0 and 100) (Default is '" + DEFAULT_SCORE + "')");
		options.addOption(ARG_TEMP_FILEPATH, true, "Temp file path");
		options.addOption(ARG_UPLOAD_GEOJSON, true, "Upload Geojson on CKAN");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		boolean help = cmd.hasOption(ARG_HELP);
		if (help) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Geoloc Generator", options);
			return;
		}

		String buildGeolocValue = cmd.getOptionValue(ARG_BUILD_GEOLOC);
		if (buildGeolocValue == null) {
			throwException(ErrorCode.BUILD_GEOLOC_NOT_DEFINED);
		}
		int buildGeoloc = Integer.parseInt(buildGeolocValue);

		String nodeUrl = cmd.getOptionValue(ARG_NODE_URL);
		if (nodeUrl == null) {
			throwException(ErrorCode.NODE_URL_NOT_DEFINED);
		}

		String nodePath = cmd.getOptionValue(ARG_NODE_PATH);
		if (nodePath == null) {
			throwException(ErrorCode.NODE_PATH_NOT_DEFINED);
		}

		String d4cUrl = cmd.getOptionValue(ARG_D4C_URL);
		if (d4cUrl == null) {
			throwException(ErrorCode.D4C_URL_NOT_DEFINED);
		}

		String ckanUrl = cmd.getOptionValue(ARG_CKAN_URL);
		if (ckanUrl == null) {
			throwException(ErrorCode.CKAN_URL_NOT_DEFINED);
		}

		String apiKey = cmd.getOptionValue(ARG_CKAN_API_KEY);
		if (apiKey == null) {
			throwException(ErrorCode.D4C_API_KEY_NOT_DEFINED);
		}

		String packName = cmd.getOptionValue(ARG_PACK_NAME);
		if (packName == null) {
			throwException(ErrorCode.PACKAGE_NAME_NOT_DEFINED);
		}

		String resourceId = cmd.getOptionValue(ARG_RESOURCE_ID);
		if (resourceId == null) {
			throwException(ErrorCode.RESOURCE_ID_NOT_DEFINED);
		}

		String separatorValue = cmd.getOptionValue(ARG_RESOURCE_SEPARATOR);
		char separator = separatorValue != null ? separatorValue.charAt(0) : DEFAULT_SEPARATOR;

		String encoding = cmd.getOptionValue(ARG_RESOURCE_ENCODING);
		encoding = encoding != null ? encoding : DEFAULT_ENCODING;

		String tempFilePath = cmd.getOptionValue(ARG_TEMP_FILEPATH);
		if (tempFilePath == null) {
			throwException(ErrorCode.TEMP_FILE_PATH_NOT_DEFINED);
		}
		
		String uploadGeojsonValue = cmd.getOptionValue(ARG_UPLOAD_GEOJSON);
		boolean uploadGeojson = convertToBoolean(uploadGeojsonValue);

//		System.out.println(" Parameters = " + buildGeoloc + " + " + nodeUrl + " + " + nodePath + " + " + ckanUrl + " + " + apiKey + " + " + packName + " + " + resourceId + " + " + separator + " + " + encoding + " + " + onlyOneAddress + " + " + address + " + " + postalCode + " + " + score + " + " + tempFilePath);

		String colCoordinateSeparator = cmd.getOptionValue(ARG_COL_COORDINATE_SEPARATOR);
		if (colCoordinateSeparator == null) {
			colCoordinateSeparator = DEFAULT_COORDINATE_SEPARATOR;
		}
		
		String colCoordinate = null;
		if (buildGeoloc == 0) {
			colCoordinate = cmd.getOptionValue(ARG_COL_COORDINATE);

			// We build the CSV by moving the geoloc column
			try {
				CSVHelper.buildCSVByMovingGeolocAndUpload(d4cUrl, ckanUrl, apiKey, packName, resourceId, separator, encoding, colCoordinate, colCoordinateSeparator, tempFilePath);
				
				colCoordinate = CSVHelper.GEOLOCALISATION_COLUMN_NAME;
			} catch (Exception e) {
				e.printStackTrace();
				throwException(e);
			}
		}
		else if (buildGeoloc == 1) {
			String onlyOneAddressValue = cmd.getOptionValue(ARG_ONLY_ONE_ADDRESS);
			if (onlyOneAddressValue == null) {
				throwException(ErrorCode.ONLYONEADDRESS_NOT_DEFINED);
			}
			boolean onlyOneAddress = Boolean.parseBoolean(onlyOneAddressValue);

			String numero = cmd.getOptionValue(ARG_NUMERO);
			String rue = cmd.getOptionValue(ARG_RUE);

			String address = cmd.getOptionValue(ARG_ADDRESS);
			String ville = cmd.getOptionValue(ARG_VILLE);
			String postalCode = cmd.getOptionValue(ARG_POSTALCODE);
			if (address == null && ville == null && postalCode == null) {
				throwException(ErrorCode.ADDRESS_NOT_DEFINED);
			}
			if (!onlyOneAddress && ville == null && postalCode == null) {
				throwException(ErrorCode.POSTALCODE_NOT_DEFINED);
			}

			String scoreValue = cmd.getOptionValue(ARG_MINIMUMSCORE);
			double score = scoreValue != null ? Double.parseDouble(scoreValue) : DEFAULT_SCORE;
			
			// We build the CSV with Geoloc
			try {
				CSVHelper.buildCSVWithGeolocAndUpload(d4cUrl, ckanUrl, apiKey, packName, resourceId, separator, encoding, onlyOneAddress, numero, rue, address, postalCode, ville, score, tempFilePath);
				
				colCoordinate = CSVHelper.GEOLOCALISATION_COLUMN_NAME;
			} catch (Exception e) {
				e.printStackTrace();
				throwException(e);
			}
		}
		else if (buildGeoloc == 2) {
			String columnLatitude = cmd.getOptionValue(ARG_LATITUDE);
			if (columnLatitude == null) {
				throwException(ErrorCode.LATITUDE_NOT_DEFINED);
			}

			String columnLongitude = cmd.getOptionValue(ARG_LONGITUDE);
			if (columnLongitude == null) {
				throwException(ErrorCode.LONGITUDE_NOT_DEFINED);
			}
			
			// We build the CSV with two coordinate column
			try {
				CSVHelper.buildCSVWithOneCoordinateColumnAndUpload(d4cUrl, ckanUrl, apiKey, packName, resourceId, separator, encoding, columnLatitude, columnLongitude, tempFilePath);
				
				colCoordinate = CSVHelper.GEOLOCALISATION_COLUMN_NAME;
			} catch (Exception e) {
				e.printStackTrace();
				throwException(e);
			}
		}

		try {
			NodeHelper.callNodeAndUploadResource(nodeUrl, nodePath, ckanUrl, apiKey, packName, separator, encoding, colCoordinate, colCoordinateSeparator, uploadGeojson);
		} catch (Exception e) {
			e.printStackTrace();
			throwException(e);
		}
		
		System.out.println("GEOLOC END WITH SUCCESS");
	}
	
	private static boolean convertToBoolean(String value) {
	    if ("1".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || 
	        "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value))
	        return true;
	    return false;
	}

	private static void throwException(ErrorCode code) {
		System.out.println(code.getCode());
		System.exit(1);
	}

	private static void throwException(Exception e) {
		System.out.println(e.getMessage());
		System.exit(1);
	}
}
