/*
 *************************************************************************
 * Copyright (c) 2013 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.weka.oda.runtime;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.ibm.icu.util.ULocale;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection {
	private boolean m_isOpen = false;
	private Properties properties;
	private WekaFile file;

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties
	 * )
	 */
	public void open(Properties connProperties) throws OdaException {
		properties = connProperties;
		
		String url = null;
		String user = null;
		String password = null;
		int groupId = 0;
		int repositoryId = 0;
		int itemId = 0;
		try {
			url = connProperties.getProperty(Driver.PROP_URL);
			user = connProperties.getProperty(Driver.PROP_USER);
			password = connProperties.getProperty(Driver.PROP_PASSWORD);
			groupId = Integer.parseInt(connProperties.getProperty(Driver.PROP_GROUPID));
			repositoryId = Integer.parseInt(connProperties.getProperty(Driver.PROP_REPID));
			itemId = Integer.parseInt(connProperties.getProperty(Driver.PROP_ITEMID));
		} catch (NumberFormatException e) {
		}
		
		String localfile = connProperties.getProperty(Driver.PROP_LOCAL_FILE);
		
		if(localfile != null && !localfile.isEmpty()) {
			file = new WekaFile(localfile);
		}
		
		else {
			file = new WekaFile(url, user, password, groupId, repositoryId, itemId);
		}
		
		
		
		m_isOpen = true;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java
	 * .lang.Object)
	 */
	public void setAppContext(Object context) throws OdaException {
		// do nothing; assumes no support for pass-through context
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#close()
	 */
	public void close() throws OdaException {
		m_isOpen = false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#isOpen()
	 */
	public boolean isOpen() throws OdaException {
		return m_isOpen;
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang
	 * .String)
	 */
	public IDataSetMetaData getMetaData(String dataSetType) throws OdaException {
		// assumes that this driver supports only one type of data set,
		// ignores the specified dataSetType
		return new DataSetMetaData(this);
	}

	/*
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang
	 * .String)
	 */
	public IQuery newQuery(String dataSetType) throws OdaException {
		// assumes that this driver supports only one type of data set,
		// ignores the specified dataSetType
		return new Query(this);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMaxQueries()
	 */
	public int getMaxQueries() throws OdaException {
		return 0; // no limit
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#commit()
	 */
	public void commit() throws OdaException {
		// do nothing; assumes no transaction support needed
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#rollback()
	 */
	public void rollback() throws OdaException {
		// do nothing; assumes no transaction support needed
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.IConnection#setLocale(com.ibm.
	 * icu.util.ULocale)
	 */
	public void setLocale(ULocale locale) throws OdaException {
		// do nothing; assumes no locale support
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public WekaFile getWekaFile() {
		return file;
	}

}
