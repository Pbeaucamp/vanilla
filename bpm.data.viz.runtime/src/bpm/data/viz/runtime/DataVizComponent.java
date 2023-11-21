package bpm.data.viz.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.data.viz.core.IDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.data.viz.core.preparation.LinkItem;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.runtime.dao.DataVizDao;
import bpm.data.viz.runtime.geoloc.AdresseGeoLocHelper;
import bpm.data.viz.runtime.geoloc.Coordinate;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;
import bpm.vanilla.platform.core.beans.data.DataType;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.JDBCType;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.AbstractVanillaComponent;
import bpm.vanilla.platform.core.components.impl.VanillaHttpContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.listeners.IVanillaEvent;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanilla.platform.logging.IVanillaLoggerService;

import com.thoughtworks.xstream.XStream;

public class DataVizComponent extends AbstractVanillaComponent implements IDataVizComponent {

	private Status status = Status.UNDEFINED;
	private ComponentContext context = null;

	private IVanillaLoggerService loggerService;
	private HttpService httpService;
	private DataVizDao dao;
	private IVanillaAPI api;

	private XStream xstream = new XStream();

	public void activate(ComponentContext ctx) throws Throwable {
		
		System.out.println("Dataviz starting");

		try {
			context = ctx;
			status = Status.STARTING;
			registerServlets();
			getLogger().info("FreeMetrics servlet registered");
			try {
				ServiceReference ref = ctx.getBundleContext().getServiceReference(HttpService.class.getName());
				String port = (String) ref.getProperty("http.port");

				registerInVanilla(VanillaComponentType.COMPONENT_DATA_VIZ, "dataVizComponent", port);
				status = Status.STARTED;
			} catch (Throwable e) {
				e.printStackTrace();
				throw new RuntimeException("Exception while regitering DataViz Service in vanilla", e);
			}

		} catch (Throwable e) {
			getLogger().error("DataViz component unable to start : ", e);
			status = Status.ERROR;
			throw e;
		}

		try {

			ApplicationContext factory = new ClassPathXmlApplicationContext("/bpm/data/viz/runtime/dao/dataviz_context.xml") {

				protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
					super.initBeanDefinitionReader(reader);
					reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_NONE);
					// This the important line and available since Equinox 3.7
					ClassLoader loader = DataVizComponent.this.getClass().getClassLoader();
					reader.setBeanClassLoader(loader);
				}

			};

