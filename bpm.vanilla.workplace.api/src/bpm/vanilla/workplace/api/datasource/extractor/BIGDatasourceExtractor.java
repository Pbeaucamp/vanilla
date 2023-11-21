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

public class BIGDatasourceExtractor implements IDatasourceExtractor{
	
	private static final String NODE_ODA_INPUT = "odaInput";
	private static final String NODE_ODA_INPUT_WITH_PARAM = "odaInputWithParameters";
	private static final String NODE_EXT_ID = "odaExtensionId";
	
	private static final String NODE_PUBLIC_DATASOURCE = "publicDataSource";
	private static final String NODE_PRIVATE_DATASOURCE = "privateDataSource";
	
	private static final String NODE_SERVER = "servers";
	private static final String NODE_DATABASE_SERVER = "dataBaseServer";
	private static final String NODE_FM_SERVER = "freemetricsServer";
	private static final String NODE_DATABASE_CONNECTION = "dataBaseConnection";
	
	private static final String NODE_OLAP_INPUT = "olapInput";
	private static final String NODE_FMDT_INPUT = "fmdtInput";
	private static final String NODE_OLAP_DIM_INPUT = "olapDimensionInput";
	
	private static final String NODE_DB_OUTPUT = "dataBaseOutputStream";
	
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();

		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		if (root.elements(NODE_ODA_INPUT) != null){
			
			for(Object o : root.elements(NODE_ODA_INPUT)){
				IDatasource ds = getDatasourceFromOdaInput(o);
				if(ds != null){
					datasources.add(ds);
				}
			}
		}
		
		if (root.elements(NODE_ODA_INPUT_WITH_PARAM) != null){
			
			for(Object o : root.elements(NODE_ODA_INPUT_WITH_PARAM)){
				IDatasource ds = getDatasourceFromOdaInput(o);
				if(ds != null){
					datasources.add(ds);
				}
			}
		}
		
