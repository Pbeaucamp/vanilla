package bpm.vanilla.workplace.api.datasource.extractor;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasourceType;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;
import bpm.vanilla.workplace.core.disco.DisconnectedDataset;

public class FASDDatasourceExtractor implements IDatasourceExtractor{
	
	private static final String NODE_DATASOURCES = "datasources";
	private static final String NODE_DATASOURCE_ODA = "datasource-oda";
	
	private static final String NODE_DATASOURCE_ID = "id";
	
	private static final String NODE_EXT_ID = "odaextensionid";
	private static final String NODE_PUBLIC_PROPS = "public-properties";
	private static final String NODE_PRIVATE_PROPS = "private-properties";
	
	private static final String NODE_DATASET = "dataobject-oda";
	private static final String NODE_DATASET_DATASOURCE_ID = "data-source-id";
	private static final String NODE_QUERY_TEXT = "querytext";
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();

		Document document = DocumentHelper.parseText(xml);
		Element ds = document.getRootElement().element(NODE_DATASOURCES);
		
		if (ds != null){
			for(Object o : ds.elements(NODE_DATASOURCE_ODA)){
				String datasourceName = ((Element)o).element("name").getText();
				String datasourceId = ((Element)o).element(NODE_DATASOURCE_ID).getText();
				String extId = ((Element)o).element(NODE_EXT_ID).getText();
				if (extId.equals(IDatasourceType.METADATA) || extId.equals(IDatasourceType.METADATA_OLAP)){

					String user = null;
					String password = null;
					String url = null;
					String dirId = null;
					String groupName = null;
					String repositoryId = null;
					String connectionName = null;
					String businessPackage = null;
					String businessModel = null;
					for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
						if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_USER)){
							user = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_PASSWORD)){
							password = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_URL)){
							url = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)){
							dirId = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_REPOSITORY_ID)){
							repositoryId = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.GROUP_NAME)){
							groupName = ((Element)p).element("value").getText();
						}
						else if (((Element) p).element("name").getText().equals(IDatasourceType.BUSINESS_PACKAGE)) {
							businessPackage = ((Element) p).getText();
						}
						else if (((Element) p).element("name").getText().equals(IDatasourceType.BUSINESS_MODEL)) {
							businessModel = ((Element) p).getText();
						}
					}
					
					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setId(datasourceId);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setRepositoryUrl(url);
					datasource.setDirId(dirId);
					datasource.setGroupName(groupName);
					datasource.setRepositoryId(repositoryId);
					datasource.setConnectionName(connectionName);
					datasource.setBusinessPackage(businessPackage);
					datasource.setBusinessModel(businessModel);
					
					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.CSV_ODA) || extId.equals(IDatasourceType.EXCEL_ODA)){

					String user = null;
					String password = null;
					String repositoryId = null;
					String repositoryItemId = null;
					String groupId = null;
					for(Object p : ((Element)o).element(NODE_PRIVATE_PROPS).elements("property")){
						if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ITEM_ID)){
							repositoryItemId = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ID)){
							repositoryId = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_GROUP_ID)){
							groupId = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_USER)){
							user = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.CSV_PASSWORD)){
							password = ((Element)p).element("value").getText();
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setDirId(repositoryItemId);
					datasource.setGroupId(groupId);
					datasource.setRepositoryId(repositoryId);
					
					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.FWR_ODA)){

					String user = null;
					String password = null;
					String repositoryUrl = null;
					String url = null;
					String dirId = null;
					String groupName = null;
					String groupId = null;
					for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
						if(((Element)p).element("name").getText().equals(IDatasourceType.FWR_USER)){
							user = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.FWR_PASSWORD)){
							password = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.FWR_URL)){
							url = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_REPOSITORY_URL)){
							repositoryUrl = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_GROUP_ID)){
							groupId = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_GROUP_NAME)){
							groupName = ((Element)p).element("value").getText();
						}
					}

					for(Object p : ((Element)o).element(NODE_PRIVATE_PROPS).elements("property")){
						if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_REPORT_ID)){
							dirId = ((Element)p).element("value").getText();
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setRepositoryUrl(repositoryUrl);
					datasource.setVanillaRuntimeUrl(url);
					datasource.setDirId(dirId);
					datasource.setGroupName(groupName);
					datasource.setGroupId(groupId);
					
					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.JDBC)){

					String driver = null;
					String url = null;
					String user = null;
					String password = null;
					boolean isPasswordEncrypted = false;
					
					for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
						if(((Element)p).element("name").getText().equals(IDatasourceType.ODA_DRIVER)){
							driver = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.ODA_URL)){
							url = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.ODA_USER)){
							user = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_PASSWORD)){
							password = ((Element)p).element("value").getText();
						}
					}

					ModelDatasourceJDBC datasource = new ModelDatasourceJDBC();
					datasource.setId(datasourceId);
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_JDBC);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setDriver(driver);
					datasource.setFullUrl(url);
					datasource.setUseFullUrl(true);
					datasource.setIsPasswordEncrypted(isPasswordEncrypted);
					
					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.LIST_DATA)){
					
					String user = null;
					String password = null;
					String vanillaRuntimeUrl = null;
					String repositoryId = null;
					for(Object p : ((Element)o).element(NODE_PUBLIC_PROPS).elements("property")){
						if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_USER)){
							user = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_PASSWORD)){
							password = ((Element)p).element("value").getText();
						}
						else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)){
							repositoryId = ((Element)p).element("value").getText();
						}
						else if (((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_RUNTIME_URL)){
							vanillaRuntimeUrl = ((Element)p).element("value").getText();
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setVanillaRuntimeUrl(vanillaRuntimeUrl);
					datasource.setRepositoryId(repositoryId);
					
					datasources.add(datasource);
				}
			}
		}
		
		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		DisconnectedBackupConnection backupConnection = new DisconnectedBackupConnection();
		backupConnection.setItemId(itemId);

		Document document = DocumentHelper.parseText(xml);

		Element ds = document.getRootElement().element(NODE_DATASOURCES);
		if (ds != null) {
			backupConnection.setDatasourcesXML(ds.asXML());

			if (ds != null){
				for(Object datasourceEl : ds.elements(NODE_DATASOURCE_ODA)){
					if (((Element)datasourceEl).elements(NODE_DATASET) != null) {
						for (Object o : ((Element)datasourceEl).elements(NODE_DATASET)) {
							String datasetName = ((Element) o).element("name").getText();
							String datasourceId = ((Element) o).element(NODE_DATASET_DATASOURCE_ID).getText();
							String queryText = ((Element) o).element(NODE_QUERY_TEXT).getText();
		
							DisconnectedDataset discoDataset = new DisconnectedDataset();
							discoDataset.setName(datasetName);
							discoDataset.setDatasourceName(datasourceId);
							discoDataset.setQueryText(queryText);
		
							backupConnection.addDataset(discoDataset);
						}
					}
				}
			}
		}

		return backupConnection;
	}

}
