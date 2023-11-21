/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.excel.oda.runtime.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.excel.oda.runtime.Activator;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.ibm.icu.util.ULocale;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection {
	public static final String REPOSITORY_ID = "repository.id";
	public static final String USER = "repository.user";
	public static final String PASSWORD = "repository.password";
	public static final String DIRECTORY_ITEM_ID = "repository.item.id";
	public static final String GROUP_ID = "vanilla.group.id";
	
	public static final String STORAGE_TYPE = "storageType";
	public static final String FILE_NAME = "fileName";
	public static final String ENCODING = "encoding";
	
	
    protected boolean m_isOpen = false;
    private String path;
    
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open(Properties connProperties) throws OdaException {		
        String vanillaUrl = Activator.getDefault().getVanillaUrl();
        IVanillaContext vCtx = new BaseVanillaContext(vanillaUrl, connProperties.getProperty(USER), connProperties.getProperty(PASSWORD));
		RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(vCtx);
  
        try {
        	Repository def = vanillaApi.getVanillaRepositoryManager().getRepositoryById(new Integer(connProperties.getProperty(REPOSITORY_ID)));
			Group group = vanillaApi.getVanillaSecurityManager().getGroupById(new Integer(connProperties.getProperty(GROUP_ID)));
			IRepositoryApi sock = new RemoteRepositoryApi(
					new BaseRepositoryContext(vCtx, group, def)); 
			
			
			RepositoryItem item = sock.getRepositoryService().getDirectoryItem(new Integer(connProperties.getProperty(DIRECTORY_ITEM_ID)));
			
			path = System.getProperty("java.io.tmpdir");
			if (!path.endsWith(File.separator)) {
				path += File.separator;
			}
			path += "temp_van_" + new Object().hashCode() + ".xls";
			FileOutputStream fos = new FileOutputStream(path);
			sock.getDocumentationService().importExternalDocument(item, fos);
			m_isOpen = true; 
		} catch (NumberFormatException e) {
			e.printStackTrace();
			m_isOpen = false; 
		} catch (Exception e) {
			e.printStackTrace();
			m_isOpen = false; 
		}
		
		
		
	           
 	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
	    // do nothing; assumes no support for pass-through context
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
	public boolean isOpen() throws OdaException {
		return m_isOpen;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang.String)
	 */
	public IDataSetMetaData getMetaData( String dataSetType ) throws OdaException {
	    // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new DataSetMetaData( this );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType ) throws OdaException {
		if (!isOpen()){
			throw new OdaException("Cannot create query on closed connection");
		}
		
		
		return new Query(this.path);
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
	public void commit() throws OdaException
	{
	    // do nothing; assumes no transaction support needed
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#rollback()
	 */
	public void rollback() throws OdaException
	{
        // do nothing; assumes no transaction support needed
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IConnection#setLocale(com.ibm.icu.util.ULocale)
     */
    public void setLocale( ULocale locale ) throws OdaException
    {
        // do nothing; assumes no locale support
    }
    
    public String getTemporaryFile() {
    	return path;
    }
    
    protected void setTemporaryFileName(String path){
    	this.path = path;
    }
    
}
