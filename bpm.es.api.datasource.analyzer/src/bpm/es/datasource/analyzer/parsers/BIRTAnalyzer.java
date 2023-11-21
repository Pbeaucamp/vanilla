package bpm.es.datasource.analyzer.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.OdaInput;


public class BIRTAnalyzer implements ICustomeAnalyzer{

	private static final String NODE_DATASOURCES = "data-sources";
	private static final String NODE_DATASOURCE = "oda-data-source";
	private static final String DS_ATTRIBUTE_EXTENSION = "extensionID";
	private static final String DS_ATTRIBUTE_EXTENSION_JDBC_ID = "org.eclipse.birt.report.data.oda.jdbc";
	
	
	private static final String NODE_PROPERTY = "property";
	private static final String ATTRIBUTE_PROPERTY_NAME = "name";
	private static final String ATTRIBUTE_URL_NAME = "odaURL";
	
	private static final String NODE_DATASETS = "data-sets";
	private static final String NODE_DATASET = "oda-data-set";
	private static final String ATTRIBUTE_DATASOURCE_NAME = "dataSource";
	
	private static final String NODE_XML_PROPERTY = "xml-property";
	private static final String ATTRIBUTE_QUERY = "queryText";
	private static final String NODE_PROPERTY_ENCRYPTED = "encrypted-property";
	private static final String ATTRIBUTE_ENCRYPTION = "encryptionID";
	private static final String NODE_LIST_PROPERTY = "list-property";
	private static final String DATESET_ATTRIBUTE_EXTENSION = "extensionID";
	
	
	public int getObjectSubType() {
		return IRepositoryApi.BIRT_REPORT_SUBTYPE;
	}

	public String getObjectSubTypeName() {
		return "Birt";
	}

	public int getObjectType() {
		return IRepositoryApi.CUST_TYPE;
	}

	public String getObjectTypeName() {
		return "Custom";
	}

	public boolean match(String xml, IPattern pattern) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		for(String serverName : findDatabaseServerName(pattern, root)){
			if (!findDataSetsUsingServer(pattern, serverName, root).isEmpty()){
				return true;
			}
		}
		
