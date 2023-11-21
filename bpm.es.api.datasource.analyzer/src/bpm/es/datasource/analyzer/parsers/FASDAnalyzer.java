package bpm.es.datasource.analyzer.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.OdaInput;


public class FASDAnalyzer implements IAnalyzer{

	private static final String NODE_DATASOURCES = "datasources";
	private static final String NODE_DATASOURCE = "datasource-item";
	private static final String NODE_DATASOURCE_ODA = "datasource-oda";
	private static final String NODE_CONNECTION = "connection";
	
	private static final String NODE_URL = "url";
	
	private static final String NODE_DATAOBJECT = "dataobject-item";
	private static final String NODE_DATAOBJECT_SELECT = "select-statment-definition";
	private static final String NODE_DATAVIEW = "dataview-item";
	private static final String NODE_DATASOURCE_EXT_ID = "odadatasourceextensionid";
	private static final String NODE_PUBLIC_PROPS = "public-properties";
	private static final String NODE_PROPERTY = "property";
	private static final String NODE_VALUE = "value";
	private static final String NODE_NAME = "name";
	private static final String NODE_PRIVATE_PROPS = "private-properties";
	private static final String NODE_DATASET_ODA = "dataobject-oda";
	private static final String NODE_DATASET_EXT_ID = "datasetextensionid";
	private static final String NODE_QUERY = "querytext";
	
	public int getObjectType() {
		return IRepositoryApi.FASD_TYPE;
	}

	public String getObjectTypeName() {
		return "FreeAnalysisSchema";
	}

	public boolean match(String xml, IPattern pattern) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		
		for(Element dataSource : (List<Element>)root.element(NODE_DATASOURCES).elements(NODE_DATASOURCE)){
			for(Element connection : (List<Element>)dataSource.elements(NODE_CONNECTION)){
				String foundUrl = connection.element(NODE_URL).getStringValue();

				
				String url = pattern.getUrl().trim();
				if (url.endsWith("/")){
					url = url.substring(0, url.length() -2);
				}
				
				if (pattern instanceof PatternTable){
					if (!url.equals(foundUrl)){
						break;
					}
					for(Element dataStream : (List<Element>)dataSource.elements(NODE_DATAOBJECT)){
						String origin = dataStream.element(NODE_DATAOBJECT_SELECT).getStringValue();
						for(String table : ((PatternTable)pattern).getTableName()){
							if (origin.contains(table)){
								return true;
							}
						}
					}
					
					for(Element dataStream : (List<Element>)dataSource.elements(NODE_DATAVIEW)){
						String origin = dataStream.element(NODE_DATAOBJECT_SELECT).getStringValue();
						for(String table : ((PatternTable)pattern).getTableName()){
							if (origin.contains(table)){
								return true;
							}
						}
					}
					
				}
				else{
					if (url.equals(foundUrl)){
						return true;
					}
				}

			}
		}
		
		return false;
	}
	
	@Override
	public List<OdaInput> extractDataSets(String xml) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		List<OdaInput> dataSets = new ArrayList<OdaInput>();
		
		
		for(Element source : (List<Element>)root.element(NODE_DATASOURCES).elements(NODE_DATASOURCE_ODA)){
			String dataSourceExtId = source.element(NODE_DATASOURCE_EXT_ID).getText();
			
			Properties dataSourcePublicProps = new Properties();
			
			for(Element p : (List<Element>)source.element(NODE_PUBLIC_PROPS).elements(NODE_PROPERTY)){
				dataSourcePublicProps.setProperty(p.element(NODE_NAME).getText(), p.element(NODE_VALUE).getText());
			}
			
			Properties dataSourcePrivateProps = new Properties();
			
			for(Element p : (List<Element>)source.element(NODE_PRIVATE_PROPS).elements(NODE_PROPERTY)){
				dataSourcePrivateProps.setProperty(p.element(NODE_NAME).getText(), p.element(NODE_VALUE).getText());
			}
			
			
			for(Element dataSet : (List<Element>)source.elements(NODE_DATASET_ODA)){
				String dataSetExtId = dataSet.element(NODE_DATASET_EXT_ID).getText();
				String dataSetName = dataSet.element(NODE_NAME).getText();
				String queryText = dataSet.element(NODE_QUERY).getText();
				
				Properties dataSetPublicProps = new Properties();
				
				for(Element p : (List<Element>)dataSet.element(NODE_PUBLIC_PROPS).elements(NODE_PROPERTY)){
					dataSetPublicProps.setProperty(p.element(NODE_NAME).getText(), p.element(NODE_VALUE).getText());
				}
				
				Properties dataSetPrivateProps = new Properties();
				
				for(Element p : (List<Element>)dataSet.element(NODE_PRIVATE_PROPS).elements(NODE_PROPERTY)){
					dataSetPrivateProps.setProperty(p.element(NODE_NAME).getText(), p.element(NODE_VALUE).getText());
				}
				
				OdaInput odaInput = new OdaInput();
				odaInput.setDatasetPrivateProperties(dataSetPrivateProps);
				odaInput.setDatasetPublicProperties(dataSetPublicProps);
				odaInput.setDatasourcePrivateProperties(dataSourcePrivateProps);
				odaInput.setDatasourcePublicProperties(dataSourcePublicProps);
				odaInput.setOdaExtensionId(dataSetExtId);
				odaInput.setOdaExtensionDataSourceId(dataSourceExtId);
				odaInput.setQueryText(queryText);
				odaInput.setName(dataSetName);
				dataSets.add(odaInput);
			}
			
		}
		
		return dataSets;
	}
}
