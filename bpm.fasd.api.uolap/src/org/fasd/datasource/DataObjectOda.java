package org.fasd.datasource;

import java.util.Properties;

public class DataObjectOda extends DataObject {

	private Properties publicProperties = new Properties();
	private Properties privateProperties = new Properties();
	
	private String queryText;
	private String odaDatasetExtensionId;
	
	
	public DataObjectOda() {}
	
	public DataObjectOda(DatasourceOda con) {
		dataSource = con;
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
	public String getQueryText() {
		return queryText;
	}
	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}
	public String getOdaDatasetExtensionId() {
		return odaDatasetExtensionId;
	}
	public void setOdaDatasetExtensionId(String odaDatasetExtensionId) {
		this.odaDatasetExtensionId = odaDatasetExtensionId;
	}

	@Override
	public String getFAXML() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<dataobject-oda>\n"); //$NON-NLS-1$
		buf.append("				<id>" + getId() + "</id>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("				<name>" + name + "</name>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("				<data-source-id>" + dataSource.getId() + "</data-source-id>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("				<datasetextensionid>" + odaDatasetExtensionId + "</datasetextensionid>\n");
	
		buf.append("				<public-properties>\n");
		for(Object obj : publicProperties.keySet()) {
			buf.append("					<property><name>" + obj + "</name><value>" + publicProperties.get(obj) + "</value></property>\n");
		}
		buf.append("				</public-properties>\n");
		
		buf.append("				<private-properties>\n");
		for(Object obj : privateProperties.keySet()) {
			buf.append("					<property><name>" + obj + "</name><value>" + privateProperties.get(obj) + "</value></property>\n");
		}
		buf.append("				</private-properties>\n");
		
		buf.append("				<querytext><![CDATA[" + queryText + "]]></querytext>\n");
		
		buf.append("				<dataobjecttype>" + dataObjectType + "</dataobjecttype>\n");
		
		for (int i=0; i < items.size(); i++) {
			buf.append("" + items.get(i).getFAXML());
		}
		
		buf.append("</dataobject-oda>\n"); //$NON-NLS-1$
		return buf.toString();
	}
	
	
	
}
