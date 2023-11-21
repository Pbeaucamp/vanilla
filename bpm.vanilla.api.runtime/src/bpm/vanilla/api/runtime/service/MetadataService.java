package bpm.vanilla.api.runtime.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.GrantException;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.MetaDataException;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.Formula;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.vanilla.api.core.IAPIManager.MetadataMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.runtime.dto.MetadataViewerItem;
import bpm.vanilla.api.runtime.dto.ViewerItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataService {

	private IVanillaSecurityManager vanillaSecurityManager;
	private IRepositoryManager repositoryManager;
	private IVanillaContext vanillaCtx;

	public MetadataService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		this.vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		this.vanillaSecurityManager = vanillaApi.getVanillaSecurityManager();
		this.repositoryManager = vanillaApi.getVanillaRepositoryManager();

		// IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx,
		// session.getGroup(), session.getRepository());

	}

	public String dispatchAction(String method, JSONArray parameters) throws Exception {
		MetadataMethod metadataMethod = MetadataMethod.valueOf(method);
		
		switch (metadataMethod) {
		case GET_METADATAS:
			return getMetadatas(parameters);
		case LOAD_METADATA:
			return loadMetadata(parameters);
		case GET_BUSINESS_MODELS: 
			return getBusinessModels(parameters);
		case GET_BUSINESS_PACKAGES: 
			return getBusinessPackages(parameters);	
		case GET_BUSINESS_PACKAGE_QUERIES : 
			return getBusinessPackageQueries(parameters);	
		case GET_SAVED_QUERY_DATA : 
			return getSavedQueryData(parameters);
		case GET_BUSINESS_TABLES: 
			return getBusinessTables(parameters);	
		case GET_COLUMNS: 
			return getColumns(parameters);
		case GET_COLUMN: 
			return getColumn(parameters);
		case GET_TABLES_AND_COLUMNS :
			return getTablesAndColumns(parameters);	
		case GET_QUERY_RESULT : 
			return ExecuteQuery(parameters);
		case GET_QUERY_SQL : 
			return getQuerySQL(parameters);
		case SAVE_QUERY : 
			return saveQuery(parameters);
		default:
			break;
		}
		//TODO: Manage errors
		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}
	



	public String getMetadatas(JSONArray parameters) throws Exception {
		
		
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		
		bpm.vanilla.platform.core.repository.Repository repo2;
		try {
			repo2 = (bpm.vanilla.platform.core.repository.Repository) this.getRepository(groupID, repositoryID).get("repository");
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		Collection<RepositoryItem> items = new ArrayList<>();
	
		try {
			items = getMetadatas(repo2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
		
		//return (""+items).substring( 1, (""+items).length() - 1 );
		
		ObjectMapper mapper = new ObjectMapper();
		
		List<ViewerItem> metadatas = new ArrayList<>();
		for (RepositoryItem item : items) {
			metadatas.add(new ViewerItem(item,IRepositoryApi.TYPES_NAMES[item.getType()]));
		}
		
		return mapper.writeValueAsString(metadatas);
		
	}

	

	public String loadMetadata(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		int metadataID = parameters.getInt(2);
		
		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(Integer.parseInt(groupID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		Repository repo = null;
		try {
			repo = repositoryManager.getRepositoryById(Integer.parseInt(repositoryID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, repo);
		RemoteRepositoryApi api = new RemoteRepositoryApi(ctx);
		bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(new RemoteRepositoryApi(ctx), IRepositoryApi.FMDT_TYPE);
		
		RepositoryItem metadataItem = repository.getItem(metadataID);
		String metadataXML = api.getRepositoryService().loadModel(metadataItem);
		List<IBusinessModel> businessModels = MetaDataReader.read(group.getName(), IOUtils.toInputStream(metadataXML, "UTF-8"), api, false);
		MetadataViewerItem metaViewer = new MetadataViewerItem(metadataItem,group,businessModels);
		
		
		return new JSONObject(metaViewer).toString();
	}
	
	
	private String getBusinessModels(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		
		
		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(Integer.parseInt(groupID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		Repository repo = null;
		try {
			repo = repositoryManager.getRepositoryById(Integer.parseInt(repositoryID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, repo);
		RemoteRepositoryApi api = new RemoteRepositoryApi(ctx);
		bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(new RemoteRepositoryApi(ctx), IRepositoryApi.FMDT_TYPE);
		
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RepositoryItem item;
		if (items != null && items.size() > 0 ) {
			item = items.get(0);
			List<IBusinessModel> models = getBusinessModels(item, api, group);
			List<String> modelsName = new ArrayList<>();
			JSONArray result = new JSONArray();
			for (IBusinessModel model : models) {
				JSONObject jsonModel = new JSONObject();
				jsonModel.put("name", model.getName());
				
				List<Relation> relations = model.getRelations();
				JSONArray jsonRelations = new JSONArray();
				for (Relation relation : relations) {
					JSONObject jsonRelation = new JSONObject();
					jsonRelation.put("Cadinality", relation.getCardinality());
					jsonRelation.put("Left table",relation.getLeftTableName());
					jsonRelation.put("Right table",relation.getRightTableName());
					//jsonRelation.put("Xml",relation.getXml());
					
					String finalRelation = relation.getFinalRelation();
					if (finalRelation.equals("null")) {
						finalRelation =  relation.getBasicWhereClause();
					}
					jsonRelation.put("Final Relation", finalRelation);
					
					JSONArray jsonJoins = new JSONArray();
					
					List<Join> joins = relation.getJoins();
					for (Join join : joins ) {
						JSONObject jsonJoin = new JSONObject();
						jsonJoin.put("Left name",join.getLeftName());
						jsonJoin.put("Right name",join.getRightName());
						jsonJoin.put("On statement",join.getOnStatement());
						jsonJoin.put("Xml",join.getXml());
						jsonJoins.put(jsonJoin);
					}
					//jsonRelation.put("joins",jsonJoins);
					jsonRelations.put(jsonRelation);
				}
				jsonModel.put("Relations",jsonRelations);
				modelsName.add(model.getName());
				result.put(jsonModel);
			}
			
			return result.toString();
			//return (""+modelsName).substring( 1, (""+modelsName).length() - 1 );
		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
		
	}
	
	
	public HashMap<String, Object> getRepository(String groupID, String repositoryID) throws Exception {
		Group group = null;
		try {
			group = vanillaSecurityManager.getGroupById(Integer.parseInt(groupID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		if (group == null) {
			throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
		}
		Repository repo = null;
		try {
			repo = repositoryManager.getRepositoryById(Integer.parseInt(repositoryID));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, repo);
		RemoteRepositoryApi api = new RemoteRepositoryApi(ctx);
		bpm.vanilla.platform.core.repository.Repository repository = new bpm.vanilla.platform.core.repository.Repository(new RemoteRepositoryApi(ctx), IRepositoryApi.FMDT_TYPE);
		
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("repository", repository);
		map.put("group",group);
		map.put("api", api);
		
		return map;
	}
	
	private String getBusinessPackages(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		
		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem item = items.get(0); // Mon metadata 
			List <IBusinessPackage> packages = this.getBusinessPackages(api, group, item, businessModelName);

			//List<String> packagesName = new ArrayList<>();
			JSONArray result = new JSONArray();
			
			for (IBusinessPackage bPackage : packages) {
				result.put(bPackage.getName());
				//packagesName.add(bPackage.getName());
			}
			
			//return (""+packagesName).substring( 1, (""+packagesName).length() - 1 );
			return result.toString();
		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
			
	}
	
	

	private String getBusinessPackageQueries(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		
		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			IBusinessPackage pack = getBusinessPackage(api, group, metadata, businessModelName, businessPackageName);
			List<SavedQuery> queries = pack.getSavedQueries();

			JSONArray result = new JSONArray();
			List<String> queriesString = new ArrayList<>();
			for (SavedQuery query : queries) {
				queriesString.add(query.getName());	
				result.put(query.getName());
			}
			
			//return (""+queriesString).substring( 1, (""+queriesString).length() - 1 );
			return result.toString();

		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
			
	}
	
	
	
	public String getRequest(QuerySql query, IRepositoryContext ctx, IBusinessPackage pack, String groupName) throws Exception {

		//CommonSession session = getSession();

		//String groupName = session.getCurrentGroup().getName();

		//IBusinessPackage pack = findBusinessPackage(session, metadataId, modelName, packageName);
		if (pack == null) {
			return null;
		}
		HashMap<Prompt, List<String>> promptMap = new HashMap<>();
		//HashMap<Prompt, List<String>> promptMap = convertPromptValue(builder.getPromptFilters(), (BusinessPackage) pack, groupName);

		//QuerySql query = getQuerySql(builder, pack);

		//IRepositoryContext ctx = new BaseRepositoryContext(session.getVanillaContext(), session.getCurrentGroup(), session.getCurrentRepository());
		EffectiveQuery sqlQuery;
		try {
			sqlQuery = SqlQueryGenerator.getQuery(ctx != null ? ctx.getGroup().getMaxSupportedWeightFmdt() : null, ctx != null ? ctx.getVanillaContext() : null, (BusinessPackage) pack, query, groupName, false, promptMap, false);

			return sqlQuery.getGeneratedQuery();
		} catch (MetaDataException e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_QUERY);
		}
	}
	
	
	
	
	
	
	
	

	
	
	
	public QuerySql fill(QuerySql query,String groupName) {
		// Class attribute : 
		List<IResource> ontheFlyResources = new ArrayList<IResource>();
		
		
		List<QueryItem> cols = new ArrayList<QueryItem>();
		ontheFlyResources.clear();
		ontheFlyResources.addAll(query.getDesignTimeResources());

		

		for (AggregateFormula agg : query.getAggs()) {
				cols.add(new QueryItem(agg.getCol(), agg.getFunction(), agg.getOutputName()));
		}
		
		for (IDataStreamElement c : query.getSelect()) {
			cols.add(new QueryItem(c, null, null));
		}
		for (Formula f : query.getFormulas()) {
			cols.add(new QueryItem(f, null, null));
		}

		/*
		 * apply ordering
		 */
		Collections.sort(cols, new Comparator<QueryItem>() {

			@Override
			public int compare(QueryItem o1, QueryItem o2) {
				Integer i1 = query.getOrderBy().indexOf(o1.col);
				Integer i2 = query.getOrderBy().indexOf(o2.col);
				return i1.compareTo(i2);
			}

		});


		//columnViewer.setInput(cols);

		List<Condition> conditions = new ArrayList<Condition>();
		for (IFilter f : query.getFilters()) {
			if (!ontheFlyResources.contains(f)) {
				conditions.add(new Condition(f));
			}

		}
		for (Prompt f : query.getPrompts()) {
			if (!ontheFlyResources.contains(f)) {
				conditions.add(new Condition(f));
			}
		}

		for (IResource r : query.getDesignTimeResources()) {
			conditions.add(new Condition(r));

		}
		
		
		return createFmdtQuery(groupName,ontheFlyResources,cols,conditions,query.getDistinct(),query.getLimit());
	}	
	
	
	
	
	
	
	
	
	
	public QuerySql createFmdtQuery(String groupName,List<IResource> ontheFlyResources,List<QueryItem> columns, List<Condition> conditions, boolean distinct, int limit) {
		List<IDataStreamElement> cols = new ArrayList<IDataStreamElement>();
		List<AggregateFormula> aggs = new ArrayList<AggregateFormula>();
		List<Formula> formulas = new ArrayList<Formula>();
		List<IFilter> filters = new ArrayList<IFilter>();
		List<Prompt> prompts = new ArrayList<Prompt>();
		List<RelationStrategy> selectedStrategies = new ArrayList<RelationStrategy>();

		for (QueryItem i : columns) {
			if (i.agg == null) {
				if (i.col instanceof Formula) {
					formulas.add((Formula) i.col);
				}
				else if (i.col instanceof IDataStreamElement) {
					cols.add((IDataStreamElement) i.col);
				}
			}
			else {
				if (i.col instanceof IDataStreamElement) {
					aggs.add(new AggregateFormula(i.agg, (IDataStreamElement) i.col, i.label));
				}
				else {
					aggs.add(new AggregateFormula(i.agg, (Formula) i.col, i.label));
				}

			}
		}

		List<IResource> usedAddedResources = new ArrayList<IResource>();
		for (Condition i : (List<Condition>) conditions) {
			if (!ontheFlyResources.contains(i.r)) {
				if (i.r instanceof Prompt) {
					prompts.add((Prompt) i.r);
				}
				else if (i.r instanceof IFilter) {
					filters.add((IFilter) i.r);
				}
			}
			else {
				usedAddedResources.add(i.r);
			}

		}
		List<Ordonable> orders = new ArrayList<Ordonable>();
		for (Object o : (List) columns) {
			orders.add(((QueryItem) o).col);
		}
		QuerySql query= (QuerySql)SqlQueryBuilder.getQuery(
				groupName, 
				cols, 
				new HashMap<ListOfValue, String>(), 
				aggs, 
				orders, 
				filters, 
				prompts, formulas, selectedStrategies);

		for (IResource r : usedAddedResources) {
			query.addDesignTimeResource(r);
		}

		query.setDistinct(distinct);
		if (limit > 0 ) {
			query.setLimit(limit);
		}

		return query;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private String getBusinessTables (JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		
		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			List<IBusinessTable> tables = getBusinessTables(api, group, metadata, businessModelName, businessPackageName);
			JSONArray result = new JSONArray();
			List<String> tablesName = new ArrayList<>();
			for (IBusinessTable table : tables) {
				tablesName.add(table.getName());
				result.put(table.getName());
			}
			//return (""+tablesName).substring( 1, (""+tablesName).length() - 1 );
			return result.toString();

		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
			
	}
	
	private String getColumns(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		String businessTableName = parameters.getString(5);
		
		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			
			List<IDataStreamElement> columns = getColumns(api, group, metadata, businessModelName, businessPackageName, businessTableName);
			JSONArray result = new JSONArray();
			List<String> columnsName = new ArrayList<>();
			for (IDataStreamElement col : columns) {
				columnsName.add(col.getName());
				result.put(col.getName());
			}
			//return (""+columnsName).substring( 1, (""+columnsName).length() - 1 );
			return result.toString();
		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
			
	}
	
	private String getColumn(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		String businessTableName = parameters.getString(5);
		String columnName = parameters.getString(6);
		
		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			
			
			IDataStreamElement column = getColumn(api, group, metadata, businessModelName, businessPackageName, businessTableName,columnName);
			

			List<String> values = column.getDistinctValues();
			JSONArray result = new JSONArray(values);
			//return (""+values).substring( 1, (""+values).length() - 1 );
			return result.toString();
		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
	}
	
	private String getTablesAndColumns(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		
		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			List<IBusinessTable> tables = getBusinessTables(api, group, metadata, businessModelName, businessPackageName);
			List< HashMap<String, List<String>> > result = new ArrayList<>();
			
			//HashMap<String, Object> 

			JSONArray jsonArray = new JSONArray();
			for (IBusinessTable table : tables) {
				
				JSONObject jsonTable = new JSONObject();
				jsonTable.put("tableName", table.getName());
				JSONArray jsonChildren = new JSONArray();
				HashMap<String, List<String>> map = new HashMap<String, List<String>>();
				List<IDataStreamElement> columns = getColumns(api, group, metadata, businessModelName, businessPackageName, table.getName());
				List<String> columnsName = new ArrayList<>();
				for (IDataStreamElement col : columns) {
					JSONObject jsonCol = new JSONObject();
					jsonCol.put("columnName", col.getName());
					jsonCol.put("type", col.getType().getParentType());
					//jsonCol.put("type", col.getParentDimension());
					//jsonCol.put("type", col.getType());
					// if(e.getType().getParentType() == IDataStreamElement.Type.DIMENSION || e.getType().getParentType() == IDataStreamElement.Type.MEASURE)
					columnsName.add(col.getName());
					jsonChildren.put(jsonCol);
				}
				jsonTable.put("columns", jsonChildren);
				jsonArray.put(jsonTable);
				//formattedResult.concat(map.toString()+"|");
				map.put(table.getName(), columnsName);
				result.add(map);

			}


			return jsonArray.toString();


		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
	}
	
	
	
	
	

	
	
	private String getQuerySQL(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		String columnsString = parameters.getString(5); // columnsString : TableName1:ColumnName1,TableName2:columnName2
		String queryLimit = parameters.getString(6);
		String queryDistinct = parameters.getString(7);
		
		
		
		List<String[]> columnsList = new ArrayList<>(); // List of tab with tableName at index 0 and columnName at index 1 
		for (String s : columnsString.split(",")) {
			columnsList.add(s.split(":"));
		}


		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			IBusinessPackage pack = getBusinessPackage(api, group, metadata, businessModelName, businessPackageName);
			List<IBusinessTable> tables = getBusinessTables(api, group, metadata, businessModelName, businessPackageName);

			List<IDataStreamElement> columns = manageColumnsList(columnsList, tables);
			

			List<Ordonable> orders = new ArrayList<Ordonable>();
			for (IDataStreamElement c : columns) {
				orders.add(c);
			}
			
			
			QuerySql query= (QuerySql)SqlQueryBuilder.getQuery(
					"none", 
					 columns, 
					new HashMap<ListOfValue, String>(), 
					new ArrayList<AggregateFormula>(), 
					orders, 
					new ArrayList<IFilter>(), 
					new ArrayList<Prompt>(), new ArrayList<Formula>(), new ArrayList<RelationStrategy>());
			
			
			if (!queryLimit.equals("")) {
				query.setLimit(Integer.parseInt(queryLimit));
			}
			
			if (!queryDistinct.equals("")) {
				query.setDistinct(Boolean.parseBoolean(queryDistinct));
			}
			
			
			List<List<String>> prompts = new ArrayList<List<String>>(query.getPrompts().size());
			for (int i = 0; i < query.getPrompts().size(); i++) {
				prompts.add(new ArrayList<String>());
			}
			EffectiveQuery sqlQuery = pack.evaluateQuery(vanillaCtx, query, prompts, false);
			String resQuery;
			resQuery = sqlQuery.getGeneratedQuery();

			//System.out.println("values : " + values);
			JSONObject json = new JSONObject();
			json.put("SQL", resQuery);
			return json.toString(); //(""+tablesName).substring( 1, (""+tablesName).length() - 1 );

		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
		
	}
	
	private List<IDataStreamElement> manageColumnsList(List<String[]> columnsList, List<IBusinessTable> tables) throws GrantException {
		List<IDataStreamElement> columns = new ArrayList<>();
		for (String[] colTab : columnsList) {
			IBusinessTable businessTable = null;  
			for (IBusinessTable table : tables) {
				if (table.getName().equals(colTab[0])) {
					businessTable = table; 
					break;
				}
			}
			columns.add(businessTable.getColumn("none", colTab[1]));
		}
		return columns;
	}
	
	
	private String ExecuteQuery(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		String columnsString = parameters.getString(5);
		String queryLimit = parameters.getString(6);
		String queryDistinct = parameters.getString(7);
		

		List<String[]> columnsList = new ArrayList<>(); // List of tab with tableName at index 0 and columnName at index 1 
		for (String s : columnsString.split(",")) {
			columnsList.add(s.split(":"));
		}
		


		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			IBusinessPackage pack = getBusinessPackage(api, group, metadata, businessModelName, businessPackageName);
			List<IBusinessTable> tables = getBusinessTables(api, group, metadata, businessModelName, businessPackageName);

			List<IDataStreamElement> columns = manageColumnsList(columnsList, tables);

			List<Ordonable> orders = new ArrayList<Ordonable>();
			for (IDataStreamElement c : columns) {
				orders.add(c);
			}
			
			
			QuerySql query= (QuerySql)SqlQueryBuilder.getQuery(
					"none", 
					columns, 
					new HashMap<ListOfValue, String>(), 
					new ArrayList<AggregateFormula>(), 
					orders, 
					new ArrayList<IFilter>(), 
					new ArrayList<Prompt>(), new ArrayList<Formula>(), new ArrayList<RelationStrategy>());
			
			
			if (!queryLimit.equals("")) {
				query.setLimit(Integer.parseInt(queryLimit));
			}
			
			if (!queryDistinct.equals("")) {
				query.setDistinct(Boolean.parseBoolean(queryDistinct));
			}
			
			
			List<List<String>> prompts = new ArrayList<List<String>>(query.getPrompts().size());
			for (int i = 0; i < query.getPrompts().size(); i++) {
				prompts.add(new ArrayList<String>());
			}

			EffectiveQuery sqlQuery = pack.evaluateQuery(vanillaCtx, query, prompts, false);


			

			//System.out.println("Package : " + pack.getName());
			//List<SavedQuery> queries = pack.getSavedQueries();
			List<List<String>> values = pack.executeQuery(query.getLimit(), 
					"Default", 
					sqlQuery.getGeneratedQuery());
			//System.out.println("values : " + values);
			JSONArray result = new JSONArray(values);
			//return values.toString();//(""+tablesName).substring( 1, (""+tablesName).length() - 1 );
			return result.toString();

		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
		
	}	
	
	
	private String saveQuery(JSONArray parameters) throws Exception {
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		String columnsString = parameters.getString(5);
		String queryName = parameters.getString(6);
		String queryDesc = parameters.getString(7);
		String queryLimit = parameters.getString(8);
		String queryDistinct = parameters.getString(9);
		

		List<String[]> columnsList = new ArrayList<>(); // List of tab with tableName at index 0 and columnName at index 1 
		for (String s : columnsString.split(",")) {
			columnsList.add(s.split(":"));
		}
		
		



		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			List<IBusinessTable> tables = getBusinessTables(api, group, metadata, businessModelName, businessPackageName);

			
			List<IDataStreamElement> tmpcolumns = new ArrayList<>(); 
			for (IBusinessTable table : tables) {
				tmpcolumns.addAll(getColumns(api, group, metadata, businessModelName, businessPackageName, table.getName()));
			}
			
			List<IDataStreamElement> columns = manageColumnsList(columnsList, tables);

			
			
			List<Ordonable> orders = new ArrayList<Ordonable>();
			for (IDataStreamElement c : columns) {
				orders.add(c);
			}
			
			
			QuerySql query= (QuerySql)SqlQueryBuilder.getQuery(
					group.getName(), 
					columns, 
					new HashMap<ListOfValue, String>(), 
					new ArrayList<AggregateFormula>(), 
					orders, 
					new ArrayList<IFilter>(), 
					new ArrayList<Prompt>(), new ArrayList<Formula>(), new ArrayList<RelationStrategy>());
			if (queryLimit != "") {
				query.setLimit(Integer.parseInt(queryLimit));
			}
			
			if (queryDistinct != "") {
				query.setDistinct(Boolean.parseBoolean(queryDistinct));
			}
			IBusinessPackage pack = getBusinessPackage(api, group, metadata, businessModelName, businessPackageName);
			pack.addSavedQuery(new SavedQuery(queryName, queryDesc, query));

			try {
				String xml = pack.getBusinessModel().getModel().getXml(false);				
				api.getRepositoryService().updateModel(metadata, xml);
			} catch (Exception e1) {
				e1.printStackTrace();			
			}
			//JSONObject result = new JSONObject();
			//result.put("", value)
			return "Query succesfully saved.";//(""+tablesName).substring( 1, (""+tablesName).length() - 1 );

		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		
		
	}	
	
	
	
	
	private String getSavedQueryData(JSONArray parameters) throws Exception {
		
		String repositoryID = parameters.getString(0);
		String groupID = parameters.getString(1);
		String metadataName = parameters.getString(2);
		String businessModelName = parameters.getString(3);
		String businessPackageName = parameters.getString(4);
		String queryName = parameters.getString(5);
		
		bpm.vanilla.platform.core.repository.Repository repository;
		Group group;
		RemoteRepositoryApi api;
		try {
			HashMap<String, Object> map = this.getRepository(groupID, repositoryID);
			repository = (bpm.vanilla.platform.core.repository.Repository) map.get("repository");
			group = (Group) map.get("group");
			api = (RemoteRepositoryApi) map.get("api");
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return ("ERROR bpm.vanilla.api.runtime : 15 - Repository not found.");
		}
		
		List<RepositoryItem> items = null;
		try {
			items = repository.getItems(metadataName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (items != null && items.size() > 0 ) {
			RepositoryItem metadata = items.get(0); // Mon metadata 
			IBusinessPackage pack = getBusinessPackage(api, group, metadata, businessModelName, businessPackageName);

			List<SavedQuery> queries = pack.getSavedQueries();


			SavedQuery query = null;
			for (SavedQuery q : queries) {
				if (q.getName().equals(queryName)) {
					query = q;
					break;
				}
			}
			//System.out.println("Saved query = " + query.getName());
			QuerySql myQuery =  query.loadQuery("none", pack);		
			
			List<QueryItem> cols = new ArrayList<QueryItem>();
			

			for (AggregateFormula agg : myQuery.getAggs()) {
				cols.add(new QueryItem(agg.getCol(), agg.getFunction(), agg.getOutputName()));
			}
			
			for (IDataStreamElement c : myQuery.getSelect()) {
				cols.add(new QueryItem(c, null, null));
			}
			for (Formula f : myQuery.getFormulas()) {
				cols.add(new QueryItem(f, null, null));
			}
			
			/*
			 * apply ordering
			 */
			Collections.sort(cols, new Comparator<QueryItem>() {

				@Override
				public int compare(QueryItem o1, QueryItem o2) {
					Integer i1 = myQuery.getOrderBy().indexOf(o1.col);
					Integer i2 = myQuery.getOrderBy().indexOf(o2.col);
					return i1.compareTo(i2);
				}

			});			
			
			JSONObject jsonResult = new JSONObject();
			jsonResult.put("queryDistinct",  myQuery.getDistinct());
			jsonResult.put("queryLimit",  myQuery.getLimit());
			JSONArray jsonArray = new JSONArray();
			String result = myQuery.getDistinct()+";"+myQuery.getLimit()+";";
			List<IBusinessTable> tables = getBusinessTables(api, group, metadata, businessModelName, businessPackageName);
			for (QueryItem i : cols) {
				if (i.agg == null) {
					if (i.col instanceof Formula) {
						System.out.println("FORMULA : " + ((Formula) i.col).getDataStreamInvolved() + ":" +((Formula) i.col).getName() +":"+ ((Formula) i.col).getFormula()+",");
						//result += ((Formula) i.col).getDataStreamInvolved() + ":" +((Formula) i.col).getName() +":"+ ((Formula) i.col).getFormula()+",";
					}
					else if (i.col instanceof IDataStreamElement) {
						IBusinessTable parentTab = null;
						for (IBusinessTable tab : tables) {
							//System.out.println("((IDataStreamElement) i.col).getName() : " + ((IDataStreamElement) i.col).getName());
							IDataStreamElement tmpCol = null;
							try {
								tmpCol = tab.getColumn("none", ((IDataStreamElement) i.col).getName());
							} catch (Exception e) {}

							if ( tmpCol != null ) {
								parentTab = tab;
								break;
							}
						}
						jsonArray.put(parentTab.getName() +":" +((IDataStreamElement) i.col).getName() );
						result += parentTab.getName() +":" +((IDataStreamElement) i.col).getName() +",";
		
					}
				}
				else {
					if (i.col instanceof IDataStreamElement) {
						IBusinessTable parentTab = null;
						for (IBusinessTable tab : tables) {
							//System.out.println("((IDataStreamElement) i.col).getName() : " + ((IDataStreamElement) i.col).getName());
							IDataStreamElement tmpCol = null;
							try {
								tmpCol = tab.getColumn("none", ((IDataStreamElement) i.col).getName());
							} catch (Exception e) {}
			
							if ( tmpCol != null ) {
								parentTab = tab;
								break;
							}
						}
						jsonArray.put(parentTab.getName() +":" +((IDataStreamElement) i.col).getName() +":"+ i.agg);
						result += parentTab.getName() +":" +((IDataStreamElement) i.col).getName() +":"+ i.agg +",";
						//aggs.add(new AggregateFormula(i.agg, (IDataStreamElement) i.col, i.label));
					}
					else {
						System.out.println("AGG FORMULA : " + ((Formula) i.col).getDataStreamInvolved() + ":" +((Formula) i.col).getName() +":" + i.agg +":"+ ((Formula) i.col).getFormula()+",");
					}

				}
			} 		
			//return result.substring(0,result.length()-1);
			jsonResult.put("columns", jsonArray);
			return jsonResult.toString();

		} else {
			throw new VanillaApiException(VanillaApiError.METADATA_NOT_FOUND);
		}
		

	}	
	
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private List<RepositoryItem> getMetadatas(bpm.vanilla.platform.core.repository.Repository repository,RepositoryDirectory directory) {
		List<RepositoryItem> items = new ArrayList<>();
		try {
			for (RepositoryItem item : repository.getItems(directory)) {
				items.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<RepositoryDirectory> dirs = null;
		try {
			dirs = repository.getChildDirectories(directory);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RepositoryDirectory dir : dirs) {
			items.addAll(getMetadatas(repository, dir));
		}

		return items;
	}
	
	private List<RepositoryItem> getMetadatas(bpm.vanilla.platform.core.repository.Repository repository) throws Exception {
		List<RepositoryItem> items = new ArrayList<>();
		for (RepositoryDirectory dir : repository.getRootDirectories()) {
			items.addAll(getMetadatas(repository, dir));
		}
		return items;
	}	
	
	
	private List<IBusinessModel> getBusinessModels(RepositoryItem metadata,RemoteRepositoryApi repositoryApi, Group group) {
		String result = null;
		try {
			result = repositoryApi.getRepositoryService().loadModel(metadata);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IBusinessModel>();
		}

		try {
			return MetaDataReader.read(group.getName(), IOUtils.toInputStream(result, "UTF-8"), repositoryApi, false);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<IBusinessModel>();
		}
	}
	
	
	public IBusinessModel getBusinessModel(RemoteRepositoryApi repositoryApi, Group group, RepositoryItem metadata, String modelName) throws Exception  {
		List<IBusinessModel> models = getBusinessModels(metadata,repositoryApi,group);
		if (models != null) {
			for (IBusinessModel model : models) {
				if (model.getName().equals(modelName)) {
					return model;
				}
			}
		}

		throw new VanillaApiException(VanillaApiError.BUSINESS_MODEL_NOT_FOUND);
	}
	
	
	public List<IBusinessPackage> getBusinessPackages(RemoteRepositoryApi repositoryApi, Group group, RepositoryItem metadata, String modelName) throws Exception {
		IBusinessModel model = getBusinessModel(repositoryApi,group,metadata, modelName);
		return model.getBusinessPackages(group.getName());
	}
	
	
	private List<IBusinessTable> getBusinessTables(RemoteRepositoryApi repositoryApi, Group group, RepositoryItem metadata, String modelName, String packageName) throws Exception {
		IBusinessPackage pack = getBusinessPackage(repositoryApi, group, metadata, modelName, packageName);
		return pack.getExplorableTables();
	}
	
	
	public IBusinessPackage getBusinessPackage(RemoteRepositoryApi repositoryApi, Group group, RepositoryItem metadata, String modelName, String packageName) throws Exception  {
		List<IBusinessPackage> packages = getBusinessPackages(repositoryApi, group, metadata, modelName);
		if (packages != null) {
			for (IBusinessPackage pack : packages) {
				if (pack.getName().equals(packageName)) {
					return pack;
				}
			}
		}

		throw new VanillaApiException(VanillaApiError.BUSINESS_PACKAGE_NOT_FOUND);
	}
	
	
	
	public IBusinessTable getBusinessTable(RemoteRepositoryApi repositoryApi, Group group,RepositoryItem metadata, String modelName, String packageName, String tableName) throws Exception  {
		List<IBusinessTable> tables = getBusinessTables(repositoryApi, group, metadata, modelName, packageName);
		if (tables != null) {
			for (IBusinessTable table : tables) {
				if (table.getName().equals(tableName)) {
					return table;
				}
			}
		}

		throw new VanillaApiException(VanillaApiError.BUSINESS_TABLE_NOT_FOUND);
	}
	
	
	
	
	private List<IDataStreamElement> getColumns(RemoteRepositoryApi repositoryApi, Group group,RepositoryItem metadata, String modelName, String packageName, String tableName) throws Exception {
		IBusinessTable table = getBusinessTable(repositoryApi, group, metadata, modelName, packageName, tableName);
		List<IDataStreamElement> columns = new ArrayList<>();
		columns.addAll(((AbstractBusinessTable) table).getColumns());
		return columns;
	}
	
	
	
	public IDataStreamElement getColumn(RemoteRepositoryApi repositoryApi, Group group,RepositoryItem metadata, String modelName, String packageName, String tableName, String columnName) throws Exception {
		List<IDataStreamElement> columns = getColumns(repositoryApi, group, metadata, modelName, packageName, tableName);
		if (columns != null) {
			for (IDataStreamElement column : columns) {
				if (column.getName().equals(columnName)) {
					return column;
				}
			}
		}

		throw new VanillaApiException(VanillaApiError.TABLE_COLUMN_NOT_FOUND);
	}
	
	
	
	
	
	

	
	
	private static class Condition {
		private IResource r;

		Condition(IResource r) {
			this.r = r;
		}

		public String getOperator() {
			if (r instanceof Filter) {
				return "IN"; //$NON-NLS-1$
			}
			if (r instanceof ComplexFilter) {
				return ((ComplexFilter) r).getOperator();
			}
			if (r instanceof SqlQueryFilter) {
				return ((SqlQueryFilter) r).getQuery();
			}
			if (r instanceof Prompt) {
				return ((Prompt) r).getOperator();
			}
			return null;
		}

		public String getValues() {
			StringBuffer buf = new StringBuffer();
			if (r instanceof Prompt) {
				return "?"; //$NON-NLS-1$
			}
			if (r instanceof ComplexFilter) {
				for (String s : ((ComplexFilter) r).getValue()) {
					if (!buf.toString().isEmpty()) {
						buf.append(", "); //$NON-NLS-1$
					}
					buf.append(s);
				}
			}
			else if (r instanceof ComplexFilter) {
				for (String s : ((ComplexFilter) r).getValue()) {
					if (!buf.toString().isEmpty()) {
						buf.append(", "); //$NON-NLS-1$
					}
					buf.append(s);
				}
			}
			return buf.toString();
		}

		public boolean supportOperatorEdition() {
			return r instanceof ComplexFilter || r instanceof Prompt;
		}

		public void setOperator(String operator) {
			if (r instanceof Prompt) {
				((Prompt) r).setOperator(operator);
			}
			if (r instanceof ComplexFilter) {
				((ComplexFilter) r).setOperator(operator);
			}
		}

	}
	
	private static class QueryItem {
		private Ordonable col;
		private String agg;
		private String label;

		public QueryItem(Ordonable col, String agg, String label) {
			super();
			this.col = col;
			this.agg = agg;
			this.label = label;
		}
	}	
	
	
	

}
