package bpm.vanilla.repository.services.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.thoughtworks.xstream.XStream;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.connection.manager.connection.oda.VanillaOdaConnection;
import bpm.connection.manager.connection.oda.VanillaOdaQuery;
import bpm.metadata.MetaData;
import bpm.metadata.MetaDataBuilder;
import bpm.metadata.digester.MetaDataDigester;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.workplace.api.datasource.disco.BIRTDatasourceDisconnected;
import bpm.vanilla.workplace.api.datasource.disco.FASDDatasourceDisconnected;
import bpm.vanilla.workplace.api.datasource.disco.FDDicoDatasourceDisconnected;
import bpm.vanilla.workplace.api.datasource.extractor.BIRTDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FASDDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDDicoDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDModelExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasourceType;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;
import bpm.vanilla.workplace.core.disco.DisconnectedDataset;
import bpm.vanilla.workplace.core.disco.DisconnectedDatasetParameter;
import bpm.vanilla.workplace.core.disco.IDisconnectedReplacement;
import bpm.vanilla.workplace.core.model.Dependency;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class DisconnectedManager {
	
	private static final String DISCO_FOLDER_DEFAULT = "webapps/vanilla_files/VanillaDisconnected";
	private static final String PATH_TO_CHANGE = "PATH_TO_CHANGE";

	private Logger logger = Logger.getLogger(getClass());
	
	private IRepositoryService repositoryService;
	private IDocumentationService documentationService;
	private IRepositoryApi repositoryApi;
	private String projectName;
	private String groupName;
	private int limitRows;
	private List<RepositoryItem> items;

	public DisconnectedManager(IRepositoryService repositoryService, IDocumentationService documentationService, IRepositoryApi repositoryApi, String projectName, 
			String groupName, int limitRows, List<RepositoryItem> items) {
		this.repositoryService = repositoryService;
		this.documentationService = documentationService;
		this.repositoryApi = repositoryApi;
		this.projectName = projectName;
		this.groupName = groupName;
		this.limitRows = limitRows;
		this.items = items;
	}

	public void createOfflinePackage(OutputStream outputStream) throws Exception {
		String vanillaDiscoFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_DISCO_PACKAGE_FOLDER);
		if(vanillaDiscoFolder == null || vanillaDiscoFolder.isEmpty()) {
			vanillaDiscoFolder = DISCO_FOLDER_DEFAULT;
		}
		vanillaDiscoFolder = vanillaDiscoFolder + "/" + projectName;
		
		File fileDisco = new File(vanillaDiscoFolder);
		if(!fileDisco.exists()) {
			fileDisco.mkdirs();
		}
		
		List<DisconnectedBackupConnection> backups = new ArrayList<DisconnectedBackupConnection>();
		List<PlaceImportItem> placeItems = new ArrayList<PlaceImportItem>();

		ZipOutputStream out = new ZipOutputStream(outputStream);
		
		for (RepositoryItem item : items) {
			if (!(item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FASD_TYPE || (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE))) {
				continue;
			}

			PlaceImportItem placeItem = new PlaceImportItem(item);
			
			if (item.getType() == IRepositoryApi.FD_TYPE) {
				List<RepositoryItem> fdItems = manageFd(placeItem);
				if(fdItems != null && !fdItems.isEmpty()) {
					for(RepositoryItem fdItem : fdItems) {
						PlaceImportItem fdItemDep = new PlaceImportItem(fdItem);
						manageItem(placeItems, out, backups, fdItem, vanillaDiscoFolder);
						placeItems.add(fdItemDep);

						Dependency finalDep = new Dependency(fdItem.getId());
						placeItem.addDependency(finalDep);
						
					}
				}
			}
			
			manageItem(placeItems, out, backups, item, vanillaDiscoFolder);
			placeItems.add(placeItem);
		}

		VanillaPackage vanillaPackage = new VanillaPackage();
		vanillaPackage.setName(projectName);
		vanillaPackage.setItems(placeItems);
		
		XStream xstream = new XStream();
		String modelXml = xstream.toXML(vanillaPackage);

		InputStream is = IOUtils.toInputStream(modelXml, "UTF-8"); //$NON-NLS-1$

		ZipEntry entry = new ZipEntry("descriptor"); //$NON-NLS-1$
		out.putNextEntry(entry);
		IOWriter.write(is, out, true, false);
		
		out.close();
	}

	private List<RepositoryItem> manageFd(PlaceImportItem item) throws Exception {
		String modelXml;
		try {
			modelXml = repositoryService.loadModel(item.getItem());
		} catch(Exception e1) {
			throw new Exception("Error when importing FdModel XML", e1);
		}
		
		Document doc;
		try {
			doc = DocumentHelper.parseText(modelXml);
		} catch(DocumentException e1) {
			throw new Exception("Error when parsing FdModel XML", e1);
		}
		
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		
		Element root = doc.getRootElement();
		for(Element e : (List<Element>) root.element("dependancies").elements("dependantDirectoryItemId")) {
			Integer dirItId = Integer.parseInt(e.getStringValue());
			RepositoryItem repitem = repositoryService.getDirectoryItem(dirItId);
			
			if(repitem != null) {
				items.add(repitem);
			}
		}
		
		boolean isMainModel = FDModelExtractor.isMainModel(modelXml);
		item.setIsMainModel(isMainModel);
		
		FDDatasourceExtractor datasourceExtractor = new FDDatasourceExtractor();
		List<IDatasource> datasources = datasourceExtractor.extractDatasources(modelXml);
		item.setDatasources(datasources);
		
		return items;
	}

	private void manageItem(List<PlaceImportItem> packageItems, ZipOutputStream out, List<DisconnectedBackupConnection> backups, RepositoryItem item, String vanillaDiscoFolder) throws Exception {
		String xml = repositoryService.loadModel(item);

		if (item.getType() == IRepositoryApi.FASD_TYPE || item.getType() == IRepositoryApi.FD_DICO_TYPE || (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE)) {
			List<IDatasource> datasources = getDatasources(item, xml);
			DisconnectedBackupConnection backupConnection = getBackupConnection(item, xml);

			String sqlLiteURL = createAndFillSQLLite(item.getId(), item.getName(), vanillaDiscoFolder, datasources, backupConnection);
			String sqlLiteURLtoChange = sqlLiteURL.replace(vanillaDiscoFolder, PATH_TO_CHANGE);
			
			xml = replaceDatasource(item, xml, datasources, sqlLiteURLtoChange, backupConnection);

			backups.add(backupConnection);
		}
		else if(item.getType() == IRepositoryApi.EXTERNAL_DOCUMENT) {
			InputStream is = documentationService.importExternalDocument(item);
			xml = IOUtils.toString(is, "UTF-8");
		}
		else if (item.getType() != IRepositoryApi.FD_TYPE) {
			// Not supported
			return;
		}

		InputStream is = IOUtils.toInputStream(xml, "UTF-8");

		ZipEntry entryItem = new ZipEntry(String.valueOf(item.getId()));
		out.putNextEntry(entryItem);
		IOWriter.write(is, out, true, false);
		
		File sqliteFile = new File(vanillaDiscoFolder + "/" + item.getName() + ".db");
		if(sqliteFile.exists()) {
			InputStream sqliteFileIs = new FileInputStream(sqliteFile);
	
			ZipEntry entryDB = new ZipEntry(item.getName() + ".db");
			out.putNextEntry(entryDB);
			IOWriter.write(sqliteFileIs, out, true, false);
			
			RepositoryItem dbItem = new RepositoryItem();
			dbItem.setItemName(item.getName() + ".db");
			dbItem.setType(-1);
			dbItem.setSubtype(-1);
			
			PlaceImportItem dbImportItem = new PlaceImportItem(dbItem);
			packageItems.add(dbImportItem);
			
			new File(vanillaDiscoFolder + "/" + item.getName() + ".db").delete();
		}
	}

