package bpm.vanilla.workplace.api.datasource.replacement;

import java.io.ByteArrayOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.IDatasourceType;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;

public class FMDTDatasourceReplacement implements IDatasourceReplacement{

	private static final String NODE_DATASOURCE = "sqlDataSource";
	private static final String NODE_CONNECTION = "sqlConnection";
	
	private static final String NODE_DATASOURCE_OLAP = "unitedOlapDatasource";
	private static final String NODE_CONNECTION_OLAP = "unitedOlapConnection";
	private static final String NODE_IDENTIFIER = "identifier";
	private static final String NODE_CONTEXT = "runtimeContext";
	
	@Override
	public String replaceElement(String xml, IDatasource dsNew) throws Exception {
		
		Document document = DocumentHelper.parseText(xml);

		if(document.getRootElement().elements(NODE_DATASOURCE) != null){
			for(Object ds : document.getRootElement().elements(NODE_DATASOURCE)){
				
				if(dsNew.getName().equals(((Element)ds).element("name").getStringValue())){
					
					ModelDatasourceJDBC dsJdbc = (ModelDatasourceJDBC)dsNew;
					for(Object c : ((Element)ds).elements(NODE_CONNECTION)){
						if(((Element)c).element(IDatasourceType.DRIVER) != null){
							((Element)c).element(IDatasourceType.DRIVER).setText(dsJdbc.getDriver());
						}
						if(((Element)c).element(IDatasourceType.HOST) != null){
							((Element)c).element(IDatasourceType.HOST).setText(dsJdbc.getHost());
						}
						if(((Element)c).element(IDatasourceType.PORT) != null){
							((Element)c).element(IDatasourceType.PORT).setText(dsJdbc.getPort());
						}
						if(((Element)c).element(IDatasourceType.DB_NAME) != null){
							((Element)c).element(IDatasourceType.DB_NAME).setText(dsJdbc.getDbName());
						}
						if(((Element)c).element(IDatasourceType.USE_FULL_URL) != null){
							((Element)c).element(IDatasourceType.USE_FULL_URL).setText(String.valueOf(dsJdbc.isUseFullUrl()));
						}
						if(((Element)c).element(IDatasourceType.FULL_URL) != null){
							((Element)c).element(IDatasourceType.FULL_URL).setText(dsJdbc.getFullUrl());
						}
						if(((Element)c).element(IDatasourceType.USER) != null){
							((Element)c).element(IDatasourceType.USER).setText(dsJdbc.getUser());
						}
						if(((Element)c).element(IDatasourceType.PASSWORD) != null){
							((Element)c).element(IDatasourceType.PASSWORD).setText(dsJdbc.getPassword());
						}
					}
					break;
				}
			}
		}


		if(document.getRootElement().elements(NODE_DATASOURCE_OLAP) != null){
			for(Object ds : document.getRootElement().elements(NODE_DATASOURCE_OLAP)){
				
				if(dsNew.getName().equals(((Element)ds).element("name").getStringValue())){
					
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)dsNew;
					for(Object c : ((Element)ds).elements(NODE_CONNECTION_OLAP)){
						
						for(Object i : ((Element)c).elements(NODE_IDENTIFIER)){
							if(((Element)i).element(IDatasourceType.OLAP_FASD_ID) != null){
								((Element)i).element(IDatasourceType.OLAP_FASD_ID).setText(dsRepo.getDirId());
							}
							if(((Element)i).element(IDatasourceType.OLAP_REPOSITORY_ID) != null){
								((Element)i).element(IDatasourceType.OLAP_REPOSITORY_ID).setText(dsRepo.getRepositoryId());
							}
						}
						
						for(Object context : ((Element)c).elements(NODE_CONTEXT)){
							if(((Element)context).element(IDatasourceType.OLAP_USER) != null){
								((Element)context).element(IDatasourceType.OLAP_USER).setText(dsRepo.getUser());
							}
							if(((Element)context).element(IDatasourceType.OLAP_PASSWORD) != null){
								((Element)context).element(IDatasourceType.OLAP_PASSWORD).setText(dsRepo.getPassword());
							}
							if(((Element)context).element(IDatasourceType.OLAP_GROUP_NAME) != null){
								((Element)context).element(IDatasourceType.OLAP_GROUP_NAME).setText(dsRepo.getGroupName());
							}
							if(((Element)context).element(IDatasourceType.OLAP_GROUP_ID) != null){
								((Element)context).element(IDatasourceType.OLAP_GROUP_ID).setText(dsRepo.getGroupId());
							}
						}
					}
					break;
				}
			}
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		
		OutputFormat form = OutputFormat.createPrettyPrint();
		form.setTrimText(false);
		XMLWriter writer = new XMLWriter(bos, form);
		writer.write(document.getRootElement());
		writer.close();
		
		String result = bos.toString("UTF-8"); 
		
		return result;
		
//		return document.getRootElement().asXML();
	}
}
