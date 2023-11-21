package bpm.es.datasource.analyzer.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.data.OdaInput;

public class FWRAnalyzer implements IAnalyzer{
	private static final String DATASOURCE_EXTENSION_ID = "bpm.metadata.birt.oda.runtime";
	private static final String DATASOURCE_EXTENSION_ID_UOLAP ="bpm.metadata.birt.oda.runtime.olap";
	private static final String DATASET_EXTENSION_ID = "bpm.metadata.birt.oda.runtime.dataSet";
	private static final String DATASET_EXTENSION_ID_UOLAP ="bpm.metadata.birt.oda.runtime.olap.dataset";
	private static final String NODE_DATASET = "dataset";
	private static final String NODE_NAME = "name";
	private static final String NODE_COLUMNS = "columns";
	private static final String NODE_PARENT = "parent";
	private static final String NODE_DATASOURCE = "datasource";
	private static final String NODE_MODEL = "model";
	private static final String NODE_PACKAGE = "package";
	private static final String NODE_CONNECTION = "connection";
	private static final String NODE_GROUP = "group";
	private static final String NODE_ITEM = "itemid";
	private static final String NODE_PASSWORD = "password";
	private static final String NODE_ENCRYPTED = "isencrypted";
	private static final String NODE_URL = "url";
	private static final String NODE_USER = "user";
	private static final String NODE_ISONOLAP = "isonolap";
	

	private static final String ODA_USER = "USER";
	private static final String ODA_PASSWORD = "PASSWORD";
	private static final String ODA_URL = "URL";
	private static final String ODA_DIRECTORY_ITEM_ID= "DIRECTORY_ITEM_ID";
	private static final String ODA_BUSINESS_MODEL= "BUSINESS_MODEL";
	private static final String ODA_BUSINESS_PACKAGE= "BUSINESS_PACKAGE";
	private static final String ODA_CONNECTION_NAME= "CONNECTION_NAME";
	private static final String ODA_GROUP_NAME = "GROUP_NAME";
	private static final String NODE_DATASETS = "datasets";
	private static final String NODE_CONTAINER = "container";
	
	
	private static class Column{
		private String name;
		private String parent;
		public Column(String name, String parent) {
			super();
			this.name = name;
			this.parent = parent;
		}
		
		
	}
	
	
	@Override
	public List<OdaInput> extractDataSets(String xml) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = null;
		for(Element e : (List<Element>)doc.getRootElement().elements()){
			if (e.element(NODE_CONTAINER) != null){
				root =e.element(NODE_CONTAINER);
				break;
			}
		}
		
		List<OdaInput> dataSets = new ArrayList<OdaInput>();
		if (root == null || root.element(NODE_DATASETS) == null){
			return dataSets;
		}
		for(Element dataSet :  (List<Element>)root.element(NODE_DATASETS).elements(NODE_DATASET)){
			String dataSetName = dataSet.element(NODE_NAME).getText();
			List<Column> cols = new ArrayList<Column>();
			
			/*
			 * get the columns
			 */
			for(Element col : (List<Element>)dataSet.element(NODE_COLUMNS).elements()){
				String columnName = col.element(NODE_NAME).getText();
				String parentName = col.element(NODE_PARENT).getText();
				cols.add(new Column(columnName, parentName));
			}
			
			Element eSource = dataSet.element(NODE_DATASOURCE);
			String model = eSource.element(NODE_MODEL).getText();
			String pack = eSource.element(NODE_PACKAGE).getText();
			String connection = eSource.element(NODE_CONNECTION).getText();
			String group = eSource.element(NODE_GROUP).getText();
			String itemid = eSource.element(NODE_ITEM).getText();
			String password = eSource.element(NODE_PASSWORD).getText();
			String isencrypted = eSource.element(NODE_ENCRYPTED).getText();
			String url = eSource.element(NODE_URL).getText();
			String user = eSource.element(NODE_USER).getText();
			boolean isonOlap = false;
			if (eSource.element(NODE_ISONOLAP) != null){
				isonOlap = Boolean.parseBoolean(eSource.element(NODE_ISONOLAP).getText());
			}
			
			
			/*
			 * generate odaInput properties
			 */
			Properties publicProps = new Properties();
			publicProps.setProperty(ODA_BUSINESS_MODEL, model);
			publicProps.setProperty(ODA_BUSINESS_PACKAGE, pack);
			
			publicProps.setProperty(ODA_GROUP_NAME, group);
			publicProps.setProperty(ODA_DIRECTORY_ITEM_ID, itemid);
			publicProps.setProperty(ODA_PASSWORD, password);
			publicProps.setProperty(ODA_URL, url);
			publicProps.setProperty(ODA_USER, user);
			
			Properties privateProps = new Properties();
			privateProps.setProperty(ODA_CONNECTION_NAME, connection);
			
			OdaInput odaInput = new OdaInput();
			odaInput.setDatasetPrivateProperties(new Properties());
			odaInput.setDatasetPublicProperties(new Properties());
			odaInput.setDatasourcePrivateProperties(privateProps);
			odaInput.setDatasourcePublicProperties(publicProps);
			
			String queryText = generateQueryText(cols);
			
			if (!isonOlap){
				odaInput.setOdaExtensionId(DATASET_EXTENSION_ID);
				odaInput.setOdaExtensionDataSourceId(DATASOURCE_EXTENSION_ID);
				odaInput.setQueryText(queryText);
			}
			else{
				odaInput.setOdaExtensionId(DATASET_EXTENSION_ID_UOLAP);
				odaInput.setOdaExtensionDataSourceId(DATASOURCE_EXTENSION_ID_UOLAP);
				odaInput.setQueryText(queryText);
			}

			odaInput.setName(dataSetName);
			dataSets.add(odaInput);
			
		}
		
		
		return dataSets;
	}

	private String generateQueryText(List<Column> cols) {
		Element root = DocumentHelper.createElement("freeMetaDataQuery");
		Element q = root.addElement("sqlQuery");
		q.addElement("distinct").setText("false");
		q.addElement("limit").setText("0");
		
		
		for(Column c : cols){
			Element s = q.addElement("selectFromBusinessTable");
			s.addElement("dataStreamElementName").setText(c.name);
			s.addElement("businessTableName").setText(c.parent);
		}
		return root.asXML();
	}

	@Override
	public int getObjectType() {
		
		return 0;
	}

	@Override
	public String getObjectTypeName() {
		
		return null;
	}

	@Override
	public boolean match(String xml, IPattern pattern) throws Exception {
		
		return false;
	}

}
