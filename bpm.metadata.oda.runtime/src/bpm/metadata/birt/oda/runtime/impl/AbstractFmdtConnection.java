package bpm.metadata.birt.oda.runtime.impl;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.metadata.birt.oda.runtime.ConnectionPool;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

import com.ibm.icu.util.ULocale;

public abstract class AbstractFmdtConnection implements IConnection{
	// constants for Properties name
	public static final String URL = "URL";
	public static final String USER = "USER";
	public static final String PASSWORD = "PASSWORD";
	public static final String VANILLA_URL = "VANILLA_URL";
	public static final String REPOSITORY_ID = "REPOSITORY_ID";
	public static final String DIRECTORY_ITEM_ID = "DIRECTORY_ITEM_ID";
	public static final String BUSINESS_MODEL = "BUSINESS_MODEL";
	public static final String BUSINESS_PACKAGE = "BUSINESS_PACKAGE";
	public static final String GROUP_NAME = "GROUP_NAME";
	public static final String CONNECTION_NAME = "CONNECTION_NAME";
	public static final String IS_ENCRYPTED = "IS_ENCRYPTED";
	
	
	protected String url, username, password, businessPackage, businessModel, groupName;
	protected String vanillaUrl;
	protected String encrypted = "false";
	protected String connectionName;
	protected Integer directoryItemId, repositoryId;
	
	protected IBusinessPackage fmdtPackage;
	
	protected boolean m_isOpen = false;
    protected IVanillaContext vanillaCtx;
    protected Group vanillaGroup;
    protected Repository vanillaRepository;

    
    
    
    private static int count = 0;
    
    
    
    protected String getConnectionName() {

		return connectionName;
	}

