package bpm.gwt.commons.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DatasourceOda;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.IObject.ItemType;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.helper.DocumentHelper;
import bpm.gwt.commons.server.helper.MetadataHelper;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.VanillaServerInformations;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataData;
import bpm.gwt.commons.shared.fmdt.metadata.Row;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.DocumentItem;
import bpm.mdm.model.supplier.MdmDirectory;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.remote.MdmRemote;
import bpm.smart.core.model.AirCube;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.vanilla.map.core.design.IMapDefinitionService.ManageAction;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.beans.VanillaImage;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceArchitect;
import bpm.vanilla.platform.core.beans.data.DatasourceFmdt;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceR;
import bpm.vanilla.platform.core.beans.data.DatasourceSocial;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.data.HbaseTable;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtDimension;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaForm;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.D4C;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItem.TypeD4CItem;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.core.utils.MD5Helper;
import bpm.vanilla.platform.core.utils.SchemaHelper;

public class CommonServiceImpl extends RemoteServiceServlet implements CommonService {

	private static final long serialVersionUID = -5796095198921620090L;

	// private static Logger logger = Logger.getLogger(CommonServiceImpl.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}

	private Locale getLocale() {
		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
	}
	
	@Override
	public Boolean ping() throws ServiceException {
		CommonSession session = getSession();
		if (session == null) {
			return false;
		}
		try {
			IVanillaContext context = CommonConfiguration.getInstance().getRootContext();
			IVanillaAPI vanillaApi = new RemoteVanillaPlatform(context);
			return vanillaApi.getVanillaSystemManager().ping();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String forwardSecurityUrl(String url, String paramLocal) throws ServiceException {
		/*
		 * bpm.vanilla.sessionId/whateva bpm.vanilla.groupId/1
		 * bpm.vanilla.repositoryId/1
		 */

		CommonSession session = getSession();
		try {
			String sessionId = session.getVanillaSessionId();

			if (url.contains("?")) {
				url += "&bpm.vanilla.sessionId=" + sessionId;
			}
			else {
				url += "?bpm.vanilla.sessionId=" + sessionId;
			}
			url += "&bpm.vanilla.groupId=" + session.getCurrentGroup().getId();
			url += "&bpm.vanilla.repositoryId=" + session.getCurrentRepository().getId();
			url += "&locale=" + paramLocal;

			return url;
		} catch (Exception e) {
			String msg = "Failed to generate secured forward url : " + e.getMessage();
			throw new ServiceException(msg);
		}
	}

	@Override
	public List<Comment> getComments(int objectId, int type) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getRepositoryConnection().getDocumentationService().getComments(session.getCurrentGroup().getId(), objectId, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get comments.", e);
		}
	}

	@Override
	public void deleteComment(Comment comment) throws ServiceException {
		CommonSession session = getSession();

		try {
			session.getRepositoryConnection().getDocumentationService().delete(comment);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete the selected comment.", e);
		}
	}

	@Override
	public void addComment(Comment comment, List<Integer> groupIds) throws ServiceException {
		CommonSession session = getSession();
		IRepositoryApi sock = session.getRepositoryConnection();

		// For now we made the begin and end date by hand with an year betwwen
		Date now = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);

		Date yearFromNow = cal.getTime();

		comment.setBeginDate(now);
		comment.setEndDate(yearFromNow);

		try {
			sock.getDocumentationService().addOrUpdateComment(comment, groupIds);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add a comment to that item.", e);
		}
	}

