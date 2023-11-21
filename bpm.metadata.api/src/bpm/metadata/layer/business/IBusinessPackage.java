package bpm.metadata.layer.business;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.IQueryExecutor;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.resource.IResource;
import bpm.vanilla.platform.core.IVanillaContext;

public interface IBusinessPackage {

	/**
	 * return the BusinessPackage name
	 * @return
	 */
	public String getName();
	
	/**
	 * return the BusinessPackage description
	 * @return
	 */
	public String getDescription();

	/**
	 * return the  BusinessTable of the Package
	 * @return
	 */
	public List<IBusinessTable> getBusinessTables(String groupName);
	
	/**
	 * return the BusinessTable named name
	 * @param name
	 * @return
	 */
	public IBusinessTable getBusinessTable(String groupName, String name);
	
	/**
	 * return the  IResource of the Package
	 * @return
	 * @deprecated use getResource(String groupName)
	 */
	public List<IResource> getResources();
	
	public List<IResource> getResources(String groupName);
	
	/**
	 * return the Resource named name
	 * @param name
	 * @return
	 * @deprecated
	 */
	public IResource getResourceByName(String name);
	
	/**
	 * return the Resource named name
	 * @param name
	 * @return
	 */
	public IResource getResourceByName(String groupName, String name);
	
	
	/**
	 * execute a query for the lister Columns(they should be present in the package)
	 * the query is filktered by MOdelRelations
	 * condition are applyed to filter the result
	 * 
	 * not yet implemented
	 * 
	 * @param select
	 * @param condition
	 * @param orderBy can be null or the specify the orderBy clause
	 * @return
	 */
	//public List<List<String>> executeQuery(List<IDataStreamElement> select, HashMap<ListOfValue, String> condition, List<AggregateFormula> aggs, List<IDataStreamElement> orderBy) throws Exception;
	
	
	/**
	 * get the  query for the list of  Columns(they should be present in the package)
	 * the query is filktered by MOdelRelations
	 * condition are applyed to filter the result
	 * 
	 * not yet implemented
	 * 
	 * @param select
	 * @param condition
	 * @param orderBy can be null or the specify the orderBy clause
	 * @return
	 */
	//public String getQuery(List<IDataStreamElement> select, HashMap<ListOfValue, String> condition, List<AggregateFormula> aggs, List<IDataStreamElement> orderBy) throws MetaDataException;


	/**
	 * return true if this an OlapDataSource
	 */
	public boolean isOnOlapDataSource();

	/**
	 * return the Query relative to the DataSource(SQL or MDX, depends of the dataSource)
	 * 
	 * @param vanillaGroupWeight
	 * @param vanillaCtx
	 * @param query
	 * @param promptValues
	 * @return
	 * @throws Exception
	 */
	public String getQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, IQuery query, List<List<String>> promptValues) throws Exception;

	/**
	 * same as getQUery() but instead of generating but return a simple objet that will
	 * holds the real query and its computed Weight
	 * 
	 * the method wont fail because of weight because we dont perform the comparison
	 * 
	 * @param vanillaCtx
	 * @param query
	 * @param promptValues
	 * @return
	 * @throws Exception
	 */
	public EffectiveQuery evaluateQuery(IVanillaContext vanillaCtx, IQuery query, List<List<String>> promptValues) throws Exception;
	
	
	/**
	 * return a query where prompt values are dynamic(for jsp purpose, or report)
	 * the prompt name will be used as the variable name containing the value 
	 * @param query
	 * @return
	 * @throws Exception
	 */
//	public String getDynamicalQuery(IQuery query) throws Exception;
	
	
	public List<List<String>> executeQuery(Integer vanillaGroupWeight, IVanillaContext vanillaCtx, String connectionName, IQuery query, List<List<String>> promptsValues) throws Exception;
	
	
//	/**
//	 * execute the query on the specified connection for allGroups present in the list
//	 * @param connectionName
//	 * @param query
//	 * @return
//	 * @throws Exception
//	 */
//	public HashMap<String, List<List<String>>> burst(List<String> groupName, String connectionName, IQuery query, List<List<String>> promptValues) throws Exception;
	
	
	
	
	public List<List<String>> executeQuery(int limit, String connectionName, String query) throws Exception;
	
	public Integer countQuery(int limit, String connectionName, String query) throws Exception;
	
//	public OLAPResult executeOlapQuery(QueryOlap query) throws Exception; 
	
	public List<String> getConnectionsNames(String groupName);
	
	public IConnection getConnection(String groupName, String name) throws Exception;
	
	/**
	 * return the BusinessTables ordered
	 * @param groupName
	 * @return
	 */
	public List<IBusinessTable> getOrderedTables(String groupName);
	
	/**
	 * 
	 * @param groupName
	 * @return all the explorables BusinessTables for the given group 
	 */
	public List<IBusinessTable> getExplorableTables(String groupName);
	
	
	/**
	 * 
	 * @param groupName
	 * @param currentTable 
	 * @return all the explorables BusinessTables for the given group from the currentTable 
	 */
	public List<IBusinessTable> getExplorableTables(String groupName, IBusinessTable currentTable);

	
	/**
	 * 
	 * @return true if the package can be explored
	 */
	public boolean isExplorable();
	
	/**
	 * 
	 * @return the firsts tables than will be available during an exploration
	 */
	public List<IBusinessTable> getFirstAccessibleTables(String groupName);
	
	/**
	 * 
	 * @param current
	 * @return an hashmap containing for all possible target the list of relations
	 */
	public HashMap<IBusinessTable, List<Relation>> getRelationsForExplorablesTable(String groupName, IBusinessTable current);
	
	/**
	 * 
	 * @return the IbusinessModel owning this package
	 */
	public IBusinessModel getBusinessModel();
	
	public void setGranted(String groupName, boolean value);
	
	public void setGranted(String groupName, String value);
	
	public HashMap<String, Boolean> getGrants();
	
	public List<String> getResourceName();
	
	public void addResource(IResource resource);
	
	public Integer getOrderPosition(String tablename);
	
	public void cleanBusinessTableContent();
	
	public void addBusinessTable(IBusinessTable table);
	
	public List<String> getBusinessTableName();
	
	public void order(String businessTableName, Integer position);
	
	public void order(String businessTableName, String position);
	
	public void removeResource(IResource r);
	
	public void setBusinessModel(BusinessModel model);
	
	public boolean isGrantedFor(String groupName);
	
	public String getXml();
	
	public String getOuputName(Locale l);
	
	public List<IBusinessTable> getExplorableTables();
	
	public List<IBusinessTable> getFirstAccessibleTables();
	
	public void setName(String name);
	
	public void setDescription(String description);
	
	public void setExplorable(boolean isExplorable);
	
	public void setExplorable(String isExplorable);
	
	public void removeAccessible(IBusinessTable t);
	
	public void addAccessible(IBusinessTable t);
	
	public void removeBusinessTable(IBusinessTable table);
	
	public List<IDataSource> getDataSources(String groupName);
	
	public void setOutputName(Locale l, String value);
	
	public boolean isMonoDataSource();
	
	public IQueryExecutor getQueryExecutor();
	
	public List<SavedQuery> getSavedQueries();
	
	public void addSavedQuery(SavedQuery savedQuery);

	public void removeSavedQuery(SavedQuery savedQuery);

	public EffectiveQuery evaluateQuery(IVanillaContext object, IQuery query, List<List<String>> prompts, boolean removePromptWithoutValues) throws Exception;
}