		if (document.getRootElement().element(NODE_SERVER) != null){
			
			Element servers = document.getRootElement().element(NODE_SERVER);
			
			if(servers.elements(NODE_DATABASE_SERVER) != null){
				for(Object o : servers.elements(NODE_DATABASE_SERVER)){
					String datasourceName = ((Element)o).element("name").getText();
					String host = null;
					String port = null;
					String databasename = null;
					String user = null;
					String password = null;
					String driverName = null;
					String useFullUrl = null;
					String fullUrl = null;
					
					Element con = ((Element)o).element(NODE_DATABASE_CONNECTION);
					if(con != null){
						if(con.element("host") != null){
							host = con.element("host").getText();
						}
						
						if(con.element("port") != null){
							port = con.element("port").getText();
						}
						
						if(con.element("dataBaseName") != null){
							databasename = con.element("dataBaseName").getText();
						}
						
						if(con.element("login") != null){
							user = con.element("login").getText();
						}
						
						if(con.element("password") != null){
							password = con.element("password").getText();
						}
						
						if(con.element("driverName") != null){
							driverName = con.element("driverName").getText();
						}
						
						if(con.element("useFullUrl") != null){
							useFullUrl = con.element("useFullUrl").getText();
						}
						
						if(con.element("fullUrl") != null){
							fullUrl = con.element("fullUrl").getText();
						}
					}
					
					ModelDatasourceJDBC ds = new ModelDatasourceJDBC();
					ds.setName(datasourceName);
					ds.setType(DatasourceType.DATASOURCE_JDBC);
					ds.setDriver(driverName);
					ds.setUser(user);
					ds.setPassword(password);
					ds.setPort(port);
					ds.setHost(host);
					ds.setDbName(databasename);
					ds.setUseFullUrl(Boolean.parseBoolean(useFullUrl));
					ds.setFullUrl(fullUrl);
					
					//Try to find if it's an out datasource
					try {
						if(document.getRootElement().elements(NODE_DB_OUTPUT) != null){
							for(Object output : document.getRootElement().elements(NODE_DB_OUTPUT)){
								if(((Element)output).element("serverRef").getText().equals(ds.getName())) {
									ds.setOut(true);
								}
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					datasources.add(ds);
				}
			}
			
			if(servers.elements(NODE_FM_SERVER) != null){
				for(Object o : servers.elements(NODE_FM_SERVER)){
					
					String datasourceName = ((Element)o).element("name").getText();
					String fmUser = ((Element)o).element("freemtricsLogin").getText();
					String fmPassword = ((Element)o).element("freemtricsPassword").getText();
					
					String host = null;
					String port = null;
					String databasename = null;
					String user = null;
					String password = null;
					String driverName = null;
					String useFullUrl = null;
					String fullUrl = null;
					
					Element con = ((Element)o).element(NODE_DATABASE_CONNECTION);
					if(con != null){
						if(con.element("host") != null){
							host = con.element("host").getText();
						}
						
						if(con.element("port") != null){
							port = con.element("port").getText();
						}
						
						if(con.element("dataBaseName") != null){
							databasename = con.element("dataBaseName").getText();
						}
						
						if(con.element("login") != null){
							user = con.element("login").getText();
						}
						
						if(con.element("password") != null){
							password = con.element("password").getText();
						}
						
						if(con.element("driverName") != null){
							driverName = con.element("driverName").getText();
						}
						
						if(con.element("useFullUrl") != null){
							useFullUrl = con.element("useFullUrl").getText();
						}
						
						if(con.element("fullUrl") != null){
							fullUrl = con.element("fullUrl").getText();
						}
					}
					
					ModelDatasourceJDBC ds = new ModelDatasourceJDBC();
					ds.setName(datasourceName);
					ds.setType(DatasourceType.DATASOURCE_JDBC);
					ds.setDriver(driverName);
					ds.setUser(user);
					ds.setPassword(password);
					ds.setPort(port);
					ds.setHost(host);
					ds.setDbName(databasename);
					ds.setUseFullUrl(Boolean.parseBoolean(useFullUrl));
					ds.setFullUrl(fullUrl);
					ds.setFmUser(fmUser);
					ds.setFmPassword(fmPassword);
					
					datasources.add(ds);
				}
			}
		}
		
		if (document.getRootElement().elements(NODE_OLAP_INPUT) != null){
			for(Object o : document.getRootElement().elements(NODE_OLAP_INPUT)){
				datasources.add(getDatasourceFromInput((Element)o, false));
			}
		}
		
		if (document.getRootElement().elements(NODE_FMDT_INPUT) != null){
			for(Object o : document.getRootElement().elements(NODE_FMDT_INPUT)){
				datasources.add(getDatasourceFromInput((Element)o, true));
			}
		}
		
		if (document.getRootElement().elements(NODE_OLAP_DIM_INPUT) != null){
			for(Object o : document.getRootElement().elements(NODE_OLAP_DIM_INPUT)){
				datasources.add(getDatasourceFromInput((Element)o, false));
			}
		}

		
		return datasources;
	}

	private IDatasource getDatasourceFromInput(Element e, boolean fmdt){
		String datasourceName = e.element("name").getText();
		String dirId = null;
		
		if(!fmdt){
			Element con = e.element("directoryItemId");
			if(con != null){
				dirId = con.getText();
			}
		}
		else {
			Element con = e.element("repositoryItemId");
			if(con != null){
				dirId = con.getText();
			}
		}
		
		ModelDatasourceRepository ds = new ModelDatasourceRepository();
		ds.setName(datasourceName);
		ds.setType(DatasourceType.DATASOURCE_REPOSITORY);
		ds.setDirId(dirId);
		
		return ds;
	}
	
	private IDatasource getDatasourceFromOdaInput(Object o){
		String datasourceName = ((Element)o).element("name").getText();
		String extId = ((Element)o).element(NODE_EXT_ID).getText();
		if (extId.equals(IDatasourceType.METADATA) 
				|| extId.equals(IDatasourceType.METADATA_OLAP)){

			Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
			if(e != null){
				
				String user = null;
				String password = null;
				String vanillaRuntimeUrl = null;
				String dirId = null;
				String groupName = null;
				String repositoryId = null;
				for(Object p : ((Element)e).elements("property")){
					if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_USER)){
						user = ((Element)p).element("value").getText();
					}
					else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_PASSWORD)){
						password = ((Element)p).element("value").getText();
					}
					else if(((Element)p).element("name").getText().equals(IDatasourceType.METADATA_VANILLA_URL)){
						vanillaRuntimeUrl = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_DIRECTORY_ITEM_ID)){
						dirId = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.GROUP_NAME)){
						groupName = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.METADATA_REPOSITORY_ID)){
						repositoryId = ((Element)p).element("value").getText();
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
				datasource.setRepositoryId(repositoryId);
				datasource.setGroupName(groupName);
				
				return datasource;
			}
		}
		else if (extId.equals(IDatasourceType.CSV_ODA) 
				|| extId.equals(IDatasourceType.EXCEL_ODA)){

			Element p = ((Element)o).element(NODE_PRIVATE_DATASOURCE);
			if(p != null){

				String user = null;
				String password = null;
				String repositoryId = null;
				String repositoryItemId = null;
				String groupId = null;
				for(Object e : ((Element)p).elements("property")){
					if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ITEM_ID)){
						repositoryItemId = ((Element)e).element("value").getText();
					}
					else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_REPOSITORY_ID)){
						repositoryId = ((Element)e).element("value").getText();
					}
					else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_GROUP_ID)){
						groupId = ((Element)e).element("value").getText();
					}
					else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_USER)){
						user = ((Element)e).element("value").getText();
					}
					else if (((Element)e).element("name").getText().equals(IDatasourceType.CSV_PASSWORD)){
						password = ((Element)e).element("value").getText();
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

				return datasource;
			}
		}
		else if (extId.equals(IDatasourceType.FWR_ODA)){
			
			Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
			if(e != null){

				String user = null;
				String password = null;
				String repositoryUrl = null;
				String url = null;
				String dirId = null;
				String groupName = null;
				String groupId = null;
				for(Object p : ((Element)e).elements("property")){
					if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_USER)){
						user = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_PASSWORD)){
						password = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.FWR_URL)){
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

				Element g = ((Element)o).element(NODE_PRIVATE_DATASOURCE);
				if(g != null){
					for(Object f : ((Element)g).elements("property")){
						if (((Element)f).element("name").getText().equals(IDatasourceType.FWR_REPORT_ID)){
							dirId = ((Element)f).element("value").getText();
						}
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

				return datasource;
			}
		}
		else if (extId.equals(IDatasourceType.JDBC)){

			Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
			if(e != null){
				
				String driver = null;
				String url = null;
				String user = null;
				String password = null;
				boolean isPasswordEncrypted = false;
				for(Object p : ((Element)e).elements("property")){
					if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_DRIVER)){
						driver = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_URL)){
						url = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_USER)){
						user = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.ODA_PASSWORD)){
						password = ((Element)p).element("value").getText();
					}
				}

				ModelDatasourceJDBC datasource = new ModelDatasourceJDBC();
				datasource.setName(datasourceName);
				datasource.setExtensionId(extId);
				datasource.setType(DatasourceType.DATASOURCE_JDBC);
				datasource.setUser(user);
				datasource.setPassword(password);
				datasource.setDriver(driver);
				datasource.setUseFullUrl(true);
				datasource.setFullUrl(url);
				datasource.setIsPasswordEncrypted(isPasswordEncrypted);

				return datasource;
			}
		}
		else if (extId.equals(IDatasourceType.FM)){

			Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
			if(e != null){
				
				String fmUser = null;
				String fmPassword = null;
				for(Object p : ((Element)e).elements("property")){
					if (((Element)p).element("name").getText().equals(IDatasourceType.FM_USER)){
						fmUser = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.FM_PASSWORD)){
						fmPassword = ((Element)p).element("value").getText();
					}
				}

				ModelDatasourceJDBC datasource = new ModelDatasourceJDBC();
				datasource.setName(datasourceName);
				datasource.setExtensionId(extId);
				datasource.setType(DatasourceType.DATASOURCE_JDBC);
				datasource.setFmUser(fmUser);
				datasource.setFmPassword(fmPassword);

				return datasource;
			}
		}
		else if (extId.equals(IDatasourceType.LIST_DATA)){

			Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
			if(e != null){
				
				String user = null;
				String password = null;
				String vanillaRuntimeUrl = null;
				String vanillaRepositoryId = null;
				for(Object p : ((Element)e).elements("property")){
					if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_USER)){
						user = ((Element)p).element("value").getText();
					}
					else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_PASSWORD)){
						password = ((Element)p).element("value").getText();
					}
					else if(((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_RUNTIME_URL)){
						vanillaRuntimeUrl = ((Element)p).element("value").getText();
					}
					else if (((Element)p).element("name").getText().equals(IDatasourceType.LIST_DATA_REPOSITORY_ID)){
						vanillaRepositoryId = ((Element)p).element("value").getText();
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

				return datasource;
			}
		}
		else if (extId.equals(IDatasourceType.FUSION_MAP)){
			
			Element e = ((Element)o).element(NODE_PUBLIC_DATASOURCE);
			if(e != null){
			
				String vanillaRuntimeUrl = null;
				for(Object p : ((Element)e).elements("property")){
					if(((Element)p).element("name").getText().equals(IDatasourceType.MAP_RUNTIME_URL)){
						vanillaRuntimeUrl = ((Element)p).element("value").getText();
					}
				}

				ModelDatasourceRepository datasource = new ModelDatasourceRepository();
				datasource.setName(datasourceName);
				datasource.setExtensionId(extId);
				datasource.setType(DatasourceType.DATASOURCE_REPOSITORY);
				datasource.setVanillaRuntimeUrl(vanillaRuntimeUrl);

				return datasource;
			}
		}
		
		return null;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		return null;
	}
}
