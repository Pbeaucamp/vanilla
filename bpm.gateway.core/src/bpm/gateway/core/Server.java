package bpm.gateway.core;

import java.util.List;

import org.dom4j.Element;

import bpm.gateway.core.exception.ServerException;

/**
 * This interface is the main interface to define 
 * InputDataStream/OutputDataStream providers
 * 
 * 
 * @author LCA
 *
 */
public interface Server {
	
	public static final String DATABASE_TYPE = "dataBase";
	public static final String FILE_TYPE = "file";
	public static final String FREEMETRICS_TYPE ="freemetrics";
	public static final String LDAP_TYPE ="ldap";
	public static final String CASSANDRA_TYPE = "Cassandra";
	public static final String HBASE_TYPE = "HBase";
	public static final String MONGODB_TYPE = "MongoDb";
	public static final String NOSQL_TYPE = "NoSQL";
	public static final String D4C_TYPE = "D4C";


	/**
	 * 
	 * @return the server name
	 */
	public String getName();
	
	
	/**
	 * 
	 * @return the server Description
	 */
	public String getDescription();

	
	/**
	 * 
	 * @return true if the connection succeed
	 */
	public boolean testConnection(DocumentGateway document) ;
	
	
	/**
	 * Establish a connection to the Server.
	 * Once connection is on, the server will be able to provide I/O -DataStream
	 */
	public void connect(DocumentGateway document) throws ServerException;
	
	
	/**
	 * Close the connection and release all used resources
	 */
	public void disconnect() throws ServerException;
	
	
		
	
	

	/**
	 * 
	 * @return one of the Constant for Servertype
	 */
	public String getType();
	
	/**
	 * 
	 * @return all the Server Connections
	 */
	public List<IServerConnection> getConnections();
	
//	/**
//	 * return the active connection
//	 * @return
//	 */
//	public IServerConnection getCurrentConnection();


	/**
	 * replace the current connection by this one
	 * socket must be in connections availables for that server
	 * @param socket the connection to set as current
	 * @throws ServerException
	 */
	public void setCurrentConnection(IServerConnection socket)throws ServerException;


	/**
	 * remove this connection from the list
	 * set the current Connection to null if it is this one
	 * @param sock
	 */
	public void removeConnection(IServerConnection sock);

	
	/**
	 * add this connection as alternate
	 * @param connection
	 */
	public void addConnection(IServerConnection connection);
	
	/**
	 * add a specific connection that will be used only for the given adapter
	 * @param adapter
	 * @param connection
	 */
	public void addOverridenConnection(Object adapter, IServerConnection connection);
	/**
	 * remove the connection for the given adapter
	 * @param adapter
	 * @param connection
	 */
	public void removeOverridenConnection(Object adapter);
	/**
	 * return the connection for the given adapter,
	 * if it does not exists, the currentConnection is returned
	 * @param adapter
	 * @return
	 */
	public IServerConnection getCurrentConnection(Object adapter);
	
	/**
	 * 
	 * @return a dom4j element for saving the resource
	 */
	public Element getElement();
}
