package bpm.es.datasource.analyzer.parsers;

import java.util.Collections;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class FMDTAnalyser implements IAnalyzer{
	
	
	private static final String NODE_DATASOURCE = "sqlDataSource";
	private static final String NODE_CONNECTION = "sqlConnection";
	private static final String NODE_HOST = "host";
	private static final String NODE_FULL_URL = "fullUrl";
	private static final String NODE_USE_FULL_URL = "useFullUrl";
	private static final String NODE_DATABASENAME = "dataBaseName";
	private static final String NODE_PORT = "portNumber";
	
	private static final String NODE_DATA_STREAM = "dataStream";
	private static final String NODE_DATA_STREAM_ORIGIN = "origin";
	private static final String NODE_CALCULATED_ELEMENT = "formulaElement";
	private static final String NODE_CALCULATED_FORMULA = "formula";
	
	
	
	public boolean match(String xml, IPattern pattern) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		
		
		for(Element dataSource : (List<Element>)root.elements(NODE_DATASOURCE)){
			for(Element connection : (List<Element>)dataSource.elements(NODE_CONNECTION)){
				String host = connection.element(NODE_HOST).getStringValue();
				String port = connection.element(NODE_PORT).getStringValue();
				String dbName = connection.element(NODE_DATABASENAME).getStringValue();
				
				String useFullUrl = connection.element(NODE_USE_FULL_URL) != null ? connection.element(NODE_USE_FULL_URL).getStringValue() : null;
				String fullUrl = connection.element(NODE_FULL_URL) != null ? connection.element(NODE_FULL_URL).getStringValue() : null;
				
				
				
				
				String oracleLike = ":@" + host + ":" + port + ":" + dbName;
				String std = "://" + host + ":" + port + "/" + dbName;

				
				String url = pattern.getUrl().trim();
				if (url.endsWith("/")){
					url = url.substring(0, url.length() -2);
				}
				
				if (pattern instanceof PatternTable){
					if (useFullUrl != null && fullUrl != null && Boolean.parseBoolean(useFullUrl)){
						if (!url.endsWith(fullUrl)){
							break;
						}
					}else if ( !(url.endsWith(oracleLike) || url.endsWith(std))){
						break;
					}
					for(Element dataStream : (List<Element>)dataSource.elements(NODE_DATA_STREAM)){
						String origin = dataStream.element(NODE_DATA_STREAM_ORIGIN).getStringValue();
						for(String table : ((PatternTable)pattern).getTableName()){
							if (origin.contains(table)){
								return true;
							}
						}
						for(Element calculatedColumn : (List<Element>)dataStream.elements(NODE_CALCULATED_ELEMENT)){
							String col = calculatedColumn.element("formula").getStringValue();
							for(String table : ((PatternTable)pattern).getTableName()){
								if (origin.contains(col.trim())){
									return true;
								}
							}
						}
						
					}
					
				}
				else{
					if (useFullUrl != null && fullUrl != null && Boolean.parseBoolean(useFullUrl)){
						if (url.endsWith(fullUrl)){
							return true;
						}
					}
					if (url.endsWith(oracleLike) || url.endsWith(std)){
						return true;
					}
				}

			}
		}
		
		return false;
	}
	
	public String getObjectTypeName() {
		return "FreeMetaData";
	}

	public int getObjectType() {
		return IRepositoryApi.FMDT_TYPE;
	}
	@Override
	public List<OdaInput> extractDataSets(String xml) throws Exception {
		
		return Collections.EMPTY_LIST;
	}
}
