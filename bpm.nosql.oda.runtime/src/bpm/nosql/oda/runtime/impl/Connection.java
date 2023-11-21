package bpm.nosql.oda.runtime.impl;

import java.net.UnknownHostException;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.ibm.icu.util.ULocale;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class Connection implements IConnection{
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public static final String DATABASE = "database";
	public static final String PORT = "port";
	public static final String HOST = "host";
	public static final String ISPASSREQUIRED = "pass";
	public static final String DATABASETYPE = "dbtype";
	public static final String CONFIGURATIONFILE = "configfile";
	public static final String COLLECTION = "collection";
	
	

	private Mongo mongo;
	private DB db;
	private boolean c_isOpen = false;
	private String encoding = "UTF-8";
	private String collection;
	
	@Override
	public void close() throws OdaException {
		 this.db.getMongo().close();
         this.db=null;
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
		return new DataSetMetaData(this);
	}

	@Override
	public boolean isOpen() throws OdaException {
		return c_isOpen;
	}

	@Override
	public Query newQuery(String dataSetType) throws OdaException {
		return new Query(db, collection);
	}

	@Override
	public void open(Properties connProperties) throws OdaException {
		

		try {
			
			if(getMongo() == null){
				setMongo(new Mongo(connProperties.getProperty(Connection.HOST), Integer.valueOf(connProperties.getProperty(Connection.PORT))));	
			
				for(String dbName : getMongo().getDatabaseNames()){
					if(dbName.equalsIgnoreCase(connProperties.getProperty(Connection.DATABASE))){
						db = getMongo().getDB(connProperties.getProperty(Connection.DATABASE));
						break;
					}
				}
			}
			
			if(db  == null){
				c_isOpen = false;
			}else if (connProperties.getProperty(Connection.ISPASSREQUIRED) != null && connProperties.getProperty(Connection.ISPASSREQUIRED).contains("true") && db != null){
				c_isOpen = db.authenticate(connProperties.getProperty(Connection.USER), 
						(connProperties.getProperty(Connection.PASSWORD)).toCharArray());
			}else{ 
				c_isOpen = true;
			}		
			collection = connProperties.getProperty(Connection.COLLECTION);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			c_isOpen = false;
		} catch (MongoException e) {
			e.printStackTrace();
			c_isOpen = false;
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

	public void setMongo(Mongo mongo) {
		this.mongo = mongo;
	}

	public Mongo getMongo() {
		return mongo;
	}


	public String getEncoding() {
		return encoding;
	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

}
