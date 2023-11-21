package org.fasd.datasource;

import java.util.Properties;

public class DatasourceOda extends DataSource {

	private Properties publicProperties = new Properties();
	private Properties privateProperties = new Properties();;
	
	private String odaExtensionId;
	private String odaDatasourceExtensionId;
	
	public DatasourceOda() {
		DataSourceConnection con = new DataSourceConnection();
		con.setType("oda");
		this.addConnection(con);
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
	
	public String getOdaDatasourceExtensionId() {
		return odaDatasourceExtensionId;
	}
	
	public void setOdaDatasourceExtensionId(String odaDatasourceExtensionId) {
		this.odaDatasourceExtensionId = odaDatasourceExtensionId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getFAXML() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("	<datasource-oda>\n");
		
		buf.append("		<name>" + name + "</name>\n");
		buf.append("		<id>" + id + "</id>\n");
		
		buf.append("		<odadatasourceextensionid>" + odaDatasourceExtensionId + "</odadatasourceextensionid>\n");
		buf.append("		<odaextensionid>" + odaExtensionId + "</odaextensionid>\n");
		
		buf.append("		<public-properties>\n");
		for(Object obj : publicProperties.keySet()) {
			buf.append("			<property><name>" + obj + "</name><value>" + publicProperties.get(obj) + "</value></property>\n");
		}
		buf.append("		</public-properties>\n");
		
		buf.append("		<private-properties>\n");
		for(Object obj : privateProperties.keySet()) {
			buf.append("			<property><name>" + obj + "</name><value>" + privateProperties.get(obj) + "</value></property>\n");
		}
		buf.append("		</private-properties>\n");
		
		for(DataObject obj : dataObjects) {
			buf.append("		" + obj.getFAXML());
		}
		
		buf.append("	</datasource-oda>\n");
		
		return buf.toString();
	}
	
	public void addPublicProperty(String name, String value) {
		if(publicProperties == null) {
			publicProperties = new Properties();
		}
		publicProperties.put(name, value);
	}
	
	public void addPrivateProperty(String name, String value) {
		if(privateProperties == null) {
			privateProperties = new Properties();
		}
		privateProperties.put(name, value);
	}

	public Properties getProperties() {
		Properties props = new Properties();
		if(publicProperties != null) {
			props.putAll(publicProperties);
		}
		if(privateProperties != null) {
			props.putAll(privateProperties);
		}
		return props;
	}
}
