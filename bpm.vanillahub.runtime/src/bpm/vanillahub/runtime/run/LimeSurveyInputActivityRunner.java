package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.resources.LimeSurveyFile;
import bpm.vanilla.platform.core.beans.resources.LimeSurveyType;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.utils.LimeSurveyHelper;
import bpm.vanilla.platform.core.utils.UTF8ToAnsiUtils;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.LimeSurveyInputActivity;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.LimeSurveyServer;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.workflow.commons.beans.Result;

public class LimeSurveyInputActivityRunner extends ActivityRunner<LimeSurveyInputActivity> {

	private List<ApplicationServer> applicationServers;

	private List<LimeSurveyFile> items;
	private int fileIndex = 0;

	public LimeSurveyInputActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, LimeSurveyInputActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<ApplicationServer> applicationServers) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.applicationServers = applicationServers;
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(applicationServers);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(applicationServers);
	}

	@Override
	protected void clearResources() {

	}

	@Override
	protected void run(Locale locale) {
		try {
			String outputFileName = activity.getOutputName(parameters, variables);

			ByteArrayInputStream bis = null;
			// String outputFileName = "";
			if (activity.isLoop() && items != null && !items.isEmpty()) {
				LimeSurveyFile file = (LimeSurveyFile) items.get(fileIndex);
				fileIndex++;

				outputFileName = file.getName();

				byte[] fileContent = file.getContent();
				bis = new ByteArrayInputStream(fileContent);
			}
			else {
				LimeSurveyServer server = (LimeSurveyServer) activity.getResource(applicationServers);

				LimeSurveyType limeSurveyType = activity.getLimeSurveyType();
				String limeSurveyId = activity.getLimeSurveyId(parameters, variables);

				String limeSurveyUrl = server.getUrl(parameters, variables);
				String login = server.getLogin(parameters, variables);
				String password = server.getPassword(parameters, variables);

				LimeSurveyHelper helper = new LimeSurveyHelper(limeSurveyUrl, login, password);

				if (limeSurveyType == LimeSurveyType.LIMESURVEY) {
					// limeSurveyId can contains multiple IDs
					String[] limeSurveyIds = limeSurveyId.split(",");
					if (limeSurveyIds.length > 1) {
						List<byte[]> responses = new ArrayList<byte[]>();
						for (String id : limeSurveyIds) {
							String format = activity.getFormat().toString().toLowerCase();
							byte[] responseBytes = helper.getSurveyResponses(id, format, null);
							responses.add(responseBytes);
						}

						byte[] mergedResponses = mergeResponses(responses);
						bis = new ByteArrayInputStream(mergedResponses);
					}
					else {
						String format = activity.getFormat().toString().toLowerCase();
						byte[] responseBytes = helper.getSurveyResponses(limeSurveyId, format, null);
						bis = new ByteArrayInputStream(responseBytes);
					}
				}
				else if (limeSurveyType == LimeSurveyType.LIMESURVEY_SHAPES) {
					items = helper.getSurveyUploadFiles(limeSurveyId);

					if (items != null && !items.isEmpty()) {
						LimeSurveyFile file = (LimeSurveyFile) items.get(fileIndex);
						fileIndex++;

						outputFileName = file.getName();

						byte[] fileContent = file.getContent();
						bis = new ByteArrayInputStream(fileContent);
					}

					setNumberTotalOfFiles(items.size());
				}
				else if (limeSurveyType == LimeSurveyType.LIMESURVEY_VMAP) {
					String vmapIdentifiant = helper.getVMapData(limeSurveyId);
					if (vmapIdentifiant == null || vmapIdentifiant.isEmpty()) {
						addError(Labels.getLabel(locale, Labels.LimeSurveyNotMappedWithVMAP));

						result.setResult(Result.ERROR);
						return;
					}

					bis = getVmapDataFromDB(vmapIdentifiant);
				}
			}

			if (bis != null) {
				result.setFileName(outputFileName);
				IResultInformation resultInformation = new SimpleResultInformation("");
				result.putInfoComp(TypeResultInformation.SIMPLE, resultInformation);
				result.setInputStream(bis);

				if (activity.isLoop() && !isActionComplete(fileIndex)) {
					result.setResult(Result.RUNNING);
				}
				else {
					IResultInformation infos = result.getInfosComp().get(TypeResultInformation.META_LINKS);
					List<MetaLink> links = infos != null && infos instanceof MetaLinksResultInformation ? ((MetaLinksResultInformation) infos).getLinks() : new ArrayList<MetaLink>();

					Meta metaLS = new Meta();
					metaLS.setKey("limesurvey");
					MetaLink linkLS = new MetaLink(metaLS);
					linkLS.setValue(new MetaValue("limesurvey", "true"));

					Meta metaType = new Meta();
					metaType.setKey("data4citizen-type");
					MetaLink linkType = new MetaLink(metaType);
					linkType.setValue(new MetaValue("data4citizen-type", "limesurvey"));

					links.add(linkLS);
					links.add(linkType);
					result.putInfoComp(TypeResultInformation.META_LINKS, new MetaLinksResultInformation(links));

					clearResources();
					result.setResult(Result.SUCCESS);
				}
			}
			else if (result.getResult() == null || result.getResult() != Result.ERROR) {
				addError(Labels.getLabel(locale, Labels.LimeSurveyDidNotSendData));

				result.setResult(Result.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			addError(e.getMessage());
			result.setResult(Result.ERROR);
		}
	}
	
	private static final List<String> COLUMNS_NOT_WANTED = Arrays.asList(new String[]{"lastpage", "startlanguage"});
	private static final String QUESTION_KEY = "Q00";
	
	private byte[] mergeResponses(List<byte[]> responses) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		
		List<String> responseHeaderNames = new ArrayList<String>();
		HashMap<String, List<String>> responseRecords = new HashMap<String, List<String>>();
		
		int nbColumns = 0;
		for (int i = 0; i < responses.size(); i++) {
			int tempNbColumns = 0;
			
			byte[] response = responses.get(i);
			String stringResponse = new String(response, StandardCharsets.UTF_8);
			
			try (
				Reader reader = new StringReader(stringResponse); 
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
						.withDelimiter(";".charAt(0))
						.withFirstRecordAsHeader()
//						.withIgnoreHeaderCase()
						.withTrim());
			) {
				
				List<String> headerNames = csvParser.getHeaderNames();
				
				int indexKey = -1;
				List<Integer> columnsNotWanted = new ArrayList<Integer>();
				// Checking for columns we are not interested in
				for (int j = 0; j < headerNames.size(); j++) {
					String headerName = headerNames.get(j);
					if (QUESTION_KEY.equals(headerName)) {
						indexKey = j;
					}
					
					if (COLUMNS_NOT_WANTED.contains(headerName)) {
						columnsNotWanted.add(j);
					}
					else {
						//We remove the bom
						headerName = UTF8ToAnsiUtils.removeUTF8BOM(headerName);
						headerName = headerName.replace("\"", "");
						responseHeaderNames.add("q" + (i+1) + "_" + headerName);
						tempNbColumns++;
					}
				}
				
				if (indexKey == -1) {
					throw new Exception("The survey does not contains the pivot question QOO");
				}
				
				for (CSVRecord csvRecord : csvParser) {

					String key = csvRecord.get(indexKey);
					List<String> values = responseRecords.get(key) != null ? responseRecords.get(key) : new ArrayList<String>();
					
					if (values.size() < nbColumns) {
						for (int h = values.size(); h < nbColumns; h++) {
							values.add("");
						}
					}
					
					// Accessing values by Header names
					Iterator<String> items = csvRecord.iterator();
					int index = 0;
					while(items.hasNext()) {
						String value = items.next();
						if (!columnsNotWanted.contains(index)) {
							values.add(value);
						}
						index++;
					}
					
					responseRecords.put(key, values);
				}
			}
			
			// Set the numberOfColumns from the step
			nbColumns += tempNbColumns;
		}
		
		try (
			CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT
					.withHeader(responseHeaderNames.toArray(new String[responseHeaderNames.size()]))
					.withDelimiter(";".charAt(0)));
		) {
			for (List<String> values : responseRecords.values()) {
				//TODO: Manage when records does not match exactly
				csvPrinter.printRecord(values);
			}
			csvPrinter.flush();
		}
		
		return out.toByteArray();
	}

	private ByteArrayInputStream getVmapDataFromDB(String question) throws Exception {
		String geoShapeName = "generated_geo_shape";
		String geoPoint2dName = "generated_geo_point_2d";

		// Récupérer les informations de la table à intégrer où
		// business_object_id = 'epci' correspond au filtre sur l'objet métier
		// récupéré à la requête précédente.
		String query = "SELECT idfield, database, schema, pg_table, geomfield FROM s_vmap_2.layerpostgres WHERE layer_id = " + question + "";

		VanillaJdbcConnection con = null;
		VanillaPreparedStatement stmt = null;
		try {
			con = getVmapJdbcConnection();

			StringBuffer mainQuery = new StringBuffer();
			String geomColumn = null;

			stmt = con.prepareQuery(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				// String idField = rs.getString("id_field");
				// String database = rs.getString("database");
				String schema = rs.getString("schema");
				String table = rs.getString("pg_table");
				geomColumn = rs.getString("geomfield");

				mainQuery.append("SELECT *,\r\n");
				mainQuery.append(" ST_AsGeoJSON(ST_Transform(" + geomColumn + ", 4326)) as " + geoShapeName + ",\r\n");
				mainQuery.append(" CONCAT(split_part(replace(replace(split_part(json_extract_path_text(ST_AsGeoJSON(ST_Transform(" + geomColumn + ", 4326))::json, 'coordinates'), '],[', 1), '[', ''), ']', '')\r\n" + ", ',', 2), ',', split_part(replace(replace(split_part(json_extract_path_text(ST_AsGeoJSON(ST_Transform(" + geomColumn + ", 4326))::json, 'coordinates'), '],[', 1), '[', ''), ']', '')\r\n" + ", ',', 1)) as " + geoPoint2dName + "\r\n");
				mainQuery.append(" FROM " + schema + "." + table);

				// if($col == "geo_point_2d"){
				// if ($hasShapes) {
				// $str = json_encode($feat["geometry"]);
				// preg_match('/\[([-]?[\d|.]+),([-]?[\d|.]+)/i', $str, $match);
				// $coord = '' . $match[2] . "," . $match[1] . '';
				// $row[] = $coord;
				// }
				// else {
				// $str = json_encode($feat["geometry"]["coordinates"]);
				// preg_match('/\[([-]?[\d|.]+),([-]?[\d|.]+)/i', $str, $match);
				// $val = '' . $match[2] . "," . $match[1] . '';
				// $row[] = $val;
				// }
				// }

				break;
			}

			if (mainQuery.toString().isEmpty()) {
				throw new Exception("VMAP data not found");
			}

			rs.close();
			stmt.close();

			stmt = con.prepareQuery(mainQuery.toString());
			rs = stmt.executeQuery();
			ResultSetMetaData metadata = rs.getMetaData();

			// TODO: Change this to make a file or find a better memory
			// management
			StringBuffer buf = new StringBuffer();

			List<String> columns = new ArrayList<String>();

			boolean isFirst = true;
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				String columnName = metadata.getColumnName(i);
				// We skip the geom_column which is not transform to geojson
				if (columnName.equals(geomColumn)) {
					continue;
				}

				columns.add(columnName);

				if (!isFirst) {
					buf.append(",");
				}

				// We set well know name for geo column for D4C
				if (columnName.equals(geoShapeName)) {
					buf.append("geo_shape");
				}
				else if (columnName.equals(geoPoint2dName)) {
					buf.append("geo_point_2d");
				}
				else {
					buf.append(columnName);
				}

				isFirst = false;
			}
			buf.append("\r\n");

			while (rs.next()) {
				isFirst = true;
				for (String column : columns) {
					String value = rs.getString(column);

					if (!isFirst) {
						buf.append(",");
					}
					if (value != null) {
						buf.append("\"" + value.replace("\"", "\"\"") + "\"");
					}

					isFirst = false;
				}
				buf.append("\r\n");
			}

			return new ByteArrayInputStream(buf.toString().getBytes());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				ConnectionManager.getInstance().returnJdbcConnection(con);
			}
		}
	}

	private VanillaJdbcConnection getVmapJdbcConnection() throws Exception {
		Datasource datasource = getVmapJdbcDatasource();
		DatasourceJdbc jdbc = (DatasourceJdbc) datasource.getObject();
		return ConnectionManager.getInstance().getJdbcConnection(jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword(), jdbc.getDriver());
	}

	private Datasource getVmapJdbcDatasource() throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String databaseName = config.getProperty(VanillaConfiguration.P_VMAP_DATA_DB_SCHEMA);
		String jdbcUrl = config.getProperty(VanillaConfiguration.P_VMAP_DATA_DB_JDBCURL);
		String user = config.getProperty(VanillaConfiguration.P_VMAP_DATA_DB_USERNAME);
		String password = config.getProperty(VanillaConfiguration.P_VMAP_DATA_DB_PASSWORD);
		String driverClass = config.getProperty(VanillaConfiguration.P_VMAP_DATA_DB_DRIVERCLASSNAME);

		DatasourceJdbc jdbc = new DatasourceJdbc();
		jdbc.setDatabaseName(databaseName);
		jdbc.setDriver(driverClass);
		jdbc.setFullUrl(true);
		jdbc.setUrl(jdbcUrl);
		jdbc.setUser(user);
		jdbc.setPassword(password);

		Datasource datasource = new Datasource();
		datasource.setName(databaseName);
		datasource.setType(DatasourceType.JDBC);
		datasource.setObject(jdbc);

		return datasource;
	}

	private boolean isActionComplete(int fileIndex) {
		return items == null || items.size() <= fileIndex;
	}
}
