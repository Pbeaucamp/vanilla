package bpm.vanilla.platform.core.runtime.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ParameterMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.osgi.framework.Bundle;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.connection.manager.connection.oda.VanillaOdaConnection;
import bpm.connection.manager.connection.oda.VanillaOdaQuery;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.remote.MdmRemote;
import bpm.metadata.MetaDataReader;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.Prompt;
import bpm.nosql.oda.runtime.impl.HbaseConnection;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.OpenPreference;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.core.beans.data.D4CTypes;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetParameter;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceArchitect;
import bpm.vanilla.platform.core.beans.data.DatasourceCsv;
import bpm.vanilla.platform.core.beans.data.DatasourceCsvVanilla;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceHBase;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.data.HbaseTable;
import bpm.vanilla.platform.core.beans.data.IDatasourceObject;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.forms.FormField.TypeField;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.dao.preferences.OpenPreferenceDAO;
import bpm.vanilla.platform.core.runtime.dao.preferences.UserRunConfigurationDAO;
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetPositionDAO;
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsGroupDao;
import bpm.vanilla.platform.core.runtime.dao.widgets.WidgetsLogsDAO;
import bpm.vanilla.platform.core.runtime.ged.hdfs.HdfsHelper;
import bpm.vanilla.platform.core.runtime.tools.DataTypeManager;
import bpm.vanilla.platform.core.runtime.tools.DatabaseStructureGenerator;
import bpm.vanilla.platform.core.utils.IOWriter;

import com.thoughtworks.xstream.XStream;

import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Schema;
import de.micromata.opengis.kml.v_2_2_0.SimpleField;

public class PreferenceManager extends AbstractVanillaManager implements IPreferencesManager {

	private OpenPreferenceDAO openPreferenceDao;

	private UserRunConfigurationDAO userConfigurationDao;

	private WidgetsLogsDAO widgetsLogsDao;
	private WidgetsGroupDao widgetGrouDao;
	private WidgetPositionDAO widgetPosition;

	private boolean includeFastConnection = true;
	private String logoFileName;

	private XStream xstream = new XStream();

	@Override
	public List<OpenPreference> getOpenPreferences() {
		return openPreferenceDao.findAll();
	}

	@Override
	public OpenPreference getOpenPreferenceById(int id) {
		return openPreferenceDao.findByPrimaryKey(id);
	}

	@Override
	public List<OpenPreference> getOpenPreferencesForUserId(int userId) {
		return openPreferenceDao.findOpenPreferencesByUserId(userId);
	}

	@Override
	public int addOpenPreference(OpenPreference d) {
		return openPreferenceDao.save(d);
	}

	@Override
	public void delOpenPreference(OpenPreference d) {
		openPreferenceDao.delete(d);
	}

	@Override
	public void updateOpenPreference(OpenPreference d) throws Exception {
		List<OpenPreference> c = openPreferenceDao.findAll();
		Iterator<OpenPreference> it = c.iterator();
		int i = 0;
		while (it.hasNext()) {
			if (((OpenPreference) it.next()).getId() == d.getId()) {
				i++;
			}
		}
		if (i <= 1) {
			openPreferenceDao.update(d);
		}
		else {
			throw new Exception("This OpenPreference doesnt exists");
		}

	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	protected void init() throws Exception {
		this.openPreferenceDao = getDao().getOpenPreferencesDao();
		if (this.openPreferenceDao == null) {
			throw new Exception("Missing OpenPreferenceDAO");
		}
		this.userConfigurationDao = getDao().getUserRunConfigurationDao();
		if (this.userConfigurationDao == null) {
			throw new Exception("Missing UserRunConfigurationDAO");
		}

		this.widgetsLogsDao = getDao().getWidgetsDao();
		if (this.widgetsLogsDao == null) {
			throw new Exception("Missing WidgetsDAO");
		}

		this.widgetGrouDao = getDao().getWidgetsGroupDAO();
		if (this.widgetGrouDao == null) {
			throw new Exception("Missing WidgetsGroupDAO");
		}

		this.widgetPosition = getDao().getWidgetPositionDAO();
		if (this.widgetPosition == null) {
			throw new Exception("Missing WidgetPositionDAO");
		}

		String customSettingsPath = getCustomSettingsPath();

		/*
		 * load properties
		 */
		try {
			File f = new File(customSettingsPath + "customsettings.properties");

			// Bundle b =
			// Platform.getBundle("bpm.vanilla.platform.core.runtime");
			// File f =
			// b.getBundleContext().getDataFile("customsettings.propesties");
			if (f.exists()) {
				Properties p = new Properties();
				p.load(new FileInputStream(f));
				includeFastConnection = Boolean.parseBoolean(p.getProperty("includeFastConnection"));
				logoFileName = p.getProperty("logoFileName");

			}
		} catch (Exception ex) {
			Logger.getLogger(getClass()).warn("Persisting customization failed - " + ex.getMessage(), ex);
		}
		/*
		 * we try to purge the oldlogos
		 */
		try {
			Logger.getLogger(getClass()).info("Purging logo files");
			Bundle wrapperBundle = Platform.getBundle("bpm.vanilla.platform.core.wrapper");
			File folder = wrapperBundle.getBundleContext().getDataFile("images");

			// File folder = new File(customSettingsPath + "images");
			if (folder.exists()) {
				for (String s : folder.list()) {
					File f = new File(folder, s);
					if (!f.getName().equals(logoFileName)) {
						if (f.delete()) {
							Logger.getLogger(getClass()).info("Deleted logo file " + f.getName());
						}
						else {
							Logger.getLogger(getClass()).warn("Could not delete logo file " + f.getName());
						}
					}

				}
			}

		} catch (Exception ex) {

		}

		getLogger().info("init done!");
	}

	private String getCustomSettingsPath() {
		String customPropertiesPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_CUSTOM_PROPERTIES_PATH);
		if (customPropertiesPath == null) {
			customPropertiesPath = "";
			getLogger().error("The property " + VanillaConfiguration.P_CUSTOM_PROPERTIES_PATH + " is not defined in vanilla.properties.");
		}
		else {
			customPropertiesPath = !customPropertiesPath.endsWith("/") ? customPropertiesPath + "/" : customPropertiesPath;
		}
		return customPropertiesPath;
	}

	@Override
	public int addUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception {
		return userConfigurationDao.save(userRunConfiguration);
	}

	@Override
	public void deleteUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception {
		userConfigurationDao.delete(userRunConfiguration);
	}

	@Override
	public UserRunConfiguration getUserRunConfigurationById(int id) throws Exception {
		return userConfigurationDao.getUserRunConfigurationById(id);
	}

	@Override
	public List<UserRunConfiguration> getUserRunConfigurations() throws Exception {
		return userConfigurationDao.getUserRunConfigurations();
	}

	@Override
	public List<UserRunConfiguration> getUserRunConfigurationsByUserId(int userId) throws Exception {
		return userConfigurationDao.getUserRunConfigurationByUserId(userId);
	}

	@Override
	public List<UserRunConfiguration> getUserRunConfigurationsByUserIdObjectId(int userId, IObjectIdentifier objectId) throws Exception {
		return userConfigurationDao.getUserRunConfigurationByUserIdObjectId(userId, objectId);
	}

	@Override
	public void updateUserRunConfiguration(UserRunConfiguration userRunConfiguration) throws Exception {
		userConfigurationDao.update(userRunConfiguration);
	}

	@Override
	public String getCustomLogoUrl() throws Exception {
		if (logoFileName == null) {
			return null;
		}

		String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_USER_IMAGE_FOLDER);

		// Bundle wrapperBundle =
		// Platform.getBundle("bpm.vanilla.platform.core.wrapper");
		// File f = wrapperBundle.getBundleContext().getDataFile("images/" +
		// logoFileName);

		File f = new File(path + "/" + logoFileName);