		return false;
	}
	
	
	private List<String> findDatabaseServerName(IPattern pattern, Element serversRoot){
		List<String> names = new ArrayList<String>();
		Element dataSourcesNode = serversRoot.element(NODE_DATASOURCES);
		
		for(Element dataSource : (List<Element>)dataSourcesNode.elements(NODE_DATASOURCE)){
			if (!DS_ATTRIBUTE_EXTENSION_JDBC_ID.equals(dataSource.attributeValue(DS_ATTRIBUTE_EXTENSION))){
				continue;
			}
			
			String url = null;
			for(Element p : (List<Element>)dataSource.elements(NODE_PROPERTY)){
				if (ATTRIBUTE_URL_NAME.equals(p.attributeValue(ATTRIBUTE_PROPERTY_NAME))){
					url = p.getStringValue();
					break;
				}
			}
			
			if (url == null){
				continue;
			}
			
			
			
			if (url.startsWith(pattern.getUrl().trim()) || pattern.getUrl().trim().startsWith(url.trim())){
				names.add(dataSource.attributeValue(ATTRIBUTE_PROPERTY_NAME));
			}
		}
		
		return names;
	}

	
	private List<String> findDataSetsUsingServer(IPattern pattern, String serverName, Element stepsRoot){
		List<String> stepsName = new ArrayList<String>();
		
		Element dataSetsNode = stepsRoot.element(NODE_DATASETS);
		for(Element dataSet : (List<Element>)dataSetsNode.elements(NODE_DATASET)){
			
			for(Element p : (List<Element>)dataSet.elements(NODE_PROPERTY)){
				if (!ATTRIBUTE_DATASOURCE_NAME.equals(p.attributeValue(ATTRIBUTE_PROPERTY_NAME))){
					continue;
				}
				
				if (!serverName.equals(p.getStringValue())){
					continue;
				}
				
				if (pattern instanceof PatternTable){
					String querySql = null; 
					for(Element xmlp : (List<Element>)dataSet.elements(NODE_XML_PROPERTY)){
						if (ATTRIBUTE_QUERY.equals(xmlp.attributeValue(ATTRIBUTE_PROPERTY_NAME))){
							querySql = xmlp.getStringValue();
							break;
						}
					}
					
					if (querySql != null){
						for(String table : ((PatternTable)pattern).getTableName()){
							
							if (querySql.contains(table)){
								stepsName.add(dataSet.attributeValue(ATTRIBUTE_PROPERTY_NAME));
								break;
							}
						}
					}
				}
				else{
					stepsName.add(dataSet.attributeValue(ATTRIBUTE_PROPERTY_NAME));
				}
			}
		}
		
		return stepsName;
	}
	
	@Override
	public List<OdaInput> extractDataSets(String xml) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		List<OdaInput> dataSets = new ArrayList<OdaInput>();
		
		
		for(Element dataSet : (List<Element>)root.element(NODE_DATASETS).elements(NODE_DATASET)){
			String dataSetExtensionId = dataSet.attributeValue(DS_ATTRIBUTE_EXTENSION);
			String dataSourceExtensionId = null;
			String dataSetName = dataSet.attributeValue(ATTRIBUTE_PROPERTY_NAME);
			String queryText = null;
			String dataSourceName = null;
			for(Element e : (List<Element>)dataSet.elements()){
				if (e.attribute(ATTRIBUTE_PROPERTY_NAME) == null){
					continue;
				}
				if (e.attributeValue(ATTRIBUTE_PROPERTY_NAME).equals(ATTRIBUTE_QUERY)){
					queryText = e.getText();
				}
				if (e.attributeValue(ATTRIBUTE_PROPERTY_NAME).equals(ATTRIBUTE_DATASOURCE_NAME)){
					dataSourceName = e.getText();
				}
			}
			
			/*
			 * look for DataSource
			 */
			Properties dataSourcePublicProps = new Properties();
			Properties dataSourcePrivateProps = new Properties();
			for(Element dataSource : (List<Element>)root.element(NODE_DATASOURCES).elements(NODE_DATASOURCE)){
				if (!dataSource.attributeValue(ATTRIBUTE_PROPERTY_NAME).equals(dataSourceName)){
					continue;
				}
				dataSourceExtensionId = dataSource.attributeValue(DATESET_ATTRIBUTE_EXTENSION);
				
				for(Element eP : (List<Element>)dataSource.elements(NODE_PROPERTY)){
					dataSourcePublicProps.setProperty(eP.attributeValue(ATTRIBUTE_PROPERTY_NAME), eP.getText());
				}
				for(Element eP : (List<Element>)dataSource.elements(NODE_PROPERTY_ENCRYPTED)){
					String encryption = eP.attributeValue(ATTRIBUTE_ENCRYPTION);
					if (encryption.equalsIgnoreCase("base64")){
						
						dataSourcePublicProps.setProperty(eP.attributeValue(ATTRIBUTE_PROPERTY_NAME), new String(Base64.decodeBase64(eP.getText())));
					}
					else{
						Logger.getLogger(getClass()).warn("Encryption " + encryption + " on a dataSourceProperty");
						dataSourcePublicProps.setProperty(eP.attributeValue(ATTRIBUTE_PROPERTY_NAME), eP.getText());
					}
					
				}
				
				
				
				for(Element eP : (List<Element>)dataSource.elements(NODE_LIST_PROPERTY)){
					if (eP.attributeValue(ATTRIBUTE_PROPERTY_NAME).equals("privateDriverProperties")){
						
						
						for(Element e : (List<Element>)eP.elements()){
							try{
								if (e.getName().equals("ex-property")){
									String pName = e.element(ATTRIBUTE_PROPERTY_NAME).getText();
									String pValue = e.element("value").getText();
									dataSourcePrivateProps.setProperty(pName, pValue);
								}
								else if (e.getName().equals(NODE_PROPERTY)){
									dataSourcePrivateProps.setProperty(eP.attributeValue(ATTRIBUTE_PROPERTY_NAME), eP.getText());
								}
							}catch(Exception ex){
								
							}
							
						}
						dataSourcePrivateProps.setProperty(eP.attributeValue(ATTRIBUTE_PROPERTY_NAME), eP.getText());
					}
					
				}
				
			}
			
			OdaInput odaInput = new OdaInput();
			odaInput.setDatasetPrivateProperties(new Properties());
			odaInput.setDatasetPublicProperties(new Properties());
			odaInput.setDatasourcePrivateProperties(dataSourcePrivateProps);
			odaInput.setDatasourcePublicProperties(dataSourcePublicProps);
			odaInput.setOdaExtensionId(dataSetExtensionId);
			odaInput.setOdaExtensionDataSourceId(dataSourceExtensionId);
			odaInput.setQueryText(queryText);
			odaInput.setName(dataSetName);
			dataSets.add(odaInput);
		}
		
		return dataSets;
	}
}
