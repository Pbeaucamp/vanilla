/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.fwr.oda.runtime.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.vanilla.platform.core.beans.Group;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection extends bpm.metadata.birt.oda.runtime.impl.Connection {
	
	// constants for Properties name
	public static final String URL = "URL";
	public static final String USER = "USER";
	public static final String PASSWORD = "PASSWORD";
	public static final String REPOSITORY_URL = "REPOSITORY_URL";
	public static final String GROUP_ID = "GROUP_ID";
	public static final String GROUP_NAME = "GROUP_NAME";
	public static final String FWREPORT_ID = "FWREPORT_ID";
	public static final String DATASET_NAME = "DATASET_NAME";
    
    private String repositoryUrl;
    private Integer fwReportId, groupId;
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{
		m_isOpen = false;
		//we get the properties
		if (connProperties == null){
			throw new OdaException("No Connection Properties Provided."); 
		}
		
		boolean hasChanged = false;

		
		if (username == null || !username.equals(connProperties.getProperty("USER"))){
			username = connProperties.getProperty("USER");
			hasChanged = true;
		}
		
		if (password == null || !password.equals(connProperties.getProperty("PASSWORD"))){
			password = connProperties.getProperty("PASSWORD");
			hasChanged = true;
		}		
		if (repositoryUrl == null || !repositoryUrl.equals(connProperties.getProperty("REPOSITORY_URL"))){
			repositoryUrl = connProperties.getProperty("REPOSITORY_URL");
			hasChanged = true;
		}
		
		if (groupId == null || groupId!=Integer.parseInt(connProperties.getProperty("GROUP_ID"))){
			groupId = Integer.parseInt(connProperties.getProperty("GROUP_ID"));
			hasChanged = true;
		}
		
		if (groupName == null || !groupName.equals(connProperties.getProperty("GROUP_NAME"))){
			groupName = connProperties.getProperty("GROUP_NAME");
			hasChanged = true;
		}
		
		if (fwReportId == null || fwReportId!=Integer.parseInt(connProperties.getProperty("FWREPORT_ID"))){
			fwReportId = Integer.parseInt(connProperties.getProperty("FWREPORT_ID"));
			hasChanged = true;
		}
		
		if (!hasChanged){
			  m_isOpen = true;
			  return;
		}
		
		
		m_isOpen = true;
		
		
 	}

	@Override
	public void close() throws OdaException {
		
		m_isOpen = false;
		super.close();
	}

	@Override
	public IQuery newQuery(String dataSetType) throws OdaException {
        // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		if (!isOpen()){
			throw new OdaException("Cannot create query on closed connection");
		}
		
		VanillaPreparedStatement jdbcStatement = null;
		Group  dummy = new Group();
//		dummy.setId(getGroupId());
		dummy.setName(getGroupName());

		return new Query(this, jdbcStatement, fmdtPackage, connectionName, dummy);
	}
	 @Override
	public boolean isOpen() throws OdaException {
		
		return m_isOpen;
	}
	 
	 public void initFmdtConnection(String connectionName, IBusinessPackage fmdtPack, String groupName, VanillaJdbcConnection jdbcCon, String url, String username, String password) {
			
			setConnectionName(connectionName);
			setFmdtPackage(fmdtPack);
			setGroupName(groupName);
			setJdbcConnection(jdbcCon);
			setUrl(url);
			setUsername(username);
			setPassword(password);
	}
	 
//	public int getGroupId(){
//		super.get
//		return groupId;
//	}
}
