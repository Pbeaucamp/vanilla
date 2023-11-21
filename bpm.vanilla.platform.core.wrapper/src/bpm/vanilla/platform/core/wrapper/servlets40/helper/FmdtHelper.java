package bpm.vanilla.platform.core.wrapper.servlets40.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Locale;

import bpm.document.management.core.utils.DocumentUtils;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.GrantException;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.Formula;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.office.core.ExcelFunctionsUtils;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;

public class FmdtHelper {

	private static IVanillaAPI vanillaApi;

	public static String loadGroupAndRepositories(String user, String pass, Session session) throws Exception {

		IVanillaAPI api = getVanillaApi();

		User vanillaUser = api.getVanillaSecurityManager().authentify("", user, pass, false);

		List<Repository> repositories = api.getVanillaRepositoryManager().getUserRepositories(user);
		List<Group> groups = api.getVanillaSecurityManager().getGroups(vanillaUser);

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADGRPREPANDREPOSITORIES);

		Element reps = root.addElement("repositories");
		for (Repository rep : repositories) {
			Element repo = reps.addElement("repository");
			repo.addElement("name").setText(rep.getName());
			repo.addElement("id").setText(rep.getId() + "");
		}

		Element elemGrps = root.addElement("groups");
		for (Group grp : groups) {
			Element elemGrp = elemGrps.addElement("group");
			elemGrp.addElement("name").setText(grp.getName());
			elemGrp.addElement("id").setText(grp.getId() + "");
		}

		session.setUser(user);
		session.setPassword(pass);

