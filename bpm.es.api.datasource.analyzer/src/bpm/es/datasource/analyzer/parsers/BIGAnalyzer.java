package bpm.es.datasource.analyzer.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.OdaInput;


public class BIGAnalyzer implements IAnalyzer{

	private static final String SERVER_NODE = "servers";
	
	private static final String[] TRANSFO_NODE_USING_SQL = new String[]{
		"dataBaseInputStream", "dataBaseOutputStream",
		"insertOrUpdate", "slowChangingDimension1", "slowChangingDimension2",
		"infobrightInjector", "sqlLookup", "sqlScript",
		"kpiList", "kpiOutput"
		
	};
	
	private static final String TRANSFO_SERVER_NODE_NAME ="serverRef";
	
	
	private static final String[] SERVER_NODE_NAMES = new String[]{
		"dataBaseServer"
	};
	private static final String[] CONNECTION_NODE_NAMES = new String[]{
		"dataBaseConnection"
	};
	    
	
	private static final String TABLE_NODES = "definition";
	
	private static final String FULL_URL = "fullUrl";
	private static final String HOST_URL = "host";
	private static final String PORT = "port";
	private static final String DBNAME = "dataBaseName";
	
	public boolean match(String xml, IPattern pattern) throws Exception{
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		for(String serverName : findDatabaseServerName(pattern, root.element(SERVER_NODE))){
			if (!findStepsUsingServer(pattern, serverName, root).isEmpty()){
				return true;
			}
		}
		
		return false;
	}

	private List<String> findDatabaseServerName(IPattern pattern, Element serversRoot){
		List<String> names = new ArrayList<String>();
		
		for(int i = 0; i < SERVER_NODE_NAMES.length; i++){
			
			for(Element server : (List<Element>)serversRoot.elements(SERVER_NODE_NAMES[i])){
				
				for(Element conn : (List<Element>)server.elements(CONNECTION_NODE_NAMES[i])){
					Element e = conn.element(FULL_URL);
					/*
					 * fullUrl
					 */
					if (e != null){
						if (e.getStringValue().contains(pattern.getUrl()) || pattern.getUrl().contains(e.getStringValue())){
							names.add(server.element("name").getStringValue());
						}
					}
					else{
						String host = conn.element(HOST_URL).getStringValue();
						String port = conn.element(PORT).getStringValue();
						String dbName = conn.element(DBNAME).getStringValue();
						
						
						String oracleLike = ":@" + host + ":" + port + ":" + dbName;
						String std = "://" + host + ":" + port + "/" + dbName;
						
						String url = pattern.getUrl().trim();
						if (url.endsWith("/")){
							url = url.substring(0, url.length() -2);
						}
						
						if (url.endsWith(oracleLike) || url.endsWith(std)){
							names.add(server.element("name").getStringValue());
						}
						
					}
					
					
				}
			}
		}
		
		return names;
		
	}
	
	private List<String> findStepsUsingServer(IPattern pattern, String serverName, Element stepsRoot){
		
		List<String> stepsName = new ArrayList<String>();
		
		for(String stepNode : TRANSFO_NODE_USING_SQL){
			
			for (Element e : (List<Element>)stepsRoot.elements(stepNode)){
				Element s = e.element(TRANSFO_SERVER_NODE_NAME);
				if (s != null){
					if (pattern instanceof PatternTable){
						
						for(String table : ((PatternTable)pattern).getTableName()){
							Element def = e.element(TABLE_NODES);
							
							if (def.getStringValue().contains(table) || stepNode.startsWith("kpi")){
								stepsName.add(e.element("name").getStringValue());
								break;
							}
						}
					}
					else{
						if (s.getStringValue().equals(serverName)){
							stepsName.add(e.element("name").getStringValue());
						}
					}
					
				}
			}
		}
		
		return stepsName;
	}

	public String getObjectTypeName() {
		return "BiGateway";
	}
	
	public int getObjectType() {
		return IRepositoryApi.GTW_TYPE;
	}

	@Override
	public List<OdaInput> extractDataSets(String xml) throws Exception {
		
		return Collections.EMPTY_LIST;
	}
	
}
