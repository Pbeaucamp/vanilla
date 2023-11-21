package bpm.nosql.oda.runtime.impl;

import java.util.Properties;

import org.apache.cassandra.thrift.AuthenticationException;
import org.apache.cassandra.thrift.AuthorizationException;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.thrift.TException;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.nosql.oda.runtime.cassandraUtility.CassandraInterface;

import com.ibm.icu.util.ULocale;

public class CassandraConnection implements IConnection{

	private boolean c_isOpen = false;
	private String host;
	private int port;
	private String username;
	private String password;
	private String keyspace;
	private Properties connProperties;
	private CassandraInterface casInterface;
	
	@Override
	public void close() throws OdaException {

		c_isOpen = false;
	}

	@Override
	public void commit() throws OdaException {
		
	}

	@Override
	public int getMaxQueries() throws OdaException {
		return 0;
	}

	@Override
	public IDataSetMetaData getMetaData(String dataSetType) throws OdaException {
		return null;
	}

	@Override
	public boolean isOpen() throws OdaException {
		return c_isOpen;
	}

	@Override
	public CassandraQuery newQuery(String dataSetType) throws OdaException {
		return new CassandraQuery(this, connProperties, casInterface);
	}

	@Override
	public void open(Properties connProperties) throws OdaException {

	    this.username = connProperties.getProperty(Connection.USER);
	    this.password = connProperties.getProperty(Connection.PASSWORD);
	    this.host = connProperties.getProperty(Connection.HOST);
	    this.port = Integer.valueOf(connProperties.getProperty(Connection.PORT)).intValue();
	    this.keyspace = connProperties.getProperty(Connection.DATABASE);
	    this.connProperties = connProperties;
	    
	    try {
	        this.casInterface = new CassandraInterface(this.host, this.port, 
	          this.keyspace, this.username, this.password);
	        this.c_isOpen = true;
	      } catch (InvalidRequestException e) {
	        this.c_isOpen = false;
	        throw new OdaException(e);
	      } catch (TException e) {
	        this.c_isOpen = false;
	        throw new OdaException(e);
	      }
	      catch (AuthenticationException e) {
	        e.printStackTrace();
	      }
	      catch (AuthorizationException e) {
	        e.printStackTrace();
	      }

	      
	}

	@Override
	public void rollback() throws OdaException {
		
	}

	@Override
	public void setAppContext(Object context) throws OdaException {
		
	}

	@Override
	public void setLocale(ULocale locale) throws OdaException {
		
	}

}
