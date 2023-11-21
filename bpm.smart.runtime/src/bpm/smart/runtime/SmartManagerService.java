package bpm.smart.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math.stat.Frequency;
import org.apache.commons.math.stat.StatUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.rosuda.REngine.REXP;

import com.thoughtworks.xstream.XStream;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.connection.manager.connection.oda.VanillaOdaConnection;
import bpm.connection.manager.connection.oda.VanillaOdaQuery;
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
import bpm.smart.core.model.AirCube;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.MirrorCran;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.SmartAdmin;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.core.model.UsersProjectsShares;
import bpm.smart.core.model.workflow.activity.AirScriptActivity;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.ISmartWorkflowManager;
import bpm.smart.runtime.dao.SmartDao;
import bpm.smart.runtime.i18n.Labels;
import bpm.smart.runtime.r.RServer;
import bpm.smart.runtime.workflow.ActivityRunner;
import bpm.smart.runtime.workflow.ActivityRunnerFactory;
import bpm.smart.runtime.workflow.ResourceManager;
import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.DataSort;
import bpm.vanilla.platform.core.beans.DataWithCount;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceArchitect;
import bpm.vanilla.platform.core.beans.data.DatasourceCsv;
import bpm.vanilla.platform.core.beans.data.DatasourceCsvVanilla;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceHBase;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.CheckResult;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.beans.WorkflowModel;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.utils.SchedulerUtils;

public class SmartManagerService implements ISmartManager, ISmartWorkflowManager {

	private SmartManagerComponent component;
	private SmartDao dao;
	private IVanillaAPI api;
	private User user;

	private Locale locale;

	private XStream xstream = new XStream();

	public SmartManagerService(SmartManagerComponent component, User user) {
		this.component = component;
		this.dao = component.getSmartDao();
		this.api = component.getVanillaApi();
		this.user = user;
	}

	public SmartDao getDao() {
		return dao;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	@Override
	public List<AirProject> getAllAirProjects() throws Exception {
		List<AirProject> projects = dao.find("From AirProject");
		for (AirProject proj : projects) {
			proj.setUrlIcon(getAvatarIconUrl(proj.getAvatar()));
		}
		return projects;
	}

	@Override
	public List<AirProject> getVisibleProjects(int idUser) throws Exception {
		List<AirProject> result = new ArrayList<AirProject>();
		List<AirProject> userProjects = dao.find("From AirProject Where idUserCreator=" + idUser);
		for (AirProject proj : userProjects) {
			result.add(proj);
		}
		List<AirProject> sharedProjects = dao.find("From AirProject Where id IN (Select idProject From UsersProjectsShares Where idUser=" + idUser + ")");
		LOOP: for (AirProject proj : sharedProjects) {
			for (AirProject res : result) {
				if (res.getId() == proj.getId())
					continue LOOP;
			}
			result.add(proj);
		}
		List<AirProject> publicProjects = dao.find("From AirProject Where isPrivate=0");
		LOOP2: for (AirProject proj : publicProjects) {
			for (AirProject res : result) {
				if (res.getId() == proj.getId())
					continue LOOP2;
			}
			result.add(proj);
		}
		for (AirProject proj : result) {
			proj.setUrlIcon(getAvatarIconUrl(proj.getAvatar()));
			proj.setScripts(getRScriptsbyProject(proj.getId()));
		}
		List<AirProject> projects = new ArrayList<AirProject>(result);
		return projects;
	}

	@Override
	public void updateAirProject(AirProject project) throws Exception {
		if (project != null) {

			dao.update(project);
		}
		else {
			throw new Exception("Cannot update a null Project");
		}

	}

	@Override
	public int saveAirProject(AirProject project) throws Exception {
		if (project == null) {
			throw new Exception("Cannot save a null Project");

		}
		int id = (Integer) dao.save(project);
		return id;
	}

	@Override
	public void deleteAirProject(AirProject project) throws Exception {
		List<RScript> scripts = getRScriptsbyProject(project.getId());
		for (RScript script : scripts) {
			deleteRScript(script);
		}

		dao.delete(project);
	}

	@Override
	public List<RScript> getRScriptsbyProject(int id) {
		List<RScript> scriptList = (List<RScript>) dao.find("From RScript Where idProject=" + id);
		return scriptList;
	}

	@Override
	public String executeAirProject(AirProject project) throws Exception {
		// rServer.connect();
		// String result = "";//server.eval(project.getBox());
		//
		// rServer.deconnect();
		// return result;
		return null;
	}

	@Override
	public RScriptModel executeScriptR(RScriptModel box, List<Parameter> lovParams) throws Exception {
		RServer rServer = component.getClusterManager().getRserver(user);
		if (box.getUserREnv() == null) {
			box.setUserREnv(user.getLogin() + user.getId());
		}
		//		rServer.connect(box.getUserREnv());

		RScriptModel result = new RScriptModel();

		if(lovParams != null && !lovParams.isEmpty()){
			// traitement LOV //////////////////

			int[][] values = getLOVValuesSequence(lovParams);

			String scriptOriginal = new String(box.getScript());
			String script = "";

			String outputLogs = "";
			List<String> outputFiles = new ArrayList<>();
			List<Serializable> outputVars = new ArrayList<>();
			List<String> outputVarstoString = new ArrayList<>();
			boolean scriptError = false;
			for(int i=0; i<values.length; i++){
				script = new String(scriptOriginal);
				for(int j=0; j< lovParams.size(); j++){
					String val = lovParams.get(j).getListOfValues().getValues().get(values[i][j]);
					try {
						Double.parseDouble(val);
						script = script.replace(lovParams.get(j).getParameterName(), val);
					} catch(Exception e) {
						script = script.replace(lovParams.get(j).getParameterName(), "\""+val+"\"");
					}
				}
				box.setScript(script);

				RScriptModel res = rServer.scriptEval(box);   //////////////// EXECUTION /////////////////
				outputLogs += res.getOutputLog() + "</br>";
				if(res.getOutputFiles()!= null)
					outputFiles.addAll(Arrays.asList(res.getOutputFiles()));
				outputVars.addAll(res.getOutputVars());
				outputVarstoString.addAll(res.getOutputVarstoString());
				scriptError = res.isScriptError();

				if(res.isScriptError()) break;
			}
			////////////////////////////////////



			//			rServer.deconnect();

			//not necessary
			result.setDateVersion(box.getDateVersion());
			result.setIdScript(box.getIdScript());
			result.setInputs(box.getInputs());
			result.setInputVars(box.getInputVars());
			result.setNumVersion(box.getNumVersion());
			result.setOutputs(box.getOutputs());
			result.setScript(scriptOriginal);
			result.setUserREnv(box.getUserREnv());

			//necessary
			result.setScriptError(scriptError);
			result.setOutputFiles(outputFiles.toArray(new String[outputFiles.size()]));
			result.setOutputLog(outputLogs);
			result.setOutputVars(outputVars);
			result.setOutputVarstoString(outputVarstoString);
		} else {
			result = rServer.scriptEval(box);
		}



		return result;
	}

	public RScriptModel executeScriptR(RScriptModel box) throws Exception {
		return executeScriptR(box, null);
	}

	@Override
	public List<MirrorCran> loadMirrors() throws Exception {
		List<MirrorCran> mirrors = dao.find("From MirrorCran");

		return mirrors;
	}

	public List<MirrorCran> getMirrorbyId(int id) throws Exception {
		List<MirrorCran> mirror = dao.find("From MirrorCran Where id=" + id);

		return mirror;
	}

	@Override
	public List<SmartAdmin> getSmartAdminbyUser(int idUser) throws Exception {
		List<SmartAdmin> adminList = (List<SmartAdmin>) dao.find("From SmartAdmin Where idUser=" + idUser);

		for (SmartAdmin admin : adminList) {
			if (admin.getIdMirror() == 0) {
				admin.setMirror(null);
			}
			else {
				MirrorCran mirror = getMirrorbyId(admin.getIdMirror()).get(0);
				admin.setMirror(mirror);
			}
		}

		return adminList;
	}

	@Override
	public void updateSmartAdmin(SmartAdmin admin) throws Exception {
		if (admin != null) {

			dao.update(admin);
		}
		else {
			throw new Exception("Cannot update a null Admin Configuration");
		}

	}

	@Override
	public void saveSmartAdmin(SmartAdmin admin) throws Exception {
		if (admin == null) {
			throw new Exception("Cannot save a null Admin Configuration");

		}
		dao.save(admin);

	}

	@Override
	public void deleteSmartAdmin(SmartAdmin admin) throws Exception {
		dao.delete(admin);
	}

	@Override
	public List<StatDataColumn> createStatsDataset(Dataset dts) throws Exception {

		List<StatDataColumn> stats = new ArrayList<StatDataColumn>();

		Datasource source = api.getVanillaPreferencesManager().getDatasourceById(dts.getDatasourceId());
		List<DataColumn> columns = api.getVanillaPreferencesManager().getDataColumnsbyDataset(dts);
		//		if (source.getType().equals(DatasourceType.R)) {
		stats.addAll(calculateRStats(columns, dts));
		//		}
		//		else if (source.getType().equals(DatasourceType.FMDT)) {
		//			stats.addAll(calculateFmdtStats(columns, dts, source));
		//		}
		//		else if (source.getType().equals(DatasourceType.CSVVanilla)) {
		//			stats.addAll(calculateCsvVanillaStats(columns, dts, source));
		//		}
		//		else if (source.getType().equals(DatasourceType.HBase)) {
		//			stats.addAll(calculateHBaseStats(columns, dts, source));
		//		}
		//		else if (source.getType().equals(DatasourceType.CSV)) {
		//			stats.addAll(calculateCsvStats(columns, dts, source));
		//		}
		//		else if (source.getType().equals(DatasourceType.SOCIAL)) {
		//			stats.addAll(calculateRStats(columns, dts));
		//		}
		//		else {
		//			stats.addAll(calculateStats(columns, dts, source));
		//		}

		saveStatsbyDataset(stats, dts);
		return stats;
	}

	@Override
	public List<StatDataColumn> loadStatsDataset(List<Dataset> datasets) throws Exception {

		List<StatDataColumn> stats = new ArrayList<StatDataColumn>();
		for (Dataset dts : datasets) {
			stats.addAll(getStatsbyDataset(dts));
		}
		return stats;
	}

	private List<StatDataColumn> calculateCsvVanillaStats(List<DataColumn> columns, Dataset dts, Datasource source) {
		List<StatDataColumn> satslist = new ArrayList<StatDataColumn>();
		try {
			DatasourceCsvVanilla csvSource = (DatasourceCsvVanilla) source.getObject();

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
			query.prepareQuery(dts.getRequest());
			IResultSet result = query.executeQuery();

			StatDataColumn datastat;
			List[] tabs = new ArrayList[columns.size()];
			boolean[] aredigits = new boolean[columns.size()];
			int i = 0;
			while (result.next()) {
				int j = 0;
				for (DataColumn col : columns) {
					String colname = col.getColumnLabel();

					if (i == 0) {
						String c = ((String) result.getString(colname));
						boolean isDigit = false;
						try {
							Double.parseDouble(c);
							isDigit = true;
						} catch (Exception e) {
						}

						aredigits[j] = isDigit;
						tabs[j] = new ArrayList<>();
					}

					if (aredigits[j]) {
						try {
							tabs[j].add(result.getDouble(colname));
						} catch (Exception e) {
							tabs[j].add(result.getString(colname));
							aredigits[j] = false;
						}
					}
					else {
						tabs[j].add(result.getString(colname));
					}
					j++;
				}
				i++;
			}

			DecimalFormat numberFormat = new DecimalFormat("#.###");

			int j = 0;
			for (DataColumn col : columns) {
				String min, max, moy, dev;
				List<String> rep = new ArrayList<String>();
				if (aredigits[j]) {
					double[] dtab = new double[tabs[j].size()];
					for (i = 0; i < tabs[j].size(); i++) {
						dtab[i] = (double) tabs[j].get(i);
					}
					min = numberFormat.format(StatUtils.min(dtab)) + "";
					max = numberFormat.format(StatUtils.max(dtab)) + "";
					moy = numberFormat.format(StatUtils.mean(dtab)) + "";
					dev = numberFormat.format(Math.sqrt(StatUtils.variance(dtab))) + "";
					rep = Arrays.asList("NA");
				}
				else {
					Set<String> stab = new HashSet<String>();
					Frequency f = new Frequency();
					for (Object data : tabs[j]) {
						if (data == null) {
							f.addValue("null");
							stab.add("null");
						}
						else {
							f.addValue(String.valueOf(data));
							stab.add(String.valueOf(data));
						}
					}
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					for (String datacol : stab) {
						rep.add(datacol + ":" + f.getCount(datacol));
					}

				}
				datastat = new StatDataColumn(col.getId(), col.getColumnLabel(), min, max, moy, dev, rep);
				satslist.add(datastat);
				j++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return satslist;
	}

	private List<StatDataColumn> calculateCsvStats(List<DataColumn> columns, Dataset dataset, Datasource source) {
		RScriptModel box = new RScriptModel();
		User user = getUser();
		String userREnv = user.getLogin() + user.getId();
		List<StatDataColumn> satslist = new ArrayList<StatDataColumn>();

		try {
			RServer rServer = component.getClusterManager().getRserver(user);

			DatasourceCsv csv = (DatasourceCsv) dataset.getDatasource().getObject();
			String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
			Path path = Paths.get(basePath, csv.getFilePath());
			byte[] resultStream;

			resultStream = Files.readAllBytes(path);
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
			//			rServer.connect(userREnv);
			if (path.toString().substring(path.toString().lastIndexOf(".") + 1).equals("csv")) {
				nameTempFile = rServer.loadCsvFile(resultStream, hasHeader, sep, userREnv);
				//				rServer.connect(userREnv);
				box = rServer.addDatasetTempFile(request, dataset.getName(), nameTempFile, userREnv);
			}
			else {
				nameTempFile = rServer.loadExcelFile(resultStream, hasHeader, true, dataset.getMetacolumns(), userREnv);
				//				rServer.connect(userREnv);
				box = rServer.addDatasetTempFile(request, dataset.getName(), nameTempFile, userREnv);
			}
			//			rServer.deconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (DataColumn col : columns) {
			String script = // dataset.getRequest() +"\n"//"library("+
					// dataset.getRequest() +")\n"
					"colclass <- class(" + dataset.getName() + "[['" + col.getColumnName() + "']])\n" + "if(colclass=='numeric' | colclass=='integer'){\n" + "min <- min(" + dataset.getName() + "[['" + col.getColumnName() + "']])\n" + "max <- max(" + dataset.getName() + "[['" + col.getColumnName() + "']])\n" + "ave <- round(mean(" + dataset.getName() + "[['" + col.getColumnName() + "']]),4)\n" + "dev <- round(sd(" + dataset.getName() + "[['" + col.getColumnName() + "']]),4)\n" + "line <- c(paste(min,max,ave,dev,sep=';'))\n" + "}\n" + "if(colclass=='factor' | colclass=='character'){\n" + "line <- ''\n" + "for( stat in names(summary(" + dataset.getName() + "[['" + col.getColumnName() + "']]))) {\n" + "if(stat!=''){\n" + "line <- c(paste(line, c(paste(stat, summary(" + dataset.getName() + "[['" + col.getColumnName() + "']])[[stat]],sep=':')), sep=';'))\n" + "}\n" + "}\n" + "line <- substr(line,2,nchar(line))\n" + "}\n" + "script_result <- c(paste('" + col.getColumnName() + "', colclass, line, sep='_!_'))";

			box.setScript(script);
			box.setUserREnv(userREnv);
			box.setOutputs("script_result".split(" "));

			RScriptModel result;
			try {
				result = executeScriptR(box, null);

				StatDataColumn datastat;

				List<String> it = new ArrayList<>();
				try {
					it = (List<String>) result.getOutputVars().get(0);
				} catch (Exception e) {
					it = Arrays.asList((String[])result.getOutputVars().get(0));
				}
				String rsM = it.get(0);

				String name = rsM.split("_!_")[0];
				String RType = rsM.split("_!_")[1];
				int SQLType = 0;
				if (RType.equals("numeric") || RType.equals("integer")) {
					SQLType = java.sql.Types.DOUBLE;
				}
				else if (RType.equals("character") || RType.equals("factor")) {
					SQLType = java.sql.Types.VARCHAR;
				}
				else if (RType.equals("logical")) {
					SQLType = java.sql.Types.BOOLEAN;
				}
				else {
					SQLType = java.sql.Types.OTHER;
				}

				String statline = rsM.split("_!_")[2];
				String[] stats = statline.split(";");
				String min, max, moy, dev;
				List<String> rep;
				DecimalFormat numberFormat = new DecimalFormat("#.###");
				switch (SQLType) {
				case java.sql.Types.DOUBLE:
					try {
						min = numberFormat.format(Double.parseDouble(stats[0]));
					} catch (Exception e) {
						min = "NA";
					}
					try {
						max = numberFormat.format(Double.parseDouble(stats[1]));
					} catch (Exception e) {
						max = "NA";
					}
					try {
						moy = numberFormat.format(Double.parseDouble(stats[2]));
					} catch (Exception e) {
						moy = "NA";
					}
					try {
						dev = numberFormat.format(Double.parseDouble(stats[3]));
					} catch (Exception e) {
						dev = "NA";
					}
					rep = Arrays.asList("NA");
					break;
				case java.sql.Types.VARCHAR:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList(stats);
					break;
				case java.sql.Types.BOOLEAN:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList("NA");
					break;
				case java.sql.Types.OTHER:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList("NA");
					break;
				default:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList("NA");
					break;
				}
				datastat = new StatDataColumn(col.getId(), name, min, max, moy, dev, rep);
				satslist.add(datastat);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return satslist;
	}

	private List<StatDataColumn> calculateFmdtStats(List<DataColumn> columns, Dataset dataset, Datasource source) {
		List<StatDataColumn> satslist = new ArrayList<StatDataColumn>();
		try {
			DatasourceFmdt fmdtDatasource = (DatasourceFmdt) source.getObject();
			Group group;
			Repository repo;
			IRepositoryApi sock;
			if(fmdtDatasource.isDefaultUrl()){
				group = component.getVanillaApi().getVanillaSecurityManager().getGroupById(fmdtDatasource.getGroupId());
				repo = component.getVanillaApi().getVanillaRepositoryManager().getRepositoryById(fmdtDatasource.getRepositoryId());

				sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(component.getVanillaApi().getVanillaUrl(), fmdtDatasource.getUser(), fmdtDatasource.getPassword()), group, repo));
			} else {
				RemoteVanillaPlatform remote = new RemoteVanillaPlatform(fmdtDatasource.getUrl(), fmdtDatasource.getUser(), fmdtDatasource.getPassword());

				group = remote.getVanillaSecurityManager().getGroupById(fmdtDatasource.getGroupId());
				repo = remote.getVanillaRepositoryManager().getRepositoryById(fmdtDatasource.getRepositoryId());

				sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(fmdtDatasource.getUrl(), fmdtDatasource.getUser(), fmdtDatasource.getPassword()), group, repo));
			}



			RepositoryItem item = sock.getRepositoryService().getDirectoryItem(fmdtDatasource.getItemId());
			String xml = sock.getRepositoryService().loadModel(item);

			List<IBusinessModel> bModels = MetaDataReader.read(group.getName(), IOUtils.toInputStream(xml, "UTF-8"), sock, false);

			IBusinessPackage pack = null;
			for (IBusinessModel model : bModels) {
				if (model.getName().equals(fmdtDatasource.getBusinessModel())) {
					for (IBusinessPackage p : model.getBusinessPackages(group.getName())) {
						if (p.getName().equals(fmdtDatasource.getBusinessPackage())) {
							pack = p;
							break;
						}
					}
				}
			}

			SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(dataset.getRequest(), "UTF-8"), group.getName(), pack);

			QuerySql sqlquery = dig.getModel();
			EffectiveQuery query = SqlQueryGenerator.getQuery(null, sock.getContext().getVanillaContext(), pack, sqlquery, group.getName(), false, new HashMap<Prompt, List<String>>());

			List<List<String>> result = pack.executeQuery(0, "Default", query.getGeneratedQuery().replace("`", "\""));

			StatDataColumn datastat;
			List[] tabs = new ArrayList[columns.size()];
			boolean[] aredigits = new boolean[columns.size()];
			int i = 0;
			for (List<String> line : result) {
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
						tabs[j] = new ArrayList<>();
					}

					if (aredigits[j]) {
						tabs[j].add(Double.parseDouble(line.get(j)));
					}
					else {
						tabs[j].add(line.get(j));
					}
					j++;
				}
				i++;
			}

