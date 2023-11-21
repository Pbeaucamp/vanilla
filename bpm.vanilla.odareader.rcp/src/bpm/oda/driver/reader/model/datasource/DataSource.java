package bpm.oda.driver.reader.model.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.oda.driver.reader.model.ILabelable;


public class DataSource  implements ILabelable	 {
	
	private Properties publicProperties, privateProperties;
	
	private String odaExtensionDataSourceId;
	private String odaExtensionId;
	
	private String odaDriverClassName;
	private String name;
	
	private List<IConnection> openedConnections = new ArrayList<IConnection>();
	
	public DataSource(Properties publicProperties,
			Properties privateProperties, String odaExtensionDataSourceId,
			String odaExtensionId, String odaDriverClassName, String name) {
		super();
		this.publicProperties = publicProperties;
		this.privateProperties = privateProperties;
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
		this.odaExtensionId = odaExtensionId;
		this.odaDriverClassName = odaDriverClassName;
		this.name = name;
	}


	public DataSource(String odaExtensionId, String name, String odaDriverClassName ) {
		super();
		this.odaExtensionDataSourceId = odaExtensionId;
		this.name = name;
		this.odaDriverClassName = odaDriverClassName;
	}
	
	public DataSource(String name, String odaExtensionDataSourceId, String odaExtensionId, Properties publicProperties, Properties privateProperties){
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
		this.odaExtensionId = odaExtensionId;
		this.name = name;
		this.privateProperties = privateProperties;
		this.publicProperties = publicProperties;
		
	}
	
	public Properties getProperties() {
		Properties p = new Properties();
		for(Object o : publicProperties.keySet()){
			p.setProperty((String)o, publicProperties.getProperty((String)o));
		}
		for(Object o : privateProperties.keySet()){
			p.setProperty((String)o, privateProperties.getProperty((String)o));
		}
		return p;
	}


	public Properties getPublicProperties() {
		return publicProperties;
	}


	public void setPublicProperties(Properties publicProperties) {
		this.publicProperties = publicProperties;
	}


	public Properties getPrivateProperties() {
		return privateProperties;
	}


	public void setPrivateProperties(Properties privateProperties) {
		this.privateProperties = privateProperties;
	}


	public String getOdaExtensionId() {
		return odaExtensionId;
	}


	public void setOdaExtensionId(String odaExtensionId) {
		this.odaExtensionId = odaExtensionId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getOdaExtensionDataSourceId() {
		return odaExtensionDataSourceId;
	}


	public void setOdaExtensionDataSourceId(String odaExtensionDataSourceId) {
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
	}


	public String getOdaDriverClassName() {
		return odaDriverClassName;
	}


	public void setOdaDriverClassName(String odaDriverClassName) {
		this.odaDriverClassName = odaDriverClassName;
	}
	
	public void addOpenedConnection(IConnection c) {
		openedConnections.add(c);
		
	}
	
	public List<IConnection> getListConn(){
		return openedConnections;
	}

	public void closeConnections(){
		for(IConnection c : openedConnections){
			try {
				c.close();
			} catch (OdaException e) {
				
				e.printStackTrace();
			}
		}
		openedConnections.clear();
	}
	
	

}