		return doc.asXML();
	}

	public static String saveGroupAndRepositories(String repId, String groupId, String language, Session session) throws Exception {
		IVanillaAPI api = getVanillaApi();

		Repository repo = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repId));
		Group grp = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, grp, repo);

		IRepositoryApi sock = new RemoteRepositoryApi(ctx);

		Locale locale = new Locale(language);

		session.setRepository(repo);
		session.setGroup(grp);
		session.setLocale(locale);
		session.setSock(sock);

		return "success";
	}

	public static String loadMetadata(Session session) throws Exception {

		IVanillaAPI api = initVanillaApi();

		Repository repo = session.getRepository();
		Group grp = session.getGroup();

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, grp, repo);

		IRepositoryApi sock = new RemoteRepositoryApi(ctx);

		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FMDT_TYPE);

		List<RepositoryItem> fmdts = new ArrayList<RepositoryItem>();

		findChilds(rep.getRootDirectories(), fmdts, rep);

		Collections.sort(fmdts, new Comparator<RepositoryItem>() {
			@Override
			public int compare(RepositoryItem o1, RepositoryItem o2) {
				return o1.getItemName().compareTo(o2.getItemName());
			}
		});

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADMETADATA);
		Element items = root.addElement("items");
		for (RepositoryItem item : fmdts) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("id").setText(item.getId() + "");
			elemItem.addElement("name").setText(item.getItemName());
		}

		return doc.asXML();
	}

	private static void findChilds(List<RepositoryDirectory> directories, List<RepositoryItem> fmdts, IRepository rep) throws Exception {

		if (directories == null) {
			return;
		}

		for (RepositoryDirectory dir : directories) {
			findChilds(rep.getChildDirectories(dir), fmdts, rep);

			for (RepositoryItem item : rep.getItems(dir)) {
				fmdts.add(item);
			}
		}
	}

	public static String loadBusinessModel(String repositoryId, Session session) throws Exception {

		IVanillaAPI api = getVanillaApi();

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository());

		IRepositoryApi sock = new RemoteRepositoryApi(ctx);

		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FMDT_TYPE);
		RepositoryItem fmdt = rep.getItem(Integer.parseInt(repositoryId));

		String model = sock.getRepositoryService().loadModel(fmdt);

		Collection<IBusinessModel> col = MetaDataReader.read(session.getGroup().getName(), IOUtils.toInputStream(model, "UTF-8"), null, true);

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADMODEL);
		Element items = root.addElement("items");

		for (IBusinessModel item : col) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("name").setText(item.getName());
		}

		return doc.asXML();
	}

	public static String loadBusinessPackage(String modelName, String repositoryId, Session session) throws Exception {

		IVanillaAPI api = getVanillaApi();

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository());

		IRepositoryApi sock = new RemoteRepositoryApi(ctx);

		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FMDT_TYPE);

		String model = sock.getRepositoryService().loadModel(rep.getItem(Integer.parseInt(repositoryId)));

		Collection<IBusinessModel> col = MetaDataReader.read(session.getGroup().getName(), IOUtils.toInputStream(model, "UTF-8"), null, true);

		IBusinessModel selectedModel = null;

		for (IBusinessModel businessModel : col) {
			if (businessModel.getName().equals(modelName)) {
				selectedModel = businessModel;
				break;
			}
		}
		List<IBusinessPackage> packages = selectedModel.getBusinessPackages(session.getGroup().getName());

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
		Element items = root.addElement("items");

		for (IBusinessPackage item : packages) {
			if (item.isExplorable()) {
				Element elemItem = items.addElement("item");
				elemItem.addElement("name").setText(item.getName());
			}
		}

		return doc.asXML();
	}

	public static String saveModelPackage(String repositoryId, String modelName, String packageName, Session session) throws Exception {

		IVanillaAPI api = getVanillaApi();

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository());

		IRepositoryApi sock = new RemoteRepositoryApi(ctx);

		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.FMDT_TYPE);

		RepositoryItem fmdt = rep.getItem(Integer.parseInt(repositoryId));
		session.setItem(fmdt);

		String model = sock.getRepositoryService().loadModel(fmdt);

		Collection<IBusinessModel> col = MetaDataReader.read(session.getGroup().getName(), IOUtils.toInputStream(model, "UTF-8"), null, false);

		IBusinessModel selectedModel = null;

		for (IBusinessModel businessModel : col) {
			if (businessModel.getName().equals(modelName)) {
				selectedModel = businessModel;
				break;
			}
		}
		session.setModel((BusinessModel) selectedModel);
		List<IBusinessPackage> packages = selectedModel.getBusinessPackages(session.getGroup().getName());
		IBusinessPackage selectePackage = null;

		for (IBusinessPackage businessPackage : packages) {
			if (businessPackage.getName().equals(packageName)) {
				selectePackage = businessPackage;
				break;
			}
		}
		session.setBusinessPackage((BusinessPackage) selectePackage);

		return "success";
	}

	public static String getServerConnection(Session session) {

		SQLConnection connection = null;

		Collection<AbstractDataSource> dataSources = (session.getModel()).getModel().getDataSources();
		for (AbstractDataSource dataSource : dataSources) {
			connection = (SQLConnection) dataSource.getConnection();
			break;
		}

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_SAVEMODELPACKAGE);
		Element elemConn = root.addElement("connection");

		if (connection != null) {
			elemConn.addElement("name").setText(connection.getName());
			elemConn.addElement("login").setText(connection.getUsername());
			elemConn.addElement("password").setText(connection.getPassword());
			elemConn.addElement("driver").setText(connection.getDriverName());
			if (!connection.isUseFullUrl()) {
				elemConn.addElement("useFullUrl").setText("false");
				elemConn.addElement("host").setText(connection.getHost());
				elemConn.addElement("port").setText(connection.getPortNumber());
				elemConn.addElement("database").setText(connection.getDataBaseName());
			} else {
				elemConn.addElement("useFullUrl").setText("true");
				elemConn.addElement("fullUrl").setText(connection.getFullUrl());
			}
		}

		return doc.asXML();
	}

	public static String loadTableName(Session session) throws Exception {

		BusinessPackage businessPackage = session.getBusinessPackage();

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
		Element items = root.addElement("items");

		List<IBusinessTable> tables = businessPackage.getExplorableTables(session.getGroup().getName());
		for (IBusinessTable table : tables) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("name").setText(table.getName());
			elemItem.addElement("field").setText(((AbstractBusinessTable) table).getOuputName(session.getLocale()));
		}
		return doc.asXML();
	}

	public static String loadPrompt(Session session) throws Exception {

		BusinessPackage businessPackage = session.getBusinessPackage();
		List<IResource> resources = businessPackage.getResources(session.getGroup().getName());

		List<Prompt> listPrompt = new ArrayList<Prompt>();

		for (IResource resource : resources) {
			if (resource instanceof Prompt) {
				listPrompt.add((Prompt) resource);
			}
		}

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
		Element items = root.addElement("items");

		for (Prompt item : listPrompt) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("name").setText(item.getName());
			elemItem.addElement("field").setText(item.getGotoDataStreamElementName());
			elemItem.addElement("operator").setText(item.getOperator());
			elemItem.addElement("value").setText("?");
			elemItem.addElement("tableName").setText(item.getOrigin().getDataStream().getName());
			elemItem.addElement("origin").setText(item.getOrigin().getDataStream().getName());
		}

		return doc.asXML();
	}

	public static String loadfilter(Session session) throws Exception {

		BusinessPackage businessPackage = session.getBusinessPackage();
		List<IResource> resources = businessPackage.getResources(session.getGroup().getName());

		List<Filter> listFilter = new ArrayList<Filter>();

		for (IResource resource : resources) {
			if (resource instanceof Filter) {
				listFilter.add((Filter) resource);
			}
		}

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
		Element items = root.addElement("items");

		for (Filter item : listFilter) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("name").setText(item.getName());
			elemItem.addElement("field").setText(item.getDataStreamElementName());
			elemItem.addElement("operator").setText("IN");
			elemItem.addElement("value").setText(convertListToString(item.getValues()));
			elemItem.addElement("tableName").setText(item.getOrigin().getDataStream().getName());
			elemItem.addElement("origin").setText(item.getOrigin().getDataStream().getName());
		}

		return doc.asXML();
	}

	public static String loadComplexfilter(Session session) throws Exception {

		BusinessPackage businessPackage = session.getBusinessPackage();
		List<IResource> resources = businessPackage.getResources(session.getGroup().getName());

		List<ComplexFilter> listFilter = new ArrayList<ComplexFilter>();

		for (IResource resource : resources) {
			if (resource instanceof ComplexFilter) {
				listFilter.add((ComplexFilter) resource);
			}
		}

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
		Element items = root.addElement("items");

		for (ComplexFilter item : listFilter) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("name").setText(item.getName());
			elemItem.addElement("field").setText(item.getDataStreamElementName());
			elemItem.addElement("operator").setText(item.getOperator());
			elemItem.addElement("value").setText(convertListToString(item.getValue()));
			elemItem.addElement("tableName").setText(item.getOrigin().getDataStream().getName());
			elemItem.addElement("origin").setText(item.getOrigin().getDataStream().getName());
		}

		return doc.asXML();
	}

	public static String loadSQLfilter(Session session) throws Exception {

		BusinessPackage businessPackage = session.getBusinessPackage();
		List<IResource> resources = businessPackage.getResources(session.getGroup().getName());

		List<SqlQueryFilter> listFilter = new ArrayList<SqlQueryFilter>();

		for (IResource resource : resources) {
			if (resource instanceof SqlQueryFilter) {
				listFilter.add((SqlQueryFilter) resource);
			}
		}
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
		Element items = root.addElement("items");

		for (SqlQueryFilter item : listFilter) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("name").setText(item.getName());
			elemItem.addElement("field").setText(item.getDataStreamElementName());
			elemItem.addElement("operator").setText(item.getQuery());
			elemItem.addElement("value").setText("");
			elemItem.addElement("tableName").setText(item.getOrigin().getDataStream().getName());
			elemItem.addElement("origin").setText(item.getOrigin().getDataStream().getName());
		}

		return doc.asXML();
	}

	public static String loadColumns(String tableName, Session session) throws Exception {

		BusinessPackage businessPackage = session.getBusinessPackage();
		IBusinessTable table = businessPackage.getBusinessTable(session.getGroup().getName(), tableName);

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADBUSINESSPACKAGE);
		Element items = root.addElement("items");

		for (IDataStreamElement item : table.getColumns(session.getGroup().getName())) {
			Element elemItem = items.addElement("item");
			elemItem.addElement("name").setText(item.getName());
			elemItem.addElement("field").setText(item.getOuputName(session.getLocale()));
			if (!(item instanceof ICalculatedElement)) {
				if (item.getOriginName().contains(".")) {
					elemItem.addElement("value").setText(item.getOriginName());
				} else {
					elemItem.addElement("value").setText(item.getDataStream().getName() + "." + item.getOriginName());
				}
			}
			elemItem.addElement("tableName").setText(table.getName());
			elemItem.addElement("type").setText("column");
			elemItem.addElement("origin").setText(item.getDataStream().getName());
		}

		return doc.asXML();
	}

	private static String convertListToString(List<String> values) {
		StringBuffer buf = new StringBuffer();
		for (String s : values) {
			if (!buf.toString().isEmpty()) {
				buf.append(", ");
			}
			buf.append(s);
		}
		return buf.toString();
	}

	public static String treatFormulasElement(String text, Session session) throws Exception {
		List<String> streams = new ArrayList<String>();
		for (IDataStream s : session.getBusinessPackage().getDataSources(session.getGroup().getName()).get(0).getDataStreams()) {
			if (text.contains(s.getOriginName() + ".")) {
				streams.add(s.getName());
			}
		}
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_TREATFORMULAELEMENT);

		for (String item : streams) {
			root.addElement("value").setText(item);
		}

		return doc.asXML();
	}

	public static String getPromptValue(String colName, String tableName, String value, Session session) {
		try {
			BusinessPackage businessPackage = session.getBusinessPackage();
			String groupName = session.getGroup().getName();

			String conName = ((SQLDataSource) businessPackage.getDataSources(groupName).get(0)).getConnections().get(0).getName();

			String query = "SELECT DISTINCT " + colName + " FROM " + tableName;
			if (value != null && !value.isEmpty())
				query += " WHERE " + colName + " LIKE '" + value + "%'";
			query += " ORDER BY " + colName;

			List<List<String>> values = businessPackage.executeQuery(200, conName, query);

			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_GETPROMPTVALUES);

			for (List<String> list : values) {
				for (String item : list) {
					Element elem = root.addElement("item");
					elem.addElement("name").setText(item);
				}
			}

			return doc.asXML();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String buildQuery(String xmlColumns, String xmlAggregates, String xmlPrompts, String xmlFilters, String xmlListOfValue, String xmlFormulas, String xmlOrdonable, String distinct, String limit, Session session) throws Exception {
		BusinessPackage businessPackage = session.getBusinessPackage();
		String groupName = session.getGroup().getName();

		List<IDataStreamElement> columns = FunctionsHelper.convertXMLtoColumns(xmlColumns, businessPackage, groupName);
		List<AggregateFormula> aggregates = FunctionsHelper.convertXMLtoAggregate(xmlAggregates, businessPackage, groupName);
		List<Prompt> prompts = FunctionsHelper.convertXMLtoPrompts(xmlPrompts, businessPackage, groupName);
		List<IFilter> filters = FunctionsHelper.convertXMLtoFilter(xmlFilters, businessPackage, groupName);
		List<ListOfValue> listOfValues = FunctionsHelper.convertXMLtoListOfValue(xmlListOfValue, businessPackage, groupName);
		List<Formula> formulas = FunctionsHelper.convertXMLtoformula(xmlFormulas);
		List<String> listOrdonable = FunctionsHelper.convertListOrdonnable(xmlOrdonable, businessPackage);
		List<RelationStrategy> selectedStrategies = new ArrayList<RelationStrategy>();

		List<Ordonable> ordonables = new ArrayList<Ordonable>();

		for (String ord : listOrdonable) {
			Boolean find = false;
			for (IDataStreamElement column : columns) {
				if (column.getName().equals(ord)) {
					ordonables.add((Ordonable) column);
					find = true;
					break;
				}
			}
			if (!find) {
				for (AggregateFormula agg : aggregates) {
					if (agg.getCol().getName().equals(ord)) {
						ordonables.add((Ordonable) agg);
						find = true;
						break;
					}
				}
			}
			if (!find) {
				for (Formula agg : formulas) {
					if (agg.getName().equals(ord)) {
						ordonables.add((Ordonable) agg);
						find = true;
						break;
					}
				}
			}
		}
		QuerySql query = (QuerySql) SqlQueryBuilder.getQuery(groupName, columns, new HashMap<ListOfValue, String>(), aggregates, ordonables, filters, prompts, formulas, selectedStrategies);

		if (distinct.equals("True")) {
			query.setDistinct(true);
		}
		int bLimit = Integer.parseInt(limit);
		if (bLimit > 0) {
			query.setLimit(bLimit);
		}
		String conName = ((SQLDataSource) businessPackage.getDataSources(groupName).get(0)).getConnections().get(0).getName();

		IVanillaAPI api = getVanillaApi();

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository());

		HashMap<Prompt, List<String>> promptMap = FunctionsHelper.convertPromptValue(xmlPrompts, prompts, businessPackage);

		EffectiveQuery sqlQuery = SqlQueryGenerator.getQuery(ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, ctx != null ? ctx.getVanillaContext() : null, businessPackage, query, groupName, false, promptMap);

		List<List<String>> values = businessPackage.executeQuery(0, conName, sqlQuery.getGeneratedQuery());

		return FunctionsHelper.convertResponseToXml(values);
	}

	public static String getDrivers() throws Exception {

		IVanillaAPI api = getVanillaApi();
		return api.getExcelManager().getListDriver();

	}

	public static String getTables(HashMap<String, String> documentParameters) throws Exception {

		IVanillaAPI api = getVanillaApi();
		return api.getExcelManager().getListTables(documentParameters);

	}

	public static String getColumns(HashMap<String, String> documentParameters) throws Exception {
		IVanillaAPI api = getVanillaApi();
		return api.getExcelManager().getListColumns(documentParameters);
	}

	public static String testConnectionServer(HashMap<String, String> documentParameters) throws Exception {

		IVanillaAPI api = getVanillaApi();
		return api.getExcelManager().testConnectionDatabase(documentParameters);

	}

	public static String getColumnType(HashMap<String, String> documentParameters) throws Exception {

		IVanillaAPI api = getVanillaApi();
		return api.getExcelManager().getColumnType(documentParameters);

	}

	public static String createTable(String sqlScript, HashMap<String, String> documentParameters) throws Exception {

		IVanillaAPI api = getVanillaApi();
		return api.getExcelManager().createTable(sqlScript, documentParameters);

	}

	public static String AddContract(HashMap<String, String> documentParameters, InputStream contractFile, Session session) throws Exception {

		IVanillaAPI api = getVanillaApi();
		IRepositoryContext ctx = getRepositoryContext(session);

		String name = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_FILENAME);
		String path = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_FILEPATH) + name;
		String sheetName = documentParameters.get(VanillaConstantsForFMDT.PARAMETER_SHEETNAME);
		return api.getExcelManager().loaderExcel(name, path, contractFile, ctx, documentParameters);
	}

	public static HashMap<String, Object> returnParameters(List<FileItem> items) throws IOException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (FileItem item : items) {
				if (item.isFormField()) {
					map.put(item.getFieldName(), item.getString("UTF-8"));
				} else {
					map.put(VanillaConstantsForFMDT.PARAMETER_FILE, item.getInputStream());
				}
			}
			return map;

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("returnParameters function failed", e);
			return null;
		}
	}

	// Import Export Excel
	public static String addDocument(String directoryId, String fileName, InputStream stream, Session session) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi(session);
			IRepositoryContext ctx = getRepositoryContext(session);
			return ExcelFunctionsUtils.addDocument(directoryId, fileName, stream, repositoryApi, ctx);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String updateDocument(String directoryId, String fileName, InputStream stream, Session session) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi(session);
			IRepositoryContext ctx = getRepositoryContext(session);
			return ExcelFunctionsUtils.UpdateDocument(directoryId, fileName, stream, repositoryApi, ctx);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String createDir(String directoryId, String name, Session session) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi(session);
			return ExcelFunctionsUtils.createDir(directoryId, name, repositoryApi);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String deleteDir(String directoryId, Session session) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi(session);
			return ExcelFunctionsUtils.deleteDir(directoryId, repositoryApi);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String deleteItem(String directoryId, Session session) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi(session);
			IRepositoryContext ctx = getRepositoryContext(session);
			return ExcelFunctionsUtils.deleteItem(directoryId, repositoryApi, ctx);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static InputStream downloadFile(String directoryId, Session session) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi(session);
			return ExcelFunctionsUtils.downloadFile(directoryId, repositoryApi);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * generateXML
	 * 
	 * @param List
	 *            <IObject> listDocument
	 * @param User
	 *            user
	 * @param String
	 *            type
	 * @return Document
	 * 
	 *         Function used to generate XML from list of Tree. Use for all
	 *         views except group view. Function use AddElement() to look
	 *         through the tree.
	 * 
	 */

	public static String generateXML(String type, Session session) {
		try {
			IRepositoryApi repositoryApi = getRepositoryApi(session);
			return ExcelFunctionsUtils.generateXML(type, repositoryApi);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String getlocales() throws Exception {

		Locale[] locales = Locale.getAvailableLocales();
		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_GETLANGUAGE);

		for (Locale locale : locales) {
			Element lang = root.addElement("language");
			lang.addElement("name").setText(locale.getLanguage());
			lang.addElement("field").setText(locale.getCountry());

		}
		return doc.asXML();
	}

	public static IVanillaAPI getVanillaApi() {
		if (vanillaApi == null) {
			vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return vanillaApi;
	}

	public static IVanillaAPI initVanillaApi() {

		vanillaApi = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));

		return vanillaApi;
	}

	private static IRepositoryApi getRepositoryApi(Session session) throws Exception {

		IVanillaAPI api = getVanillaApi();

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository()));

		return repositoryApi;
	}

	private static IRepositoryContext getRepositoryContext(Session session) {
		IVanillaAPI api = getVanillaApi();

		IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository());

		return ctx;
	}

}
