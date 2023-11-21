/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.fwr.oda.runtime.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fwr.oda.runtime.ConnectionPool;
import bpm.metadata.birt.oda.runtime.impl.Connection;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.vanilla.platform.core.beans.Group;

/**
 * Implementation class of IQuery for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class Query extends bpm.metadata.birt.oda.runtime.impl.Query {
 
	protected Document document;
    private String username, password, repositoryUrl, groupName, businessModel, businessPackage, connectionName, dataSourceName;
    private Integer directoryItemId;
	private String encrypted = "false";
	
    /*
     * jdbc objects
     */
   private VanillaJdbcConnection jdbcConnection;
	
	public Query(Connection fmdtConnection, VanillaPreparedStatement jdbcStatement,
			IBusinessPackage pack, String connectionName, Group group) {
		super(fmdtConnection, jdbcStatement, pack, connectionName, group);
		
	}
	
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IQuery#prepare(java.lang.String)
	 */
	public void prepare( String queryText ) throws OdaException
	{		
		try {
			document = DocumentHelper.parseText(queryText);
		} catch (DocumentException e1) {
			
			e1.printStackTrace();
		}
		
		for(Element e : (List<Element>)document.getRootElement().elements("datasource")){
			if (e.element("model") != null){
				
				Element d = e.element("model");
				if(d != null){
					businessModel = d.getStringValue();
				}
			}
			if (e.element("package") != null){
				
				Element d = e.element("package");
				if(d != null){
					businessPackage = d.getStringValue();
				}
			}
			if (e.element("connection") != null){
				
				Element d = e.element("connection");
				if(d != null){
					connectionName = d.getStringValue();
				}
			}
			if (e.element("group") != null){
				
				Element d = e.element("group");
				if(d != null){
					groupName = d.getStringValue();
				}
			}
			if (e.element("itemid") != null){
				
				Element d = e.element("itemid");
				if(d != null){
					directoryItemId = Integer.parseInt(d.getStringValue());
				}
			}
			if (e.element("name") != null){
				
				Element d = e.element("name");
				if(d != null){
					dataSourceName = d.getStringValue();
				}
			}
			if (e.element("password") != null){
				
				Element d = e.element("password");
				if(d != null){
					password = d.getStringValue();
				}
			}
			if (e.element("isencrypted") != null){
				
				Element d = e.element("isencrypted");
				if(d != null){
					encrypted = d.getStringValue();
				}
			}
			if (e.element("url") != null){
				
				Element d = e.element("url");
				if(d != null){
					repositoryUrl = d.getStringValue();
				}
			}
			if (e.element("user") != null){
				
				Element d = e.element("user");
				if(d != null){
					username = d.getStringValue();
				}
			}
		}
		
		Element qE = document.getRootElement().element("freeMetaDataQuery");
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		XMLWriter writer;
		
		String fmdtquerytext = null;
		try {
			writer = new XMLWriter(bos, OutputFormat.createPrettyPrint());

			writer.write(qE);
			writer.close();
		
			fmdtquerytext = bos.toString("UTF-8");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		/*
		 * init the super Connection
		 */
		Properties fmdtProp = new Properties();
		fmdtProp.put(Connection.BUSINESS_MODEL, businessModel);
		fmdtProp.put(Connection.BUSINESS_PACKAGE, businessPackage);
		fmdtProp.put(Connection.CONNECTION_NAME, "Default");
		fmdtProp.put(Connection.DIRECTORY_ITEM_ID, directoryItemId+"");
		fmdtProp.put(Connection.GROUP_NAME, groupName);
		fmdtProp.put(Connection.IS_ENCRYPTED, encrypted);
		fmdtProp.put(Connection.PASSWORD, password);
		fmdtProp.put(Connection.URL, repositoryUrl);
		fmdtProp.put(Connection.USER, username);
		try {
			
			for(IBusinessModel m : ConnectionPool.getConnection(fmdtProp)){
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
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException("Unable to rebuild FMDT model from xml\n" , e.getMessage());
		}
		
		try {
			jdbcConnection = ((SQLConnection)fmdtPackage.getConnection(groupName, connectionName)).getJdbcConnection();  
		} catch (Exception e) {
			e.printStackTrace();
			throw new OdaException(e);
		}
		
		((bpm.fwr.oda.runtime.impl.Connection)getConnection()).initFmdtConnection(connectionName, fmdtPackage, groupName, jdbcConnection, repositoryUrl, username, password);
		try {
			VanillaPreparedStatement jdbcStatement = jdbcConnection.createStatement();
			super.setJdbcStatement(jdbcStatement);
			super.prepare(fmdtquerytext);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	

	

	
    
}
