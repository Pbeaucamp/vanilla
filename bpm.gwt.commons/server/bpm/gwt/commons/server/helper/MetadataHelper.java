package bpm.gwt.commons.server.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin;
import bpm.gwt.commons.shared.fmdt.metadata.ColumnJoin.Outer;
import bpm.gwt.commons.shared.fmdt.metadata.Metadata;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataChart;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataFilter;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataModel;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPackage;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataPrompt;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQueries;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataQuery;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataRelation;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResource;
import bpm.gwt.commons.shared.fmdt.metadata.MetadataResources;
import bpm.gwt.commons.shared.fmdt.metadata.TableRelation;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.metadata.MetaData;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.FactorySQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLRelation;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.metadata.tools.SecurityHelper;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.beans.chart.SavedChart;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataHelper {

	private static final String JAVA_INTEGER = "java.lang.Integer";
	private static final String JAVA_DOUBLE = "java.lang.Double";
	private static final String JAVA_FLOAT = "java.lang.Float";
	private static final String JAVA_LONG = "java.lang.Long";
	private static final String JAVA_STRING = "java.lang.String";
	private static final String JAVA_DATE = "java.util.Date";
	private static final String JAVA_BOOLEAN = "java.lang.Boolean";
	private static final String JAVA_OBJECT = "java.lang.Object";

	// CONVERSION WEB -> API

	public static MetaData convertMetadata(Metadata metadata, List<String> groupNames) throws Exception {
		return convertMetadata(metadata, null, groupNames);
	}

	public static MetaData convertMetadata(Metadata metadata, List<DatabaseTable> tablesFiltered, List<String> groupNames) throws Exception {

		Datasource datasource = metadata.getDatasource();
		DatasourceJdbc jdbc = (DatasourceJdbc) datasource.getObject();
		List<MetadataModel> models = metadata.getModels();

		String driverName = CommonConstants.jdbcLabels.get(jdbc.getDriver());
		
		SQLConnection scon = new SQLConnection();
		scon.setName("Default");
		scon.setDriverName(driverName);
		scon.setDataBaseName(jdbc.getDatabaseName());
		scon.setHost(jdbc.getHost());
		scon.setPortNumber(jdbc.getPort());
		scon.setUseFullUrl(jdbc.getFullUrl());
		scon.setFullUrl(jdbc.getUrl());
		scon.setUsername(jdbc.getUser());
		scon.setPassword(jdbc.getPassword());

		SQLDataSource sds = FactorySQLDataSource.getInstance().createDataSource(scon);
		sds.setName(datasource.getName());
		sds.getConnection().connect();

		List<IDataStream> tables = new ArrayList<>();
		if (tablesFiltered != null || datasource.getTables() != null) {
			for (DatabaseTable table : tablesFiltered != null ? tablesFiltered : datasource.getTables()) {

				IDataStream str = sds.add(sds.getConnection().getTable(table.getName()));

				if (str != null) {
					List<IDataStreamElement> elementsToRemove = new ArrayList<>();
					for (IDataStreamElement el : str.getElements()) {
						boolean found = false;
						for (DatabaseColumn column : table.getColumns()) {
							if (el.getName().equals(column.getOriginName())) {
//								el.setDefaultMeasureBehavior(column.getMeasureBehavior());
//								el.setType(column.getMetadataType());
								found = true;
								break;
							}
						}
						
						if (!found) {
							elementsToRemove.add(el);
						}
					}
					
					for (IDataStreamElement el : elementsToRemove) {
						str.removeDataStreamElement(el);
					}
					
					tables.add(str);
				}
			}
		}

		MetaData newMetadata = new MetaData();
		newMetadata.setD4cServerId(metadata.getD4cServerId());
		newMetadata.setD4cOrganisation(metadata.getD4cOrganisation());
		newMetadata.addDataSource(sds);

		for (MetadataModel model : models) {

			BusinessModel newModel = new BusinessModel();
			newModel.setName(model.getName());

			for (MetadataPackage pack : model.getPackages()) {

				BusinessPackage newPack = new BusinessPackage();
				newPack.setName(pack.getName());
				newPack.setDescription(pack.getDescription());

				for (DatabaseTable table : pack.getTables()) {

					SQLBusinessTable bt = new SQLBusinessTable(table.getCustomName());

					for (DatabaseColumn col : table.getColumns()) {
						IDataStreamElement element = getStreamElement(tables, col.getOriginName());
						if (element != null) {
							bt.addColumn(element);
						}
					}

					newModel.addBusinessTable(bt);
					newPack.addBusinessTable(bt);
				}

				if (pack.getResources() != null && pack.getResources().getResources() != null) {
					for (MetadataResource resource : pack.getResources().getResources()) {
						IDataStreamElement element = getStreamElement(tables, resource.getColumn().getOriginName());
						
						IResource newResource = convertResource(element, resource);

						if (newResource != null) {
							newPack.addResource(newResource);
							newModel.addResource(newResource);
							newMetadata.addResource(newResource);
						}
					}
				}

				newModel.addBusinessPackage(newPack);
			}

			if (model.getRelation() != null && model.getRelation().getRelations() != null) {
				for (TableRelation relation : model.getRelation().getRelations()) {

					SQLRelation newRelation = new SQLRelation();

					IDataStream tableLeft = getTable(jdbc.getDriver(), relation.getLeftTable().getName(), tables);
					IDataStream tableRight = getTable(jdbc.getDriver(), relation.getRightTable().getName(), tables);

					for (ColumnJoin join : relation.getJoins()) {

						IDataStreamElement leftCol = getStreamElement(tableLeft, join.getLeftColumn().getOriginName());
						IDataStreamElement rightCol = getStreamElement(tableRight, join.getRightColumn().getOriginName());
						if (leftCol != null && rightCol != null) {
							newRelation.add(leftCol, rightCol, join.getOuter().getValue());
						}
					}

					sds.addRelation(newRelation);
					newModel.addRelation(newRelation);
				}
			}

			newMetadata.addBusinessModel(newModel);
		}

		if (groupNames != null) {
			if (newMetadata.getDataSources() != null) {
				for (AbstractDataSource ds : newMetadata.getDataSources()) {
					ds.securizeConnection("Default", groupNames);
				}
			}
			
			for (String groupName : groupNames) {
				SecurityHelper.grantGlobal(newMetadata, groupName, true);
			}
		}

		return newMetadata;
	}

	private static IResource convertResource(IDataStreamElement element, MetadataResource resource) {
		String name = resource.getName();

		if (resource instanceof MetadataPrompt) {
			MetadataPrompt prompt = (MetadataPrompt) resource;
			
			Prompt newPrompt = new Prompt();
			newPrompt.setName(name);
			newPrompt.setOperator(prompt.getOperator());
			newPrompt.setOrigin(element);
			newPrompt.setGotoDataStreamElement(element);
			
			return newPrompt;
		}
		else if (resource instanceof MetadataFilter) {
			MetadataFilter filter = (MetadataFilter) resource;
			
			Filter newFilter = new Filter();
			newFilter.setName(name);
			newFilter.setValues(filter.getValues());
			newFilter.setOrigin(element);
			
			return newFilter;
		}
		return null;
	}

	private static IDataStream getTable(String driver, String tableName, List<IDataStream> tables) {
		if (tables != null) {
			for (IDataStream table : tables) {
				if (table.getName().equals(tableName)) {
					return table;
				}
			}
		}
		return null;
	}

	private static IDataStreamElement getStreamElement(List<IDataStream> tables, String columnOriginName) {
		if (tables != null) {
			for (IDataStream table : tables) {
				IDataStreamElement element = getStreamElement(table, columnOriginName);
				if (element != null) {
					return element;
				}
			}
		}

		return null;
	}

	private static IDataStreamElement getStreamElement(IDataStream table, String columnOriginName) {
		if (table.getElements() != null) {
			for (IDataStreamElement e : table.getElements()) {
				if (e.getOrigin().getName().equals(columnOriginName)) {
					return e;
				}
			}
		}
		return null;
	}

	// CONVERSION API -> WEB

	public static Metadata convertMetadata(RepositoryItem itemMetadata, MetaData metadata, String groupName) throws ServiceException {
		int itemId = itemMetadata.getId();
		String name = itemMetadata.getName();
		String description = itemMetadata.getComment();

		Metadata newMetadata = new Metadata(itemId, name, description);
		newMetadata.setD4cServerId(metadata.getD4cServerId());
		newMetadata.setD4cOrganisation(metadata.getD4cOrganisation());

		Collection<AbstractDataSource> datasources = metadata.getDataSources();
		Datasource datasource = null;
		if (datasources != null) {
			datasource = convertDatasource(datasources);
		}
		newMetadata.setDatasource(datasource);

		List<MetadataModel> models = new ArrayList<>();
		if (metadata.getBusinessModels() != null) {
			for (IBusinessModel model : metadata.getBusinessModels()) {
				models.add(convertModel(datasource, model, groupName));
			}
		}
		newMetadata.setModels(models);

		return newMetadata;
	}

	private static Datasource convertDatasource(Collection<AbstractDataSource> datasources) throws ServiceException {
		if (datasources != null && !datasources.isEmpty()) {
			for (AbstractDataSource datasource : datasources) {
				if (datasource instanceof SQLDataSource) {
					SQLDataSource sqlDatasource = (SQLDataSource) datasource;

					SQLConnection scon = (SQLConnection) sqlDatasource.getConnection();

					String driverClass = null;
					try {
						driverClass = findDriverClass(scon.getDriverName());
					} catch(Exception e) {
						throw new ServiceException("Driver " + scon.getDriverName() + " not found");
					}
					
					DatasourceJdbc jdbc = new DatasourceJdbc();
					jdbc.setDriver(driverClass);
					jdbc.setDatabaseName(scon.getDataBaseName());
					jdbc.setHost(scon.getHost());
					jdbc.setPort(scon.getPortNumber());
					jdbc.setFullUrl(scon.isUseFullUrl());
					jdbc.setUrl(scon.getFullUrl());
					jdbc.setUser(scon.getUsername());
					jdbc.setPassword(scon.getPassword());

					List<IDataStream> dbTables = sqlDatasource.getDataStreams();

					List<DatabaseTable> newTables = new ArrayList<>();
					if (dbTables != null) {
						for (IDataStream table : dbTables) {
							newTables.add(convertTable(table));
						}
					}

					Datasource newDatasource = new Datasource();
					newDatasource.setName(datasource.getName());
					newDatasource.setType(DatasourceType.JDBC);
					newDatasource.setTables(newTables);
					newDatasource.setObject(jdbc);
					return newDatasource;
				}
				else {
					throw new ServiceException("This application doesn't support United Olap Datasource for now.");
				}
			}
		}

		throw new ServiceException("This metadata does not have a valid datsource.");
	}

	private static String findDriverClass(String driverName) throws Exception {
		return ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(driverName).getClassName();
//		HashMap<String, String> drivers = CommonConstants.jdbcLabels;
//		for (String driverClass : drivers.keySet()) {
//			if (drivers.get(driverClass).equals(driverName)) {
//				return driverClass;
//			}
//		}
//		return null;
	}

	private static MetadataModel convertModel(Datasource datasource, IBusinessModel model, String groupName) {
		String modelName = model.getName();
		List<IBusinessPackage> packages = model.getBusinessPackages(groupName);
		List<IBusinessTable> tables = model.getBusinessTables();
		List<Relation> relations = model.getRelations();

		List<DatabaseTable> newTables = new ArrayList<>();
		if (tables != null) {
			for (IBusinessTable table : tables) {
				newTables.add(convertTable(table, groupName));
			}
		}

		List<MetadataPackage> newPackages = new ArrayList<>();
		if (packages != null) {
			for (IBusinessPackage pack : packages) {
				String packageName = pack.getName();
				String packageDescription = pack.getDescription();
				List<IBusinessTable> packTables = pack.getBusinessTables(groupName);
				List<IResource> resources = pack.getResources(groupName);

				List<DatabaseTable> newPackTables = findTables(newTables, packTables);
				List<MetadataResource> newResources = convertResources(datasource.getTables(), resources);
				
				List<MetadataQuery> newQueries = new ArrayList<>();
				List<SavedQuery> queries = pack.getSavedQueries();
				if (queries != null && !queries.isEmpty()) {
					for (SavedQuery query : queries) {
						List<MetadataChart> newCharts = new ArrayList<>();
						if (query.hasChart()) {
							List<SavedChart> charts = query.loadChart();
							for (SavedChart chart : charts) {
								newCharts.add(new MetadataChart(chart.getTitle(), chart));
							}
						}
						newQueries.add(new MetadataQuery(query.getName(), newCharts));
					}
				}

				MetadataResources resourcesContainer = new MetadataResources(newResources);
				MetadataQueries queriesContainer = new MetadataQueries(newQueries);
				newPackages.add(new MetadataPackage(packageName, packageDescription, newPackTables, resourcesContainer, queriesContainer));
			}
		}

		MetadataRelation newRelation = new MetadataRelation();
		if (relations != null) {
			for (Relation rel : relations) {
				if (rel instanceof SQLRelation) {
					SQLRelation relation = (SQLRelation) rel;

					DatabaseTable leftTable = findTable(relation.getLeftTableName(), datasource.getTables());
					DatabaseTable rightTable = findTable(relation.getRightTableName(), datasource.getTables());

					TableRelation tableRelation = new TableRelation(leftTable, rightTable);
					for (Join join : relation.getJoins()) {

						DatabaseColumn leftCol = findColumn(leftTable, join.getLeftName());
						DatabaseColumn rightCol = findColumn(rightTable, join.getRightName());
						if (leftCol != null && rightCol != null) {
							tableRelation.addJoin(new ColumnJoin(leftCol, rightCol, Outer.valueOf(join.getOuter())));
						}
					}

					newRelation.addRelation(tableRelation);
				}
			}
		}

		MetadataModel newModel = new MetadataModel(modelName);
		newModel.setTables(newTables);
		newModel.setPackages(newPackages);
		newModel.setRelation(newRelation);
		return newModel;
	}

	private static DatabaseTable convertTable(IDataStream table) {
		String tableName = table.getName();
		Collection<IDataStreamElement> columns = table.getElements();

		List<DatabaseColumn> newColumns = convertColumns(columns);

		DatabaseTable newTable = new DatabaseTable();
		newTable.setName(tableName);
		newTable.setColumns(newColumns);
		return newTable;
	}

	private static DatabaseTable convertTable(IBusinessTable table, String groupName) {
		String customTableName = table.getName();
		Collection<IDataStreamElement> columns = table.getColumns(groupName);

		List<DatabaseColumn> newColumns = convertColumns(columns);
		
		// Try to extract table name from columns
		String tableName = customTableName;
		if (newColumns != null) {
			for (DatabaseColumn col : newColumns) {
				if (col.getParentSQLOriginName() != null && !col.getParentSQLOriginName().isEmpty()) {
					tableName = col.getParentSQLOriginName();
				}
				break;
			}
		}
		
		DatabaseTable newTable = new DatabaseTable();
		newTable.setName(tableName);
		newTable.setCustomName(customTableName);
		newTable.setColumns(newColumns);
		return newTable;
	}

	private static List<DatabaseColumn> convertColumns(Collection<IDataStreamElement> columns) {
		List<DatabaseColumn> newColumns = new ArrayList<>();
		if (columns != null) {
			for (IDataStreamElement el : columns) {
				if (!(el instanceof ICalculatedElement)) {
					DatabaseColumn newColumn = new DatabaseColumn();
					newColumn.setCustomName(el.getName());
					newColumn.setOriginName(el.getOriginName());
					newColumn.setName(splitColumnName(el.getOriginName()));
					newColumn.setParentSQLOriginName(el.getDataStream() != null ? el.getDataStream().getName() : null);
//					newColumn.setMetadataType(el.getType());
//					newColumn.setMeasureBehavior(el.getDefaultMeasureBehavior());
	
					newColumns.add(newColumn);
				}
			}
		}
		return newColumns;
	}

	private static List<MetadataResource> convertResources(List<DatabaseTable> tables, List<IResource> resources) {
		List<MetadataResource> newResources = new ArrayList<>();
		if (resources != null) {
			for (IResource resource : resources) {
				if (resource instanceof Prompt) {
					newResources.add(convertPrompt(tables, (Prompt) resource));
				}
				else if (resource instanceof Filter) {
					newResources.add(convertFilter(tables, (Filter) resource));
				}
			}
		}
		return newResources;
	}

	private static MetadataPrompt convertPrompt(List<DatabaseTable> tables, Prompt resource) {
		String name = resource.getName();
		String operator = resource.getOperator();

		String tableName = resource.getOriginDataStreamName();
		String columnName = resource.getOriginDataStreamElementName();
		DatabaseColumn column = findColumn(tables, tableName, columnName);

		return new MetadataPrompt(name, operator, column);
	}

	private static MetadataFilter convertFilter(List<DatabaseTable> tables, Filter resource) {
		String name = resource.getName();
		List<String> values = resource.getValues();

		String tableName = resource.getDataStreamName();
		String columnName = resource.getDataStreamElementName();
		DatabaseColumn column = findColumn(tables, tableName, columnName);

		return new MetadataFilter(name, column, values);
	}

	private static String splitColumnName(String originName) {
		if (originName.contains(".")) {
			String name = originName.substring(originName.indexOf(".") + 1);
			return splitColumnName(name);
		}
		return originName;
	}

	private static List<DatabaseTable> findTables(List<DatabaseTable> newTables, List<IBusinessTable> packTables) {
		List<DatabaseTable> tablesFound = new ArrayList<>();
		if (newTables != null && packTables != null) {
			for (IBusinessTable bTable : packTables) {
				for (DatabaseTable newTable : newTables) {
					if (bTable.getName().equals(newTable.getName()) || bTable.getName().equals(newTable.getCustomName())) {
						tablesFound.add(newTable);
						break;
					}
				}
			}
		}
		return tablesFound;
	}

	private static DatabaseTable findTable(String tableName, List<DatabaseTable> tables) {
		if (tables != null) {
			for (DatabaseTable table : tables) {
				if (table.getName().equals(tableName)) {
					return table;
				}
			}
		}
		return null;
	}

	private static DatabaseColumn findColumn(DatabaseTable table, String columnName) {
		if (table != null && table.getColumns() != null) {
			for (DatabaseColumn column : table.getColumns()) {
				if (column.getCustomName().equals(columnName)) {
					return column;
				}
			}
		}
		return null;
	}

	private static DatabaseColumn findColumn(List<DatabaseTable> tables, String tableName, String columnName) {
		DatabaseTable table = findTable(tableName, tables);
		return findColumn(table, columnName);
	}

	// Helper methods

	// Only support String for now

	public static String getValue(String colName, String type, ResultSet jdbcResult) throws SQLException {
		if (type != null) {
			switch (type) {
			case JAVA_INTEGER:
			case JAVA_DOUBLE:
			case JAVA_FLOAT:
			case JAVA_LONG:
				return jdbcResult.getString(colName);
			case JAVA_DATE:
			case JAVA_BOOLEAN:
			case JAVA_STRING:
				return jdbcResult.getString(colName);
			case JAVA_OBJECT:
				return jdbcResult.getString(colName);

			default:
				return jdbcResult.getString(colName);
			}
		}
		else {
			return jdbcResult.getString(colName);
		}
	}
}
