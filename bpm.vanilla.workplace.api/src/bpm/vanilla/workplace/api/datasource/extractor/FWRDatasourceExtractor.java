package bpm.vanilla.workplace.api.datasource.extractor;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.thoughtworks.xstream.XStream;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;

public class FWRDatasourceExtractor implements IDatasourceExtractor{
	
	private static final String NODE_DATASET = "dataset";
	private static final String NODE_NAME = "name";
	private static final String NODE_DATASOURCE = "datasource";
	private static final String NODE_GROUP = "group";
	private static final String NODE_ITEM = "itemid";
	private static final String NODE_PASSWORD = "password";
	private static final String NODE_URL = "url";
	private static final String NODE_USER = "user";

	private static final String NODE_DATASETS = "datasets";
	private static final String NODE_CONTAINER = "container";
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		try {
			return getDatasourcesForNewModel(xml);
		} catch(Exception e) {
			e.printStackTrace();
			return getDatasourcesForOldModel(xml);
		}

	}
	private List<IDatasource> getDatasourcesForNewModel(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();
		
		XStream xstream = new XStream();
		FWRReport report = (FWRReport) xstream.fromXML(xml);
		
		List<DataSet> datasets = report.getAllDatasets();
		if(datasets != null) {
			for(DataSet dataset : datasets){
				DataSource datasource = dataset.getDatasource();
				
				ModelDatasourceRepository ds = new ModelDatasourceRepository();
				ds.setName(datasource.getName());
				ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
				ds.setDirId(String.valueOf(datasource.getItemId()));
				ds.setGroupName(datasource.getGroup());
				ds.setUser(datasource.getUser());
				ds.setPassword(datasource.getPassword());
				ds.setRepositoryUrl(datasource.getUrl());
				
				datasources.add(ds);
			}
		}
		
		return datasources;
	}
	
	private List<IDatasource> getDatasourcesForOldModel(String xml) throws DocumentException {
		List<IDatasource> datasources = new ArrayList<IDatasource>();
		
		Document document = DocumentHelper.parseText(xml);
		Element root = null;
		for(Object e : document.getRootElement().elements()){
			if (((Element)e).element(NODE_CONTAINER) != null){
				root = ((Element)e).element(NODE_CONTAINER);
				break;
			}
		}
		
		if(root != null && root.element(NODE_DATASETS).elements(NODE_DATASET) != null){
			
			for(Object dataSet :  root.element(NODE_DATASETS).elements(NODE_DATASET)){
				Element eSource = ((Element)dataSet).element(NODE_DATASOURCE);
				
				String name = eSource.element(NODE_NAME).getText();
				String group = eSource.element(NODE_GROUP).getText();
				String itemid = eSource.element(NODE_ITEM).getText();
				String password = eSource.element(NODE_PASSWORD).getText();
//				String isencrypted = eSource.element(NODE_ENCRYPTED).getText();
				String url = eSource.element(NODE_URL).getText();
				String user = eSource.element(NODE_USER).getText();

				ModelDatasourceRepository ds = new ModelDatasourceRepository();
				ds.setName(name);
				ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
				ds.setDirId(itemid);
				ds.setGroupName(group);
				ds.setUser(user);
				ds.setPassword(password);
				ds.setRepositoryUrl(url);

				datasources.add(ds);
			}
		}
		
		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		return null;
	}

}
