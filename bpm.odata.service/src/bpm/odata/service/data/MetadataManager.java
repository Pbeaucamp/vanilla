package bpm.odata.service.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.odata.service.MetadataEdmProvider;
import bpm.odata.service.servlets.DownloadDataServlet;
import bpm.odata.service.utils.Utils;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataManager {

	public static final String PREFIX_DATASET = "Dataset_";

	private Group group;
	private IRepository repository;

	private IRepositoryApi repositoryApi;

	private String runtimeUrl;

	private List<MetadataDataset> datasets;

	public MetadataManager(String runtimeUrl, IRepositoryApi repositoryApi, Group group, IRepository repository) {
		this.runtimeUrl = runtimeUrl;
		this.repositoryApi = repositoryApi;
		this.group = group;
		this.repository = repository;
	}

	// Metadata Part
	public EntityCollection getMetadataCollection() throws ODataApplicationException {
		List<Entity> entities = new ArrayList<>();

		try {
			List<RepositoryItem> items = getMetadatas();
			if (items != null) {
				for (RepositoryItem it : items) {
					entities.add(buildEntity(it));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable to browse metadatas: " + e.getMessage(), HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
		return buildCollection(entities);
	}

	public EntityCollection getBusinessModelCollection(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);

		List<Entity> entities = new ArrayList<>();

		RepositoryItem item = getMetadata(metadataId);
		try {
			List<IBusinessModel> models = getBusinessModels(item);
			if (models != null) {
				for (IBusinessModel model : models) {
					entities.add(buildEntity(model));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable to browse business models for metadata with id '" + metadataId + "'", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
		return buildCollection(entities);
	}

	public EntityCollection getBusinessPackageCollection(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);
		String modelName = extractModelName(keys);

		List<Entity> entities = new ArrayList<>();

		RepositoryItem item = getMetadata(metadataId);
		try {
			List<IBusinessPackage> packages = getBusinessPackages(item, modelName);
			if (packages != null) {
				for (IBusinessPackage pack : packages) {
					entities.add(buildEntity(pack));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable to browse business packages for metadata with id '" + metadataId + "' and model '" + modelName + "'", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
		return buildCollection(entities);
	}

	public EntityCollection getBusinessTableCollection(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);
		String modelName = extractModelName(keys);
		String packageName = extractPackageName(keys);

		List<Entity> entities = new ArrayList<>();

		RepositoryItem item = getMetadata(metadataId);
		try {
			List<IBusinessTable> tables = getBusinessTables(item, modelName, packageName);
			if (tables != null) {
				for (IBusinessTable table : tables) {
					entities.add(buildEntity(metadataId, modelName, packageName, table.getName(), table));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable to browse business tables for metadata with id '" + metadataId + "', model '" + modelName + "' and package '" + packageName + "'", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
		return buildCollection(entities);
	}

	public EntityCollection getColumnCollection(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);
		String modelName = extractModelName(keys);
		String packageName = extractPackageName(keys);
		String tableName = extractTableName(keys);

		List<Entity> entities = new ArrayList<>();

		RepositoryItem item = getMetadata(metadataId);
		try {
			List<IDataStreamElement> columns = getColumns(item, modelName, packageName, tableName);
			if (columns != null) {
				for (IDataStreamElement column : columns) {
					entities.add(buildEntity(column));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable to browse business tables for metadata with id '" + metadataId + "', model '" + modelName + "' and package '" + packageName + "'", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
		return buildCollection(entities);
	}

	private EntityCollection buildCollection(List<Entity> entities) {
		EntityCollection retEntitySet = new EntityCollection();
		for (Entity entity : entities) {
			retEntitySet.getEntities().add(entity);
		}

		return retEntitySet;
	}

	public Entity getMetadataEntity(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);

		RepositoryItem item = getMetadata(metadataId);
		return buildEntity(item);
	}

	public Entity getBusinessModelEntity(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);
		String modelName = extractModelName(keys);

		RepositoryItem item = getMetadata(metadataId);
		IBusinessModel model = getBusinessModel(item, modelName);
		return buildEntity(model);
	}

	public Entity getBusinessPackageEntity(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);
		String modelName = extractModelName(keys);
		String packageName = extractPackageName(keys);

		RepositoryItem item = getMetadata(metadataId);
		IBusinessPackage pack = getBusinessPackage(item, modelName, packageName);
		return buildEntity(pack);
	}

	public Entity getBusinessTableEntity(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);
		String modelName = extractModelName(keys);
		String packageName = extractPackageName(keys);
		String tableName = extractTableName(keys);

		RepositoryItem item = getMetadata(metadataId);
		IBusinessTable table = getBusinessTable(item, modelName, packageName, tableName);
		return buildEntity(metadataId, modelName, packageName, tableName, table);
	}

	public AbstractBusinessTable getBusinessTable(int metadataId, String modelName, String packageName, String tableName) throws Exception {
		RepositoryItem item = getMetadata(metadataId);
		IBusinessTable table = getBusinessTable(item, modelName, packageName, tableName);
		return table instanceof AbstractBusinessTable ? (AbstractBusinessTable) table : null;
	}

	public Entity getColumnEntity(List<EntityKey> keys) throws ODataApplicationException {
		int metadataId = extractMetadataId(keys);
		String modelName = extractModelName(keys);
		String packageName = extractPackageName(keys);
		String tableName = extractTableName(keys);
		String columnName = extractColumnName(keys);

		RepositoryItem item = getMetadata(metadataId);
		IDataStreamElement column = getColumn(item, modelName, packageName, tableName, columnName);
		return buildEntity(column);
	}

	private List<RepositoryItem> getMetadatas() throws Exception {
		List<RepositoryItem> items = new ArrayList<>();
		for (RepositoryDirectory dir : repository.getRootDirectories()) {
			items.addAll(getMetadatas(dir));
		}
		return items;
	}

	private List<RepositoryItem> getMetadatas(RepositoryDirectory directory) {
		List<RepositoryItem> items = new ArrayList<>();
		try {
			for (RepositoryItem item : repository.getItems(directory)) {
				if (item.isAvailableOData()) {
					items.add(item);
				}
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
			items.addAll(getMetadatas(dir));
		}

		return items;
	}

	private List<IBusinessModel> getBusinessModels(RepositoryItem metadata) {
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

	public List<IBusinessPackage> getBusinessPackages(RepositoryItem metadata, String modelName) throws ODataApplicationException {
		IBusinessModel model = getBusinessModel(metadata, modelName);
		return model.getBusinessPackages(group.getName());
	}

	private List<IBusinessTable> getBusinessTables(RepositoryItem metadata, String modelName, String packageName) throws ODataApplicationException {
		IBusinessPackage pack = getBusinessPackage(metadata, modelName, packageName);
		return pack.getExplorableTables();
	}

	private List<IDataStreamElement> getColumns(RepositoryItem metadata, String modelName, String packageName, String tableName) throws ODataApplicationException {
		IBusinessTable table = getBusinessTable(metadata, modelName, packageName, tableName);
		List<IDataStreamElement> columns = new ArrayList<>();
		columns.addAll(((AbstractBusinessTable) table).getColumns());
		return columns;
	}

	// private Entity getBusinessModel(List<EntityKey> keys) throws ODataApplicationException {
	// return metadataManager.getBusinessModelEntity(metadataId, modelName);
	// }
	//
	// private Entity getBusinessPackage(List<EntityKey> keys) throws ODataApplicationException {
	// return metadataManager.getBusinessPackageEntity(metadataId, modelName, packageName);
	// }
	//
	// private Entity getBusinessTable(List<EntityKey> keys) throws ODataApplicationException {
	// return metadataManager.getBusinessTableEntity(metadataId, modelName, packageName, tableName);
	// }
	//
	// public AbstractBusinessTable getBusinessTable(int metadataId, String modelName, String packageName, String tableName) throws Exception {
	// return metadataManager.getBusinessTable(metadataId, modelName, packageName, tableName);
	// }

	// private Entity getColumn(List<EntityKey> keys) throws ODataApplicationException {
	// return metadataManager.getColumnEntity(metadataId, modelName, packageName, tableName, columnName);
	// }

	// private Entity getDatasetValue(List<EntityKey> keys) throws ODataApplicationException {
	// return metadataManager.getDatasetValue(datasetName, datasetId);
	// }

	private RepositoryItem getMetadata(int metadataId) throws ODataApplicationException {
		try {
			RepositoryItem item = repository.getItem(metadataId);
			if (item != null) {
				return item;
			}

			throw new ODataApplicationException("The metadata with id '" + metadataId + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		} catch (Exception e) {
			throw new ODataApplicationException("Unable to get the metadata with id '" + metadataId + "' : " + e.getMessage(), HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
	}

	public IBusinessModel getBusinessModel(RepositoryItem metadata, String modelName) throws ODataApplicationException {
		List<IBusinessModel> models = getBusinessModels(metadata);
		if (models != null) {
			for (IBusinessModel model : models) {
				if (model.getName().equals(modelName)) {
					return model;
				}
			}
		}

		throw new ODataApplicationException("The business model '" + modelName + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	public IBusinessPackage getBusinessPackage(RepositoryItem metadata, String modelName, String packageName) throws ODataApplicationException {
		List<IBusinessPackage> packages = getBusinessPackages(metadata, modelName);
		if (packages != null) {
			for (IBusinessPackage pack : packages) {
				if (pack.getName().equals(packageName)) {
					return pack;
				}
			}
		}

		throw new ODataApplicationException("The business package '" + packageName + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	public IBusinessTable getBusinessTable(RepositoryItem metadata, String modelName, String packageName, String tableName) throws ODataApplicationException {
		List<IBusinessTable> tables = getBusinessTables(metadata, modelName, packageName);
		if (tables != null) {
			for (IBusinessTable table : tables) {
				if (table.getName().equals(tableName)) {
					return table;
				}
			}
		}

		throw new ODataApplicationException("The business table '" + tableName + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	public IDataStreamElement getColumn(RepositoryItem metadata, String modelName, String packageName, String tableName, String columnName) throws ODataApplicationException {
		List<IDataStreamElement> columns = getColumns(metadata, modelName, packageName, tableName);
		if (columns != null) {
			for (IDataStreamElement column : columns) {
				if (column.getName().equals(columnName)) {
					return column;
				}
			}
		}

		throw new ODataApplicationException("The column '" + columnName + "' can not be found.", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
	}

	private Entity buildEntity(RepositoryItem item) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, MetadataEdmProvider.ID, ValueType.PRIMITIVE, item.getId()));
		entity.addProperty(new Property(null, MetadataEdmProvider.NAME, ValueType.PRIMITIVE, item.getName()));
		entity.addProperty(new Property(null, MetadataEdmProvider.DESCRIPTION, ValueType.PRIMITIVE, item.getComment()));
		entity.setType(MetadataEdmProvider.ET_METADATA_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, "ID"));
		return entity;
	}

	private Entity buildEntity(IBusinessModel model) throws ODataApplicationException {
		if (model instanceof BusinessModel) {
			return buildEntity((BusinessModel) model);
		}

		throw new ODataApplicationException("The business model '" + model.getName() + "' is not a proper BusinessModel.", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
	}

	private Entity buildEntity(BusinessModel model) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, MetadataEdmProvider.NAME, ValueType.PRIMITIVE, model.getName()));
		entity.addProperty(new Property(null, MetadataEdmProvider.DESCRIPTION, ValueType.PRIMITIVE, model.getDescription()));
		entity.setType(MetadataEdmProvider.ET_BUSINESS_MODEL_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, MetadataEdmProvider.NAME));
		return entity;
	}

	private Entity buildEntity(IBusinessPackage pack) throws ODataApplicationException {
		if (pack instanceof BusinessPackage) {
			return buildEntity((BusinessPackage) pack);
		}

		throw new ODataApplicationException("The business package '" + pack.getName() + "' is not a proper BusinessPackage.", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
	}

	private Entity buildEntity(BusinessPackage pack) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, MetadataEdmProvider.NAME, ValueType.PRIMITIVE, pack.getName()));
		entity.addProperty(new Property(null, MetadataEdmProvider.DESCRIPTION, ValueType.PRIMITIVE, pack.getDescription()));
		entity.setType(MetadataEdmProvider.ET_BUSINESS_PACKAGE_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, MetadataEdmProvider.NAME));
		return entity;
	}

	private Entity buildEntity(int metadataId, String modelName, String packageName, String tableName, IBusinessTable table) throws ODataApplicationException {
		if (table instanceof AbstractBusinessTable) {
			return buildEntity(metadataId, modelName, packageName, tableName, (AbstractBusinessTable) table);
		}

		throw new ODataApplicationException("The business package '" + table.getName() + "' is not a proper BusinessPackage.", HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
	}

	private Entity buildEntity(int metadataId, String modelName, String packageName, String tableName, AbstractBusinessTable table) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, MetadataEdmProvider.NAME, ValueType.PRIMITIVE, table.getName()));
		entity.addProperty(new Property(null, MetadataEdmProvider.DESCRIPTION, ValueType.PRIMITIVE, table.getDescription()));
		entity.addProperty(new Property(null, MetadataEdmProvider.DATA_CSV, ValueType.PRIMITIVE, buildTableUrl(metadataId, modelName, packageName, tableName, DownloadDataServlet.FORMAT_CSV)));
		entity.addProperty(new Property(null, MetadataEdmProvider.DATA_XLS, ValueType.PRIMITIVE, buildTableUrl(metadataId, modelName, packageName, tableName, DownloadDataServlet.FORMAT_XLS)));
		entity.setType(MetadataEdmProvider.ET_BUSINESS_TABLE_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, MetadataEdmProvider.NAME));
		return entity;
	}

	private String buildTableUrl(int metadataId, String modelName, String packageName, String tableName, String format) {
		return runtimeUrl + DownloadDataServlet.DOWNLOAD_DATA_SERVLET + "/?" + DownloadDataServlet.METADATA_ID + "=" + metadataId + "&" + DownloadDataServlet.MODEL_NAME + "=" + modelName + "&" + DownloadDataServlet.PACKAGE_NAME + "=" + packageName + "&" + DownloadDataServlet.TABLE_NAME + "=" + tableName + "&" + DownloadDataServlet.FORMAT + "=" + format;
	}

	private Entity buildEntity(IDataStreamElement column) {
		Entity entity = new Entity();
		entity.addProperty(new Property(null, MetadataEdmProvider.NAME, ValueType.PRIMITIVE, column.getName()));
		entity.addProperty(new Property(null, MetadataEdmProvider.DESCRIPTION, ValueType.PRIMITIVE, column.getDescription()));
		entity.setType(MetadataEdmProvider.ET_COLUMN_FQN.getFullQualifiedNameAsString());
		entity.setId(Utils.createId(entity, MetadataEdmProvider.NAME));
		return entity;
	}

	// Dataset part
	public List<MetadataDataset> getDatasets(boolean reload) throws Exception {
		if (datasets == null || reload) {
			this.datasets = getDatasets();
		}

		return datasets;
	}

	private List<MetadataDataset> getDatasets() throws Exception {
		List<MetadataDataset> datasets = new ArrayList<>();

		List<RepositoryItem> metadatas = getMetadatas();
		if (metadatas != null) {
			for (RepositoryItem metadata : metadatas) {

				List<IBusinessModel> models = getBusinessModels(metadata);
				if (models != null) {
					for (IBusinessModel model : models) {
						List<IBusinessPackage> packages = getBusinessPackages(metadata, model.getName());
						if (packages != null) {
							for (IBusinessPackage pack : packages) {
								List<SavedQuery> queries = pack.getSavedQueries();
								if (queries != null) {
									for (SavedQuery query : queries) {
										datasets.add(new MetadataDataset(pack, query));
									}
								}
							}
						}
					}
				}
			}
		}

		return datasets;
	}

	public String getDatasetName(MetadataDataset dataset, boolean isList) {
		return isList ? PREFIX_DATASET + dataset.getDatasetName() : dataset.getDatasetName();
	}

	public MetadataDataset findDataset(List<MetadataDataset> datasets, String datasetName, boolean isList) {
		if (datasets != null) {
			for (MetadataDataset dataset : datasets) {
				if (getDatasetName(dataset, isList).equals(datasetName)) {
					return dataset;
				}
			}
		}

		return null;
	}

	public EntityCollection getDatasetValueCollection(String datasetName) throws ODataApplicationException {
		MetadataDataset dataset = findDataset(datasets, datasetName, true);
		try {
			QuerySql query = dataset.getDataset().loadQuery(group.getName(), dataset.getPack());

			List<String> columnNames = getDatasetColumnNames(query);
			List<List<String>> datas = dataset.getPack().executeQuery(null, null, "Default", query, new ArrayList<List<String>>());

			List<Entity> entities = buildEntities(dataset, columnNames, datas);
			return buildCollection(entities);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable execute the datasets '" + datasetName + "'", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
	}

	public List<String> getDatasetColumnNames(String datasetName, MetadataDataset dataset) throws ODataApplicationException {
		try {
			QuerySql query = dataset.getDataset().loadQuery(group.getName(), dataset.getPack());
			return getDatasetColumnNames(query);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ODataApplicationException("Unable to get dataset's columns for '" + datasetName + "'", HttpStatusCode.EXPECTATION_FAILED.getStatusCode(), Locale.ENGLISH);
		}
	}

	private List<String> getDatasetColumnNames(QuerySql query) {
		List<String> columnNames = new ArrayList<>();
		if (query.getSelect() != null) {
			for (IDataStreamElement element : query.getSelect()) {
				columnNames.add(element.getName());
			}
		}
		return columnNames;
	}

	private List<Entity> buildEntities(MetadataDataset dataset, List<String> columnNames, List<List<String>> datas) {
		List<Entity> entities = new ArrayList<>();
		for (int i = 0; i < datas.size(); i++) {
			List<String> row = datas.get(i);

			Entity entity = new Entity();
			entity.setType(new FullQualifiedName(MetadataEdmProvider.NAMESPACE, getDatasetName(dataset, false)).getFullQualifiedNameAsString());

			boolean isIdSet = false;
			for (int j = 0; j < row.size(); j++) {
				String columnName = columnNames.get(j);

				entity.addProperty(new Property(null, columnName, ValueType.PRIMITIVE, row.get(j)));
				if (!isIdSet) {
					entity.setId(Utils.createId(entity, columnName));
					isIdSet = true;
				}
			}

			entities.add(entity);
		}
		return entities;
	}

	public Entity getDatasetValue(List<EntityKey> keys) throws ODataApplicationException {
		// String datasetName = extractDatasetName(keys);
		// String datasetId = /* extractDatasetId(keys) */"";

		return null;
	}

	private int extractMetadataId(List<EntityKey> keys) throws ODataApplicationException {
		if (keys != null) {
			for (EntityKey key : keys) {
				if (key.getEdmEntityType().getName().equals(MetadataEdmProvider.ET_METADATA_NAME) && key.getKeyParams() != null) {
					for (UriParameter param : key.getKeyParams()) {
						if (param.getName().equals(MetadataEdmProvider.ID)) {
							try {
								return Integer.parseInt(param.getText());
							} catch (Exception e) {
								throw new ODataApplicationException("The ID parameter for metadata '" + param.getText() + "' is not valid.", HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
							}
						}
					}
				}
			}
		}

		throw new ODataApplicationException("The 'ID' parameter for metadata can not be found.", HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
	}

	private String extractModelName(List<EntityKey> keys) throws ODataApplicationException {
		return extractName(MetadataEdmProvider.ET_BUSINESS_MODEL_NAME, keys);
	}

	private String extractPackageName(List<EntityKey> keys) throws ODataApplicationException {
		return extractName(MetadataEdmProvider.ET_BUSINESS_PACKAGE_NAME, keys);
	}

	private String extractTableName(List<EntityKey> keys) throws ODataApplicationException {
		return extractName(MetadataEdmProvider.ET_BUSINESS_TABLE_NAME, keys);
	}

	private String extractColumnName(List<EntityKey> keys) throws ODataApplicationException {
		return extractName(MetadataEdmProvider.ET_COLUMN_NAME, keys);
	}

	private String extractName(String entityType, List<EntityKey> keys) throws ODataApplicationException {
		if (keys != null) {
			for (EntityKey key : keys) {
				if (key.getEdmEntityType().getName().equals(entityType) && key.getKeyParams() != null) {
					String value = extractName(key);
					if (value != null) {
						return value;
					}
					else {
						break;
					}
				}
			}
		}

		throw new ODataApplicationException("The 'NAME' parameter for '" + entityType + "' can not be found.", HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
	}

	// private String extractDatasetName(List<EntityKey> keys) throws ODataApplicationException {
	// if (keys != null) {
	// for (EntityKey key : keys) {
	// if (key.getEdmEntityType().getName().startsWith(MetadataManager.PREFIX_DATASET) && key.getKeyParams() != null) {
	// String value = extractName(key);
	// if (value != null) {
	// return value;
	// }
	// else {
	// break;
	// }
	// }
	// }
	// }
	//
	// throw new ODataApplicationException("The 'NAME' parameter for dataset can not be found.", HttpStatusCode.UNAUTHORIZED.getStatusCode(), Locale.ROOT);
	// }

	private String extractName(EntityKey key) {
		for (UriParameter param : key.getKeyParams()) {
			if (param.getName().equals(MetadataEdmProvider.NAME)) {
				return param.getText().replaceAll("'", "");
			}
		}

		return null;
	}
}
