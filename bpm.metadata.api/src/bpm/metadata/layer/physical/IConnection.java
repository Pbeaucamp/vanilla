package bpm.metadata.layer.physical;

import java.sql.Statement;
import java.util.List;

import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;

public interface IConnection {
	
	/**
	 * used to init context of requested
	 * @param conf
	 */
	public void configure(Object conf);
	
	/**
	 * connect to the dataSource and return the list of ITable
	 * contained by this dataSource
	 * @return
	 */
	public List<ITable> connect() throws Exception;
	
	public ITable getTable(String name) throws Exception;
	
	public ITable getTableByName(String name) throws Exception;
	/**
	 * test the connection
	 */
	public void test() throws Exception;

	
	public String getName();
	
	public String getXml();
	
	/**
	 * 
	 * @return an SQL Statement on JDBC Connection set as a stream 
	 * 
	 * This statement must be closed once it is used and cannot perform
	 * more than one query at once
	 * 
	 */
	public VanillaPreparedStatement getStreamStatement() throws Exception;
	
	/**
	 * execute the given selection query and return its result
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public List<List<String>> executeQuery(String query, Integer maxRows, boolean[] flags) throws Exception;
	
	public Integer countQuery(String query, Integer maxRows) throws Exception;
	
	//public List<List<String>> executeQuery(IQUery query, Integer maxRows) throws Exception;
	
	
	
}
