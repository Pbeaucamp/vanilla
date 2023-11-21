package bpm.vanilla.workplace.api.datasource.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.IDatasourceType;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceFD;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;
import bpm.vanilla.workplace.core.disco.DisconnectedDataset;

public class FDDicoDatasourceExtractor implements IDatasourceExtractor{
	
	private static final String NODE_DATASOURCE = "dataSource";

	private static final String NODE_MAP = "map";
	private static final String NODE_MAP_INFO = "mapInfo";
	private static final String NODE_VANILLA_RUNTIME_URL = "vanillaRuntimeUrl";
	
	private static final String NODE_OLAP_VIEW = "olapView";
	private static final String NODE_DIRECTORY_ITEM_ID = "directoryItemId";
	
	private static final String NODE_DEPENDANCIES = "dependancies";
	private static final String NODE_DEPENDANCIES_ITEM_ID = "dependantDirectoryItemId";
	
	private static final String NODE_EXT_ID = "odaExtensionDataSourceId";
	private static final String NODE_PUBLIC_PROPS = "publicProperty";
	private static final String NODE_PRIVATE_PROPS = "privateProperty";

	private static final String NODE_DATASET = "dataSet";
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();

		Document document = DocumentHelper.parseText(xml);

		if (document.getRootElement().elements(NODE_DATASOURCE) != null){
			for(Object o : document.getRootElement().elements(NODE_DATASOURCE)){
				String datasourceName = ((Element)o).element("name").getText();
				String extId = ((Element)o).element(NODE_EXT_ID).getText();
				if (extId.equals(IDatasourceType.METADATA) || extId.equals(IDatasourceType.METADATA_OLAP)){

					String user = null;
					String password = null;
					String vanillaRuntimeUrl = null;
					String dirId = null;
					String groupName = null;
					String repositoryId = null;
					String connectionName = null;
					String businessPackage = null;
					String businessModel = null;
					for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
						if(((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_USER)){
							user = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_PASSWORD)){
							password = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_VANILLA_URL)){
							vanillaRuntimeUrl = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)){
							dirId = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.METADATA_REPOSITORY_ID)){
							repositoryId = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.GROUP_NAME)){
							groupName = ((Element)p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.BUSINESS_PACKAGE)) {
							businessPackage = ((Element) p).getText();
						}
						else if (((Element) p).attribute("name").getText().equals(IDatasourceType.BUSINESS_MODEL)) {
							businessModel = ((Element) p).getText();
						}
					}
					

					for(Object p : ((Element)o).elements(NODE_PRIVATE_PROPS)){
						if(((Element)p).attributeValue("name").equals(IDatasourceType.CONNECTION_NAME)){
							connectionName = ((Element)p).getText();
						}
					}
					
					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setVanillaRuntimeUrl(vanillaRuntimeUrl);
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
					for(Object p : ((Element)o).elements(NODE_PRIVATE_PROPS)){
						if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_REPOSITORY_ITEM_ID)){
							repositoryItemId = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_REPOSITORY_ID)){
							repositoryId = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_GROUP_ID)){
							groupId = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_USER)){
							user = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.CSV_PASSWORD)){
							password = ((Element)p).getText();
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
					for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
						if(((Element)p).attributeValue("name").equals(IDatasourceType.FWR_USER)){
							user = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.FWR_PASSWORD)){
							password = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.FWR_URL)){
							url = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_REPOSITORY_URL)){
							repositoryUrl = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_GROUP_ID)){
							groupId = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_GROUP_NAME)){
							groupName = ((Element)p).getText();
						}
					}

					for(Object p : ((Element)o).elements(NODE_PRIVATE_PROPS)){
						if (((Element)p).attributeValue("name").equals(IDatasourceType.FWR_REPORT_ID)){
							dirId = ((Element)p).getText();
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
					for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
						if(((Element)p).attributeValue("name").equals(IDatasourceType.ODA_DRIVER)){
							driver = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.ODA_URL)){
							url = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.ODA_USER)){
							user = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.ODA_PASSWORD)){
							password = ((Element)p).getText();
						}
					}

					ModelDatasourceJDBC datasource = new ModelDatasourceJDBC();
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
					String vanillaRepositoryId = null;
					for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
						if(((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_USER)){
							user = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_PASSWORD)){
							password = ((Element)p).getText();
						}
						else if(((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_RUNTIME_URL)){
							vanillaRuntimeUrl = ((Element)p).getText();
						}
						else if (((Element)p).attributeValue("name").equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)){
							vanillaRepositoryId = ((Element)p).getText();
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setVanillaRuntimeUrl(vanillaRuntimeUrl);
					datasource.setRepositoryId(vanillaRepositoryId);
					
					datasources.add(datasource);
				}
				else if (extId.equals(IDatasourceType.FUSION_MAP)){
					
					String vanillaRuntimeUrl = null;
					for(Object p : ((Element)o).elements(NODE_PUBLIC_PROPS)){
						if(((Element)p).attributeValue("name").equals(IDatasourceType.MAP_RUNTIME_URL)){
							vanillaRuntimeUrl = ((Element)p).getText();
						}
					}

					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(datasourceName);
					datasource.setExtensionId(extId);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setVanillaRuntimeUrl(vanillaRuntimeUrl);
					
					datasources.add(datasource);
				}
			}
		}
		
		if (document.getRootElement().elements(NODE_MAP) != null){
			for(Object o : document.getRootElement().elements(NODE_MAP)){
				String datasourceName = ((Element)o).attributeValue("name");
				String vanillaRuntimeUrl = "";
				if(((Element)o).element(NODE_MAP_INFO) != null){
					vanillaRuntimeUrl = ((Element)o).element(NODE_MAP_INFO).attributeValue(NODE_VANILLA_RUNTIME_URL);
				}
				
				ModelDatasourceRepository ds = new ModelDatasourceRepository();
				ds.setName(datasourceName);
				ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
				ds.setVanillaRuntimeUrl(vanillaRuntimeUrl);
				
				datasources.add(ds);
			}	
		}
		
		if (document.getRootElement().elements(NODE_OLAP_VIEW) != null){
			for(Object o : document.getRootElement().elements(NODE_OLAP_VIEW)){
				String datasourceName = ((Element)o).attributeValue("name");
				String dirId = "";
				if(((Element)o).element(NODE_DIRECTORY_ITEM_ID) != null){
					dirId = ((Element)o).element(NODE_DIRECTORY_ITEM_ID).getText();
				}
				
				ModelDatasourceRepository ds = new ModelDatasourceRepository();
				ds.setName(datasourceName);
				ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
				ds.setDirId(dirId);
				
				datasources.add(ds);
			}	
		}
		
		if (document.getRootElement().elements(NODE_DEPENDANCIES) != null){
			for(Object o : document.getRootElement().elements(NODE_DEPENDANCIES)){
				
				HashMap<Integer, Integer> dependancies = new HashMap<Integer, Integer>();
				for(Object p : ((Element)o).elements(NODE_DEPENDANCIES_ITEM_ID)){
					
					int depId = Integer.parseInt(((Element)p).getText());
					
					dependancies.put(depId, depId);
				}

				ModelDatasourceFD ds = new ModelDatasourceFD();
				ds.setName(IDatasourceType.NAME_FDDICO_DEPENDANCIES);
				ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
				ds.setDependancies(dependancies);
				
				datasources.add(ds);
			}	
		}
		
		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		DisconnectedBackupConnection backupConnection = new DisconnectedBackupConnection();
		backupConnection.setItemId(itemId);

		Document document = DocumentHelper.parseText(xml);

		if (document.getRootElement().elements(NODE_DATASOURCE) != null) {
			StringBuffer buf = new StringBuffer();
			for(Object el : document.getRootElement().elements(NODE_DATASOURCE)) {
				buf.append(((Element)el).asXML());
			}
			backupConnection.setDatasourcesXML(buf.toString());
		}

		if (document.getRootElement().elements(NODE_DATASET) != null) {
			for (Object o : document.getRootElement().elements(NODE_DATASET)) {
				String datasetName = ((Element) o).element("name").getText();
				String queryText = ((Element) o).element("queryText").getText();
				String datasourceName = ((Element) o).element("dataSourceName").getText();

				DisconnectedDataset discoDataset = new DisconnectedDataset();
				discoDataset.setName(datasetName);
				discoDataset.setDatasourceName(datasourceName);
				discoDataset.setQueryText(queryText);

				backupConnection.addDataset(discoDataset);
			}
		}

		return backupConnection;
	}

}
