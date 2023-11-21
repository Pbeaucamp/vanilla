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

public class FMDTDatasourceExtractor implements IDatasourceExtractor{

	private static final String NODE_DATASOURCE = "sqlDataSource";
	private static final String NODE_CONNECTION = "sqlConnection";
	
	private static final String NODE_DATASOURCE_OLAP = "unitedOlapDatasource";
	private static final String NODE_CONNECTION_OLAP = "unitedOlapConnection";
	private static final String NODE_IDENTIFIER = "identifier";
	private static final String NODE_CONTEXT = "runtimeContext";
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();
		
		Document document = DocumentHelper.parseText(xml);

		if(document.getRootElement().elements(NODE_DATASOURCE) != null){
			for(Object ds : document.getRootElement().elements(NODE_DATASOURCE)){
				
				String databaseName = ((Element)ds).element("name").getStringValue(); 
				for(Object c : ((Element)ds).elements(NODE_CONNECTION)){
					String driverName = ((Element)c).element(IDatasourceType.DRIVER) != null ? ((Element)c).element(IDatasourceType.DRIVER).getStringValue() : null;
					String host = ((Element)c).element(IDatasourceType.HOST) != null ? ((Element)c).element(IDatasourceType.HOST).getStringValue() : null;
					String port = ((Element)c).element(IDatasourceType.PORT) != null ? ((Element)c).element(IDatasourceType.PORT).getStringValue() : null;
					String dbName = ((Element)c).element(IDatasourceType.DB_NAME) != null ? ((Element)c).element(IDatasourceType.DB_NAME).getStringValue() : null;
					
					String useFullUrl = ((Element)c).element(IDatasourceType.USE_FULL_URL) != null ? ((Element)c).element(IDatasourceType.USE_FULL_URL).getStringValue() : null;
					String fullUrl = ((Element)c).element(IDatasourceType.FULL_URL) != null ? ((Element)c).element(IDatasourceType.FULL_URL).getStringValue() : null;
					
					String userName = ((Element)c).element(IDatasourceType.USER).getStringValue(); 
					String password = ((Element)c).element(IDatasourceType.PASSWORD).getStringValue();
					
					
					ModelDatasourceJDBC datasource = new ModelDatasourceJDBC();
					datasource.setName(databaseName);
					datasource.setType(DatasourceType.DATASOURCE_JDBC);
					datasource.setDriver(driverName);
					datasource.setHost(host);
					datasource.setPort(port);
					datasource.setUser(userName);
					datasource.setPassword(password);
					datasource.setDbName(dbName);
					datasource.setUseFullUrl(Boolean.parseBoolean(useFullUrl));
					datasource.setFullUrl(fullUrl);
					
					datasources.add(datasource);
				}
			}
		}


		if(document.getRootElement().elements(NODE_DATASOURCE_OLAP) != null){
			for(Object ds : document.getRootElement().elements(NODE_DATASOURCE_OLAP)){
				
				String databaseName = ((Element)ds).element("name").getStringValue(); 
				for(Object c : ((Element)ds).elements(NODE_CONNECTION_OLAP)){
					
					String fasdId = null;
					String repositoryId = null;
					for(Object i : ((Element)c).elements(NODE_IDENTIFIER)){
						fasdId = ((Element)i).element(IDatasourceType.OLAP_FASD_ID).getStringValue();
						repositoryId = ((Element)i).element(IDatasourceType.OLAP_REPOSITORY_ID).getStringValue();
					}
					
					String user = null;
					String password = null;
					String groupName = null;
					String groupId = null;
					for(Object context : ((Element)c).elements(NODE_CONTEXT)){
						user = ((Element)context).element(IDatasourceType.OLAP_USER).getStringValue();
						password = ((Element)context).element(IDatasourceType.OLAP_PASSWORD).getStringValue();
						groupName = ((Element)context).element(IDatasourceType.OLAP_GROUP_NAME).getStringValue();
						groupId = ((Element)context).element(IDatasourceType.OLAP_GROUP_ID).getStringValue();
					}
					
					ModelDatasourceRepository datasource = new ModelDatasourceRepository();
					datasource.setName(databaseName);
					datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
					datasource.setUser(user);
					datasource.setPassword(password);
					datasource.setGroupName(groupName);
					datasource.setGroupId(groupId);
					datasource.setDirId(fasdId);
					datasource.setRepositoryId(repositoryId);
					
					datasources.add(datasource);
				}
			}
		}
		
		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		return null;
	}

}