	protected void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}


	protected void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	protected void setFmdtPackage(IBusinessPackage fmdtPackage) {
		this.fmdtPackage = fmdtPackage;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	protected void setPassword(String password) {
		this.password = password;
	}
	
	public AbstractFmdtConnection(){
    	
    }
    
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{


		//we get the properties
		if (connProperties == null){
			throw new OdaException("No Connection Properties Provided."); 
		}
		
		boolean hasChanged = false;
		
		
		if (url == null || !url.equals(connProperties.getProperty("URL"))){
			url = connProperties.getProperty("URL");
			hasChanged = true;
		}
		
		if (username == null || !username.equals(connProperties.getProperty("USER"))){
			username = connProperties.getProperty("USER");
			hasChanged = true;
		}
		
		if (password == null || !password.equals(connProperties.getProperty("PASSWORD"))){
			password = connProperties.getProperty("PASSWORD");
			hasChanged = true;
		}
		
		if (directoryItemId == null || directoryItemId != Integer.parseInt(connProperties.getProperty("DIRECTORY_ITEM_ID"))){
			directoryItemId = Integer.parseInt(connProperties.getProperty("DIRECTORY_ITEM_ID"));
			hasChanged = true;
		}
		
		
		if (businessModel == null || !businessModel.equals(connProperties.getProperty("BUSINESS_MODEL"))){
			businessModel = connProperties.getProperty(BUSINESS_MODEL);
			hasChanged = true;
		}
		
		if (businessPackage == null || !businessPackage.equals(connProperties.getProperty("BUSINESS_PACKAGE"))){
			businessPackage =  connProperties.getProperty(BUSINESS_PACKAGE);
			hasChanged = true;
		}
		
		if (groupName == null || !groupName.equals(connProperties.getProperty("GROUP_NAME"))){
			groupName =  connProperties.getProperty(GROUP_NAME);
			hasChanged = true;
		}
		
		if (connectionName == null || !connectionName.equals(connProperties.getProperty("CONNECTION_NAME"))){
			connectionName = connProperties.getProperty(CONNECTION_NAME);
			hasChanged = true;
		}
		if (encrypted == null || !encrypted.equals(connProperties.getProperty("ENCRYPTION_TYPE"))){
			encrypted = connProperties.getProperty(IS_ENCRYPTED);
			hasChanged = true;
		}
		
		if (vanillaUrl == null || !vanillaUrl.equals(connProperties.getProperty("VANILLA_URL"))){
			vanillaUrl = connProperties.getProperty(VANILLA_URL);
			hasChanged = true;
		}
		if (repositoryId == null || !repositoryId.equals(Integer.parseInt(connProperties.getProperty("REPOSITORY_ID")))){
			
			if (connProperties.getProperty(REPOSITORY_ID) != null && !"".equals(connProperties.getProperty(REPOSITORY_ID))){
				repositoryId = Integer.parseInt(connProperties.getProperty(REPOSITORY_ID));
				hasChanged = true;
			}
			
		}
		
		/**
		 * XXX ere override for url client/server
		 */
		try{
			ConfigurationManager vanillaConfigManager =ConfigurationManager.getInstance();
			if (url != null){
				connProperties.setProperty("URL", vanillaConfigManager.getVanillaConfiguration().translateClientUrlToServer(url));
			}
			
			if (vanillaUrl != null){
				connProperties.setProperty("VANILLA_URL", vanillaConfigManager.getVanillaConfiguration().translateClientUrlToServer(vanillaUrl));			
			}
			else {
				vanillaUrl = vanillaConfigManager.getVanillaConfiguration().getVanillaServerUrl();
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			Logger.getLogger(getClass()).warn("Issues with the ConfigurationManager when trying to override URL. Check the vaillaConfig property and the file");
//			connProperties.setProperty("URL", url);
//			connProperties.setProperty("VANILLA_URL", vanillaUrl);
		}
		
		
		//XXX I think this is not working.
		//Also it's completly useless so I remove it just in case.
//		if (!hasChanged){
//			
//			try{
//				//calling connection will make sure that the
//				//Fmdt has not been updated and that it is still on
//				//if a failure happens, this means the fmdt is down
//				
//				ConnectionPool.getConnection(connProperties);
//				m_isOpen = true;
//				return;
//			}catch(OdaException ex){
//				throw ex;
//			}
//			
//			 
//		}
		
		
		try {
			
			for(IBusinessModel m : ConnectionPool.getConnection(connProperties)){
				if (m.getName().equals(businessModel)){
					
					for(IBusinessPackage p : m.getBusinessPackages(groupName)){
						if (p.getName().equals(businessPackage)){
							fmdtPackage = p;
							break;
						}
					}
					
					break;
				}
			}
			
			IRepositoryApi sock = getRepositoryConnection();
			
			if(vanillaUrl != null && !vanillaUrl.isEmpty()){
				vanillaCtx = new BaseVanillaContext(vanillaUrl, username, password);
			}
			else {
				vanillaCtx = new BaseVanillaContext(sock.getContext().getVanillaContext().getVanillaUrl(), username, password);
			}
			
			
			try{
				IVanillaAPI api = new RemoteVanillaPlatform(vanillaCtx);
				try {
					vanillaGroup = api.getVanillaSecurityManager().getGroupByName(groupName);
				} catch(Exception e) {
					vanillaCtx = new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), vanillaCtx.getLogin(), vanillaCtx.getPassword());
					api = new RemoteVanillaPlatform(vanillaCtx);
					vanillaGroup = api.getVanillaSecurityManager().getGroupByName(groupName);
				}
				
				if(repositoryId != null){
					vanillaRepository = api.getVanillaRepositoryManager().getRepositoryById(repositoryId);
				}
				else {
					vanillaRepository = api.getVanillaRepositoryManager().getRepositoryFromUrl(sock.getContext().getRepository().getUrl());
				}
				
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Could not load Vanilla Group " + groupName + " : " + ex.getMessage(), ex);
				throw new RuntimeException("Could not load Vanilla Group " + groupName, ex);
			}
			
			
		}catch(OdaException ex){
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			throw ex;
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException("Unable to rebuild FMDT model from xml\n" + e.getMessage());
		}
    
 	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
	    // do nothing; assumes no support for pass-through context
	}


	
	


	public abstract boolean isOpen() throws OdaException;
	
	public abstract IQuery newQuery( String dataSetType ) throws OdaException;
	public abstract void close() throws OdaException;
	
	
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang.String)
	 */
	public IDataSetMetaData getMetaData( String dataSetType ) throws OdaException
	{
	    // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new DataSetMetaData( this );
	}
	
	protected IVanillaContext getVanillaContext(){
		return vanillaCtx;
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
	
	
	public IBusinessPackage getFmdtPackage(){
		return fmdtPackage;
	}
    
	public String getGroupName(){
		return groupName;
	}

	public void setLocale(ULocale arg0) throws OdaException {
		
		
	}
	
	public IRepositoryApi getRepositoryConnection() throws Exception{
		Properties p = new Properties();
		/**
		 * XXX ere override for url client/server
		 */
		ConfigurationManager vanillaConfigManager =ConfigurationManager.getInstance();
		
		if (url != null){
			try{
				p.setProperty(URL, vanillaConfigManager.getVanillaConfiguration().translateClientUrlToServer(url));
			}catch(Exception ex){
				p.setProperty(URL, url);
				Logger.getLogger(getClass()).warn("Issues with the ConfigurationManager when trying to override URL. Check the vaillaConfig property and the file");
			}

		}
		
		if (vanillaUrl != null){
			try{
				p.setProperty(VANILLA_URL, vanillaConfigManager.getVanillaConfiguration().translateClientUrlToServer(vanillaUrl));
			}catch(Exception ex){
				p.setProperty(VANILLA_URL, vanillaUrl);
				Logger.getLogger(getClass()).warn("Issues with the ConfigurationManager when trying to override VANILLA_URL. Check the vaillaConfig property and the file");
			}
		}
		if (repositoryId != null){
			p.setProperty(REPOSITORY_ID, repositoryId + "");
		}
		if (groupName != null){
			p.setProperty(GROUP_NAME, groupName);
		}
		p.setProperty(USER, username);
		p.setProperty(PASSWORD, password);
		return ConnectionPool.getRepositoryConnection(p);
	}

	public Repository getVanillaRepository() throws Exception{
		if (vanillaRepository == null ){
			if (vanillaCtx == null){
				IRepositoryApi sock = (IRepositoryApi)getRepositoryConnection();
				try{
					vanillaCtx =  sock.getContext().getVanillaContext();
				}catch(Exception ex){
					
				}
				
			}
			if (repositoryId != null){
				try{
					vanillaRepository = new RemoteVanillaPlatform(vanillaCtx).getVanillaRepositoryManager().getRepositoryById(repositoryId);
				}catch(Exception ex){
					Logger.getLogger(getClass()).error("Could not get Repository from vanilla - " + ex.getMessage(), ex);
				}
			}
			else if (url != null){
				try{
					vanillaRepository = new RemoteVanillaPlatform(vanillaCtx).getVanillaRepositoryManager().getRepositoryFromUrl(url);
				}catch(Exception ex){
					Logger.getLogger(getClass()).error("Could not get Repository from vanilla - " + ex.getMessage(), ex);
				}
			}
			
		}
		return vanillaRepository;
	}
	
	public int getItemId() {
		return directoryItemId;
	}
}
