/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.vanilla.listdata.oda.driver.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.utils.MD5Helper;

import com.ibm.icu.util.ULocale;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection
{
    private boolean m_isOpen = false;
    public static final String PROP_VANILLA_URL = "vanillaUrl";
	public static final String PROP_VANILLA_LOGIN = "vanillaLogin";
	public static final String PROP_VANILLA_PASSWORD = "vanillaPassword";
	public static final String PROP_REPOSITORY_ID = "vanillaRepositoryId";
    public static final String PROP_VANILLA_DATAPROVIDERR_ID = "vanillaDataProviderId";
    public static final String PROP_GROUP_ID = "groupId";
	
	private User vanillaUser;
	private IVanillaAPI vanillaApi;
	private IRepositoryApi socket;
	
	private Integer groupId = -1;
	
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{
		IVanillaContext vCtx = new BaseVanillaContext(connProperties.getProperty(PROP_VANILLA_URL), connProperties.getProperty(PROP_VANILLA_LOGIN), connProperties.getProperty(PROP_VANILLA_PASSWORD));
		vanillaApi = new RemoteVanillaPlatform(vCtx);
        try{
        	vanillaUser = vanillaApi.getVanillaSecurityManager().getUserByLogin(connProperties.getProperty(PROP_VANILLA_LOGIN));
        }catch(Exception ex){
        	throw new OdaException("Unable to fnd User, " + ex.getMessage());
        }
        
        if (vanillaUser == null){
        	throw new OdaException("The User " + connProperties.getProperty(PROP_VANILLA_LOGIN) + " does not exist.");
        }
        
        String pass = connProperties.getProperty(PROP_VANILLA_PASSWORD);
        
        if (!pass.matches("[0-9a-f]{32}")) {
        	pass = MD5Helper.encode(pass);
        }
        
        if (!vanillaUser.getPassword().equals(pass)){
        	throw new OdaException("Bad password.");
        }
        

        try{
        	groupId = Integer.parseInt(connProperties.getProperty(PROP_GROUP_ID));
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        
        Repository repDef = null;
        try{
        	for(Repository def : vanillaApi.getVanillaRepositoryManager().getUserRepositories(vanillaUser.getLogin())){
            	if (def.getId() == Integer.parseInt(connProperties.getProperty(PROP_REPOSITORY_ID))){
            		repDef = def;
            		break;
            	}
            }
        }catch(Exception ex){
        	throw new OdaException("Unable to Browse User's repositories, " + ex.getMessage());
        }
        
        
        if (repDef == null){
        	try{
        		
        		Repository def = vanillaApi.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(connProperties.getProperty(PROP_REPOSITORY_ID)));
            	if ( def == null){
            		throw new OdaException("Vanilla has no Repository regsitered with id=" + connProperties.getProperty(PROP_REPOSITORY_ID));
            	}
            	else{
            		throw new OdaException("The repository " + def.getName() + " cannot be accessed by the user " + vanillaUser.getLogin());
            	}
        	}catch(Exception ex){
        		throw new OdaException("Unable to find Repository, " + ex.getMessage());
        	}
        	
        }
        
       try{
    	   socket = new RemoteRepositoryApi(
           		new BaseRepositoryContext(
           				vCtx, 
           				vanillaApi.getVanillaSecurityManager().getGroupById(groupId), 
           				repDef)); 
       }catch(Exception ex){
    	   throw new OdaException(ex);
       }

	    m_isOpen = true;        
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
		return new Query(socket);
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
    
}
