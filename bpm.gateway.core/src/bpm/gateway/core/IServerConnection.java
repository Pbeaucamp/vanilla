package bpm.gateway.core;

import org.dom4j.Element;

import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.exception.ServerException;

/**
 * interface for all Server Connection
 * @author LCA
 *
 */
public interface IServerConnection {
	public String getName();
	
	/**
	 * establish a connection 
	 * @throws ServerException
	 */
	public void connect(DocumentGateway document) throws ServerException;
	
	/**
	 * close the Connection  
	 * @throws ServerException
	 */
	public void disconnect() throws JdbcException;
	
	/**
	 * 
	 * @return true if all parameter have been set
	 */
	public boolean isSet() ;
	
	
	/**
	 * 
	 * @return true if the SQLConnection on the SQL Database is still opened
	 */
	public boolean isOpened();
	
	
	/**
	 * 
	 * @return the Server 
	 */
	public Server getServer();

	/**
	 * 
	 * @return element to save the document as xml
	 */
	public Element getElement();

	public String getAutoDocumentationDetails();

		
}
