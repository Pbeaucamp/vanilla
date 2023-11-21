package bpm.vanilla.workplace.api.datasource.extractor;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.datasource.IDatasourceType.DatasourceType;
import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;

public class BIWDatasourceExtractor implements IDatasourceExtractor{
	
	private static final String NODE_SERVER = "dataBaseServer";
	
	private static final String NODE_GATEWAY_ACTIVITY = "gatewayActivity";
	private static final String NODE_GATEWAY_OBJECT = "gatewayObject";
	
	private static final String NODE_BIW_ACTIVITY = "BIWActivity";
	private static final String NODE_BIW_OBJECT = "BIWObject";
	
	private static final String NODE_START_STOP_ACTIVITY = "startstopactivity";
	private static final String NODE_START_STOP_OBJECT = "biRepositoryObject";
	
	private static final String NODE_BURST_ACTIVITY = "burstActivity";
	private static final String NODE_BURST_OBJECT = "birtObject";
	
	private static final String NODE_REPORT_ACTIVITY = "reportActivity";
	private static final String NODE_REPORT_FWR_OBJECT = "fwrObject";
	private static final String NODE_REPORT_BIRT_OBJECT = "birtObject";
	
	@Override
	public List<IDatasource> extractDatasources(String xml) throws Exception {
		List<IDatasource> datasources = new ArrayList<IDatasource>();

		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		
		if(root.elements(NODE_GATEWAY_ACTIVITY) != null){
			for(Object o : root.elements(NODE_GATEWAY_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				String dirId = null;
				
				Element el = ((Element)o).element(NODE_GATEWAY_OBJECT);	
				if (el != null && el.element("id") != null) {
					dirId = el.element("id").getText();
					
					ModelDatasourceRepository dsRepo = new ModelDatasourceRepository();
					dsRepo.setName(datasourceName);
					dsRepo.setType(DatasourceType.DATASOURCE_REPOSITORY);
					dsRepo.setDirId(dirId);
					
					datasources.add(dsRepo);
				}
			}
		}
		
		if(root.elements(NODE_BIW_ACTIVITY) != null){
			for(Object o : root.elements(NODE_BIW_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				String dirId = null;
				
				Element el = ((Element)o).element(NODE_BIW_OBJECT);	
				dirId = el.element("id").getText();
				
				ModelDatasourceRepository dsRepo = new ModelDatasourceRepository();
				dsRepo.setName(datasourceName);
				dsRepo.setType(DatasourceType.DATASOURCE_REPOSITORY);
				dsRepo.setDirId(dirId);
				
				datasources.add(dsRepo);
			}
		}
		
		if(root.elements(NODE_START_STOP_ACTIVITY) != null){
			for(Object o : root.elements(NODE_START_STOP_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				String dirId = null;
				
				Element el = ((Element)o).element(NODE_START_STOP_OBJECT);	
				dirId = el.element("id").getText();
				
				ModelDatasourceRepository dsRepo = new ModelDatasourceRepository();
				dsRepo.setName(datasourceName);
				dsRepo.setType(DatasourceType.DATASOURCE_REPOSITORY);
				dsRepo.setDirId(dirId);
				
				datasources.add(dsRepo);
			}
		}
		
		if(root.elements(NODE_BURST_ACTIVITY) != null){
			for(Object o : root.elements(NODE_BURST_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				String dirId = null;
				
				Element el = ((Element)o).element(NODE_BURST_OBJECT);	
				dirId = el.element("id").getText();
				
				ModelDatasourceRepository dsRepo = new ModelDatasourceRepository();
				dsRepo.setName(datasourceName);
				dsRepo.setType(DatasourceType.DATASOURCE_REPOSITORY);
				dsRepo.setDirId(dirId);
				
				datasources.add(dsRepo);
			}
		}
		
		if(root.elements(NODE_REPORT_ACTIVITY) != null){
			for(Object o : root.elements(NODE_REPORT_ACTIVITY)){
				
				String datasourceName = ((Element)o).element("name").getText();
				String dirId = null;
				
				Element el = ((Element)o).element(NODE_REPORT_FWR_OBJECT);
				if(el != null){
					dirId = el.element("id").getText();
				}
				
				Element el2 = ((Element)o).element(NODE_REPORT_BIRT_OBJECT);
				if(el2 != null){
					dirId = el2.element("id").getText();
				}
				
				ModelDatasourceRepository dsRepo = new ModelDatasourceRepository();
				dsRepo.setName(datasourceName);
				dsRepo.setType(DatasourceType.DATASOURCE_REPOSITORY);
				dsRepo.setDirId(dirId);
				
				datasources.add(dsRepo);
			}
		}
		
		if(root.elements(NODE_SERVER) != null){
			for(Object o : root.elements(NODE_SERVER)){
				
				String datasourceName = ((Element)o).element("name").getText();
				String host = null;
				String port = null;
				String databaseName = null;
				String login = null;
				String password = null;
				String jdbcDriver = null;
				
				if(((Element)o).element("url") != null){
					host = ((Element)o).element("url").getText();
				}
				
				if(((Element)o).element("port") != null){
					port = ((Element)o).element("port").getText();
				}
				
				if(((Element)o).element("dataBaseName") != null){
					databaseName = ((Element)o).element("dataBaseName").getText();
				}
				
				if(((Element)o).element("login") != null){
					login = ((Element)o).element("login").getText();
				}
				
				if(((Element)o).element("password") != null){
					password = ((Element)o).element("password").getText();
				}
				
				if(((Element)o).element("jdbcDriver") != null){
					jdbcDriver = ((Element)o).element("jdbcDriver").getText();
				}
				
				ModelDatasourceJDBC dsJdbc = new ModelDatasourceJDBC();
				dsJdbc.setName(datasourceName);
				dsJdbc.setType(DatasourceType.DATASOURCE_JDBC);
				dsJdbc.setHost(host);
				dsJdbc.setPort(port);
				dsJdbc.setDbName(databaseName);
				dsJdbc.setUser(login);
				dsJdbc.setPassword(password);
				dsJdbc.setDriver(jdbcDriver);
				
				datasources.add(dsJdbc);
			}
		}
		
		return datasources;
	}

	@Override
	public DisconnectedBackupConnection extractBackupConnection(int itemId, String xml) throws Exception {
		return null;
	}
}
