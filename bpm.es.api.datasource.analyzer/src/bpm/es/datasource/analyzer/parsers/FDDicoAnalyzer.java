package bpm.es.datasource.analyzer.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class FDDicoAnalyzer implements IAnalyzer{

	private static final String NODE_DATASOURCE = "dataSource";
	private static final String NODE_DATASOURCE_NAME = "name";
	private static final String NODE_ODA_EXTENSIONID = "odaExtensionDataSourceId";
	private static final String NODE_ODA_DATASET_EXTENSIONID = "odaExtensionDataSetId";
	private static final String NODE_DATASOURCE_PROPERTY = "publicProperty";
	private static final String NODE_PRIVATE_PROPERTY = "privateProperty";
	
	private static final String ATTRIBUTE_URL = "odaURL";
	private static final String ODA_JDBC_EXTENSIONID = "org.eclipse.birt.report.data.oda.jdbc";
	
	
	private static final String NODE_DATASET = "dataSet";
	private static final String NODE_DATASET_NAME = "name";
	private static final String NODE_DATASET_OWNER = "dataSourceName";
	private static final String NODE_QUERY = "queryText";

	
	public int getObjectType() {
		return IRepositoryApi.FD_DICO_TYPE;
	}

	public String getObjectTypeName() {
		return "FreeDashboard Dictionary";
	}

	public boolean match(String xml, IPattern pattern) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		for(Element dataSource : (List<Element>)root.elements(NODE_DATASOURCE)){
			if (ODA_JDBC_EXTENSIONID.equals(dataSource.element(NODE_ODA_EXTENSIONID).getStringValue())){
				String dsName = dataSource.element(NODE_DATASOURCE_NAME).getStringValue();
				
				String url = null;
				for(Element p : (List<Element>)dataSource.elements(NODE_DATASOURCE_PROPERTY)){
					if (ATTRIBUTE_URL.equals(p.attributeValue("name"))){
						url = p.getStringValue();
						break;
					}
				}
				if (url == null){
					continue;
				}
				
				if (url.equals(pattern.getUrl())){
					
					if (pattern instanceof PatternTable){
						for(Element dataSet : (List<Element>)root.elements(NODE_DATASET)){
							if (dsName.equals(dataSet.element(NODE_DATASET_OWNER).getStringValue())){
								String query = dataSet.element(NODE_QUERY).getStringValue();
								
								for(String s : ((PatternTable)pattern).getTableName()){
									if (query.contains(s)){
										return true;
									}
								}
							}
						}
					}
					else{
						return true;
					}
					
				}
				
			}
		}
		return false;
	}

	@Override
	public List<OdaInput> extractDataSets(String xml)throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		List<OdaInput> dataSets = new ArrayList<OdaInput>();
		
		for(Element dataSet : (List<Element>)root.selectNodes("//" + NODE_DATASET)){
			String queryText = dataSet.element(NODE_QUERY).getText();
			String dataSetExtensionId = dataSet.element(NODE_ODA_DATASET_EXTENSIONID).getText();
			String dataSourceExtensionId = dataSet.element(NODE_ODA_EXTENSIONID).getText();
			String dataSetName =  dataSet.element(NODE_DATASET_NAME).getText();
			Properties privateProps = new Properties();
			
			for(Element privateProp : (List<Element>)dataSet.elements(NODE_PRIVATE_PROPERTY)){
				privateProps.setProperty(privateProp.attributeValue("name"), privateProp.getText());
			}
			Properties publicProps = new Properties();
			for(Element publicProp : (List<Element>)dataSet.elements(NODE_DATASOURCE_PROPERTY)){
				publicProps.setProperty(publicProp.attributeValue("name"), publicProp.getText());
			}
			
			String dataSourceName = dataSet.element(NODE_DATASET_OWNER).getText();
			
			
			/*
			 * gather dataSourceInfos
			 */
			Properties dataSourceprivateProps = new Properties();
			Properties dataSourcepublicProps = new Properties();
			for(Element dataSource : (List<Element>)root.selectNodes("//" + NODE_DATASOURCE)){
				if (dataSource.element(NODE_DATASOURCE_NAME).getText().equals(dataSourceName)){

					for(Element privateProp : (List<Element>)dataSource.elements(NODE_PRIVATE_PROPERTY)){
						dataSourceprivateProps.setProperty(privateProp.attributeValue("name"), privateProp.getText());
					}
					
					for(Element publicProp : (List<Element>)dataSource.elements(NODE_DATASOURCE_PROPERTY)){
						dataSourcepublicProps.setProperty(publicProp.attributeValue("name"), publicProp.getText());
					}
				}
			}
			
			OdaInput odaInput = new OdaInput();
			odaInput.setDatasetPrivateProperties(privateProps);
			odaInput.setDatasetPublicProperties(publicProps);
			odaInput.setDatasourcePrivateProperties(dataSourceprivateProps);
			odaInput.setDatasourcePublicProperties(dataSourcepublicProps);
			odaInput.setOdaExtensionId(dataSetExtensionId);
			odaInput.setOdaExtensionDataSourceId(dataSourceExtensionId);
			odaInput.setQueryText(queryText);
			odaInput.setName(dataSetName);
			dataSets.add(odaInput);
			
		}
		return dataSets;
	}

}
