/*
 *************************************************************************
 * Copyright (c) 2009 <<Bpm-Conseil>>
 *  
 *************************************************************************
 */

package bpm.fm.oda.driver.impl;

import java.util.HashMap;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fm.oda.driver.Activator;
import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.manager.client.FmClientAccessor;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.utils.Tools;

import com.ibm.icu.util.ULocale;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection {
	
	public static final String CTX_PROP_JDBC_DRIVER = "driverClassName";
	public static final String CTX_PROP_DB_URL = "url";
	public static final String CTX_PROP_DB_LOGIN = "username";
	public static final String CTX_PROP_DB_PASSWORD = "password";
	public static final String CTX_SPRING_FILE_PATH = "configFile";
	
	public static final String PROP_FM_LOGIN = "fmLogin";
	public static final String PROP_FM_PASSWORD = "freemetricsPassword";
	public static final String PROP_FM_GROUP_ID = "fmGroupId";
	public static final String PROP_FM_THEME_ID = "fmThemeId";
	public static final String PROP_FM_URL = "fmUrl";

    private boolean m_isOpen = false;
    
    private FmUser userFm;
    private IManager fmMgr = Activator.getDefault().getFmManager();
    private FmClientAccessor fmAssessor;
    
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{
		
//		if (fmMgr == null){
//			try{
//				setAppContext(connProperties);
//			}catch(OdaException ex){
//				throw ex;
//			}
//			
//		}
//		try{
//			userFm = fmMgr.getUserByNameAndPass(connProperties.getProperty(PROP_FM_LOGIN), connProperties.getProperty(PROP_FM_PASSWORD));
//		}catch(Exception ex){
//			ex.printStackTrace();
//			throw new OdaException(new Exception("Error when authentifying in FreeMetrics: " + ex.getMessage(), ex));
//
//		}
//		
//		if (userFm == null){
//			throw new OdaException("The authentication failed.");
//		}
	    
		try {
			fmAssessor = FmClientAccessor.getClient(fmMgr, connProperties.getProperty(PROP_FM_LOGIN), connProperties.getProperty(PROP_FM_PASSWORD), connProperties.getProperty(PROP_FM_URL));
			
			try {
				int groupId = Integer.parseInt(connProperties.getProperty(PROP_FM_GROUP_ID));
				int themeId = Integer.parseInt(connProperties.getProperty(PROP_FM_THEME_ID));
				
				fmAssessor.setGroupId(groupId);
				fmAssessor.setThemeId(themeId);
			} catch (Exception e) {
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e);
		}
		m_isOpen = true;        
 	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	/**
	 * Object is a Properties Object
	 */
	public void setAppContext( Object context ) throws OdaException
	{
//		FactoryManager.init("", Tools.OS_TYPE_WINDOWS);
//		
//		
//		HashMap prop = (HashMap)context;
//		Properties _p = new Properties();
//		for(Object o : prop.keySet()){
//			if (o instanceof String ){
//				try{
//					_p.setProperty((String)o, (String)prop.get(o));
//				}catch(Exception ex){
//					ex.printStackTrace();
//				}
//				
//			}
//		}
//		try{
//			String configFilePath = "";
//			
//			
//			
//			
//			if (_p.getProperty(CTX_SPRING_FILE_PATH) != null){
//				configFilePath = _p.getProperty(CTX_SPRING_FILE_PATH);
//			}
//			else{
//				configFilePath = "./resources/freeMetricsContext.xml";
//			}
//			fmMgr = FactoryManager.getInstance(/*_p, configFilePath*/).getManager();
//		}catch(Exception ex){
//			ex.printStackTrace();
//			throw new OdaException(new Exception("Error when initializing Freemetrics context : " + ex.getMessage(), ex));
//		}
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#close()
	 */
	public void close() throws OdaException
	{
       
	    m_isOpen = false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#isOpen()
	 */
	public boolean isOpen() throws OdaException
	{
		return m_isOpen;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang.String)
	 */
	public IDataSetMetaData getMetaData( String dataSetType ) throws OdaException
	{
	    // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new DataSetMetaData( this );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType ) throws OdaException
	{
        // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new Query(fmAssessor);
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMaxQueries()
	 */
	public int getMaxQueries() throws OdaException
	{
		return 0;	// no limit
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#commit()
	 */
	public void commit() throws OdaException {}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#rollback()
	 */
	public void rollback() throws OdaException	{}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IConnection#setLocale(com.ibm.icu.util.ULocale)
     */
    public void setLocale( ULocale locale ) throws OdaException {}
    
    
    
}
