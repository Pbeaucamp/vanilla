package bpm.es.datasource.analyzer.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.OdaInput;


public class BIWAnalyzer implements IAnalyzer{

	private static final String DB_SERVER_NODE_NAMES = "dataBaseServer";
	private static final String HOST = "url";
	private static final String PORT = "port";
	private static final String DBNAME = "dataBaseName";
	
	private static final String[] ACTIVITIES_NODES_USING_SQL = new String[]{
		"checkTableActivity", "checkColumnActivity",
		"sqlActivity" 
		
	};
	
	private static final String[] ACTIVITIES_SERVER_NODE_REF = new String[]{
		"fileserver", "fileserver",
		"serverRefName"
	};
	
	private static final String[] ACTIVITIES_TABLE_NODE_REF = new String[]{
		"path", "path",
		"query"
	};
	
	
	
	public int getObjectType() {
		return IRepositoryApi.BIW_TYPE;
	}

	public String getObjectTypeName() {
		return "BiWorkflow";
	}

	public boolean match(String xml, IPattern pattern) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		for(String serverName : findDatabaseServerName(pattern, root)){
			if (!findActivitiesUsingServer(pattern, serverName, root).isEmpty()){
				return true;
			}
		}
		
		return false;
	}
	
	private List<String> findDatabaseServerName(IPattern pattern, Element serversRoot){
		List<String> names = new ArrayList<String>();
		for(Element server : (List<Element>)serversRoot.elements(DB_SERVER_NODE_NAMES)){
			
			String host = server.element(HOST).getStringValue();
			String port = server.element(PORT).getStringValue();
			String dbName = server.element(DBNAME).getStringValue();
			
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
		return names;
	}

	private List<String> findActivitiesUsingServer(IPattern pattern, String serverName, Element stepsRoot){
		List<String> stepsName = new ArrayList<String>();
		
		for(int i = 0; i < ACTIVITIES_NODES_USING_SQL.length; i++){
			for(Element activityNode : (List<Element>) stepsRoot.elements(ACTIVITIES_NODES_USING_SQL[i])){
				Element e = activityNode.element(ACTIVITIES_SERVER_NODE_REF[i]);
				if (e != null){
					if (pattern instanceof PatternTable){
						
						for(String table : ((PatternTable)pattern).getTableName()){
							Element def = e.element(ACTIVITIES_TABLE_NODE_REF[i]);
							
							if (def.getStringValue().contains(table) ){
								stepsName.add(e.element("name").getStringValue());
								break;
							}
						}
					}
					else{
						if (e.getStringValue().equals(serverName)){
							stepsName.add(activityNode.element("name").getStringValue());
						}
						
					}
					
				}
			}
			
		}
		return stepsName;
	}
	
	@Override
	public List<OdaInput> extractDataSets(String xml) throws Exception {
		
		return Collections.EMPTY_LIST;
	}
}