			DecimalFormat numberFormat = new DecimalFormat("#.###");

			int j = 0;
			for (DataColumn col : columns) {
				String min, max, moy, dev;
				List<String> rep = new ArrayList<String>();
				if (aredigits[j]) {
					double[] dtab = new double[tabs[j].size()];
					for (i = 0; i < tabs[j].size(); i++) {
						dtab[i] = (double) tabs[j].get(i);
					}
					min = numberFormat.format(StatUtils.min(dtab)) + "";
					max = numberFormat.format(StatUtils.max(dtab)) + "";
					moy = numberFormat.format(StatUtils.mean(dtab)) + "";
					dev = numberFormat.format(Math.sqrt(StatUtils.variance(dtab))) + "";
					rep = Arrays.asList("NA");
				}
				else {
					Set<String> stab = new HashSet<String>();
					Frequency f = new Frequency();
					for (Object data : tabs[j]) {
						f.addValue((String) data);
						stab.add((String) data);
					}
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					for (String datacol : stab) {
						rep.add(datacol + ":" + f.getCount(datacol));
					}

				}
				datastat = new StatDataColumn(col.getId(), col.getColumnLabel(), min, max, moy, dev, rep);
				satslist.add(datastat);
				j++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return satslist;
	}

	@Override
	public List<StatDataColumn> calculateRStats(List<DataColumn> columns, Dataset dataset) throws Exception {
		List<StatDataColumn> satslist = new ArrayList<StatDataColumn>();
		User user = getUser();
		String userREnv = user.getLogin() + user.getId();
		RScriptModel box = new RScriptModel();
		for (DataColumn col : columns) {
			String script = //dataset.getRequest() + "\n"// "library("+ //
					// dataset.getRequest()
					// // +")\n"
					"colclass <- class(" + dataset.getName() + "[['" + col.getColumnName() + "']])\n" 
					+ "if(colclass=='numeric' | colclass=='integer'){\n" + "min <- min(" + dataset.getName() + "[['" + col.getColumnName() + "']])\n" 
					+ "max <- max(" + dataset.getName() + "[['" + col.getColumnName() + "']])\n" 
					+ "ave <- round(mean(" + dataset.getName() + "[['" + col.getColumnName() + "']]),4)\n" 
					+ "dev <- round(sd(" + dataset.getName() + "[['" + col.getColumnName() + "']]),4)\n" 
					+ "line <- c(paste(min,max,ave,dev,sep=';'))\n" + "}\n" 
					+ "if(colclass=='factor' | colclass=='character'){\n" 
					+ "line <- ''\n" 
					+ "for( stat in names(summary(" + dataset.getName() + "[['" + col.getColumnName() + "']]))) {\n" 
					+ "line <- c(paste(line, c(paste(stat, if(stat == '') data.frame(summary(" + dataset.getName() + "[['" + col.getColumnName() + "']]))[1,] else summary(" + dataset.getName() + "[['" + col.getColumnName() + "']])[[stat]],sep=':')), sep=';'))\n" 
					+ "}\n" 
					+ "line <- substr(line,2,nchar(line))\n" 
					+ "}\n" 
					+ "script_result <- c(paste('" + col.getColumnName() + "', colclass, line, sep='_!_'))";

			box.setScript(script);
			box.setUserREnv(userREnv);
			box.setOutputs("script_result".split(" "));

			RScriptModel result = executeScriptR(box);

			StatDataColumn datastat;
			List<String> it = new ArrayList<>();
			try {
				it = (List<String>) result.getOutputVars().get(0);
			} catch (Exception e) {
				try {
					it = Arrays.asList((String[])result.getOutputVars().get(0));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if(result.getOutputVars().size() >0 && it.size() > 0){
				String rsM = it.get(0);

				String name = rsM.split("_!_")[0];
				String RType = rsM.split("_!_")[1];
				int SQLType = 0;
				if (RType.equals("numeric") || RType.equals("integer")) {
					SQLType = java.sql.Types.DOUBLE;
				}
				else if (RType.equals("character") || RType.equals("factor")) {
					SQLType = java.sql.Types.VARCHAR;
				}
				else if (RType.equals("logical")) {
					SQLType = java.sql.Types.BOOLEAN;
				}
				else {
					SQLType = java.sql.Types.OTHER;
				}

				String statline = rsM.split("_!_")[2];
				String[] stats = statline.split(";");
				String min, max, moy, dev;
				List<String> rep;
				DecimalFormat numberFormat = new DecimalFormat("#.###");
				switch (SQLType) {
				case java.sql.Types.DOUBLE:
					try {
						min = numberFormat.format(Double.parseDouble(stats[0]));
					} catch (Exception e) {
						min = "NA";
					}
					try {
						max = numberFormat.format(Double.parseDouble(stats[1]));
					} catch (Exception e) {
						max = "NA";
					}
					try {
						moy = numberFormat.format(Double.parseDouble(stats[2]));
					} catch (Exception e) {
						moy = "NA";
					}
					try {
						dev = numberFormat.format(Double.parseDouble(stats[3]));
					} catch (Exception e) {
						dev = "NA";
					}
					rep = Arrays.asList("NA");
					break;
				case java.sql.Types.VARCHAR:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList(stats);
					break;
				case java.sql.Types.BOOLEAN:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList("NA");
					break;
				case java.sql.Types.OTHER:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList("NA");
					break;
				default:
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					rep = Arrays.asList("NA");
					break;
				}
				datastat = new StatDataColumn(col.getId(), name, min, max, moy, dev, rep);
				satslist.add(datastat);
			}
		}

		return satslist;
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

	private List<StatDataColumn> calculateHBaseStats(List<DataColumn> columns, Dataset dts, Datasource source) {
		List<StatDataColumn> satslist = new ArrayList<StatDataColumn>();
		try {
			DatasourceHBase hbase = (DatasourceHBase) source.getObject();

			String configFile = makeConfigurationFileUrl(hbase.getUrl(), hbase.getPort());

			Properties publicProperties = new Properties();
			publicProperties.put("configfile", configFile);

			Properties privateProperties = new Properties();
			String datasourceId = "bpm.nosql.oda.runtime.hbase";

			VanillaOdaConnection connection = ConnectionManager.getInstance().getOdaConnection(publicProperties, privateProperties, datasourceId);
			VanillaOdaQuery query = connection.newQuery(null);
			query.prepareQuery(dts.getRequest());
			IResultSet result = query.executeQuery();

			StatDataColumn datastat;
			List[] tabs = new ArrayList[columns.size()];
			boolean[] aredigits = new boolean[columns.size()];
			int i = 0;
			while (result.next()) {
				int j = 0;
				for (DataColumn col : columns) {
					String colname = col.getColumnLabel();

					if (i == 0) {
						String c = ((String) result.getString(colname));
						boolean isDigit = false;
						try {
							Double.parseDouble(c);
							isDigit = true;
						} catch (Exception e) {
						}

						aredigits[j] = isDigit;
						tabs[j] = new ArrayList<>();
					}

					if (aredigits[j]) {
						try {
							tabs[j].add(result.getDouble(colname));
						} catch (Exception e) {
							tabs[j].add(result.getString(colname));
							aredigits[j] = false;
						}
					}
					else {
						tabs[j].add(result.getString(colname));
					}
					j++;
				}
				i++;
			}

			DecimalFormat numberFormat = new DecimalFormat("#.###");

			int j = 0;
			for (DataColumn col : columns) {
				String min, max, moy, dev;
				List<String> rep = new ArrayList<String>();
				if (aredigits[j]) {
					double[] dtab = new double[tabs[j].size()];
					for (i = 0; i < tabs[j].size(); i++) {
						dtab[i] = (double) tabs[j].get(i);
					}
					min = numberFormat.format(StatUtils.min(dtab)) + "";
					max = numberFormat.format(StatUtils.max(dtab)) + "";
					moy = numberFormat.format(StatUtils.mean(dtab)) + "";
					dev = numberFormat.format(Math.sqrt(StatUtils.variance(dtab))) + "";
					rep = Arrays.asList("NA");
				}
				else {
					Set<String> stab = new HashSet<String>();
					Frequency f = new Frequency();
					for (Object data : tabs[j]) {
						if (data == null) {
							f.addValue("null");
							stab.add("null");
						}
						else {
							f.addValue(String.valueOf(data));
							stab.add(String.valueOf(data));
						}
					}
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					for (String datacol : stab) {
						rep.add(datacol + ":" + f.getCount(datacol));
					}

				}
				datastat = new StatDataColumn(col.getId(), col.getColumnLabel(), min, max, moy, dev, rep);
				satslist.add(datastat);
				j++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return satslist;
	}

	private List<StatDataColumn> calculateStats(List<DataColumn> columns, Dataset dataset, Datasource source) throws Exception {
		List<StatDataColumn> satslist = new ArrayList<StatDataColumn>();
		try {
			DatasourceJdbc jdbcSource = (DatasourceJdbc) source.getObject();
			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
			VanillaPreparedStatement rs = connection.prepareQuery(dataset.getRequest());
			ResultSet result = rs.executeQuery(dataset.getRequest());
			StatDataColumn datastat;
			List[] tabs = new ArrayList[columns.size()];
			boolean[] aredigits = new boolean[columns.size()];
			int i = 0;
			while (result.next()) {
				int j = 0;
				for (DataColumn col : columns) {
					String colname = col.getColumnLabel();

					if (i == 0) {
						String c = ((String) result.getString(colname));
						boolean isDigit = false;
						try {
							Integer.parseInt(c);
							isDigit = true;
						} catch (Exception e) {
						}

						aredigits[j] = isDigit;
						tabs[j] = new ArrayList<>();
					}

					if (aredigits[j]) {
						try {
							tabs[j].add(result.getDouble(colname));
						} catch (Exception e) {
							tabs[j].add(result.getString(colname));
							aredigits[j] = false;
						}
					}
					else {
						tabs[j].add(result.getString(colname));
					}
					j++;
				}
				i++;
			}

			DecimalFormat numberFormat = new DecimalFormat("#.###");

			int j = 0;
			for (DataColumn col : columns) {
				String min, max, moy, dev;
				List<String> rep = new ArrayList<String>();
				if (aredigits[j]) {
					double[] dtab = new double[tabs[j].size()];
					for (i = 0; i < tabs[j].size(); i++) {
						dtab[i] = (double) tabs[j].get(i);
					}
					min = numberFormat.format(StatUtils.min(dtab)) + "";
					max = numberFormat.format(StatUtils.max(dtab)) + "";
					moy = numberFormat.format(StatUtils.mean(dtab)) + "";
					dev = numberFormat.format(Math.sqrt(StatUtils.variance(dtab))) + "";
					rep = Arrays.asList("NA");
				}
				else {
					Set<String> stab = new HashSet<String>();
					Frequency f = new Frequency();
					for (Object data : tabs[j]) {
						if (data == null) {
							f.addValue("null");
							stab.add("null");
						}
						else {
							f.addValue(String.valueOf(data));
							stab.add(String.valueOf(data));
						}
					}
					min = "NA";
					max = "NA";
					moy = "NA";
					dev = "NA";
					for (String datacol : stab) {
						rep.add(datacol + ":" + f.getCount(datacol));
					}

				}
				datastat = new StatDataColumn(col.getId(), col.getColumnLabel(), min, max, moy, dev, rep);
				satslist.add(datastat);
				j++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return satslist;
	}

	@Override
	public String addAvatarIcon(String name, InputStream extDocStream, String format, boolean addDate) throws Exception {
		long time = new Date().getTime();
		String fName = name;
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String relPath = "";
		if (addDate) {
			relPath = "/air_project_avatars/" + time + "_" + fName;
		}
		else {
			relPath = "/air_project_avatars/" + fName;
		}

		try {
			File file = new File(basePath + "/" + relPath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			IOWriter.write(extDocStream, fileOutputStream, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "success;" + relPath;
	}

	@Override
	public InputStream importIcon(String name) throws Exception {

		return null;
	}

	@Override
	public String getAvatarIconUrl(String filename) throws Exception {
		if (filename == null || filename.equals("")) {
			return "";
		}

		String path = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);

		File f = new File(path + "/" + filename);

		if (f.exists()) {
			return path + "/" + filename;
		}

		return "";
	}

	@Override
	public int saveRScript(RScript script) throws Exception {
		if (script == null) {
			throw new Exception("Cannot save a null Script");
		}
		int id = (Integer) dao.save(script);
		return id;
	}

	@Override
	public void updateRScript(RScript script) throws Exception {
		if (script == null) {
			throw new Exception("Cannot update a null Script");
		}
		dao.update(script);
	}

	@Override
	public void deleteRScript(RScript script) throws Exception {
		List<RScriptModel> scripts = getRScriptModelsbyScript(script.getId());
		for (RScriptModel scriptmodel : scripts) {
			deleteRScriptModel(scriptmodel);
		}

		dao.delete(script);
	}

	@Override
	public int saveRScriptModel(RScriptModel scriptmodel) throws Exception {
		if (scriptmodel == null) {
			throw new Exception("Cannot save a null ScriptModel");
		}
		int id = (Integer) dao.save(scriptmodel);
		return id;
	}

	@Override
	public void updateRScriptModel(RScriptModel scriptmodel) throws Exception {
		if (scriptmodel == null) {
			throw new Exception("Cannot update a null ScriptModel");
		}
		dao.update(scriptmodel);

	}

	@Override
	public void deleteRScriptModel(RScriptModel scriptmodel) throws Exception {

		dao.delete(scriptmodel);
	}

	@Override
	public List<RScriptModel> getRScriptModelsbyScript(int id) {
		List<RScriptModel> modelList = (List<RScriptModel>) dao.find("From RScriptModel Where idScript=" + id, 10);
		return modelList;
	}

	@Override
	public List<RScriptModel> getLastScriptModels(List<RScript> scripts) throws Exception {
		List<RScriptModel> result = new ArrayList<RScriptModel>();
		for (RScript script : scripts) {
			if(script.getId() != 0){
				RScriptModel model = getLastModelbyScript(script.getId());
				result.add(model);
			}
		}

		return result;
	}

	@Override
	public RScriptModel getLastModelbyScript(int idScript) throws Exception {
		List<RScriptModel> result = (List<RScriptModel>) dao.find("From RScriptModel Where idScript=" + idScript + " Order by numVersion DESC");

		return result.get(0);
	}

	@Override
	public List<RScript> getAllScripts() throws Exception {
		List<RScript> result = (List<RScript>) dao.find("From RScript");
		return result;
	}

	@Override
	public void checkInScript(RScript script) throws Exception {
		if (script.getId() != 0) {
			updateRScript(script);
		}
		else {
			saveRScript(script);
		}

	}

	@Override
	public String checkOutScript(RScript script) throws Exception {
		RScript registerScript = ((List<RScript>) dao.find("From RScript Where id=" + script.getId())).get(0);
		if (!registerScript.isFree()) {
			return registerScript.getHoldingUsername();
		}
		else {
			registerScript.setFree(false);
			registerScript.setHoldingUsername(script.getHoldingUsername());
			return "";
		}
	}

	public void addTempFile(byte[] bytes, String filename) {
		ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(bytes));
		InputStream extDocStream = (InputStream) bis;
		try {
			File file = new File(filename);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			IOWriter.write(extDocStream, fileOutputStream, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void shareProject(int id, List<User> users) throws Exception {
		List<UsersProjectsShares> ups = getSharedProjectsUsersbyIdProject(id);
		for (UsersProjectsShares share : ups) {
			deleteSharedProjectsUsers(share);
		}

		for (User user : users) {
			UsersProjectsShares share = new UsersProjectsShares(user.getId(), id);
			saveSharedProjectsUsers(share);
		}

	}

	@Override
	public List<UsersProjectsShares> getSharedProjectsUsersbyIdProject(int id) throws Exception {
		List<UsersProjectsShares> ups = (List<UsersProjectsShares>) dao.find("From UsersProjectsShares Where idProject=" + id);
		return ups;
	}

	public void deleteSharedProjectsUsers(UsersProjectsShares ups) throws Exception {
		dao.delete(ups);
	}

	public void saveSharedProjectsUsers(UsersProjectsShares ups) throws Exception {
		dao.save(ups);
	}

	@Override
	public AirProject getAirProjectbyId(int idProject) throws Exception {
		List<AirProject> projects = (List<AirProject>) dao.find("From AirProject Where id=" + idProject);
		return projects.get(0);
	}

	@Override
	public InputStream zipAirProject(int idProject, boolean allVersions) throws Exception {
		AirProject project = getAirProjectbyId(idProject);
		List<RScript> scripts = getRScriptsbyProject(idProject);
		ByteArrayInputStream bis = null;
		try {
			// FileOutputStream fos = new FileOutputStream(project.getName()+
			// ".vanillaair");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(bos);

			/* descriptor metadonnée project */
			String xml = new XStream().toXML(project);
			String descriptorName = "descriptor.xml";
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(xml.getBytes());
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			addToZipFile(descriptorName, "", in, zos);

			/* icone */
			String url = getAvatarIconUrl(project.getAvatar());
			if (!url.equals("")) {
				FileInputStream iconStream = new FileInputStream(url);
				addToZipFile(project.getAvatar().split("air_project_avatars/")[1], "", iconStream, zos);
			}

			for (RScript script : scripts) {
				if (allVersions) {
					List<RScriptModel> models = getRScriptModelsbyScript(script.getId());
					for (RScriptModel model : models) {
						/* descriptor metadonnée model */
						xml = new XStream().toXML(model);
						descriptorName = script.getName() + model.getNumVersion() + ".xml";
						out = new ByteArrayOutputStream();
						out.write(xml.getBytes());
						in = new ByteArrayInputStream(out.toByteArray());
						addToZipFile(descriptorName, script.getName(), in, zos);

						String fileName = "";
						if(script.getScriptType().equals("R")){
							fileName = script.getName() + model.getNumVersion() + ".R";
						} else {
							fileName = script.getName() + model.getNumVersion() + ".Rmd";
						}
						out = new ByteArrayOutputStream();
						out.write(model.getScript().getBytes());
						in = new ByteArrayInputStream(out.toByteArray());
						addToZipFile(fileName, script.getName(), in, zos);
					}
				}
				else {
					RScriptModel model = getLastModelbyScript(script.getId());
					/* descriptor metadonnée model */
					xml = new XStream().toXML(model);
					descriptorName = script.getName() + model.getNumVersion() + ".xml";
					out = new ByteArrayOutputStream();
					out.write(xml.getBytes());
					in = new ByteArrayInputStream(out.toByteArray());
					addToZipFile(descriptorName, script.getName(), in, zos);

					String fileName = "";
					if(script.getScriptType().equals("R")){
						fileName = script.getName() + model.getNumVersion() + ".R";
					} else {
						fileName = script.getName() + model.getNumVersion() + ".Rmd";
					}
					out = new ByteArrayOutputStream();
					out.write(model.getScript().getBytes());
					in = new ByteArrayInputStream(out.toByteArray());
					addToZipFile(fileName, script.getName(), in, zos);
				}
			}

			zos.close();
			bos.close();
			bis = new ByteArrayInputStream(bos.toByteArray());

			// String basePath =
			// ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
			// String relPath = "/air_project_avatars/" + project.getName() +
			// ".vanillaair";
			// File file = new File(basePath + "/" + relPath);
			// FileOutputStream fileOutputStream = new FileOutputStream(file);
			// IOWriter.write(bis, fileOutputStream, true, true);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bis;

	}

	public void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	public void addToZipFile(String fileName, String path, InputStream input, ZipOutputStream zos) throws FileNotFoundException, IOException {

		System.out.println("Writing '" + fileName + "' to zip file");

		// File file = new File(fileName);
		// FileInputStream fis = new FileInputStream(file);
		if (path.equals("")) {
			ZipEntry zipEntry = new ZipEntry(fileName);
			zos.putNextEntry(zipEntry);
		}
		else {
			ZipEntry zipEntry = new ZipEntry(path + "/" + fileName);
			zos.putNextEntry(zipEntry);
		}

		byte[] bytes = new byte[1024];
		int length;
		while ((length = input.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		// fis.close();
	}

	@Override
	public RScriptModel addDatasettoR(Dataset dataset) throws Exception {
		RServer rServer = component.getClusterManager().getRserver(user);
		User user = getUser();
		String userEnv = user.getLogin() + user.getId();
		RScriptModel scriptResult = new RScriptModel();
		if (!dataset.getIsRPackaged()) {

			byte[] resultStream = {};
			StringBuffer sb = new StringBuffer();
			List<String> columnsName = new ArrayList<String>();
			String row = "";
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
					row += csvMeta.getColumnLabel(i) + ";";
				}
				sb.append(row.substring(0, row.length() - 1) + "\n");
				aredigits = new boolean[columnsName.size()];
				i = 0;
				while (csvResult.next()) {
					int j = 0;
					row = "";
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
								row += csvResult.getDouble(colname) + ";";
							} catch (Exception e) {
								row += csvResult.getString(colname) + ";";
								aredigits[j] = false;
							}
						}
						else {
							row += csvResult.getString(colname) + ";";
						}
						j++;
					}
					sb.append(row.substring(0, row.length() - 1) + "\n");
					i++;
				}
				resultStream = sb.toString().getBytes();
				break;
			case FMDT:
				DatasourceFmdt fmdtSource = (DatasourceFmdt) dataset.getDatasource().getObject();

				Group group;
				Repository repo;
				IRepositoryApi sock;
				if(fmdtSource.isDefaultUrl()){
					group = component.getVanillaApi().getVanillaSecurityManager().getGroupById(fmdtSource.getGroupId());
					repo = component.getVanillaApi().getVanillaRepositoryManager().getRepositoryById(fmdtSource.getRepositoryId());

					sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(component.getVanillaApi().getVanillaUrl(), fmdtSource.getUser(), fmdtSource.getPassword()), group, repo));
				} else {
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

				List<List<String>> fmdtResult = pack.executeQuery(0, "Default", equery.getGeneratedQuery().replace("`", "\""));

				//				List<DataColumn> columns = api.getVanillaPreferencesManager().getDataColumnsbyDataset(dataset);
				List<DataColumn> columns = dataset.getMetacolumns();
				for (DataColumn col : columns) {
					row += col.getColumnLabel() + ";";
				}
				sb.append(row.substring(0, row.length() - 1) + "\n");
				aredigits = new boolean[columns.size()];
				i = 0;
				for (List<String> line : fmdtResult) {
					int j = 0;
					row = "";
					for (DataColumn col : columns) {
						// String colname = col.getColumnLabel();

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

						if (aredigits[j]) {
							row += Double.parseDouble(line.get(j)) + ";";
						}
						else {
							row += line.get(j) + ";";
						}
						j++;
					}
					sb.append(row.substring(0, row.length() - 1) + "\n");
					i++;
				}

				resultStream = sb.toString().getBytes();
				break;
			case JDBC:
				DatasourceJdbc jdbcSource = (DatasourceJdbc) dataset.getDatasource().getObject();

				VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
				VanillaPreparedStatement rs = jdbcConnection.prepareQuery(dataset.getRequest());
				ResultSet jdbcResult = rs.executeQuery(dataset.getRequest());
				ResultSetMetaData jdbcMeta = jdbcResult.getMetaData();

				for (i = 1; i <= jdbcMeta.getColumnCount(); i++) {
					columnsName.add(jdbcMeta.getColumnLabel(i));
					row += jdbcMeta.getColumnLabel(i) + ";";
				}
				// sb.append(row.substring(0, row.length() - 1) + "\n");
				byte[] newtab = (row.substring(0, row.length() - 1) + "\n").toString().getBytes();
				byte[] oldtab = resultStream;
				resultStream = new byte[oldtab.length + newtab.length];
				System.arraycopy(oldtab, 0, resultStream, 0, oldtab.length);
				System.arraycopy(newtab, 0, resultStream, oldtab.length, newtab.length);
				aredigits = new boolean[columnsName.size()];
				i = 0;
				while (jdbcResult.next()) {
					int j = 0;
					row = "";
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
								row += jdbcResult.getDouble(colname) + ";";
							} catch (Exception e) {
								row += jdbcResult.getString(colname) + ";";
								aredigits[j] = false;
							}
						}
						else {
							row += "\"" + jdbcResult.getString(colname) + "\";";
						}
						j++;
					}
					// sb.append(row.substring(0, row.length() - 1) + "\n");
					newtab = (row.substring(0, row.length() - 1) + "\n").toString().getBytes();
					oldtab = resultStream;
					resultStream = new byte[oldtab.length + newtab.length];
					System.arraycopy(oldtab, 0, resultStream, 0, oldtab.length);
					System.arraycopy(newtab, 0, resultStream, oldtab.length, newtab.length);
					i++;
				}

				// resultStream = sb.toString().getBytes();
				break;
			case R:
				String req = dataset.getRequest();
				String name = dataset.getName();
				//				rServer.connect(userEnv);
				scriptResult = rServer.loadDataset(name, req, userEnv);
				//				rServer.deconnect();
				resultStream = null;
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
					row += HBaseMeta.getColumnLabel(i) + ";";
				}
				sb.append(row.substring(0, row.length() - 1) + "\n");
				aredigits = new boolean[columnsName.size()];
				i = 0;
				while (HBaseResult.next()) {
					int j = 0;
					row = "";
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
								row += HBaseResult.getDouble(colname) + ";";
							} catch (Exception e) {
								row += HBaseResult.getString(colname) + ";";
								aredigits[j] = false;
							}
						}
						else {
							row += HBaseResult.getString(colname) + ";";
						}
						j++;
					}
					sb.append(row.substring(0, row.length() - 1) + "\n");

					i++;
				}
				resultStream = sb.toString().getBytes();
				break;
			case CSV:
				DatasourceCsv csv = (DatasourceCsv) dataset.getDatasource().getObject();

				String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
				Path path = Paths.get(basePath, csv.getFilePath());
				resultStream = Files.readAllBytes(path);

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
				//				rServer.connect(userEnv);
				if (path.toString().substring(path.toString().lastIndexOf(".") + 1).equals("csv")) {
					nameTempFile = rServer.loadCsvFile(resultStream, hasHeader, sep, userEnv);
					//					rServer.connect(userEnv);
					scriptResult = rServer.addDatasetTempFile(request, dataset.getName(), nameTempFile, userEnv);
				}
				else {
					nameTempFile = rServer.loadExcelFile(resultStream, hasHeader, true, dataset.getMetacolumns(), userEnv);
					//					rServer.connect(userEnv);
					scriptResult = rServer.addDatasetTempFile(request, dataset.getName(), nameTempFile, userEnv);
				}
				//				rServer.deconnect();
				resultStream = null;
				break;
			case SOCIAL:
				req = dataset.getRequest();
				name = dataset.getName();
				scriptResult = rServer.loadDataset(name, req, userEnv);
				resultStream = null;
				break;
			case ARCHITECT:
				RemoteVanillaPlatform rootVanillaApi = new RemoteVanillaPlatform(
						ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
						ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
						ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
				DatasourceArchitect architectSource = (DatasourceArchitect) dataset.getDatasource().getObject();

				user = rootVanillaApi.getVanillaSecurityManager().authentify("", rootVanillaApi.getVanillaContext().getLogin(), 
						rootVanillaApi.getVanillaContext().getPassword(), false);

				int userId = user.getId();

				MdmRemote mdmRemote = new MdmRemote(user.getLogin(), user.getPassword(), architectSource.getUrl());
				RemoteGedComponent ged = new RemoteGedComponent(architectSource.getUrl(), user.getLogin(), user.getPassword());

				try {
					Contract contract = mdmRemote.getContract(architectSource.getContractId());
					GedDocument document = ged.getDocumentDefinitionById(contract.getDocId());
					GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(document, userId);

					if (contract.getVersionId() != null) {
						DocumentVersion docVersion = ged.getDocumentVersionById(contract.getVersionId());
						config = new GedLoadRuntimeConfig(document, userId, docVersion.getVersion());
					}
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					IOWriter.write(ged.loadGedDocument(config), out, true, true);
					resultStream = out.toByteArray();
					String format = document.getLastVersion().getFormat();
					String datasetName;
					switch(format) {
					case "xlsx":
						datasetName = rServer.loadExcelFile(resultStream, false, true, dataset.getMetacolumns(), userEnv);
//						rServer.connect(userEnv);
						scriptResult = rServer.addDatasetTempFile(dataset.getRequest(), dataset.getName(), datasetName, userEnv);
						break;
					case "xls":
						datasetName = rServer.loadExcelFile(resultStream, false, true, dataset.getMetacolumns(), userEnv);
//						rServer.connect(userEnv);
						scriptResult = rServer.addDatasetTempFile(dataset.getRequest(), dataset.getName(), datasetName, userEnv);
						break;
					case "csv":
						datasetName = rServer.loadCsvFile(resultStream, false, architectSource.getSeparator(), userEnv);
//						rServer.connect(userEnv);
						scriptResult = rServer.addDatasetTempFile(dataset.getRequest(), dataset.getName(), datasetName, userEnv);
						break;
					}

				}catch (Exception e) {
					e.printStackTrace();

				}
				rServer.deconnect();
				resultStream = null;
				break;
			}
			if (resultStream != null) {
				//				rServer.connect(userEnv);
				scriptResult = rServer.addDatasetTemp(resultStream, aredigits, dataset.getName(), userEnv);
				//				rServer.deconnect();
			}

		}
		else {
			String rPackage = dataset.getRPackages().split(";")[0];
			String name = dataset.getName();
			String request = "library(" + rPackage + ")\n" + "data(" + name + ")\n";

			//			rServer.connect(userEnv);
			scriptResult = rServer.loadDataset(name, request, userEnv);
			//			rServer.deconnect();
		}

		dataset = component.getVanillaApi().getVanillaPreferencesManager().getDatasetById(dataset.getId());
		if(scriptResult.isScriptError()){
			dataset.setIsRLoaded(false);
			component.getVanillaApi().getVanillaPreferencesManager().updateDataset(dataset);
		} else {
			dataset.setIsRLoaded(true);
			component.getVanillaApi().getVanillaPreferencesManager().updateDataset(dataset);
		}
		return scriptResult;
	}

	@Override
	public boolean checkProjectNameExistence(String projectName) throws Exception {
		List<AirProject> projects = getAllAirProjects();
		for (AirProject project : projects) {
			if (project.getName().equals(projectName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void saveAirProjectWithElements(InputStream imporProjectStream, String alternativeName, int idUser) throws Exception {

		ZipInputStream zis = new ZipInputStream(imporProjectStream);
		ZipEntry entry;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];

		AirProject project = null;
		List<RScript> scripts = new ArrayList<RScript>();
		List<RScriptModel> scriptsModels = new ArrayList<RScriptModel>();
		String iconfileName, iconcontentType;
		InputStream iconstream;
		int idProject = 0;

		Set<String> rmds = new HashSet<String>();

		while ((entry = zis.getNextEntry()) != null) {
			if (entry.getName().equals("descriptor.xml")) {
				int len = 0;
				bos = new ByteArrayOutputStream();
				while ((len = zis.read(buffer)) > 0) {
					bos.write(buffer, 0, len);
				}
				String xml = new String(bos.toByteArray());
				project = (AirProject) new XStream().fromXML(xml);
				if (alternativeName != null) {
					project.setName(alternativeName);
				}
				project.setIdUserCreator(idUser);
				idProject = saveAirProject(project);

				bos.close();
			}
			else if (entry.getName().substring(entry.getName().lastIndexOf(".") + 1, entry.getName().length()).equals("xml")) {
				String nameScriptParent = "";
				if(entry.getName().contains("/")){
					nameScriptParent = entry.getName().substring(0, entry.getName().indexOf("/"));
				} else {
					nameScriptParent = entry.getName();
				}
				boolean exists = false;
				for (RScript s : scripts) {
					if (s.getName().equals(nameScriptParent)) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					RScript s = new RScript();
					s.setName(nameScriptParent);
					s.setFree(true);
					s.setHoldingUsername("");
					s.setIdProject(idProject); // peut-etre à 0

					int idScript = saveRScript(s);
					s.setId(idScript);
					scripts.add(s);
				}

				RScriptModel sm = new RScriptModel();
				int idScriptParent = 0;

				int len = 0;
				bos = new ByteArrayOutputStream();
				while ((len = zis.read(buffer)) > 0) {
					bos.write(buffer, 0, len);
				}
				String xml = new String(bos.toByteArray());
				sm = (RScriptModel) new XStream().fromXML(xml);

				for (RScript s : scripts) {
					if (s.getName().equals(nameScriptParent)) {
						idScriptParent = s.getId();
						break;
					}
				}
				sm.setIdScript(idScriptParent);

				saveRScriptModel(sm);
			}
			else if (entry.getName().substring(entry.getName().lastIndexOf(".") + 1, entry.getName().length()).matches("jpg|png")) {

				iconfileName = entry.getName();
				iconcontentType = "image/" + entry.getName().substring(entry.getName().lastIndexOf(".") + 1, entry.getName().length());
				int len = 0;
				bos = new ByteArrayOutputStream();
				while ((len = zis.read(buffer)) > 0) {
					bos.write(buffer, 0, len);
				}
				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				iconstream = bis;

				addAvatarIcon(iconfileName, iconstream, iconcontentType, false);

				bos.close();
				bis.close();
			}
			else if (entry.getName().substring(entry.getName().lastIndexOf(".") + 1, entry.getName().length()).equals("Rmd")) {
				rmds.add(entry.getName().substring(0, entry.getName().indexOf("/")));
			}
		}

		for (RScript s : scripts) {
			s.setIdProject(idProject);
			if(rmds.contains(s.getName())) s.setScriptType("MARKDOWN");
			updateRScript(s);
		}
	}

	@Override
	public RScriptModel generateStatRPlot(DataColumn col1, DataColumn col2, Dataset dataset) throws Exception {
		int[] Nums = { Types.BIGINT, Types.BINARY, Types.BIT, Types.BOOLEAN, Types.DECIMAL, Types.DATE, Types.DOUBLE, Types.FLOAT, Types.INTEGER, Types.LONGVARBINARY, Types.NUMERIC, Types.REAL, Types.SMALLINT, Types.ROWID, Types.TIME, Types.TIMESTAMP, Types.TINYINT, Types.VARBINARY };

		int[] Chars = { Types.CHAR, Types.LONGNVARCHAR, Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.NULL, Types.REF, Types.SQLXML, Types.VARCHAR };
		User user= getUser();
		String userREnv = user.getLogin() + user.getId();
		// addDatasettoR(dataset, userREnv);

		RScriptModel box = new RScriptModel();

		String script =

				"library(ggplot2)\n"// "library("+ dataset.getRequest() + data...")\n"
						+ "colclass1 <- class(" + dataset.getName() + "[['" + col1.getColumnName() + "']])\n" 
						+ "colclass2 <- class(" + dataset.getName() + "[['" + col2.getColumnName() + "']])\n" 
						+ "if((colclass1 =='numeric' | colclass1=='integer')&& (colclass2 =='numeric' | colclass2=='integer')) {\n" 
						+ "class_name<-paste(names(c(" + dataset.getName() + ")['" + col2.getColumnName() + "']), ' ~ .', sep = '')\n" 
						+ "p <- ggplot(" + dataset.getName() + ", aes(x = " + dataset.getName() + "[,'" + col1.getColumnName() + "'], y = " + dataset.getName() + "[,'" + col2.getColumnName() + "']))\n" 
						+ "p <- p + geom_point(colour='black')\n" 
						+ "p <- p + theme_bw() + xlab(names(c(" + dataset.getName() + "['" + col1.getColumnName() + "']))) + ylab(names(c(" + dataset.getName() + "['" + col2.getColumnName() + "'])))\n" 
						+ "p <- p + guides(fill=guide_legend(title=names(c(" + dataset.getName() + ")['" + col2.getColumnName() + "'])))\n" 
						+ "p\n" 
						+ "}"
						+ "else if((colclass1=='factor' | colclass1=='character') && (colclass2=='factor' | colclass2=='character')) {\n" 
						+ "class_name<-paste(names(c(" + dataset.getName() + ")['" + col2.getColumnName() + "']), ' ~ .', sep = '')\n" 
						+ "p <- ggplot(" + dataset.getName() + ", aes(x = " + dataset.getName() + "[,'" + col1.getColumnName() + "'], shape=" + dataset.getName() + "[,'" + col2.getColumnName() + "'], fill = " + dataset.getName() + "[,'" + col2.getColumnName() + "']))\n" 
						+ "p <- p + geom_bar()\n" 
						+ "p <- p + facet_grid(class_name, scales = 'free')\n" 
						+ "p <- p + theme_bw() + xlab(names(c(" + dataset.getName() + "['" + col1.getColumnName() + "'])))\n" 
						+ "p <- p + guides(fill=guide_legend(title=names(c(" + dataset.getName() + ")['" + col2.getColumnName() + "'])))\n" 
						+ "p\n" 
						+ "}" 
						+ "else if((colclass1 =='numeric' | colclass1=='integer') && (colclass2=='factor' | colclass2=='character')) {\n"
						+ dataset.getName() + "<- " + dataset.getName() + "[order(" + dataset.getName() + "[,'" + col2.getColumnName() + "']),]\n"
						+ "class_name<-paste(names(c(" + dataset.getName() + ")['" + col2.getColumnName() + "']), ' ~ .', sep = '')\n" 
						+ "p <- ggplot(" + dataset.getName() + ", aes(x = " + dataset.getName() + "[,'" + col1.getColumnName() + "'], colour=" + dataset.getName() + "[,'" + col2.getColumnName() + "'], shape=" + dataset.getName() + "[,'" + col2.getColumnName() + "'], fill = " + dataset.getName() + "[,'" + col2.getColumnName() + "']))\n" 
						+ "p <- p + geom_histogram(binwidth=.5, colour='black')\n" + "p <- p + facet_grid(class_name, scales = 'free')\n" 
						+ "p <- p + theme_bw() + xlab(names(c(" + dataset.getName() + "['" + col1.getColumnName() + "'])))\n" 
						+ "p <- p + guides(fill=guide_legend(title=names(c(" + dataset.getName() + ")['" + col2.getColumnName() + "'])))\n" 
						+ "p\n" 
						+ "}" 
						+ "else if((colclass1=='factor' | colclass1=='character') && (colclass2 =='numeric' | colclass2=='integer')) {\n" 
						+ dataset.getName() + "<- " + dataset.getName() + "[order(" + dataset.getName() + "[,'" + col1.getColumnName() + "']),]\n"
						+ "class_name<-paste(names(c(" + dataset.getName() + ")['" + col1.getColumnName() + "']), ' ~ .', sep = '')\n"
						+ "p <- ggplot(" + dataset.getName() + ", aes(x = " + dataset.getName() + "[,'" + col2.getColumnName() + "'], colour=" + dataset.getName() + "[,'" + col1.getColumnName() + "'], shape=" + dataset.getName() + "[,'" + col1.getColumnName() + "'], fill = " + dataset.getName() + "[,'" + col1.getColumnName() + "']))\n" 
						+ "p <- p + geom_histogram(binwidth=.5, colour='black')\n" 
						+ "p <- p + facet_grid(class_name, scales = 'free')\n" 
						+ "p <- p + theme_bw() + xlab(names(c(" + dataset.getName() + "['" + col2.getColumnName() + "'])))\n" 
						+ "p <- p + guides(fill=guide_legend(title=names(c(" + dataset.getName() + ")['" + col1.getColumnName() + "'])))\n" 
						+ "p\n" 
						+ "}";
		box.setScript(script);
		// box.setOutputs("script_result".split(" "));
		box.setUserREnv(userREnv);
		RScriptModel result = executeScriptR(box);

		return result;
	}

	@Override
	public RScriptModel renderMarkdown(String script, String name, List<String> outputs, List<Parameter> lovParams) throws Exception {
		RServer rServer = component.getClusterManager().getRserver(user);
		User user = getUser();
		String userEnv = user.getLogin() + user.getId();

		RScriptModel scriptResult = new RScriptModel();
		byte[] resultStream = null;
		String adaptedScript ="";
		for(String line : script.split("\n")){
			adaptedScript += rServer.adaptRMarkdownLine(line, userEnv).trim() +"\n";
		}


		if(lovParams != null && !lovParams.isEmpty()){
			// traitement LOV //////////////////
			int[][] values = getLOVValuesSequence(lovParams);

			String scriptOriginal = new String(adaptedScript);
			String tempScript = "";

			String outputLogs = "";
			List<String> outputFiles = new ArrayList<>();
			List<String> outputVarstoString = new ArrayList<>();
			List<String> outputsRes = new ArrayList<>();
			boolean scriptError = false;
			for(int i=0; i<values.length; i++){
				tempScript = new String(scriptOriginal);
				for(int j=0; j< lovParams.size(); j++){
					String val = lovParams.get(j).getListOfValues().getValues().get(values[i][j]);
					try {
						Double.parseDouble(val);
						tempScript = tempScript.replace(lovParams.get(j).getParameterName(), val);
					} catch(Exception e) {
						tempScript = tempScript.replace(lovParams.get(j).getParameterName(), "\""+val+"\"");
					}
				}
				resultStream = tempScript.getBytes();

				RScriptModel res = rServer.renderMarkdown(resultStream, name, userEnv, outputs);    //////////////// EXECUTION /////////////////

				outputLogs += res.getOutputLog() + "</br>";
				if(res.getOutputFiles()!= null)
					outputFiles.addAll(Arrays.asList(res.getOutputFiles()));

				if (res.getOutputVars() != null) {
					List<String> list = new ArrayList<String>();
					for (int j = 0; j < res.getOutputVars().size(); j++) {
						//list.add(IOUtils.toString(Base64.encodeBase64((byte[]) ((List<String>)res.getOutputVars().get(j)).get(0).getBytes()), "UTF-8"));
						list.add(IOUtils.toString(Base64.encodeBase64((byte[]) ((List)res.getOutputVars().get(j)).get(0)), "UTF-8"));
					}
					res.setOutputVarstoString(list);
				}
				outputVarstoString.addAll(res.getOutputVarstoString());
				scriptError = res.isScriptError();
				for(String output : outputs){
					if(output.equals("html")) outputsRes.add("temphtml");
					if(output.equals("pdf")) outputsRes.add("temppdf");
					if(output.equals("docx")) outputsRes.add("tempdoc");
				}


				if(res.isScriptError()) break;
			}


			////////////////////////////////////

			//necessary
			scriptResult.setOutputs(outputsRes.toArray(new String[outputsRes.size()]));
			scriptResult.setScriptError(scriptError);
			scriptResult.setOutputFiles(outputFiles.toArray(new String[outputFiles.size()]));
			scriptResult.setOutputLog(outputLogs);
			scriptResult.setOutputVars(null);
			scriptResult.setOutputVarstoString(outputVarstoString);


		} else {
			resultStream = adaptedScript.getBytes();
			if(Arrays.toString(resultStream).contains("-96")){  //kmo protection
				List<Byte> bytes = new ArrayList<>();
				for(int i=0; i< resultStream.length; i++){
					if(resultStream[i] != -96){
						bytes.add(resultStream[i]);
					}
				}
				resultStream = new byte[bytes.size()];
				for(int i=0; i< resultStream.length; i++){
					resultStream[i] = bytes.get(i);
				}
			}


			//			rServer.connect(userEnv);
			scriptResult = rServer.renderMarkdown(resultStream, name, userEnv, outputs);
			//			rServer.deconnect();
			if (scriptResult.getOutputVars() != null) {
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < scriptResult.getOutputVars().size(); i++) {
					//list.add(IOUtils.toString(Base64.encodeBase64((byte[]) ((List<String>)scriptResult.getOutputVars().get(i)).get(0).getBytes()), "UTF-8"));
					list.add(IOUtils.toString(Base64.encodeBase64((byte[]) ((List)scriptResult.getOutputVars().get(i)).get(0)), "UTF-8"));
				}
				scriptResult.setOutputVarstoString(list);
			}

			scriptResult.setOutputVars(null);


		}
		return scriptResult;
	}

	@Override
	public String uploadFile(String name, InputStream extDocStream) throws Exception {
		long time = new Date().getTime();
		String fName = name;
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String relPath = "";

		relPath = "/air_files/" + time + "_" + fName;

		try {
			File file = new File(basePath + "/" + relPath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			IOWriter.write(extDocStream, fileOutputStream, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "success;" + relPath;
	}

	@Override
	public List<DataColumn> getDatasourceCsvMetadata(Datasource datasourceCsv) throws Exception {
		RServer rServer = component.getClusterManager().getRserver(user);
		User user = getUser();
		String userREnv = user.getLogin() + user.getId();

		List<DataColumn> columns = new ArrayList<DataColumn>();
		String nameTempFile;
		DatasourceCsv csv = (DatasourceCsv) datasourceCsv.getObject();
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		Path path = Paths.get(basePath, csv.getFilePath());
		byte[] data = Files.readAllBytes(path);
		boolean hasHeader = csv.getHasHeader();
		String sep = csv.getSeparator();
		//		rServer.connect(userREnv);
		if (path.toString().substring(path.toString().lastIndexOf(".") + 1).equals("csv")) {
			nameTempFile = rServer.loadCsvFile(data, hasHeader, sep, userREnv);
			//			rServer.connect(userREnv);
			columns = rServer.getTempFileMetaData(nameTempFile, userREnv);
		}
		else {
			nameTempFile = rServer.loadExcelFile(data, hasHeader, false, null, userREnv);
			//			rServer.connect(userREnv);
			columns = rServer.getTempFileMetaData(nameTempFile, userREnv);
		}
		//		rServer.deconnect();

		return columns;
	}

	@Override
	public List<Workflow> getWorkflows() throws Exception {
		//TODO: Deactivated for now because we need to add a type for the workflow to separate between smart workflow and VanillaHub
//		List<Workflow> workflows = dao.getHibernateTemplate().find("From Workflow");
		List<Workflow> workflows = new ArrayList<Workflow>();
		buildWorkflows(workflows);
		return workflows;
	}
	
	@Override
	public DataWithCount<Workflow> getWorkflows(String query, int firstResult, int length, boolean lightWeight, DataSort dataSort) throws Exception {
		throw new Exception("Not implemented !");
	}

	@Override
	public Workflow getWorkflow(int workflowId, boolean lightWeight) throws Exception {
		//TODO: Deactivated for now because we need to add a type for the workflow to separate between smart workflow and VanillaHub
		throw new Exception("Not implemented !");
	}

	@Override
	public Workflow manageWorkflow(Workflow currentWorkflow, boolean modify) throws Exception {
		if (modify) {
			currentWorkflow.setModel(xstream.toXML(currentWorkflow.getWorkflowModel()));
			dao.getHibernateTemplate().update(currentWorkflow);

			if (currentWorkflow.getSchedule() != null) {
				if (currentWorkflow.getSchedule().getId() > 0) {
					dao.update(currentWorkflow.getSchedule());
				}
				else {
					dao.add(currentWorkflow.getSchedule());
				}
			}
			return currentWorkflow;
		}
		else {
			buildWorkflow(currentWorkflow);
			Integer id = (Integer) dao.getHibernateTemplate().save(currentWorkflow);
			currentWorkflow.setId(id);

			if (currentWorkflow.getSchedule() != null) {
				dao.add(currentWorkflow.getSchedule());
			}
			return currentWorkflow;
		}
	}

	private void buildWorkflows(List<Workflow> workflows) {
		if (workflows != null) {
			for (Workflow workflow : workflows) {
				if (workflow.getModel() == null) {
					String model = xstream.toXML(workflow.getWorkflowModel());
					workflow.setModel(model);
				}

				if (workflow.getWorkflowModel() == null || workflow.getWorkflowModel().getActivities() == null) {
					WorkflowModel model = (WorkflowModel) xstream.fromXML(workflow.getModel());
					workflow.setWorkflowModel(model);
				}

				workflow.setRunningRuns(component.getWorkflowManager().getRunningInstances(workflow.getId()));

				workflow.setNextExecution(SchedulerUtils.getNextExecution(new Date(), workflow.getSchedule()));

				Schedule schedule = getSchedule(workflow.getId());
				workflow.setSchedule(schedule);
			}
		}
	}

	private void buildWorkflow(Workflow workflow) {
		if (workflow.getModel() == null) {
			String model = xstream.toXML(workflow.getWorkflowModel());
			workflow.setModel(model);
		}
		if (workflow.getWorkflowModel() == null) {
			WorkflowModel model = (WorkflowModel) xstream.fromXML(workflow.getModel());
			workflow.setWorkflowModel(model);
		}

		Schedule schedule = getSchedule(workflow.getId());
		workflow.setSchedule(schedule);

		workflow.setNextExecution(SchedulerUtils.getNextExecution(new Date(), workflow.getSchedule()));
	}

	@SuppressWarnings("unchecked")
	private Schedule getSchedule(int workflowId) {
		List<Schedule> items = dao.getHibernateTemplate().find("from Schedule where workflowId=" + workflowId);
		dao.buildSchedule(items);
		if (items.isEmpty()) {
			return null;
		}
		return items.get(0);
	}

	@Override
	public void removeWorkflow(Workflow currentWorkflow) throws Exception {
		dao.getHibernateTemplate().delete(currentWorkflow);
	}

	@Override
	public List<Parameter> getWorkflowParameters(Workflow workflow) throws Exception {
		return getParameters(workflow);
	}

	public List<Parameter> getParameters(Workflow workflow) throws Exception {
		Logger.getLogger(getClass()).info("Getting parameters for workflow '" + workflow.getName() + "'.");
		if (!workflow.isValid()) {
			Logger.getLogger(getClass()).error("Workflow '" + workflow.getName() + "' is not valid.");
			throw new Exception("'" + workflow.getName() + "' not valid.");
		}

		ResourceManager manager = initResourceManager();

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (Activity activity : workflow.getWorkflowModel().getActivities()) {

			ActivityRunner<?> runner = ActivityRunnerFactory.getActiviyRunner(activity, null, manager);


			List<Parameter> activityParams = runner.getParameters();

			if(activity instanceof AirScriptActivity){
				RScriptModel model = getLastModelbyScript(((AirScriptActivity)activity).getrScriptId());
				List<Parameter> params = getScriptParameters(model.getScript());
				activityParams.addAll(params);
				//((AirScriptActivity)activity).setParameters(params);
			}

			for (Parameter param : activityParams) {

				boolean found = false;
				for (Parameter currentParam : manager.getParameters()) {
					if (param.getId() == currentParam.getId() && !isParamPresent(parameters, param)) {
						parameters.add(currentParam);
						found = true;
						break;
					}
				}

				if (!found && !isParamPresent(parameters, param)) {
					Logger.getLogger(getClass()).warn("Variable '" + param.getName() + "' has not been found, we will use an old definition of it.");
					parameters.add(param);
				}
			}
		}

		return parameters;
	}

	private boolean isParamPresent(List<Parameter> parameters, Parameter currentParam) {
		if (parameters != null) {
			for (Parameter param : parameters) {
				if (param.getId() == currentParam.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public WorkflowInstance runWorkflow(Workflow workflow, String uuid, User launcher, List<Parameter> parameters) throws Exception {
		ResourceManager resourceManager = initResourceManager();
		return component.getWorkflowManager().runWorkflow(workflow, parameters, getLocale(), this, resourceManager);
	}

	@Override
	public WorkflowInstance getWorkflowProgress(Workflow workflow, String uuid) throws Exception {
		return component.getWorkflowManager().getWorkflowProgress(uuid, workflow.getId());
	}

	public SmartManagerComponent getComponent() {
		return component;
	}

	public User getUser() {
		return user;
	}

	private Workflow getWorkflowById(int workflowId, boolean lightWeight) throws Exception {
		Workflow workflow = (Workflow) dao.getHibernateTemplate().find("From Workflow where id = " + workflowId).get(0);
		buildWorkflow(workflow);

		if (!lightWeight) {
			List<WorkflowInstance> instances = dao.getHibernateTemplate().find("From WorkflowInstance where workflowId = " + workflowId);
			for(WorkflowInstance instance : instances) {
				instance.setWorkflow(workflow.getId(), workflow.getName());
				if(instance.getModelLogs() != null) {
					try {
						instance.setActivityLogs((List<ActivityLog>) xstream.fromXML(instance.getModelLogs()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			Collections.sort(instances, new Comparator<WorkflowInstance>() {
				@Override
				public int compare(WorkflowInstance o1, WorkflowInstance o2) {
					return - o1.getEndDate().compareTo(o2.getEndDate());
				}
			});

			workflow.setRuns(instances);
		}

		return workflow;
	}

	@Override
	public List<WorkflowInstance> getWorkflowRuns(Workflow workflow) throws Exception {
		workflow = getWorkflowById(workflow.getId(), false);
		return workflow.getRuns();
	}

	@Override
	public List<WorkflowInstance> getWorkflowRunningInstances(int workflowId) throws Exception {
		return component.getWorkflowManager().getRunningInstances(workflowId);
	}

	@Override
	public String generateCSVinR(Dataset dts) throws Exception {
		addDatasettoR(dts);
		User user = getUser();
		String userEnv = user.getLogin() + user.getId();
		RScriptModel box = new RScriptModel();

		String script = "filetemp <- tempfile()\n" + "write.csv(" + dts.getName() + ", file = filetemp)\n" + "streamcsv<-readBin(filetemp,'raw',1024*1024)";
		box.setScript(script);
		box.setOutputs("streamcsv".split(" "));
		box.setUserREnv(userEnv);
		RScriptModel result = executeScriptR(box);

		long time = new Date().getTime();
		String fName = dts.getName() + ".csv";
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String relPath = "";

		relPath = "/air_files/" + time + "_" + fName;

		try {
			File file = new File(basePath + "/" + relPath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ByteArrayInputStream extDocStream = new ByteArrayInputStream((byte[]) result.getOutputVars().get(0));

			IOWriter.write(extDocStream, fileOutputStream, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return basePath + "/" + relPath;

	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) {
		return dao.manageResource(resource, edit);
	}

	@Override
	public void removeResource(Resource resource) {
		dao.delete(resource);
	}

	@Override
	public List<? extends Resource> getResources(TypeResource type) {
		return dao.getResources(type);
	}

	@Override
	public List<String> getColumnDistinctValues(String datasetName, int columnIndex) throws Exception {

		String script = "manual_result<-" + datasetName + "[" + (columnIndex + 1) + "]";

		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setUserREnv(user.getLogin() + user.getId());
		model.setOutputs(new String[] { "manual_result" });

		model = executeScriptR(model);
		String values = model.getOutputVarstoString().get(0);
		List<String> distincts = Arrays.asList(values.trim().split("\\t"));

		HashSet<String> temp = new HashSet<String>(distincts);
		distincts = new ArrayList<String>(temp);

		return distincts;
	}

	@Override
	public CheckResult validScript(Variable variable) throws Exception {
		try {
			dao.testVariable(getComponent().getLogger(), getLocale(), variable);
			return new CheckResult(Labels.getLabel(getLocale(), Labels.ScriptCorrect), false);
		} catch (Exception e) {
			e.printStackTrace();
			return new CheckResult(e.getMessage(), true);
		}
	}

	@Override
	public void deleteFile(String path) throws Exception {

		try {
			File file = new File(path);
			file.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public RScriptModel generateSummaryRPlot(Dataset dataset) throws Exception {
		RScriptModel box = new RScriptModel();

		String script = "library(tabplot)\n";
		script += "data(" + dataset.getName() + ")\n";
		script += "result_sum <- paste(capture.output(summary(" + dataset.getName() + ")), sep=\"\t\", collapse='<br>')\n";
		script += "if(ncol(" + dataset.getName() + "[, sapply(" + dataset.getName() + ", class) == 'character']) !=0){\n";
		script += "resultDF <- cbind( " + dataset.getName() + "[, sapply(" + dataset.getName() + ", class) != 'character'], sapply(" + dataset.getName() + "[, sapply(" + dataset.getName() + ", class) == 'character'], factor) )\n";
		script += "} else {\n";
		script += "resultDF <- " + dataset.getName() + " }\n";
		script += "tableplot(resultDF, nBins=100)";
		box.setScript(script);
		box.setOutputs(new String[] { "result_sum" });
		RScriptModel result = executeScriptR(box);
		return result;
	}

	@Override
	public WorkflowInstance runIncompleteWorkflow(Workflow workflow, List<Parameter> parameters, String stopActivityName) throws Exception {
		buildWorkflow(workflow);
		ResourceManager resourceManager = initResourceManager();
		return component.getWorkflowManager().runIncompleteWorkflow(workflow, parameters, stopActivityName, locale, this, resourceManager);
	}

	@Override
	public List<DataColumn> getDatasetColumns(String datasetName) throws Exception {
		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();

		StringBuilder builder = new StringBuilder("res<-character()\n");
		builder.append("data(" + datasetName + ")\n");
		builder.append("for(name in colnames(" + datasetName + ")) {\n");
		builder.append("    colclass<-class(" + datasetName + "[[name]])\n");
		builder.append("    res<-rbind(res,c(paste(name, colclass, sep='_!_')))\n");
		builder.append("}\n");
		builder.append("manual_result <- res");

		RScriptModel box = new RScriptModel();
		box.setScript(builder.toString());
		box.setOutputs("manual_result".split(" "));
		RScriptModel result = executeScriptR(box);

		DataColumn column;
		List<String> it = new ArrayList<>();
		try {
			it = (List<String>) result.getOutputVars().get(0);
		} catch (Exception e) {
			it = Arrays.asList((String[])result.getOutputVars().get(0));
		}
		for (String rsM : it) {
			String name = rsM.split("_!_")[0];
			String RType = rsM.split("_!_")[1];
			int SQLType = 0;
			if (RType.equals("numeric")) {
				SQLType = java.sql.Types.DOUBLE;
			}
			else if (RType.equals("character") || RType.equals("factor")) {
				SQLType = java.sql.Types.VARCHAR;
			}
			else if (RType.equals("logical")) {
				SQLType = java.sql.Types.BOOLEAN;
			}
			else {
				SQLType = java.sql.Types.OTHER;
			}

			column = new DataColumn(name, RType, SQLType, name);

			columns.add(column);
		}

		return columns;
	}

	@SuppressWarnings("unchecked")
	public ResourceManager initResourceManager() {
		List<Cible> cibles = (List<Cible>) dao.getResources(TypeResource.CIBLE);
		List<Variable> variables = (List<Variable>) dao.getResources(TypeResource.VARIABLE);
		List<Parameter> parameters = (List<Parameter>) dao.getResources(TypeResource.PARAMETER);

		ResourceManager resourceManager = new ResourceManager();
		resourceManager.setVariables(variables);
		resourceManager.setParameters(parameters);
		resourceManager.setCibles(cibles);
		return resourceManager;
	}

	@Override
	public int saveAirCube(AirCube airCube) throws Exception {	
		if (airCube == null) {
			throw new Exception("Cannot save a null Cube");
		}
		airCube.setIdUser(getUser().getId());
		if(airCube.getId() == 0){
			int id = (Integer) dao.save(airCube);
			return id;
		} else {
			dao.update(airCube);
			return airCube.getId();
		}
	}

	@Override
	public List<AirCube> getCubesbyDataset(int idDataset) throws Exception {
		List<AirCube> list = (List<AirCube>) dao.find("From AirCube Where idDataset=" + idDataset + " AND idUser=" + getUser().getId());
		return list;
	}

	@Override
	public ActivityLog executeActivity(Activity activity) throws Exception {
		ActivityRunnerFactory fact = new ActivityRunnerFactory();
		ActivityRunner<Activity> runner = (ActivityRunner<Activity>)fact.getActiviyRunner(activity, new WorkflowRunInstance(null, locale, this, true, "", null), null);
		return runner.executeActivity();
	}

	public List<Dataset> getPermittedDatasets(User user) throws Exception {
		Set<Dataset> result = new HashSet<Dataset>();
		Set<Integer> ids = new HashSet<Integer>();

		List<Dataset> globalList = component.getVanillaApi().getVanillaPreferencesManager().getDatasets();
		for(Dataset dts : globalList){
			if(dts.getIdAuthor() == user.getId()){
				result.add(dts);
				ids.add(dts.getId());
			}
		}

		List<AirProject> projects = getVisibleProjects(user.getId());
		for(AirProject proj : projects){
			if(proj.getLinkedDatasets()!=null && !proj.getLinkedDatasets().equals("")){
				for(String id : proj.getLinkedDatasets().split(";")){
					if(!ids.contains(Integer.parseInt(id))){
						Dataset dts = component.getVanillaApi().getVanillaPreferencesManager().getDatasetById(Integer.parseInt(id));
						result.add(dts);
						ids.add(dts.getId());
					}
				}
			}

		}

		return new ArrayList<Dataset>(result);
	}

	private void saveStatsbyDataset(List<StatDataColumn> stats, Dataset dataset) {
		List<StatDataColumn> oldstats = getStatsbyDataset(dataset);

		for(StatDataColumn stat : oldstats){
			dao.delete(stat);
		}

		for(StatDataColumn stat : stats){
			dao.save(stat);
		}
	}

	private List<StatDataColumn> getStatsbyDataset(Dataset dataset) {
		List<DataColumn> cols = new ArrayList<DataColumn>();
		try {
			cols = api.getVanillaPreferencesManager().getDataColumnsbyDataset(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<StatDataColumn> stats = new ArrayList<StatDataColumn>();
		for(DataColumn col : cols){
			List<StatDataColumn> stat = (List<StatDataColumn>) dao.find("Select new StatDataColumn(idDatacolumn, nameDatacolumn, minStat, maxStat, average, deviation, repartitionToString) From StatDataColumn Where idDatacolumn=" + col.getId());
			stats.addAll(stat);
		}
		return stats;
	}

	@Override
	public List<Dataset> getDatasetsbyProject(AirProject proj) throws Exception {
		List<Dataset> result = new ArrayList<Dataset>();
		if(proj.getLinkedDatasets()!=null){
			for(String id : proj.getLinkedDatasets().split(";")){
				Dataset dts = component.getVanillaApi().getVanillaPreferencesManager().getDatasetById(Integer.parseInt(id));
				result.add(dts);
			}
		}
		return new ArrayList<Dataset>(result);
	}

	@Override
	public List<RScriptModel> addDatasetstoR(List<Dataset> datasets) throws Exception {
		List<RScriptModel> scriptResults = new ArrayList<RScriptModel>();
		for(Dataset dts : datasets){
			scriptResults.add(addDatasettoR(dts));
		}
		return scriptResults;
	}

	@Override
	public void deleteLinkedDatasets(Dataset dataset) throws Exception {
		int id = dataset.getId();
		List<AirProject> projs = getAllAirProjects();
		for(AirProject project : projs){
			if(project.getLinkedDatasets() != null && !project.getLinkedDatasets().equals("")){
				List<String> linklist = new ArrayList<String>(Arrays.asList(project.getLinkedDatasets().split(";")));
				if(linklist.contains(id+"")){
					linklist.remove(id+"");
					String res = "";
					for(String num : linklist){
						res+= num + ";";
					}
					if(res.length() > 0){
						res = res.substring(0, res.length()-1);
					}

					project.setLinkedDatasets(res);

					updateAirProject(project);
				}
			}

		}

	}

	@Override
	public RScript getScriptbyId(int idScript) throws Exception {
		List<RScript> scripts = (List<RScript>) dao.find("From RScript Where id=" + idScript);
		return scripts.get(0);
	}

	@Override
	public RScriptModel getScriptModelbyId(int idModel) throws Exception {
		List<RScriptModel> scripts = (List<RScriptModel>) dao.find("From RScriptModel Where id=" + idModel);
		return scripts.get(0);
	}

	@Override
	public String addAirImage(String name, InputStream extDocStream, String format) throws Exception {
		String fName = name;
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String relPath = "/air_files/images/" + fName;


		try {
			File file = new File(basePath + "/" + relPath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			IOWriter.write(extDocStream, fileOutputStream, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "success;" + relPath;
	}

	@Override
	public Map<String, List<Serializable>> getRDatasetData(Dataset dataset) throws Exception {
		Map<String, List<Serializable>> result = new HashMap<String, List<Serializable>>();
		RScriptModel model = new RScriptModel();
		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
		String script = "res<-character()\n";
		script += "data(" + dataset.getName() + ")\n";
		// script+= "for(name in colnames("+ dataset.getName() +")) {\n";
		// script+= "colclass<-sapply("+ dataset.getName() +"[name], class)\n";
		// script+= "res<-rbind(res,c(paste(name, colclass)))\n";
		// script+= "}\n";
		script += "manual_result <- " + dataset.getName();
		model.setScript(script);
		model.setOutputs("manual_result".split(" "));
		model = executeScriptR(model);
		if (model.getOutputVars() != null) {
			int i = 0;
			for (List<Object> col : (List<List<Object>>) model.getOutputVars().get(0)) {
				String name = dataset.getMetacolumns().get(i).getColumnLabel();
				int columnType = dataset.getMetacolumns().get(i).getColumnType();

				List<Serializable> values = new ArrayList<Serializable>();
				for (Object item : col) {
					
					switch (columnType) {
					case Types.BIGINT:
					case Types.INTEGER:
					case Types.SMALLINT:
					case Types.BOOLEAN:
						int[] colint = ((REXP) col).asIntegers();
						for (int str : colint) {
							values.add(str);
						}
						break;

					case Types.CHAR:
					case Types.LONGVARCHAR:
					case Types.VARCHAR:
					case Types.VARBINARY:
					case Types.DATE:
					case Types.TIME:
					case Types.TIMESTAMP:
						try {
							String[] colstring = ((REXP) item).asStrings();
							for (String str : colstring) {
								values.add(str);
							}
						} catch (Exception e) {
							String val = (String) item;
							values.add(val);
						}
						break;

					case Types.FLOAT:
					case Types.REAL:
					case Types.DECIMAL:
					case Types.DOUBLE:
					case Types.NUMERIC:
						try {
							double[] coldouble = ((REXP) item).asDoubles();
							for (double str : coldouble) {
								values.add(str);
							}
						} catch (Exception e) {
							String val = (String) item;
							values.add(Double.parseDouble(val));
						}
						break;
					default:
						values.add(item != null ? String.valueOf(item) : "");
						break;
					}
				}

				result.put(name, values);
				i++;
			}
		}

		return result;
	}

	@Override
	public void loadRDatasetTemp(Dataset dataset) throws Exception{
		RServer rServer = component.getClusterManager().getRserver(user);
		String userEnv = user.getLogin() + user.getId();
		rServer.loadDataset(dataset.getName(), dataset.getRequest(), userEnv);
	}

	@Override
	public String loadRCsvFile(byte[] resultStream, boolean hasHeader, String sep) throws Exception{
		RServer rServer = component.getClusterManager().getRserver(user);
		String userEnv = user.getLogin() + user.getId();
		return rServer.loadCsvFile(resultStream, hasHeader, sep, userEnv);
	}

	@Override
	public String loadRExcelFile(byte[] resultStream, boolean hasHeader, Dataset dataset) throws Exception{
		RServer rServer = component.getClusterManager().getRserver(user);
		String userEnv = user.getLogin() + user.getId();
		return rServer.loadExcelFile(resultStream, hasHeader, true, dataset.getMetacolumns(), userEnv);
	}

	@Override
	public void addRDatasetTempFile(Dataset dataset, String request, String nameTempFile) throws Exception{
		RServer rServer = component.getClusterManager().getRserver(user);
		String userEnv = user.getLogin() + user.getId();
		rServer.addDatasetTempFile(request, dataset.getName(), nameTempFile, userEnv);
	}

	public String adaptRline(String line) throws Exception {
		RServer rServer = component.getClusterManager().getRserver(user);
		line = rServer.adaptLine(line, user.getLogin() + user.getId());
		return line;
	}

	public List<Parameter> getScriptParameters(String script) {
		List<Parameter> params = new ArrayList<Parameter>();
		//		String script = getText();
		for(Parameter param : (List<Parameter>)getResources(TypeResource.PARAMETER)){
			if(script.contains(param.getParameterName())){
				params.add(param);
			}
		}
		return params;
	}

	private int[][] getLOVValuesSequence(List<Parameter> lovParams){
		int possibilities = 1, tempPossibilities = 1;
		for(Parameter param : lovParams){
			possibilities *= param.getListOfValues().getValues().size();
		}

		int[][] values = new int[possibilities][lovParams.size()];
		int col = 0;
		for(Parameter param : lovParams){

			tempPossibilities *= param.getListOfValues().getValues().size();

			int row = 0;
			int val = 0;
			while(row< possibilities){

				for(int j=0; j< possibilities/tempPossibilities; j++){
					values[row][col] = val;
					row ++;
				}
				if(val == param.getListOfValues().getValues().size()-1){
					val = 0;
				} else {
					val ++;
				}
			}

			col++;
		}

		return values;
	}

	public void loadDatasettoR(Dataset d) throws Exception{
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "air_files/datasets/";
		File file = new File(basePath + d.getName()+".airdata");
		if(file.exists()){
			RServer rServer = component.getClusterManager().getRserver(user);
			RScriptModel script = rServer.loadDatasetTemp(d.getName(), file.getPath().replace("\\", "/"), user.getLogin() + user.getId());
			if(script.isScriptError()){
				d.setIsRLoaded(false);
				component.getVanillaApi().getVanillaPreferencesManager().updateDataset(d);
			} else {
				d.setIsRLoaded(true);
				component.getVanillaApi().getVanillaPreferencesManager().updateDataset(d);
			}
		} else {
			d.setIsRLoaded(false);
			component.getVanillaApi().getVanillaPreferencesManager().updateDataset(d);
		}

	}

	@Override
	public String initWorkflow(Workflow workflow) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public void stopWorkflow(int workflowId, String uuid) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<ActivityLog> getWorkflowRun(WorkflowInstance instance) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<String> getJdbcDrivers() throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public String testConnection(DatabaseServer databaseServer) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ClassRule addOrUpdateClassRule(ClassRule classRule) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public void removeClassRule(ClassRule classRule) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<ClassRule> getClassRules(String identifiant) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<CkanPackage> getCkanDatasets(String ckanUrl) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public Workflow duplicateWorkflow(int workflowId, String name) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public Resource duplicateResource(int resourceId, String name) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ContractIntegrationInformations generateIntegration(IRepositoryContext ctx, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ContractIntegrationInformations generateKPI(IRepositoryContext ctx, KPIGenerationInformations infos) throws Exception {
		throw new Exception("Not implemented");
	}
	
//	@Override
//	public ContractIntegrationInformations generateSimpleKPI(IRepositoryContext ctx, SimpleKPIGenerationInformations infos) throws Exception {
//		throw new Exception("Not implemented");
//	}

	@Override
	public List<String> getValidationSchemas() throws Exception {
		throw new Exception("Not implemented");
	}
	
	@Override
	public void deleteIntegration(IRepositoryContext ctx, ContractIntegrationInformations integrationInfos) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception {
		throw new Exception("Not implemented");
	}
}