		// String customSettingsPath = getCustomSettingsPath();
		// File f = new File(customSettingsPath + "images/" + logoFileName);
		if (f.exists()) {
			return path + "/" + logoFileName;
			// return "/images/logos/" + logoFileName;
		}

		return null;
	}

	@Override
	public void setCustomLogo(DataHandler datas) throws Exception {
		// String vanillaFilePath =
		// ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");

		String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_USER_IMAGE_FOLDER);

		File folder = new File(path);

		// Bundle wrapperBundle =
		// Platform.getBundle("bpm.vanilla.platform.core.wrapper");
		// File folder = wrapperBundle.getBundleContext().getDataFile("images");
		if (!folder.exists()) {
			folder.mkdirs();
		}

		if (datas == null) {
			logoFileName = null;
			persistPrefs();
			return;
		}

		/*
		 * looking for an available fileName its really not good, but for some
		 * reason, once the registered file is once acceed, the file is locked
		 * and cannot be deleted so we create some new files each time and
		 * update the store properties
		 */
		int i = 0;
		File f = new File(folder, "_customLogo_" + i + ".png");

		while (f.exists()) {
			i++;
			f = new File(folder, "_customLogo_" + i + ".png");
		}

		logoFileName = f.getName();

		try {
			Logger.getLogger(getClass()).debug("Receiving CustomLog Picture");

			FileOutputStream fos = new FileOutputStream(f);
			IOWriter.write(datas.getInputStream(), fos, true, true);
			Logger.getLogger(getClass()).info("Saved logo file " + f.getName());
			persistPrefs();
		} catch (Exception ex) {
			String m = "Failed to write customLogo file - " + ex.getMessage();
			Logger.getLogger(getClass()).error(m, ex);
			throw new Exception(m, ex);
		}
		Logger.getLogger(getClass()).debug("LogoPicture saved in " + f.getAbsolutePath());

	}

	@Override
	public boolean includeFastConnection() throws Exception {
		return includeFastConnection;
	}

	@Override
	public void setIncludeFastConnection(boolean include) throws Exception {
		this.includeFastConnection = include;
		persistPrefs();

	}

	/**
	 * we persist the customization on file system
	 */
	private void persistPrefs() {
		try {
			// Bundle b =
			// Platform.getBundle("bpm.vanilla.platform.core.runtime");
			// File f =
			// b.getBundleContext().getDataFile("customsettings.propesties");

			File f = new File(getCustomSettingsPath() + "customsettings.properties");
			if (!f.exists()) {
				f.mkdirs();
			}

			Properties p = new Properties();
			p.setProperty("includeFastConnection", includeFastConnection() + "");
			if (logoFileName != null) {
				p.setProperty("logoFileName", logoFileName);
			}
			p.store(new FileOutputStream(f), "");
		} catch (Exception ex) {
			Logger.getLogger(getClass()).warn("Persisting customization failed - " + ex.getMessage(), ex);
		}

	}

	@Override
	public int addWidget(Widgets w) {
		if (w.getWidgetId() > 0) {
			widgetsLogsDao.update(w);
			return w.getWidgetId();
		}
		return widgetsLogsDao.save(w);
	}

	@Override
	public void delWidget(Widgets w) {
		widgetsLogsDao.delete(w);
	}

	@Override
	public List<Widgets> getAllWidgets() {
		return widgetsLogsDao.findAll();
	}

	@Override
	public Widgets getWidgetById(int id) {
		return widgetsLogsDao.findByPrimaryKey(id);
	}

	@Override
	public Datasource getDatasourceById(int id) throws Exception {
		try {
			Datasource res = (Datasource) openPreferenceDao.find("From Datasource where id = " + id).get(0);
			if (res.getModel() != null && res.getModel() != "") {
				res.setObject((IDatasourceObject) xstream.fromXML(res.getModel()));
			}
			return res;
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public List<Dataset> getDatasetByDatasource(Datasource ds) throws Exception {
		List<Dataset> res = openPreferenceDao.find("From Dataset where datasourceId = " + ds.getId());
		List<Dataset> result = new ArrayList<>();
		for (Dataset dts : res) {
			try {
				List<DataColumn> columns = getDataColumnsbyDataset(dts);
				dts.setMetacolumns(columns);
				dts.setDatasource(ds);

				List<DatasetParameter> parameters = getDatasetParameters(dts);
				dts.setParameters(parameters);
				result.add(dts);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}

		return result;
	}

	private List<DatasetParameter> getDatasetParameters(Dataset dts) throws Exception {
		List<DatasetParameter> parameters = new ArrayList<DatasetParameter>();
		if (dts.getDatasource().getType() == DatasourceType.JDBC) {
			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection((DatasourceJdbc) dts.getDatasource().getObject());
			VanillaPreparedStatement rs = connection.prepareQuery(dts.getRequest());
			ParameterMetaData rsM = rs.getParameterMetadata(dts.getRequest());

			for (int i = 0; i < rsM.getParameterCount(); i++) {
				DatasetParameter param = new DatasetParameter();
				param.setName("parameter " + (i + 1));
				param.setIndex(i);
				parameters.add(param);
			}

			rs.close();
			ConnectionManager.getInstance().returnJdbcConnection(connection);

		}
		else if (dts.getDatasource().getType() == DatasourceType.FMDT) {

			DatasourceFmdt fmdt = (DatasourceFmdt) dts.getDatasource().getObject();

			RemoteVanillaPlatform rootVanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

			Properties publicProps = new Properties();
			Properties privateProps = new Properties();

			publicProps.put("URL", fmdt.getUrl());
			publicProps.put("USER", fmdt.getUser());
			publicProps.put("PASSWORD", fmdt.getPassword());
			publicProps.put("VANILLA_URL", fmdt.getUrl());
			publicProps.put("REPOSITORY_ID", fmdt.getRepositoryId() + "");
			publicProps.put("DIRECTORY_ITEM_ID", fmdt.getItemId() + "");
			publicProps.put("BUSINESS_MODEL", fmdt.getBusinessModel());
			publicProps.put("BUSINESS_PACKAGE", fmdt.getBusinessPackage());
			publicProps.put("GROUP_NAME", rootVanillaApi.getVanillaSecurityManager().getGroupById(fmdt.getGroupId()).getName());
			publicProps.put("CONNECTION_NAME", "Default");
			publicProps.put("IS_ENCRYPTED", false + "");

			VanillaOdaConnection connection = ConnectionManager.getInstance().getOdaConnection(publicProps, privateProps, "bpm.metadata.birt.oda.runtime");
			VanillaOdaQuery query = connection.newQuery("");
			query.prepareQuery(dts.getRequest());

			IParameterMetaData metadata = query.getParameterMetadata();

			for (int i = 0; i < metadata.getParameterCount(); i++) {
				DatasetParameter param = new DatasetParameter();
				param.setName(metadata.getParameterName(i + 1));
				param.setIndex(i);
				parameters.add(param);
			}

		}

		return parameters;
	}

	@Override
	public Datasource addDatasource(Datasource datasource, boolean forceCreation) throws Exception {

		datasource.setModel(xstream.toXML(datasource.getObject()));

		if(!forceCreation) {
			for(Datasource ds : getDatasources()) {
				if(ds.getObject() != null && ds.getObject().getClass().equals(datasource.getObject().getClass()) && ds.getObject().equals(datasource.getObject())) {
					return ds;
				}
			}
		}

		int datasourceId = (Integer) openPreferenceDao.save(datasource);
		datasource.setId(datasourceId);
		return datasource;
	}

	@Override
	public void updateDatasource(Datasource datasource) throws Exception {
		datasource.setModel(xstream.toXML(datasource.getObject()));
		openPreferenceDao.update(datasource);
	}

	@Override
	public void deleteDatasource(Datasource datasource) throws Exception {
		openPreferenceDao.delete(datasource);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Dataset getDatasetById(int id) throws Exception {
		try {
			List<Dataset> datasets = (List<Dataset>) openPreferenceDao.find("From Dataset where id = " + id);
			if (datasets == null || datasets.isEmpty()) {
				return null;
			}
			
			Dataset res = (Dataset) datasets.get(0);

			List<DataColumn> columns = getDataColumnsbyDataset(res);
			res.setMetacolumns(columns);
			res.setDatasource(getDatasourceById(res.getDatasourceId()));
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<Dataset> getDatasets() throws Exception {
		List<Dataset> res = openPreferenceDao.find("From Dataset");
		for (Dataset dts : res) {
			try {
				List<DataColumn> columns = getDataColumnsbyDataset(dts);
				dts.setMetacolumns(columns);
				dts.setDatasource(getDatasourceById(dts.getDatasourceId()));
			} catch(Exception e) {
			}
		}

		return res;
	}

	@Override
	public Dataset addDataset(Dataset dataset) throws Exception {

		if (dataset.getDatasource() != null && dataset.getDatasource().getId() < 1) {
			dataset.setDatasource(addDatasource(dataset.getDatasource(), false));
		}

		Datasource datasource = dataset.getDatasource();
		if (datasource == null && dataset.getDatasourceId() > 0) {
			datasource = getDatasourceById(dataset.getDatasourceId());
		}

		for (Dataset ds : getDatasetByDatasource(datasource)) {
			if (ds.getRequest().equals(dataset.getRequest())) {
				return ds;
			}
		}

		int id = (Integer) openPreferenceDao.save(dataset);

		for (DataColumn col : dataset.getMetacolumns()) {
			col.setIdDataset(id);
			col.setTypesXml(xstream.toXML(col.getTypes()));
			addDataColumn(col);
		}

		dataset.setId(id);
		return dataset;
	}

	@Override
	public void updateDataset(Dataset dataset) throws Exception {
		openPreferenceDao.update(dataset);
		List<DataColumn> old = getDataColumnsbyDataset(dataset);
		for (DataColumn col : old) {
			deleteDataColumn(col);
		}

		for (DataColumn col : dataset.getMetacolumns()) {
			col.setIdDataset(dataset.getId());
			col.setTypesXml(xstream.toXML(col.getTypes()));
			addDataColumn(col);
		}
	}

	@Override
	public void deleteDataset(Dataset dataset) throws Exception {
		openPreferenceDao.delete(dataset);
		List<DataColumn> old = getDataColumnsbyDataset(dataset);
		for (DataColumn col : old) {
			deleteDataColumn(col);
		}
	}

	@Override
	public List<DataColumn> getDataColumnsbyDataset(Dataset dataset) throws Exception {
		List<DataColumn> res = openPreferenceDao.find("From DataColumn where idDataset = " + dataset.getId() + " order by id");
		for(DataColumn c : res) {
			if(c.getTypesXml() != null) {
				c.setTypes((D4CTypes) xstream.fromXML(c.getTypesXml()));
			}
		}
		return res;
	}

	@Override
	public DataColumn getDataColumnbyId(int id) throws Exception {
		DataColumn res = (DataColumn) openPreferenceDao.find("From DataColumn where id = " + id).get(0);

		return res;
	}

	@Override
	public void addDataColumn(DataColumn column) throws Exception {
		openPreferenceDao.save(column);
	}

	@Override
	public void updateDataColumn(DataColumn column) throws Exception {
		openPreferenceDao.update(column);
	}

	@Override
	public void deleteDataColumn(DataColumn column) throws Exception {
		openPreferenceDao.delete(column);
	}

	// @Override
	// public List<String> getDatasourceCsvMetadata(Datasource
	// datasourceCsvVanilla) throws Exception {
	// DatasourceCsvVanilla csv = (DatasourceCsvVanilla)
	// datasourceCsvVanilla.getObject();
	//
	// Properties publicProperties = new Properties();
	// publicProperties.put("repository.id", csv.getRepositoryId() + "");
	// publicProperties.put("repository.user", csv.getUser());
	// publicProperties.put("repository.password", csv.getPassword());
	// publicProperties.put("repository.item.id", csv.getItemId() + "");
	// publicProperties.put("vanilla.group.id", csv.getGroupId() + "");
	// publicProperties.put("vanilla.csv.separator", csv.getSeparator());
	//
	// Properties privateProperties = new Properties();
	// String datasourceId = "bpm.csv.oda.runtime";
	//
	// VanillaOdaConnection connection =
	// ConnectionManager.getInstance().getOdaConnection(publicProperties,
	// privateProperties, datasourceId);
	//
	// VanillaOdaQuery query = connection.newQuery(null);
	// query.prepareQuery("");
	// IResultSetMetaData metadata = query.getMetadata();
	//
	// List<String> columns = new ArrayList<String>();
	// for(int i = 0 ; i < metadata.getColumnCount() ; i++) {
	// String name = metadata.getColumnName(i);
	// columns.add(name);
	// }
	//
	// return columns;
	// }

	@Override
	public List<HbaseTable> getDatasourceHbaseMetadataListTables(Datasource datasourceHBase) throws Exception {
		List<HbaseTable> result = new ArrayList<HbaseTable>();
		DatasourceHBase hbase = (DatasourceHBase) datasourceHBase.getObject();

		String configFile = makeConfigurationFileUrl(hbase.getUrl(), hbase.getPort());

		Properties connProperties = new Properties();
		connProperties.put("configfile", configFile);

		HbaseConnection conn = new HbaseConnection();
		conn.open(connProperties);
		HTableDescriptor[] listTable = conn.getListTables();
		conn.close();
		for (HTableDescriptor tableDescr : listTable) {

			HbaseTable table = new HbaseTable();
			table.setName(tableDescr.getNameAsString());
			HashMap<String, List<String>> family = new HashMap<String, List<String>>();
			for (HColumnDescriptor fam : tableDescr.getColumnFamilies()) {
				String famName = fam.getNameAsString();

				family.put(famName, new ArrayList<String>());

				Scan s;
				ResultScanner rs = null;
				try {
					HTable t = new HTable(conn.getConfig(), table.getName());
					s = new Scan();
					s.addFamily(Bytes.toBytes(famName));

					rs = t.getScanner(s);
				} catch (IOException e) {
					e.printStackTrace();
				}
				Result r = rs.next();

				for (KeyValue kv : r.list()) {
					String column = Bytes.toString(kv.getQualifier());
					family.get(famName).add(column);
				}

			}

			table.setFamilies(family);
			result.add(table);

			// result.add(tableDescr.getNameAsString());
			// XXX
		}

		return result;
	}

	@Override
	public List<DataColumn> getDatasourceHbaseMetadata(String tableName, Datasource datasourceHBase) throws Exception {

		DatasourceHBase hbase = (DatasourceHBase) datasourceHBase.getObject();

		String configFile = makeConfigurationFileUrl(hbase.getUrl(), hbase.getPort());

		Properties publicProperties = new Properties();
		publicProperties.put("configfile", configFile);

		Properties privateProperties = new Properties();
		String datasourceId = "bpm.nosql.oda.runtime.hbase";

		VanillaOdaConnection connection = ConnectionManager.getInstance().getOdaConnection(publicProperties, privateProperties, datasourceId);

		VanillaOdaQuery query = connection.newQuery(null);
		query.prepareQuery("Select from " + tableName);
		IResultSetMetaData metadata = query.getMetadata();

		List<DataColumn> columns = new ArrayList<DataColumn>();
		for (int i = 0; i < metadata.getColumnCount(); i++) {
			DataColumn col = new DataColumn();
			col.setColumnName(metadata.getColumnName(i));
			col.setColumnLabel(metadata.getColumnLabel(i));
			col.setColumnType(metadata.getColumnType(i));
			col.setColumnTypeName(metadata.getColumnTypeName(i));
			columns.add(col);
		}

		return columns;
	}

	@Override
	public List<DataColumn> getDatasourceArchitectMetadata(String tableName, Datasource datasourceArchitect) throws Exception {
		List<DataColumn> columns = new ArrayList<DataColumn>();

		DatasourceArchitect architect = (DatasourceArchitect) datasourceArchitect.getObject();
		
		String login = architect.getUser();
		String pass = architect.getPassword();
		String url = architect.getUrl();

		RemoteVanillaPlatform rootVanillaApi = new RemoteVanillaPlatform(url, login, pass);
		User user = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), rootVanillaApi.getVanillaContext().getPassword(), false);

		int userId = user.getId();

		MdmRemote mdmRemote = new MdmRemote(login, pass, url);
		RemoteGedComponent ged = new RemoteGedComponent(url, login, pass);

		try {
			Contract contract = mdmRemote.getContract(architect.getContractId());
			GedDocument doc = ged.getDocumentDefinitionById(contract.getDocId());
			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(doc, userId);

			if (contract.getVersionId() != null) {
				DocumentVersion docVersion = ged.getDocumentVersionById(contract.getVersionId());
				config = new GedLoadRuntimeConfig(doc, userId, docVersion.getVersion());
			}

			InputStream is = ged.loadGedDocument(config);
			String format = doc.getLastVersion().getFormat();

			switch (format) {
			case "xls":
			case "xlsx":
				// Workbook workbook = new XSSFWorkbook(is);
				Workbook workbook = null;
				if (format.equals("xlsx")) {
					workbook = new XSSFWorkbook(is);
				}
				else if (format.equals("xls")) {
					workbook = new HSSFWorkbook(is);
				}
				Sheet datatypeSheet = workbook.getSheetAt(0);
				Row firstRow = datatypeSheet.getRow(0);

				Iterator<Cell> cellIterator = firstRow.iterator();

				while (cellIterator.hasNext()) {

					Cell currentCell = cellIterator.next();

					DataColumn col = new DataColumn();
					col.setColumnName(currentCell.getStringCellValue());
					col.setColumnLabel(currentCell.getStringCellValue());
					col.setColumnType(currentCell.getCellType());

					switch (col.getColumnType()) {
					case 0:
						col.setColumnTypeName("NUMERIC");
						break;
					case 1:
						col.setColumnTypeName("STRING");
						break;
					case 2:
						col.setColumnTypeName("FORMULA");
						break;
					case 3:
						col.setColumnTypeName("BLANK");
						break;
					case 4:
						col.setColumnTypeName("BOOLEAN");
						break;
					case 5:
						col.setColumnTypeName("ERROR");
						break;
					}
					columns.add(col);
				}
				break;

			case "csv":
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					String line = br.readLine();
					String[] data = line.split(architect.getSeparator());
					for (int i = 0; i < data.length; i++) {
						DataColumn col = new DataColumn();
						col.setColumnName(data[i].replace("\"", ""));
						col.setColumnLabel(data[i].replace("\"", ""));
						// col.setColumnType(columnType);
						columns.add(col);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			case "kml":
				
				try {
					Kml kml = Kml.unmarshal(is);
					de.micromata.opengis.kml.v_2_2_0.Document document = (de.micromata.opengis.kml.v_2_2_0.Document) kml.getFeature();
					
					Schema schema = document.getSchema().get(0);
					List<SimpleField> kmlColumns = schema.getSimpleField();
					for (SimpleField field : kmlColumns) {
						
						DataColumn col = new DataColumn();
						col.setColumnName(field.getName());
						col.setColumnLabel(field.getName());
						// col.setColumnType(columnType);
						columns.add(col);
					}
				} catch(Exception e1) {
				}
				
				//add coordinates data
				DataColumn col = new DataColumn();
				col.setColumnName("latitude");
				col.setColumnLabel("latitude");
				// col.setColumnType("string");
				columns.add(col);
				
				col = new DataColumn();
				col.setColumnName("longitude");
				col.setColumnLabel("longitude");
				// col.setColumnType("string");
				columns.add(col);
				
				col = new DataColumn();
				col.setColumnName("altitude");
				col.setColumnLabel("altitude");
				// col.setColumnType("string");
				columns.add(col);
				
				col = new DataColumn();
				col.setColumnName("sub_id");
				col.setColumnLabel("sub_id");
				// col.setColumnType("string");
				columns.add(col);
				
				col = new DataColumn();
				col.setColumnName("placemark_name");
				col.setColumnLabel("placemark_name");
				// col.setColumnType("string");
				columns.add(col);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return columns;
	}

	private String makeConfigurationFileUrl(String host, String port) throws Exception {

		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String relPath = "hbase_config/hbase_config.xml";
		String path = basePath + relPath;

		FileInputStream fis = new FileInputStream(new File(path));
		String xml = IOUtils.toString(fis, "UTF-8");
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		List list = doc.selectNodes("//configuration/property/value");
		Element el = (Element) list.get(0);
		el.setText(host + ":" + port);
		el = (Element) list.get(1);
		el.setText(port);

		XMLWriter writer = new XMLWriter(new FileWriter(path));
		writer.write(doc);
		writer.close();
		return path;
	}

	@Override
	public List<String> getDatasourceCsvVanillaMetadata(Datasource datasourceCsvVanilla) throws Exception {

		DatasourceCsvVanilla csv = (DatasourceCsvVanilla) datasourceCsvVanilla.getObject();

		Properties publicProperties = new Properties();
		publicProperties.put("repository.id", csv.getRepositoryId() + "");
		publicProperties.put("repository.user", csv.getUser());
		publicProperties.put("repository.password", csv.getPassword());
		publicProperties.put("repository.item.id", csv.getItemId() + "");
		publicProperties.put("vanilla.group.id", csv.getGroupId() + "");
		publicProperties.put("vanilla.csv.separator", csv.getSeparator());

		Properties privateProperties = new Properties();
		String datasourceId = "bpm.csv.oda.runtime";

		VanillaOdaConnection connection = ConnectionManager.getInstance().getOdaConnection(publicProperties, privateProperties, datasourceId);

		VanillaOdaQuery query = connection.newQuery(null);
		query.prepareQuery("");
		IResultSetMetaData metadata = query.getMetadata();

		List<String> columns = new ArrayList<String>();
		for (int i = 0; i < metadata.getColumnCount(); i++) {
			String name = metadata.getColumnName(i);
			columns.add(name);
		}

		return columns;
	}

	@Override
	public String getHdfsFile(String path) throws Exception {
		InputStream is = null;

		is = HdfsHelper.loadFileFromHDFS(path);

		long time = new Date().getTime();
		String fName;
		if (path.lastIndexOf("/") == -1) {
			fName = path;
		}
		else {
			fName = path.substring(path.lastIndexOf("/"));
		}
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String relPath = "";

		relPath = "/air_files/" + time + "_" + fName;

		try {
			File file = new File(basePath + "/" + relPath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			IOWriter.write(is, fileOutputStream, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "success;" + relPath;

	}

	@Override
	public String testJdbcDatasource(DatasourceJdbc datasource) throws Exception {

		try {
			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(datasource);
			ConnectionManager.getInstance().returnJdbcConnection(connection);
		} catch (Exception e) {
			return "Error - " + e.getMessage();
		}

		return "";
	}

	@Override
	public List<DatabaseTable> getDatabaseStructure(Datasource datasource, boolean managePostgres) throws Exception {
		return DatabaseStructureGenerator.createDatabaseStructure(datasource, managePostgres);
	}

	@Override
	public boolean testConnection(Datasource datasource) throws Exception {
		return DatabaseStructureGenerator.testConnection(datasource);
	}

	@Override
	public int getCountQuery(Dataset dataset) throws Exception {
		RemoteVanillaPlatform rootVanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		RemoteAdminManager admin = new RemoteAdminManager(rootVanillaApi.getVanillaUrl(), null, Locale.getDefault());
		User u = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), rootVanillaApi.getVanillaContext().getPassword(), false); //$NON-NLS-1$
		String session = admin.connect(u);

		RemoteSmartManager smartManager = new RemoteSmartManager(rootVanillaApi.getVanillaUrl(), session, Locale.getDefault());
		Map<String, List<Serializable>> map = null;
		
		int i = 0;	
		switch (dataset.getDatasource().getType()) {
		case CSVVanilla:
			DatasourceCsvVanilla csvSource = (DatasourceCsvVanilla) dataset.getDatasource().getObject();
			Properties publicProperties = new Properties();
			publicProperties.put("repository.id", csvSource.getRepositoryId() + "");
			publicProperties.put("repository.user", csvSource.getUser());
			publicProperties.put("repository.password", csvSource.getPassword());
			publicProperties.put("repository.item.id", csvSource.getItemId() + "");
			publicProperties.put("vanilla.group.id", csvSource.getGroupId() + "");
			publicProperties.put("vanilla.csv.separator", csvSource.getSeparator());

			Properties privateProperties = new Properties();
			String datasourceId = "bpm.csv.oda.runtime";

			VanillaOdaConnection connection = ConnectionManager.getInstance().getOdaConnection(publicProperties, privateProperties, datasourceId);
			VanillaOdaQuery query = connection.newQuery(null);
			query.prepareQuery(dataset.getRequest());
			IResultSet csvResult = query.executeQuery();

			i = 0;
			while (csvResult.next()) {
				i++;
			}
			return i;
		case FMDT:
			DatasourceFmdt fmdtSource = (DatasourceFmdt) dataset.getDatasource().getObject();

			Group group;
			Repository repo;
			IRepositoryApi sock;
			if (fmdtSource.isDefaultUrl()) {
				group = rootVanillaApi.getVanillaSecurityManager().getGroupById(fmdtSource.getGroupId());
				repo = rootVanillaApi.getVanillaRepositoryManager().getRepositoryById(fmdtSource.getRepositoryId());

				sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(rootVanillaApi.getVanillaUrl(), fmdtSource.getUser(), fmdtSource.getPassword()), group, repo));
			}
			else {
				RemoteVanillaPlatform remote = new RemoteVanillaPlatform(fmdtSource.getUrl(), fmdtSource.getUser(), fmdtSource.getPassword());

				group = remote.getVanillaSecurityManager().getGroupById(fmdtSource.getGroupId());
				repo = remote.getVanillaRepositoryManager().getRepositoryById(fmdtSource.getRepositoryId());

				sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(fmdtSource.getUrl(), fmdtSource.getUser(), fmdtSource.getPassword()), group, repo));
			}
			RepositoryItem item = sock.getRepositoryService().getDirectoryItem(fmdtSource.getItemId());
			String xml = sock.getRepositoryService().loadModel(item);

			List<IBusinessModel> bModels = MetaDataReader.read(group.getName(), IOUtils.toInputStream(xml, "UTF-8"), sock, false);

			IBusinessPackage pack = null;
			for (IBusinessModel model : bModels) {
				if (model.getName().equals(fmdtSource.getBusinessModel())) {
					for (IBusinessPackage p : model.getBusinessPackages(group.getName())) {
						if (p.getName().equals(fmdtSource.getBusinessPackage())) {
							pack = p;
							break;
						}
					}
				}
			}

			SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(dataset.getRequest(), "UTF-8"), group.getName(), pack);

			QuerySql sqlquery = dig.getModel();
			EffectiveQuery equery = SqlQueryGenerator.getQuery(null, sock.getContext().getVanillaContext(), pack, sqlquery, group.getName(), false, new HashMap<Prompt, List<String>>());

			return pack.countQuery(0, "Default", equery.getGeneratedQuery().replace("`", "\""));
		case JDBC:
			DatasourceJdbc jdbcSource = (DatasourceJdbc) dataset.getDatasource().getObject();

			VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
			VanillaPreparedStatement rs = jdbcConnection.createStatement(dataset.getRequest(),java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			ResultSet jdbcResult = rs.executeQuery(dataset.getRequest());
			jdbcResult.last();
			return jdbcResult.getRow();
		case R:
			smartManager.loadRDatasetTemp(dataset);
			map = smartManager.getRDatasetData(dataset);
			if (map != null) {
				for (String key : map.keySet()) {
					List<Serializable> values = map.get(key);
					return values.size();
				}
			}
			return 0;
		case HBase:
//			DatasourceHBase hbase = (DatasourceHBase) dataset.getDatasource().getObject();

			Properties HBasePublicProperties = new Properties();
			HBasePublicProperties.put("configfile", ""); // TODO

			Properties HBasePrivateProperties = new Properties();
			String HBaseDatasourceId = "bpm.nosql.oda.runtime.hbase";

			VanillaOdaConnection HBaseConnection = ConnectionManager.getInstance().getOdaConnection(HBasePublicProperties, HBasePrivateProperties, HBaseDatasourceId);
			VanillaOdaQuery HBaseQuery = HBaseConnection.newQuery(null);
			HBaseQuery.prepareQuery(dataset.getRequest());
			IResultSet hBaseResult = HBaseQuery.executeQuery();
			return hBaseResult.getRow();
		case CSV:
			DatasourceCsv csv = (DatasourceCsv) dataset.getDatasource().getObject();

			String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
			Path path = Paths.get(basePath, csv.getFilePath());
			byte[] resultStream = Files.readAllBytes(path);

			boolean hasHeader = csv.getHasHeader();
			String sep = csv.getSeparator();

			String request = "";
			Document doc = DocumentHelper.parseText(dataset.getRequest());
			List<Element> indexes = doc.selectNodes("//bpm.csv.oda.query/columns/column");
			for (Element idx : indexes) {
				request += idx.getText() + ",";
			}
			request = request.substring(0, request.length() - 1);

			String nameTempFile;
			if (path.toString().substring(path.toString().lastIndexOf(".") + 1).equals("csv")) {
				nameTempFile = smartManager.loadRCsvFile(resultStream, hasHeader, sep);
				smartManager.addRDatasetTempFile(dataset, request, nameTempFile);
			}
			else {
				nameTempFile = smartManager.loadRExcelFile(resultStream, hasHeader, dataset);
				smartManager.addRDatasetTempFile(dataset, request, nameTempFile);
			}
			map = smartManager.getRDatasetData(dataset);
			if (map != null) {
				for (String key : map.keySet()) {
					List<Serializable> values = map.get(key);
					return values.size();
				}
			}
			return 0;
		case ARCHITECT:
			DatasourceArchitect architectSource = (DatasourceArchitect) dataset.getDatasource().getObject();

			User user = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), rootVanillaApi.getVanillaContext().getPassword(), false);

			int userId = user.getId();

			MdmRemote mdmRemote = new MdmRemote(architectSource.getUser(), architectSource.getPassword(), architectSource.getUrl());
			RemoteGedComponent ged = new RemoteGedComponent(architectSource.getUrl(), architectSource.getUser(), architectSource.getPassword());

			try {
				Contract contract = mdmRemote.getContract(architectSource.getContractId());
				GedDocument document = ged.getDocumentDefinitionById(contract.getDocId());
				GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(document, userId);

				if (contract.getVersionId() != null) {
					DocumentVersion docVersion = ged.getDocumentVersionById(contract.getVersionId());
					config = new GedLoadRuntimeConfig(document, userId, docVersion.getVersion());
				}
				
				try (InputStream is = ged.loadGedDocument(config)) {
					String format = document.getLastVersion().getFormat();

					switch (format) {
					case "xlsx":
					case "xls":
						Workbook workbook = null;
						if (format.equals("xlsx")) {
							workbook = new XSSFWorkbook(is);
						}
						else if (format.equals("xls")) {
							workbook = new HSSFWorkbook(is);
						}
						
						Sheet datatypeSheet = workbook.getSheetAt(0);
						i = datatypeSheet.getLastRowNum();
						is.close();
						return i;
					case "csv":
						Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
						CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(architectSource.getSeparator().charAt(0));
						CSVParser parser = new CSVParser(reader, csvFileFormat);
						
						return new Long(parser.getRecordNumber()).intValue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case SOCIAL:
			smartManager.loadRDatasetTemp(dataset);
			
			map = smartManager.getRDatasetData(dataset);
			if (map != null) {
				for (String key : map.keySet()) {
					List<Serializable> values = map.get(key);
					return values.size();
				}
			}
			return 0;
		}
		return 0;
	}
	
	@Override
	public DatasetResultQuery getResultQuery(Dataset dataset, int limit) throws Exception {
		DatasetResultQuery result = new DatasetResultQuery();
		Map<String, List<Serializable>> map = new LinkedHashMap<String, List<Serializable>>();
		List<String> columnsName = new ArrayList<String>();

		RemoteVanillaPlatform rootVanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		RemoteAdminManager admin = new RemoteAdminManager(rootVanillaApi.getVanillaUrl(), null, Locale.getDefault());
		User u = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), rootVanillaApi.getVanillaContext().getPassword(), false); //$NON-NLS-1$
		String session = admin.connect(u);

		RemoteSmartManager smartManager = new RemoteSmartManager(rootVanillaApi.getVanillaUrl(), session, Locale.getDefault());

		boolean[] aredigits = null;// = new boolean[columnsName.size()];
		int i = 0;
		switch (dataset.getDatasource().getType()) {
		case CSVVanilla:
			DatasourceCsvVanilla csvSource = (DatasourceCsvVanilla) dataset.getDatasource().getObject();
			Properties publicProperties = new Properties();
			publicProperties.put("repository.id", csvSource.getRepositoryId() + "");
			publicProperties.put("repository.user", csvSource.getUser());
			publicProperties.put("repository.password", csvSource.getPassword());
			publicProperties.put("repository.item.id", csvSource.getItemId() + "");
			publicProperties.put("vanilla.group.id", csvSource.getGroupId() + "");
			publicProperties.put("vanilla.csv.separator", csvSource.getSeparator());

			Properties privateProperties = new Properties();
			String datasourceId = "bpm.csv.oda.runtime";

			VanillaOdaConnection connection = ConnectionManager.getInstance().getOdaConnection(publicProperties, privateProperties, datasourceId);
			VanillaOdaQuery query = connection.newQuery(null);
			query.prepareQuery(dataset.getRequest());
			IResultSet csvResult = query.executeQuery();
			IResultSetMetaData csvMeta = csvResult.getMetaData();

			for (i = 1; i <= csvMeta.getColumnCount(); i++) {
				columnsName.add(csvMeta.getColumnLabel(i));
				map.put(columnsName.get(i), new ArrayList<Serializable>());
			}
			aredigits = new boolean[columnsName.size()];
			i = 0;
			while (csvResult.next()) {
				int j = 0;
				for (String colname : columnsName) {

					if (i == 0) {
						String c = ((String) csvResult.getString(colname));
						boolean isDigit = false;
						try {
							Double.parseDouble(c);
							isDigit = true;
						} catch (Exception e) {
						}

						aredigits[j] = isDigit;

					}

					if (aredigits[j]) {
						try {
							map.get(colname).add(csvResult.getDouble(colname));
						} catch (Exception e) {
							map.get(colname).add(csvResult.getString(colname));
							aredigits[j] = false;
						}
					}
					else {
						map.get(colname).add(csvResult.getString(colname));
					}
					j++;
				}
				i++;
			}
			break;
		case FMDT:
			DatasourceFmdt fmdtSource = (DatasourceFmdt) dataset.getDatasource().getObject();

			Group group;
			Repository repo;
			IRepositoryApi sock;
			if (fmdtSource.isDefaultUrl()) {
				group = rootVanillaApi.getVanillaSecurityManager().getGroupById(fmdtSource.getGroupId());
				repo = rootVanillaApi.getVanillaRepositoryManager().getRepositoryById(fmdtSource.getRepositoryId());

				sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(rootVanillaApi.getVanillaUrl(), fmdtSource.getUser(), fmdtSource.getPassword()), group, repo));
			}
			else {
				RemoteVanillaPlatform remote = new RemoteVanillaPlatform(fmdtSource.getUrl(), fmdtSource.getUser(), fmdtSource.getPassword());

				group = remote.getVanillaSecurityManager().getGroupById(fmdtSource.getGroupId());
				repo = remote.getVanillaRepositoryManager().getRepositoryById(fmdtSource.getRepositoryId());

				sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(fmdtSource.getUrl(), fmdtSource.getUser(), fmdtSource.getPassword()), group, repo));
			}
			RepositoryItem item = sock.getRepositoryService().getDirectoryItem(fmdtSource.getItemId());
			String xml = sock.getRepositoryService().loadModel(item);

			List<IBusinessModel> bModels = MetaDataReader.read(group.getName(), IOUtils.toInputStream(xml, "UTF-8"), sock, false);

			IBusinessPackage pack = null;
			for (IBusinessModel model : bModels) {
				if (model.getName().equals(fmdtSource.getBusinessModel())) {
					for (IBusinessPackage p : model.getBusinessPackages(group.getName())) {
						if (p.getName().equals(fmdtSource.getBusinessPackage())) {
							pack = p;
							break;
						}
					}
				}
			}

			SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(dataset.getRequest(), "UTF-8"), group.getName(), pack);

			QuerySql sqlquery = dig.getModel();
			EffectiveQuery equery = SqlQueryGenerator.getQuery(null, sock.getContext().getVanillaContext(), pack, sqlquery, group.getName(), false, new HashMap<Prompt, List<String>>());

			List<List<String>> fmdtResult = pack.executeQuery(limit, "Default", equery.getGeneratedQuery().replace("`", "\""));

			// List<DataColumn> columns =
			// api.getVanillaPreferencesManager().getDataColumnsbyDataset(dataset);
			List<DataColumn> columns = dataset.getMetacolumns();
			i = 0;
			for (DataColumn col : columns) {
				map.put(col.getColumnLabel(), new ArrayList<Serializable>());
				i++;
			}

			aredigits = new boolean[columns.size()];
			i = 0;
			for (List<String> line : fmdtResult) {
				int j = 0;
				for (DataColumn col : columns) {
					String colname = col.getColumnLabel();

					if (i == 0) {
						String c = line.get(j);
						boolean isDigit = false;
						try {
							Integer.parseInt(c);
							isDigit = true;
						} catch (Exception e) {
						}

						aredigits[j] = isDigit;
					}

					// if (aredigits[j]) {
					// map.get(colname).add(Double.parseDouble(line.get(j)));
					// }
					// else {
					map.get(colname).add(line.get(j));
					// }
					j++;
				}
				i++;
			}
			break;
		case JDBC:
			DatasourceJdbc jdbcSource = (DatasourceJdbc) dataset.getDatasource().getObject();

			VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
			VanillaPreparedStatement rs = jdbcConnection.prepareQuery(dataset.getRequest());
			rs.setMaxRows(limit);
			ResultSet jdbcResult = rs.executeQuery(dataset.getRequest());
			ResultSetMetaData jdbcMeta = jdbcResult.getMetaData();

			for (i = 1; i <= jdbcMeta.getColumnCount(); i++) {
				columnsName.add(jdbcMeta.getColumnLabel(i));
				map.put(columnsName.get(i - 1), new ArrayList<Serializable>());
			}

			aredigits = new boolean[columnsName.size()];
			i = 0;
			while (jdbcResult.next()) {
				int j = 0;
				for (String colname : columnsName) {

					if (i == 0) {
						String c = ((String) jdbcResult.getString(colname));
						boolean isDigit = false;
						try {
							Integer.parseInt(c);
							isDigit = true;
						} catch (Exception e) {
						}

						aredigits[j] = isDigit;

					}

					if (aredigits[j]) {
						try {
							String c = ((String) jdbcResult.getString(colname));
							if (c.contains(".")) {
								map.get(colname).add(jdbcResult.getDouble(colname));
							}
							else if (!c.startsWith("0")) {
								map.get(colname).add(jdbcResult.getInt(colname));
							}
							else {
								map.get(colname).add(jdbcResult.getString(colname));
								aredigits[j] = false;
							}
						} catch (Exception e) {
							map.get(colname).add(jdbcResult.getString(colname));
							aredigits[j] = false;
						}
					}
					else {
						map.get(colname).add(jdbcResult.getString(colname));
					}
					j++;
				}
				i++;
			}

			break;
		case R:
			String req = dataset.getRequest();
			String name = dataset.getName();
			smartManager.loadRDatasetTemp(dataset);
			map = smartManager.getRDatasetData(dataset);
			break;
		case HBase:
			DatasourceHBase hbase = (DatasourceHBase) dataset.getDatasource().getObject();

			Properties HBasePublicProperties = new Properties();
			HBasePublicProperties.put("configfile", ""); // TODO

			Properties HBasePrivateProperties = new Properties();
			String HBaseDatasourceId = "bpm.nosql.oda.runtime.hbase";

			VanillaOdaConnection HBaseConnection = ConnectionManager.getInstance().getOdaConnection(HBasePublicProperties, HBasePrivateProperties, HBaseDatasourceId);
			VanillaOdaQuery HBaseQuery = HBaseConnection.newQuery(null);
			HBaseQuery.prepareQuery(dataset.getRequest());
			IResultSet HBaseResult = HBaseQuery.executeQuery();
			IResultSetMetaData HBaseMeta = HBaseResult.getMetaData();

			for (i = 1; i <= HBaseMeta.getColumnCount(); i++) {
				columnsName.add(HBaseMeta.getColumnLabel(i));
				map.put(columnsName.get(i), new ArrayList<Serializable>());
			}

			aredigits = new boolean[columnsName.size()];
			i = 0;
			while (HBaseResult.next()) {
				int j = 0;
				for (String colname : columnsName) {

					if (i == 0) {
						String c = ((String) HBaseResult.getString(colname));
						boolean isDigit = false;
						try {
							Double.parseDouble(c);
							isDigit = true;
						} catch (Exception e) {
						}

						aredigits[j] = isDigit;

					}

					if (aredigits[j]) {
						try {
							map.get(colname).add(HBaseResult.getDouble(colname));
						} catch (Exception e) {
							map.get(colname).add(HBaseResult.getString(colname));
							aredigits[j] = false;
						}
					}
					else {
						map.get(colname).add(HBaseResult.getString(colname));
					}
					j++;
				}

				i++;
			}
			break;
		case CSV:
			DatasourceCsv csv = (DatasourceCsv) dataset.getDatasource().getObject();

			String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
			Path path = Paths.get(basePath, csv.getFilePath());
			byte[] resultStream = Files.readAllBytes(path);

			boolean hasHeader = csv.getHasHeader();
			String sep = csv.getSeparator();

			String request = "";
			Document doc = DocumentHelper.parseText(dataset.getRequest());
			List<Element> indexes = doc.selectNodes("//bpm.csv.oda.query/columns/column");
			for (Element idx : indexes) {
				request += idx.getText() + ",";
			}
			request = request.substring(0, request.length() - 1);

			String nameTempFile;
			if (path.toString().substring(path.toString().lastIndexOf(".") + 1).equals("csv")) {
				nameTempFile = smartManager.loadRCsvFile(resultStream, hasHeader, sep);
				smartManager.addRDatasetTempFile(dataset, request, nameTempFile);
			}
			else {
				nameTempFile = smartManager.loadRExcelFile(resultStream, hasHeader, dataset);
				smartManager.addRDatasetTempFile(dataset, request, nameTempFile);
			}
			map = smartManager.getRDatasetData(dataset);
			break;
		case ARCHITECT:

			String[] requiredColumns = dataset.getRequest().split(",");

			DatasourceArchitect architectSource = (DatasourceArchitect) dataset.getDatasource().getObject();

			User user = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), rootVanillaApi.getVanillaContext().getPassword(), false);

			int userId = user.getId();

			MdmRemote mdmRemote = new MdmRemote(architectSource.getUser(), architectSource.getPassword(), architectSource.getUrl());
			RemoteGedComponent ged = new RemoteGedComponent(architectSource.getUrl(), architectSource.getUser(), architectSource.getPassword());

			try {
				Contract contract = mdmRemote.getContract(architectSource.getContractId());
				GedDocument document = ged.getDocumentDefinitionById(contract.getDocId());
				GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(document, userId);

				if (contract.getVersionId() != null) {
					DocumentVersion docVersion = ged.getDocumentVersionById(contract.getVersionId());
					config = new GedLoadRuntimeConfig(document, userId, docVersion.getVersion());
				}
				InputStream is = ged.loadGedDocument(config);
				String format = document.getLastVersion().getFormat();

				switch (format) {
				case "xlsx":
				case "xls":
					Workbook workbook = null;
					if (format.equals("xlsx")) {
						workbook = new XSSFWorkbook(is);
					}
					else if (format.equals("xls")) {
						workbook = new HSSFWorkbook(is);
					}
					// Workbook workbook = new XSSFWorkbook(is);
					Sheet datatypeSheet = workbook.getSheetAt(0);
					Row firstRow = datatypeSheet.getRow(0);

					for (int x = 0; x < requiredColumns.length; x++) {
						Iterator<Cell> cellIterator = firstRow.iterator();
						int mainIndex = -1;

						String cell = "";

						int cellIndex = 0;
						while (cellIterator.hasNext()) {
							
							cell = cellIterator.next().getStringCellValue();
							if (cell.equals(requiredColumns[x])) {
								map.put(cell, new ArrayList<Serializable>());
								mainIndex = cellIndex;
								break;
							}
							cellIndex++;
						}

						Iterator<Row> rowIterator = datatypeSheet.iterator();
						while (rowIterator.hasNext()) {
							Row r = rowIterator.next();
							Iterator<Cell> cells = r.cellIterator();
							int in = 0;
							while (cells.hasNext()) {
//								System.out.println(in);
								Cell c = cells.next();
								if (mainIndex == in) {
									map.get(cell).add(c.toString());
									break;
								}
								in++;
							}
						}

					}

					for (List<Serializable> l : map.values()) {
						l.remove(0);
					}

					is.close();
					break;
				case "csv":

					Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//					Reader reader = new BufferedReader(new InputStreamReader(is));
					CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(architectSource.getSeparator().charAt(0));
					CSVParser parser = new CSVParser(reader, csvFileFormat);
					
					Iterator<CSVRecord> csvIterator = parser.iterator();
					CSVRecord recordHeader = csvIterator.next();

					String[] columnNames = new String[recordHeader.size()];
					for (int x=0; x<recordHeader.size(); x++) {
						columnNames[x] = recordHeader.get(x);
					}
					
					List<Integer> dataIndexes = new ArrayList<Integer>();
					for (int x = 0; x < requiredColumns.length; x++) {
						int y = 0;
						for (y = 0; y < columnNames.length; y++) {

							if (requiredColumns[x].equals(columnNames[y])) {
								map.put(columnNames[y], new ArrayList<Serializable>());
								break;
							}
						}
						dataIndexes.add(y);
					}

					while (csvIterator.hasNext()) {
						CSVRecord recordData = csvIterator.next();
						for (int s = 0; s < recordData.size(); s++) {
							if (dataIndexes.contains(s)) {
								map.get(columnNames[s]).add(recordData.get(s));
							}
						}
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			break;
		case SOCIAL:
			req = dataset.getRequest();
			name = dataset.getName();
			smartManager.loadRDatasetTemp(dataset);
			map = smartManager.getRDatasetData(dataset);
			break;
		}
		result.setDataset(dataset);
		
		result.setResult(map);

		DataTypeManager.convertResultTypes(result);
		
		return result;
	}

	@Override
	public DatasetResultQuery getResultQuery(Dataset dataset) throws Exception {
		return getResultQuery(dataset, 0);
	}

	@Override
	public List<Widgets> getWidgetsByUser(int userId) throws Exception {
		return widgetsLogsDao.findByUserId(userId);
	}

	@Override
	public void executeFormQuery(Form form) throws Exception {
		DatasourceJdbc jdbcSource = (DatasourceJdbc) form.getDatasource().getObject();

		VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);

		VanillaPreparedStatement stmt = jdbcConnection.prepareQuery(generateInsertQuery(form));
		stmt.executeUpdate();
		stmt.close();

		ConnectionManager.getInstance().returnJdbcConnection(jdbcConnection);
	}

	private String generateInsertQuery(Form form) {
		StringBuilder buf = new StringBuilder();
		buf.append("Insert into ");
		buf.append(form.getTableName());
		buf.append(" (");
		boolean first = true;
		for (FormField f : form.getFields()) {
			if (first) {
				first = false;
			}
			else {
				buf.append(",");
			}
			buf.append(f.getColumnName());
		}

		buf.append(") values (");
		first = true;
		for (FormField f : form.getFields()) {
			if (first) {
				first = false;
			}
			else {
				buf.append(",");
			}

			String value = f.getValue();
			if (f.getType().equals(TypeField.CHECKBOX)) {
				if (Boolean.parseBoolean(value)) {
					buf.append("1");
				}
				else {
					buf.append("0");
				}
			}
			else {
				try {
					Double.parseDouble(value);
					buf.append(value);
				} catch (Exception e) {
					if (value == null || value.isEmpty()) {
						buf.append("null");
					}
					else {
						buf.append("'" + value + "'");
					}
				}
			}
		}
		buf.append(")");
		return buf.toString();
	}

	@Override
	public List<Map<String, FormField>> executeFormSearchQuery(String query, Form form) throws Exception {
		DatasourceJdbc jdbcSource = (DatasourceJdbc) form.getDatasource().getObject();

		VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
		VanillaPreparedStatement stmt = jdbcConnection.prepareQuery(query);
		ResultSet rs = stmt.executeQuery();

		List<Map<String, FormField>> result = new ArrayList<>();

		while (rs.next()) {
			Map<String, FormField> line = new HashMap<>();

			for (FormField f : form.getFields()) {
				String val = rs.getString(f.getColumnName());
				f.setValue(val);
				line.put(f.getLabel(), f);
			}
			result.add(line);
		}

		return result;
	}

	private Object getFieldValue(FormField f, Map<String, FormField> editedLine) {
		String value = f.getValue();

		if (value == null || value.isEmpty()) {
			if (editedLine.get(f.getLabel()).getValue() != null && editedLine.get(f.getLabel()).getValue().isEmpty()) {
				if (f.getColumnType() == Types.DATE) {
					return "null";
				}
				return "''";
			}
			return "null";
		}

		if (f.getColumnType() == Types.BIGINT || f.getColumnType() == Types.DECIMAL || f.getColumnType() == Types.DOUBLE || f.getColumnType() == Types.FLOAT || f.getColumnType() == Types.INTEGER || f.getColumnType() == Types.NUMERIC || f.getColumnType() == Types.SMALLINT) {
			try {
				Double.parseDouble(value);
				return value;
			} catch (Exception e) {
				return "'" + value.replace("'", "''") + "'";
			}
		}
		else {
			return "'" + value.replace("'", "''") + "'";
		}

	}

	private Object getWhereFieldValue(FormField f) {
		String value = f.getValue();

		if (value == null || value.isEmpty()) {
			if (f.getColumnType() == Types.BIGINT || f.getColumnType() == Types.DECIMAL || f.getColumnType() == Types.DOUBLE || f.getColumnType() == Types.FLOAT || f.getColumnType() == Types.INTEGER || f.getColumnType() == Types.NUMERIC || f.getColumnType() == Types.SMALLINT) {
				return " is null";
			}
			if (f.getColumnType() == Types.DATE) {
				return " is null";
			}
			return " is null or " + f.getColumnName() + " = '' ";
		}

		if (f.getColumnType() == Types.BIGINT || f.getColumnType() == Types.DECIMAL || f.getColumnType() == Types.DOUBLE || f.getColumnType() == Types.FLOAT || f.getColumnType() == Types.INTEGER || f.getColumnType() == Types.NUMERIC || f.getColumnType() == Types.SMALLINT) {
			try {
				Double.parseDouble(value);
				return " = " + value;
			} catch (Exception e) {
				return " = '" + value.replace("'", "''") + "'";
			}
		}
		else {
			return " = '" + value.replace("'", "''") + "'";
		}

	}

	@Override
	public void executeFormUpdateQuery(Form form, Map<String, FormField> editedLine) throws Exception {
		DatasourceJdbc jdbcSource = (DatasourceJdbc) form.getDatasource().getObject();

		VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);

		StringBuilder buf = new StringBuilder();
		buf.append("Update ");
		buf.append(form.getTableName());
		buf.append(" set ");
		boolean first = true;
		for (FormField f : form.getFields()) {
			if (first) {
				first = false;
			}
			else {
				buf.append(",");
			}
			buf.append(f.getColumnName());
			buf.append(" = ");
			String value = f.getValue();
			if (f.getType().equals(TypeField.CHECKBOX)) {
				if (Boolean.parseBoolean(value)) {
					buf.append("1");
				}
				else {
					buf.append("0");
				}
			}
			else {
				buf.append(getFieldValue(f, editedLine));
			}
		}

		buf.append(" where ");
		first = true;
		for (String col : editedLine.keySet()) {
			if (first) {
				first = false;
				buf.append(" (");
			}
			else {
				buf.append(" and (");
			}
			FormField f = editedLine.get(col);
			buf.append(f.getColumnName());

			String value = f.getValue();
			if (f.getType().equals(TypeField.CHECKBOX)) {
				buf.append(" = ");
				if (Boolean.parseBoolean(value)) {
					buf.append("1)");
				}
				else {
					if (value.equals("1)")) {
						buf.append("1)");
					}
					else {
						buf.append("0)");
					}
				}
			}
			else {
				buf.append(getWhereFieldValue(f) + ") ");
			}
		}

		String query = buf.toString();
		System.out.println(query);
		VanillaPreparedStatement stmt = jdbcConnection.prepareQuery(query);
		stmt.executeUpdate();
		stmt.close();

		ConnectionManager.getInstance().returnJdbcConnection(jdbcConnection);

	}

	@SuppressWarnings("unchecked")
	private List<Datasource> getDatasources(boolean lightweight) throws Exception {
		List<Datasource> res = openPreferenceDao.find("From Datasource");

		Collections.sort(res, new Comparator<Datasource>() {
			@Override
			public int compare(Datasource o1, Datasource o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		List<Datasource> resultats = new ArrayList<>();
		for (Datasource r : res) {
			if (r.getModel() != null && r.getModel() != "") {
				r.setObject((IDatasourceObject) xstream.fromXML(r.getModel()));
			}

			if (!lightweight) {
				List<Dataset> datasets = getDatasetByDatasource(r);
				r.setDatasets(datasets);
			}
			resultats.add(r);
		}

		return resultats;
	}

	@Override
	public List<Datasource> getDatasources() throws Exception {
		return getDatasources(false);
	}

	@Override
	public List<Datasource> getDatasources(List<DatasourceType> filterTypes) throws Exception {
		List<Datasource> datasources = getDatasources(true);
		List<Datasource> filtered = new ArrayList<Datasource>();

		for (Datasource datasource : datasources) {
			if (filterTypes.contains(datasource.getType())) {
				filtered.add(datasource);
			}
		}

		return filtered;
	}
}
