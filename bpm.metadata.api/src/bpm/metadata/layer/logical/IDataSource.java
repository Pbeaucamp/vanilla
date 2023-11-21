package bpm.metadata.layer.logical;


import java.util.List;

import javax.swing.plaf.BorderUIResource.LineBorderUIResource;

import bpm.metadata.MetaData;
import bpm.metadata.layer.physical.IConnection;

/**
 * A datasource contains a list of IDataStreams
 * and a list of Relations between those IDataStreams
 * 
 * @author LCA
 *
 */
public interface IDataSource {
	/**
	 * DataSource constant for SQL DataSource
	 */
	public static final int SQL = 0;
	public static final int XML = 1;
	public static final int MDX = 2;
	public static final int XLS = 3;
	public static final int OLAP = 4;
	/**
	 * return the list of all DataStreams 
	 * @return
	 */
	public List<IDataStream> getDataStreams();
	
	/**
	 * add a DataStream
	 * @param stream
	 */
	public void add(IDataStream stream);
	
	
	/**
	 * remove a DataStream
	 * @param stream
	 */
	public List<Relation> remove(IDataStream stream);
	
	/**
	 * removeAlla dataStream
	 */
	public void removeAll();
	
	/**
	 * return the DataSource name
	 * @return
	 */
	public String getName();
	
	/**
	 * return the DataSource type
	 * @return
	 */
	public int getType();
	
	/**
	 * return the specified dataStream
	 * @param name
	 * @return
	 */
	public IDataStream getDataStreamNamed(String name);
	
	/**
	 * 
	 * @return the list of Relations
	 */
	public List<Relation> getRelations();
	
	/**
	 * Add a new relation to the dataSOurce
	 * @param relation
	 */
	public void addRelation(Relation relation);
	
	/**
	 * remove a Relation from the DataSource
	 * @param relation
	 */
	public void removeRelation(Relation relation);
	
	/**
	 * return an Xml String 
	 * @return
	 */
	public String getXml();
	
	
	/**
	 * 
	 */
	public List<List<String>> executeQuery(String connectionName, String query, int maxRows)  throws Exception;
	
	public Integer countQuery(String connectionName, String query, int maxRows)  throws Exception;
	
	
//	public List<List<String>> executeQuery(String connectionName, String query)  throws Exception;
	
	public List<List<String>> executeQuery(String connectionName, String query, boolean[] flags, int maxRows)  throws Exception;
	
	public MetaData getMetaDataModel();
	
	public List<IConnection> getConnections(String groupName);
	
	/**
	 * add the given connection to the data
	 * @param connection
	 * @throws Exception
	 * @deprecated  
	 * @see #addAlternateConnection(IConnection, boolean)
	 */
	public void addAlternateConnection(IConnection connection) throws Exception;
	
	/**
	 * add the given connection to the data
	 * @param connection
	 * @param saveConnectionInModel :  if true, the connection will be stored in the XML
	 */
	public void addAlternateConnection(IConnection connection, 	boolean saveConnectionInModel);
	
	/**
	 * return all the available connection for this datasource
	 * @return
	 */
	public List<String> getConnectionNames(String groupName);

	/**
	 * remove the given IConnection from the connections
	 * be carefull because if the model has been built on the given connection,
	 * the model will become inconsistent, it should be rebuilt
	 * 
	 * @param connection
	 */
	public void removeConnection(IConnection connection);

	public void setDefaultConnection(IConnection connection);

	
	
}
