package bpm.nosql.oda.runtime.impl;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.ibm.icu.util.ULocale;

public class HbaseConnection implements IConnection{

	private boolean c_isOpen = false;
	private String configFile;
	private Configuration config;
	private HBaseAdmin admin;
	private Properties connProperties;
	
	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	@Override
	public void close() throws OdaException {
		
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
	public IQuery newQuery(String dataSetType) throws OdaException {
		
		return new HbaseQuery(this);
	}


	@Override
	public void open(Properties connProperties) throws OdaException {

		this.configFile = connProperties.getProperty(Connection.CONFIGURATIONFILE);
	    this.connProperties = connProperties;
	    
	    config = new Configuration();
	    config.addResource(new Path(this.configFile));
		
		File workaround = new File(".");
        System.getProperties().put("hadoop.home.dir", workaround.getAbsolutePath());
        new File("./bin").mkdirs();
        try {
			new File("./bin/winutils.exe").createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
		try {
			admin = new HBaseAdmin(config);
			c_isOpen = admin.isMasterRunning();
			setHbaseAdmin(admin);
			
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
			c_isOpen = false;
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
			c_isOpen = false;
		} catch (Exception e) {
			e.printStackTrace();
			c_isOpen = false;
		}		

	}

	public void setHbaseAdmin(HBaseAdmin admin){
		this.admin = admin;	
	}
	
	public HBaseAdmin getHbaseAdmin(){
		return this.admin;
	}
	
	public HTableDescriptor[] getListTables() throws Exception{
		return this.admin.getConnection().listTables();
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

	public void setConfigurationFileUrl(String configFile) {
		this.configFile = configFile;
	}
	
	public String getConfigurationFileUrl() {
		return configFile;
	}
}