	@Override
	public void editComment(Comment comment, List<Integer> groupIds) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getRepositoryConnection().getDocumentationService().addOrUpdateComment(comment, groupIds);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update the selected comment.", e);
		}
	}

	@Override
	public List<RepositoryDirectory> getRepositoryTree(int typeRepository) throws ServiceException {
		CommonSession session = getSession();
		try {
			Repository repository = null;
			if (typeRepository != -1) {
				repository = new Repository(session.getRepositoryConnection(), typeRepository);
			}
			else {
				repository = new Repository(session.getRepositoryConnection());
			}
			return repository.getRepositoryTree();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the repository.", e);
		}
	}

	@Override
	public List<RepositoryDirectory> getRepositoryTree(String url, String login, String pass, Group group, bpm.vanilla.platform.core.beans.Repository repo, int typeRepository) throws ServiceException {
		try {
			Repository repository = null;
			if (typeRepository != -1) {
				repository = new Repository(new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(url, login, pass), group, repo)), typeRepository);
			}
			else {
				repository = new Repository(new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(url, login, pass), group, repo)));
			}
			return repository.getRepositoryTree();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the repository.", e);
		}
	}

	@Override
	public List<Tree> getAklaboxTree() throws ServiceException {
		CommonSession session = getSession();

		User aklaboxUser = session.getAklaboxUser();
		IVdmManager manager = session.getAklaboxManager();

		try {
			List<IObject> items = manager.getItems(aklaboxUser.getUserId(), null, null, ItemTreeType.MY_DOCUMENTS, ItemType.FOLDER, true);
			
			List<Tree> folders = new ArrayList<>();
			if (items != null) {
				for (IObject item : items) {
					if (item instanceof Tree) {
						folders.add((Tree) item);
					}
				}
			}
			return folders;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// private void getChilds(IVdmManager manager, Tree parent) throws Exception
	// {
	// List<Tree> subItems = manager.getSubdirectories(parent);
	// if (subItems != null) {
	// for (Tree subItem : subItems) {
	// parent.addChild(subItem);
	//
	// getChilds(manager, subItem);
	// }
	// }
	// }

	@Override
	public List<String> getCommonJavascript() throws ServiceException {
		File jsFiles = new File(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "/" + "js_files");

		List<String> result = new ArrayList<String>();

		for (File f : jsFiles.listFiles()) {
			if (f.getPath().endsWith(".js")) {
				try {
					result.add(new String(Files.readAllBytes(Paths.get(f.getPath())), Charset.forName("UTF-8")));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	@Override
	public List<String> getCommonJavascriptFiles() throws ServiceException {
		File jsFiles = new File(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "/" + "js_files");

		File scriptList = new File(jsFiles + "/" + "list_scripts.txt");

		List<String> result = new ArrayList<String>();

		try {
			for (String script : Files.readAllLines(Paths.get(scriptList.getPath()), Charset.forName("UTF-8"))) {

				//FIXME : ATTENTION ATTENTION GROS TRUC DE MERDE
//				String url = "https://libourne2-vanilla.data4citizen.com/VanillaRuntime" + "/freedashboardRuntime" + "/" + script;
//				String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL) + "/freedashboardRuntime" + "/" + script;
				String url = ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaExternalUrl() + "/freedashboardRuntime" + "/" + script;
				url = url.replace("\\", "/");

				result.add(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<Datasource> getDatasources() throws Exception {
		return getSession().getVanillaApi().getVanillaPreferencesManager().getDatasources();
	}

	@Override
	public void deleteDatasource(Datasource datasource) throws Exception {
		getSession().getVanillaApi().getVanillaPreferencesManager().deleteDatasource(datasource);
	}

	@Override
	public Datasource addDatasource(Datasource datasource) throws Exception {
		datasource.setIdAuthor(getSession().getUser().getId());
		return getSession().getVanillaApi().getVanillaPreferencesManager().addDatasource(datasource, true);
	}

	@Override
	public void updateDatasource(Datasource datasource) throws Exception {
		getSession().getVanillaApi().getVanillaPreferencesManager().updateDatasource(datasource);
	}

	@Override
	public List<String> getJdbcDrivers() throws Exception {
		// get drivers
		List<String> drivers = new ArrayList<String>();
		String jdbcXmlFile = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_JDBC_XML_FILE);
		Collection<DriverInfo> infos = ListDriver.getInstance(jdbcXmlFile).getDriversInfo();
		Iterator<DriverInfo> it = infos.iterator();
		while (it.hasNext()) {
			String className = it.next().getClassName();
			if (!drivers.contains(className)) {
				drivers.add(className);
			}
		}

		return drivers;
	}

	@Override
	public UpdateInformations checkUpdates() throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.hasUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to check updates : " + e.getMessage());
		}
	}

	@Override
	public List<Dataset> getDatasets() throws Exception {
		List<Dataset> list = getSession().getVanillaApi().getVanillaPreferencesManager().getDatasets();
		return list;
	}

	@Override
	public void deleteDataset(Dataset dataset) throws Exception {
		getSession().getVanillaApi().getVanillaPreferencesManager().deleteDataset(dataset);
	}

	@Override
	public Dataset addDataset(Dataset dataset) throws Exception {
		dataset.setIdAuthor(getSession().getUser().getId());
		return getSession().getVanillaApi().getVanillaPreferencesManager().addDataset(dataset);
	}

	@Override
	public void updateDataset(Dataset dataset) throws Exception {
		getSession().getVanillaApi().getVanillaPreferencesManager().updateDataset(dataset);
	}

	@Override
	public void addDatasourceAndDataset(int metadataId, String modelName, String packageName, String datasourceName, String datasetName, Dataset dataset) throws ServiceException {
		CommonSession session = getSession();
		bpm.vanilla.platform.core.beans.User user = session.getUser();

		DatasourceFmdt dsFmdt = new DatasourceFmdt();
		dsFmdt.setUser(user.getLogin());
		dsFmdt.setPassword(user.getPassword());
		dsFmdt.setUrl(session.getVanillaRuntimeUrl());
		dsFmdt.setRepositoryId(session.getCurrentRepository().getId());
		dsFmdt.setGroupId(session.getCurrentGroup().getId());
		dsFmdt.setItemId(metadataId);
		dsFmdt.setBusinessModel(modelName);
		dsFmdt.setBusinessPackage(packageName);
		dsFmdt.setDefaultUrl(false);

		Datasource datasource = new Datasource();
		datasource.setName(datasourceName);
		datasource.setType(DatasourceType.FMDT);
		datasource.setObject(dsFmdt);
		datasource.setIdAuthor(user.getId());

		int datasourceId;
		try {
			datasourceId = getSession().getVanillaApi().getVanillaPreferencesManager().addDatasource(datasource, false).getId();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save the datasource : " + e.getMessage());
		}

		dataset.setName(datasetName);
		dataset.setDatasourceId(datasourceId);
		dataset.setIdAuthor(user.getId());
		dataset.setIdAuthor(getSession().getUser().getId());

		try {
			getSession().getVanillaApi().getVanillaPreferencesManager().addDataset(dataset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save the dataset : " + e.getMessage());
		}
	}

	@Override
	public ArrayList<DataColumn> getDataSetMetaData(DatasourceJdbc dts, String query) throws ServiceException {

		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
		try {
			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(dts);
			VanillaPreparedStatement rs = connection.prepareQuery(query);
			ResultSetMetaData rsM = rs.getQueryMetadata(query);

			DataColumn column;
			for (int i = 1; i < rsM.getColumnCount() + 1; i++) {
				column = new DataColumn(rsM.getColumnName(i), rsM.getColumnTypeName(i), rsM.getColumnType(i), rsM.getColumnLabel(i));
				columns.add(column);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
		return columns;
	}

	@Override
	public RScriptModel runRScript(RScriptModel box) throws Exception {
		CommonSession session = getSession();

		RScriptModel script_return;

		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
		String sessionId = adm.connect(session.getUser());
		
		ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
		script_return = manager.executeScriptR(box, null);
		return script_return;
	}

	@Override
	public ArrayList<DataColumn> getRDataSetMetaData(Dataset dataset, String userEnv) throws Exception {
		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
		RScriptModel box = new RScriptModel();
		DatasourceR dtsr = (DatasourceR) dataset.getDatasource().getObject();
		/*
		 * for( name in names(iris)) { colclass <- class(iris[[name]])
		 * if(colclass=="numeric"){ min <- min(iris[[name]]) max <-
		 * max(iris[[name]]) ave <- round(mean(iris[[name]]),4) dev <-
		 * round(sd(iris[[name]]),4) line <- c(paste(min,max,ave,dev,sep='/')) }
		 * if(colclass=="factor" | colclass=="character"){ line <- '' for( stat
		 * in names(summary(iris[[name]]))) { line <- c(paste(line,
		 * c(paste(stat, summary(iris[[name]])[[stat]],sep=':')), sep='/')) }
		 * line <- substr(line,2,nchar(line)) }
		 * 
		 * res <- rbind(res,c(paste(name, colclass, line))) }
		 */
		String script = "library(" + dtsr.getPackageR() + ")\n";
		script += "res<-character()\n";
		script += "data(" + dtsr.getOriginalDatasetR() + ")\n";
		script += "for(name in colnames(" + dtsr.getOriginalDatasetR() + ")) {\n";
		script += "colclass<-class(" + dtsr.getOriginalDatasetR() + "[[name]])\n";
		script += "res<-rbind(res,c(paste(name, colclass)))\n";
		script += "}\n";
		script += "manual_result <- res";
		box.setScript(script);
		box.setOutputs("manual_result".split(" "));
		box.setUserREnv(userEnv);
		RScriptModel result = runRScript(box);

		DataColumn column;
		for (String rsM : result.getOutputVarstoString().get(0).trim().split("\t")) {
			String name = rsM.split(" ")[0];
			String RType = rsM.split(" ")[1];
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

	@Override
	public void setFeedback(boolean allowFeedback) throws ServiceException {
		CommonSession session = getSession();

		CommonConfiguration config = CommonConfiguration.getInstance();
		config.setFeedback(allowFeedback);

		bpm.vanilla.platform.core.beans.User user = session.getUser();
		session.sendInfoUser(user.getLogin(), user.getPassword());
	}

	@Override
	public void sendFeedBackMessage(String mail, String message, boolean isSupport) throws ServiceException {
		CommonSession session = getSession();
		session.sendFeedbackMessage(session.getUser(), mail, message, isSupport);
	}

	@Override
	public RepositoryItem saveItem(SaveItemInformations itemInfo) throws ServiceException {
		CommonSession session = getSession();

		try {
			XStream xstream = new XStream();
			xstream.alias("parameter", Parameter.class);
			String xml = null;
			if(itemInfo.getItem() instanceof String) {
				xml = (String) itemInfo.getItem();
			}
			else {
				xml = xstream.toXML(itemInfo.getItem());
			}
			

			RepositoryItem p = session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(itemInfo.getRepositoryType(), itemInfo.getRepositorySubtype(), itemInfo.getSelectedDirectory(), itemInfo.getName(), itemInfo.getDescription(), "", "", xml, true);

			if (itemInfo.getGroups() != null && p != null) {
				for (Group grp : itemInfo.getGroups()) {
					SecuredCommentObject secComment = new SecuredCommentObject();
					secComment.setGroupId(grp.getId());
					secComment.setObjectId(p.getId());
					secComment.setType(Comment.ITEM);

					session.getRepositoryConnection().getDocumentationService().addSecuredCommentObject(secComment);
				}
			}
			
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save this item : " + e.getMessage());
		}
	}

	@Override
	public RepositoryItem saveItem(String url, String login, String pass, Group group, bpm.vanilla.platform.core.beans.Repository repo, SaveItemInformations itemInfo) throws ServiceException {
		try {
			XStream xstream = new XStream();
			xstream.alias("parameter", Parameter.class);
			String xml = xstream.toXML(itemInfo.getItem());

			IRepositoryApi repoApi = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(url, login, pass), group, repo));
			RepositoryItem p = repoApi.getRepositoryService().addDirectoryItemWithDisplay(itemInfo.getRepositoryType(), itemInfo.getRepositorySubtype(), itemInfo.getSelectedDirectory(), itemInfo.getName(), itemInfo.getDescription(), "", "", xml, true);

			if (itemInfo.getGroups() != null && p != null) {
				for (Group grp : itemInfo.getGroups()) {
					SecuredCommentObject secComment = new SecuredCommentObject();
					secComment.setGroupId(grp.getId());
					secComment.setObjectId(p.getId());
					secComment.setType(Comment.ITEM);

					repoApi.getDocumentationService().addSecuredCommentObject(secComment);
				}
			}
			
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save this item : " + e.getMessage());
		}
	}

	@Override
	public List<DataColumn> getDatasetCsvMetadata(Datasource datasourceCsv) throws ServiceException {
		CommonSession session = getSession();
		try {
			String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
			String sessionId = adm.connect(session.getUser());
			ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
		
			return manager.getDatasourceCsvMetadata(datasourceCsv);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<String> getDatasetCsvVanillaMetadata(Datasource datasourceCsvVanilla) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getDatasourceCsvVanillaMetadata(datasourceCsvVanilla);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<HbaseTable> getDatasourceHBaseMetadataListTables(Datasource datasourceHBase) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getDatasourceHbaseMetadataListTables(datasourceHBase);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<DataColumn> getDatasetHBaseMetadata(String tableName, Datasource datasourceHBase) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getDatasourceHbaseMetadata(tableName, datasourceHBase);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<DataColumn> getDatasetArchitectMetadata(String tableName, Datasource datasourceArchitect) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getDatasourceArchitectMetadata(tableName, datasourceArchitect);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public String getHdfsFile(String path) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getHdfsFile(path);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public String generateCube(List<FmdtDimension> dimensions, List<FmdtData> measures, String url, String request, String title, String description) throws ServiceException {
		CommonSession session = getSession();

		try {
			Properties properties = new Properties();

			properties.put("repository.user", session.getUser().getLogin());
			properties.put("repository.password", session.getUser().getPassword());
			properties.put("repository.id", "null");
			properties.put("repository.item.id", "null");
			properties.put("vanilla.group.id", "null");
			properties.put("vanilla.csv.separator", ",");
			properties.put("csv.file.type.location", "false");
			properties.put("csv.file.path", url);

			DatasourceOda source = new DatasourceOda();
			source.setOdaDatasourceExtensionId("bpm.csv.oda.runtime");
			source.setPublicProperties(properties);

			DataObjectOda object = new DataObjectOda(source);
			object.setOdaDatasetExtensionId("bpm.csv.oda.runtime.dataSet");
			object.setQueryText(request);

			source.addDataObject(object);

			OLAPSchema shema = new OLAPSchema();
			OLAPCube cube = new OLAPCube();

			for (FmdtDimension dimension : dimensions) {
				OLAPDimension olapDimension = new OLAPDimension();
				olapDimension.setName(dimension.getName());
				olapDimension.setCaption(dimension.getName());
				OLAPHierarchy hierarchy = new OLAPHierarchy(dimension.getHierarchyName());
				hierarchy.setCaption(dimension.getHierarchyName());
				for (FmdtData level : dimension.getLevels()) {
					OLAPLevel lev = new OLAPLevel(level.getName());
					DataObjectItem item = new DataObjectItem(level.getName());
					item.setSqlType(level.getSqlType());
					item.setOrigin(level.getName());
					object.addDataObjectItem(item);
					item.setParent(object);
					lev.setItem(item);
					lev.setCaption(level.getLabel());
					lev.setParent(hierarchy);
					hierarchy.addLevel(lev);
				}
				olapDimension.addHierarchy(hierarchy);
				shema.addDimension(olapDimension);
				cube.addDim(olapDimension);
			}

			for (FmdtData measure : measures) {
				OLAPMeasure meas = new OLAPMeasure(measure.getName());
				DataObjectItem origin = new DataObjectItem(measure.getName());
				origin.setOrigin(measure.getName());
				origin.setSqlType(measure.getSqlType());
				object.addDataObjectItem(origin);
				meas.setOrigin(origin);
				meas.setAggregator(measure.getMeasOp());
				meas.setColumnName(measure.getName());
				meas.setCaption(measure.getLabel());
				shema.addMeasure(meas);
				cube.addMes(meas);
			}

			cube.setName(title);
			cube.setDescription(description);
			cube.setFactDataObjectId(source.getDataObjects().get(0).getId());

			shema.addCube(cube);

			FAModel model = new FAModel();
			model.addDataSource(source);
			model.setOLAPSchema(shema);
			model.setName(title);

			XStream stream = new XStream();
			String modelXml = stream.toXML(model);
			session.setCube(modelXml);
			VanillaSession s = session.getSystemManager().getSession(session.getVanillaSessionId());
			s.setCubeXml(modelXml);
			session.getSystemManager().updateSession(s, session.getVanillaSessionId());

			return modelXml;
		} catch (Exception e) {
			String msg = "Failed to generate cube : " + e.getMessage();
			e.printStackTrace();
			throw new ServiceException(msg);
		}
	}

	@Override
	public String generateCSVinR(Dataset dts) throws ServiceException {
		CommonSession session = getSession();

		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
		try {
			String sessionId = adm.connect(session.getUser());
			
			ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
			return manager.generateCSVinR(dts);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public String testJdbcDatasource(DatasourceJdbc datasource) throws ServiceException {

		try {
			return getSession().getVanillaApi().getVanillaPreferencesManager().testJdbcDatasource(datasource);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public void deleteFile(String path) throws ServiceException {
		CommonSession session = getSession();
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
		try {
			String sessionId = adm.connect(session.getUser());
			
			ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
			manager.deleteFile(path);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public void saveAirCube(AirCube airCube) throws ServiceException {
		CommonSession session = getSession();
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
		try {
			String sessionId = adm.connect(session.getUser());
			
			ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
			manager.saveAirCube(airCube);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<AirCube> getCubesbyDataset(int idDataset) throws ServiceException {
		CommonSession session = getSession();
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
		try {
			String sessionId = adm.connect(session.getUser());
			
			ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
			return manager.getCubesbyDataset(idDataset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public HashMap<String, Serializable> loadCube(String xmlModel) throws ServiceException {
		HashMap<String, Serializable> result = new HashMap<String, Serializable>();
		try {
			XStream stream = new XStream();
			FAModel model = (FAModel) stream.fromXML(xmlModel);

			OLAPSchema schema = model.getOLAPSchema();
			OLAPCube cube = schema.getCubes().get(0);

			String description = cube.getDescription();
			List<FmdtData> measures = new ArrayList<FmdtData>();
			List<FmdtDimension> dimensions = new ArrayList<FmdtDimension>();
			for (OLAPMeasure mes : cube.getMes()) {
				FmdtData dat = new FmdtData();
				dat.setLabel(mes.getName());
				dat.setName(mes.getName());
				dat.setMeasOp(mes.getAggregator());
				dat.setSqlType(mes.getOrigin().getSqlType());
				measures.add(dat);
			}

			for (OLAPDimension dim : cube.getDims()) {
				FmdtDimension fmdim = new FmdtDimension();
				fmdim.setLabel(dim.getName());
				// fmdim.setName(dim.getName());
				fmdim.setHierarchyName(dim.getHierarchies().get(0).getName());
				List<FmdtData> levels = new ArrayList<FmdtData>();
				int i = 0;
				for (OLAPLevel olaplev : dim.getHierarchies().get(0).getLevels()) {
					if (i == 0)
						fmdim.setName(olaplev.getName());
					FmdtData lev = new FmdtData();
					lev.setName(olaplev.getName());
					lev.setLabel(olaplev.getName());
					lev.setSqlType(olaplev.getItem().getSqlType());
					levels.add(lev);
					i++;
				}
				fmdim.setLevels(levels);
				dimensions.add(fmdim);
			}

			result.put("desc", description);
			result.put("measures", (Serializable) measures);
			result.put("dimensions", (Serializable) dimensions);
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public List<Dataset> getPermittedDatasets() throws Exception {
		Set<Dataset> result = new HashSet<Dataset>();
		Set<Integer> ids = new HashSet<Integer>();

		CommonSession session = getSession();
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
		String sessionId = adm.connect(session.getUser());
		
		bpm.vanilla.platform.core.beans.User user = session.getUser();
		List<Dataset> globalList = session.getVanillaApi().getVanillaPreferencesManager().getDatasets();
		for (Dataset dts : globalList) {
			if (dts.getIdAuthor() == user.getId()) {
				result.add(dts);
				ids.add(dts.getId());
			}
		}

		ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
		List<AirProject> projects = manager.getVisibleProjects(user.getId());
		for (AirProject proj : projects) {
			if (proj.getLinkedDatasets() != null && proj.getLinkedDatasets() != "") {
				for (String id : proj.getLinkedDatasets().split(";")) {
					if (!ids.contains(Integer.parseInt(id))) {
						Dataset dts = session.getVanillaApi().getVanillaPreferencesManager().getDatasetById(Integer.parseInt(id));
						result.add(dts);
						ids.add(dts.getId());
					}
				}
			}

		}

		return new ArrayList<Dataset>(result);
	}

	@Override
	public List<Datasource> getPermittedDatasources() throws Exception {
		Set<Datasource> result = new HashSet<Datasource>();
		Set<Integer> ids = new HashSet<Integer>();

		CommonSession session = getSession();
		// String vanillaUrl =
		// ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		// String sessionId = session.getServerSessionId();
		bpm.vanilla.platform.core.beans.User user = session.getUser();
		List<Datasource> globalList = session.getVanillaApi().getVanillaPreferencesManager().getDatasources();
		for (Datasource dts : globalList) {
			if (dts.getIdAuthor() == user.getId()) {
				result.add(dts);
				ids.add(dts.getId());
			}
		}

		boolean bfind = false;
		List<Dataset> permittedDatasets = getPermittedDatasets();
		for (Dataset dts : permittedDatasets) {
			bfind = false;
			Datasource sharedSource = getSession().getVanillaApi().getVanillaPreferencesManager().getDatasourceById(dts.getDatasourceId());
			for (Datasource res : result) {
				if (res.getId() == sharedSource.getId()) {
					bfind = true;
					break;
				}
			}
			if (!bfind) {
				result.add(sharedSource);
				ids.add(sharedSource.getId());
			}
		}
		return new ArrayList<Datasource>(result);
	}

	@Override
	public RScript getRScriptbyId(int idScript) throws Exception {
		CommonSession session = getSession();
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		String airRuntimeUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SMART_RUNTIME_URL);

		RemoteAdminManager manag = new RemoteAdminManager(airRuntimeUrl, null, getLocale());
		String sessionId = manag.connect(session.getUser());
		ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale());
		RScript result = manager.getScriptbyId(idScript);
		return result;
	}

	@Override
	public List<String> getIcons() throws ServiceException {
		List<String> fileNames = new ArrayList<>();
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
		try {
			DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(basePath + "/KpiMap_Icons/"));
			for (Path path : directoryStream) {
				fileNames.add(path.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileNames;
	}

	@Override
	public RepositoryItem getItemById(int itemId, String url, String login, String pass, Group group, bpm.vanilla.platform.core.beans.Repository repo) throws ServiceException {
		// CommonSession session = getSession();
		try {
			IRepositoryApi repoApi = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(url, login, pass), group, repo));
			return repoApi.getRepositoryService().getDirectoryItem(itemId);

			// return
			// session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the selected item with id " + itemId);
		}
	}

	@Override
	public ArrayList<DataColumn> getSocialDataSetMetaData(Dataset dataset, Datasource datasource, String userEnv) throws Exception {
		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
		RScriptModel box = new RScriptModel();
		DatasourceSocial dts = (DatasourceSocial) dataset.getDatasource().getObject();

		// construction du script
		HashMap<String, String> params = new HashMap<String, String>();
		if (dts.getParams() != null && !dts.getParams().isEmpty()) {
			for (String param : dts.getParams().split("&&")) {
				params.put(param.split("=")[0], param.split("=")[1]);
			}
		}
		String script = "";

		switch (dts.getType()) {
		case TWITTER:
			script += "library(twitteR)\n";
			// script += "consumer_key    <- 'vIBQsoRkFxLaKIO7FeWa8SNsZ'\n";
			// script +=
			// "consumer_secret <- '5JCwWavmw7S6XjTJYzyCAqahEgzGxgLhzaO6qRyN4EmZob4jX0'\n";
			// script +=
			// "access_token    <- '449516774-pNNEKCn3MvNUAXkk0SRLgW7hIVIpVsaTzzoRc52M'\n";
			// script +=
			// "access_secret   <- 'TJh0ZsJTf3ZMWsuAVok3Vi0DO6r55NHe8gVJOWJoT53aZ'\n";
			// script += "options(httr_oauth_cache=T)\n";
			// script +=
			// "setup_twitter_oauth(consumer_key, consumer_secret,  access_token, access_secret )\n";
			switch (dts.getFunction()) {
			case TWITTER_USERS:
				String users = "c(";
				for (String user : params.get("userList").split(";")) {
					users += "'" + user + "',";
				}
				users = users.substring(0, users.length() - 1) + ")";

				script += "resultList <- lookupUsers(" + users + ")\n";
				script += "resultDF <- twListToDF(resultList)\n";
				break;
			case TWITTER_TRENDS:
				script += "resultDF <- getTrends(" + params.get("woeid") + ")\n";
				break;
			case TWITTER_SEARCH:
				script += "resultList <- searchTwitter('" + params.get("searchString") + "', n=" + params.get("n") + ", lang=NULL, since=" + ((params.get("since") == null) ? "NULL" : "'" + params.get("since") + "'") + ", until=" + ((params.get("until") == null) ? "NULL" : "'" + params.get("until") + "'") + ")\n";
				script += "resultDF <- twListToDF(resultList)\n";
				break;
			case TWITTER_TIMELINE:
				script += "resultList <- userTimeline('" + params.get("user") + "', n=" + params.get("n") + ")\n";
				script += "resultDF <- twListToDF(resultList)\n";
				break;
			}

			break;
		// case FACEBOOK:
		// //nothing
		// break;
		}

		script += "res<-character()\n";
		script += "for(name in colnames(resultDF)) {\n";
		script += "colclass<-class(resultDF[[name]])\n";
		script += "res<-rbind(res,c(paste(name, colclass)))\n";
		script += "}\n";
		script += "manual_result <- res";
		box.setScript(script);
		box.setOutputs("manual_result".split(" "));
		box.setUserREnv(userEnv);
		RScriptModel result = runRScript(box);

		DataColumn column;
		for (String rsM : result.getOutputVarstoString().get(0).trim().split("\t")) {
			String name = rsM.split(" ")[0];
			String RType = rsM.split(" ")[1];
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

	@Override
	public RepositoryItem getItemById(int itemId) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the selected item with id " + itemId);
		}
	}

	@Override
	public void addDirectory(String name, String description, RepositoryDirectory parent) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().addDirectory(name, description, parent);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add a folder");
		}
	}

	@Override
	public void deleteDirectory(RepositoryDirectory selectedDirectory) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().delete(selectedDirectory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add a folder");
		}
	}

	@Override
	public List<bpm.vanilla.platform.core.beans.User> getUsers() throws ServiceException {
		CommonSession session = getSession();
		try {
			List<bpm.vanilla.platform.core.beans.User> result = session.getVanillaApi().getVanillaSecurityManager().getUsers();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load users.", e);
		}
	}

	@Override
	public List<Metric> getAllMetrics() throws ServiceException {
		IFreeMetricsManager manager = new RemoteFreeMetricsManager(getSession().getVanillaContext());

		try {
			List<Metric> result = manager.getMetrics();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load metrics.", e);
		}
	}

	@Override
	public AxisInfo browseAxis(Axis axis) throws Exception {
		IFreeMetricsManager manager = new RemoteFreeMetricsManager(getSession().getVanillaContext());
		return manager.getLoaderAxe(axis.getId());
	}

	@Override
	public void indexFile(GedInformations gedInfos) throws ServiceException {

		CommonSession session = getSession();
		session.setGedInformations(gedInfos);

	}

	@Override
	public List<DocumentDefinitionDTO> getAllGEDDocuments() throws ServiceException {

		CommonSession session = getSession();
		List<DocumentDefinitionDTO> documents = new ArrayList<DocumentDefinitionDTO>();

		List<Integer> groupIds = new ArrayList<Integer>();
		groupIds.add(session.getCurrentGroup().getId());

		int repositoryId = 0;
		if (session.getCurrentRepository() != null) {
			repositoryId = session.getCurrentRepository().getId();
		}
		else {
			repositoryId = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID));
		}

		List<GedDocument> docs;
		try {
			docs = session.getGedComponent().getDocuments(groupIds, repositoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Failed to get existing documents", e);
		}
		if (docs == null) {
			return documents;
		}

		for (GedDocument doc : docs) {
			DocumentDefinitionDTO d = DocumentHelper.transformGedDocumentToDto(doc);
			if (doc.getDocumentVersions() != null) {
				for (DocumentVersion version : doc.getDocumentVersions()) {
					if ((version.getPeremptionDate() == null || version.getPeremptionDate().after(new Date()))) {
						String key = session.addDocumentVersion(version);

						d.addVersion(DocumentHelper.transformDocumentVersionToDto(version, d, key));
					}
				}
			}

			documents.add(d);
		}

		Collections.sort(documents);

		return documents;
	}

	@Override
	public List<VanillaImage> getImages() throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getVanillaApi().getImageManager().getImageList(null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve images : " + e.getMessage());
		}
	}

	@Override
	public VanillaImage getImage(int imageId) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getVanillaApi().getImageManager().getImage(imageId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve image with id = '" + imageId + "' : " + e.getMessage());
		}
	}
	
	@Override
	public Datasource getDatasourceById(int datasourceId) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getDatasourceById(datasourceId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the data : " + e.getMessage());
		}
	}

	@Override
	public MetadataData getJdbcData(int datasourceId, int datasetId, int limit) throws ServiceException {
		CommonSession session = getSession();

		try {
			Datasource datasource = session.getVanillaApi().getVanillaPreferencesManager().getDatasourceById(datasourceId);
			Dataset dataset = session.getVanillaApi().getVanillaPreferencesManager().getDatasetById(datasetId);

			DatasourceJdbc jdbcSource = (DatasourceJdbc) datasource.getObject();
			String query = dataset.getRequest();

			VanillaJdbcConnection jdbcConnection = ConnectionManager.getInstance().getJdbcConnection(jdbcSource);
			VanillaPreparedStatement rs = jdbcConnection.prepareQuery(query);
			ResultSet jdbcResult = rs.executeQuery(query);
			ResultSetMetaData jdbcMeta = jdbcResult.getMetaData();

			List<DatabaseColumn> columns = new ArrayList<>();
			for (int i = 1; i <= jdbcMeta.getColumnCount(); i++) {
				DatabaseColumn col = new DatabaseColumn();
				col.setName(jdbcMeta.getColumnName(i));
				columns.add(col);
			}

			MetadataData data = new MetadataData(columns);

			while (jdbcResult.next()) {
				Row row = new Row();
				for (DatabaseColumn col : columns) {

					String value = MetadataHelper.getValue(col.getName(), col.getType(), jdbcResult);
					row.addValue(value);
				}
				data.addRow(row);
			}

			return data;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the data : " + e.getMessage());
		}
	}

	@Override
	public PublicUrl addPublicUrl(PublicUrl publicUrl, HashMap<String, String> parameters) throws ServiceException {
		CommonSession session = getSession();

		try {
			String key = MD5Helper.encode(publicUrl.getCreationDate().toString() + new Object().hashCode());
			publicUrl.setPublicKey(key);

			int publicUrlId = session.getVanillaApi().getVanillaExternalAccessibilityManager().savePublicUrl(publicUrl);

			if (parameters != null) {
				for (String paramName : parameters.keySet()) {
					PublicParameter pp = new PublicParameter();
					pp.setPublicUrlId(publicUrlId);
					pp.setParameterName(paramName);
					pp.setParameterValue(parameters.get(paramName));

					int ppId = session.getVanillaApi().getVanillaExternalAccessibilityManager().addPublicParameter(pp);
					pp.setId(ppId);
				}
			}

			return session.getVanillaApi().getVanillaExternalAccessibilityManager().getPublicUrlById(publicUrlId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save this public url: " + e.getMessage());
		}
	}

	@Override
	public void deletePublicUrl(PublicUrl publicUrl) throws ServiceException {
		CommonSession session = getSession();

		try {
			session.getVanillaApi().getVanillaExternalAccessibilityManager().deletePublicUrl(publicUrl.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete this public url: " + e.getMessage());
		}
	}

	@Override
	public List<PublicUrl> getPublicUrls(int itemId, TypeURL typeUrl) throws ServiceException {
		CommonSession session = getSession();

		int repositoryId = session.getCurrentRepository().getId();

		try {
			return session.getVanillaApi().getVanillaExternalAccessibilityManager().getPublicUrls(itemId, repositoryId, typeUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get public urls: " + e.getMessage());
		}
	}

	@Override
	public List<CkanPackage> getCkanDatasets(String ckanUrl, String organisation) throws ServiceException {
		try {
			CkanHelper ckanHelper = new CkanHelper(ckanUrl, organisation, null);
			if (organisation != null && !organisation.isEmpty()) {
				return ckanHelper.getCkanPackages(organisation);
			}
			else {
				return ckanHelper.getCkanPackages();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get packages from CKAN: " + e.getMessage());
		}
	}

	@Override
	public List<RepositoryItem> getItemsByType(int itemType, int subtype) throws ServiceException {
		CommonSession session = getSession();
		try {
			return new ArrayList<>(new Repository(session.getRepositoryConnection(), itemType, subtype).getAllItems());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get repository items: " + e.getMessage());
		}
	}

	@Override
	public Form loadForm(RepositoryItem repositoryItem) throws ServiceException {
		CommonSession session = getSession();
		try {
			String model = session.getRepositoryConnection().getRepositoryService().loadModel(repositoryItem);
			Form form = (Form) new XStream().fromXML(model);
			form.setId(repositoryItem.getId());
			return form;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load model: " + e.getMessage());
		}
	}

	@Override
	public DatasetResultQuery executeDataset(Dataset dataset) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getResultQuery(dataset);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute query: " + e.getMessage());
		}
	}

	@Override
	public void saveFormValues(Form form) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getVanillaApi().getVanillaPreferencesManager().executeFormQuery(form);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute query: " + e.getMessage());
		}
	}

	@Override
	public List<Map<String, FormField>> executeFormSearchQuery(Form form) throws ServiceException {
		StringBuilder buf = new StringBuilder();
		buf.append("Select * From ");
		buf.append(form.getTableName());
		buf.append(" Where ");
		boolean first = true;
		for (FormField f : form.getFields()) {
			if (f.getValue() != null) {
				if (first) {
					first = false;
				}
				else {
					buf.append(" and ");
				}
				buf.append(f.getColumnName());
				buf.append(" = ");
				String value = f.getValue();
				try {
					Double.parseDouble(value);
					buf.append(value);
				} catch (Exception e) {
					buf.append("'" + value + "'");
				}
			}
		}
		CommonSession session = getSession();

		try {
			return session.getVanillaApi().getVanillaPreferencesManager().executeFormSearchQuery(buf.toString(), form);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute query: " + e.getMessage());
		}
	}

	@Override
	public void updateFormValues(Form form, Map<String, FormField> editedLine) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getVanillaApi().getVanillaPreferencesManager().executeFormUpdateQuery(form, editedLine);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute query: " + e.getMessage());
		}
	}

	@Override
	public void updateForm(Form form) throws ServiceException {
		CommonSession session = getSession();
		try {
			XStream xstream = new XStream();
			xstream.alias("parameter", Parameter.class);
			String xml = xstream.toXML(form);
			session.getRepositoryConnection().getRepositoryService().updateModel(session.getRepositoryConnection().getRepositoryService().getDirectoryItem(form.getId()), xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to execute query: " + e.getMessage());
		}
	}

	@Override
	public List<DocumentItem> getLinkedItems(int contractId) throws ServiceException {
		CommonSession session = getSession();
		try {
			IMdmProvider mdmProvider = getMdmProvider();
			List<DocumentItem> docItems = mdmProvider.getDocumentItems(contractId);
			if (docItems != null) {
				for (DocumentItem docItem : docItems) {
					RepositoryItem item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(docItem.getItemId());
					docItem.setItem(item);
				}
			}
			return docItems;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get all linked items: " + e.getMessage());
		}
	}
	
	private IMdmProvider getMdmProvider() throws ServiceException {
		CommonSession session = getSession();
		
		String login = session.getUser().getLogin();
		String password = session.getUser().getPassword();
		String vanillaUrl = session.getVanillaRuntimeUrl();
		
		return new MdmRemote(login, password, vanillaUrl);
	}
	
	@Override
	public List<Supplier> getSuppliers() throws ServiceException {
		CommonSession session = getSession();

		int groupId = -1;
		if (session.getCurrentGroup() != null) {
			groupId = session.getCurrentGroup().getId();
		}
		else {
			groupId = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_GROUP_ID));
		}
		try {
			IMdmProvider mdmRemote = getMdmProvider();
			IGedComponent gedComponent = session.getGedComponent();
			IVanillaSecurityManager securityManager = session.getVanillaApi().getVanillaSecurityManager();
			return getSuppliers(gedComponent, securityManager, mdmRemote, groupId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get suppliers : " + e.getMessage());
		}
	}
	
	@Override
	public List<Supplier> getSuppliersByServer(String login, String pass, String url) throws ServiceException {
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(url, login, pass);
		IGedComponent gedComponent = new RemoteGedComponent(url, login, pass);
		IMdmProvider mdmProvider = new MdmRemote(login, pass, url);

		try {
			return getSuppliers(gedComponent, vanillaApi.getVanillaSecurityManager(), mdmProvider, null);
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get suppliers : " + e.getMessage());
		}
	}
	
	private List<Supplier> getSuppliers(IGedComponent gedComponent, IVanillaSecurityManager securityManager, IMdmProvider mdmProvider, Integer groupId) throws Exception {
		List<Supplier> suppliers = groupId != null ? mdmProvider.getSuppliersByGroupId(groupId) : mdmProvider.getSuppliers();
		if (suppliers != null) {
			for (Supplier supplier : suppliers) {
				if (supplier.getContracts() != null) {
					List<Contract> supplierContracts = new ArrayList<>();
					for (Contract contract : supplier.getContracts()) {
						if (contract.getDocId() != null) {
							GedDocument doc = gedComponent.getDocumentDefinitionById(contract.getDocId());
							if (doc.getDocumentVersions() != null) {
								for (DocumentVersion version : doc.getDocumentVersions()) {
									if (version.getModifiedBy() > 0) {
										bpm.vanilla.platform.core.beans.User user = securityManager.getUserById(version.getModifiedBy());
										version.setModificator(user);
									}
								}
							}

							contract.setFileVersions(doc);
						}
						
						contract.setParent(supplier);
						supplierContracts.add(contract);
					}
					supplier.setContracts(supplierContracts);
				}
			}
		}
		return suppliers;
	}

	@Override
	public void createDataPreparation(String name, Contract contract, String separator) throws ServiceException {
		try {
			CommonSession session = getSession();
			bpm.vanilla.platform.core.beans.User user = session.getUser();
			
			DatasourceArchitect dsArch = new DatasourceArchitect();
			dsArch.setUrl(session.getVanillaRuntimeUrl());
			dsArch.setUser(user.getLogin());
			dsArch.setPassword(user.getPassword());
			
			dsArch.setSupplierId(contract.getParent().getId());
			dsArch.setContractId(contract.getId());
			dsArch.setHasInput(contract.hasInput());
			dsArch.setSeparator(separator);
	
			Datasource datasource = new Datasource();
			datasource.setName(name);
			datasource.setType(DatasourceType.ARCHITECT);
			datasource.setIdAuthor(user.getId());
			datasource.setObject(dsArch);
			
			Dataset dataset = getDatasetFromArchitectFile(session, datasource);
			dataset.setName(name);
			dataset.setDatasource(datasource);
			dataset = getSession().getVanillaApi().getVanillaPreferencesManager().addDataset(dataset);
	
			session.createDataPreparation(name, dataset.getId());
		} catch(Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create DataPreparation : " + e.getMessage());
		}
	}

	private Dataset getDatasetFromArchitectFile(CommonSession session, Datasource datasourceArchitect) throws Exception {
		List<DataColumn> columns = getDatasetArchitectMetadata("", datasourceArchitect);
		
		StringBuffer buf = new StringBuffer();
		if (columns != null) {
			for (DataColumn col : columns) {
				buf.append(col.getColumnLabel() + ",");
			}
		}
		
		Dataset dataset = new Dataset();
		dataset.setMetacolumns(columns);
		dataset.setRequest(buf.toString());
		return dataset;
	}

	@Override
	public Contract addContract(Supplier supplier, Contract contract) throws ServiceException {
		try {
			supplier.addContract(contract);
			contract.setParent(supplier);

			getMdmProvider().addSupplier(supplier);
			
			List<Supplier> suppliers = getMdmProvider().getSuppliers();
			Supplier selectedSupplier = suppliers.get(suppliers.size() - 1);
			return selectedSupplier.getContracts().get(selectedSupplier.getContracts().size() - 1);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void confirmUpload(Contract contract, String name, String filePath) throws ServiceException {
		CommonSession session = getSession();
		int userId = session.getUser().getId();
		int repositoryId = session.getCurrentRepository().getId();

		try {
			GedDocument doc = contract.getFileVersions();
			if (doc != null) {
				String format = extractFormat(contract, doc, filePath);
				InputStream newVersion = session.getPendingNewVersion();

				session.getGedComponent().addVersionToDocumentThroughServlet(doc.getId(), format, newVersion);
			}
			else {
				String format = DocumentHelper.getFormat(filePath);
				InputStream newVersion = session.getPendingNewVersion();
				
				List<Integer> groupIds = getMdmProvider().getSupplierSecurity(contract.getParent().getId());

				doc = session.getGedComponent().createDocumentThroughServlet(name, format, userId, groupIds, repositoryId, newVersion);
				contract.setFileVersions(doc);
			}

			contract.setVersionId(null);
			getMdmProvider().addContract(contract);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to add a new version: " + e.getMessage());
		}
	}
	
	private String extractFormat(Contract contract, GedDocument doc, String filePath) {
		if (doc != null && doc.getCurrentVersion(contract.getVersionId()) != null && doc.getCurrentVersion(contract.getVersionId()).getFormat() != null) {
			return doc.getLastVersion().getFormat();
		}
		else {
			return DocumentHelper.getFormat(filePath);
		}
	}

	@Override
	public List<MapServer> getMapServers() throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getMapService().getMapServers();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get map servers: " + e.getMessage());
		}
	}

	@Override
	public MapServer manageMapServer(MapServer server, ManageAction action) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getMapService().manageMapServer(server, action);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to manage map server: " + e.getMessage());
		}
	}
	
	@Override
	public List<MapLayer> loadLayers(MapServer server) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getMapService().getLayers(server);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get map layers: " + e.getMessage());
		}
	}
	
	@Override
	public List<MapServer> getArcgisServices(MapServer server) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getMapService().getArcgisServices(server);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get map layers: " + e.getMessage());
		}
	}

	@Override
	public String getFormat(Contract contract) throws ServiceException {
		CommonSession session = getSession();

		GedDocument doc = null;
		if (contract.getDocId() != null) {
			try {
				doc = session.getGedComponent().getDocumentDefinitionById(contract.getDocId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (doc != null && doc.getCurrentVersion(contract.getVersionId()) != null && doc.getCurrentVersion(contract.getVersionId()).getFormat() != null) {
			return doc.getLastVersion().getFormat();
		}
		return "csv";
	}

	@Override
	public List<MetaLink> getMetaLinks(VanillaServerInformations server, int itemId, TypeMetaLink type, boolean loadValue) throws ServiceException {
		CommonSession session = getSession();
		try {
			IRepositoryApi repositoryApi = null;
			if (server != null) {
				IRepositoryContext ctx = new BaseRepositoryContext(new BaseVanillaContext(server.getUrl(), server.getLogin(), server.getPassword()), server.getEmptyGroup(), server.getEmptyRepo());
				repositoryApi = new RemoteRepositoryApi(ctx);
			}
			else {
				repositoryApi = session.getRepositoryConnection();
			}
			
			return repositoryApi.getMetaService().getMetaLinks(itemId, type, loadValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get meta for this item : " + e.getMessage());
		}
	}

	@Override
	public List<Meta> getMetaByForm(VanillaServerInformations server, int formId) throws ServiceException {
		CommonSession session = getSession();
		try {
			IRepositoryApi repositoryApi = null;
			if (server != null) {
				IRepositoryContext ctx = new BaseRepositoryContext(new BaseVanillaContext(server.getUrl(), server.getLogin(), server.getPassword()), server.getEmptyGroup(), server.getEmptyRepo());
				repositoryApi = new RemoteRepositoryApi(ctx);
			}
			else {
				repositoryApi = session.getRepositoryConnection();
			}
			
			return repositoryApi.getMetaService().getMetaByForm(formId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get meta for this form id '" + formId + "' : " + e.getMessage());
		}
	}

	@Override
	public void manageMetaValues(VanillaServerInformations server, List<MetaLink> values) throws ServiceException {
		CommonSession session = getSession();
		try {
			IRepositoryApi repositoryApi = null;
			if (server != null) {
				IRepositoryContext ctx = new BaseRepositoryContext(new BaseVanillaContext(server.getUrl(), server.getLogin(), server.getPassword()), server.getEmptyGroup(), server.getEmptyRepo());
				repositoryApi = new RemoteRepositoryApi(ctx);
			}
			else {
				repositoryApi = session.getRepositoryConnection();
			}
			
			repositoryApi.getMetaService().manageMetaValues(values, bpm.vanilla.platform.core.ManageAction.SAVE_OR_UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update meta values : " + e.getMessage());
		}
	}

	@Override
	public List<ItemMetadataTableLink> getMetadataLinks(int itemId) throws ServiceException {
		CommonSession session = getSession();
		try {
			List<ItemMetadataTableLink> links = session.getRepositoryConnection().getRepositoryService().getMetadataLinks(itemId);
			if (links != null) {
				for (ItemMetadataTableLink link : links) {
					Datasource datasource = session.getVanillaApi().getVanillaPreferencesManager().getDatasourceById(link.getDatasourceId());
					link.setDatasource(datasource);
				}
			}
			return links;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get metadata links : " + e.getMessage());
		}
	}
	
	@Override
	public List<MetaForm> getMetaForms() throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getRepositoryConnection().getMetaService().getMetaForms();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get metadata links : " + e.getMessage());
		}
	}

	@Override
	public void addItemMetadataTableLink(ItemMetadataTableLink link) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().addItemMetadataTableLink(link);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get metadata links : " + e.getMessage());
		}
	}

	@Override
	public void deleteItemMetadataTableLink(ItemMetadataTableLink link) throws ServiceException {
		CommonSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().deleteItemMetadataTableLink(link);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get metadata links : " + e.getMessage());
		}
	}

	@Override
	public ArrayList<DataColumn> getDataSetMetaData(DatabaseServer databaseServer) throws ServiceException {
		DatasourceJdbc datasource = new DatasourceJdbc();
		datasource.setDriver(databaseServer.getDriverJdbc());
		datasource.setFullUrl(true);
		datasource.setUrl(databaseServer.getDatabaseUrlDisplay());
		datasource.setUser(databaseServer.getLogin());
		datasource.setPassword(databaseServer.getPassword());
		
		String query = databaseServer.getQueryDisplay();
		
		ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
		try {
			VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(datasource);
			VanillaPreparedStatement rs = connection.prepareQuery(query);
			ResultSetMetaData rsM = rs.getQueryMetadata(query);

			DataColumn column;
			for (int i = 1; i < rsM.getColumnCount() + 1; i++) {
				column = new DataColumn(rsM.getColumnName(i), rsM.getColumnTypeName(i), rsM.getColumnType(i), rsM.getColumnLabel(i));
				columns.add(column);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage(), e);
		}
		return columns;
	}
	
	@Override
	public List<ClassDefinition> loadSchemaValidations() throws ServiceException {
		try {
			String schemaPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/";
			List<Path> jsonSchemas = Files.walk(Paths.get(schemaPath))
                    .filter(p -> p.toString().endsWith(".json"))
                    .collect(Collectors.toList());
			
			List<ClassDefinition> classes = new ArrayList<ClassDefinition>();
			for (Path schema : jsonSchemas) {
				//Get filename without extension
				String name = schema.getFileName().toString().replaceFirst("[.][^.]+$", "");

				ClassDefinition classSchema = SchemaHelper.loadSchema(schema.toString(), name, false);
				classes.add(classSchema);
			}
			return classes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load class: " + e.getMessage());
		}
	}

	@Override
	public List<D4C> getD4CDefinitions() throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getVanillaApi().getExternalManager().getD4CDefinitions();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get D4C definitions : " + e.getMessage());
		}
	}
	
	public List<MdmDirectory> getMdmDirectories() throws ServiceException {
		try {
			return getMdmProvider().getDirectories(null, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get mdm directory : " + e.getMessage());
		}
	}

	@Override
	public MdmDirectory saveMdmDirectory(MdmDirectory directory) throws ServiceException {
		try {
			return getMdmProvider().saveOrUpdateDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get mdm directory : " + e.getMessage());
		}
	}
	
	@Override
	public void deleteMdmDirectory(MdmDirectory directory) throws ServiceException {
		try {
			getMdmProvider().removeDirectory(directory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get mdm directory : " + e.getMessage());
		}
	}

	@Override
	public HashMap<String, HashMap<String, List<D4CItem>>> getD4CItems(int parentId, TypeD4CItem type) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getVanillaApi().getExternalManager().getD4cItems(parentId, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get D4C items : " + e.getMessage());
		}
	}
	
	@Override
	public D4C manageD4CDefinition(D4C item, bpm.vanilla.platform.core.ManageAction action) throws ServiceException {
		CommonSession session = getSession();
		try {
			return (D4C) session.getVanillaApi().getGlobalManager().manageItem(item, action);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to manage item : " + e.getMessage());
		}
	}
	
	@Override
	public D4CItem manageD4CItem(D4CItem item, bpm.vanilla.platform.core.ManageAction action) throws ServiceException {
		CommonSession session = getSession();
		try {
			return (D4CItem) session.getVanillaApi().getGlobalManager().manageItem(item, action);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to manage item : " + e.getMessage());
		}
	}

	@Override
	public List<DocumentSchema> getLinkedSchemas(int contractId) throws ServiceException {
		try {
			IMdmProvider mdmProvider = getMdmProvider();
			return mdmProvider.getDocumentSchemas(contractId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get all linked schemas: " + e.getMessage());
		}
	}
}