//	private String findFileName(RepositoryItem item) throws Exception {
//		int type = item.getType();
//		Integer subtype = item.getSubtype();
//		switch (type) {
//		case IRepositoryApi.FASD_TYPE:
//			return item.getName() + ".fasd";
//		case IRepositoryApi.FD_TYPE:
//			return item.getName() + ".freedashboard";
//		case IRepositoryApi.FD_DICO_TYPE:
//			return item.getName() + ".dictionary";
//		case IRepositoryApi.CUST_TYPE:
//			if (subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
//				return item.getName() + ".rptdesign";
//			}
//		}
//
//		return item.getName();
//	}

	private List<IDatasource> getDatasources(RepositoryItem item, String xml) throws Exception {
		IDatasourceExtractor extractor = null;

		int type = item.getType();
		Integer subtype = item.getSubtype();
		switch (type) {
		case IRepositoryApi.FASD_TYPE:
			extractor = new FASDDatasourceExtractor();
			break;
		case IRepositoryApi.FD_DICO_TYPE:
			extractor = new FDDicoDatasourceExtractor();
			break;
		case IRepositoryApi.CUST_TYPE:
			if (subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				extractor = new BIRTDatasourceExtractor();
				break;
			}
		}

		if (extractor != null) {
			List<IDatasource> ds = extractor.extractDatasources(xml);
			return ds;
		}
		else {
			throw new Exception("This type of item is not supported for working disconnected.");
		}
	}

	private DisconnectedBackupConnection getBackupConnection(RepositoryItem item, String xml) throws Exception {
		IDatasourceExtractor extractor = null;

		int type = item.getType();
		Integer subtype = item.getSubtype();
		switch (type) {
		case IRepositoryApi.FASD_TYPE:
			extractor = new FASDDatasourceExtractor();
			break;
		case IRepositoryApi.FD_DICO_TYPE:
			extractor = new FDDicoDatasourceExtractor();
			break;
		case IRepositoryApi.CUST_TYPE:
			if (subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				extractor = new BIRTDatasourceExtractor();
				break;
			}
		}

		if (extractor != null) {
			return extractor.extractBackupConnection(item.getId(), xml);
		}
		else {
			throw new Exception("This type of item is not supported for working disconnected.");
		}
	}

	private String replaceDatasource(RepositoryItem item, String xml, List<IDatasource> datasources, String sqlLiteURL, DisconnectedBackupConnection backupConnection) throws Exception {
		IDisconnectedReplacement remplacement = null;

		int type = item.getType();
		Integer subtype = item.getSubtype();
		switch (type) {
		case IRepositoryApi.FASD_TYPE:
			remplacement = new FASDDatasourceDisconnected();
			break;
		case IRepositoryApi.FD_DICO_TYPE:
			remplacement = new FDDicoDatasourceDisconnected();
			break;
		case IRepositoryApi.CUST_TYPE:
			if (subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				remplacement = new BIRTDatasourceDisconnected();
				break;
			}
		}

		if (remplacement != null) {
			for (IDatasource ds : datasources) {
				xml = remplacement.replaceElement(xml, ds.getName(), sqlLiteURL, backupConnection);
			}
		}

		return xml;
	}

	private String createAndFillSQLLite(int itemId, String fileName, String vanillaDiscoFolder, List<IDatasource> datasources, DisconnectedBackupConnection backupConnection) throws Exception {
		String sqlLiteUrl = "jdbc:sqlite:" + vanillaDiscoFolder + "/" + fileName + ".db";
		ConnectionManager manager = ConnectionManager.getInstance();

		for (DisconnectedDataset dataset : backupConnection.getDatasets()) {
			IDatasource datasource = findDatasource(datasources, dataset.getDatasourceName());

			Properties publicProperties = getPublicProperties(datasource);

			VanillaOdaConnection odaConnection = manager.getOdaConnection(publicProperties, new Properties(), datasource.getExtensionId());
			VanillaOdaQuery query = odaConnection.newQuery(datasource.getExtensionId());

			if (datasource.getExtensionId().equals(IDatasourceType.METADATA)) {
				String queryText = cleanFMDTDataset((ModelDatasourceRepository)datasource, odaConnection, dataset);
				logger.debug(queryText);
				query.prepareQuery(queryText);
			}
			else if (datasource.getExtensionId().equals(IDatasourceType.JDBC)) {
				String queryText = cleanQuery(dataset);
				logger.debug(queryText);
				query.prepareQuery(queryText);
			}
			else {
				throw new Exception("This type of datasource (" + datasource.getExtensionId() + ") is not supported.");
			}

			IResultSet resultSet = query.executeQuery();
			String querySqlite = buildDataset(manager, sqlLiteUrl, itemId, dataset, resultSet);
			
			dataset.setQuerySqlite(querySqlite);
			
			manager.returnOdaConnection(odaConnection);
		}

		return sqlLiteUrl;
	}

	private String buildDataset(ConnectionManager manager, String sqlLiteUrl, int itemId, DisconnectedDataset dataset, IResultSet resultSet) throws Exception {
		String tableName = dataset.getName().replaceAll("\\s+", "") + "_" + itemId;
		
		VanillaJdbcConnection jdbcConnection = manager.getJdbcConnection(sqlLiteUrl, "", "", "org.sqlite.JDBC");

		IResultSetMetaData metadata = resultSet.getMetaData();

		StringBuffer createQuery = new StringBuffer();
		createQuery.append("CREATE TABLE " + tableName  + " (");
		boolean first = true;
		
		List<String> columnNames = new ArrayList<String>();
		
		for (int i = 1; i < metadata.getColumnCount() + 1; i++) {
			String columnName = metadata.getColumnName(i);
			int columnType = metadata.getColumnType(i);
			String columnTypeName = metadata.getColumnTypeName(i);
			logger.debug(columnType + " " + columnTypeName);

			if(!columnNames.contains(columnName)) {
				if (first) {
					createQuery.append(" " + columnName + " " + getColumnType(columnType));
					first = false;
				}
				else {
					createQuery.append(", " + columnName + " " + getColumnType(columnType));
				}
				columnNames.add(columnName);
			}
		}
		createQuery.append(")");

		logger.debug(createQuery.toString());

		VanillaPreparedStatement createStmt = jdbcConnection.prepareQuery(createQuery.toString());
		createStmt.executeUpdate();
		createStmt.close();

		int insertIndex = 0;
		while (resultSet.next()) {
			String row = createRow(tableName, metadata, resultSet);

			logger.debug(row);

			if (limitRows != 0 && insertIndex >= limitRows) {
				break;
			}

			VanillaPreparedStatement insertStatement = jdbcConnection.prepareQuery(row);
			insertStatement.executeUpdate();
			insertStatement.close();
			
			insertIndex++;
		}

		resultSet.close();

		String params = buildParams(dataset);
		logger.debug(params);
		
		manager.returnJdbcConnection(jdbcConnection);

		return "SELECT * FROM " + tableName + params;
	}

	private String buildParams(DisconnectedDataset dataset) {
		StringBuilder buf = new StringBuilder();
		if (dataset.getParams() != null) {
			buf.append(" WHERE ");
			boolean first = true;
			for (DisconnectedDatasetParameter param : dataset.getParams()) {
				if (first) {
					buf.append(param.getColumnName() + " " + param.getSeparator() + " ? ");
					first = false;
				}
				else {
					buf.append(param.getOperator() + " " + param.getColumnName() + " " + param.getSeparator() + " ? ");
				}
			}
		}
		return buf.toString();
	}

	private String getColumnType(int columnType) {
		// TODO: Fill type for SQLITE
		if (columnType == 12) {
			return "TEXT";
		}
		else if (columnType == 3) {
			return "NUMERIC";
		}
		return "";
	}

	private String createRow(String tableName, IResultSetMetaData metadata, IResultSet resultSet) throws OdaException {
		StringBuffer insertionBaseSql = new StringBuffer();
		insertionBaseSql.append("INSERT INTO ");
		insertionBaseSql.append(tableName);
		insertionBaseSql.append("(");

		boolean isFirst = true;
		for (int i = 1; i < metadata.getColumnCount() + 1; i++) {
			if (isFirst) {
				isFirst = false;
			}
			else {
				insertionBaseSql.append(", ");
			}
			insertionBaseSql.append(metadata.getColumnName(i));
		}

		insertionBaseSql.append(") values(");
		isFirst = true;
		for (int i = 1; i < metadata.getColumnCount() + 1; i++) {
			if (isFirst) {
				isFirst = false;
			}
			else {
				insertionBaseSql.append(", ");
			}

			insertionBaseSql.append(getValue(i, resultSet));
		}
		insertionBaseSql.append(");\n");

		return insertionBaseSql.toString();
	}

	private static Object getValue(int i, IResultSet rs) throws OdaException {
		Object val = null;
		int columnType = rs.getMetaData().getColumnType(i);
		switch (columnType) {

		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.INTEGER:
			val = rs.getInt(i);
			break;
		case Types.DATE:
			val = rs.getDate(i);
			break;
		case Types.TIMESTAMP:
			val = rs.getTimestamp(i);
			break;
		case Types.TIME:
			val = rs.getTime(i);
			break;

		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.REAL:
		case Types.NUMERIC:
			val = rs.getDouble(i);
			break;

		case Types.DECIMAL:
			val = rs.getBigDecimal(i);

			break;

		default:
			val = "\"" + rs.getString(i).replace("\"", "'") + "\"";
		}

		return val;
	}

	private Properties getPublicProperties(IDatasource datasource) {
		Properties props = new Properties();
		if (datasource instanceof ModelDatasourceRepository) {
			ModelDatasourceRepository dsRep = (ModelDatasourceRepository) datasource;

			props.put(IDatasourceType.METADATA_USER, dsRep.getUser());
			props.put(IDatasourceType.METADATA_PASSWORD, dsRep.getPassword());
			props.put(IDatasourceType.METADATA_URL, dsRep.getRepositoryUrl() != null ? dsRep.getRepositoryUrl() : "");
			props.put(IDatasourceType.METADATA_DIRECTORY_ITEM_ID, dsRep.getDirId());
			props.put(IDatasourceType.GROUP_NAME, dsRep.getGroupName());
			props.put(IDatasourceType.METADATA_REPOSITORY_ID, dsRep.getRepositoryId());
			props.put(IDatasourceType.METADATA_VANILLA_URL, dsRep.getVanillaRuntimeUrl());
			props.put(IDatasourceType.CONNECTION_NAME, dsRep.getConnectionName());
			props.put(IDatasourceType.BUSINESS_MODEL, dsRep.getBusinessModel() != null ? dsRep.getBusinessModel() : "");
			props.put(IDatasourceType.BUSINESS_PACKAGE, dsRep.getBusinessPackage() != null ? dsRep.getBusinessPackage() : "");
		}
		else if (datasource instanceof ModelDatasourceJDBC) {
			ModelDatasourceJDBC dsJDBC = (ModelDatasourceJDBC) datasource;

			props.put(IDatasourceType.ODA_DRIVER, dsJDBC.getDriver());
			props.put(IDatasourceType.ODA_URL, dsJDBC.getFullUrl());

			if (dsJDBC.getUser() != null) {
				props.put(IDatasourceType.ODA_USER, dsJDBC.getUser());
			}
			if (dsJDBC.getPassword() != null) {
				props.put(IDatasourceType.ODA_PASSWORD, dsJDBC.getPassword());
			}
		}
		return props;
	}

	private IDatasource findDatasource(List<IDatasource> datasources, String datasourceName) {
		for (IDatasource ds : datasources) {
			if ((ds.getId() != null && ds.getId().equals(datasourceName)) || ds.getName().equals(datasourceName)) {
				return ds;
			}
		}
		return null;
	}

	/**
	 * Connection to DB Part
	 */
	private String cleanQuery(DisconnectedDataset dataset) {
		String queryText = dataset.getQueryText();
		String lowerQuery = queryText.toLowerCase();

		int indexParam = queryText.indexOf("?");
		if (indexParam == -1) {
			return queryText;
		}

		// Check Previous
		int indexPrevious = -1;

		int indexWhere = lowerQuery.indexOf(" where ") != -1 ? lowerQuery.indexOf(" where ") : lowerQuery.indexOf("\nwhere ");
		int indexPreviousAnd = lowerQuery.substring(0, indexParam).lastIndexOf(" and ") != -1 ? lowerQuery.substring(0, indexParam).lastIndexOf(" and ") : lowerQuery.substring(0, indexParam).lastIndexOf("\nand ");
		int indexPreviousOr = lowerQuery.substring(0, indexParam).lastIndexOf(" or ") != -1 ? lowerQuery.substring(0, indexParam).lastIndexOf(" or ") : lowerQuery.substring(0, indexParam).lastIndexOf("\nor ");

		if (indexPreviousAnd == -1 && indexPreviousOr == -1) {
			indexPrevious = indexWhere;
		}
		else if (indexPreviousAnd != -1 && indexPreviousOr == -1) {
			indexPrevious = indexPreviousAnd;
		}
		else if (indexPreviousAnd == -1 && indexPreviousOr != -1) {
			indexPrevious = indexPreviousOr;
		}
		else {
			indexPrevious = indexPreviousAnd < indexPreviousOr ? indexPreviousOr : indexPreviousAnd;
		}

		// Check After
		int indexNext = -1;
		String next = null;

		int indexNextAnd = lowerQuery.indexOf(" and ", indexParam);
		int indexNextOr = lowerQuery.indexOf(" or ", indexParam);

		if (indexNextAnd == -1 && indexNextOr == -1) {
			indexNext = -1;
		}
		else if (indexNextAnd != -1 && indexNextOr == -1) {
			indexNext = indexNextAnd;
			next = "and";
		}
		else if (indexNextAnd == -1 && indexNextOr != -1) {
			indexNext = indexNextOr;
			next = "or";
		}
		else {
			indexNext = indexNextAnd < indexNextOr ? indexNextAnd : indexNextOr;
			next = indexNextAnd < indexNextOr ? "and" : "or";
		}

		// Clean Part
		String param = "";
		if (indexNext == -1 && indexPrevious != -1) {
			param = queryText.substring(indexPrevious + 1, indexParam + 1);
			queryText = queryText.replace(param, "");
		}
		else if (indexNext != -1 && indexPrevious != -1) {
			if (indexPrevious == indexWhere) {
				param = queryText.substring(indexPrevious + 1 + "where".length(), indexNext + 1 + next.length());
				queryText = queryText.replace(param, "");
			}
			else {
				param = queryText.substring(indexPrevious + 1, indexParam + 1);
				queryText = queryText.replace(param, "");
			}
		}

		String separator = getSeparator(param);
		String operator = getOperator(param);
		String columnName = getColumnName(param, separator, operator);

		dataset.addParam(new DisconnectedDatasetParameter(columnName, separator, operator));

		if (lowerQuery.indexOf("*") == -1) {
			int indexFrom = lowerQuery.indexOf(" from") != -1 ? lowerQuery.indexOf(" from") : lowerQuery.indexOf("\nfrom");
			if(queryText.indexOf(columnName) == -1) {
				
				if(columnName.contains("+")) {
					columnName = columnName.substring(0, columnName.indexOf("+"));
				}
				
				queryText = new StringBuilder(queryText).insert(indexFrom, ", " + columnName).toString();
			}
		}

		dataset.setQueryText(queryText);

		return cleanQuery(dataset);
	}

	private String cleanFMDTDataset(ModelDatasourceRepository datasource, VanillaOdaConnection odaConnection, DisconnectedDataset dataset) throws Exception {
		int itemId = Integer.parseInt(datasource.getDirId());
		RepositoryItem metadataItem = repositoryService.getDirectoryItem(itemId);
		String metadataModel = repositoryService.loadModel(metadataItem);

		InputStream fmdtStream = IOUtils.toInputStream(metadataModel);

		MetaDataBuilder builder = new MetaDataBuilder(repositoryApi);
		MetaDataDigester dig = new MetaDataDigester(fmdtStream, builder);
		MetaData metadata = dig.getModel(repositoryApi, groupName);

		IBusinessPackage pack = findBusinessPackage(datasource, metadata, groupName);

		InputStream is = IOUtils.toInputStream(dataset.getQueryText());

		SqlQueryDigester queryDigester = new SqlQueryDigester(is, groupName, pack);

		List<Prompt> prompts = queryDigester.getModel().getPrompts();
		if (prompts != null) {
			for (Prompt prt : prompts) {
				queryDigester.getModel().getSelect().add(prt.getOrigin());
				dataset.addParam(new DisconnectedDatasetParameter(prt.getOriginDataStreamElementName(), prt.getOperator(), "AND"));
				queryDigester.getModel().removePrompt(prt);
			}
		}
		
		if(queryDigester.getModel().getDesignTimeResources() != null) {
			List<Prompt> toRemove = new ArrayList<Prompt>();
			for(IResource ressource : queryDigester.getModel().getDesignTimeResources()) {
				if(ressource instanceof Prompt) {
					toRemove.add((Prompt) ressource);
				}
			}
			
			for(Prompt prt : toRemove) {
				queryDigester.getModel().removeDesignTimeResource(prt);
			}
		}

		return queryDigester.getModel().getXml();
	}

	private IBusinessPackage findBusinessPackage(ModelDatasourceRepository datasource, MetaData metadata, String groupName) {
		for (IBusinessModel model : metadata.getBusinessModels()) {
			if (model.getName().equals(datasource.getBusinessModel())) {
				for (IBusinessPackage pack : model.getBusinessPackages(groupName)) {
					if (pack.getName().equals(datasource.getBusinessPackage())) {
						return pack;
					}
				}
			}
		}
		return null;
	}

	private static String getColumnName(String param, String separator, String operator) {
		String upperParam = param.toUpperCase();
		if (upperParam.indexOf(operator) > upperParam.indexOf(separator)) {
			return param.substring(0, upperParam.indexOf(separator)).trim();
		}
		else {
			return param.substring(upperParam.indexOf(operator) + operator.length(), upperParam.indexOf(separator)).trim();
		}
	}

	private static String getSeparator(String param) {
		String upperParam = param.toUpperCase();
		if (upperParam.contains("=")) {
			return "=";
		}
		else if (upperParam.contains("<")) {
			return "<";
		}
		else if (upperParam.contains(">")) {
			return ">";
		}
		else if (upperParam.contains("<=")) {
			return ">";
		}
		else if (upperParam.contains(">=")) {
			return ">";
		}
		else if (upperParam.contains("!=")) {
			return ">";
		}
		else if (upperParam.contains("<>")) {
			return ">";
		}
		else if (upperParam.contains("IN")) {
			return "IN";
		}
		else if (upperParam.contains("LIKE")) {
			return "LIKE";
		}
		else if (upperParam.contains("BETWEEN")) {
			return "BETWEEN";
		}

		return null;
	}

	private static String getOperator(String param) {
		String upperParam = param.toUpperCase();
		if (upperParam.contains("AND")) {
			return "AND";
		}
		else if (upperParam.contains("OR")) {
			return "OR";
		}
		else if (upperParam.contains("WHERE")) {
			return "WHERE";
		}
		return null;
	}
}