			dao = (DataVizDao) factory.getBean("dataVizDao");

		} catch (Exception e) {
			throw new RuntimeException("Excetion while init dao", e);
		}
	}

	public IVanillaLogger getLogger() {
		return loggerService.getLogger(getClass().getName());
	}

	public DataVizDao getDao() {
		return dao;
	}

	private void registerServlets() {
		try {
			VanillaHttpContext httpCtx = new VanillaHttpContext(getVanillaApi().getVanillaSecurityManager(), getVanillaApi().getVanillaSystemManager());
			httpService.registerServlet(IDataVizComponent.DATA_VIZ_SERVLET, new DataVizServlet(this, getLogger()), null, httpCtx);
			System.out.println("DataViz Servlet registered");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IVanillaAPI getVanillaApi() {
		if (api == null) {
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

			api = new RemoteVanillaPlatform(config.getVanillaServerUrl(), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return api;
	}

	private IRepositoryApi getRepositoryApi(int repositoryId, int groupId) {
		IVanillaAPI api = getVanillaApi();
		
		Group gr = new Group();
		gr.setId(groupId);
		
		Repository rep = new Repository();
		rep.setId(repositoryId);
		
		return new RemoteRepositoryApi(new BaseRepositoryContext(api.getVanillaContext(), gr, rep));
	}

	public void bind(IVanillaLoggerService service) {
		loggerService = service;
	}

	public void unbind(IVanillaLoggerService service) {
		loggerService = null;
	}

	public void bind(HttpService service) {
		httpService = service;
	}

	public void unbind(HttpService service) {
		httpService = null;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	protected void doStop() throws Exception {
		context.disableComponent(DataVizComponent.class.toString());
	}

	@Override
	protected void doStart() throws Exception {
		context.enableComponent(DataVizComponent.class.toString());
	}

	@Override
	public void notify(IVanillaEvent event) {
	}

	@Override
	public DataPreparation saveDataPreparation(DataPreparation dataprep) throws Exception {
		dataprep.setRuleModel(xstream.toXML(dataprep.getRules()));
		dataprep.setMapModel(xstream.toXML(dataprep.getMapInformation()));
		dataprep.setLinkedItemsModel(xstream.toXML(dataprep.getLinkedItems()));
		dataprep = (DataPreparation) dao.saveOrUpdate(dataprep);
		for(DataPreparation dp : getDataPreparations()) {
			if(dp.getId() == dataprep.getId()) {
				return dp;
			}
		}
		return dataprep;
	}

	@Override
	public void deleteDataPreparation(DataPreparation dataprep) throws Exception {
		dao.delete(dataprep);
	}

	@Override
	public List<DataPreparation> getDataPreparations() throws Exception {
		List<DataPreparation> result = dao.find("From DataPreparation");

		for (DataPreparation dp : result) {
			try {

				User u = getVanillaApi().getVanillaSecurityManager().getUserById(dp.getUserId());
				dp.setUser(u);
				if(dp.getDatasetId() > 0) {
					Dataset ds = getVanillaApi().getVanillaPreferencesManager().getDatasetById(dp.getDatasetId());
					dp.setDataset(ds);
				}
				try {
					if(dp.getMapId() != null) {
					
						RemoteMapDefinitionService remoteMap = new RemoteMapDefinitionService();
						remoteMap.configure(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl());
						MapVanilla map = remoteMap.getMapVanillaById(dp.getMapId()).get(0);
						dp.setMap(map);
						
						for(MapDataSet mapDs : map.getDataSetList()) {
							if(mapDs.getId() == dp.getMapDatasetId()) {
								dp.setMapDataset(mapDs);
								break;
							}
						}
					}
				} catch(Exception e1) {
				}
				try {
					dp.setRules((List<PreparationRule>) xstream.fromXML(dp.getRuleModel()));
				} catch (Exception e) {
				}
				try {
					dp.setMapInformation((MapInformation) xstream.fromXML(dp.getMapModel()));
				} catch (Exception e) {
				}
				try {
					dp.setLinkedItems((List<LinkItem>) xstream.fromXML(dp.getLinkedItemsModel()));
					if (dp.getLinkedItems() != null) {
						for (LinkItem item : dp.getLinkedItems()) {
							IRepositoryApi repositoryApi = getRepositoryApi(item.getRepositoryId(), item.getGroupId());
							RepositoryItem repItem = repositoryApi.getRepositoryService().getDirectoryItem(item.getItemId());
							item.setItem(repItem);
						}
					}
					
				} catch (Exception e) {
				}
			} catch(Exception e) {
			}
		}

		return result;
	}

	@Override
	public DataPreparationResult executeDataPreparation(DataPreparation dataPrep) throws Exception {
		DataPreparationResult result = new DataPreparationResult();
		result.setPreparation(dataPrep);

//		DatasetResultQuery resQuery = getVanillaApi().getVanillaPreferencesManager().getResultQuery(dataPrep.getDataset());
		DatasetResultQuery resQuery = getVanillaApi().getVanillaPreferencesManager().getResultQuery(dataPrep.getDataset(), 20000);
		for (PreparationRule rule : dataPrep.getRules()) {
			RuleHelper.applyRule(resQuery, rule, dataPrep);
		}

		result.setValues(RuleHelper.convertResult(dataPrep, resQuery.getResult()));

		return result;
	}
	
	@Override
	public Integer countDataPreparation(DataPreparation dataPrep) throws Exception {
		return getVanillaApi().getVanillaPreferencesManager().getCountQuery(dataPrep.getDataset());
	}

	private VanillaJdbcConnection getGeoConnection() throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String driver = config.getProperty(VanillaConfiguration.P_AGILA_DB_DRIVERCLASSNAME);
		String dbUrl = config.getProperty(VanillaConfiguration.P_AGILA_DB_JDBCURL);
		String login = config.getProperty(VanillaConfiguration.P_AGILA_DB_USERNAME);
		String password = config.getProperty(VanillaConfiguration.P_AGILA_DB_PASSWORD);
	
		return ConnectionManager.getInstance().getJdbcConnection(dbUrl, login, password, driver);
	}
	
	@Override
	public InputStream exportDataPreparation(ExportPreparationInfo info) throws Exception {
		DataPreparationResult result = executeDataPreparation(info.getDataPreparation());
		ByteArrayOutputStream is = new ByteArrayOutputStream();
		switch (info.getExportType()) {
		case "csv":
			OutputStreamWriter writer = new OutputStreamWriter(is, "UTF-8");
			//case "CKAN":
			VanillaJdbcConnection conn = getGeoConnection();
			CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(info.getSeparator().charAt(0));// CSVFormat.newFormat(info.getSeparator().charAt(0)).withQuote('"');
			CSVPrinter p = new CSVPrinter(writer, csvFileFormat);

			List<List<Serializable>> values = new ArrayList<>();
			List<Serializable> head = new ArrayList<>();
			DataColumn colCp = null;
			for (DataColumn col : result.getValues().get(0).keySet()) {
				String colName = col.getColumnLabel().toLowerCase().replace(" ", "_");
				colName = Normalizer.normalize(colName, Normalizer.Form.NFD);
				colName = colName.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
				head.add(colName);
				if(col.getFt() == FunctionalType.PAYS || col.getFt() == FunctionalType.COMMUNE || col.getFt() == FunctionalType.ADRESSE) {
					head.add(col.getFt() == FunctionalType.PAYS ? "geom" : "geoloc");
				}
				if(col.getFt() == FunctionalType.CODE_POSTAL) {
					colCp = col;
				}
			}
			values.add(head);

			for (Map<DataColumn, Serializable> val : result.getValues()) {
				List<Serializable> line = new ArrayList<>();
				for (DataColumn c : val.keySet()) {
					line.add(val.get(c));
					if(c.getFt() == FunctionalType.PAYS || c.getFt() == FunctionalType.COMMUNE) {
						String valGeo = getGeocolumnValue(conn, c.getFt(), val.get(c));
						line.add(valGeo);
					}
					if(c.getFt() == FunctionalType.ADRESSE) {
						Serializable cp = null;
						if(colCp != null) {
							cp = val.get(colCp);
						}
						String valGeo = getGeocolumnValueApiAddress(val.get(c), cp);
						line.add(valGeo);
					}
				}
				values.add(line);
			}

			p.printRecords(values);
			p.close();
			ConnectionManager.getInstance().returnJdbcConnection(conn);
			ByteArrayInputStream bys = new ByteArrayInputStream(is.toByteArray());
//			String test = IOUtils.toString(bys);
//			bys = new ByteArrayInputStream(IOUtils.toInputStream(test).toByteArray());
			return bys;

		case "xlsx":
			writer = new OutputStreamWriter(is);
			//case "ARCHITECT":
			try {
				String sheetName = "Sheet1";
				XSSFWorkbook wb = new XSSFWorkbook();
				XSSFSheet sheet = wb.createSheet(sheetName);
				XSSFRow firstRow = sheet.createRow(0);
				int index = 0;
				for (DataColumn col : result.getValues().get(0).keySet()) {

					String columnName = col.getColumnName();
					XSSFCell cell = firstRow.createCell(index);
					cell.setCellValue(columnName);

					index++;
				}

				int rowNb = 0;
				// Parcours des lignes
				for (Map<DataColumn, Serializable> val : result.getValues()) {
					XSSFRow row = sheet.createRow(rowNb + 1);
					int ind = 0;

					for (DataColumn col : val.keySet()) {
						String type = col.getColumnTypeName();
						Object value = val.get(col);
						XSSFCell cell = row.createCell(ind);
						if (value != null) {
							cell.setCellValue(value.toString());
						}
						else {
							cell.setCellValue(" ");
						}
						ind++;

					}
					rowNb++;
				}

				try (ByteArrayOutputStream xlsOs = new ByteArrayOutputStream()) {
					// write this workbook to an Outputstream.
					wb.write(xlsOs);
					ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(xlsOs.toByteArray());
					return byteArrayIs;

				} catch (IOException e) {
					e.printStackTrace();
				}

				wb.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	


	private String getGeocolumnValueApiAddress(Serializable adresse, Serializable codePostal) throws Exception {
		StringBuilder buf = new StringBuilder();
		if(codePostal != null) {
			Coordinate coordinate = AdresseGeoLocHelper.getGeoloc(false, adresse.toString(), codePostal.toString(), 0.5);
			buf.append(coordinate != null ? String.valueOf(coordinate.getLatitude()) + "," + String.valueOf(coordinate.getLongitude()) : "");
		}
		else {
			Coordinate coordinate = AdresseGeoLocHelper.getGeoloc(true, adresse.toString(), null, 0.5);
			buf.append(coordinate != null ? String.valueOf(coordinate.getLatitude()) + "," + String.valueOf(coordinate.getLongitude()) : "");
		}
		
		return buf.toString();
	}

	private String getGeocolumnValue(VanillaJdbcConnection conn, FunctionalType ft, Serializable serializable) throws Exception {
		if(ft == FunctionalType.PAYS) {
			String countryName = serializable.toString().replaceAll("'", "''");
			ResultSet set = conn.prepareQuery("select * from country where name_country = '" + countryName + "'").executeQuery();
			StringBuilder buf = new StringBuilder();
			buf.append("{\"type\": \"Polygon\", \"coordinates\": [[");
			boolean first = true;
			while(set.next()) {
				if(first) {
					first = false;
				}
				else {
					buf.append(", ");
				}
				buf.append("[");
				buf.append(set.getString("longitude"));
				buf.append(", ");
				buf.append(set.getString("latitude"));
				buf.append("]");
				
			}
			buf.append("]]}");
			
			return buf.toString();
		}
		
		else if(ft == FunctionalType.COMMUNE) {
			ResultSet rs = conn.prepareQuery("SELECT ville_nom_reel, ville_code_commune, ville_longitude_deg, ville_latitude_deg FROM villes_france_free WHERE ville_code_commune = '" + serializable.toString() + "'").executeQuery();
			while(rs.next()) {
				return rs.getDouble("ville_latitude_deg") + ", " + rs.getDouble("ville_longitude_deg");
			}
		}
		
		return null;
	}

	public String createQuery(Dataset ds, String tableName) {

		StringBuilder buf = new StringBuilder();
		buf.append("CREATE TABLE " + tableName);
		buf.append(" (");
		boolean first = true;
		for(DataColumn dc: ds.getMetacolumns()) {
			String s= "";
			String type = JDBCType.valueOf(dc.getColumnType()).getName();
			if(dc.getCustomDataType() != null) {
				type = getCustomType(dc.getCustomDataType());
			}

			if(type.endsWith("CHAR") || type.endsWith("TEXT") || type.endsWith("BLOB")) {
				s = dc.getColumnName() + " TEXT(1000)";
			}
			else {
				s = dc.getColumnName() + " " + type;
			}

			if(first) {
				first = false;
			}
			else {
				buf.append(",");
			}
			buf.append(s);
		}

		buf.append(")");
		return buf.toString();
	}

	private String getCustomType(DataType customDataType) {
		switch(customDataType) {
			case DATE:
				return JDBCType.DATE.getName();
			case DECIMAL:
				return JDBCType.DOUBLE.getName();
			case INT:
				return JDBCType.INTEGER.getName();
			case STRING:
				return JDBCType.VARCHAR.getName();
		}
		return JDBCType.VARCHAR.getName();
	}

	private List<String> convert (DataPreparationResult result) {
		List<String> list = new ArrayList<String>();

		for(int i=0; i<result.getValues().size(); i++) {
			Object[] o = result.getValues().get(i).values().toArray();
			String s = "";
			boolean first = true;
			for(int j=0; j<o.length; j++) {
				if(first) {
					first = false;
				}
				else {
					s+=",";
				}
				if(o[j]==null) {
					s+="null";
				}
				else if(o[j] instanceof String) {
					if(o[j].toString().contains("'")) {
						String oldString = o[j].toString();
						String newString = oldString.replaceAll("'", "''");
						s+="'" + newString + "'";
					}
					else 
					{
						s+="'" + o[j] + "'";
					}
				}
				else {
					s+=o[j];
				}
			} 
			list.add(s);

		}
		return list;
	}

	@Override
	public void createDatabase(String tableName, DataPreparation dataPrep, boolean insert) throws Exception {
		DatasourceJdbc jdbcSource = new DatasourceJdbc();

		//jdbcSource.setDatabaseName();
		jdbcSource.setDriver(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_DRIVERCLASSNAME));
		//jdbcSource.setHost();

		jdbcSource.setPassword(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_PASSWORD));
		jdbcSource.setUser(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_USERNAME));
		jdbcSource.setUrl(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_JDBCURL));
		jdbcSource.setFullUrl(true);

		VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);


		DataPreparationResult result = executeDataPreparation(dataPrep);
		List<String> data = convert(result);

		/*CREATE TABLE*/
		VanillaPreparedStatement stmt = jdbcConnection.prepareQuery(createQuery(dataPrep.getDataset(), tableName));
		stmt.executeUpdate();
		stmt.close();

		/*INSERT INTO*/
		if(insert) {
			stmt = jdbcConnection.prepareQuery(insertQuery(data, tableName, dataPrep.getDataset()));
			stmt.executeUpdate();
			stmt.close();
		}

		ConnectionManager.getInstance().returnJdbcConnection(jdbcConnection);

	}

	private String insertQuery(List<String> data, String tableName, Dataset ds) {
		StringBuilder buf = new StringBuilder();
		buf.append("INSERT INTO ");
		buf.append(tableName);
		buf.append(" (");
		boolean first = true;
		for(DataColumn dc: ds.getMetacolumns()) {
			if(first) {
				first = false;
			}
			else {
				buf.append(",");
			}
			buf.append(dc.getColumnName());
		}

		buf.append(") VALUES ");


		boolean firstString = true;
		for(int i=0; i<data.size(); i++) {

			if(firstString) {
				firstString = false;
			}
			else {
				buf.append(",");
			}

			buf.append("(");
			buf.append(data.get(i));
			buf.append(")");
		}
		return buf.toString();
	}

	@Override
	public String publicationETL(DataPreparation dataPrep) throws Exception {

		DataPreparationInput dataPrepInput = new DataPreparationInput();
		dataPrepInput.setName("DataPreparationInput");
		dataPrepInput.setSelectedDataPrep(dataPrep);


		DataBaseOutputStream dataBase = new DataBaseOutputStream();
		dataBase.setName("DataBaseOutputStream");
		dataBase.setTableName("DataPreparation_" + dataPrep.getName().replace(" ", ""));
		dataBase.setDefinition("select * from DataPreparation_" + dataPrep.getName().replace(" ", ""));
		dataBase.setPositionX(200);
		dataBase.setPositionY(200);

		try {
			createDatabase("DataPreparation_" + dataPrep.getName().replace(" ", ""), dataPrep, false);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		DataBaseServer server = new DataBaseServer();
		server.setName("DataPreparation_" + dataPrep.getName().replace(" ", ""));
		DataBaseConnection dbCon = new DataBaseConnection();
		dbCon.setName("DataPreparation_" + dataPrep.getName().replace(" ", ""));
		dbCon.setLogin(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_USERNAME));
		dbCon.setPassword(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_PASSWORD));
		dbCon.setFullUrl(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_JDBCURL));
		String driver = null;
		for(DriverInfo info : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()) {
			if(info.getClassName().equals(ConfigurationManager.getProperty(VanillaConfiguration.P_MDM_DB_DRIVERCLASSNAME))) {
				driver  = info.getName();
				break;
			}
		}
		dbCon.setDriverName(driver);
		dbCon.setUseFullUrl(true);

		//dbCon.setServer(server);
		server.addConnection(dbCon);
		server.setCurrentConnection(dbCon);

		dataBase.setServer(server);
		dataPrepInput.refreshDescriptor();
		try {
			dataPrepInput.addOutput(dataBase);
			dataBase.addInput(dataPrepInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DocumentGateway gateway = new DocumentGateway();
		gateway.setName("ETL");
		gateway.addTransformation(dataPrepInput);
		gateway.addTransformation(dataBase);
		gateway.addServer(server);
		
		dataBase.setDocumentGateway(gateway);
		dataBase.setInited();
		dataBase.refreshDescriptor();
		
		for(int i=0; i<dataPrep.getNbColumns(); i++){
			dataBase.createMapping(dataPrepInput, i, i);
		}



		return gateway.getElement().asXML();
	}
}
