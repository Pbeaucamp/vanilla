package bpm.metadata.birt.oda.runtime.impl;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.metadata.layer.physical.sql.SQLConnection;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection extends AbstractFmdtConnection implements IConnection
{

    /*
     * jdbc objects
     */
   private VanillaJdbcConnection jdbcConnection;
   private SQLConnection sqlConnection; //the fmdt object;
    
    
    private static int count = 0;
    
    
    
   

	protected VanillaJdbcConnection getJdbcConnection() {
		return jdbcConnection;
	}

	protected void setJdbcConnection(VanillaJdbcConnection jdbcConnection2) {
		this.jdbcConnection = jdbcConnection2;
	}

    
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{


		super.open(connProperties);
		
		try {
			sqlConnection = (SQLConnection)fmdtPackage.getConnection(groupName, connectionName);
			
			//jdbcConnection = ((SQLConnection)fmdtPackage.getConnection(groupName, connectionName)).getJdbcConnection();
			jdbcConnection = sqlConnection.getJdbcConnection();
			m_isOpen = true;      
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e);
		}
		
	    
 	}

	

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#close()
	 */
	public void close() throws OdaException
	{
		//XXX not really closing anymore, but returning to pool
	    m_isOpen = false;
	    if (jdbcConnection != null){
			try{
				//jdbcConnection.close();
				//System.out.println("debug : not closing");
				sqlConnection.returnJdbcConnection(jdbcConnection);
				jdbcConnection = null;
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	}

	
	
	@Override
	protected void finalize() throws Throwable {
	
		m_isOpen = false;
		if (jdbcConnection != null){
			try{
				sqlConnection.returnJdbcConnection(jdbcConnection);
				bpm.connection.manager.connection.ConnectionManager.getInstance().returnJdbcConnection(jdbcConnection);
				jdbcConnection = null;
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		super.finalize();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#isOpen()
	 */
	public boolean isOpen() throws OdaException
	{
		try{
			return m_isOpen && jdbcConnection != null && !jdbcConnection.isClosed();
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType ) throws OdaException
	{
        // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		VanillaPreparedStatement jdbcStatement = createJdbcStatement(jdbcConnection);	
		return new Query(this, jdbcStatement, fmdtPackage, connectionName, vanillaGroup);
	}
	public VanillaPreparedStatement createJdbcStatement(VanillaJdbcConnection jdbcConnection)throws OdaException{
		if (!isOpen()){
			throw new OdaException("Cannot create query on closed connection");
		}
		try{
			VanillaPreparedStatement jdbcStatement=  jdbcConnection.createStatement();
//			java.sql.Statement jdbcStatement = jdbcConnection.createStatement(
//					java.sql.ResultSet.TYPE_FORWARD_ONLY,
//					java.sql.ResultSet.CONCUR_READ_ONLY);
			try{
				jdbcStatement.setFetchSize(1024);
			}catch(SQLException ex){
//				jdbcStatement.setFetchSize(1);
				ex.printStackTrace();
			}
			return jdbcStatement;
		}catch(Exception e){
			e.printStackTrace();
			try{
				Logger.getLogger(getClass()).warn("-Error when fetching at Integer.MIN_VALUE on JDBC Statement");
				Logger.getLogger(getClass()).warn("-Try to set Fetch at 1");
				VanillaPreparedStatement jdbcStatement = jdbcConnection.createStatement(
						java.sql.ResultSet.TYPE_FORWARD_ONLY,
						java.sql.ResultSet.CONCUR_READ_ONLY);
				try{
					jdbcStatement.setFetchSize(Integer.MIN_VALUE);
				}catch(SQLException ex1){
					jdbcStatement.setFetchSize(1);
				}
				
				return jdbcStatement;
			}catch(SQLException ex){
				String errMsg = "SQLException : ";
				while (e != null) {
	                
	                errMsg += "\n\tErrorCode = "+ ex.getErrorCode();
	                errMsg += "\n\tSQLState = "+ ex.getSQLState();
	                errMsg += "\n\tMessage = "+ ex.getMessage();
	            
	                e = ex.getNextException();
	            }
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
	            throw new OdaException(new Exception(errMsg, e)); 
			}catch(Exception ex){
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new OdaException(ex);
			}		
		}
	}
	
}
